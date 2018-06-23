/*******************************************************************************
 * Copyright (c) 2005, 2006 Erkki Lindpere and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Erkki Lindpere - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.bulletinboard.commons.util;

/**
 * Various helper methods for manipulating Strings.
 * 
 * @author Erkki
 */
public class StringUtil {

    /**
	 * Tests whether the String parameter is null or equals ""
	 * 
	 * @param str
	 * @return true
	 */
    public static boolean isEmptyStr(String str) {
        return (str == null || str.equals(""));
    }

    /**
	 * {@link #isEmptyStr(String)}
	 * 
	 * @param str
	 * @return true
	 */
    public static boolean notEmptyStr(String str) {
        return !isEmptyStr(str);
    }

    /**
	 * @param stringArray
	 * @return a string that is the result of the array concatenation
	 */
    public static String concat(String stringArray[]) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < stringArray.length; i++) {
            buf.append(stringArray[i]);
        }
        return buf.toString();
    }

    /**
	 * @param stringArray
	 * @param separator
	 * @return the concatenation result
	 */
    public static String concat(String stringArray[], String separator) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < stringArray.length; i++) {
            if (i > 0) {
                buf.append(separator);
            }
            buf.append(stringArray[i]);
        }
        return buf.toString();
    }

    /**
	 * Simply removes HTML or XML tags from the string.
	 * 
	 * @param htmlString
	 * @return a new string with tags removed.
	 */
    public static String simpleStripHTML(String htmlString) {
        String str = htmlString.replaceAll("<.*?>", "");
        return str;
    }

    /**
	 * Removes HTML or XML tags and trims the resulting string.
	 * 
	 * @param htmlString
	 * @return a new trimmed string with the tags removed.
	 */
    public static String stripHTMLTrim(final String htmlString) {
        String str = htmlString.replaceAll("<.*?>", "");
        return str.trim();
    }

    /**
	 * Strips HTML tags from strings. Trims lines. Removes double spaces.
	 * 
	 * @param htmlString
	 * @return stripped, trimmed-by-line String
	 */
    public static String stripHTMLFullTrim(final String htmlString) {
        String str = stripHTMLTrim(htmlString);
        StringBuffer lines = new StringBuffer();
        int j = 0;
        for (int i = str.indexOf('\n'); i > -1; i = str.indexOf('\n', j)) {
            lines.append(str.substring(j, i).trim() + '\n');
            j = i + 1;
        }
        lines.append(str.substring(j).trim());
        str = lines.toString();
        // replacing double newlines and spaces
        while (str.indexOf("\n\n") > -1) {
            str = str.replaceAll("\n\n", "\n");
        }
        while (str.indexOf("  ") > -1) {
            str = str.replaceAll("  ", " ");
        }
        return str;
    }
}
