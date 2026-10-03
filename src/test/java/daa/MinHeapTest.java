package daa;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.Test;

class MinHeapTest {
    @Test
    void emptyHeapThrows() {
        MinHeap h = new MinHeap();
        assertTrue(h.isEmpty());
        assertThrows(IllegalStateException.class, h::peekMin);
        assertThrows(IllegalStateException.class, h::extractMin);
    }

    @Test
    void oneElement() {
        MinHeap h = new MinHeap();
        h.insert(5);
        assertEquals(5, h.peekMin());
        assertEquals(5, h.extractMin());
        assertTrue(h.isEmpty());
        assertThrows(IllegalStateException.class, h::extractMin);
    }

    @Test
    void duplicates() {
        MinHeap h = new MinHeap();
        for (int i = 0; i < 20; i++) {
            h.insert(4);
        }
        for (int i = 0; i < 20; i++) {
            assertEquals(4, h.extractMin());
        }
    }

    @Test
    void negativeAndExtremeValues() {
        MinHeap h = new MinHeap();
        int[] v = {0, -1, Integer.MAX_VALUE, Integer.MIN_VALUE, 7};
        for (int x : v) {
            h.insert(x);
        }
        Arrays.sort(v);
        for (int x : v) {
            assertEquals(x, h.extractMin());
        }
    }

    @Test
    void heapPropertyAfterEveryInsertAndExtract() {
        Random rnd = new Random(7);
        MinHeap h = new MinHeap();
        for (int i = 0; i < 2000; i++) {
            h.insert(rnd.nextInt(500));
            assertTrue(h.isHeap(), "after insert " + i);
        }
        while (!h.isEmpty()) {
            h.extractMin();
            assertTrue(h.isHeap(), "after extract, size " + h.size());
        }
    }

    @Test
    void peekMinIsOneStep() {
        MinHeap h = new MinHeap();
        for (int i = 0; i < 100; i++) {
            h.insert(100 - i);
        }
        h.metrics().reset();
        assertEquals(1, h.peekMin());
        assertEquals(1, h.metrics().steps);
        assertEquals(0, h.metrics().comparisons);
    }

    @Test
    void sortedOutputMatchesArraysSort() {
        for (int n : new int[] {1, 2, 3, 10, 100, 1000}) {
            Random rnd = new Random(42);
            int[] data = new int[n];
            MinHeap h = new MinHeap();
            for (int i = 0; i < n; i++) {
                data[i] = rnd.nextInt();
                h.insert(data[i]);
            }
            int[] expected = data.clone();
            Arrays.sort(expected);
            for (int i = 0; i < n; i++) {
                assertEquals(expected[i], h.extractMin());
            }
        }
    }

    @Test
    void buildHeapBonus() {
        Random rnd = new Random(3);
        int[] data = new int[1000];
        for (int i = 0; i < data.length; i++) {
            data[i] = rnd.nextInt(1000);
        }
        int[] original = data.clone();
        MinHeap h = MinHeap.buildHeap(data);
        assertArrayEquals(original, data, "input must not be modified");
        assertTrue(h.isHeap());
        Arrays.sort(original);
        for (int x : original) {
            assertEquals(x, h.extractMin());
        }
    }
}