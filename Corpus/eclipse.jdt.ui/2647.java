/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ltk.internal.core.refactoring;

import org.eclipse.ltk.core.refactoring.RefactoringSessionDescriptor;

/**
 * Interface for constants related to refactoring serialization.
 *
 * @since 3.2
 */
public interface IRefactoringSerializationConstants {

    /** The comment attribute */
    //$NON-NLS-1$
    public static final String ATTRIBUTE_COMMENT = "comment";

    /** The description attribute */
    //$NON-NLS-1$
    public static final String ATTRIBUTE_DESCRIPTION = "description";

    /** The flags attribute */
    //$NON-NLS-1$
    public static final String ATTRIBUTE_FLAGS = "flags";

    /** The id attribute */
    //$NON-NLS-1$
    public static final String ATTRIBUTE_ID = "id";

    /** The project attribute */
    //$NON-NLS-1$
    public static final String ATTRIBUTE_PROJECT = "project";

    /** The time stamp attribute */
    //$NON-NLS-1$
    public static final String ATTRIBUTE_STAMP = "stamp";

    /** The version attribute */
    //$NON-NLS-1$
    public static final String ATTRIBUTE_VERSION = "version";

    /** The current version tag */
    public static final String CURRENT_VERSION = RefactoringSessionDescriptor.VERSION_1_0;

    /** The refactoring element */
    //$NON-NLS-1$
    public static final String ELEMENT_REFACTORING = "refactoring";

    /** The session element */
    //$NON-NLS-1$
    public static final String ELEMENT_SESSION = "session";

    /** The output encoding */
    //$NON-NLS-1$
    public static final String OUTPUT_ENCODING = "utf-8";

    /** The indent flag */
    //$NON-NLS-1$
    public static final String OUTPUT_INDENT = "yes";

    /** The output method */
    //$NON-NLS-1$
    public static final String OUTPUT_METHOD = "xml";
}
