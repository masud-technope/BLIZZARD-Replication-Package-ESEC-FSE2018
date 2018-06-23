/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.osgi.services.distribution;

import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteConstants;

/**
 * @since 1.1
 */
public interface IDistributionConstants {

    public static final String REMOTE_CONFIGS_SUPPORTED = org.osgi.service.remoteserviceadmin.RemoteConstants.REMOTE_CONFIGS_SUPPORTED;

    public static final String REMOTE_INTENTS_SUPPORTED = org.osgi.service.remoteserviceadmin.RemoteConstants.REMOTE_INTENTS_SUPPORTED;

    public static final String SERVICE_EXPORTED_CONFIGS = org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_EXPORTED_CONFIGS;

    public static final String SERVICE_EXPORTED_INTENTS = org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_EXPORTED_INTENTS;

    public static final String SERVICE_EXPORTED_INTENTS_EXTRA = org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_EXPORTED_INTENTS_EXTRA;

    public static final String SERVICE_EXPORTED_INTERFACES = org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_EXPORTED_INTERFACES;

    //$NON-NLS-1$
    public static final String SERVICE_EXPORTED_INTERFACES_WILDCARD = "*";

    public static final String SERVICE_IMPORTED = org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED;

    public static final String SERVICE_IMPORTED_CONFIGS = org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED_CONFIGS;

    public static final String SERVICE_INTENTS = org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_INTENTS;

    public static final String SERVICE_PID = org.osgi.framework.Constants.SERVICE_PID;

    public static final String SERVICE_EXPORTED_CONTAINER_FACTORY_ARGUMENTS = RemoteConstants.SERVICE_EXPORTED_CONTAINER_FACTORY_ARGS;

    public static final String SERVICE_EXPORTED_CONTAINER_CONNECT_CONTEXT = RemoteConstants.SERVICE_EXPORTED_CONTAINER_CONNECT_CONTEXT;

    public static final String SERVICE_EXPORTED_CONTAINER_ID = RemoteConstants.SERVICE_EXPORTED_CONTAINER_ID;
}
