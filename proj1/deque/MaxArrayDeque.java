package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> defaultComparator;

    /** creates a MaxArrayDeque with the given Comparator. */
    public MaxArrayDeque(Comparator<T> c) {
        super();
        defaultComparator = c;
    }

    /** returns the maximum element in the deque as governed by the previously given Comparator.
     * If the MaxArrayDeque is empty, simply return null. */
    public T max() {
        T biggest = get(0);
        for (int i = 1; i < size(); i++) {
            if (defaultComparator.compare(biggest, get(i)) < 0) {
                biggest = get(i);
            }
        }
        return biggest;
    }

    /** returns the maximum element in the deque as governed by the parameter Comparator c.
     * If the MaxArrayDeque is empty, simply return null. */
    public T max(Comparator<T> c) {
        T biggest = get(0);
        for (int i = 1; i < size(); i++) {
            if (c.compare(biggest, get(i)) < 0) {
                biggest = get(i);
            }
        }
        return biggest;
    }
}
