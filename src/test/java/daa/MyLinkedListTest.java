package daa;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class MyLinkedListTest extends IntListContractTest {
    @Override
    IntList create() {
        return new MyLinkedList();
    }

    @Test
    void getCostsIndexPlusOneSteps() {
        MyLinkedList l = new MyLinkedList();
        for (int i = 0; i < 1000; i++) {
            l.add(i);
        }
        l.metrics().reset();
        l.get(999);
        assertEquals(1000, l.metrics().steps);
    }

    @Test
    void headInsertIsConstantTime() {
        MyLinkedList l = new MyLinkedList();
        for (int i = 0; i < 1000; i++) {
            l.add(i);
        }
        l.metrics().reset();
        l.add(0, -1);
        assertEquals(0, l.metrics().steps);
        assertEquals(2, l.metrics().moves);
    }

    @Test
    void tailStaysCorrectAfterRemovingLast() {
        MyLinkedList l = new MyLinkedList();
        l.add(1);
        l.add(2);
        l.add(3);
        l.remove(2);
        l.add(4); // must be appended after 2
        assertEquals(3, l.size());
        assertEquals(2, l.get(1));
        assertEquals(4, l.get(2));
    }
}