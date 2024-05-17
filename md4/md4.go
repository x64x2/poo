// Package md4 implements the MD4 message-digest algorithm as specified in RFC1320.
package md4

import (
	"encoding/binary"
	"hash"
	"math/bits"
)

const (
	// The size of an MD4 checksum, in bytes.
	Size = 16

	// The blocksize of MD4, in bytes.
	BlockSize = 64
)

type md4State struct {
	// 128-bit internal state.
	s [4]uint32
	// Buffer for yet to be processed bytes by the MD4 block function.
	buf [BlockSize]byte
	// Number of bytes in the buffer.
	bufN int
	// The amount of processed bytes.
	len uint64
}

// New returns a new [hash.Hash] computing the MD4 checksum.
func New() hash.Hash {
	md4 := &md4State{}
	md4.Reset()

	return md4
}

// Sum returns the Whirlpool checksum of the data.
func Sum(data []byte) [Size]byte {
	var sum [Size]byte
	h := md4State{}
	h.s = iv
	h.Write(data)
	h.finalize()

	binary.LittleEndian.PutUint32(sum[0:4], h.s[0])
	binary.LittleEndian.PutUint32(sum[4:8], h.s[1])
	binary.LittleEndian.PutUint32(sum[8:12], h.s[2])
	binary.LittleEndian.PutUint32(sum[12:16], h.s[3])

	return sum
}

func (m *md4State) Size() int      { return Size }
func (m *md4State) BlockSize() int { return BlockSize }

var iv = [4]uint32{0x67452301, 0xefcdab89, 0x98badcfe, 0x10325476}

func (m *md4State) Reset() {
	m.s = iv
	m.buf = [BlockSize]byte{}
	m.bufN = 0
	m.len = 0
}

func (m *md4State) Write(p []byte) (n int, err error) {
	n = len(p)
	m.len += uint64(n)

	// First try to empty the buffer.
	if m.bufN > 0 {
		rem := copy(m.buf[m.bufN:], p)
		m.bufN += rem
		if m.bufN == BlockSize {
			block(m, m.buf[:])
			m.bufN = 0
		}
		p = p[rem:]
	}

	// Simply process the message if it's larger than the blocksize.
	for len(p) >= BlockSize {
		block(m, p[:BlockSize])
		p = p[BlockSize:]
	}

	// Flush the rest to the buffer.
	if len(p) > 0 {
		m.bufN = copy(m.buf[:], p)
	}

	return
}

func (m *md4State) Sum(in []byte) []byte {
	// Make a copy of m so that caller can keep writing and summing.
	m0 := *m
	m0.finalize()

	in = binary.LittleEndian.AppendUint32(in, m0.s[0])
	in = binary.LittleEndian.AppendUint32(in, m0.s[1])
	in = binary.LittleEndian.AppendUint32(in, m0.s[2])
	in = binary.LittleEndian.AppendUint32(in, m0.s[3])
	return in
}

func (m *md4State) finalize() {
	// Padding, Add 1 bit and 0 bit until 56 bytes (mod 64).
	padding := make([]byte, 64)
	padding[0] = 0x80

	// Pad to an full block if there are more or equal to 56 bytes in the buffer.
	if m.bufN >= 56 {
		rem := copy(m.buf[m.bufN:], padding)
		block(m, m.buf[:])

		m.bufN = 0
		padding = padding[rem:]
	}

	// Pad to 56 bytes in the buffer.
	copy(m.buf[m.bufN:56], padding)

	// Write the length in bits.
	binary.LittleEndian.PutUint64(m.buf[56:], m.len<<3)

	block(m, m.buf[:])
}

var padding = [17][]byte{
	{},
	{0x01},
	{0x02, 0x02},
	{0x03, 0x03, 0x03},
	{0x04, 0x04, 0x04, 0x04},
	{0x05, 0x05, 0x05, 0x05, 0x05},
	{0x06, 0x06, 0x06, 0x06, 0x06, 0x06},
	{0x07, 0x07, 0x07, 0x07, 0x07, 0x07, 0x07},
	{0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08},
	{0x09, 0x09, 0x09, 0x09, 0x09, 0x09, 0x09, 0x09, 0x09},
	{0x0a, 0x0a, 0x0a, 0x0a, 0x0a, 0x0a, 0x0a, 0x0a, 0x0a, 0x0a},
	{0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b},
	{0x0c, 0x0c, 0x0c, 0x0c, 0x0c, 0x0c, 0x0c, 0x0c, 0x0c, 0x0c, 0x0c, 0x0c},
	{0x0d, 0x0d, 0x0d, 0x0d, 0x0d, 0x0d, 0x0d, 0x0d, 0x0d, 0x0d, 0x0d, 0x0d, 0x0d},
	{0x0e, 0x0e, 0x0e, 0x0e, 0x0e, 0x0e, 0x0e, 0x0e, 0x0e, 0x0e, 0x0e, 0x0e, 0x0e, 0x0e},
	{0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f},
	{0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10},
}

func (m *md2State) finalize() {
	m.Write(padding[16-m.bufN])
	m.block(m.c[:])
}

