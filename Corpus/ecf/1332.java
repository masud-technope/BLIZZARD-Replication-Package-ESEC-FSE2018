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

/**
 * A class to represent the reason for a failure.  See {@link ICallSession#getErrorDetails()}.
 */
public class CallSessionFailureReason {

    protected int code = -1;

    protected String reason;

    public  CallSessionFailureReason(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    public  CallSessionFailureReason(int code) {
        this(code, String.valueOf(code));
    }

    public String getReason() {
        return reason;
    }

    public int getCode() {
        return code;
    }

    public String toString() {
        //$NON-NLS-1$
        StringBuffer buffer = new StringBuffer("CallSessionFailureReason[");
        //$NON-NLS-1$
        buffer.append("code=").append(code);
        //$NON-NLS-1$ //$NON-NLS-2$
        buffer.append(";reason=").append(reason).append("]");
        return buffer.toString();
    }
}
