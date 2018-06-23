/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.launching.support;

/**
 * Evaluates system properties passed as program arguments for pre 1.4 VMs.
 * 
 * @since 3.2
 */
public class LegacySystemProperties {

    public static void main(String[] args) {
        StringBuffer buffer = new StringBuffer();
        //$NON-NLS-1$
        buffer.append("<systemProperties>\n");
        for (int i = 0; i < args.length; i++) {
            String name = args[i];
            String value = System.getProperty(name);
            if (value != null) {
                //$NON-NLS-1$
                buffer.append(//$NON-NLS-1$
                "<property ");
                //$NON-NLS-1$
                buffer.append(//$NON-NLS-1$
                "\n\tname= \"");
                buffer.append(name);
                //$NON-NLS-1$
                buffer.append(//$NON-NLS-1$
                "\"\n\tvalue= \"");
                buffer.append(value);
                //$NON-NLS-1$
                buffer.append(//$NON-NLS-1$
                "\"/>\n");
            }
        }
        //$NON-NLS-1$
        buffer.append("</systemProperties>");
        System.out.print(buffer.toString());
    }
}
