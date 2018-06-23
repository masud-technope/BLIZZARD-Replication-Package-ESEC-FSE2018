/*******************************************************************************
 * Copyright (c) 2013 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservice;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.*;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.reflection.ClassUtil;

/**
 * @since 8.3
 */
public class RemoteServiceRegistrationImpl implements IRemoteServiceRegistration, Serializable {

    private static final long serialVersionUID = -6420067298294549200L;

    transient Object service;

    /** service classes for this registration. */
    protected String[] clazzes;

    /** properties for this registration. */
    protected Properties properties;

    /** service ranking. */
    protected int serviceranking;

    /* internal object to use for synchronization */
    protected transient Object registrationLock = new Object();

    /** The registration state */
    protected int state = REGISTERED;

    public static final int REGISTERED = 0x00;

    public static final int UNREGISTERING = 0x01;

    public static final int UNREGISTERED = 0x02;

    protected transient RemoteServiceReferenceImpl reference = null;

    /**
	 * @since 3.0
	 */
    protected IRemoteServiceID remoteServiceID;

    protected IRegistrationListener registrationListener;

    public  RemoteServiceRegistrationImpl() {
        this.registrationListener = null;
    }

    public  RemoteServiceRegistrationImpl(IRegistrationListener listener) {
        this.registrationListener = listener;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (!(o.getClass().equals(this.getClass())))
            return false;
        return getID().equals(((RemoteServiceRegistrationImpl) o).getID());
    }

    public int hashCode() {
        return getID().hashCode();
    }

    public void publish(RemoteServiceRegistryImpl registry, Object svc, String[] clzzes, Dictionary props) {
        this.service = svc;
        this.clazzes = clzzes;
        this.reference = new RemoteServiceReferenceImpl(this);
        synchronized (registry) {
            ID containerID = registry.getContainerID();
            if (containerID == null)
                throw new //$NON-NLS-1$
                NullPointerException(//$NON-NLS-1$
                "Local containerID must be non-null to register remote services");
            this.remoteServiceID = registry.createRemoteServiceID(registry.getNextServiceId());
            this.properties = createProperties(props);
            registry.publishService(this);
        }
    }

    public Object getService() {
        return service;
    }

    public ID getContainerID() {
        return (remoteServiceID == null) ? null : remoteServiceID.getContainerID();
    }

    protected String[] getClasses() {
        return clazzes;
    }

    public IRemoteServiceReference getReference() {
        if (reference == null) {
            synchronized (this) {
                reference = new RemoteServiceReferenceImpl(this);
            }
        }
        return reference;
    }

    public void setProperties(Dictionary properties) {
        synchronized (registrationLock) {
            /* in the process of unregistering */
            if (state != REGISTERED) {
                throw new //$NON-NLS-1$
                IllegalStateException(//$NON-NLS-1$
                "Service already registered");
            }
            this.properties = createProperties(properties);
        }
    // XXX Need to notify that registration modified
    }

    public void unregister() {
        if (this.registrationListener != null) {
            this.registrationListener.unregister(this);
        }
    }

    /**
	 * Construct a properties object from the dictionary for this
	 * ServiceRegistration.
	 * 
	 * @param props
	 *            The properties for this service.
	 * @return A Properties object for this ServiceRegistration.
	 */
    protected Properties createProperties(Dictionary props) {
        final Properties resultProps = new Properties(props);
        resultProps.setProperty(RemoteServiceRegistryImpl.REMOTEOBJECTCLASS, clazzes);
        resultProps.setProperty(RemoteServiceRegistryImpl.REMOTESERVICE_ID, new Long(getID().getContainerRelativeID()));
        final Object ranking = (props == null) ? null : props.get(RemoteServiceRegistryImpl.REMOTESERVICE_RANKING);
        serviceranking = (ranking instanceof Integer) ? ((Integer) ranking).intValue() : 0;
        return (resultProps);
    }

    static class Properties extends Hashtable {

        /**
		 * 
		 */
        private static final long serialVersionUID = -3684607010228779249L;

        /**
		 * Create a properties object for the service.
		 * 
		 * @param props
		 *            The properties for this service.
		 */
        private  Properties(int size, Dictionary props) {
            super((size << 1) + 1);
            if (props != null) {
                synchronized (props) {
                    final Enumeration keysEnum = props.keys();
                    while (keysEnum.hasMoreElements()) {
                        final Object key = keysEnum.nextElement();
                        if (key instanceof String) {
                            final String header = (String) key;
                            setProperty(header, props.get(header));
                        }
                    }
                }
            }
        }

        /**
		 * Create a properties object for the service.
		 * 
		 * @param props
		 *            The properties for this service.
		 */
        protected  Properties(Dictionary props) {
            this((props == null) ? 2 : Math.max(2, props.size()), props);
        }

        /**
		 * Get a clone of the value of a service's property.
		 * 
		 * @param key
		 *            header name.
		 * @return Clone of the value of the property or <code>null</code> if
		 *         there is no property by that name.
		 */
        protected Object getProperty(String key) {
            return (cloneValue(get(key)));
        }

