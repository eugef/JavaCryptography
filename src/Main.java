import java.io.*;
import java.security.*;

import static java.lang.Math.ceil;

public class Main {
    final protected static char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

    final protected static int buff = 1024;

    public static void main(String[] args) throws FileNotFoundException, NoSuchAlgorithmException, IOException  {
        RandomAccessFile file = new RandomAccessFile("D:\\example.mp4", "r");
        MessageDigest hashSum = MessageDigest.getInstance("SHA-256");

        long offset = file.length();
        int blocksCount = (int) ceil((double) offset / buff);

        byte[] digest = null;

        for (int i = blocksCount-1; i >= 0; i--) {
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

        System.out.println(byteArrayToHexString(digest)) ;
    }

    public static String byteArrayToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] concat(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] c = new byte[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }
}
