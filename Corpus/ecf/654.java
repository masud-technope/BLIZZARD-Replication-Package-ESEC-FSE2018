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
package org.eclipse.ecf.discovery.service;

import java.util.Properties;
import org.eclipse.ecf.discovery.IDiscoveryContainerAdapter;

/**
 * OSGI discovery service interface.  This interface should be registered
 * by providers when they wish to expose discovery services to OSGI
 * service clients.
 * 
 * @deprecated use IDiscoveryLocator and IDiscoveryContainer instead
 */
public interface IDiscoveryService extends IDiscoveryContainerAdapter {

    // All methods provided by superclass
    //$NON-NLS-1$
    public static final String CONTAINER_ID = "org.eclipse.ecf.discovery.containerID";

    /**
	 * The name of the discovery container under which it is registered with the OSGi runtime as a {@link Properties} 
	 * @since 2.1
	 */
    //$NON-NLS-1$
    public static final String CONTAINER_NAME = "org.eclipse.ecf.discovery.containerName";
}
