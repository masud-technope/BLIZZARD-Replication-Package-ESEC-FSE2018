/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.osgi.services.distribution;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.util.LogHelper;
import org.eclipse.ecf.core.util.SystemLogService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.service.EventListenerHook;
import org.osgi.service.log.LogService;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.EndpointEvent;
import org.osgi.service.remoteserviceadmin.EndpointEventListener;
import org.osgi.service.remoteserviceadmin.EndpointListener;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf.osgi.services.distribution";

    public static final boolean autoCreateProxyContainer = new Boolean(System.getProperty("org.eclipse.ecf.osgi.services.distribution.autoCreateProxyContainer", //$NON-NLS-1$
    "true")).booleanValue();

    public static final boolean autoCreateHostContainer = new Boolean(System.getProperty("org.eclipse.ecf.osgi.services.distribution.autoCreateHostContainer", //$NON-NLS-1$
    "true")).booleanValue();

    public static final String defaultHostConfigType = System.getProperty(//$NON-NLS-1$
    "org.eclipse.ecf.osgi.services.distribution.defaultConfigType", //$NON-NLS-1$
    "ecf.generic.server");

    //$NON-NLS-1$
    private static final String PROP_USE_DS = "equinox.use.ds";

    private static Activator plugin;

    private BundleContext context;

    private ServiceTracker logServiceTracker = null;

    private LogService logService = null;

    private BasicTopologyManagerImpl basicTopologyManagerImpl;

    private ServiceRegistration endpointListenerReg;

    private ServiceRegistration endpointEventListenerReg;

    private Map<Bundle, List<EndpointEventHolder>> bundleEndpointEventListenerMap = new HashMap<Bundle, List<EndpointEventHolder>>();

    private BasicTopologyManagerComponent basicTopologyManagerComp;

    private ServiceRegistration eventListenerHookRegistration;

    private ServiceRegistration eventAdminListenerRegistration;

    private static final boolean disableBasicTopologyManager = new Boolean(System.getProperty("org.eclipse.ecf.osgi.services.distribution.disableBasicTopologyManager", //$NON-NLS-1$
    "false")).booleanValue();

    private OSGiTopologyManagerImpl osgiTopologyManagerImpl;

    public static Activator getDefault() {
        return plugin;
    }

    public BundleContext getContext() {
        return context;
    }

    protected LogService getLogService() {
        if (this.context == null)
            return null;
        if (logServiceTracker == null) {
            logServiceTracker = new ServiceTracker(this.context, LogService.class.getName(), null);
            logServiceTracker.open();
        }
        logService = (LogService) logServiceTracker.getService();
        if (logService == null)
            logService = new SystemLogService(PLUGIN_ID);
        return logService;
    }

    public void log(IStatus status) {
        if (logService == null)
            logService = getLogService();
        if (logService != null)
            logService.log(null, LogHelper.getLogCode(status), LogHelper.getLogMessage(status), status.getException());
    }

    public void log(ServiceReference sr, IStatus status) {
        log(sr, LogHelper.getLogCode(status), LogHelper.getLogMessage(status), status.getException());
    }

    public void log(ServiceReference sr, int level, String message, Throwable t) {
        if (logService == null)
            logService = getLogService();
        if (logService != null)
            logService.log(sr, level, message, t);
    }

    class EndpointEventHolder {

        private final EndpointDescription endpointDescription;

        private final String filter;

        public  EndpointEventHolder(EndpointDescription d, String f) {
            this.endpointDescription = d;
            this.filter = f;
        }

        public EndpointDescription getEndpoint() {
            return this.endpointDescription;
        }

        public String getFilter() {
            return this.filter;
        }
    }

    public class ProxyEndpointEventListener implements EndpointEventListener {

        private final Bundle bundle;

        public  ProxyEndpointEventListener(Bundle b) {
            this.bundle = b;
        }

        public void endpointChanged(EndpointEvent event, String filter) {
            int type = event.getType();
            if (type == EndpointEvent.ADDED) {
                synchronized (bundleEndpointEventListenerMap) {
                    List<EndpointEventHolder> endpointEventHolders = bundleEndpointEventListenerMap.get(this.bundle);
                    if (endpointEventHolders == null)
                        // create new one
                        endpointEventHolders = new ArrayList<EndpointEventHolder>();
                    endpointEventHolders.add(new EndpointEventHolder(event.getEndpoint(), filter));
                    bundleEndpointEventListenerMap.put(this.bundle, endpointEventHolders);
                }
            } else if (type == EndpointEvent.REMOVED) {
                synchronized (bundleEndpointEventListenerMap) {
                    List<EndpointEventHolder> endpointEventHolders = bundleEndpointEventListenerMap.get(this.bundle);
                    if (endpointEventHolders != null) {
                        for (Iterator<EndpointEventHolder> i = endpointEventHolders.iterator(); i.hasNext(); ) {
                            EndpointEventHolder eh = i.next();
                            EndpointDescription oldEd = eh.getEndpoint();
                            EndpointDescription newEd = event.getEndpoint();
                            if (oldEd.equals(newEd))
                                i.remove();
                        }
                        if (endpointEventHolders.size() == 0)
                            bundleEndpointEventListenerMap.remove(this.bundle);
                    }
                }
            }
            // Actually call underlying listener
            deliverSafe(event, filter);
        }

        public BasicTopologyManagerImpl getBasicTopologyManagerImpl() {
            return Activator.this.basicTopologyManagerImpl;
        }

        private void logError(String methodName, String message, Throwable e) {
            getDefault().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR, //$NON-NLS-1$
            Activator.class.getName() + ":" + (//$NON-NLS-1$
            (methodName == null) ? "<unknown>" : //$NON-NLS-1$
            methodName) + ":" + //$NON-NLS-1$
            ((message == null) ? "<empty>" : message), e));
        }

        private void deliverSafe(EndpointEvent endpointEvent, String matchingFilter) {
            EndpointEventListener listener = Activator.this.basicTopologyManagerImpl;
            if (listener == null)
                return;
            try {
                listener.endpointChanged(endpointEvent, matchingFilter);
            } catch (Exception e) {
                String message = "Exception in EndpointEventListener listener=" + listener + " event=" + endpointEvent + " matchingFilter=" + matchingFilter;
                logError("deliverSafe", message, e);
            } catch (LinkageError e) {
                String message = "LinkageError in EndpointEventListener listener=" + listener + " event=" + endpointEvent + " matchingFilter=" + matchingFilter;
                logError("deliverSafe", message, e);
            } catch (AssertionError e) {
                String message = "AssertionError in EndpointEventListener listener=" + listener + " event=" + endpointEvent + " matchingFilter=" + matchingFilter;
                logError("deliverSafe", message, e);
            }
        }

        public void deliverRemoveEventForBundle(EndpointEventHolder eventHolder) {
            deliverSafe(new EndpointEvent(EndpointEvent.REMOVED, eventHolder.getEndpoint()), eventHolder.getFilter());
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
    public void start(final BundleContext ctxt) throws Exception {
        // Always set plugin and context
        plugin = this;
        this.context = ctxt;
        // use OSGiTopologyManager
        if (disableBasicTopologyManager) {
            osgiTopologyManagerImpl = new OSGiTopologyManagerImpl(context);
            Properties props = new Properties();
            props.put(org.osgi.service.remoteserviceadmin.EndpointEventListener.ENDPOINT_LISTENER_SCOPE, osgiTopologyManagerImpl.getScope());
            // Register as deprecated EndpointListener
            endpointListenerReg = getContext().registerService(EndpointListener.class.getName(), osgiTopologyManagerImpl, (Dictionary) props);
            // Also register as EndpointEventListener
            endpointEventListenerReg = getContext().registerService(EndpointEventListener.class.getName(), osgiTopologyManagerImpl, (Dictionary) props);
            // export any previously registered remote services by calling
            // activate
            osgiTopologyManagerImpl.activate();
        } else {
            // Create basicTopologyManagerImpl
            basicTopologyManagerImpl = new BasicTopologyManagerImpl(context);
            // Register basicTopologyManagerImpl as EndpointListener always, so
            // that
            // gets notified when Endpoints are discovered
            Properties props = new Properties();
            props.put(org.osgi.service.remoteserviceadmin.EndpointEventListener.ENDPOINT_LISTENER_SCOPE, basicTopologyManagerImpl.getScope());
            // Register as deprecated EndpointListener
            // endpointListenerReg = getContext().registerService(
            // EndpointListener.class.getName(), basicTopologyManagerImpl,
            // (Dictionary) props);
            // As per section 122.6.3/Tracking providers -Tracking providers –
            // An
            // Endpoint Event
            // Listener or Endpoint Listener must track the bundles that provide
            // it
            // with
            // Endpoint Descriptions. If a bundle that provided Endpoint
            // Descriptions is
            // stopped, all Endpoint Descriptions that were provided by that
            // bundle
            // must
            // be removed. This can be implemented straightforwardly with a
            // Service
            // Factory
            endpointEventListenerReg = getContext().registerService(EndpointEventListener.class.getName(), new ServiceFactory() {

                public Object getService(Bundle bundle, ServiceRegistration registration) {
                    return new ProxyEndpointEventListener(bundle);
                }

                public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
                    ProxyEndpointEventListener peel = (service instanceof ProxyEndpointEventListener) ? (ProxyEndpointEventListener) service : null;
                    if (peel == null)
                        return;
                    synchronized (bundleEndpointEventListenerMap) {
                        List<EndpointEventHolder> endpointEventHolders = bundleEndpointEventListenerMap.get(bundle);
                        if (endpointEventHolders != null)
                            for (EndpointEventHolder eh : endpointEventHolders) peel.deliverRemoveEventForBundle(eh);
                    }
                }
            }, (Dictionary) props);
            // more to do)
            if (Boolean.valueOf(context.getProperty(PROP_USE_DS)).booleanValue())
                // If this property is set we assume DS is being used.
                return;
            // The following code is to make sure that we don't do any more if
            // EventListenerHook has already been registered for us by DS
            // Create serviceFilter for EventListenerHook classname
            String serviceName = EventListenerHook.class.getName();
            Filter serviceFilter = context.createFilter(//$NON-NLS-1$ //$NON-NLS-2$
            "(objectclass=" + serviceName + ")");
            // if this bundle has already registered EventListenerHook service
            // via
            // ds, then
            // we're done
            ServiceReference[] refs = context.getBundle().getRegisteredServices();
            if (refs != null) {
                for (int i = 0; i < refs.length; i++) if (serviceFilter.match(refs[i]))
                    // We found a service registered by this bundle
                    return;
            // already so we return
            }
            // Otherwise (no DS), we create a basicTopologyManagerComponent
            basicTopologyManagerComp = new BasicTopologyManagerComponent();
            // bind the topology manager to it
            basicTopologyManagerComp.bindEndpointEventListener(basicTopologyManagerImpl);
            // Register RemoteServiceAdminListener
            eventAdminListenerRegistration = this.context.registerService(RemoteServiceAdminListener.class, basicTopologyManagerComp, null);
            // register the basic topology manager as EventListenerHook service
            eventListenerHookRegistration = this.context.registerService(EventListenerHook.class, basicTopologyManagerComp, null);
            // export any previously registered remote services by calling
            // activate
            basicTopologyManagerComp.activate();
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext ctxt) throws Exception {
        if (eventListenerHookRegistration != null) {
            eventListenerHookRegistration.unregister();
            eventListenerHookRegistration = null;
        }
        if (basicTopologyManagerComp != null) {
            basicTopologyManagerComp.unbindEndpointEventListener(basicTopologyManagerImpl);
            basicTopologyManagerComp = null;
        }
        if (endpointEventListenerReg != null) {
            endpointEventListenerReg.unregister();
            endpointEventListenerReg = null;
        }
        if (endpointListenerReg != null) {
            endpointListenerReg.unregister();
            endpointListenerReg = null;
        }
        if (eventAdminListenerRegistration != null) {
            eventAdminListenerRegistration.unregister();
            eventAdminListenerRegistration = null;
        }
        synchronized (bundleEndpointEventListenerMap) {
            bundleEndpointEventListenerMap.clear();
        }
        if (basicTopologyManagerImpl != null) {
            basicTopologyManagerImpl.close();
            basicTopologyManagerImpl = null;
        }
        if (logServiceTracker != null) {
            logServiceTracker.close();
            logServiceTracker = null;
            logService = null;
        }
        this.context = null;
        plugin = null;
    }
}
