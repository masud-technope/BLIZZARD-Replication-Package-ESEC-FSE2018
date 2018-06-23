/*******************************************************************************
 * Copyright (c) 2007 Remy Suen, Composent, Inc., and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.irc;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    //$NON-NLS-1$
    private static final String BUNDLE_NAME = "org.eclipse.ecf.internal.provider.irc.messages";

    public static String IRCNamespace_EXCEPTION_INVALID_PROTOCOL;

    public static String IRCRootContainer_0;

    public static String IRCRootContainer_Connect_Timeout;

    public static String IRCRootContainer_Connecting;

    public static String IRCRootContainer_Connecting_To;

    public static String IRCRootContainer_EXCEPTION_CONNECTION_CANNOT_BE_NULL;

    public static String IRCRootContainer_EXCEPTION_TARGETID_CANNOT_BE_NULL;

    public static String IRCRootContainer_TopicChange;

    public static String IRCRootContainer_UserKicked;

    public static String IRCRootContainer_Disconnected;

    public static String IRCRootContainer_Error;

    public static String IRCRootContainer_Unknown_Message;

    public static String IRCRootContainer_Command_Error;

    public static String IRCRootContainer_Command_Unrecognized;

    public static String IRCRootContainer_353_Error;

    public static String IRCRootContainer_Whois;

    public static String IRCRootContainer_Server;

    public static String IRCRootContainer_Idle;

    public static String IRCRootContainer_Whois_End;

    public static String IRCRootContainer_Channels;

    public static String IRCRootContainer_Exception_Already_Connected;

    public static String IRCRootContainer_Exception_TargetID_Null;

    public static String IRCRootContainer_Exception_TargetID_Wrong_Type;

    public static String IRCRootContainer_Exception_Connect_Failed;

    public static String IRCRootContainer_Exception_Unexplained_Disconnect;

    public static String IRCRootContainer_Exception_Create_ChatRoom;

    public static String IRCRootContainer_Exception_Parse;

    public static String IRCRootContainer_Exception_Create_Not_Supported;

    public static String IRCRootContainer_CTCP_VERSION_Request;

    public static String IRCChannelContainer_Exception_Connecting;

    public static String IRCChannelContainer_Exception_TargetID_Null;

    public static String IRCChannelContainer_Exception_Connect_Failed;

    public static String IRCChannelContainer_Exception_Connect_Timeout;

    public static String IRCContainerInstantiator_Exception_CreateID_Failed;

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
