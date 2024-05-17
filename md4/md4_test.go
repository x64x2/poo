package md4

import (
	"bytes"
	"encoding/hex"
	"hash"
	"strconv"
	"testing"
)

func TestKAT(t *testing.T) {
	testCases := []struct {
		input    string
		expected string
	}{
		// RFC1320
		{
			input:    "",
			expected: "31d6cfe0d16ae931b73c59d7e0c089c0",
		},
		{
			input:    "a",
			expected: "bde52cb31de33e46245e05fbdbd6fb24",
		},
		{
			input:    "abc",
			expected: "a448017aaf21d8525fc10ae87aa6729d",
		},
		{
			input:    "message digest",
			expected: "d9130a8164549fe818874806e1c7014b",
		},
		{
			input:    "abcdefghijklmnopqrstuvwxyz",
			expected: "d79e1c308aa5bbcdeea8ed63df412da9",
		},
		{
			input:    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789",
			expected: "043f8582f241db351ce627e153e7f0e4",
		},
		{
			input:    "12345678901234567890123456789012345678901234567890123456789012345678901234567890",
			expected: "e33b4ddc9c38f2199c3e7b164fcc0536",
		},
		// Wikipedia
		{
			input:    "The quick brown fox jumps over the lazy dog",
			expected: "1bee69a46ba811185c194762abaeae90",
		},
		{
			input:    "The quick brown fox jumps over the lazy cog",
			expected: "b86e130ce7028da59e672d56ad0113df",
		},
	}
	for _, testCase := range testCases {
		actual := Sum([]byte(testCase.input))
		b, _ := hex.DecodeString(testCase.expected)
		if !bytes.Equal(actual[:], b) {
			t.Errorf("Testcase failed.\nWant: %x\nGot:  %x", b, actual)
		}
	}
}

var buf = make([]byte, 8192)

func benchmarWriteSum(b *testing.B, size int, bench hash.Hash) {
	sum := make([]byte, Size)

	b.ReportAllocs()
	b.SetBytes(int64(size))
	for i := 0; i < b.N; i++ {
		bench.Reset()
		bench.Write(buf[:size])
		bench.Sum(sum[:0])
	}
}

func BenchmarkMD4(b *testing.B) {
	for _, size := range []int{8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096} {
		b.Run("WriteSum-"+strconv.Itoa(size), func(b *testing.B) {
			benchmarWriteSum(b, size, New())
		})
	}
}
