/*******************************************************************************
 * Copyright (c) 2010 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.dnssd;

public interface DnsSdDebugOptions {

    //$NON-NLS-1$
    public static final String DEBUG = Activator.PLUGIN_ID + "/debug";

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
