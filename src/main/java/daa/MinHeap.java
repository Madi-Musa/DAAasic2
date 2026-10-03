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
        System.arraycopy(array, 0, h.a, 0, array.length);
        h.size = array.length;
        for (int i = h.size / 2 - 1; i >= 0; i--) {
            h.siftDown(i, h.a[i]);
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
            int[] bigger = new int[a.length * 2];
            for (int i = 0; i < size; i++) {
                bigger[i] = a[i];
                metrics.moves++;
            }
            a = bigger;
        }
        int i = size++;
        while (i > 0) {
            int p = (i - 1) / 2;
            metrics.steps++;
            metrics.comparisons++;
            if (a[p] <= x) {
                break;
            }
            a[i] = a[p];
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
        int last = a[--size];
        metrics.steps++;
        if (size > 0) {
            siftDown(0, last);
        }
        return min;
    }

    private void siftDown(int i, int x) {
        while (true) {
            int l = 2 * i + 1;
            if (l >= size) {
                break;
            }
            int c = l;
            metrics.steps++; // read a[l]
            int r = l + 1;
            if (r < size) {
                metrics.steps++; // read a[r]
                metrics.comparisons++;
                if (a[r] < a[l]) {
                    c = r;
                }
            }
            metrics.comparisons++;
            if (a[c] >= x) {
                break;
            }
            a[i] = a[c];
            metrics.moves++;
            i = c;
        }
        a[i] = x;
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