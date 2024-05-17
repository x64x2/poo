package main

import (
	"fmt"
	"os"
	"runtime"
	"sync/atomic"
	"time"

)

func benchmark() {
	foundN := atomic.Uint64{}

	// Parallelize the collision finder.
	for i := 0; i < runtime.NumCPU(); i++ {
		go func() {
			for {
				md4_collision.ProduceCollision()
				foundN.Add(1)
			}
		}()
	}

	ticker := time.NewTicker(time.Second)
	var lastN uint64
	for {
		<-ticker.C
		currentN := foundN.Load()
		fmt.Printf("Produced %d MD4 collisions in the last second.\n", currentN-lastN)
		lastN = currentN
	}
}

func produce() {
	m1, m2 := md4_collision.ProduceCollision()
	fmt.Printf("m1: %x\nm2: %x\nh:  %x\n", m1, m2, md4.Sum(m1))

	// Print difference.
	for i := 0; i < len(m1); i++ {
		if m1[i] != m2[i] {
			fmt.Printf("idx %02d: %x %x\n", i, m1[i], m2[i])
		}
	}
}

func main() {
	if len(os.Args) != 2 || (os.Args[1] != "produce" && os.Args[1] != "benchmark") {
		fmt.Printf("Invalid usage: %s [produce] [benchmark]\n", os.Args[0])
		os.Exit(1)
	}
	if os.Args[1] == "produce" {
		produce()
	} else {
		benchmark()
	}
}
