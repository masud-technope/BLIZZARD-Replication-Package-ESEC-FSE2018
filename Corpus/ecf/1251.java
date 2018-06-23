/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.filetransfer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.util.ECFException;

/**
 *
 */
public class RemoteFileSystemException extends ECFException {

    private static final long serialVersionUID = -2199951600347999396L;

    /**
	 * 
	 */
    public  RemoteFileSystemException() {
        super();
    }

    /**
	 * @param status status
	 */
    public  RemoteFileSystemException(IStatus status) {
        super(status);
    }

    /**
	 * @param message message
	 * @param cause cause
	 */
    public  RemoteFileSystemException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
	 * @param message message
	 */
    public  RemoteFileSystemException(String message) {
        super(message);
    }

    /**
	 * @param cause cause
	 */
    public  RemoteFileSystemException(Throwable cause) {
        super(cause);
    }
}
