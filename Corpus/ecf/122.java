/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.remoteservice.generic;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.tests.remoteservice.AbstractConcatHostApplication;
import org.eclipse.ecf.tests.remoteservice.Activator;

public class GenericConcatHostApplication extends AbstractConcatHostApplication {

    protected IContainer createContainer() throws ContainerCreateException {
        return Activator.getDefault().getContainerManager().getContainerFactory().createContainer(getContainerType(), IDFactory.getDefault().createStringID(Generic.HOST_CONTAINER_ENDPOINT_ID));
    }

    public String getContainerType() {
        return Generic.HOST_CONTAINER_TYPE;
    }
}
