package deque;
// Random
import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class StuffNode {
        private T item;
        private StuffNode next;
        private StuffNode prev;

        StuffNode(T i, StuffNode p, StuffNode n) {
            item = i;
            prev = p;
            next = n;
        }

        /** Helper for getRecursive: gets item at index i using recursion within the StuffNode class
         * WARNING: Will not work if index is out of bounds. */
        public T getRecursive(int index) {
            if (index == 0) {
                return item;
            } else {
                return this.next.getRecursive(index - 1);
            }
        }
    }

    private StuffNode sentinel;
    private int size;

    /** Constructor, creates an empty deque. */
    public LinkedListDeque() {
        sentinel = new StuffNode(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

//    /** Constructor, creates a deque with 1 item. */
//    public LinkedListDeque(T item) {
//        sentinel = new StuffNode(null, null, null);
//        sentinel.next = new StuffNode(item, sentinel, sentinel);
//        sentinel.prev = sentinel.next;
//        size = 1;
//    }

    /** Adds an item to the front of the list and updates size. */
    @Override
    public void addFirst(T item) {
        sentinel.next.prev = new StuffNode(item, sentinel, sentinel.next);
        sentinel.next = sentinel.next.prev;
        size += 1;
    }

    /** Adds an item to the end of the list and updates size. */
    @Override
    public void addLast(T item) {
        sentinel.prev.next = new StuffNode(item, sentinel.prev, sentinel);
        sentinel.prev = sentinel.prev.next;
        size += 1;
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
     * Removes all references to the removed item so it gets tossed into the trash.
     * Also updates size.*/
    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T i = sentinel.next.item;
        sentinel.next.next.prev = sentinel;
        sentinel.next = sentinel.next.next;
        size -= 1;
        return i;
    }

    /** Removes and returns the item at the back of the deque. If no such item exists, returns null.
     * Removes all references to the removed item so it gets tossed into the trash.
     * Also updates size.*/
    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T i = sentinel.prev.item;
        sentinel.prev.prev.next = sentinel;
        sentinel.prev = sentinel.prev.prev;
        size -= 1;
        return i;
    }

    /** Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null. Must not alter the deque!
     * Must use iteration*/
    @Override
    public T get(int index) {
        StuffNode p = sentinel.next;
        if (index >= size || index < 0) {
            return null;
        }
        for (int i = 0; i <= index; i++) {
            if (i == index) {
                return p.item;
            }
            p = p.next;
        }
        return null;
    }

    /** Same as get but uses recursion */
    public T getRecursive(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        return sentinel.next.getRecursive(index);
    }

    /** returns an iterator (a.k.a. seer) into ME */
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private StuffNode p;

        LinkedListDequeIterator() {
            p = sentinel.next;
        }

        public boolean hasNext() {
            return p != sentinel;
        }

        public T next() {
            T returnItem = p.item;
            p = p.next;
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
