package daa;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

abstract class IntListContractTest {
    abstract IntList create();

    @Test
    void emptyStructure() {
        IntList s = create();
        assertEquals(0, s.size());
        assertFalse(s.contains(1));
        assertThrows(IndexOutOfBoundsException.class, () -> s.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> s.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> s.add(1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> s.add(-1, 5));
    }

    @Test
    void oneElement() {
        IntList s = create();
        s.add(7);
        assertEquals(1, s.size());
        assertEquals(7, s.get(0));
        assertTrue(s.contains(7));
        assertFalse(s.contains(8));
        assertEquals(7, s.remove(0));
        assertEquals(0, s.size());
        s.add(0, 9);
        assertEquals(9, s.get(0));
    }

    @Test
    void invalidIndexes() {
        IntList s = create();
        for (int i = 0; i < 5; i++) {
            s.add(i);
        }
        assertThrows(IndexOutOfBoundsException.class, () -> s.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> s.get(5));
        assertThrows(IndexOutOfBoundsException.class, () -> s.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> s.remove(5));
        assertThrows(IndexOutOfBoundsException.class, () -> s.add(6, 1));
        assertEquals(5, s.size());
    }

    @Test
    void firstAndLastIndex() {
        IntList s = create();
        for (int i = 0; i < 10; i++) {
            s.add(i);
        }
        s.add(0, 100);
        s.add(s.size(), 200);
        assertEquals(100, s.get(0));
        assertEquals(200, s.get(s.size() - 1));
        assertEquals(200, s.remove(s.size() - 1));
        assertEquals(100, s.remove(0));
        assertEquals(10, s.size());
        for (int i = 0; i < 10; i++) {
            assertEquals(i, s.get(i));
        }
    }

    @Test
    void duplicateValues() {
        IntList s = create();
        for (int i = 0; i < 6; i++) {
            s.add(3);
        }
        assertTrue(s.contains(3));
        s.remove(2);
        assertEquals(5, s.size());
        assertTrue(s.contains(3));
        while (s.size() > 0) {
            s.remove(0);
        }
        assertFalse(s.contains(3));
    }

    @Test
    void growthPastInitialCapacity() {
        IntList s = create();
        for (int i = 0; i < 1000; i++) {
            s.add(i);
        }
        assertEquals(1000, s.size());
        for (int i = 0; i < 1000; i++) {
            assertEquals(i, s.get(i));
        }
    }

    @Test
    void randomOperationsMatchArrayList() {
        Random rnd = new Random(1);
        IntList s = create();
        List<Integer> ref = new ArrayList<>();
        for (int step = 0; step < 5000; step++) {
            int op = rnd.nextInt(4);
            if (op == 0 || ref.isEmpty()) {
                int x = rnd.nextInt(50);
                s.add(x);
                ref.add(x);
            } else if (op == 1) {
                int idx = rnd.nextInt(ref.size() + 1);
                int x = rnd.nextInt(50);
                s.add(idx, x);
                ref.add(idx, x);
            } else if (op == 2) {
                int idx = rnd.nextInt(ref.size());
                assertEquals(ref.remove(idx), s.remove(idx));
            } else {
                int x = rnd.nextInt(50);
                assertEquals(ref.contains(x), s.contains(x));
            }
            assertEquals(ref.size(), s.size());
        }
        for (int i = 0; i < ref.size(); i++) {
            assertEquals(ref.get(i), s.get(i));
        }
    }

    @Test
    void countersAreNotZero() {
        IntList s = create();
        for (int i = 0; i < 100; i++) {
            s.add(i);
        }
        s.metrics().reset();
        s.get(50);
        assertTrue(s.metrics().steps >= 1);
        s.metrics().reset();
        s.add(50, 1);
        assertTrue(s.metrics().moves > 0, "insert must count moves (shifts or link updates)");
        s.metrics().reset();
        s.remove(50);
        assertTrue(s.metrics().moves > 0, "remove must count moves (shifts or link updates)");
        s.metrics().reset();
        s.contains(-1);
        assertEquals(100, s.metrics().comparisons);
    }
}