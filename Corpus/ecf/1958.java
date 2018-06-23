/* Copyright (c) 2006-2009 Jan S. Rellermeyer
 * Systems Group,
 * Institute for Pervasive Computing, ETH Zurich.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of ETH Zurich nor the names of its contributors may be
 *      used to endorse or promote products derived from this software without
 *      specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package ch.ethz.iks.r_osgi.impl;

import java.lang.reflect.Method;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import org.objectweb.asm.Type;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import ch.ethz.iks.r_osgi.RemoteOSGiService;
import ch.ethz.iks.r_osgi.messages.DeliverServiceMessage;

/**
 * Encapsulates a service registered for remote access.
 * 
 * @author Jan S. Rellermeyer
 */
final class RemoteServiceRegistration {

    /**
	 * the local service reference.
	 */
    private final ServiceReference reference;

    /**
	 * the service id.
	 */
    private final long serviceID;

    /**
	 * the interface names.
	 */
    private final String[] interfaceNames;

    /**
	 * the service object.
	 */
    private final Object serviceObject;

    /**
	 * an internal method table, pregenerated to speed up the reflective calls.
	 */
    private final HashMap methodTable = new HashMap(0);

    /**
	 * a prefactored deliver service message.
	 */
    private DeliverServiceMessage deliverServiceMessage;

    /**
	 * creates a new RemoteService object.
	 * 
	 * @param ref
	 *            the <code>ServiceReference</code> under which the service was
	 *            registered. Can be a surrogate.
	 * @param service
	 *            the <code>ServiceReference</code>
	 * @throws ClassNotFoundException
	 *             if one of the interface classes cannot be found.
	 * @throws ServiceLocationException
	 */
     RemoteServiceRegistration(final ServiceReference ref, final ServiceReference service) throws ClassNotFoundException {
        reference = service;
        serviceID = ((Long) service.getProperty(Constants.SERVICE_ID)).longValue();
        interfaceNames = (String[]) service.getProperty(Constants.OBJECTCLASS);
        // get the service object
        serviceObject = RemoteOSGiActivator.getActivator().getContext().getService(service);
        if (serviceObject == null) {
            //$NON-NLS-1$
            throw new IllegalStateException("Service is not present.");
        }
        // get the interface classes
        final ClassLoader bundleLoader = serviceObject.getClass().getClassLoader();
        final String[] interfaceNames = (String[]) service.getProperty(Constants.OBJECTCLASS);
        final int interfaceCount = interfaceNames.length;
        final Class[] serviceInterfaces = new Class[interfaceCount];
        // build up the method table for each interface
        for (int i = 0; i < interfaceCount; i++) {
            serviceInterfaces[i] = bundleLoader.loadClass(interfaceNames[i]);
            final Method[] methods = serviceInterfaces[i].getMethods();
            for (int j = 0; j < methods.length; j++) {
                methodTable.put(methods[j].getName() + Type.getMethodDescriptor(methods[j]), methods[j]);
            }
        }
        final Dictionary headers = service.getBundle().getHeaders();
        final CodeAnalyzer analyzer = new CodeAnalyzer(bundleLoader, (String) headers.get(Constants.IMPORT_PACKAGE), (String) headers.get(Constants.EXPORT_PACKAGE));
        try {
            deliverServiceMessage = analyzer.analyze(interfaceNames, (String) ref.getProperty(RemoteOSGiService.SMART_PROXY), (String[]) ref.getProperty(RemoteOSGiService.INJECTIONS), (String) ref.getProperty(RemoteOSGiService.PRESENTATION));
            deliverServiceMessage.setServiceID(((Long) ref.getProperty(Constants.SERVICE_ID)).toString());
        } catch (final Exception e) {
            if (RemoteOSGiServiceImpl.log != null) {
                RemoteOSGiServiceImpl.log.log(LogService.LOG_ERROR, "Error during remote service registration", e);
            }
        }
    }

    /**
	 * get the service id.
	 * 
	 * @return the service id.
	 * @since 0.5
	 */
    long getServiceID() {
        return serviceID;
    }

    ServiceReference getReference() {
        return reference;
    }

    /**
	 * get the service properties.
	 * 
	 * @return the properties.
	 */
    Dictionary getProperties() {
        final String[] keys = reference.getPropertyKeys();
        final Dictionary props = new Hashtable(keys.length);
        for (int i = 0; i < keys.length; i++) {
            props.put(keys[i], reference.getProperty(keys[i]));
        }
        return props;
    }

    /**
	 * get the service interfaces.
	 * 
	 * @return the class names of the service interfaces.
	 */
    String[] getInterfaceNames() {
        return interfaceNames;
    }

    /**
	 * check, if the registration equals a service reference.
	 * 
	 * @param obj
	 *            the object to check.
	 * @return true if the object is equal.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
    public boolean equals(final Object obj) {
        if (obj instanceof ServiceReference) {
            final ServiceReference ref = (ServiceReference) obj;
            return ref.equals(reference);
        } else if (obj instanceof RemoteServiceRegistration) {
            return ((RemoteServiceRegistration) obj).equals(reference);
        }
        return false;
    }

    /**
	 * get the hash code.
	 * 
	 * @return the hash code.
	 */
    public int hashCode() {
        return (int) (serviceID ^ (serviceID >>> 32));
    }

    /**
	 * get the service object.
	 * 
	 * @return the service object.
	 */
    Object getServiceObject() {
        return serviceObject;
    }

    /**
	 * get a method from the method table.
	 * 
	 * @param signature
	 *            the signature of the method.
	 * @return the Method object.
	 */
    Method getMethod(final String signature) {
        Method method = (Method) methodTable.get(signature);
        // https://bugs.eclipse.org/327029
        if (method == null && signature.startsWith("_rosgi")) {
            final String newsig = signature.substring(6);
            final String firstChar = newsig.substring(0, 1).toLowerCase();
            final String correctedSig = newsig.substring(1);
            method = (Method) methodTable.get(firstChar + correctedSig);
        }
        return method;
    }

    /**
	 * get the DeliverServiceMessage.
	 * 
	 * @return the message.
	 */
    DeliverServiceMessage getDeliverServiceMessage() {
        return deliverServiceMessage;
    }

    public String toString() {
        //$NON-NLS-1$ //$NON-NLS-2$
        return "RemoteServiceRegistration{" + reference.toString() + "}";
    }
}
