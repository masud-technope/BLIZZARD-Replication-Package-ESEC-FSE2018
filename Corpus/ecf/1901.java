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
package org.eclipse.ecf.internal.provider.filetransfer.scp;

import org.eclipse.osgi.util.NLS;

/**
 *
 */
public class Messages extends NLS {

    //$NON-NLS-1$
    private static final String BUNDLE_NAME = "org.eclipse.ecf.internal.provider.filetransfer.scp.messages";

    public static String ScpOutgoingFileTransfer_EXCEPTION_CONNECTING;

    public static String ScpOutgoingFileTransfer_EXCEPTION_SETTING_KNOWN_HOSTS;

    public static String ScpOutgoingFileTransfer_EXCEPTION_SETTING_SSH_IDENTITY;

    public static String ScpOutgoingFileTransfer_PASSPHRASE_PROMPT;

    public static String ScpOutgoingFileTransfer_PASSWORD_PROMPT;

    public static String ScpOutgoingFileTransfer_USERNAME_PROMPT;

    public static String ScpRetrieveFileTransfer_EXCEPTION_CONNECTING;

    public static String ScpRetrieveFileTransfer_EXCEPTION_ERROR_READING_FILE;

    public static String ScpRetrieveFileTransfer_EXCEPTION_SCP_PROTOCOL;

    public static String ScpUtil_EXCEPTION_UNKNOWN_SCP_ERROR;

    public static String ScpUtil_EXCEPTION_USERNAME_NOT_NULL;

    public static String ScpUtil_SCP_ERROR;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private  Messages() {
    //
    }
}
