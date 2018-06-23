/*******************************************************************************
 * Copyright (c) 2005, 2006 Erkki Lindpere and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Erkki Lindpere - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.bulletinboard;

/**
 * The exception thrown when a write is attempted on a read-only attribute of
 * any Bulletin Board object.
 * 
 * @author Erkki
 */
public class IllegalWriteException extends BBException {

    private static final long serialVersionUID = -5318682594191389121L;

    public  IllegalWriteException() {
        super();
    }

    public  IllegalWriteException(String message) {
        super(message);
    }

    public  IllegalWriteException(String message, Throwable cause) {
        super(message, cause);
    }

    public  IllegalWriteException(Throwable cause) {
        super(cause);
    }
}
