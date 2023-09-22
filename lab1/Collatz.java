/** Class that prints the Collatz sequence starting from a given number.
 *  @author YOUR NAME HERE
 */
public class Collatz {
    public static void main(String[] args) {
        int n = 5;
        String res = "5 ";
        while (n!=1){
            n=nextNumber(n);
            res = res + n + " ";
        }
        System.out.print(res);

    }
    public static int nextNumber(int n){
        /** Returns next number in collatz sequence */
        if (n%2==0){
            return n/2;
        }
        else {
            return 3*n+1;
        }
    }
}

