package org.eclipse.ecf.tests.osgi.services.remoteserviceadmin;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.DiscoveredEndpointDescriptionFactory;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.ServiceInfoFactory;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription;

public class EndpointDescriptionFactoryTest extends AbstractMetadataFactoryTest {

    protected void setUp() throws Exception {
        super.setUp();
        discoveryAdvertiser = getDiscoveryAdvertiser();
        Assert.isNotNull(discoveryAdvertiser);
        serviceInfoFactory = new ServiceInfoFactory();
        Assert.isNotNull(serviceInfoFactory);
        discoveryLocator = getDiscoveryLocator();
        Assert.isNotNull(discoveryLocator);
        endpointDescriptionFactory = new DiscoveredEndpointDescriptionFactory();
        Assert.isNotNull(endpointDescriptionFactory);
    }

    public void testCreateRequiredEndpointDescriptionFromServiceInfo() throws Exception {
        EndpointDescription published = createRequiredEndpointDescription();
        assertNotNull(published);
        IServiceInfo serviceInfo = createServiceInfoForDiscovery(published);
        assertNotNull(serviceInfo);
        org.osgi.service.remoteserviceadmin.EndpointDescription received = createEndpointDescriptionFromDiscovery(serviceInfo);
        assertNotNull(received);
        assertTrue(published.equals(received));
    }

    public void testCreateFullEndpointDescriptionFromServiceInfo() throws Exception {
        EndpointDescription published = createFullEndpointDescription();
        assertNotNull(published);
        IServiceInfo serviceInfo = createServiceInfoForDiscovery(published);
        assertNotNull(serviceInfo);
        org.osgi.service.remoteserviceadmin.EndpointDescription received = createEndpointDescriptionFromDiscovery(serviceInfo);
        assertNotNull(received);
        assertTrue(published.equals(received));
    }
}
