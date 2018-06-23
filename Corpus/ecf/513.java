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
package org.eclipse.ecf.internal.provider.r_osgi;

import org.eclipse.ecf.remoteservice.IRemoteFilter;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.osgi.framework.*;

/**
 *
 */
public class RemoteFilterImpl extends org.eclipse.ecf.remoteservice.util.RemoteFilterImpl implements IRemoteFilter {

    /**
	 * @param createFilter
	 */
    public  RemoteFilterImpl(BundleContext context, String createFilter) throws InvalidSyntaxException {
        super(context, createFilter);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.remoteservice.IRemoteFilter#match(org.eclipse.ecf.remoteservice.IRemoteServiceReference)
	 */
    public boolean match(IRemoteServiceReference reference) {
        if (reference == null)
            return false;
        if (reference instanceof RemoteServiceReferenceImpl) {
            RemoteServiceReferenceImpl impl = (RemoteServiceReferenceImpl) reference;
            return match(impl.getProperties());
        }
        return false;
    }

    public boolean match(ServiceReference reference) {
        return false;
    }
}
