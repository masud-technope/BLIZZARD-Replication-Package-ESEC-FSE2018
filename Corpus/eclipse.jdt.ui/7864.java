/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Sebastian Davids: sdavids@gmx.de - see bug 25376
 *******************************************************************************/
package org.eclipse.jdt.internal.corext.template.java;

/**
 * The context type for templates inside Java code.
 * The same class is used for several context types:
 * <dl>
 * <li>templates for all Java code locations</li>
 * <li>templates for member locations</li>
 * <li>templates for statement locations</li>
 * </dl>
 */
public class JavaContextType extends AbstractJavaContextType {

    /**
	 * The context type id for templates working on all Java code locations
	 */
    //$NON-NLS-1$
    public static final String ID_ALL = "java";

    /**
	 * The context type id for templates working on member locations
	 */
    //$NON-NLS-1$
    public static final String ID_MEMBERS = "java-members";

    /**
	 * The context type id for templates working on statement locations
	 */
    //$NON-NLS-1$
    public static final String ID_STATEMENTS = "java-statements";

    @Override
    protected void initializeContext(JavaContext context) {
        if (// a specific context must also allow the templates that work everywhere
        !getId().equals(JavaContextType.ID_ALL)) {
            context.addCompatibleContextType(JavaContextType.ID_ALL);
        }
    }
}
