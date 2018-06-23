/*******************************************************************************
 * Copyright (c) 2009 Versant Corp and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.discovery.ui.userinput;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    //$NON-NLS-1$
    private static final String BUNDLE_NAME = "org.eclipse.ecf.discovery.ui.userinput.messages";

    public static String UserInputNameSpace_INVALID_PARAMS;

    public static String LookupHandler_DIALOG_LABEL;

    public static String LookupHandler_DIALOG_TITLE;

    public static String LookupHandler_EXEC_FAILED;

    public static String LookupHandler_HOSTNAME_UNABLE_TO_RESOLVE;

    public static String LookupHandler_INVALID_HOSTNAME;

    public static String LookupHandler_INVALID_PORT;

    public static String LookupHandler_UNKNOWN_HOSTNAME;

    public static String LookupHandler_RESOLVING;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private  Messages() {
    }
}
