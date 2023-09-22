package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    //Resizing not implemented yet, assumes a max of 8 items
    private T[] items; //Array of the items and nulls, static size
    private int size; //Size of the deque, dynamic
    private int f; //Index of the first item in items
    private static int defaultLength = 8;

    /** Helper, moves an index by some amount, looping around if necessary. */
    private int loopAdd(int move, int by) {
        return (move + items.length + by) % items.length;
    }
//
//    /** Helper, moves f by some amount, looping around if necessary. */
//    private void movef(int by) {
//        f = loop(f, by);
//    }
//
//    /** Helper, moves l by some amount, looping around if necessary. */
//    private void movel(int by) {
//        l = loop(l, by);
//    }

    /** Helper to resize the items array (should update f).
     * assumes x is reasonable */
    private void resize(int x) {
        T[] newItems = (T[]) new Object[items.length + x];
        for (int i = 0; i < Math.min(items.length, items.length + x); i++) {
            newItems[i] = get(i);
        }
        f = 0;
        items = newItems;
    }

//    /** Helper to resize the items array (should update f).
//     * f and everything after f should be shifted down by x
//     * everything before f should stay in the same place
//     * assumes x is reasonable */
//    private void resizeDown(int x) {
//        T[] newItems = (T[]) new Object[items.length - x];
//        f = f - x;
//        for (int i = 0; i < f; i++) {
//            newItems[i] = items[i];
//        }
//        for (int i = f; i < newItems.length; i++) {
//            newItems[i] = items[i + x];
//        }
//        items = newItems;
//    }

    /** Constructor, creates an empty deque. */
    public ArrayDeque() {
        items = (T []) new Object[defaultLength];
        size = 0;
        f = 4;
    }

//    /** Constructor, creates a deque with 1 item. */
//    public ArrayDeque(T item) {
//        items = (T []) new Object[8];
//        size = 0;
//    }

    /** Adds an item to the front of the list and updates size. */
    public void addFirst(T item) {
        if (size >= items.length) {
            resize(items.length);
        }
        size += 1;
        f = loopAdd(f, -1);
        items[f] = item;
    }

    /** Adds an item to the end of the list and updates size. */
    @Override
    public void addLast(T item) {
        if (size >= items.length) {
            resize(items.length);
        }
        size += 1;
        items[loopAdd(f, size - 1)] = item;
    }

//    /** Returns whether the deque is empty. */
//    @Override
//    public boolean isEmpty() {
//        return size == 0;
//    }

    /** Returns the size of the deque. */
    @Override
    public int size() {
        return size;
    }

    /** Prints the items in the deque from first to last, separated by a space.
     * Once all the items have been printed, print out a new line. */
    @Override
    public void printDeque() {
        for (int i = 0; i < size(); i++) {
            System.out.print(get(i) + " ");
        }
        System.out.println();
    }

    /** Removes and returns the item at the front of the deque.
     * If no such item exists, returns null.
     * Also updates size and moves index marker. */
    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        if (items.length >= 16 && size <= items.length / 4) {
            resize(-items.length / 2);
        }
        size -= 1;
        f = loopAdd(f, 1);
        return items[loopAdd(f, -1)];
    }

    /** Removes and returns the item at the back of the deque.
     * If no such item exists, returns null.
     * Also updates size and moves index marker. */
    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        if (items.length >= 16 && size <= items.length / 4) {
            resize(-items.length / 2);
        }
        size -= 1;
        return items[loopAdd(f, size)];
    }

    /** Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null. Must not alter the deque! */
    @Override
    public T get(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        return items[loopAdd(f, index)];
    }

    /** returns an iterator (a.k.a. seer) into ME */
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int wizPos;

        ArrayDequeIterator() {
            wizPos = 0;
        }

        public boolean hasNext() {
            return wizPos < size();
        }

        public T next() {
            T returnItem = get(wizPos);
            wizPos += 1;
            return returnItem;
        }
    }
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof Deque)) {
            return false;
        }
        // Will this crash if other is not type T??
        Deque<T> o = (Deque<T>) other;
        if (o.size() != this.size()) {
            return false;
        }
        for (int i = 0; i < this.size(); i++) {
            if (!(o.get(i).equals(this.get(i)))) {
                return false;
            }
        }
        return true;
    }
}
