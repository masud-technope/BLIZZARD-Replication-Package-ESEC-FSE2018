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
package org.eclipse.ecf.internal.telephony.call.ui;

import org.eclipse.osgi.util.NLS;

/**
 *
 */
public class Messages extends NLS {

    //$NON-NLS-1$
    private static final String BUNDLE_NAME = "org.eclipse.ecf.internal.telephony.call.ui.messages";

    public static String CallAction_Exception_CallAction_Run;

    public static String CallAction_Exception_Container_Not_Call_API;

    public static String CallAction_Title_Call_Failed;

    public static String CallAction_Message_Call_Failed;

    public static String CallAction_Initiate_Call_Title;

    public static String CallAction_Initiate_Call_Message;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private  Messages() {
    // private null constructor
    }
}
