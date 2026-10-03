# Assignment 2 - Data Structures: DynamicArray, MyLinkedList, MinHeap

Student: *Mussa Madi, SE-2509*  |  Course: Design and Analysis of Algorithms

All structures store primitive `int`. Counters are incremented inside the methods (see README). Benchmarks use `new Random(42)`,
3 discarded warm-up runs + median of 5 runs, sizes n = 100, 1 000, 10 000, 100 000. Raw data: `results/results.csv`.

## 1. Complexity table

Notation: n = current size. "Avg" assumes a uniformly random index (arrays/lists) or random keys (heap).

| Structure | Operation | Best | Average | Worst | Justification |
|---|---|---|---|---|---|
| DynamicArray | `get(i)` | Θ(1) | Θ(1) | Θ(1) | One indexed read. |
| DynamicArray | `add(x)` | Θ(1) | Θ(1) amortized | Θ(n) single call (amortized O(1)) | Write at `a[size]`; a full array triggers a copy of n cells, but doubling means n copies are paid by the previous n/2 cheap adds. |
| DynamicArray | `add(i,x)` | Θ(1) (i = size) | Θ(n) | Θ(n) (i = 0) | Shifts `size - i` elements; for random i the average is n/2. |
| DynamicArray | `remove(i)` | Θ(1) (i = size-1) | Θ(n) | Θ(n) (i = 0) | Shifts `size - i - 1` elements. |
| DynamicArray | `contains(x)` | Θ(1) (x at index 0) | Θ(n) | Θ(n) (x absent) | Linear scan; absent x always scans all n cells. |
| MyLinkedList | `get(i)` | Θ(1) (i = 0) | Θ(n) | Θ(n) (i = n-1) | Walks i+1 nodes from the head. |
| MyLinkedList | `add(x)` | Θ(1) | Θ(1) | Θ(1) | Tail pointer: two pointer updates. |
| MyLinkedList | `add(i,x)` | Θ(1) (i = 0 or n) | Θ(n) | Θ(n) (i = n-1) | Walk to node i-1 (i steps), then 2 pointer updates. |
| MyLinkedList | `remove(i)` | Θ(1) (i = 0) | Θ(n) | Θ(n) | Walk to node i-1, then 1 pointer update. |
| MyLinkedList | `contains(x)` | Θ(1) | Θ(n) | Θ(n) | Same linear scan as the array. |
| MinHeap | `peekMin()` | Θ(1) | Θ(1) | Θ(1) | Reads `a[0]`. |
| MinHeap | `insert(x)` | Θ(1) (x ≥ parent) | Θ(1) (random keys) | Θ(log n) (new global minimum) | Bubble-up climbs at most ⌊log₂ n⌋ levels; random keys rarely climb more than a constant number of levels. |
| MinHeap | `extractMin()` | Θ(1) (all keys equal) | Θ(log n) | Θ(log n) | The moved last element is a leaf-level value and normally sinks almost to the bottom (about 2 comparisons per level). |
| MinHeap | `buildHeap` (bonus) | Θ(n) | Θ(n) | Θ(n) | Sum over levels of (nodes at level x height) = O(n). |

Auxiliary space: DynamicArray Θ(n) (capacity < 2n, plus a temporary copy of n cells during growth), MyLinkedList Θ(n)
(one node object with header, value and pointer per element), MinHeap Θ(n) (array, capacity < 2n); all operations use O(1) extra space.

## 2. Loop invariant proofs

### 2.1 `DynamicArray.contains(x)`

```java
for (int i = 0; i < size; i++) {
        if (a[i] == x) return true;
        }
        return false;
```

* **Invariant:** before the iteration with index i, x does not occur in `a[0..i-1]`.
* **Initialization:** i = 0, the range `a[0..-1]` is empty, so the statement is trivially true.
* **Maintenance:** assume x is not in `a[0..i-1]` and the iteration runs. If `a[i] == x` the method returns true and the loop is left. Otherwise `a[i] != x`, so x is not in `a[0..i]`, which is exactly the invariant for i+1.
* **Termination:** the loop ends normally when i = size. The invariant then says x is not in `a[0..size-1]`, i.e. in no stored element.
* **Conclusion:** `true` is returned only when `a[i] == x` for a valid i, so it is never wrong; `false` is returned only after termination, where the invariant proves absence. The loop makes at most `size` iterations, so it always terminates, and `contains` is correct.

### 2.2 `MinHeap.siftDown` (the loop of `extractMin`)

`extractMin` saves `min = a[0]`, takes `x = a[--size]` (the last element) and calls `siftDown(0, x)`: position i is a "hole", x is the value to place.

