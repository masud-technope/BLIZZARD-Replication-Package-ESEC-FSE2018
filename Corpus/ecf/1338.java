/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.discovery.identity;

import org.eclipse.ecf.core.identity.Namespace;

/**
 * ServiceID factory contract.
 * 
 * @see ServiceIDFactory
 */
public interface IServiceIDFactory {

    /**
	 * Create an IServiceTypeID. Creates an immutable IServiceTypeID.
	 * 
	 * @param namespace
	 *            the Namespace instance to create the service ID with. Must not
	 *            be <code>null</code>.
	 * @param serviceType
	 *            The service type. Must not be <code>null</code>.
	 * @since 3.0
	 * 
	 * @return IServiceTypeID created. Will not be <code>null</code>.
	 */
    public IServiceTypeID createServiceTypeID(Namespace namespace, String serviceType);

    /**
	 * Create an IServiceTypeID. Creates an immutable IServiceTypeID.
	 * 
	 * @param namespace
	 *            the Namespace instance to create the service ID with. Must not
	 *            be <code>null</code>.
	 * @param serviceType
	 *            Array containing the ordered naming hierarchy from 0...n. Must
	 *            not be <code>null</code>.
	 * @since 3.0
	 * 
	 * @return IServiceTypeID created. Will not be <code>null</code>.
	 */
    public IServiceTypeID createServiceTypeID(Namespace namespace, String[] serviceType);

    /**
	 * Create an IServiceTypeID. Creates an immutable IServiceTypeID.
	 * 
	 * @param namespace
	 *            the Namespace instance to create the service ID with. Must not
	 *            be <code>null</code>.
	 * @param services
	 *            Array containing the ordered naming hierarchy from 0...n. Must
	 *            not be <code>null</code>.
	 * @param scopes
	 *            Array containing all scopes or
	 *            {@link IServiceTypeID#DEFAULT_SCOPE} for default. Must not be
	 *            <code>null</code>.
	 * @param protocols
	 *            Array containing all protocols or
	 *            {@link IServiceTypeID#DEFAULT_PROTO} for default. Must not be
	 *            <code>null</code>.
	 * @param namingAuthority
	 *            the NamingAuthority or {@link IServiceTypeID#DEFAULT_NA} for
	 *            default. Must not be <code>null</code>.
	 * @since 3.0
	 * 
	 * @return IServiceTypeID created. Will not be <code>null</code>.
	 */
    public IServiceTypeID createServiceTypeID(Namespace namespace, String[] services, String[] scopes, String[] protocols, String namingAuthority);

    /**
	 * Create an IServiceTypeID. Creates an immutable IServiceTypeID.
	 * NamingAuthority will be set to {@link IServiceTypeID#DEFAULT_NA}
	 * 
	 * @param namespace
	 *            the Namespace instance to create the service ID with. Must not
	 *            be <code>null</code>.
	 * @param serviceType
	 *            Array containing the ordered naming hierarchy from 0...n. Must
	 *            not be <code>null</code>.
	 * @param protocols
	 *            Array containing the protocols. Must not be <code>null</code>.
	 * @since 3.0
	 * 
	 * @return IServiceTypeID created. Will not be <code>null</code>.
	 */
    public IServiceTypeID createServiceTypeID(Namespace namespace, String[] serviceType, String[] protocols);

    /**
	 * Create an IServiceTypeID. Creates an immutable IServiceTypeID from a non-
	 * <code>null</code> {@link IServiceTypeID} with a different
	 * {@link Namespace}.
	 * 
	 * @param namespace
	 *            the Namespace instance to create the service type ID with.
	 *            Must not be <code>null</code>.
	 * @param aServiceTypeID
	 *            service type id. Must not be <code>null</code>.
	 * @since 3.0
	 * 
	 * @return IServiceTypeID created. Will not be <code>null</code>.
	 */
    public IServiceTypeID createServiceTypeID(Namespace namespace, IServiceTypeID aServiceTypeID);
}
