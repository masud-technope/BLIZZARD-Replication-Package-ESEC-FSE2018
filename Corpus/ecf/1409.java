/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.remoteservice.util;

import java.util.*;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.internal.remoteservice.Activator;
import org.eclipse.ecf.remoteservice.IRemoteFilter;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.osgi.framework.*;

/**
 * @since 3.0
 *
 */
public class RemoteFilterImpl implements IRemoteFilter {

    /**
	 * @since 8.4
	 */
    //$NON-NLS-1$ //$NON-NLS-2$
    public static final String REMOTE_SERVICEID_PREFIX = "(&(" + org.eclipse.ecf.remoteservice.Constants.SERVICE_ID + "=";

    Filter filter;

    long rsId = 0;

    /**
	 * @param createFilter filter
	 * @throws InvalidSyntaxException if the createFilter is not of valid syntax
	 */
    public  RemoteFilterImpl(String createFilter) throws InvalidSyntaxException {
        this(Activator.getDefault().getContext(), createFilter);
    }

    private void parseForRsId(String createFilter) {
        if (createFilter == null)
            return;
        if (createFilter.startsWith(REMOTE_SERVICEID_PREFIX)) {
            String f = createFilter.substring(REMOTE_SERVICEID_PREFIX.length());
            int rightParenIndex = f.indexOf(')');
            if (rightParenIndex == -1)
                return;
            f = f.substring(0, rightParenIndex);
            try {
                this.rsId = Long.parseLong(f);
            } catch (NumberFormatException e) {
                Activator.getDefault().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Exception parsing remote service filter=" + filter, e));
            }
        }
    }

    /**
	 * @return long the remote service container relative id
	 * @since 8.4
	 */
    public long getRsId() {
        return rsId;
    }

    /**
	 * @param context bundle context
	 * @param createFilter filter
	 * @throws InvalidSyntaxException if given createFilter is not of valid filter syntax
	 * @since 6.0
	 */
    public  RemoteFilterImpl(BundleContext context, String createFilter) throws InvalidSyntaxException {
        if (createFilter == null)
            //$NON-NLS-1$
            throw new InvalidSyntaxException("Filter cannot be null", createFilter);
        parseForRsId(createFilter);
        this.filter = context.createFilter(createFilter);
    }

    public  RemoteFilterImpl(Filter filter) {
        this.filter = filter;
    }

    public static String getObjectClassFilterString(String objectClass) {
        if (objectClass == null)
            return null;
        //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
        return "(" + org.eclipse.ecf.remoteservice.Constants.OBJECTCLASS + "=" + objectClass + ")";
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.remoteservice.IRemoteFilter#match(org.eclipse.ecf.remoteservice.IRemoteServiceReference)
	 */
    @SuppressWarnings("unchecked")
    public boolean match(IRemoteServiceReference reference) {
        if (reference == null)
            return false;
        String[] propertyKeys = reference.getPropertyKeys();
        Hashtable props = new Hashtable();
        for (int i = 0; i < propertyKeys.length; i++) {
            props.put(propertyKeys[i], reference.getProperty(propertyKeys[i]));
        }
        return match(props);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.remoteservice.IRemoteFilter#match(java.util.Dictionary)
	 */
    @SuppressWarnings("unchecked")
    public // DO NOT REMOVE!!!
    boolean match(Dictionary dictionary) {
        return filter.match(dictionary);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.remoteservice.IRemoteFilter#matchCase(java.util.Dictionary)
	 */
    @SuppressWarnings("unchecked")
    public // DO NOT REMOVE!!!
    boolean matchCase(Dictionary dictionary) {
        return filter.matchCase(dictionary);
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RemoteFilterImpl)) {
            return false;
        }
        return this.toString().equals(obj.toString());
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
    public int hashCode() {
        return this.toString().hashCode();
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        return filter.toString();
    }

    /* (non-Javadoc)
	 * @see org.osgi.framework.Filter#match(org.osgi.framework.ServiceReference)
	 */
    public boolean match(ServiceReference reference) {
        return filter.match(reference);
    }

    /**
	 * @param map map
	 * @return boolean true if map matches this filter, false otherwise
	 * @since 6.0
	 */
    @SuppressWarnings("unchecked")
    public boolean matches(Map map) {
        // Once we stop supporting <= OSGi r4.2, delegate to filter.matches(Map) instead
        final Dictionary dict = new Hashtable(map.size());
        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iterator.next();
            dict.put(entry.getKey(), entry.getValue());
        }
        return filter.matchCase(dict);
    }
}
