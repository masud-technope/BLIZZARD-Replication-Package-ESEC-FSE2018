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
package org.eclipse.ecf.telephony.call;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.util.ECFException;

/**
 * Exception thrown by Call API calls.
 */
public class CallException extends ECFException {

    private static final long serialVersionUID = -4189098435363433141L;

    private CallSessionState callSessionState;

    public  CallException() {
    // null constructor
    }

    /**
	 * @param message
	 */
    public  CallException(String message) {
        super(message);
    }

    public  CallException(String message, CallSessionState state) {
        super(message);
        this.callSessionState = state;
    }

    /**
	 * @param cause
	 */
    public  CallException(Throwable cause) {
        super(cause);
    }

    public  CallException(Throwable cause, CallSessionState state) {
        super(cause);
        this.callSessionState = state;
    }

    /**
	 * @param message
	 * @param cause
	 */
    public  CallException(String message, Throwable cause) {
        super(message, cause);
    }

    public  CallException(String message, Throwable cause, CallSessionState state) {
        super(message, cause);
        this.callSessionState = state;
    }

    public  CallException(IStatus status) {
        super(status);
    }

    public  CallException(IStatus status, CallSessionState state) {
        super(status);
        this.callSessionState = state;
    }

    public CallSessionState getCallSessionState() {
        return this.callSessionState;
    }
}
