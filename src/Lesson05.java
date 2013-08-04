import java.math.BigInteger;
import java.util.Hashtable;

import static java.lang.Math.pow;

/**
 * Your goal this week is to write a program to compute discrete log modulo a prime p. Let g be some element in Z∗p and
 * suppose you are given h in Z∗p such that h = g^x where 1 ≤ x ≤ 2^40. Your goal is to find x.
 * More precisely, the input to your program is p, g, h and the output is x.
 * <p/>
 * The trivial algorithm for this problem is to try all 2^40 possible values of x until the correct one is found,
 * that is until we find an x satisfying h = g*x in Zp. This requires 240 multiplications. In this project you will
 * implement an algorithm that runs in time roughly 2^40 = √2^20 using a meet in the middle attack.
 * <p/>
 * Let B = 2^20.
 * Since x is less than B^2 we can write the unknown x base B as x = x0*B + x1 where x0,x1 are in the range [0, B−1].
 * Then
 * h = g^x = g^(x*0B+x1)=(g^B)^x0 * g^x1  in Zp.
 * By moving the term g^x1 to the other side we obtain
 * h / g^x1 = (g^B)^x0      in Zp.
 * <p/>
 * The variables in this equation are x0,x1 and everything else is known: you are given g,h and B = 2^20.
 * Since the variables x0 and x1 are now on different sides of the equation we can find a solution using meet in the middle.
 * <p/>
 * First build a hash table of all possible values of the left hand side h / g^x1 for x1 = 0,1,…,2^20.
 * Then for each value x0 = 0,1,2,…,2^20 check if the right hand side (g^B)^x0 is in this hash table.
 * If so, then you have found a solution (x0,x1) from which you can compute the required x as x = x*0B+x1.
 * The overall work is about 2^20 multiplications to build the table and another 2^20 lookups in this table.
 * <p/>
 * Now that we have an algorithm, here is the problem to solve:
 * p = 134078079299425970995740249982058461274793658205923933\
 *     77723561443721764030073546976801874298166903427690031\
 *     858186486050853753882811946569946433649006084171
 * g = 11717829880366207009516117596335367088558084999998952205\
 *     59997945906392949973658374667057217647146031292859482967\
 *     5428279466566527115212748467589894601965568
 * h = 323947510405045044356526437872806578864909752095244\
 *     952783479245297198197614329255807385693795855318053\
 *     2878928001494706097394108577585732452307673444020333
 * <p/>
 * Each of these three numbers is about 153 digits. Find x such that h = g^x in Zp.
 * <p/>
 * NOTICE!
 * <p/>
 * Hash table will contains all possible values of right hand side (g^B)^x0, because it is faster to calculate than left
 * side. g^B can be calculated once before loop.
 */
public class Lesson05 {

    public static void main(String[] args) {
        BigInteger p = new BigInteger(
                "134078079299425970995740249982058461274793658205923933" +
                "77723561443721764030073546976801874298166903427690031" +
                "858186486050853753882811946569946433649006084171"
        );
        BigInteger g = new BigInteger(
                "11717829880366207009516117596335367088558084999998952205" +
                "59997945906392949973658374667057217647146031292859482967" +
                "5428279466566527115212748467589894601965568");
        BigInteger h = new BigInteger(
                "323947510405045044356526437872806578864909752095244" +
                "952783479245297198197614329255807385693795855318053" +
                "2878928001494706097394108577585732452307673444020333"
        );

        int B = (int) pow(2, 20);
        // calculate g^B that is used in x0 computation
        BigInteger gB = g.modPow(BigInteger.valueOf(B), p);

        long x = 0;

        Hashtable hg = new Hashtable();

        long startTime = System.nanoTime();

        System.out.println("Calculating (g ^ B) ^ x0 ...");

        for (int x0 = 0; x0 <= B; x0++) {
            hg.put(
                    gB.modPow(BigInteger.valueOf(x0), p),
                    x0
            );
            System.out.printf("x0: %d | %.2f%% %n", x0, ((double) x0 / B) * 100);
        }

        System.out.println("Calculating h / g ^ x1 ...");

        // Trick - we know that x1 is located closer to B
        for (int x1 = B; x1 >= 0; x1--) {
            System.out.printf("x1: %d | %.2f%% %n", x1, ((double) x1 / B) * 100);
            // h / g^x1 = h * (g^x1)^-1
            Integer x0 = (Integer) hg.get(
                    h.multiply(g.modPow(BigInteger.valueOf(x1), p).modInverse(p)).mod(p)
            );
            if (x0 != null) {
                System.out.println("x0 " + x0);
                x = (long) x0 * B + x1;
                break;
            }
        }

        System.out.println("x = " + x);

        long stopTime = System.nanoTime();
        System.out.println("time: " + (stopTime - startTime));
    }
}
