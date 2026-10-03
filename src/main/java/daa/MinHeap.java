package daa;

public class MinHeap {
    private static final int INITIAL_CAPACITY = 16;

    private int[] a;
    private int size;
    private final Metrics metrics = new Metrics();

    public MinHeap() {
        a = new int[INITIAL_CAPACITY];
    }

    public static MinHeap buildHeap(int[] array) {
        MinHeap h = new MinHeap();
        h.a = new int[Math.max(INITIAL_CAPACITY, array.length)];

        for (int i = 0; i < array.length; i++) {
            h.a[i] = array[i];
            h.metrics.steps++;
            h.metrics.moves++;
        }

        h.size = array.length;

        for (int i = h.size / 2 - 1; i >= 0; i--) {
            int x = h.a[i];
            h.metrics.steps++;
            h.siftDown(i, x, i);
        }

        return h;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Metrics metrics() {
        return metrics;
    }

    public void insert(int x) {
        if (size == a.length) {
            grow();
        }

        int i = size;
        size++;

        while (i > 0) {
            int p = (i - 1) / 2;

            int parentValue = a[p];
            metrics.steps++;
            metrics.comparisons++;

            if (parentValue <= x) {
                break;
            }

            a[i] = parentValue;
            metrics.moves++;
            i = p;
        }

        a[i] = x;
    }

    public int peekMin() {
        if (size == 0) {
            throw new IllegalStateException("heap is empty");
        }

        metrics.steps++;
        return a[0];
    }

    public int extractMin() {
        if (size == 0) {
            throw new IllegalStateException("heap is empty");
        }

        int min = a[0];
        metrics.steps++;
        size--;

        if (size > 0) {
            int last = a[size];
            metrics.steps++;
            siftDown(0, last, size);
        }

        return min;
    }

    private void siftDown(int i, int x, int originalIndex) {
        while (true) {
            int left = 2 * i + 1;

            if (left >= size) {
                break;
            }

            int child = left;
            int childValue = a[left];
            metrics.steps++;

            int right = left + 1;

            if (right < size) {
                int rightValue = a[right];
                metrics.steps++;
                metrics.comparisons++;

                if (rightValue < childValue) {
                    child = right;
                    childValue = rightValue;
                }
            }

            metrics.comparisons++;

            if (childValue >= x) {
                break;
            }

            a[i] = childValue;
            metrics.moves++;
            i = child;
        }

        a[i] = x;

        if (i != originalIndex) {
            metrics.moves++;
        }
    }

    private void grow() {
        int[] bigger = new int[a.length * 2];

        for (int i = 0; i < size; i++) {
            bigger[i] = a[i];
            metrics.steps++;
            metrics.moves++;
        }

        a = bigger;
    }

    public boolean isHeap() {
        for (int i = 1; i < size; i++) {
            if (a[(i - 1) / 2] > a[i]) {
                return false;
            }
        }

        return true;
    }
}