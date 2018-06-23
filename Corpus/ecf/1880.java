/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.sharedobject.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.util.ECFException;

public class QueueException extends ECFException {

    private static final long serialVersionUID = 3691039863709118774L;

    IQueue theQueue = null;

    public  QueueException(IStatus status) {
        super(status);
    }

    public  QueueException() {
        super();
    }

    public  QueueException(IQueue queue) {
        theQueue = queue;
    }

    public  QueueException(String message) {
        super(message);
    }

    public  QueueException(String message, Throwable cause) {
        super(message, cause);
    }

    public  QueueException(Throwable cause) {
        super(cause);
    }

    public IQueue getQueue() {
        return theQueue;
    }
}
