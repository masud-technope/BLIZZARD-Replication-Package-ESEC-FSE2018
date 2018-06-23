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
package org.eclipse.ecf.internal.discovery;

public class DiscoveryDebugOption {

    //$NON-NLS-1$
    public static final String DEBUG = DiscoveryPlugin.PLUGIN_ID + "/debug";

    //$NON-NLS-1$
    public static final String EXCEPTIONS_CATCHING = DEBUG + "/exceptions/catching";

    //$NON-NLS-1$
    public static final String EXCEPTIONS_THROWING = DEBUG + "/exceptions/throwing";

    //$NON-NLS-1$
    public static final String METHODS_ENTERING = DEBUG + "/methods/entering";

    //$NON-NLS-1$
    public static final String METHODS_EXITING = DEBUG + "/methods/exiting";

    //$NON-NLS-1$
    public static final String METHODS_TRACING = DEBUG + "/methods/tracing";
}
