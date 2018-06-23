/*******************************************************************************
 * Copyright (c) 2009 EclipseSource, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: EclilpseSource, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.filetransfer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.util.ECFException;

/**
 * Exception thrown upon browse problem
 * 
 * @since 3.0
 */
public class BrowseFileTransferException extends ECFException {

    private static final long serialVersionUID = 3486429797589398022L;

    private int errorCode = -1;

    public  BrowseFileTransferException(IStatus status) {
        super(status);
    }

    public  BrowseFileTransferException() {
    // null constructor
    }

    public  BrowseFileTransferException(int errorCode) {
        this();
        this.errorCode = errorCode;
    }

    public  BrowseFileTransferException(String message) {
        super(message);
    }

    public  BrowseFileTransferException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public  BrowseFileTransferException(Throwable cause) {
        super(cause);
    }

    public  BrowseFileTransferException(Throwable cause, int errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public  BrowseFileTransferException(String message, Throwable cause) {
        super(message, cause);
    }

    public  BrowseFileTransferException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
