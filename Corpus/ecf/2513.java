/*******************************************************************************
* Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.internal.provider.local.container;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.sharedobject.SharedObjectInitException;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.provider.remoteservice.generic.RegistrySharedObject;
import org.eclipse.ecf.provider.remoteservice.generic.RemoteServiceRegistrationImpl;
import org.eclipse.ecf.remoteservice.IRemoteCall;

public class LocalRemoteServiceRegistry extends RegistrySharedObject {

    public  LocalRemoteServiceRegistry(LocalRemoteServiceContainer container) throws SharedObjectInitException {
        super();
        init(new LocalSharedObjectConfig(container, IDFactory.getDefault().createGUID()));
    }

    protected Object callSynch(RemoteServiceRegistrationImpl registration, IRemoteCall call) throws ECFException {
        try {
            Object service = registration.getService();
            if (service == null)
                throw new //$NON-NLS-1$
                NullPointerException(//$NON-NLS-1$
                "Service is null for registration=" + registration.getReference().getID());
            // Lookup method on service object
            Class[] svcClasses = getClassesForService(service, (String[]) registration.getProperty(org.eclipse.ecf.remoteservice.Constants.OBJECTCLASS));
            if (svcClasses == null || svcClasses.length == 0)
                throw new //$NON-NLS-1$
                NullPointerException(//$NON-NLS-1$
                "Service interface not found for registration=" + registration.getReference().getID());
            Method method = getMethodForService(call.getMethod(), svcClasses);
            if (method == null)
                //$NON-NLS-1$ //$NON-NLS-2$
                throw new NullPointerException("Method " + call.getMethod() + " not found for registration=" + registration.getReference().getID());
            return method.invoke(service, call.getParameters());
        } catch (Exception e) {
            throw new ECFException("Exception invoking local service registration=" + registration.getReference().getID(), e);
        }
    }

    private Method getMethodForService(String method, Class[] svcClasses) {
        for (int i = 0; i < svcClasses.length; i++) {
            Method[] methods = svcClasses[i].getDeclaredMethods();
            if (methods == null)
                return null;
            for (int j = 0; j < methods.length; j++) {
                if (method.equals(methods[j].getName()))
                    return methods[j];
            }
        }
        return null;
    }

    private Class[] getClassesForService(Object service, String[] svcInterfaces) {
        Class[] classes = service.getClass().getInterfaces();
        List results = new ArrayList();
        for (int i = 0; i < classes.length; i++) {
            for (int j = 0; j < svcInterfaces.length; j++) {
                if (classes[i].getName().equals(svcInterfaces[j])) {
                    results.add(classes[i]);
                }
            }
        }
        return (Class[]) results.toArray(new Class[] {});
    }

    protected void sendAddRegistration(ID receiver, Integer requestId, RemoteServiceRegistrationImpl reg) {
    // do nothing
    }

    protected void sendUnregister(RemoteServiceRegistrationImpl serviceRegistration) {
    // do nothing
    }
}
