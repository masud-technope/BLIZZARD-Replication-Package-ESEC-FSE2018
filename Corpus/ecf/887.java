/*******************************************************************************
 * Copyright (c) 2006, 2009 Remy Suen, Composent Inc., and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.protocol.bittorrent.internal.encode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public final class Decode {

    /**
	 * Reads four bytes in succesion within the specified byte array and return
	 * the corresponding integer value.
	 * 
	 * @param bytes
	 *            the byte array with the four bytes to decode
	 * @param index
	 *            the starting index of the four-byte value
	 * @return the integer that is encoded within the four bytes of the array
	 */
    public static int decodeFourByteNumber(byte[] bytes, int index) {
        return (bytes[index] & 0xff) * 16777216 + (bytes[index + 1] & 0xff) * 65536 + (bytes[index + 2] & 0xff) * 256 + (bytes[index + 3] & 0xff);
    }

    public static int decodeSignedByte(int num) {
        num = num & 0xff;
        if (num < 128) {
            return num + 127;
        }
        String str = Integer.toString(num, 2);
        char[] array = new char[8];
        for (int j = 0; j < str.length(); j++) {
            array[j] = str.charAt(7 - j);
        }
        return Integer.parseInt(new String(array), 2);
    }

    private static String getInteger(String input) {
        return input.substring(1, input.indexOf('e'));
    }

    private static String getString(String input) {
        int index = input.indexOf(':') + 1;
        int length = Integer.parseInt(input.substring(0, index - 1));
        return input.substring(index, index + length);
    }

    private static List getList(String string) {
        List list = new ArrayList() {

            private static final long serialVersionUID = 1093559083643494037L;

            public String toString() {
                StringBuffer buffer = new StringBuffer();
                synchronized (buffer) {
                    buffer.append('l');
                    for (int i = 0; i < size(); i++) {
                        Object obj = get(i);
                        if (obj instanceof String) {
                            String string = (String) obj;
                            buffer.append(string.length()).append(':').append(string);
                        } else if (obj instanceof Long) {
                            buffer.append('i' + String.valueOf(obj) + 'e');
                        } else if (obj instanceof List || obj instanceof BEncodedDictionary) {
                            buffer.append(obj.toString());
                        }
                    }
                    buffer.append('e');
                }
                return buffer.toString();
            }
        };
        char ch = string.charAt(0);
        while (ch != 'e') {
            if (Character.isDigit(ch)) {
                if (ch == '0') {
                    //$NON-NLS-1$
                    list.add("");
                    string = string.substring(2);
                } else {
                    String value = getString(string);
                    string = string.substring(string.indexOf(value, string.indexOf(':')) + value.length());
                    list.add(value);
                }
            } else if (ch == 'd') {
                BEncodedDictionary dictionary = parse(string.substring(1));
                list.add(dictionary);
                String value = dictionary.toString();
                string = string.substring(value.length());
            } else if (ch == 'l') {
                List aList = getList(string.substring(1));
                list.add(aList);
                string = string.substring(aList.toString().length());
            } else if (ch == 'i') {
                String value = getInteger(string);
                string = string.substring(value.length() + 2);
                list.add(new Long(value));
            }
            ch = string.charAt(0);
        }
        return list;
    }

    private static String parse(String string, BEncodedDictionary dictionary) {
        String key = getString(string);
        string = string.substring(string.indexOf(key) + key.length());
        char ch = string.charAt(0);
        if (Character.isDigit(ch)) {
            if (ch == '0') {
                //$NON-NLS-1$
                dictionary.put(//$NON-NLS-1$
                key, //$NON-NLS-1$
                "");
                string = string.substring(2);
            } else {
                String value = getString(string);
                string = string.substring(string.indexOf(value, string.indexOf(':')) + value.length());
                dictionary.put(key, value);
            }
        } else if (ch == 'd') {
            BEncodedDictionary aDictionary = parse(string.substring(1));
            dictionary.put(key, aDictionary);
            string = string.substring(aDictionary.toString().length());
        } else if (ch == 'l') {
            List list = getList(string.substring(1));
            dictionary.put(key, list);
            string = string.substring(list.toString().length());
        } else if (ch == 'i') {
            String value = getInteger(string);
            string = string.substring(value.length() + 2);
            dictionary.put(key, new Long(value));
        }
        return string;
    }

    private static BEncodedDictionary parse(String string) {
        BEncodedDictionary dictionary = new BEncodedDictionary();
        while (string.charAt(0) != 'e') {
            string = parse(string, dictionary);
        }
        return dictionary;
    }

    public static BEncodedDictionary bDecode(String string) {
        if (string.charAt(0) != 'd') {
            //$NON-NLS-1$
            throw new IllegalArgumentException("The string must begin with a dictionary");
        }
        return parse(string.substring(1));
    }

    public static BEncodedDictionary bDecode(InputStream inputStream) throws IOException {
        return bDecode(read(new BufferedReader(new InputStreamReader(inputStream, //$NON-NLS-1$
        "ISO-8859-1"))));
    }

    private static String read(Reader reader) throws IOException {
        StringBuffer buffer = new StringBuffer();
        int ch = reader.read();
        synchronized (buffer) {
            while (ch != -1) {
                buffer.append((char) ch);
                ch = reader.read();
            }
        }
        reader.close();
        return buffer.toString();
    }

    /**
	 * Private constructor to prevent instantiation.
	 */
    private  Decode() {
    // do nothing
    }
}
