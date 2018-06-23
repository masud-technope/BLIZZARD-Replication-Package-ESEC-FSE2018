package $packageName$;

import java.util.Dictionary;
import java.util.Hashtable;
import org.eclipse.ecf.osgi.services.distribution.IDistributionConstants;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.eclipse.ecf.examples.remoteservices.hello.HelloMessage;
import org.eclipse.ecf.examples.remoteservices.hello.IHello;

@SuppressWarnings("restriction")
public class $activator$ implements BundleActivator {

    // This is the hello service implementation
    class Hello implements IHello {

        public String hello(String from) {
            System.out.println("received hello from=" + from);
            return "Server says 'Hi' back to " + from;
        }

        public String helloMessage(HelloMessage message) {
            System.out.println("received HelloMessage=" + message);
            return "Server says 'Hi' back to " + message.getFrom();
        }
    }

    private ServiceRegistration<IHello> helloServiceRegistration;

    public void start(BundleContext context) throws Exception {
        Dictionary<String, Object> props = new Hashtable<String, Object>();
        // add OSGi service property indicated export of all interfaces exposed
        // by service (wildcard)
        props.put("service.exported.interfaces", "*");
        // add OSGi service property specifying config
        props.put("service.exported.configs", "$containerType$");
        // add ECF service property specifying container factory args
        props.put(IDistributionConstants.SERVICE_EXPORTED_CONTAINER_FACTORY_ARGUMENTS, "$containerId$");
        // register the service with remote service properties
        helloServiceRegistration = context.registerService(IHello.class, new Hello(), props);
    }

    public void stop(BundleContext context) throws Exception {
        if (helloServiceRegistration != null)
            helloServiceRegistration.unregister();
    }
}
