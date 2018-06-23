/*******************************************************************************
 * Copyright (c) 2011 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.osgi.services.remoteserviceadmin;

import org.eclipse.ecf.core.ContainerTypeDescription;

/**
 * @since 2.0
 */
public class SelectContainerException extends Exception {

    private static final long serialVersionUID = -5507248105370677422L;

    private ContainerTypeDescription containerTypeDescription;

    public  SelectContainerException(String message, Throwable cause, ContainerTypeDescription containerTypeDescription) {
        super(message, cause);
        this.containerTypeDescription = containerTypeDescription;
    }

    public ContainerTypeDescription getContainerTypeDescription() {
        return containerTypeDescription;
    }
}