        /**
		 * Get the list of key names for the service's properties.
		 * 
		 * @return The list of property key names.
		 */
        protected synchronized String[] getPropertyKeys() {
            final int size = size();
            final String[] keynames = new String[size];
            final Enumeration keysEnum = keys();
            for (int i = 0; i < size; i++) {
                keynames[i] = (String) keysEnum.nextElement();
            }
            return (keynames);
        }

        /**
		 * Put a clone of the property value into this property object.
		 * 
		 * @param key
		 *            Name of property.
		 * @param value
		 *            Value of property.
		 * @return previous property value.
		 */
        protected synchronized Object setProperty(String key, Object value) {
            return (put(key, cloneValue(value)));
        }

        /**
		 * Attempt to clone the value if necessary and possible.
		 * 
		 * For some strange reason, you can test to see of an Object is
		 * Cloneable but you can't call the clone method since it is protected
		 * on Object!
		 * 
		 * @param value
		 *            object to be cloned.
		 * @return cloned object or original object if we didn't clone it.
		 */
        protected static Object cloneValue(Object value) {
            if (value == null) {
                return null;
            }
            if (value instanceof String) {
                return (value);
            }
            final Class clazz = value.getClass();
            if (clazz.isArray()) {
                // Do an array copy
                final Class type = clazz.getComponentType();
                final int len = Array.getLength(value);
                final Object clonedArray = Array.newInstance(type, len);
                System.arraycopy(value, 0, clonedArray, 0, len);
                return clonedArray;
            }
            // must use reflection because Object clone method is protected!!
            try {
                return (//$NON-NLS-1$
                clazz.getMethod("clone", (Class[]) null).invoke(value, (Object[]) null));
            } catch (final Exception e) {
            } catch (final Error e) {
                if (value instanceof Vector) {
                    return (((Vector) value).clone());
                }
                if (value instanceof Hashtable) {
                    return (((Hashtable) value).clone());
                }
            }
            return (value);
        }

        public synchronized String toString() {
            final String keys[] = getPropertyKeys();
            final int size = keys.length;
            final StringBuffer sb = new StringBuffer(20 * size);
            sb.append('{');
            int n = 0;
            for (int i = 0; i < size; i++) {
                final String key = keys[i];
                if (!key.equals(RemoteServiceRegistryImpl.REMOTEOBJECTCLASS)) {
                    if (n > 0) {
                        //$NON-NLS-1$
                        sb.append(", ");
                    }
                    sb.append(key);
                    sb.append('=');
                    final Object value = get(key);
                    if (value.getClass().isArray()) {
                        sb.append('[');
                        final int length = Array.getLength(value);
                        for (int j = 0; j < length; j++) {
                            if (j > 0) {
                                sb.append(',');
                            }
                            sb.append(Array.get(value, j));
                        }
                        sb.append(']');
                    } else {
                        sb.append(value);
                    }
                    n++;
                }
            }
            sb.append('}');
            return (sb.toString());
        }
    }

    public Object getProperty(String key) {
        return properties.getProperty(key);
    }

    public String[] getPropertyKeys() {
        return properties.getPropertyKeys();
    }

    public long getServiceId() {
        IRemoteServiceID rsID = getID();
        if (rsID == null)
            return 0L;
        return rsID.getContainerRelativeID();
    }

    private static final Object[] NULL_ARGS = new Object[0];

    private static final Class[] NULL_TYPES = new Class[0];

    public static Class[] getTypesForParameters(Object args[]) {
        Class argTypes[] = null;
        if (args == null || args.length == 0)
            argTypes = NULL_TYPES;
        else {
            argTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                if (args[i] == null)
                    argTypes[i] = null;
                else
                    argTypes[i] = args[i].getClass();
            }
        }
        return argTypes;
    }

    public Object callService(IRemoteCall call) throws Exception {
        Object[] callArgs = call.getParameters();
        Object[] args = (callArgs == null) ? NULL_ARGS : callArgs;
        final Method method = ClassUtil.getMethod(service.getClass(), call.getMethod(), getTypesForParameters(args));
        AccessController.doPrivileged(new PrivilegedExceptionAction() {

            public Object run() throws Exception {
                if (!method.isAccessible())
                    method.setAccessible(true);
                return null;
            }
        });
        return method.invoke(service, args);
    }

    public String toString() {
        //$NON-NLS-1$
        StringBuffer buf = new StringBuffer("RemoteServiceRegistrationImpl[");
        //$NON-NLS-1$ //$NON-NLS-2$
        buf.append("remoteServiceID=").append(getID()).append(";");
        //$NON-NLS-1$ //$NON-NLS-2$
        buf.append("rserviceranking=").append(serviceranking).append(";");
        //$NON-NLS-1$ //$NON-NLS-2$
        buf.append("classes=").append(Arrays.asList(clazzes)).append(";");
        //$NON-NLS-1$ //$NON-NLS-2$
        buf.append("state=").append(state).append(";");
        //$NON-NLS-1$ //$NON-NLS-2$
        buf.append("properties=").append(properties).append("]");
        return buf.toString();
    }

    /**
	 * @since 3.0
	 */
    public IRemoteServiceID getID() {
        return this.remoteServiceID;
    }

    /**
	 * @since 8.9
	 * @return String[] the interface classes associated with this registration
	 */
    public String[] getInterfaces() {
        return this.clazzes;
    }
}
