/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.ui.dialogs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/**
 * Error dialog to show when an {@link ContainerConnectException} is thrown.
 */
public class IDCreateErrorDialog extends ErrorDialog {

    public  IDCreateErrorDialog(Shell parentShell, String targetID, IDCreateException createException) {
        super(//$NON-NLS-1$
        parentShell, //$NON-NLS-1$
        "ID Create Error", NLS.bind("Could not create ID with {0}", targetID), createException.getStatus(), IStatus.ERROR);
    }
}
