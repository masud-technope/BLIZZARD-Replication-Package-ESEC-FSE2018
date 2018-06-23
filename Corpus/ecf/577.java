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

import java.util.ArrayList;

/**
 * <p>
 * The StringUtils class provides static methods that helps make string
 * manipulation easy. The primary functionality it is meant to provide is the
 * ability to split a string into a string array based on a given delimiter.
 * This functionality is meant to take the place of the split(String) and
 * split(String, int) method that was introduced in J2SE-1.4. Please note,
 * however, that the splitting performed by this class simply splits the string
 * based on the delimiter and does not perform any regular expression matching
 * like the split methods provided in J2SE-1.4.
 * </p>
 * 
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still
 * under development and expected to change significantly before reaching
 * stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 * </p>
 */
public final class StringUtils {

    public static final String[] splitOnSpace(String string) {
        int index = string.indexOf(' ');
        if (index == -1) {
            return new String[] { string };
        }
        ArrayList split = new ArrayList();
        while (index != -1) {
            split.add(string.substring(0, index));
            string = string.substring(index + 1);
            index = string.indexOf(' ');
        }
        if (//$NON-NLS-1$
        !string.equals("")) {
            split.add(string);
        }
        return (String[]) split.toArray(new String[split.size()]);
    }

    public static final String[] split(String string, char character) {
        int index = string.indexOf(character);
        if (index == -1) {
            return new String[] { string };
        }
        ArrayList split = new ArrayList();
        while (index != -1) {
            split.add(string.substring(0, index));
            string = string.substring(index + 1);
            index = string.indexOf(character);
        }
        if (//$NON-NLS-1$
        !string.equals("")) {
            split.add(string);
        }
        return (String[]) split.toArray(new String[split.size()]);
    }

    public static final String[] split(String string, String delimiter) {
        int index = string.indexOf(delimiter);
        if (index == -1) {
            return new String[] { string };
        }
        int length = delimiter.length();
        ArrayList split = new ArrayList();
        while (index != -1) {
            split.add(string.substring(0, index));
            string = string.substring(index + length);
            index = string.indexOf(delimiter);
        }
        if (//$NON-NLS-1$
        !string.equals("")) {
            split.add(string);
        }
        return (String[]) split.toArray(new String[split.size()]);
    }

    public static final String[] split(String string, String delimiter, int limit) {
        int index = string.indexOf(delimiter);
        if (index == -1) {
            return new String[] { string };
        }
        int count = 0;
        int length = delimiter.length();
        ArrayList split = new ArrayList(limit);
        while (index != -1 && count < limit - 1) {
            split.add(string.substring(0, index));
            string = string.substring(index + length);
            index = string.indexOf(delimiter);
            count++;
        }
        if (//$NON-NLS-1$
        !string.equals("")) {
            split.add(string);
        }
        return (String[]) split.toArray(new String[split.size()]);
    }

    public static final String splitSubstring(String string, String delimiter, int pos) {
        int index = string.indexOf(delimiter);
        if (index == -1) {
            return string;
        }
        int count = 0;
        int length = delimiter.length();
        while (count < pos) {
            string = string.substring(index + length);
            index = string.indexOf(delimiter);
            count++;
        }
        return index == -1 ? string : string.substring(0, index);
    }

    public static final String xmlDecode(String string) {
        if (//$NON-NLS-1$
        string.equals("")) {
            return string;
        }
        //$NON-NLS-1$
        int index = string.indexOf("&amp;");
        while (index != -1) {
            string = string.substring(0, index) + '&' + string.substring(index + 5);
            //$NON-NLS-1$
            index = string.indexOf("&amp;", index + 1);
        }
        //$NON-NLS-1$
        index = string.indexOf("&quot;");
        while (index != -1) {
            string = string.substring(0, index) + '"' + string.substring(index + 6);
            //$NON-NLS-1$
            index = string.indexOf("&quot;", index + 1);
        }
        //$NON-NLS-1$
        index = string.indexOf("&apos;");
        while (index != -1) {
            string = string.substring(0, index) + '\'' + string.substring(index + 6);
            //$NON-NLS-1$
            index = string.indexOf("&apos;", index + 1);
        }
        //$NON-NLS-1$
        index = string.indexOf("&lt;");
        while (index != -1) {
            string = string.substring(0, index) + '<' + string.substring(index + 4);
            //$NON-NLS-1$
            index = string.indexOf("&lt;", index + 1);
        }
        //$NON-NLS-1$
        index = string.indexOf("&gt;");
        while (index != -1) {
            string = string.substring(0, index) + '>' + string.substring(index + 4);
            //$NON-NLS-1$
            index = string.indexOf("&gt;", index + 1);
        }
        return string;
    }

    public static final String xmlEncode(String string) {
        if (//$NON-NLS-1$
        string.equals("")) {
            return string;
        }
        int index = string.indexOf('&');
        while (index != -1) {
            string = //$NON-NLS-1$
            string.substring(0, index) + "&amp;" + string.substring(index + 1);
            index = string.indexOf('&', index + 1);
        }
        index = string.indexOf('"');
        while (index != -1) {
            string = //$NON-NLS-1$
            string.substring(0, index) + "&quot;" + string.substring(index + 1);
            index = string.indexOf('"', index + 1);
        }
        index = string.indexOf('\'');
        while (index != -1) {
            string = //$NON-NLS-1$
            string.substring(0, index) + "&apos;" + string.substring(index + 1);
            index = string.indexOf('\'', index + 1);
        }
        index = string.indexOf('<');
        while (index != -1) {
            string = //$NON-NLS-1$
            string.substring(0, index) + "&lt;" + string.substring(index + 1);
            index = string.indexOf('<', index + 1);
        }
        index = string.indexOf('>');
        while (index != -1) {
            string = //$NON-NLS-1$
            string.substring(0, index) + "&gt;" + string.substring(index + 1);
            index = string.indexOf('>', index + 1);
        }
        return string;
    }
}
