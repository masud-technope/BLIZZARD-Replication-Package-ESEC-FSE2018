package org.eclipse.ecf.tests.osgi.services.remoteserviceadmin;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;
import org.eclipse.ecf.discovery.IDiscoveryAdvertiser;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescriptionWriter;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.IServiceInfoFactory;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.ServiceInfoFactory;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.remoteserviceadmin.RemoteConstants;

public abstract class AbstractEndpointDescriptionWriterTest extends AbstractDistributionTest {

    protected static final int REGISTER_WAIT = 2000;

    private ServiceRegistration registration;

    private ServiceRegistration serviceInfoFactory;

    protected void tearDown() throws Exception {
        if (registration != null) {
            registration.unregister();
            registration = null;
        }
        if (serviceInfoFactory != null) {
            serviceInfoFactory.unregister();
            serviceInfoFactory = null;
        }
        super.tearDown();
    }

    private static class EDEFServiceInfoFactory extends ServiceInfoFactory {

        @Override
        public IServiceInfo createServiceInfo(IDiscoveryAdvertiser advertiser, org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription) {
            try {
                EDEFBundleGenerator edefBundleGenerator = new EDEFBundleGenerator(new File(System.getProperty("java.io.tmpdir")), "org.eclipse.ecf.edefbundlegenerator", "1.0.0", null);
                edefBundleGenerator.generateEDEFBundle(new EndpointDescription[] { (EndpointDescription) endpointDescription });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return super.createServiceInfo(advertiser, endpointDescription);
        }
    }

    public void testRegisterOnCreatedServerEDEF() throws Exception {
        // Make sure we take precedence over default ISIF
        final Dictionary<String, String> props = new Hashtable<String, String>();
        props.put(Constants.SERVICE_RANKING, "9999");
        serviceInfoFactory = getContext().registerService(IServiceInfoFactory.class, new EDEFServiceInfoFactory(), props);
        // Actually register with default service (IConcatService)
        registration = registerDefaultService(getServiceProperties());
        // Wait a while
        Thread.sleep(REGISTER_WAIT);
    }

    private static class XMLServiceInfoFactory extends ServiceInfoFactory {

        private EndpointDescriptionWriter writer;

        public  XMLServiceInfoFactory(EndpointDescriptionWriter writer) {
            this.writer = writer;
        }

        @Override
        public IServiceInfo createServiceInfo(IDiscoveryAdvertiser advertiser, org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription) {
            writeEndpointDescription(endpointDescription);
            return super.createServiceInfo(advertiser, endpointDescription);
        }
    }

    protected static void writeEndpointDescription(org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription) {
        try {
            // Print to system out
            System.out.println(new EndpointDescriptionWriter().writeEndpointDescription(endpointDescription).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testRegisterOnCreatedServer() throws Exception {
        // Make sure we take precedence over default ISIF
        final Dictionary<String, String> props = new Hashtable<String, String>();
        props.put(Constants.SERVICE_RANKING, "9999");
        serviceInfoFactory = getContext().registerService(IServiceInfoFactory.class, new XMLServiceInfoFactory(new EndpointDescriptionWriter()), props);
        // Actually register with default service (IConcatService)
        registration = registerDefaultService(getServiceProperties());
        // Wait a while
        Thread.sleep(REGISTER_WAIT);
    //TODO really test something here
    }

    protected abstract String getServerContainerTypeName();

    private Properties getServiceProperties() {
        Properties props = new Properties();
        // Set config to the server container name/provider config name (e.g.
        // ecf.generic.server)
        props.put(RemoteConstants.SERVICE_EXPORTED_CONFIGS, getServerContainerTypeName());
        // Set the service exported interfaces to all
        props.put(RemoteConstants.SERVICE_EXPORTED_INTERFACES, "*");
        return props;
    }
}
