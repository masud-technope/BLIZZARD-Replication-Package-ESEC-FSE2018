/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.sharedobject;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.util.ECFException;

/**
 * Exception thrown when creating connector between shared object
 * 
 * @see ISharedObjectManager#connectSharedObjects(org.eclipse.ecf.core.identity.ID,
 *      org.eclipse.ecf.core.identity.ID[])
 */
public class SharedObjectConnectException extends ECFException {

    private static final long serialVersionUID = 3256440287659570228L;

    public  SharedObjectConnectException() {
        super();
    }

    public  SharedObjectConnectException(IStatus status) {
        super(status);
    }

    public  SharedObjectConnectException(String arg0) {
        super(arg0);
    }

    public  SharedObjectConnectException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public  SharedObjectConnectException(Throwable cause) {
        super(cause);
    }
}
