/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.core.provider;

import java.util.*;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ecf.core.*;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.internal.core.ECFPlugin;

/**
 *  Default implementation of {@link IContainerInstantiator}.  ECF provider implementers
 *  may subclass as desired.
 */
public class BaseContainerInstantiator implements IContainerInstantiator {

    protected static String[] NO_ADAPTERS_ARRAY = new String[] { IContainer.class.getName() };

    protected static String[] EMPTY_STRING_ARRAY = new String[] {};

    protected static Class[][] EMPTY_CLASS_ARRAY = new Class[][] { {} };

    /**
	 * @param arg object to get Integer from
	 * @return Integer created from Object arg
	 * @since 3.6
	 */
    protected Integer getIntegerFromArg(Object arg) {
        if (arg == null)
            return null;
        if (arg instanceof Integer)
            return (Integer) arg;
        else if (arg instanceof String) {
            return new Integer((String) arg);
        } else
            //$NON-NLS-1$ //$NON-NLS-2$
            throw new IllegalArgumentException("arg=" + arg + " is not of integer type");
    }

    /**
	 * @param arg object to get String from
	 * @return String created from Object arg
	 * @since 3.6
	 */
    protected String getStringFromArg(Object arg) {
        if (arg == null)
            return null;
        if (arg instanceof String) {
            return (String) arg;
        }
        //$NON-NLS-1$ //$NON-NLS-2$
        throw new IllegalArgumentException("arg=" + arg + " is not of String type");
    }

    protected Set getAdaptersForClass(Class clazz) {
        Set result = new HashSet();
        IAdapterManager adapterManager = ECFPlugin.getDefault().getAdapterManager();
        if (adapterManager != null)
            result.addAll(Arrays.asList(adapterManager.computeAdapterTypes(clazz)));
        return result;
    }

    protected Set getInterfacesForClass(Set s, Class clazz) {
        if (clazz.equals(Object.class))
            return s;
        s.addAll(getInterfacesForClass(s, clazz.getSuperclass()));
        s.addAll(Arrays.asList(clazz.getInterfaces()));
        return s;
    }

    protected Set getInterfacesForClass(Class clazz) {
        Set clazzes = getInterfacesForClass(new HashSet(), clazz);
        Set result = new HashSet();
        for (Iterator i = clazzes.iterator(); i.hasNext(); ) result.add(((Class) i.next()).getName());
        return result;
    }

    protected String[] getInterfacesAndAdaptersForClass(Class clazz) {
        Set result = getAdaptersForClass(clazz);
        result.addAll(getInterfacesForClass(clazz));
        return (String[]) result.toArray(new String[] {});
    }

    /**
	 * @param parameters parameters to get Map from
	 * @return Map from first of parameters that is instance of Map
	 * @since 3.6
	 */
    protected Map<String, ?> getMap(Object[] parameters) {
        if (parameters != null && parameters.length > 0)
            for (Object p : parameters) if (p instanceof Map)
                return (Map<String, ?>) p;
        return null;
    }

    /**
	 * @param parameters Map parameters to get value from
	 * @param key the key to use to get value from parameters
	 * @param clazz the expected type of the value accessed by key
	 * @param def the default of the value accessed by key.  May be <code>null</code>
	 * @param <T> the expected value type
	 * @return T value from parameters with key and of type clazz
	 * @since 3.6
	 */
    protected <T> T getParameterValue(Map<String, ?> parameters, String key, Class<T> clazz, T def) {
        if (parameters != null) {
            Object o = parameters.get(key);
            if (clazz.isInstance(o))
                return (T) o;
        }
        return def;
    }

    /**
	 * @param parameters Map parameters to get value from
	 * @param key the key to use to get value from parameters
	 * @param def the default of the value accessed by key.  May be <code>null</code>
	 * @return String value from parameters with key
	 * @since 3.6
	 */
    protected String getParameterValue(Map<String, ?> parameters, String key, String def) {
        return getParameterValue(parameters, key, String.class, def);
    }

