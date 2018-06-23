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
package org.eclipse.jdt.internal.corext.template.java;

/**
 * The context type for templates inside SWT code.
 * The same class is used for several context types:
 * <dl>
 * <li>templates for all Java code locations</li>
 * <li>templates for member locations</li>
 * <li>templates for statement locations</li>
 * </dl>
 * @since 3.4
 */
public class SWTContextType extends AbstractJavaContextType {

    /**
	 * The context type id for templates working on all Java code locations in SWT projects
	 */
    //$NON-NLS-1$
    public static final String ID_ALL = "swt";

    /**
	 * The context type id for templates working on member locations in SWT projects
	 */
    //$NON-NLS-1$
    public static final String ID_MEMBERS = "swt-members";

    /**
	 * The context type id for templates working on statement locations in SWT projects
	 */
    //$NON-NLS-1$
    public static final String ID_STATEMENTS = "swt-statements";

    @Override
    protected void initializeContext(JavaContext context) {
        if (// a specific context must also allow the templates that work everywhere
        !getId().equals(SWTContextType.ID_ALL)) {
            context.addCompatibleContextType(SWTContextType.ID_ALL);
        }
    }
}
