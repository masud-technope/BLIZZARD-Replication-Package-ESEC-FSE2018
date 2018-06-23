/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.identity;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.util.ECFRuntimeException;

public class IDCreateException extends ECFRuntimeException {

    private static final long serialVersionUID = 3258416140119323960L;

    public  IDCreateException() {
        super();
    }

    public  IDCreateException(IStatus status) {
        super(status);
    }

    public  IDCreateException(String message) {
        super(message);
    }

    public  IDCreateException(Throwable cause) {
        super(cause);
    }

    public  IDCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
