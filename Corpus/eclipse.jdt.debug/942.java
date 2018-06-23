/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.launching.support;

import java.util.Enumeration;

/**
 * Used to discover the boot path, extension directories, and endorsed
 * directories for a Java VM.
 */
public class LibraryDetector {

    /**
	 * Prints system properties to standard out.
	 * <ul>
	 * <li>java.version</li>
	 * <li>sun.boot.class.path</li>
	 * <li>java.ext.dirs</li>
	 * <li>java.endorsed.dirs</li>
	 * </ul>
	 * 
	 * @param args the command line arguments
	 */
    public static void main(String[] args) {
        // if we are running raw j9
        if (//$NON-NLS-1$ //$NON-NLS-2$
        "j9".equalsIgnoreCase(System.getProperty("java.vm.name"))) {
            // Map class lib versions onto things that the launch infrastructure understands.  J9 
            // behaves like 1.4 with-respect-to launch/debug
            //$NON-NLS-1$
            String configuration = System.getProperty("com.ibm.oti.configuration");
            if (//$NON-NLS-1$
            "found10".equals(configuration))
                //$NON-NLS-1$
                System.out.print(//$NON-NLS-1$
                "1.4");
            else if (//$NON-NLS-1$
            "found11".equals(configuration))
                //$NON-NLS-1$
                System.out.print(//$NON-NLS-1$
                "1.4");
            else
                //$NON-NLS-1$
                System.out.print(//$NON-NLS-1$
                System.getProperty("java.version"));
            //$NON-NLS-1$
            System.out.print("|");
            //$NON-NLS-1$
            System.out.print(System.getProperty("com.ibm.oti.system.class.path"));
        } else {
            //$NON-NLS-1$
            System.out.print(System.getProperty("java.version"));
            //$NON-NLS-1$
            System.out.print("|");
            // get the boot class path - the property may vary for different vendors
            Enumeration keys = System.getProperties().keys();
            boolean found = false;
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                if (//$NON-NLS-1$
                key.endsWith(".boot.class.path")) {
                    found = true;
                    System.out.print(System.getProperty(key));
                    break;
                }
            }
            if (!found) {
                // old behavior
                //$NON-NLS-1$
                System.out.print(//$NON-NLS-1$
                System.getProperty("sun.boot.class.path"));
            }
        }
        //$NON-NLS-1$
        System.out.print("|");
        //$NON-NLS-1$
        System.out.print(System.getProperty("java.ext.dirs"));
        //$NON-NLS-1$
        System.out.print("|");
        //$NON-NLS-1$
        System.out.print(System.getProperty("java.endorsed.dirs"));
    }
}
