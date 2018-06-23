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

import java.util.Map;

class Javadoc {

    //$NON-NLS-1$
    private static final String LINK_PREFIX = "http://help.eclipse.org/stable/nftopic/org.eclipse.platform.doc.isv/reference/api/";

    //$NON-NLS-1$
    private static final String LINK_SUFFIX = ".html";

    private Map javadocs;

    private String fqn;

    private String link;

     Javadoc(Map javadocs, String fullQualifiedName) {
        this.javadocs = javadocs;
        fqn = fullQualifiedName;
        //$NON-NLS-1$ //$NON-NLS-2$
        link = LINK_PREFIX + fqn.replaceAll("\\.", "/") + LINK_SUFFIX;
    }

    String getField(String field) {
        return link + '#' + field;
    }

    String getMethod(String methodName, String[] array) {
        String ret = link + '#' + methodName + '(';
        for (int i = 0; i < array.length; i++) {
            Object match = javadocs.get(array[i]);
            if (match == null) {
                if (//$NON-NLS-1$ //$NON-NLS-2$
                array[i].equals("int") || array[i].equals("float") || array[i].equals("short") || //$NON-NLS-1$ //$NON-NLS-2$
                array[i].equals("long") || //$NON-NLS-1$
                array[i].equals(//$NON-NLS-1$
                "byte") || //$NON-NLS-1$
                array[i].equals(//$NON-NLS-1$
                "boolean") || array[i].equals("double") || //$NON-NLS-1$ //$NON-NLS-2$
                array[i].equals("char")) {
                    ret = //$NON-NLS-1$
                    ret + array[i] + //$NON-NLS-1$
                    ",%20";
                } else if (//$NON-NLS-1$
                array[i].equals(//$NON-NLS-1$
                "Object") || //$NON-NLS-1$
                array[i].equals(//$NON-NLS-1$
                "Class") || //$NON-NLS-1$
                array[i].equals(//$NON-NLS-1$
                "String")) {
                    //$NON-NLS-1$ //$NON-NLS-2$
                    ret = ret + "java.lang." + array[i] + ",%20";
                } else if (//$NON-NLS-1$ //$NON-NLS-2$
                array[i].equals("Map") || array[i].equals("List") || //$NON-NLS-1$
                array[i].equals("Set") || //$NON-NLS-1$
                array[i].equals(//$NON-NLS-1$
                "Collection")) {
                    //$NON-NLS-1$ //$NON-NLS-2$
                    ret = ret + "java.util." + array[i] + ",%20";
                } else {
                    ret = //$NON-NLS-1$
                    ret + array[i] + //$NON-NLS-1$
                    ",%20";
                }
            } else if (match instanceof Javadoc) {
                ret = //$NON-NLS-1$
                ret + ((Javadoc) match).fqn + //$NON-NLS-1$
                ",%20";
            } else {
                Javadoc[] docs = (Javadoc[]) match;
                boolean found = false;
                for (int j = 0; j < docs.length; j++) {
                    if (array[i].equals(docs[j].fqn)) {
                        ret = //$NON-NLS-1$
                        ret + array[i] + //$NON-NLS-1$
                        ",%20";
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return null;
                }
            }
        }
        if (//$NON-NLS-1$
        ret.endsWith(",%20")) {
            ret = ret.substring(0, ret.length() - 4);
        }
        return ret + ')';
    }

    String getDefault() {
        //$NON-NLS-1$
        return fqn + " - " + link;
    }

    public String toString() {
        return fqn;
    }
}
