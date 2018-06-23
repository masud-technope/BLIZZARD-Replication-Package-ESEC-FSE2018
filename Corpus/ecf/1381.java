/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.remoteservice.generic;

import java.io.Serializable;
import java.util.*;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.remoteservice.*;

public class RemoteServiceRegistryImpl implements Serializable {

    private static long nextServiceId = 1;

    private static final long serialVersionUID = -291866447335444599L;

    protected static final String REMOTEOBJECTCLASS = Constants.OBJECTCLASS;

    protected static final String REMOTESERVICE_ID = Constants.SERVICE_ID;

    protected static final String REMOTESERVICE_RANKING = Constants.SERVICE_RANKING;

    public  RemoteServiceRegistryImpl() {
    //
    }

    /**
	 * Published services by class name. Key is a String class name; Value is a
	 * ArrayList of IRemoteServiceRegistrations
	 */
    protected HashMap publishedServicesByClass = new HashMap(50);

    protected ID containerID;

    /** All published services */
    protected ArrayList allPublishedServices = new ArrayList(50);

    public  RemoteServiceRegistryImpl(ID localContainerID) {
        this();
        this.containerID = localContainerID;
    }

    protected long getNextServiceId() {
        return nextServiceId++;
    }

    public ID getContainerID() {
        return containerID;
    }

    /**
	 * @param containerID containerID
	 * @since 3.4
	 */
    public void setContainerID(ID containerID) {
        this.containerID = containerID;
    }

    @SuppressWarnings("unchecked")
    public void publishService(RemoteServiceRegistrationImpl serviceReg) {
        // Add the ServiceRegistration to the list of Services published by
        // Class Name.
        final String[] clazzes = (String[]) serviceReg.getReference().getProperty(REMOTEOBJECTCLASS);
        final int size = clazzes.length;
        for (int i = 0; i < size; i++) {
            final String clazz = clazzes[i];
            ArrayList services = (ArrayList) publishedServicesByClass.get(clazz);
            if (services == null) {
                services = new ArrayList(10);
                publishedServicesByClass.put(clazz, services);
            }
            services.add(serviceReg);
        }
        // Add the ServiceRegistration to the list of all published Services.
        allPublishedServices.add(serviceReg);
    }

    public void unpublishService(RemoteServiceRegistrationImpl serviceReg) {
        // Remove the ServiceRegistration from the list of Services published by
        // Class Name.
        final String[] clazzes = (String[]) serviceReg.getReference().getProperty(REMOTEOBJECTCLASS);
        final int size = clazzes.length;
        for (int i = 0; i < size; i++) {
            final String clazz = clazzes[i];
            final ArrayList services = (ArrayList) publishedServicesByClass.get(clazz);
            // Fix for bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=329161
            if (services != null)
                services.remove(serviceReg);
        }
        // Remove the ServiceRegistration from the list of all published
        // Services.
        allPublishedServices.remove(serviceReg);
    }

    public void unpublishServices() {
        publishedServicesByClass.clear();
        allPublishedServices.clear();
    }

    @SuppressWarnings("unchecked")
    public IRemoteServiceReference[] lookupServiceReferences(String clazz, IRemoteFilter filter) {
        int size;
        ArrayList references;
        ArrayList serviceRegs;
        if (clazz == null) {
            serviceRegs = allPublishedServices;
        } else {
            /* services registered under the class name */
            serviceRegs = (ArrayList) publishedServicesByClass.get(clazz);
        }
        if (serviceRegs == null) {
            return (null);
        }
        size = serviceRegs.size();
        if (size == 0) {
            return (null);
        }
        references = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            final IRemoteServiceRegistration registration = (IRemoteServiceRegistration) serviceRegs.get(i);
            final IRemoteServiceReference reference = registration.getReference();
            if ((filter == null) || filter.match(reference)) {
                references.add(reference);
            }
        }
        if (references.size() == 0) {
            return null;
        }
        return (IRemoteServiceReference[]) references.toArray(new RemoteServiceReferenceImpl[references.size()]);
    }

    @SuppressWarnings("unchecked")
    public IRemoteServiceReference[] lookupServiceReferences() {
        int size;
        ArrayList references;
        size = allPublishedServices.size();
        if (size == 0) {
            return (null);
        }
        references = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            final IRemoteServiceRegistration registration = (IRemoteServiceRegistration) allPublishedServices.get(i);
            final IRemoteServiceReference reference = registration.getReference();
            references.add(reference);
        }
        if (references.size() == 0) {
            return null;
        }
        return (IRemoteServiceReference[]) references.toArray(new RemoteServiceReferenceImpl[references.size()]);
    }

    @SuppressWarnings("unchecked")
    protected RemoteServiceRegistrationImpl[] getRegistrations() {
        return (RemoteServiceRegistrationImpl[]) allPublishedServices.toArray(new RemoteServiceRegistrationImpl[allPublishedServices.size()]);
    }

    protected RemoteServiceRegistrationImpl findRegistrationForServiceId(long serviceId) {
        for (final Iterator i = allPublishedServices.iterator(); i.hasNext(); ) {
            final RemoteServiceRegistrationImpl reg = (RemoteServiceRegistrationImpl) i.next();
            if (serviceId == reg.getServiceId()) {
                return reg;
            }
        }
        return null;
    }

    /**
	 * @param remoteServiceID remoteServiceID for registration to find
	 * @return RemoteServiceRegistrationImpl a registration instance for given remoteServiceID
	 * @since 3.0
	 */
    protected RemoteServiceRegistrationImpl findRegistrationForRemoteServiceId(IRemoteServiceID remoteServiceID) {
        for (final Iterator i = allPublishedServices.iterator(); i.hasNext(); ) {
            final RemoteServiceRegistrationImpl reg = (RemoteServiceRegistrationImpl) i.next();
            if (remoteServiceID.equals(reg.getID()))
                return reg;
        }
        return null;
    }

    public String toString() {
        //$NON-NLS-1$
        final StringBuffer buf = new StringBuffer("RemoteServiceRegistryImpl[");
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        buf.append("all=").append(allPublishedServices).append(";").append("byclass=").append(publishedServicesByClass).append("]");
        return buf.toString();
    }

    /**
	 * @param serviceid remote service id
	 * @return IRemoteServiceID new remoteServiceId
	 * @since 3.0
	 */
    public IRemoteServiceID createRemoteServiceID(long serviceid) {
        return (IRemoteServiceID) IDFactory.getDefault().createID(IDFactory.getDefault().getNamespaceByName(RemoteServiceNamespace.NAME), new Object[] { getContainerID(), new Long(serviceid) });
    }
}
