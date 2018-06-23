/*******************************************************************************
 * Copyright (c) 2010-2011 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.osgi.services.remoteserviceadmin;

public interface DebugOptions {

    //$NON-NLS-1$
    public static final String DEBUG = Activator.PLUGIN_ID + "/debug";

    public static final String EXCEPTIONS_CATCHING = DEBUG + //$NON-NLS-1$
    "/exceptions/catching";

    public static final String EXCEPTIONS_THROWING = DEBUG + //$NON-NLS-1$
    "/exceptions/throwing";

    //$NON-NLS-1$
    public static final String METHODS_ENTERING = DEBUG + "/methods/entering";

    //$NON-NLS-1$
    public static final String METHODS_EXITING = DEBUG + "/methods/exiting";

    public static final String REMOTE_SERVICE_ADMIN = DEBUG + //$NON-NLS-1$
    "/remoteserviceadmin";

    //$NON-NLS-1$
    public static final String TOPOLOGY_MANAGER = DEBUG + "/topologymanager";

    public static final String CONTAINER_SELECTOR = DEBUG + //$NON-NLS-1$
    "/containerselector";

    //$NON-NLS-1$
    public static final String METADATA_FACTORY = DEBUG + "/metadatafactory";

    public static final String ENDPOINT_DESCRIPTION_ADVERTISER = DEBUG + //$NON-NLS-1$
    "/endpointdescriptionadvertiser";

    public static final String ENDPOINT_DESCRIPTION_LOCATOR = DEBUG + //$NON-NLS-1$
    "/endpointdescriptionlocator";

    public static final String ENDPOINT_DESCRIPTION_READER = DEBUG + //$NON-NLS-1$
    "/endpointdescriptionreader";

    public static final String PACKAGE_VERSION_COMPARATOR = DEBUG + //$NON-NLS-1$
    "/packageversioncomparator";
}