func block(m *md4State, p []byte) {
	a := m.s[0]
	b := m.s[1]
	c := m.s[2]
	d := m.s[3]
	aa, bb, cc, dd := a, b, c, d

	x := [16]uint32{}
	for i := 0; i < 16; i++ {
		x[i] = binary.LittleEndian.Uint32(p[i*4:])
	}

	// Round 1.
	// Let [abcd k s] denote the operation:
	// a = (a + F(b,c,d) + X[k]) ⋘ s
	// F(X,Y,Z) = (X ∧ Y) ∨ (¬X ∧ Z)
	round1 := func(A, B, C, D, x uint32, s int) uint32 {
		return bits.RotateLeft32(A+((B&C)|((^B)&D))+x, s)
	}

	// Round 2.
	// Let [abcd k s] denote the operation:
	// a = (a + G(b,c,d) + X[k] + 5A827999) ⋘ s
	// G(X,Y,Z) = (X ∧ Y) ∨ (X ∧ Z) ∨ (Y ∧ Z)
	round2 := func(A, B, C, D, x uint32, s int) uint32 {
		return bits.RotateLeft32(A+((B&C)|(B&D)|(C&D))+x+0x5A827999, s)
	}

	// Round 3.
	// Let [abcd k s] denote the operation:
	// a = (a + H(b,c,d) + X[k] + 6ED9EBA1) ⋘ s
	// H(X,Y,Z) = X ⊕ Y ⊕ Z
	round3 := func(A, B, C, D, x uint32, s int) uint32 {
		return bits.RotateLeft32(A+(B^C^D)+x+0x6ED9EBA1, s)
	}

	// [ABCD  0  3]  [DABC  1  7]  [CDAB  2 11]  [BCDA  3 19]
	// [ABCD  4  3]  [DABC  5  7]  [CDAB  6 11]  [BCDA  7 19]
	// [ABCD  8  3]  [DABC  9  7]  [CDAB 10 11]  [BCDA 11 19]
	// [ABCD 12  3]  [DABC 13  7]  [CDAB 14 11]  [BCDA 15 19]
	a = round1(a, b, c, d, x[0], 3)
	d = round1(d, a, b, c, x[1], 7)
	c = round1(c, d, a, b, x[2], 11)
	b = round1(b, c, d, a, x[3], 19)
	a = round1(a, b, c, d, x[4], 3)
	d = round1(d, a, b, c, x[5], 7)
	c = round1(c, d, a, b, x[6], 11)
	b = round1(b, c, d, a, x[7], 19)
	a = round1(a, b, c, d, x[8], 3)
	d = round1(d, a, b, c, x[9], 7)
	c = round1(c, d, a, b, x[10], 11)
	b = round1(b, c, d, a, x[11], 19)
	a = round1(a, b, c, d, x[12], 3)
	d = round1(d, a, b, c, x[13], 7)
	c = round1(c, d, a, b, x[14], 11)
	b = round1(b, c, d, a, x[15], 19)

	// [ABCD  0  3]  [DABC  4  5]  [CDAB  8  9]  [BCDA 12 13]
	// [ABCD  1  3]  [DABC  5  5]  [CDAB  9  9]  [BCDA 13 13]
	// [ABCD  2  3]  [DABC  6  5]  [CDAB 10  9]  [BCDA 14 13]
	// [ABCD  3  3]  [DABC  7  5]  [CDAB 11  9]  [BCDA 15 13]
	a = round2(a, b, c, d, x[0], 3)
	d = round2(d, a, b, c, x[4], 5)
	c = round2(c, d, a, b, x[8], 9)
	b = round2(b, c, d, a, x[12], 13)
	a = round2(a, b, c, d, x[1], 3)
	d = round2(d, a, b, c, x[5], 5)
	c = round2(c, d, a, b, x[9], 9)
	b = round2(b, c, d, a, x[13], 13)
	a = round2(a, b, c, d, x[2], 3)
	d = round2(d, a, b, c, x[6], 5)
	c = round2(c, d, a, b, x[10], 9)
	b = round2(b, c, d, a, x[14], 13)
	a = round2(a, b, c, d, x[3], 3)
	d = round2(d, a, b, c, x[7], 5)
	c = round2(c, d, a, b, x[11], 9)
	b = round2(b, c, d, a, x[15], 13)

	// [ABCD  0  3]  [DABC  8  9]  [CDAB  4 11]  [BCDA 12 15]
	// [ABCD  2  3]  [DABC 10  9]  [CDAB  6 11]  [BCDA 14 15]
	// [ABCD  1  3]  [DABC  9  9]  [CDAB  5 11]  [BCDA 13 15]
	// [ABCD  3  3]  [DABC 11  9]  [CDAB  7 11]  [BCDA 15 15]
	a = round3(a, b, c, d, x[0], 3)
	d = round3(d, a, b, c, x[8], 9)
	c = round3(c, d, a, b, x[4], 11)
	b = round3(b, c, d, a, x[12], 15)
	a = round3(a, b, c, d, x[2], 3)
	d = round3(d, a, b, c, x[10], 9)
	c = round3(c, d, a, b, x[6], 11)
	b = round3(b, c, d, a, x[14], 15)
	a = round3(a, b, c, d, x[1], 3)
	d = round3(d, a, b, c, x[9], 9)
	c = round3(c, d, a, b, x[5], 11)
	b = round3(b, c, d, a, x[13], 15)
	a = round3(a, b, c, d, x[3], 3)
	d = round3(d, a, b, c, x[11], 9)
	c = round3(c, d, a, b, x[7], 11)
	b = round3(b, c, d, a, x[15], 15)

	m.s[0] = a + aa
	m.s[1] = b + bb
	m.s[2] = c + cc
	m.s[3] = d + dd
}
