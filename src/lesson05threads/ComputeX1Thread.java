package lesson05threads;

import java.math.BigInteger;
import java.util.Hashtable;

/**
 * Thread to calculate h / g^x1 and search for them in the hash table.
 */
public class ComputeX1Thread implements Runnable {

    // variable shared among all threads to store result
    public static Long x = new Long(0);

    private Hashtable ht;
    private int min, max, B;
    private BigInteger p, h, g;

    public Thread thread;

    public ComputeX1Thread(Hashtable ht, BigInteger p, BigInteger g, BigInteger h, int B, int min, int max) {
        this.ht = ht;
        this.B = B;
        this.min = min;
        this.max = max;
        this.p = p;
        this.g = g;
        this.h = h;

        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        for (int x1 = min; x1 <= max; x1++) {
            System.out.printf("x1: %d | %.2f%% %n", x1, ((double) (x1 - min) / (max - min) * 100));
            // h / g^x1 = h * (g^x1)^-1
            Integer x0 = (Integer) ht.get(
                    h.multiply(g.modPow(BigInteger.valueOf(x1), p).modInverse(p)).mod(p)
            );
            if (x0 != null) {
                System.out.println("x0 = " + x0 + ", x1 = " + x1);
                x = (long) x0 * B + x1;
                break;
            }
            synchronized (x) {
                if (x != 0) {
                    // x found here or in other thread
                    break;
                }
            }
        }
    }
}
