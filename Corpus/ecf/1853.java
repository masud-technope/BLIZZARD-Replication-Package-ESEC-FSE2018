/*******************************************************************************
 * Copyright (c) 2007 Remy Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.presence.bot.kosmos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osgi.util.NLS;

class JavadocAnalyzer {

    private final Map javadocs = new HashMap();

     JavadocAnalyzer() {
        try {
            initialize();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        } catch (RuntimeException e) {
            e.printStackTrace(System.err);
            System.exit(0);
        }
    }

    private void initialize() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(JavadocAnalyzer.class.getResourceAsStream("javadoc.txt")));
        String input = reader.readLine();
        while (input != null) {
            String className = input.substring(input.lastIndexOf('.') + 1);
            Object o = javadocs.get(className);
            Javadoc doc = new Javadoc(javadocs, input);
            if (o != null) {
                if (o instanceof Javadoc) {
                    Javadoc[] docs = new Javadoc[2];
                    docs[0] = (Javadoc) o;
                    docs[1] = doc;
                    javadocs.put(className, docs);
                } else {
                    Javadoc[] docs = (Javadoc[]) o;
                    Javadoc[] copy = new Javadoc[docs.length + 1];
                    System.arraycopy(docs, 0, copy, 0, docs.length);
                    copy[docs.length] = doc;
                    javadocs.put(className, copy);
                }
            } else {
                javadocs.put(className, doc);
            }
            javadocs.put(input, doc);
            input = reader.readLine();
        }
        reader.close();
    }

    String getJavadocs(String className) {
        Object docs = javadocs.get(className);
        if (docs == null) {
            return NLS.bind(CustomMessages.getString(CustomMessages.Javadoc_NotFound), className);
        } else if (docs instanceof Javadoc) {
            return ((Javadoc) docs).getDefault();
        } else {
            Javadoc[] array = (Javadoc[]) docs;
            //$NON-NLS-1$
            String reply = "";
            for (int i = 0; i < array.length; i++) {
                reply += //$NON-NLS-1$
                array[i].getDefault() + //$NON-NLS-1$
                " ";
            }
            reply = reply.substring(0, reply.length() - 1);
            return reply;
        }
    }

    String getJavadocs(String className, String field) {
        Object docs = javadocs.get(className);
        if (docs == null) {
            return NLS.bind(CustomMessages.getString(CustomMessages.Javadoc_NotFound), className);
        } else if (docs instanceof Javadoc) {
            return ((Javadoc) docs).getField(field);
        } else {
            Javadoc[] array = (Javadoc[]) docs;
            //$NON-NLS-1$
            String reply = "";
            for (int i = 0; i < array.length; i++) {
                reply += //$NON-NLS-1$
                array[i].getField(field) + //$NON-NLS-1$
                " ";
            }
            reply = reply.substring(0, reply.length() - 1);
            return reply;
        }
    }

    String getJavadocs(String className, String methodName, String[] parameters) {
        Object docs = javadocs.get(className);
        if (docs == null) {
            return NLS.bind(CustomMessages.getString(CustomMessages.Javadoc_NotFound), className);
        } else if (docs instanceof Javadoc) {
            String javadocs = ((Javadoc) docs).getMethod(methodName, parameters);
            if (javadocs == null) {
                return CustomMessages.getString(CustomMessages.Javadoc_ResultsUnknown);
            } else {
                return javadocs;
            }
        } else {
            Javadoc[] array = (Javadoc[]) docs;
            //$NON-NLS-1$
            String reply = "";
            for (int i = 0; i < array.length; i++) {
                String ret = array[i].getMethod(methodName, parameters);
                if (ret != null) {
                    //$NON-NLS-1$
                    reply = //$NON-NLS-1$
                    reply + ret + " ";
                }
            }
            reply = reply.substring(0, reply.length() - 1);
            return reply;
        }
    }
}
