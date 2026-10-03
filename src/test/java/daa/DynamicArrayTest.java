package daa;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class DynamicArrayTest extends IntListContractTest {
    @Override
    IntList create() {
        return new DynamicArray();
    }

    @Test
    void capacityDoublesWhenFull() {
        DynamicArray a = new DynamicArray();
        int c = a.capacity();
        for (int i = 0; i < c; i++) {
            a.add(i);
        }
        assertEquals(c, a.capacity());
        a.add(1);
        assertEquals(2 * c, a.capacity());
    }

    @Test
    void getIsOneStep() {
        DynamicArray a = new DynamicArray();
        for (int i = 0; i < 1000; i++) {
            a.add(i);
        }
        a.metrics().reset();
        a.get(999);
        assertEquals(1, a.metrics().steps);
    }

    @Test
    void headInsertShiftsEverything() {
        DynamicArray a = new DynamicArray();
        for (int i = 0; i < 10; i++) {
            a.add(i);
        }
        a.metrics().reset();
        a.add(0, -1);
        assertEquals(10, a.metrics().moves);
    }
}