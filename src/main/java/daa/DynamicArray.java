package daa;

public class DynamicArray implements IntList {
    private static final int INITIAL_CAPACITY = 16;

    private int[] a;
    private int size;
    private final Metrics metrics = new Metrics();

    public DynamicArray() {
        a = new int[INITIAL_CAPACITY];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Metrics metrics() {
        return metrics;
    }

    public int capacity() {
        return a.length;
    }

    @Override
    public void add(int x) {
        if (size == a.length) {
            grow();
        }
        a[size++] = x;
    }

    @Override
    public void add(int index, int x) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index " + index + ", size " + size);
        }
        if (size == a.length) {
            grow();
        }
        for (int i = size; i > index; i--) {
            a[i] = a[i - 1];
            metrics.moves++;
        }
        a[index] = x;
        size++;
    }

    @Override
    public int remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index " + index + ", size " + size);
        }
        int removed = a[index];
        metrics.steps++;
        for (int i = index; i < size - 1; i++) {
            a[i] = a[i + 1];
            metrics.moves++;
        }
        size--;
        return removed;
    }

    @Override
    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index " + index + ", size " + size);
        }
        metrics.steps++;
        return a[index];
    }

    @Override
    public boolean contains(int x) {
        for (int i = 0; i < size; i++) {
            metrics.steps++;
            metrics.comparisons++;
            if (a[i] == x) {
                return true;
            }
        }
        return false;
    }

    private void grow() {
        int[] bigger = new int[a.length * 2];
        for (int i = 0; i < size; i++) {
            bigger[i] = a[i];
            metrics.moves++;
        }
        a = bigger;
    }
}