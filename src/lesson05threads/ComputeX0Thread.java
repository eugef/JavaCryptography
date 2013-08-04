package lesson05threads;

import java.math.BigInteger;
import java.util.Hashtable;

/**
 * Thread to calculate all possible values of equation (g^B)^x0 and add them to hash table.
 */
public class ComputeX0Thread implements Runnable {

    private Hashtable ht;
    private int min, max, B;
    private BigInteger p, g;

    public Thread thread;

    public ComputeX0Thread(Hashtable ht, BigInteger p, BigInteger g, int B, int min, int max) {
        this.ht = ht;
        this.B = B;
        this.min = min;
        this.max = max;
        this.p = p;
        this.g = g;

        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        // calculate g^B that is used in x0 computation
        BigInteger gB = g.modPow(BigInteger.valueOf(B), p);

        for (int x0 = min; x0 <= max; x0++) {
            ht.put(
                    gB.modPow(BigInteger.valueOf(x0), p),
                    x0
            );
            System.out.printf("x0: %d | %.2f%% %n", x0, ((double) (x0 - min) / (max - min) * 100));
        }
    }
}
