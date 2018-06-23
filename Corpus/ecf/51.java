package $packageName$;

import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ecf.examples.remoteservices.hello.HelloMessage;
import org.eclipse.ecf.examples.remoteservices.hello.IHello;
import org.eclipse.ecf.examples.remoteservices.hello.IHelloAsync;
import org.eclipse.ecf.osgi.services.distribution.IDistributionConstants;
import org.eclipse.ecf.remoteservice.IAsyncCallback;
import org.eclipse.equinox.concurrent.future.IFuture;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

@SuppressWarnings("restriction")
public class Activator implements BundleActivator, IDistributionConstants {

    private static BundleContext context;

    private ServiceTracker<IHello, IHello> helloServiceTracker;

    private static final String CONSUMER_NAME = "$consumerName$";

    public void start(BundleContext bundleContext) throws Exception {
        Activator.context = bundleContext;
        helloServiceTracker = new ServiceTracker<IHello, IHello>(context, createFilter(), new HelloTrackerCustomizer());
        helloServiceTracker.open();
    }

    private Filter createFilter() throws InvalidSyntaxException {
        return context.createFilter("(&(" + org.osgi.framework.Constants.OBJECTCLASS + "=" + IHello.class.getName() + ")(" + SERVICE_IMPORTED + "=*))");
    }

    class HelloTrackerCustomizer implements ServiceTrackerCustomizer<IHello, IHello> {

        public IHello addingService(ServiceReference<IHello> reference) {
            IHello proxy = context.getService(reference);
            useHelloService(proxy);
            return proxy;
        }

        private void useHelloService(IHello proxy) {
            System.out.println("STARTING remote call via proxy...");
            proxy.hello(CONSUMER_NAME + " via proxy");
            System.out.println("COMPLETED remote call via proxy");
            System.out.println();
            // Call other helloMessage method
            System.out.println("STARTING remote call via proxy...");
            proxy.helloMessage(new HelloMessage(CONSUMER_NAME + " via proxy", "howdy"));
            System.out.println("COMPLETED remote call via proxy");
            System.out.println();
            // this asynchronous interface to invoke methods asynchronously
            if (proxy instanceof IHelloAsync) {
                IHelloAsync helloA = (IHelloAsync) proxy;
                // Create callback for use in IHelloAsync
                IAsyncCallback<String> callback = new IAsyncCallback<String>() {

                    public void onSuccess(String result) {
                        System.out.println("COMPLETED remote call with callback SUCCESS with result=" + result);
                        System.out.println();
                    }

                    public void onFailure(Throwable t) {
                        System.out.println("COMPLETED remote call with callback FAILED with exception=" + t);
                        System.out.println();
                    }
                };
                // Call asynchronously with callback
                System.out.println("STARTING async remote call via callback...");
                helloA.helloAsync(CONSUMER_NAME + " via async proxy with listener", callback);
                System.out.println("LOCAL async invocation complete");
                System.out.println();
                // Call asynchronously with future
                System.out.println("STARTING async remote call via future...");
                IFuture future = helloA.helloAsync(CONSUMER_NAME + " via async proxy with future");
                System.out.println("LOCAL async future invocation complete");
                System.out.println();
                try {
                    while (!future.isDone()) {
                        // do some other stuff
                        System.out.println("LOCAL future not yet done...so we're doing other stuff while waiting for future to be done");
                        Thread.sleep(200);
                    }
                    // Now it's done, so this will not block
                    Object result = future.get();
                    System.out.println("COMPLETED remote call with future SUCCEEDED with result=" + result);
                    System.out.println();
                } catch (OperationCanceledException e) {
                    System.out.println("COMPLETED remote call with callback CANCELLED with exception=" + e);
                    System.out.println();
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    System.out.println("COMPLETED remote call with callback INTERRUPTED with exception=" + e);
                    System.out.println();
                    e.printStackTrace();
                }
                // Call other helloMessage method
                // Call asynchronously with callback
                System.out.println("STARTING async remote call via callback...");
                helloA.helloMessageAsync(new HelloMessage(CONSUMER_NAME + " via async proxy with listener", "howdy"), callback);
                System.out.println("LOCAL async invocation complete");
                System.out.println();
                // Call asynchronously with future
                System.out.println("STARTING async remote call via future...");
                future = helloA.helloMessageAsync(new HelloMessage(CONSUMER_NAME + " via async proxy with future", "howdy"));
                System.out.println("LOCAL async future invocation complete");
                System.out.println();
                try {
                    while (!future.isDone()) {
                        // do some other stuff
                        System.out.println("LOCAL future not yet done...so we're doing other stuff while waiting for future to be done");
                        Thread.sleep(200);
                    }
                    // Now it's done, so this will not block
                    Object result = future.get();
                    System.out.println("COMPLETED remote call with future SUCCEEDED with result=" + result);
                    System.out.println();
                } catch (OperationCanceledException e) {
                    System.out.println("COMPLETED remote call with callback CANCELLED with exception=" + e);
                    System.out.println();
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    System.out.println("COMPLETED remote call with callback INTERRUPTED with exception=" + e);
                    System.out.println();
                    e.printStackTrace();
                }
            }
        }

        public void modifiedService(ServiceReference<IHello> reference, IHello service) {
        }

        public void removedService(ServiceReference<IHello> reference, IHello service) {
        }
    }

    public void stop(BundleContext bundleContext) throws Exception {
        if (helloServiceTracker != null) {
            helloServiceTracker.close();
            helloServiceTracker = null;
        }
        Activator.context = null;
    }
}
