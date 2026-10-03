import csv
import os

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

OUT = os.path.join("results", "plots")
os.makedirs(OUT, exist_ok=True)


def load(path):
    rows = []
    with open(path) as f:
        for r in csv.DictReader(f):
            rows.append({**r, "n": int(r["n"]), "time_ms": float(r["time_ms"]),
                         "steps": int(r["steps"]), "moves": int(r["moves"]),
                         "comparisons": int(r["comparisons"])})
    return rows


STYLE = {"DynamicArray": "-", "MyLinkedList": "--", "MinHeap": "-"}
MCOL = {"steps": "tab:blue", "moves": "tab:orange", "comparisons": "tab:green"}
TCOL = {"DynamicArray": "tab:blue", "MyLinkedList": "tab:red", "MinHeap": "tab:purple"}
TITLES = {"W1": "W1 Random access: 10 000 get(i)", "W2": "W2 Search: 1 000 contains(x)",
          "W3": "W3 Insert & remove: 1 000 + 1 000 ops", "W4": "W4 Priority processing: n inserts + n extractMin"}


def series(rows, w, variant, structure):
    s = sorted((r for r in rows if r["workload"] == w and r["variant"] == variant and r["structure"] == structure),
               key=lambda r: r["n"])
    return [r["n"] for r in s], s


def panel_time(ax, rows, w, variant):
    for st in ("DynamicArray", "MyLinkedList", "MinHeap"):
        xs, s = series(rows, w, variant, st)
        if xs:
            ax.plot(xs, [r["time_ms"] for r in s], STYLE[st], marker="o", color=TCOL[st], label=st)
    ax.set_xscale("log"); ax.set_yscale("log")
    ax.set_xlabel("n (number of elements)"); ax.set_ylabel("time (ms, median of 5 runs)")
    ax.set_title("Time vs n" + (f" ({variant})" if variant != "-" else ""))
    ax.grid(True, which="both", alpha=0.3); ax.legend()


def panel_ops(ax, rows, w, variant):
    for st in ("DynamicArray", "MyLinkedList", "MinHeap"):
        xs, s = series(rows, w, variant, st)
        if not xs:
            continue
        for m in ("steps", "moves", "comparisons"):
            ys = [r[m] for r in s]
            if any(y > 0 for y in ys):
                ax.plot(xs, [max(y, 0.5) for y in ys], STYLE[st], marker="s", markersize=4,
                        color=MCOL[m], label=f"{st}: {m}")
    ax.set_xscale("log"); ax.set_yscale("log")
    ax.set_xlabel("n (number of elements)"); ax.set_ylabel("operations (count)")
    ax.set_title("Steps / moves / comparisons vs n" + (f" ({variant})" if variant != "-" else ""))
    ax.grid(True, which="both", alpha=0.3); ax.legend(fontsize=7)


rows = load(os.path.join("results", "results.csv"))
for w in ("W1", "W2", "W3", "W4"):
    variants = ["head", "middle"] if w == "W3" else ["-"]
    fig, axes = plt.subplots(len(variants), 2, figsize=(12, 4.5 * len(variants)), squeeze=False)
    for i, v in enumerate(variants):
        panel_time(axes[i][0], rows, w, v)
        panel_ops(axes[i][1], rows, w, v)
    fig.suptitle(TITLES[w], fontsize=13)
    fig.tight_layout()
    fig.savefig(os.path.join(OUT, f"{w}.png"), dpi=130)
    plt.close(fig)

bh = os.path.join("results", "buildheap.csv")
if os.path.exists(bh):
    rows = load(bh)
    fig, axes = plt.subplots(1, 2, figsize=(12, 4.5))
    for v, c in (("n-inserts", "tab:red"), ("buildHeap", "tab:blue")):
        s = sorted((r for r in rows if r["variant"] == v), key=lambda r: r["n"])
        axes[0].plot([r["n"] for r in s], [r["time_ms"] for r in s], marker="o", color=c, label=v)
        axes[1].plot([r["n"] for r in s], [r["comparisons"] for r in s], marker="o", color=c, label=v)
    for ax, yl in zip(axes, ("time (ms, median of 5 runs)", "comparisons (count)")):
        ax.set_xscale("log"); ax.set_yscale("log"); ax.set_xlabel("n (number of elements)")
        ax.set_ylabel(yl); ax.grid(True, which="both", alpha=0.3); ax.legend()
    axes[0].set_title("Bonus B: time"); axes[1].set_title("Bonus B: comparisons")
    fig.tight_layout(); fig.savefig(os.path.join(OUT, "bonus_buildheap.png"), dpi=130); plt.close(fig)
print("plots written to", OUT)