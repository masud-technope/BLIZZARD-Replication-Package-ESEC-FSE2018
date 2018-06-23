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
package org.eclipse.ecf.internal.provider.xmpp;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    //$NON-NLS-1$
    private static final String BUNDLE_NAME = "org.eclipse.ecf.internal.provider.xmpp.messages";

    public static String XMPPChatRoomContainer_EXCEPTION_CALLBACKHANDLER;

    public static String XMPPChatRoomContainer_Exception_Connect_Wrong_Type;

    public static String XMPPChatRoomContainer_EXCEPTION_CREATING_ROOM_ID;

    public static String XMPPChatRoomContainer_EXCEPTION_JOINING_ROOM;

    public static String XMPPChatRoomContainer_EXCEPTION_NOT_CONNECTED;

    public static String XMPPChatRoomContainer_EXCEPTION_SEND_MESSAGE;

    public static String XMPPChatRoomContainer_EXCEPTION_TARGET_USER_NOT_NULL;

    public static String XMPPChatRoomContainer_NAME_CALLBACK_NICK;

    public static String XMPPChatRoomManager_EXCEPTION_CONTAINER_DISCONNECTED;

    public static String XMPPChatRoomManager_EXCEPTION_CREATING_CHAT_CONTAINER;

    public static String XMPPChatRoomManager_EXCEPTION_NO_ROOM_INFO;

    public static String XMPPChatRoomManager_EXCEPTION_ROOM_CANNOT_BE_NULL;

    public static String XMPPChatRoomManager_ROOM_NOT_FOUND;

    public static String XMPPContainer_EXCEPTION_ADDING_SHARED_OBJECT;

    public static String XMPPContainer_EXCEPTION_DESERIALIZED_OBJECT_NULL;

    public static String XMPPContainer_EXCEPTION_HANDLING_ASYCH_EVENT;

    public static String XMPPContainer_EXCEPTION_INVALID_RESPONSE_FROM_SERVER;

    public static String XMPPContainer_UNEXPECTED_EVENT;

    public static String XMPPContainer_UNEXPECTED_XMPP_MESSAGE;

    public static String XMPPContainer_UNRECOGONIZED_CONTAINER_MESSAGE;

    public static String XMPPContainer_UNRECOGONIZED_SEARCH_SERVICE;

    public static String XMPPID_EXCEPTION_HOST_PORT_NOT_VALID;

    public static String XMPPID_EXCEPTION_INVALID_PORT;

    public static String XMPPID_EXCEPTION_XMPPID_USERNAME_NOT_NULL;

    public static String XMPPIncomingFileTransfer_Progress_Data;

    public static String XMPPIncomingFileTransfer_Exception_User_Cancelled;

    public static String XMPPIncomingFileTransfer_Status_Transfer_Completed_OK;

    public static String XMPPIncomingFileTransfer_Status_Transfer_Exception;

    public static String XMPPNamespace_EXCEPTION_ID_CREATE;

    public static String XMPPRoomNamespace_EXCEPTION_ID_CREAT;

    public static String XMPPRoomNamespace_EXCEPTION_INVALID_ARGUMENTS;

    public static String XMPPSNamespace_EXCEPTION_ID_CREATE;

    public static String XMPPUserSearchManager_JOB;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private  Messages() {
    }
}
