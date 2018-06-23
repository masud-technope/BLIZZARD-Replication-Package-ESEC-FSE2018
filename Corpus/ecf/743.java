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
package org.eclipse.ecf.ui;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;

/**
 * Utility class to hold {@link ContainerTypeDescription} and associated
 * {@link IContainer} resulting from container configuration.
 */
public class ContainerConfigurationResult {

    protected IContainer container;

    protected ContainerTypeDescription containerTypeDescription;

    public  ContainerConfigurationResult(ContainerTypeDescription containerTypeDescription, IContainer container) {
        Assert.isNotNull(containerTypeDescription);
        Assert.isNotNull(container);
        this.containerTypeDescription = containerTypeDescription;
        this.container = container;
    }

    public  ContainerConfigurationResult(String containerType, IContainer container) {
        this(ContainerFactory.getDefault().getDescriptionByName(containerType), container);
    }

    public IContainer getContainer() {
        return this.container;
    }

    public ContainerTypeDescription getContainerTypeDescription() {
        return this.containerTypeDescription;
    }

    public String toString() {
        //$NON-NLS-1$
        StringBuffer buf = new StringBuffer("ContainerConfigurationResult[");
        //$NON-NLS-1$
        buf.append(containerTypeDescription).append(";");
        //$NON-NLS-1$
        buf.append(container).append("]");
        return buf.toString();
    }
}
