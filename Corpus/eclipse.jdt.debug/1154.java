/*******************************************************************************
 * Copyright (c) 2007, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.launching;

import java.io.File;
import org.eclipse.jdt.launching.IVMInstallType;

/**
 * A VM install created from an execution environment description file.
 * 
 * @since 3.4
 */
public class EEVMInstall extends StandardVM {

    /**
	 * Attribute key for Java version property
	 */
    //$NON-NLS-1$
    public static final String ATTR_JAVA_VERSION = "ATTR_JAVA_VERSION";

    /**
	 * Attribute key for supported execution environment by this runtime
	 */
    //$NON-NLS-1$
    public static final String ATTR_EXECUTION_ENVIRONMENT_ID = "ATTR_EXECUTION_ENVIRONMENT_ID";

    /**
	 * Attribute key for Java executable used by this VM
	 */
    //$NON-NLS-1$
    public static final String ATTR_JAVA_EXE = "ATTR_JAVA_EXE";

    /**
	 * Attribute key for VM debug arguments
	 */
    //$NON-NLS-1$
    public static final String ATTR_DEBUG_ARGS = "ATTR_DEBUG_ARGS";

    /**
	 * Path to file used to define the JRE
	 */
    //$NON-NLS-1$
    public static final String ATTR_DEFINITION_FILE = "ATTR_DEFINITION_FILE";

    /**
	 * Constructs a VM install.
	 * 
	 * @param type vm type
	 * @param id unique id
	 */
     EEVMInstall(IVMInstallType type, String id) {
        super(type, id);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.launching.StandardVM#getJavaVersion()
	 */
    @Override
    public String getJavaVersion() {
        return getAttribute(ATTR_JAVA_VERSION);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.launching.StandardVM#getJavaExecutable()
	 */
    @Override
    File getJavaExecutable() {
        String exe = getAttribute(ATTR_JAVA_EXE);
        if (exe != null) {
            return new File(exe);
        }
        return null;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.launching.StandardVM#getDebugArgs()
	 */
    @Override
    public String getDebugArgs() {
        return getAttribute(ATTR_DEBUG_ARGS);
    }
}
