package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        int M = 10000;
        AList<Integer> Ms = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> Ns = new AList<>();
        for (int i = 0; i <= 7; i++){
            Ms.addLast(M);
            Ns.addLast((int) Math.pow(2,i) * 1000);
            SLList<Integer> a = new SLList<>();
            for (int j = 0; j < Ns.getLast(); j++){
                a.addFirst(i);
            }
            Stopwatch sw = new Stopwatch();
            for (int m = 0; m < M; m++){
                a.getLast();
            }
            times.addLast(sw.elapsedTime());
        }
        printTimingTable(Ns, times, Ms);
    }

}
