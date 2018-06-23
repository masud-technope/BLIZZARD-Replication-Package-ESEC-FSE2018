/*******************************************************************************
 * Copyright (c) 2005, 2007 Remy Suen
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.protocol.msn.internal.encode;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class provides static methods to compute the SHA digest of strings.
 */
public class Encryption {

    private static MessageDigest shaDigest;

    static {
        try {
            //$NON-NLS-1$
            shaDigest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException();
        }
    }

    /**
	 * Computes the SHA hash of the bytes provided and then truncate it down to
	 * a length of twenty. A base 64 encoding of the twenty character string
	 * literal is then computed and returned.
	 * 
	 * @param bytes
	 *            the bytes to be computed from
	 * @return a base 64 encoding of a twenty character SHA hash of the bytes
	 *         provided
	 */
    public static String computeSHA(byte[] bytes) {
        synchronized (shaDigest) {
            bytes = shaDigest.digest(bytes);
        }
        StringBuffer buffer = new StringBuffer();
        synchronized (buffer) {
            for (int i = 0; i < 20; i++) {
                if (0 < bytes[i] && bytes[i] < 16) {
                    buffer.append('0');
                }
                buffer.append(Integer.toHexString((0xff & bytes[i])));
            }
            bytes = new byte[20];
            for (int i = 0; i < 20; i++) {
                bytes[i] = (byte) Integer.parseInt(buffer.substring(i * 2, i * 2 + 2), 16);
            }
        }
        return Base64.encodeBytes(bytes);
    }
}
