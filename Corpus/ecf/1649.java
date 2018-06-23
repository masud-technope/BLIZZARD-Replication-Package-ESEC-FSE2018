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
package org.eclipse.ecf.internal.irc.ui;

import org.eclipse.osgi.util.NLS;

/**
 *
 */
public class Messages extends NLS {

    //$NON-NLS-1$
    private static final String BUNDLE_NAME = "org.eclipse.ecf.internal.irc.ui.messages";

    public static String IRCConnectWizardPage_CONNECTID_DEFAULT;

    public static String IRCConnectWizardPage_CONNECTID_EXAMPLE;

    public static String IRCConnectWizardPage_CONNECTID_LABEL;

    public static String IRCConnectWizardPage_PASSWORD_INFO;

    public static String IRCConnectWizardPage_PASSWORD_LABEL;

    public static String IRCConnectWizardPage_CONNECT_TIMEOUT;

    public static String IRCConnectWizardPage_CONNECT_TIMEOUT_INFO;

    public static String IRCConnectWizardPage_STATUS_MESSAGE_EMPTY;

    public static String IRCConnectWizardPage_STATUS_MESSAGE_MALFORMED;

    public static String IRCConnectWizardPage_WIZARD_PAGE_DESCRIPTION;

    public static String IRCConnectWizardPage_WIZARD_PAGE_TITLE;

    public static String IRCUI_DEPART_CONFIRM_MESSAGE;

    public static String IRCUI_DEPART_CONFIRM_TITLE;

    public static String IRCUI_DISCONNECT_CONFIRM_MESSAGE;

    public static String IRCUI_DISCONNECT_CONFIRM_TITLE;

    public static String IRCUI_JOIN_COMMAND;

    public static String IRCUI_PART_COMMAND;

    public static String IRCUI_QUIT_COMMAND;

    public static String IRCConnectWizard_WIZARD_TITLE;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private  Messages() {
    }
}
