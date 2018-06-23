/******************************************************************************* 
 * Copyright (c) 2010-2011 Naumen. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Pavel Samolisov - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.tests.remoteservice.rpc;

import java.net.URI;
import java.net.URL;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.remoteservice.rpc.client.RpcClientContainer;

public class RpcContainerTest extends AbstractRpcTestCase {

    protected void tearDown() throws Exception {
        getContainerManager().removeAllContainers();
    }

    public void testCreateContainer() throws Exception {
        IContainer container = createRpcContainer(RpcConstants.TEST_ECHO_TARGET);
        assertNotNull(container);
        assertTrue(container instanceof RpcClientContainer);
    }

    public void testCreateContainer1() throws Exception {
        IContainer container = createRpcContainer(RpcConstants.TEST_ECHO_TARGET);
        assertNotNull(container);
        assertTrue(container instanceof RpcClientContainer);
    }

    public void testCreateContainer2() throws Exception {
        ContainerTypeDescription description = getContainerFactory().getDescriptionByName(RpcConstants.RPC_CONTAINER_TYPE);
        IContainer container = getContainerFactory().createContainer(description, new Object[] { new URL(RpcConstants.TEST_ECHO_TARGET) });
        assertNotNull(container);
        assertTrue(container instanceof RpcClientContainer);
    }

    public void testCreateContainer3() throws Exception {
        ContainerTypeDescription description = getContainerFactory().getDescriptionByName(RpcConstants.RPC_CONTAINER_TYPE);
        IContainer container = getContainerFactory().createContainer(description, new Object[] { new URI(RpcConstants.TEST_ECHO_TARGET) });
        assertNotNull(container);
        assertTrue(container instanceof RpcClientContainer);
    }
}
