package daa;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

public class Benchmark {
    static final int[] SIZES = {100, 1_000, 10_000, 100_000};
    static final int WARMUP_RUNS = 3;
    static final int MEASURED_RUNS = 5;
    static final long SEED = 42;
    static final int VALUE_BOUND = 1_000_000;

    static volatile long sink;

    record Result(
            double timeMs,
            long steps,
            long moves,
            long comparisons
    ) {}

    interface Run {
        Result run();
    }

    public static void main(String[] args) throws IOException {
        Path dir = Path.of(args.length > 0 ? args[0] : "results");
        Files.createDirectories(dir);

        globalWarmup();

        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(dir.resolve("results.csv")))) {

            out.println(
                    "workload,variant,structure,n,time_ms,steps,moves,comparisons");

            for (int n : SIZES) {
                Supplier<IntList> arr = DynamicArray::new;
                Supplier<IntList> lst = MyLinkedList::new;

                row(out, "W1", "-", "DynamicArray", n,
                        () -> w1(arr, n));
                row(out, "W1", "-", "MyLinkedList", n,
                        () -> w1(lst, n));

                row(out, "W2", "-", "DynamicArray", n,
                        () -> w2(arr, n));
                row(out, "W2", "-", "MyLinkedList", n,
                        () -> w2(lst, n));

                for (String variant : new String[]{"head", "middle"}) {
                    int pos = variant.equals("head") ? 0 : n / 2;

                    row(out, "W3", variant, "DynamicArray", n,
                            () -> w3(arr, n, pos));
                    row(out, "W3", variant, "MyLinkedList", n,
                            () -> w3(lst, n, pos));
                }

                row(out, "W4", "-", "MinHeap", n,
                        () -> w4(n));
            }
        }

        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(dir.resolve("buildheap.csv")))) {

            out.println(
                    "workload,variant,structure,n,time_ms,steps,moves,comparisons");

            for (int n : SIZES) {
                row(out, "B", "n-inserts", "MinHeap", n,
                        () -> bonusInsert(n));
                row(out, "B", "buildHeap", "MinHeap", n,
                        () -> bonusBuild(n));
            }
        }

        System.out.println(
                "Done. Results written to " + dir.toAbsolutePath());
    }

    static void globalWarmup() {
        Supplier<IntList> arr = DynamicArray::new;
        Supplier<IntList> lst = MyLinkedList::new;

        for (int i = 0; i < 20; i++) {
            w1(arr, 1_000);
            w1(lst, 1_000);

            w2(arr, 1_000);
            w2(lst, 1_000);

            w3(arr, 1_000, 0);
            w3(lst, 1_000, 0);

            w3(arr, 1_000, 500);
            w3(lst, 1_000, 500);

            w4(1_000);
            bonusInsert(1_000);
            bonusBuild(1_000);
        }
    }

    static void row(
            PrintWriter out,
            String workload,
            String variant,
            String structure,
            int n,
            Run run
    ) {
        for (int i = 0; i < WARMUP_RUNS; i++) {
            run.run();
        }

        Result[] results = new Result[MEASURED_RUNS];

        for (int i = 0; i < MEASURED_RUNS; i++) {
            results[i] = run.run();
        }

        Arrays.sort(results,
                (x, y) -> Double.compare(x.timeMs(), y.timeMs()));

        Result median = results[MEASURED_RUNS / 2];

        out.printf(
                Locale.ROOT,
                "%s,%s,%s,%d,%.3f,%d,%d,%d%n",
                workload,
                variant,
                structure,
                n,
                median.timeMs(),
                median.steps(),
                median.moves(),
                median.comparisons()
        );

        out.flush();

        System.out.printf(
                Locale.ROOT,
                "%s %-6s %-13s n=%-6d %10.3f ms%n",
                workload,
                variant,
                structure,
                n,
                median.timeMs()
        );
    }

    static int[] data(Random rnd, int n) {
        int[] values = new int[n];

        for (int i = 0; i < n; i++) {
            values[i] = rnd.nextInt(VALUE_BOUND) * 2;
        }

        return values;
    }

    static IntList fill(Supplier<IntList> factory, int[] data) {
        IntList structure = factory.get();

        for (int value : data) {
            structure.add(value);
        }

        structure.metrics().reset();
        return structure;
    }

    static Result result(IntList structure, long t0, long t1) {
        Metrics m = structure.metrics();

        return new Result(
                (t1 - t0) / 1e6,
                m.steps,
                m.moves,
                m.comparisons
        );
    }

    static Result w1(Supplier<IntList> factory, int n) {
        Random rnd = new Random(SEED);
        int[] values = data(rnd, n);

        int[] indexes = new int[10_000];

        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = rnd.nextInt(n);
        }

        IntList structure = fill(factory, values);
        long sum = 0;

        long t0 = System.nanoTime();

        for (int index : indexes) {
            sum += structure.get(index);
        }

        long t1 = System.nanoTime();

        sink = sum;
        return result(structure, t0, t1);
    }

    static Result w2(Supplier<IntList> factory, int n) {
        Random rnd = new Random(SEED);
        int[] values = data(rnd, n);

        int[] queries = new int[1_000];

        for (int i = 0; i < queries.length; i++) {
            queries[i] = (i % 2 == 0)
                    ? values[rnd.nextInt(n)]
                    : rnd.nextInt(VALUE_BOUND) * 2 + 1;
        }

        IntList structure = fill(factory, values);
        int found = 0;

        long t0 = System.nanoTime();

        for (int x : queries) {
            if (structure.contains(x)) {
                found++;
            }
        }

        long t1 = System.nanoTime();

        sink = found;

        if (found != 500) {
            throw new IllegalStateException(
                    "expected 500 hits, got " + found);
        }

        return result(structure, t0, t1);
    }

    static Result w3(Supplier<IntList> factory, int n, int pos) {
        Random rnd = new Random(SEED);
        int[] values = data(rnd, n);
        int[] insertedValues = data(rnd, 1_000);

        IntList structure = fill(factory, values);
        long sum = 0;

        long t0 = System.nanoTime();

        for (int value : insertedValues) {
            structure.add(pos, value);
        }

        for (int i = 0; i < insertedValues.length; i++) {
            sum += structure.remove(pos);
        }

        long t1 = System.nanoTime();

        sink = sum;

        if (structure.size() != n) {
            throw new IllegalStateException("size mismatch");
        }

        return result(structure, t0, t1);
    }

    static Result w4(int n) {
        Random rnd = new Random(SEED);
        int[] values = data(rnd, n);

        MinHeap heap = new MinHeap();

        long t0 = System.nanoTime();

        for (int value : values) {
            heap.insert(value);
        }

        int previous = Integer.MIN_VALUE;
        boolean sorted = true;

        for (int i = 0; i < n; i++) {
            int x = heap.extractMin();

            if (x < previous) {
                sorted = false;
            }

            previous = x;
        }

        long t1 = System.nanoTime();

        if (!sorted) {
            throw new IllegalStateException("heap output not sorted");
        }

        Metrics m = heap.metrics();
        sink = previous;

        return new Result(
                (t1 - t0) / 1e6,
                m.steps,
                m.moves,
                m.comparisons
        );
    }

    static Result bonusInsert(int n) {
        int[] values = data(new Random(SEED), n);
        MinHeap heap = new MinHeap();

        long t0 = System.nanoTime();

        for (int value : values) {
            heap.insert(value);
        }

        long t1 = System.nanoTime();

        Metrics m = heap.metrics();

        Result measured = new Result(
                (t1 - t0) / 1e6,
                m.steps,
                m.moves,
                m.comparisons
        );

        sink = heap.peekMin();

        return measured;
    }

    static Result bonusBuild(int n) {
        int[] values = data(new Random(SEED), n);

        long t0 = System.nanoTime();

        MinHeap heap = MinHeap.buildHeap(values);

        long t1 = System.nanoTime();

        Metrics m = heap.metrics();

        Result measured = new Result(
                (t1 - t0) / 1e6,
                m.steps,
                m.moves,
                m.comparisons
        );

        sink = heap.peekMin();

        return measured;
    }
}