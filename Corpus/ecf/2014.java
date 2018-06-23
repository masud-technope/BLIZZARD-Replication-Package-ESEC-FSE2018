/****************************************************************************
 * Copyright (c) 2007, 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.docshare2;

import org.eclipse.osgi.util.NLS;

/**
 *
 */
public class Messages extends NLS {

    //$NON-NLS-1$
    private static final String BUNDLE_NAME = "org.eclipse.ecf.internal.docshare2.messages";

    public static String DocShare_EXCEPTION_DESERIALIZING_MESSAGE0;

    public static String DocShare_EXCEPTION_RECEIVING_MESSAGE_MESSAGE;

    public static String DocShare_EXCEPTION_RECEIVING_MESSAGE_TITLE;

    public static String DocShare_EXCEPTION_SEND_MESSAGE;

    public static String DocShare_RemoteSelection;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private  Messages() {
    // nothing to do
    }
}
