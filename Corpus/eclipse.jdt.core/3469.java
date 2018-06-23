/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.util;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class GenericXMLWriter extends PrintWriter {

    /* constants */
    //$NON-NLS-1$
    private static final String XML_VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    private static void appendEscapedChar(StringBuffer buffer, char c) {
        String replacement = getReplacement(c);
        if (replacement != null) {
            buffer.append('&');
            buffer.append(replacement);
            buffer.append(';');
        } else {
            buffer.append(c);
        }
    }

    private static String getEscaped(String s) {
        StringBuffer result = new StringBuffer(s.length() + 10);
        for (int i = 0; i < s.length(); ++i) appendEscapedChar(result, s.charAt(i));
        return result.toString();
    }

    private static String getReplacement(char c) {
        // These five are defined by default for all XML documents.
        switch(c) {
            case '<':
                //$NON-NLS-1$
                return "lt";
            case '>':
                //$NON-NLS-1$
                return "gt";
            case '"':
                //$NON-NLS-1$
                return "quot";
            case '\'':
                //$NON-NLS-1$
                return "apos";
            case '&':
                //$NON-NLS-1$
                return "amp";
        }
        return null;
    }

    private String lineSeparator;

    private int tab;

    public  GenericXMLWriter(OutputStream stream, String lineSeparator, boolean printXmlVersion) {
        this(new PrintWriter(stream), lineSeparator, printXmlVersion);
    }

    public  GenericXMLWriter(Writer writer, String lineSeparator, boolean printXmlVersion) {
        super(writer);
        this.tab = 0;
        this.lineSeparator = lineSeparator;
        if (printXmlVersion) {
            print(XML_VERSION);
            print(this.lineSeparator);
        }
    }

    public void endTag(String name, boolean insertTab, boolean insertNewLine) {
        this.tab--;
        printTag('/' + name, /*no parameters*/
        null, insertTab, insertNewLine, /*don't close tag*/
        false);
    }

    /*
	 * External API
	 */
    public void printString(String string, boolean insertTab, boolean insertNewLine) {
        if (insertTab) {
            printTabulation();
        }
        print(string);
        if (insertNewLine) {
            print(this.lineSeparator);
        }
    }

    private void printTabulation() {
        for (int i = 0; i < this.tab; i++) this.print('\t');
    }

    public void printTag(String name, HashMap parameters, boolean insertTab, boolean insertNewLine, boolean closeTag) {
        if (insertTab) {
            printTabulation();
        }
        this.print('<');
        this.print(name);
        if (parameters != null) {
            int length = parameters.size();
            Map.Entry[] entries = new Map.Entry[length];
            parameters.entrySet().toArray(entries);
            Arrays.sort(entries, new Comparator() {

                public int compare(Object o1, Object o2) {
                    Map.Entry entry1 = (Map.Entry) o1;
                    Map.Entry entry2 = (Map.Entry) o2;
                    return ((String) entry1.getKey()).compareTo((String) entry2.getKey());
                }
            });
            for (int i = 0; i < length; i++) {
                this.print(' ');
                this.print(entries[i].getKey());
                //$NON-NLS-1$
                this.print(//$NON-NLS-1$
                "=\"");
                this.print(getEscaped(String.valueOf(entries[i].getValue())));
                this.print('\"');
            }
        }
        if (closeTag) {
            //$NON-NLS-1$
            this.print("/>");
        } else {
            //$NON-NLS-1$
            this.print(">");
        }
        if (insertNewLine) {
            print(this.lineSeparator);
        }
        if (parameters != null && !closeTag)
            this.tab++;
    }

    public void startTag(String name, boolean insertTab) {
        printTag(name, /*no parameters*/
        null, insertTab, /*insert new line*/
        true, /*don't close tag*/
        false);
        this.tab++;
    }
}
