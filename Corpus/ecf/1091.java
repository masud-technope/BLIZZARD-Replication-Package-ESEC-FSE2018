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
package org.eclipse.ecf.internal.provider.vbulletin.container;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.provider.IContainerInstantiator;

public class ContainerInstantiator implements IContainerInstantiator {

    public IContainer createInstance(ContainerTypeDescription description, Object[] args) throws ContainerCreateException {
        ID guid;
        try {
            guid = IDFactory.getDefault().createGUID();
        } catch (IDCreateException e) {
            throw new ContainerCreateException("Exception creating ID", e);
        }
        return new VBContainer(guid);
    }

    public String[] getSupportedAdapterTypes(ContainerTypeDescription description) {
        return new String[] { "org.eclipse.ecf.bulletinboard.IBulletinBoardContainerAdapter" };
    }

    public Class[][] getSupportedParameterTypes(ContainerTypeDescription description) {
        return new Class[][] {};
    }
}
