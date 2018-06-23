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
package org.eclipse.ecf.internal.provider.datashare;

public interface DatashareProviderDebugOptions {

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
}
