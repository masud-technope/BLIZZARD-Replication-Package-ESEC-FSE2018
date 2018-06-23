/*******************************************************************************
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.tests.osgi.services.distribution;

import java.util.Properties;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.remoteservice.Constants;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.osgi.framework.ServiceRegistration;

public abstract class AbstractRemoteServiceRegisterTest extends AbstractDistributionTest {

    protected static final int REGISTER_WAIT = 10000;

    private ServiceRegistration registration;

    protected abstract String getServerContainerTypeName();

    protected void tearDown() throws Exception {
        // Then unregister
        if (registration != null) {
            registration.unregister();
            registration = null;
        }
        Thread.sleep(REGISTER_WAIT);
        super.tearDown();
        IContainer[] containers = getContainerManager().getAllContainers();
        for (int i = 0; i < containers.length; i++) {
            containers[i].dispose();
        }
        getContainerManager().removeAllContainers();
    }

    protected void registerWaitAndUnregister(Properties props, boolean verifyRegistration) throws Exception {
        // Actually register with default service (IConcatService)
        registration = registerDefaultService(props);
        // Wait a while
        Thread.sleep(REGISTER_WAIT);
        // Verify
        if (verifyRegistration) {
            verifyRemoteServiceRegisteredWithServer();
        }
    }

    private void verifyRemoteServiceRegisteredWithServer() throws Exception {
        verifyRemoteServiceRegistered(getServerContainerAdapter(), getDefaultServiceClasses()[0]);
    }

    protected void verifyRemoteServiceRegistered(IRemoteServiceContainerAdapter adapter, String className) throws Exception {
        assertNotNull(adapter);
        IRemoteServiceReference[] refs = adapter.getRemoteServiceReferences((ID[]) null, className, null);
        assertNotNull(refs);
        assertTrue(refs.length > 0);
        String[] objectClasses = (String[]) refs[0].getProperty(Constants.OBJECTCLASS);
        assertTrue(objectClasses != null);
        assertTrue(objectClasses.length > 0);
        assertTrue(objectClasses[0].equals(className));
    }

    private IRemoteServiceContainerAdapter getServerContainerAdapter() {
        if (this.server != null)
            return (IRemoteServiceContainerAdapter) this.server.getAdapter(IRemoteServiceContainerAdapter.class);
        IContainer[] containers = getContainerManager().getAllContainers();
        String containerType = getServerContainerTypeName();
        for (int i = 0; i < containers.length; i++) {
            ContainerTypeDescription ctd = getContainerManager().getContainerTypeDescription(containers[i].getID());
            if (ctd != null && ctd.getName().equals(containerType))
                return (IRemoteServiceContainerAdapter) containers[i].getAdapter(IRemoteServiceContainerAdapter.class);
        }
        return null;
    }

    public void testRegisterOnCreatedServer() throws Exception {
        startTest("testRegisterOnCreatedServer");
        Properties props = getServiceProperties();
        registerWaitAndUnregister(props, true);
        endTest("testRegisterOnCreatedServer");
    }

    private Properties getServiceProperties() {
        Properties props = new Properties();
        // Set config to the server container name/provider config name (e.g. ecf.generic.server)
        props.put(SERVICE_EXPORTED_CONFIGS, getServerContainerTypeName());
        // Set the service exported interfaces to all
        props.put(SERVICE_EXPORTED_INTERFACES, SERVICE_EXPORTED_INTERFACES_WILDCARD);
        return props;
    }

    public void testRegisterOnCreatedServerWithIdentity() throws Exception {
        startTest("testRegisterOnCreatedServerWithIdentity");
        Properties props = getServiceProperties();
        // set the container factory arguments to the server identity (e.g. ecftcp://localhost:3282/server)
        props.put(SERVICE_EXPORTED_CONTAINER_FACTORY_ARGUMENTS, new String[] { getServerIdentity() });
        registerWaitAndUnregister(props, true);
        endTest("testRegisterOnCreatedServerWithIdentity");
    }

    public void testRegisterOnExistingServer() throws Exception {
        startTest("testRegisterOnExistingServer");
        // Create server container
        this.server = ContainerFactory.getDefault().createContainer(getServerContainerTypeName(), new Object[] { createServerID() });
        Properties props = getServiceProperties();
        registerWaitAndUnregister(props, true);
        endTest("testRegisterOnExistingServer");
    }

    public void testRegisterOnExistingServerWithIntents() throws Exception {
        startTest("testRegisterOnExistingServerWithIntents");
        // Create server container
        this.server = ContainerFactory.getDefault().createContainer(getServerContainerTypeName(), new Object[] { createServerID() });
        Properties props = getServiceProperties();
        // Add intents
        props.put(SERVICE_INTENTS, "passByValue");
        registerWaitAndUnregister(props, true);
        endTest("testRegisterOnExistingServerWithIntents");
    }

    public void testRegisterOnExistingServerWithMissingIntents() throws Exception {
        startTest("testRegisterOnExistingServerWithMissingIntents");
        // Create server container
        this.server = ContainerFactory.getDefault().createContainer(getServerContainerTypeName(), new Object[] { createServerID() });
        Properties props = getServiceProperties();
        // Add intent that no one actually exposes
        props.put(SERVICE_INTENTS, "foobar");
        registerWaitAndUnregister(props, false);
        endTest("testRegisterOnExistingServerWithMissingIntents");
    }

    public void testRegisterOnExistingServerWithExportedIntents() throws Exception {
        startTest("testRegisterOnExistingServerWithExportedIntents");
        // Create server container
        this.server = ContainerFactory.getDefault().createContainer(getServerContainerTypeName(), new Object[] { createServerID() });
        Properties props = getServiceProperties();
        // Add intents
        props.put(SERVICE_EXPORTED_INTENTS, "passByValue");
        registerWaitAndUnregister(props, true);
        endTest("testRegisterOnExistingServerWithExportedIntents");
    }

    public void testRegisterOnExistingServerWithMissingExportedIntents() throws Exception {
        startTest("testRegisterOnExistingServerWithMissingExportedIntents");
        // Create server container
        this.server = ContainerFactory.getDefault().createContainer(getServerContainerTypeName(), new Object[] { createServerID() });
        Properties props = getServiceProperties();
        // Add intent that no one actually exposes
        props.put(SERVICE_EXPORTED_INTENTS, "foobar");
        registerWaitAndUnregister(props, false);
        endTest("testRegisterOnExistingServerWithMissingExportedIntents");
    }

    public void testRegisterOnExistingServerWithExportedExtraIntents() throws Exception {
        startTest("testRegisterOnExistingServerWithExportedExtraIntents");
        // Create server container
        this.server = ContainerFactory.getDefault().createContainer(getServerContainerTypeName(), new Object[] { createServerID() });
        Properties props = getServiceProperties();
        // Add intents
        props.put(SERVICE_EXPORTED_INTENTS_EXTRA, "passByValue");
        registerWaitAndUnregister(props, true);
        endTest("testRegisterOnExistingServerWithExportedExtraIntents");
    }

    public void testRegisterOnExistingServerWithMissingExportedExtraIntents() throws Exception {
        startTest("testRegisterOnExistingServerWithMissingExportedExtraIntents");
        // Create server container
        this.server = ContainerFactory.getDefault().createContainer(getServerContainerTypeName(), new Object[] { createServerID() });
        Properties props = getServiceProperties();
        // Add intent that no one actually exposes
        props.put(SERVICE_EXPORTED_INTENTS_EXTRA, "foobar");
        registerWaitAndUnregister(props, false);
        endTest("testRegisterOnExistingServerWithMissingExportedExtraIntents");
    }

    public void testRegisterOnExistingServerWithContainerID() throws Exception {
        startTest("testRegisterOnExistingServerWithContainerID");
        // Create server container
        this.server = ContainerFactory.getDefault().createContainer(getServerContainerTypeName(), new Object[] { createServerID() });
        final Properties props = getServiceProperties();
        props.put(SERVICE_EXPORTED_CONTAINER_ID, this.server.getID());
        registerWaitAndUnregister(props, true);
        endTest("testRegisterOnExistingServerWithContainerID");
    }

    public void testRegisterOnExistingServerWithIdentity() throws Exception {
        startTest("testRegisterOnExistingServerWithIdentity");
        // Create server container
        this.server = ContainerFactory.getDefault().createContainer(getServerContainerTypeName(), getServerIdentity());
        Properties props = getServiceProperties();
        registerWaitAndUnregister(props, true);
        endTest("testRegisterOnExistingServerWithIdentity");
    }
}
