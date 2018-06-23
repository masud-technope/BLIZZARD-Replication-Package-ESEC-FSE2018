package org.eclipse.ecf.protocol.msn.internal.encode;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5HashImpl {

    public static String getMD5(String input) {
        try {
            //$NON-NLS-1$
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = //$NON-NLS-1$
                "0" + //$NON-NLS-1$
                hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
	 * @throws NoSuchAlgorithmException  
	 */
    public static void main(String[] args) throws NoSuchAlgorithmException {
        //$NON-NLS-1$
        System.out.println(getMD5("22210219642164014968YMM8C_H7KCQ2S_KL"));
    }
}