```java
while (true) {
    int l = 2*i+1;
    if (l >= size) break;

    int c = l;
    if (l+1 < size && a[l+1] < a[l]) c = l+1;

    if (a[c] >= x) break;

    a[i] = a[c];
    i = c;
}
a[i] = x;
```

* **Invariant** (before each iteration, with position i regarded as holding x):
  **(I1)** `a[parent] <= a[child]` holds for every parent-child pair of the tree `[0..size-1]` except possibly the pairs (i, child of i);
  **(I2)** if i > 0 then `a[parent(i)] <= x` and `a[parent(i)] <= a[child]` for every child of i.
* **Initialization (i = 0):** before the call the array was a valid heap. Removing the last position (a leaf) leaves every pair among positions `1..size-1` unchanged and valid; the only pairs involving position 0 are (0, child), which are excepted. (I2) is vacuous because i = 0 has no parent.
* **Maintenance:** let the iteration continue, so i has a child and c is its smaller child with `a[c] < x`. The body does `a[i] = a[c]; i = c`. Let i' = c be the new hole.
  * Pair (i, c): the old hole now holds the old `a[c]`, and the new hole virtually holds x; `a[c] < x` gives `a[i] <= x`, so the pair is valid.
  * Pair (i, other child s): c was the smaller child, so `a[c] <= a[s]`, valid.
  * Pair (parent(i), i): the old hole now holds the old `a[c]`; by (I2) of the previous iteration `a[parent(i)] <= a[c]`, valid.
  * (I2) for i': `a[parent(i')] = a[i]` (new value = old `a[c]`) `< x`, and the children of c still have their original values from the initial valid heap, so `a[c] <= a[child of c]`, which gives `a[parent(i')] <= a[child of i']`.
  * All other pairs are untouched and valid by (I1). So (I1) and (I2) hold for i'.
* **Termination:** each iteration moves i from level k to level k+1, so the loop ends after at most ⌊log₂ n⌋ iterations, either (a) i has no child, or (b) `a[c] >= x` for the smaller child c. Then `a[i] = x` fills the hole.
* **Conclusion:** at exit the pair (parent(i), i) is valid by (I2) (`a[parent(i)] <= x`); in case (a) the pairs (i, child) do not exist, in case (b) x is at most the smaller child and therefore at most both children, so they are valid too. All other pairs are valid by (I1), hence the whole array is a min-heap again, and `extractMin` returns the smallest element. Because the heap property holds after every `extractMin`, n calls return the keys in non-decreasing order.

## 3. Results and plots

Each figure has "time vs n" on the left and "steps / moves / comparisons vs n" on the right.

![W1](results/plots/W1.png)

![W2](results/plots/W2.png)

![W3](results/plots/W3.png)

![W4](results/plots/W4.png)

### Bonus: buildHeap

![Bonus buildHeap](results/plots/bonus_buildheap.png)

Selected values for n = 100 000 (median of 5 runs):

| Workload | Structure | time (ms) | steps | moves | comparisons |
|---|---|---:|---:|---:|---:|
| W1 get | DynamicArray | 0.018 | 10 000 | 0 | 0 |
| W1 get | MyLinkedList | 838.573 | 504 930 938 | 0 | 0 |
| W2 contains | DynamicArray | 21.869 | 73 835 543 | 0 | 73 835 543 |
| W2 contains | MyLinkedList | 165.383 | 73 835 043 | 0 | 73 835 543 |
| W3 head | DynamicArray | 11.415 | 201 000 000 | 200 999 000 | 0 |
| W3 head | MyLinkedList | 0.006 | 1 000 | 3 000 | 0 |
| W3 middle | DynamicArray | 5.696 | 101 000 000 | 100 999 000 | 0 |
| W3 middle | MyLinkedList | 186.727 | 100 001 000 | 3 000 | 0 |
| W4 | MinHeap | 8.767 | 3 390 180 | 1 759 235 | 3 059 125 |

Bonus buildHeap results for n = 100 000:

| Method | time (ms) | steps | moves | comparisons |
|---|---:|---:|---:|---:|
| n separate inserts | 1.224 | 358 718 | 258 731 | 227 662 |
| Floyd buildHeap | 0.889 | 338 424 | 213 453 | 188 424 |

Absolute times vary between runs and machines.
Small cases such as n = 100 are the noisiest because the operations finish in a very small amount of time. This is why warm-up runs are performed before the measured runs. The counters are deterministic for the same input and seed.

## 4. Discussion

1. `get(i)` costs exactly 1 step in DynamicArray and i+1 steps in MyLinkedList. For n = 100 000, 10 000 random reads require 10 000 steps for DynamicArray and about 505 million steps for MyLinkedList. The measured times are 0.018 ms and 838.573 ms respectively.

2. The reason DynamicArray performs well for random access is that an array provides direct indexed access. MyLinkedList must start from the head and follow nodes until it reaches the required index.

3. W1 clearly demonstrates the difference between Θ(1) random access in DynamicArray and Θ(n) random access in MyLinkedList. As n grows, the linked-list time increases strongly while the DynamicArray time remains very small.

4. In W2, both structures use linear search. For n = 100 000, DynamicArray performs 73 835 543 comparisons and MyLinkedList performs the same number of comparisons.

5. Even though both W2 implementations are Θ(n), DynamicArray finishes in 21.869 ms while MyLinkedList takes 165.383 ms. Therefore, equal asymptotic complexity does not mean equal real execution time.

6. One important reason is memory layout. DynamicArray stores primitive integers next to each other in one array, while MyLinkedList stores each value in a separate node connected through references.

7. Sequential array access has good cache locality. When the CPU loads one part of the array, nearby values are likely to be loaded into the cache as well.

8. A linked list requires pointer chasing. The program must read the current node's reference before it knows where the next node is located, which creates more memory-access overhead.

9. W3 demonstrates an important trade-off between arrays and linked lists.

10. In the head variant, MyLinkedList is very efficient because inserting and removing at the head only changes a small number of references. For n = 100 000, it performs only 3 000 moves and takes 0.006 ms.

11. DynamicArray is much more expensive at the head because every insertion or removal requires shifting many elements. In the same workload it performs 200 999 000 moves and takes 11.415 ms.

12. Therefore, MyLinkedList has a clear advantage for repeated operations at the front of the structure.

13. The situation changes for operations in the middle. A linked list must first traverse nodes to find the requested position.

14. In W3 middle, MyLinkedList performs about 100 million steps and takes 186.727 ms. DynamicArray performs about 101 million moves but only takes 5.696 ms.

15. This shows that moving contiguous array elements can be considerably faster in practice than traversing the same approximate number of linked-list nodes.

16. Therefore, DynamicArray is generally suitable for random access, searching and many middle operations, while MyLinkedList is useful when operations are concentrated at the head or when the required node is already known.

17. W4 evaluates MinHeap as a priority structure. A MinHeap keeps the smallest element at the root, so `peekMin()` is Θ(1).

18. Insertion and `extractMin()` may move through the height of the heap, so their worst-case complexity is Θ(log n).

19. For n = 100 000, W4 performs 100 000 inserts and 100 000 `extractMin` operations in 8.767 ms.

20. The measured number of comparisons is 3 059 125, while the number of moves is 1 759 235. Their growth is consistent with the expected heap behaviour as n increases.

21. MinHeap is represented using an array. For an element at index i, the left child is at `2*i + 1` and the right child is at `2*i + 2`. Because of this representation, separate node objects and child pointers are not required.

22. The bonus experiment compares two ways of creating a heap: inserting every element separately and Floyd's bottom-up `buildHeap`.

23. For n = 100 000, separate inserts take 1.224 ms, while `buildHeap` takes 0.889 ms.

24. Separate inserts perform 227 662 comparisons and 258 731 moves. `buildHeap` performs 188 424 comparisons and 213 453 moves.

25. This result agrees with the theoretical difference. Repeated insertion has worst-case Θ(n log n) total complexity, while Floyd's `buildHeap` has Θ(n) complexity.

26. For random values, the practical difference is not extremely large because many inserted elements do not travel all the way to the root. However, `buildHeap` still performs fewer operations for the large test case.

27. Overall, the benchmark results agree with the expected behaviour of the three data structures. DynamicArray is strong for indexing and contiguous access, MyLinkedList is strong for head operations, and MinHeap is appropriate when efficient minimum-priority processing is needed.

## 5. Testing

The project contains 30 JUnit 5 tests.

The tests cover:

* empty structures;
* one-element structures;
* duplicate values;
* first and last indices;
* invalid indices;
* DynamicArray capacity growth;
* 5 000 random operations compared with `java.util.ArrayList`;
* operation counters;
* DynamicArray `get(i)` step counting;
* MyLinkedList traversal and tail behaviour;
* MinHeap empty-heap exceptions;
* heap property after insertions;
* heap property after extractions;
* sorted extraction output compared with `Arrays.sort`;
* Floyd's `buildHeap`.

All tests pass successfully:

```text
Tests run: 30, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

The successful test run confirms that the implemented data structures satisfy the tested functional requirements and that the benchmark can be executed without errors.