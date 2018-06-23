/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.datashare.mergeable;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.util.ECFException;

public class PublishException extends ECFException {

    public  PublishException() {
        super();
    }

    public  PublishException(IStatus status) {
        super(status);
    }

    public  PublishException(String message, Throwable cause) {
        super(message, cause);
    }

    public  PublishException(String message) {
        super(message);
    }

    public  PublishException(Throwable cause) {
        super(cause);
    }

    private static final long serialVersionUID = 291096966795312256L;
}
