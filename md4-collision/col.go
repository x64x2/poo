// Package md4_collision implements an collision attack on the MD4 hash function
// as described in "New Message Difference for MD4" by Yu Sasaki, Lei Wang,
// Kazuo Ohta and Noboru Kunihiro. Available at
// https://iacr.org/archive/fse2007/45930331/45930331.pdf
package md4_collision

import (
	"bytes"
	"crypto/rand"
	"encoding/binary"
	"math/bits"
)

type constraint interface {
	Massage(m1, m2 uint32) uint32
}

func zero(indexes ...int) zeroConstraint {
	mask := uint32(0)
	for _, idx := range indexes {
		mask |= 1 << idx
	}
	return zeroConstraint{inv: mask ^ 0xFFFFFFFF}
}

type zeroConstraint struct {
	inv uint32
}

func (c zeroConstraint) Massage(m1, _ uint32) uint32 {
	return m1 & c.inv
}

func one(indexes ...int) oneConstraint {
	mask := uint32(0)
	for _, idx := range indexes {
		mask |= 1 << idx
	}
	return oneConstraint{mask: mask}
}

type oneConstraint struct {
	mask uint32
}

func (c oneConstraint) Massage(m1, _ uint32) uint32 {
	return m1 | c.mask
}

func eq(indexes ...int) eqConstraint {
	mask := uint32(0)
	for _, idx := range indexes {
		mask |= 1 << idx
	}
	return eqConstraint{mask: mask, inv: mask ^ 0xFFFFFFFF}
}

type eqConstraint struct {
	mask uint32
	inv  uint32
}

func (c eqConstraint) Massage(m1, m2 uint32) uint32 {
	return (m1 & c.inv) | (m2 & c.mask)
}

// round1Constraints are the constraints described by Sasaki et al, in table 8.
var round1Constraints = [16][]constraint{
	{zero(1), one(31, 0), eq(9, 7)},
	{zero(19, 7, 1), one(31, 9, 0), eq(11)},
	{zero(24, 9, 7, 0), one(31, 19, 11, 1), eq(21, 20)},
	{zero(20, 19, 11), one(31, 24, 21, 9, 7), eq(23, 22, 8, 6, 5, 4, 3)},
	{zero(24, 21, 20, 19, 11, 9), one(23, 22, 8, 7, 6, 5, 4, 3), eq(31, 18)},
	{zero(31, 19, 18, 9, 8, 7, 6, 5, 4, 3), one(24, 23, 22, 21, 20), eq(2, 1)},
	{zero(31, 23, 22, 20, 18, 9), one(25, 24, 19, 8, 7, 6, 5, 4, 3, 2, 1)},
	{zero(24, 23, 22, 19, 12, 9, 8, 7, 6, 5, 4, 2, 1), one(31, 20, 18, 3), eq(25)},
	{zero(31, 25, 23, 5), one(24, 22, 9, 8, 7, 6, 4, 3, 2, 1), eq(30, 29, 27, 26, 12)},
	{zero(31, 26, 25), one(30, 29, 27, 23, 22, 12)},
	{zero(31, 30, 29, 25, 12), one(27, 26, 23, 22)},
	{zero(31, 27, 26), one(30, 29, 25, 12), eq(28)},
	{zero(28, 25, 12), one(29)},
	{zero(29, 28, 25, 12)},
	{one(29, 28, 25), eq(31)},
	{one(31), eq(28)},
}

func applyDiff(m [16]uint32) [16]uint32 {
	mPrime := m

	// Δm0 = 2²⁸, Δm2 = 2³¹, Δm4 = 2³¹, Δm8 = 2³¹, Δm12 = 2³¹
	mPrime[0] += 1 << 28
	mPrime[2] += 1 << 31
	mPrime[4] += 1 << 31
	mPrime[8] += 1 << 31
	mPrime[12] += 1 << 31

	return mPrime
}

func produceMaybeCollision() ([]byte, []byte, bool) {
	message := make([]byte, 64)
	rand.Read(message)

	x := [16]uint32{}
	for i := 0; i < 16; i++ {
		x[i] = binary.LittleEndian.Uint32(message[i<<2:])
	}

	var a, b, c, d uint32 = 0x67452301, 0xEFCDAB89, 0x98BADCFE, 0x10325476
	f := func(a, b, c, d uint32, k, s int) uint32 {
		// Do normal first round.
		F := (b & c) | ((^b) & d)
		a_new := bits.RotateLeft32(a+F+x[k], s)

		// message the chaining variable.
		for _, constraint := range round1Constraints[k] {
			a_new = constraint.Massage(a_new, b)
		}

		// Reverse computation with the massaged chaining variable.
		x[k] = bits.RotateLeft32(a_new, -s) - a - F
		return a_new
	}

	a = f(a, b, c, d, 0x0, 3)
	d = f(d, a, b, c, 0x1, 7)
	c = f(c, d, a, b, 0x2, 11)
	b = f(b, c, d, a, 0x3, 19)
	a = f(a, b, c, d, 0x4, 3)
	d = f(d, a, b, c, 0x5, 7)
	c = f(c, d, a, b, 0x6, 11)
	b = f(b, c, d, a, 0x7, 19)
	a = f(a, b, c, d, 0x8, 3)
	d = f(d, a, b, c, 0x9, 7)
	c = f(c, d, a, b, 0xA, 11)
	b = f(b, c, d, a, 0xB, 19)
	a = f(a, b, c, d, 0xC, 3)
	d = f(d, a, b, c, 0xD, 7)
	c = f(c, d, a, b, 0xE, 11)
	f(b, c, d, a, 0xF, 19)
	mPrime := applyDiff(x)

	m1 := []byte{}
	m2 := []byte{}
	for i := 0; i < 16; i++ {
		m1 = binary.LittleEndian.AppendUint32(m1, x[i])
		m2 = binary.LittleEndian.AppendUint32(m2, mPrime[i])
	}

	// Check if the two messages hash to the same value.
	h1 := md4.Sum(m1)
	h2 := md4.Sum(m2)
	return m1, m2, bytes.Equal(h1[:], h2[:])
}

func ProduceCollision() ([]byte, []byte) {
	var m1, m2 []byte
	var ok bool
	for {
		m1, m2, ok = produceMaybeCollision()
		if ok {
			break
		}
	}

	return m1, m2
}
