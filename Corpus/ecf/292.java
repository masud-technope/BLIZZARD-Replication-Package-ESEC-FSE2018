/*******************************************************************************
 * Copyright (c) 2007 Versant Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Kuppe (mkuppe <at> versant <dot> com) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.discovery.identity;

import org.eclipse.ecf.core.identity.ID;

/**
 * Service type ID contract.  
 * 
 */
public interface IServiceTypeID extends ID {

    /**
	 * Default ECF protocols (will be translated into provider specific representation)
	 * @since 3.0
	 */
    //$NON-NLS-1$
    public static final String[] DEFAULT_PROTO = new String[] { "tcp" };

    /**
	 * Default ECF scopes (will be translated into provider specific representation)
	 * @since 3.0
	 */
    //$NON-NLS-1$
    public static final String[] DEFAULT_SCOPE = new String[] { "default" };

    /**
	 * Default ECF naming authority (will be translated into provider specific representation)
	 * @since 3.0
	 */
    //$NON-NLS-1$
    public static final String DEFAULT_NA = "iana";

    /*
	 * jSLP => getServices()[0]:getServices()[1][.getNamingAuthoriy():getService()[n]
	 * jmDNS => _getServices()[0]._getServices()[n]._getProtocol()[0]._getScopes()[0]
	 */
    /*
	 * jSLP => naming authority (IANA or custom)
	 * jmDNS => IANA
	 */
    /**
	 * @return String Naming Authority for this ServiceType.  Will not be <code>null</code>.
	 * If this instance has been created with the provider specific default, this will return
	 * {@link IServiceTypeID#DEFAULT_NA} instead.
	 */
    public String getNamingAuthority();

    /*
	 * jSLP => unknown (0) only known at the service consumer level
	 * jmDNS => protocols (udp/ip or tcp/ip or both) (1)
	 */
    /**
	 * @return String[] of protocols supported.  Will not be <code>null</code>, but may
	 * be empty array.
	 * If this instance has been created with the provider specific default, this will return
	 * {@link IServiceTypeID#DEFAULT_PROTO} instead.
	 */
    public String[] getProtocols();

    /*
	 * jSLP => Scopes (n)
	 * jmDNS => domain (1)
	 */
    /**
	 * @return The scopes in which this Service is registered.  Will not be <code>null</code>, but may
	 * be empty array.
	 * If this instance has been created with the provider specific default, this will return
	 * {@link IServiceTypeID#DEFAULT_SCOPE} instead!
	 */
    public String[] getScopes();

    /*
	 * jSLP => abstract and concrete types (n)
	 * jmDNS => everything before port (n)
	 */
    /**
	 * @return The name of the service type.  If the underlying discovery mechanism
	 *         supports naming hierarchies, the hierarchy will be returned
	 *         flattened as an array.  Will not be <code>null</code>, but may
	 *         be empty array.
	 */
    public String[] getServices();

    /**
	 * Get the internal name of the service type.  Provider implementations may choose
	 * to have this return the same value as {@link ID#getName()}, or they may return
	 * a different, internal value appropriate to the provider.
	 * 
	 * @return String internal name for this service type.  Will not return <code>null</code>.
	 */
    public String getInternal();
}
