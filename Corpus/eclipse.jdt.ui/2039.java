/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.propertiesfileeditor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;

/**
 * Helper class to convert between Java chars and the escaped form that must be used in .properties
 * files.
 * 
 * @since 3.7
 */
public class PropertiesFileEscapes {

    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    private static char toHex(int halfByte) {
        return HEX_DIGITS[(halfByte & 0xF)];
    }

    /**
	 * Returns the decimal value of the Hex digit, or -1 if the digit is not a valid Hex digit.
	 * 
	 * @param digit the Hex digit
	 * @return the decimal value of digit, or -1 if digit is not a valid Hex digit.
	 */
    private static int getHexDigitValue(char digit) {
        switch(digit) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return digit - '0';
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
                return 10 + digit - 'a';
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
                return 10 + digit - 'A';
            default:
                return -1;
        }
    }

    /**
	 * Convert a Java char to the escaped form that must be used in .properties files.
	 * 
	 * @param c the Java char
	 * @return escaped string
	 */
    public static String escape(char c) {
        return escape(c, true, true, true);
    }

    /**
	 * Convert characters in a Java string to the escaped form that must be used in .properties
	 * files.
	 * 
	 * @param s the Java string
	 * @param escapeWhitespaceChars if <code>true</code>, escape whitespace characters
	 * @param escapeBackslash if <code>true</code>, escape backslash characters
	 * @param escapeUnicodeChars if <code>true</code>, escape unicode characters
	 * @return escaped string
	 */
    public static String escape(String s, boolean escapeWhitespaceChars, boolean escapeBackslash, boolean escapeUnicodeChars) {
        StringBuffer sb = new StringBuffer(s.length());
        int length = s.length();
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            sb.append(escape(c, escapeWhitespaceChars, escapeBackslash, escapeUnicodeChars));
        }
        return sb.toString();
    }

    /**
	 * Convert a Java char to the escaped form that must be used in .properties files.
	 * 
	 * @param c the Java char
	 * @param escapeWhitespaceChars if <code>true</code>, escape whitespace characters
	 * @param escapeBackslash if <code>true</code>, escape backslash characters
	 * @param escapeUnicodeChars if <code>true</code>, escape unicode characters
	 * @return escaped string
	 */
    public static String escape(char c, boolean escapeWhitespaceChars, boolean escapeBackslash, boolean escapeUnicodeChars) {
        switch(c) {
            case '\t':
                //$NON-NLS-1$//$NON-NLS-2$
                return escapeWhitespaceChars ? "\\t" : "\t";
            case '\n':
                //$NON-NLS-1$//$NON-NLS-2$
                return escapeWhitespaceChars ? "\\n" : "\n";
            case '\f':
                //$NON-NLS-1$//$NON-NLS-2$
                return escapeWhitespaceChars ? "\\f" : "\r";
            case '\r':
                //$NON-NLS-1$//$NON-NLS-2$
                return escapeWhitespaceChars ? "\\r" : "\r";
            case '\\':
                return escapeBackslash ? "\\\\" : "\\";
            default:
                if (escapeUnicodeChars && ((c < 0x0020) || (c > 0x007e && c <= 0x00a0) || (c > 0x00ff))) {
                    return new StringBuffer().append('\\').append('u').append(toHex((c >> 12) & 0xF)).append(toHex((c >> 8) & 0xF)).append(toHex((c >> 4) & 0xF)).append(toHex(c & 0xF)).toString();
                } else
                    return String.valueOf(c);
        }
    }

    public static String unescape(String s) throws CoreException {
        boolean isValidEscapedString = true;
        if (s == null)
            return null;
        char aChar;
        int len = s.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = s.charAt(x++);
            if (aChar == '\\') {
                if (x > len - 1) {
                    return outBuffer.toString();
                }
                aChar = s.charAt(x++);
                if (aChar == 'u') {
                    int value = 0;
                    if (x > len - 4) {
                        String exceptionMessage = Messages.format(PropertiesFileEditorMessages.PropertiesFileHover_MalformedEncoding, outBuffer.toString() + s.substring(x - 2));
                        throw new CoreException(new StatusInfo(IStatus.WARNING, exceptionMessage));
                    }
                    StringBuffer buf = new StringBuffer("\\u");
                    int digit = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = s.charAt(x++);
                        digit = getHexDigitValue(aChar);
                        if (digit == -1) {
                            isValidEscapedString = false;
                            x--;
                            break;
                        }
                        value = (value << 4) + digit;
                        buf.append(aChar);
                    }
                    outBuffer.append(digit == -1 ? buf.toString() : String.valueOf((char) value));
                } else if (aChar == 't') {
                    outBuffer.append('\t');
                } else if (aChar == 'n') {
                    outBuffer.append('\n');
                } else if (aChar == 'f') {
                    outBuffer.append('\f');
                } else if (aChar == 'r') {
                    outBuffer.append('\r');
                } else {
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        if (isValidEscapedString) {
            return outBuffer.toString();
        } else {
            String exceptionMessage = Messages.format(PropertiesFileEditorMessages.PropertiesFileHover_MalformedEncoding, outBuffer.toString());
            throw new CoreException(new StatusInfo(IStatus.WARNING, exceptionMessage));
        }
    }

    public static String unescapeBackslashes(String s) {
        if (s == null)
            return null;
        char c;
        int length = s.length();
        StringBuffer outBuffer = new StringBuffer(length);
        for (int i = 0; i < length; ) {
            c = s.charAt(i++);
            if (c == '\\') {
                c = s.charAt(i++);
            }
            outBuffer.append(c);
        }
        return outBuffer.toString();
    }

    public static boolean containsInvalidEscapeSequence(String text) {
        try {
            unescape(text);
        } catch (CoreException e) {
            return true;
        }
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if (c == '\\') {
                if (i < length - 1) {
                    char nextC = text.charAt(i + 1);
                    switch(nextC) {
                        case 't':
                        case 'n':
                        case 'f':
                        case 'r':
                        case 'u':
                        case '\n':
                        case '\r':
                        case '=':
                        case ':':
                            break;
                        case '\\':
                            i++;
                            break;
                        default:
                            return true;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean containsUnescapedBackslash(String text) {
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if (c == '\\') {
                if (i < length - 1) {
                    char nextC = text.charAt(i + 1);
                    switch(nextC) {
                        case '\\':
                            i++;
                            break;
                        default:
                            return true;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean containsEscapedBackslashes(String text) {
        boolean result = false;
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if (c == '\\') {
                if (i < length - 1) {
                    char nextC = text.charAt(i + 1);
                    switch(nextC) {
                        case '\\':
                            i++;
                            result = true;
                            break;
                        default:
                            return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return result;
    }
}
