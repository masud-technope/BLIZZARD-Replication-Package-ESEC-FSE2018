/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.util.ECFException;

/**
 * Exception thrown during container creation
 * 
 * @see ContainerFactory#createContainer(ContainerTypeDescription, Object[])
 */
public class ContainerCreateException extends ECFException {

    private static final long serialVersionUID = -6979687717421003065L;

    public  ContainerCreateException(IStatus status) {
        super(status);
    }

    public  ContainerCreateException() {
        super();
    }

    public  ContainerCreateException(String message) {
        super(message);
    }

    public  ContainerCreateException(Throwable cause) {
        super(cause);
    }

    public  ContainerCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