    /**
	 * @param parameters Map parameters to get value from
	 * @param key the key to use to get value from parameters
	 * @return String value from parameters with key
	 * @since 3.6
	 */
    protected String getParameterValue(Map<String, ?> parameters, String key) {
        return getParameterValue(parameters, key, null);
    }

    /**
	 * @param ns namespace to use for ID creation.  Must not be <code>null</code>
	 * @param parameters Map parameters to get value from
	 * @param key the key to use to get value from parameters
	 * @param type the expected type of the value from parameters
	 * @param def a default value to use if value from parameters is null
	 * @param <T> the expected value type
	 * @return ID the created ID
	 * @since 3.8
	 */
    protected <T> ID getIDParameterValue(Namespace ns, Map<String, ?> parameters, String key, Class<T> type, T def) {
        return ns.createInstance(new Object[] { getParameterValue(parameters, key, type, def) });
    }

    /**
	 * @param ns namespace to use for ID creation.  Must not be <code>null</code>
	 * @param parameters Map parameters to get value from
	 * @param key the key to use to get value from parameters
	 * @param def a default String value to use if value from parameters is null
	 * @return ID the created ID
	 * @since 3.8
	 */
    protected ID getIDParameterValue(Namespace ns, Map<String, ?> parameters, String key, String def) {
        return getIDParameterValue(ns, parameters, key, String.class, def);
    }

    /**
	 * @param ns namespace to use for ID creation.  Must not be <code>null</code>
	 * @param parameters Map parameters to get value from
	 * @param key the key to use to get value from parameters
	 * @return ID the created ID
	 * @since 3.8
	 */
    protected ID getIDParameterValue(Namespace ns, Map<String, ?> parameters, String key) {
        return getIDParameterValue(ns, parameters, key, null);
    }

    /**
	 * @param parameters parameters assumed to contain a Map
	 * @param key key to use to get parameter value from Map
	 * @param clazz the expected type of the value from Map
	 * @param def a default value to use if value from Map is <code>null</code>
	 * @param <T> the expected value type
	 * @return T the parameter value with key from Map
	 * @since 3.6
	 */
    protected <T> T getParameterValue(Object[] parameters, String key, Class<T> clazz, T def) {
        return getParameterValue(getMap(parameters), key, clazz, def);
    }

    /**
	 * @param parameters parameters assumed to contain a Map
	 * @param key key to use to get parameter value from Map
	 * @param clazz the expected type of the value from Map
	 * @param <T> the expected value type
	 * @return T the parameter value with key from Map
	 * @since 3.6
	 */
    protected <T> T getParameterValue(Object[] parameters, String key, Class<T> clazz) {
        return getParameterValue(parameters, key, clazz, null);
    }

    /**
	 * @param parameters parameters assumed to contain a Map
	 * @param key key to use to get parameter value from Map
	 * @param def a default String value to use if value from Map is <code>null</code>
	 * @return Sting the parameter value with key from Map
	 * @since 3.6
	 */
    protected String getMapParameterString(Object[] parameters, String key, String def) {
        return getParameterValue(parameters, key, String.class, def);
    }

    /**
	 * @param parameters parameters assumed to contain a Map
	 * @param key key to use to get parameter value from Map
	 * @return Sting the parameter value with key from Map
	 * @since 3.6
	 */
    protected String getMapParameterString(Object[] parameters, String key) {
        return getParameterValue(parameters, key, String.class, null);
    }

    public IContainer createInstance(ContainerTypeDescription description, Object[] parameters) throws ContainerCreateException {
        //$NON-NLS-1$
        throw new ContainerCreateException("createInstance not supported");
    }

    public String[] getSupportedAdapterTypes(ContainerTypeDescription description) {
        return NO_ADAPTERS_ARRAY;
    }

    public Class[][] getSupportedParameterTypes(ContainerTypeDescription description) {
        return EMPTY_CLASS_ARRAY;
    }

    public String[] getSupportedIntents(ContainerTypeDescription description) {
        return null;
    }
}
