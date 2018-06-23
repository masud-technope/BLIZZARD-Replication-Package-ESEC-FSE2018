/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.mylyn.ui;

import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.start.IECFStart;

public class NoopECFStart implements IECFStart {

    public IStatus run(IProgressMonitor monitor) {
        return Status.OK_STATUS;
    }
}
