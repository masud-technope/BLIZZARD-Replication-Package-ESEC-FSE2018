/*******************************************************************************
* Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.tests.server.generic;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import org.eclipse.ecf.server.generic.IGenericServerContainerGroup;
import org.eclipse.ecf.server.generic.IGenericServerContainerGroupFactory;
import junit.framework.TestCase;

public class GenericServerContainerGroupFactoryTest extends TestCase {

    private static final String hostname = "localhost";

    private static final int port = 4000;

    private InetAddress allAddress;

    private IGenericServerContainerGroupFactory gscgFactory;

    protected void setUp() throws Exception {
        super.setUp();
        gscgFactory = Activator.getDefault().getGenericServerContainerGroupFactory();
        allAddress = new InetSocketAddress((InetAddress) null, 0).getAddress();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        gscgFactory = null;
    }

    protected IGenericServerContainerGroup createContainerGroup(InetAddress bindAddress) throws Exception {
        return gscgFactory.createContainerGroup(hostname, port, bindAddress, null);
    }

    protected IGenericServerContainerGroup createContainerGroup() throws Exception {
        return createContainerGroup(null);
    }

    protected void removeContainerGroup() throws Exception {
        gscgFactory.removeContainerGroup(hostname, port);
    }

    public void testCreateContainerGroup() throws Exception {
        IGenericServerContainerGroup containerGroup = createContainerGroup(null);
        assertNotNull(containerGroup);
        URI groupEndpoint = containerGroup.getGroupEndpoint();
        assertNotNull(groupEndpoint);
        assertTrue(groupEndpoint.getHost().equals(hostname));
        assertTrue(groupEndpoint.getPort() == port);
        removeContainerGroup();
    }

    public void testCreateContainerGroupListen() throws Exception {
        IGenericServerContainerGroup containerGroup = createContainerGroup(null);
        assertNotNull(containerGroup);
        URI groupEndpoint = containerGroup.getGroupEndpoint();
        assertNotNull(groupEndpoint);
        assertTrue(groupEndpoint.getHost().equals(hostname));
        assertTrue(groupEndpoint.getPort() == port);
        assertTrue(!containerGroup.isListening());
        containerGroup.startListening();
        assertTrue(containerGroup.isListening());
        containerGroup.stopListening();
        assertTrue(!containerGroup.isListening());
        removeContainerGroup();
    }

    public void testCreateContainerGroupWithBindAddress() throws Exception {
        IGenericServerContainerGroup containerGroup = createContainerGroup(this.allAddress);
        assertNotNull(containerGroup);
        URI groupEndpoint = containerGroup.getGroupEndpoint();
        assertNotNull(groupEndpoint);
        assertTrue(groupEndpoint.getHost().equals(hostname));
        assertTrue(groupEndpoint.getPort() == port);
        removeContainerGroup();
    }

    public void testCreateContainerGroupWithBindAddressListen() throws Exception {
        IGenericServerContainerGroup containerGroup = createContainerGroup(this.allAddress);
        assertNotNull(containerGroup);
        URI groupEndpoint = containerGroup.getGroupEndpoint();
        assertNotNull(groupEndpoint);
        assertTrue(groupEndpoint.getHost().equals(hostname));
        assertTrue(groupEndpoint.getPort() == port);
        assertTrue(!containerGroup.isListening());
        containerGroup.startListening();
        assertTrue(containerGroup.isListening());
        containerGroup.stopListening();
        assertTrue(!containerGroup.isListening());
        removeContainerGroup();
    }

    public void testGetContainerGroup() throws Exception {
        IGenericServerContainerGroup gscg = gscgFactory.getContainerGroup(hostname, port);
        assertNull(gscg);
        createContainerGroup();
        gscg = gscgFactory.getContainerGroup(hostname, port);
        assertNotNull(gscg);
        URI groupEndpoint = gscg.getGroupEndpoint();
        assertNotNull(groupEndpoint);
        assertTrue(groupEndpoint.getHost().equals(hostname));
        assertTrue(groupEndpoint.getPort() == port);
        removeContainerGroup();
    }

    public void testGetContainerGroups() throws Exception {
        IGenericServerContainerGroup[] gscgs = gscgFactory.getContainerGroups();
        assertNotNull(gscgs);
        assertTrue(gscgs.length == 0);
        createContainerGroup();
        gscgs = gscgFactory.getContainerGroups();
        assertNotNull(gscgs);
        assertTrue(gscgs.length == 1);
        URI groupEndpoint = gscgs[0].getGroupEndpoint();
        assertNotNull(groupEndpoint);
        assertTrue(groupEndpoint.getHost().equals(hostname));
        assertTrue(groupEndpoint.getPort() == port);
        removeContainerGroup();
    }
}
