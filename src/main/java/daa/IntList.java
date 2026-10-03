package daa;

public interface IntList {
    int size();

    void add(int x);
    void add(int index, int x);

    int remove(int index);
    int get(int index);

    boolean contains(int x);
    Metrics metrics();
}