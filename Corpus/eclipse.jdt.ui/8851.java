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
package org.eclipse.jdt.internal.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;

public class JavaUIException extends CoreException {

    private static final long serialVersionUID = 1L;

    public  JavaUIException(IStatus status) {
        super(status);
    }
}
