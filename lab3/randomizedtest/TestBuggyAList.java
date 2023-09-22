package randomizedtest;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> works = new AListNoResizing<>();
        BuggyAList<Integer> buggy = new BuggyAList<>();
        for (int i = 1; i <= 3; i++){
            works.addLast(i);
            buggy.addLast(i);
        }
        assertEquals(works.size(), buggy.size());
        for (int i = 1; i <= 3; i++) {
            assertEquals(works.removeLast(), buggy.removeLast());
        }
    }

    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
            } else if (operationNumber == 1) {
                int size = L.size();
                int sizeB = B.size();
            } else if (operationNumber == 2) {
                if (L.size() != 0){
                    int l = L.getLast();
                }
                if (B.size() != 0){
                    int lb = B.getLast();
                }
            } else {
                if (L.size() != 0){
                    int r = L.removeLast();
                }
                if (B.size() != 0){
                    int rb = B.removeLast();
                }
            }
        }
    }
}
