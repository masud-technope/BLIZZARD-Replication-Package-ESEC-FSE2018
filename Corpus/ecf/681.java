/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.storage;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.util.ECFException;

/**
 *
 */
public class IDStoreException extends ECFException {

    private static final long serialVersionUID = 3886247422255119017L;

    public  IDStoreException() {
    }

    /**
	 * @param status
	 */
    public  IDStoreException(IStatus status) {
        super(status);
    }

    /**
	 * @param message
	 * @param cause
	 */
    public  IDStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
	 * @param message
	 */
    public  IDStoreException(String message) {
        super(message);
    }

    /**
	 * @param cause
	 */
    public  IDStoreException(Throwable cause) {
        super(cause);
    }
}
