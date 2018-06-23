/****************************************************************************
 * Copyright (c) 2008 Versant Corp. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Versant Corp. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.discovery.ui.model.resource;

import java.io.IOException;
import java.net.*;
import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.*;
import org.eclipse.ecf.core.*;
import org.eclipse.ecf.core.util.StringUtils;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.discovery.*;
import org.eclipse.ecf.discovery.ui.model.*;
import org.eclipse.ecf.discovery.ui.model.IServiceInfo;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.URIConverterImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.osgi.util.NLS;

public class ServiceResource extends ResourceImpl implements Resource {

    private class ServiceDiscoveryListener implements IServiceListener {

        private volatile ILock lock;

        //$NON-NLS-1$
        public static final String DISCOVERY_CONTAINER = "ecf.singleton.discovery";

        private IContainer container;

        private IDiscoveryLocator discovery;

        private Comparator aComparator = new ECFServiceInfoComparator();

        private  ServiceDiscoveryListener() {
            IJobManager jobManager = Job.getJobManager();
            lock = jobManager.newLock();
        }

        /**
		 * Connect to the underlying ECF discovery service
		 */
        public void connect() {
            try {
                lock.acquire();
                container = ContainerFactory.getDefault().createContainer(DISCOVERY_CONTAINER, new Object[] { "ecf.discovery.composite.locator" });
                discovery = (IDiscoveryLocator) container.getAdapter(IDiscoveryLocator.class);
                if (discovery != null) {
                    discovery.addServiceListener(this);
                    container.connect(null, null);
                    org.eclipse.ecf.discovery.IServiceInfo[] services = discovery.getServices();
                    for (int i = 0; i < services.length; i++) {
                        serviceDiscovered(services[i]);
                        Trace.trace(ModelPlugin.PLUGIN_ID, //$NON-NLS-1$
                        ModelPlugin.PLUGIN_ID + //$NON-NLS-1$
                        "/methods/tracing", //$NON-NLS-1$
                        ServiceResource.class, "connect", //$NON-NLS-1$
                        "serviceResolved: " + //$NON-NLS-1$
                        services[i].toString());
                    }
                } else {
                    ModelPlugin.getDefault().getLog().log(new Status(IStatus.WARNING, ModelPlugin.PLUGIN_ID, Messages.ServiceResource_NO_DISCOVERY_CONTAINER_AVAILABLE));
                }
            } catch (ContainerCreateException e1) {
                container = null;
                discovery = null;
                ModelPlugin.getDefault().getLog().log(new Status(IStatus.WARNING, ModelPlugin.PLUGIN_ID, Messages.ServiceResource_NO_DISCOVERY_CONTAINER_AVAILABLE, e1));
                return;
            } catch (ContainerConnectException e) {
                ModelPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, ModelPlugin.PLUGIN_ID, e.getMessage(), e));
            } finally {
                lock.release();
            }
        }

        private void createEMFIServiceInfo(org.eclipse.ecf.discovery.IServiceInfo ecfServiceInfo, IServiceInfo emfIServiceInfo) {
            emfIServiceInfo.setEcfServiceInfo(ecfServiceInfo);
            emfIServiceInfo.setEcfName(ecfServiceInfo.getServiceID().getName());
            emfIServiceInfo.setEcfLocation(ecfServiceInfo.getLocation());
            emfIServiceInfo.setEcfPriority(ecfServiceInfo.getPriority());
            emfIServiceInfo.setEcfWeight(ecfServiceInfo.getWeight());
            // ECF IServiceID -> EMF IServiceID
            org.eclipse.ecf.discovery.identity.IServiceID ecfServiceID = ecfServiceInfo.getServiceID();
            IServiceID emfIServiceID = ModelFactory.eINSTANCE.createIServiceID();
            emfIServiceID.setEcfServiceID(ecfServiceID);
            emfIServiceID.setEcfServiceName(ecfServiceID.getServiceName());
            emfIServiceInfo.setServiceID(emfIServiceID);
            // ECF IServiceTypeID -> EMF IServiceTypeID
            org.eclipse.ecf.discovery.identity.IServiceTypeID ecfServiceTypeID = ecfServiceID.getServiceTypeID();
            IServiceTypeID emfIServiceTypeID = ModelFactory.eINSTANCE.createIServiceTypeID();
            emfIServiceTypeID.setEcfNamingAuthority(ecfServiceTypeID.getNamingAuthority());
            emfIServiceTypeID.setEcfServiceName(ecfServiceTypeID.getName());
            emfIServiceTypeID.setEcfServiceTypeID(ecfServiceTypeID);
            emfIServiceTypeID.getEcfScopes().addAll(Arrays.asList(ecfServiceTypeID.getScopes()));
            emfIServiceTypeID.getEcfServices().addAll(Arrays.asList(ecfServiceTypeID.getServices()));
            emfIServiceTypeID.getEcfProtocols().addAll(Arrays.asList(ecfServiceTypeID.getProtocols()));
            emfIServiceID.setServiceTypeID(emfIServiceTypeID);
        }

        private IHost getIHost(InetAddress anAddress) {
            IHost host = ModelFactory.eINSTANCE.createIHost();
            host.setAddress(anAddress);
            host.setName(anAddress.getCanonicalHostName());
            return host;
        }

        /**
		 * @param ecfServiceInfo
		 * @return
		 */
        private IServiceInfo getIServiceInfo(org.eclipse.ecf.discovery.IServiceInfo ecfServiceInfo) {
            Trace.entering(ModelPlugin.PLUGIN_ID, ModelPlugin.PLUGIN_ID + "/methods/entering", //$NON-NLS-1$
            ServiceResource.class, //$NON-NLS-1$
            "getIServiceInfo", //$NON-NLS-1$
            ecfServiceInfo);
            IServiceInfo emfIServiceInfo;
            URI anUri = null;
            try {
                anUri = convertToURI(ecfServiceInfo);
                emfIServiceInfo = (IServiceInfo) resourceSet.getEObject(anUri, true);
                createEMFIServiceInfo(ecfServiceInfo, emfIServiceInfo);
                return emfIServiceInfo;
            } catch (RuntimeException e) {
                if (e.getCause() instanceof MalformedURLException) {
                    ModelPlugin.getDefault().getLog().log(new Status(IStatus.INFO, ModelPlugin.PLUGIN_ID, NLS.bind(Messages.ServiceResource_NoEMFServiceModel, anUri)));
                } else {
                    ModelPlugin.getDefault().getLog().log(new Status(IStatus.INFO, ModelPlugin.PLUGIN_ID, e.getMessage()));
                }
            }
            emfIServiceInfo = ModelFactory.eINSTANCE.createIServiceInfo();
            createEMFIServiceInfo(ecfServiceInfo, emfIServiceInfo);
            return emfIServiceInfo;
        }

        /**
		 * @param anIServiceInfo
		 * @return
		 */
        private URI convertToURI(org.eclipse.ecf.discovery.IServiceInfo anIServiceInfo) {
            // get rid of the first underscore
            String name = anIServiceInfo.getServiceID().getServiceTypeID().getName().substring(1);
            // replace the ECF delimiter
            //$NON-NLS-1$ //$NON-NLS-2$
            name = StringUtils.replaceAll(name, "._", "/");
            //$NON-NLS-1$ //$NON-NLS-2$
            name = StringUtils.replaceFirst(name, ".", "/");
            //$NON-NLS-1$
            name = "ecf://" + name;
            // use an URI Converter to allow possible model extender to rewrite
            // URI
            // TODO replace with ExtensibleURIConverterImpl once EMF dep moves
            // up to 2.4
            URIConverter uriConverter = new URIConverterImpl();
            URI uri = uriConverter.normalize(URI.createURI(name));
            // set Authority to host and port of the service
            final java.net.URI location = anIServiceInfo.getLocation();
            final String authority = location.getAuthority();
            uri = URI.createHierarchicalURI(uri.scheme(), authority, uri.device(), uri.segments(), location.getQuery(), location.getFragment());
            return uri;
        }

        /**
		 * Disconnect from the underlying ECF discovery service
		 */
        public void disconnect() {
            container.dispose();
        }

        private IHost findIHost(InetAddress anAddress) {
            INetwork network = (INetwork) ServiceResource.this.getContents().get(0);
            for (java.util.Iterator itr = network.getHosts().iterator(); itr.hasNext(); ) {
                IHost host = (IHost) itr.next();
                if (host.getAddress().equals(anAddress)) {
                    return host;
                }
            }
            return null;
        }

        private IServiceInfo findIServiceInfo(org.eclipse.ecf.discovery.IServiceInfo ecfServiceInfo) {
            IHost host = findIHost(getInetAddress(ecfServiceInfo.getLocation()));
            if (host != null) {
                for (java.util.Iterator itr = host.getServices().iterator(); itr.hasNext(); ) {
                    IServiceInfo emfIServiceInfo = (IServiceInfo) itr.next();
                    if (aComparator.compare(ecfServiceInfo, emfIServiceInfo.getEcfServiceInfo()) == 0) {
                        return emfIServiceInfo;
                    }
                }
            }
            return null;
        }

        /*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.ecf.discovery.IServiceListener#serviceRemoved(org.eclipse
		 * .ecf.discovery.IServiceEvent)
		 */
        public void serviceUndiscovered(IServiceEvent ecfEvent) {
            try {
                lock.acquire();
                Trace.entering(ModelPlugin.PLUGIN_ID, ModelPlugin.PLUGIN_ID + //$NON-NLS-1$
                "/methods/entering", //$NON-NLS-1$
                ServiceResource.class, //$NON-NLS-1$
                "serviceUndiscovered", //$NON-NLS-1$
                ecfEvent);
                org.eclipse.ecf.discovery.IServiceInfo ecfServiceInfo = ecfEvent.getServiceInfo();
                // remove the IServiceInfo
                IServiceInfo emfIServiceInfo = findIServiceInfo(ecfServiceInfo);
                Assert.isNotNull(emfIServiceInfo);
                EcoreUtil.remove(emfIServiceInfo);
                // remove lazy loaded resources as well
                Set resources = new HashSet();
                Iterator iter = EcoreUtil.getAllContents(emfIServiceInfo, false);
                while (iter.hasNext()) {
                    resources.add(((EObject) iter.next()).eResource());
                }
                getResourceSet().getResources().removeAll(resources);
                Trace.trace(ModelPlugin.PLUGIN_ID, //$NON-NLS-1$
                ModelPlugin.PLUGIN_ID + "/methods/tracing", //$NON-NLS-1$
                ServiceResource.class, "serviceUndiscovered", //$NON-NLS-1$ //$NON-NLS-2$
                "Removed service " + emfIServiceInfo);
                // remove the host if no services left for this particular host
                IHost host = findIHost(getInetAddress(ecfServiceInfo.getLocation()));
                if (host != null && host.getServices().size() < 1) {
                    EcoreUtil.remove(host);
                    Trace.trace(ModelPlugin.PLUGIN_ID, ModelPlugin.PLUGIN_ID + //$NON-NLS-1$
                    "/methods/tracing", //$NON-NLS-1$
                    ServiceResource.class, "serviceUndiscovered", //$NON-NLS-1$ //$NON-NLS-2$
                    "Removed host " + host);
                }
                Trace.exiting(ModelPlugin.PLUGIN_ID, ModelPlugin.PLUGIN_ID + //$NON-NLS-1$
                "/methods/exiting", //$NON-NLS-1$
                ServiceResource.class, //$NON-NLS-1$
                "serviceUndiscovered", //$NON-NLS-1$
                ecfEvent);
            } catch (AssertionFailedException e) {
                Trace.catching(ModelPlugin.PLUGIN_ID, ModelPlugin.PLUGIN_ID + "/methods/tracing", ServiceResource.class, "serviceUndiscovered", e);
            } finally {
                lock.release();
            }
        }

        /*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.ecf.discovery.IServiceListener#serviceResolved(org.eclipse
		 * .ecf.discovery.IServiceEvent)
		 */
        public void serviceDiscovered(IServiceEvent ecfEvent) {
            try {
                lock.acquire();
                serviceDiscovered(ecfEvent.getServiceInfo());
            } finally {
                lock.release();
            }
        }

        private void serviceDiscovered(org.eclipse.ecf.discovery.IServiceInfo ecfServiceInfo) {
            Trace.entering(ModelPlugin.PLUGIN_ID, ModelPlugin.PLUGIN_ID + "/methods/entering", //$NON-NLS-1$
            ServiceResource.class, //$NON-NLS-1$
            "serviceResolved", //$NON-NLS-1$
            ecfServiceInfo);
            String hostname = ecfServiceInfo.getLocation().getHost();
            if (hostname != null && // so
            findIServiceInfo(ecfServiceInfo) == // so
            null) {
                // far
                // we
                // only
                // support
                // uris
                // which
                // define
                // a
                // host
                IServiceInfo emfIServiceInfo = getIServiceInfo(ecfServiceInfo);
                Trace.trace(ModelPlugin.PLUGIN_ID, ModelPlugin.PLUGIN_ID + //$NON-NLS-1$
                "/methods/tracing", //$NON-NLS-1$
                ServiceResource.class, //$NON-NLS-1$ //$NON-NLS-2$
                "serviceResolved", "Service created " + emfIServiceInfo);
                InetAddress inetAddress = getInetAddress(ecfServiceInfo.getLocation());
                if (inetAddress == null) {
                    // ignore broken service
                    return;
                }
                IHost host = findIHost(inetAddress);
                if (host == null) {
                    host = getIHost(inetAddress);
                    INetwork network = (INetwork) ServiceResource.this.getContents().get(0);
                    network.getHosts().add(host);
                    Trace.trace(ModelPlugin.PLUGIN_ID, ModelPlugin.PLUGIN_ID + //$NON-NLS-1$
                    "/methods/tracing", //$NON-NLS-1$
                    ServiceResource.class, //$NON-NLS-1$ //$NON-NLS-2$
                    "serviceResolved", "Host created " + emfIServiceInfo);
                }
                host.getServices().add(emfIServiceInfo);
            }
            Trace.exiting(ModelPlugin.PLUGIN_ID, ModelPlugin.PLUGIN_ID + "/methods/exiting", //$NON-NLS-1$
            ServiceResource.class, //$NON-NLS-1$
            "serviceResolved", //$NON-NLS-1$
            ecfServiceInfo);
        }

        private InetAddress getInetAddress(java.net.URI anURI) {
            String host = anURI.getHost();
            if (host != null) {
                try {
                    return InetAddress.getByName(host);
                } catch (UnknownHostException e) {
                    Trace.catching(ModelPlugin.PLUGIN_ID, ModelPlugin.PLUGIN_ID + "/methods/catching", ServiceResource.class, "getInetAddress", e);
                }
            }
            return null;
        }

        private class ECFServiceInfoComparator implements Comparator {

            /*
			 * (non-Javadoc)
			 * 
			 * @see java.util.Comparator#compare(java.lang.Object,
			 * java.lang.Object)
			 */
            public int compare(Object anObj1, Object anObj2) {
                if (anObj1 == anObj2) {
                    return 0;
                }
                org.eclipse.ecf.discovery.IServiceInfo si1 = (org.eclipse.ecf.discovery.IServiceInfo) anObj1;
                org.eclipse.ecf.discovery.IServiceInfo si2 = (org.eclipse.ecf.discovery.IServiceInfo) anObj2;
                org.eclipse.ecf.discovery.identity.IServiceID sId1 = si1.getServiceID();
                org.eclipse.ecf.discovery.identity.IServiceID sId2 = si2.getServiceID();
                if (sId1.equals(sId2)) {
                    return 0;
                }
                return -1;
            }
        }

        public boolean triggerDiscovery() {
            return false;
        }
    }

    public  ServiceResource(URI uri) {
        super(uri);
    }

    /*
	 * Javadoc copied from interface.
	 */
    public synchronized void load(Map options) throws IOException {
        if (!isLoaded) {
            Notification notification = setLoaded(true);
            isLoading = true;
            if (errors != null) {
                errors.clear();
            }
            if (warnings != null) {
                warnings.clear();
            }
            try {
                INetwork network = ModelFactory.eINSTANCE.createINetwork();
                this.getContents().add(network);
                setModified(true);
                Job job = new //$NON-NLS-1$
                Job(//$NON-NLS-1$
                "ServiceDiscoveryListener") {

                    /*
					 * (non-Javadoc)
					 * 
					 * @see
					 * org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core
					 * .runtime.IProgressMonitor)
					 */
                    protected IStatus run(IProgressMonitor monitor) {
                        ServiceDiscoveryListener sdl = new ServiceDiscoveryListener();
                        sdl.connect();
                        return Status.OK_STATUS;
                    }
                };
                job.setSystem(true);
                job.schedule();
            } finally {
                isLoading = false;
                if (notification != null) {
                    eNotify(notification);
                }
                setModified(false);
            }
        }
    }
}
