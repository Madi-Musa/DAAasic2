# DAA Assignment 2 - Data Structures (In-Memory Workload Engine)

`DynamicArray`, `MyLinkedList` and `MinHeap` implemented from scratch (primitive `int`, no `java.util` collections in `src/main`),
with operation counters (steps / moves / comparisons), JUnit 5 tests, a benchmark and a report.

Requirements: JDK 17+, Maven 3.8+, Python 3 with `matplotlib` (only for plots).

## Build and test
```
mvn clean test
```

## Run the benchmark (seed 42, writes `results/results.csv` and `results/buildheap.csv`)
```
mvn -q compile exec:java
```
Every case: 3 warm-up runs (discarded) + 5 measured runs, the median time is saved. A global JIT warm-up runs first.

## Plots
```
python scripts/plot.py
```

## Layout
```
src/main/java/daa   DynamicArray, MyLinkedList, MinHeap, IntList, Metrics, Benchmark
src/test/java/daa   JUnit 5 tests
results/            results.csv, buildheap.csv, plots/
scripts/plot.py     charts from the CSV files
REPORT.md           complexity table, loop-invariant proofs, plots, discussion
```

## Counters (see `Metrics`)
* steps - one array cell read, or one list node visited
* moves - one element shifted/copied in an array, or one pointer update in a list
* comparisons - one comparison of two stored elements

Bonus B (Floyd `buildHeap`) is implemented in `MinHeap.buildHeap` and measured in `results/buildheap.csv`.