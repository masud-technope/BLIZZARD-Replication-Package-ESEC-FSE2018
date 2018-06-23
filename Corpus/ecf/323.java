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

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.tests.remoteservice.AbstractConcatConsumerTestCase;

public class GenericConcatConsumerTest extends AbstractConcatConsumerTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        IContainer container = createContainer();
        rsContainer = createRemoteServiceContainer(container);
        targetID = createID(container, Generic.HOST_CONTAINER_ENDPOINT_ID);
    }

    protected String getContainerType() {
        return Generic.CONSUMER_CONTAINER_TYPE;
    }
}
