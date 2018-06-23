/*******************************************************************************
* Copyright (c) 2009 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice.rest;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.util.ECFException;

public class RestException extends ECFException {

    private static final long serialVersionUID = -6565657473114300609L;

    private int errorCode = -1;

    private byte[] response = null;

    public  RestException(IStatus status) {
        super(status);
    }

    public  RestException() {
    // null constructor
    }

    public  RestException(int errorCode) {
        this();
        this.errorCode = errorCode;
    }

    public  RestException(String message) {
        super(message);
    }

    public  RestException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public  RestException(Throwable cause) {
        super(cause);
    }

    public  RestException(Throwable cause, int errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public  RestException(String message, Throwable cause) {
        super(message, cause);
    }

    public  RestException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public  RestException(String message, Throwable cause, int errorCode, byte[] response) {
        super(message, cause);
        this.errorCode = errorCode;
        this.response = response;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public byte[] getResponseBody() {
        return response;
    }
}
