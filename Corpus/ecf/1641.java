/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.osgi.services.remoteserviceadmin;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.ServiceInfoFactory;

public abstract class ServiceInfoFactoryTest extends AbstractMetadataFactoryTest {

    protected void setUp() throws Exception {
        super.setUp();
        discoveryAdvertiser = getDiscoveryAdvertiser();
        Assert.isNotNull(discoveryAdvertiser);
        serviceInfoFactory = new ServiceInfoFactory();
    }

    public void testCreateServiceInfoFromMinimalEndpointDescription() throws Exception {
        IServiceInfo serviceInfo = createServiceInfoForDiscovery(createRequiredEndpointDescription());
        assertNotNull(serviceInfo);
    }

    public void testCreateServiceInfoFromFullEndpointDescription() throws Exception {
        IServiceInfo serviceInfo = createServiceInfoForDiscovery(createFullEndpointDescription());
        assertNotNull(serviceInfo);
    }

    public void testCreateBadOSGiEndpointDescription() throws Exception {
        try {
            createBadOSGiEndpointDescrption();
            fail();
        } catch (Exception e) {
        }
    }
}
