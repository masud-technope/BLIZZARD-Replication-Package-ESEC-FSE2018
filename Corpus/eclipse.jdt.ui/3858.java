/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.core.manipulation;

import org.eclipse.osgi.util.NLS;

public class JavaManipulationMessages extends NLS {

    //$NON-NLS-1$
    private static final String BUNDLE_NAME = "org.eclipse.jdt.internal.core.manipulation.JavaManipulationMessages";

    private  JavaManipulationMessages() {
    }

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, JavaManipulationMessages.class);
    }

    public static String JavaManipulationMessages_internalError;

    public static String UndoCompilationUnitChange_no_file;
}
