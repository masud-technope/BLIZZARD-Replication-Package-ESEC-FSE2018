/*******************************************************************************
 * Copyright (c) 2006, 2009 Remy Suen, Composent Inc., and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *    Matthew Jucius <matthew@jucius.com> - Bug 265174
 ******************************************************************************/
package org.eclipse.ecf.protocol.bittorrent.internal.encode;

public final class Encode {

    public static byte encodeForBitfield(char[] array) {
        char[] bitfield = { '0', '0', '0', '0', '0', '0', '0', '0' };
        int length = array.length;
        for (int i = 0; i < length; i++) {
            bitfield[length - 1 - i] = array[i];
        }
        return (byte) Integer.parseInt(new String(bitfield), 2);
    }

    public static void placeRequestInformation(byte[] request, int[] info) {
        int count = 0;
        for (int i = 5; i < 17; i += 4) {
            request[i] = info[count] > 16777215 ? (byte) (info[count] / 16777216) : 0;
            request[i + 1] = info[count] > 65535 ? (byte) (info[count] / 65536) : 0;
            request[i + 2] = info[count] > 255 ? (byte) (info[count] / 256) : 0;
            request[i + 3] = (byte) (info[count] % 256);
            count++;
        }
    }

    /**
	 * Places the specified integer value into the byte array as a value that
	 * can be represented by four bytes.
	 * 
	 * @param haveArray
	 *            the byte array to insert the value into
	 * @param number
	 *            the desired number to insert
	 * @param index
	 *            the index in <code>haveArray</code> that the value should be
	 *            inserted into
	 */
    public static void putIntegerAsFourBytes(byte[] haveArray, int number, int index) {
        haveArray[index] = (byte) (number >>> 24);
        haveArray[index + 1] = (byte) (number >>> 16);
        haveArray[index + 2] = (byte) (number >>> 8);
        haveArray[index + 3] = (byte) number;
    }

    /**
	 * Private constructor to prevent instantiation.
	 */
    private  Encode() {
    // do nothing
    }
}
