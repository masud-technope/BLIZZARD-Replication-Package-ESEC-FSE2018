/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.docshare;

import org.eclipse.osgi.util.NLS;

/**
 *
 */
public class Messages extends NLS {

    //$NON-NLS-1$
    private static final String BUNDLE_NAME = "org.eclipse.ecf.internal.docshare.messages";

    public static String DocShare_EDITOR_SHARE_POPUP_MESSAGE;

    public static String DocShare_EDITOR_SHARE_POPUP_TITLE;

    public static String DocShare_ERROR_STARTING_EDITOR_MESSAGE;

    public static String DocShare_ERROR_STARTING_EDITOR_TITLE;

    public static String DocShare_EXCEPTION_DESERIALIZING_MESSAGE0;

    public static String DocShare_EXCEPTION_HANDLE_MESSAGE;

    public static String DocShare_EXCEPTION_INVALID_MESSAGE;

    public static String DocShare_EXCEPTION_RECEIVING_MESSAGE_MESSAGE;

    public static String DocShare_EXCEPTION_RECEIVING_MESSAGE_TITLE;

    public static String DocShare_EXCEPTION_SEND_MESSAGE;

    public static String DocShare_REMOTE_USER_STOPPED;

    public static String DocShare_RemoteSelection;

    public static String DocShare_STOP_SHARED_EDITOR_REMOTE;

    public static String DocShare_STOP_SHARED_EDITOR_TITLE;

    public static String DocShare_STOP_SHARED_EDITOR_US;

    public static String DocShareRosterMenuContributionItem_SHARE_EDITOR_MENU_TEXT;

    public static String DocShareRosterMenuContributionItem_STOP_SHARE_EDITOR_MENU_TEXT;

    public static String DocShareRosterMenuHandler_DOCSHARE_START_ERROR_TITLE;

    public static String DocShareRosterMenuHandler_ERROR_EDITOR_ALREADY_SHARING;

    public static String DocShareRosterMenuHandler_ERROR_NO_SENDER;

    public static String DocShareRosterMenuHandler_ERROR_NOT_CONNECTED;

    public static String DocShareRosterMenuHandler_EXCEPTION_EDITOR_NOT_TEXT;

    public static String DocShareRosterMenuHandler_NO_FILENAME_WITH_CONTENT;

    public static String ECFStart_ERROR_CONTAINER_MANAGER_NOT_AVAILABLE;

    public static String ECFStart_ERROR_DOCUMENT_SHARE_NOT_CREATED;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private  Messages() {
    // nothing to do
    }
}
