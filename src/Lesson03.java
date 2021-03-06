import java.io.*;
import java.security.*;

import static java.lang.Math.ceil;

/**
 * Suppose a web site hosts large video file F that anyone can download. Browsers who download the file need to make
 * sure the file is authentic before displaying the content to the user. One approach is to have the web site hash the
 * contents of F using a collision resistant hash and then distribute the resulting short hash value h=H(F) to users via
 * some authenticated channel (later on we will use digital signatures for this). Browsers would download the entire
 * file F, check that H(F) is equal to the authentic hash value h and if so, display the video to the user.
 * <p/>
 * Unfortunately, this means that the video will only begin playing after the *entire* file F has been downloaded.
 * Our goal in this project is to build a file authentication system that lets browsers authenticate and play video
 * chunks as they are downloaded without having to wait for the entire file.
 * <p/>
 * Instead of computing a hash of the entire file, the web site breaks the file into 1KB blocks (1024 bytes).
 * It computes the hash of the last block and appends the value to the second to last block. It then computes the hash
 * of this augmented second to last block and appends the resulting hash to the third block from the end.
 * <p/>
 * The final hash value h0 – a hash of the first block with its appended hash – is distributed to users via the
 * authenticated channel as above.
 * <p/>
 * Now, a browser downloads the file F one block at a time, where each block includes the appended hash value from the
 * diagram above. When the first block (B0 ∥∥ h1) is received the browser checks that H(B0 ∥∥ h1) is equal to h0 and if
 * so it begins playing the first video block. When the second block (B1 ∥∥ h2) is received the browser checks that
 * H(B1 ∥ h2) is equal to h1 and if so it plays this second block. This process continues until the very last block.
 * This way each block is authenticated and played as it is received and there is no need to wait until the entire
 * file is downloaded.
 * <p/>
 * It is not difficult to argue that if the hash function H is collision resistant then an attacker cannot modify any
 * of the video blocks without being detected by the browser. Indeed, since h0=H(B0 ∥∥ h1) an attacker cannot find a
 * pair (B′0,h′1)≠(B0,h1) such that h0=H(B0 ∥∥ h1) since this would break collision resistance of H. Therefore after
 * the first hash check the browser is convinced that both B0 and h1 are authentic. Exactly the same argument proves
 * that after the second hash check the browser is convinced that both B1 and h2 are authentic, and so on for the
 * remaining blocks.
 * <p/>
 * In this project we will be using SHA256 as the hash function. When appending the hash value to each block, please
 * append it as binary data, that is, as 32 unencoded bytes (which is 256 bits). If the file size is not a multiple of
 * 1KB then the very last block will be shorter than 1KB, but all other blocks will be exactly 1KB.
 */
public class Lesson03 {
    final protected static char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    final protected static int buff = 1024;

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        RandomAccessFile file = new RandomAccessFile("D:\\example.mp4", "r");
        MessageDigest hashSum = MessageDigest.getInstance("SHA-256");

        long offset = file.length();
        int blocksCount = (int) ceil((double) offset / buff);

        byte[] digest = null;

        for (int i = blocksCount - 1; i >= 0; i--) {
            file.seek(i * buff);
            int blockSize = (i * buff + buff > offset) ? (int) (offset - i * buff) : buff;
            byte[] buffer = new byte[blockSize];
            file.read(buffer, 0, blockSize);

            if (digest == null) {
                digest = hashSum.digest(buffer);
            } else {
                digest = hashSum.digest(concat(buffer, digest));
            }
        }

        file.close();

        System.out.println(byteArrayToHexString(digest));
    }

    public static String byteArrayToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] concat(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] c = new byte[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }
}
