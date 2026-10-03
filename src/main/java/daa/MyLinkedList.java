package daa;

public class MyLinkedList implements IntList {
    private static final class Node {
        final int value;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }

    private Node head;
    private Node tail;
    private int size;
    private final Metrics metrics = new Metrics();

    @Override
    public int size() {
        return size;
    }

    @Override
    public Metrics metrics() {
        return metrics;
    }

    @Override
    public void add(int x) {
        Node n = new Node(x);

        if (tail == null) {
            head = n;
            tail = n;
        } else {
            tail.next = n;
            tail = n;
        }

        metrics.moves += 2;
        size++;
    }

    @Override
    public void add(int index, int x) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(
                    "index " + index + ", size " + size);
        }

        if (index == size) {
            add(x);
            return;
        }

        Node n = new Node(x);

        if (index == 0) {
            n.next = head;
            head = n;
        } else {
            Node prev = nodeAt(index - 1);

            n.next = prev.next;
            metrics.steps++;

            prev.next = n;
        }

        metrics.moves += 2;
        size++;
    }

    @Override
    public int remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "index " + index + ", size " + size);
        }

        int removed;

        if (index == 0) {
            removed = head.value;

            head = head.next;
            metrics.steps++;
            metrics.moves++;

            if (head == null) {
                tail = null;
                metrics.moves++;
            }
        } else {
            Node prev = nodeAt(index - 1);

            Node target = prev.next;
            metrics.steps++;

            removed = target.value;

            prev.next = target.next;
            metrics.steps++;
            metrics.moves++;

            if (target == tail) {
                tail = prev;
                metrics.moves++;
            }
        }

        size--;
        return removed;
    }

    @Override
    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "index " + index + ", size " + size);
        }

        return nodeAt(index).value;
    }

    @Override
    public boolean contains(int x) {
        Node cur = head;

        while (cur != null) {
            metrics.comparisons++;

            if (cur.value == x) {
                return true;
            }

            cur = cur.next;
            metrics.steps++;
        }

        return false;
    }

    private Node nodeAt(int index) {
        Node cur = head;

        for (int k = 0; k < index; k++) {
            cur = cur.next;
            metrics.steps++;
        }

        return cur;
    }
}