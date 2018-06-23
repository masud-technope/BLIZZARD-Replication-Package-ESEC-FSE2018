/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tutorial.basic;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.example.clients.IMessageReceiver;
import org.eclipse.ecf.example.clients.XMPPChatClient;
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.IPresenceListener;
import org.eclipse.ecf.presence.im.IChatMessage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class Client4 extends XMPPChatClient {

    private static final String DEFAULT_PASSWORD = "eclipsecon";

    private static final String DEFAULT_USERNAME = "eclipsecon@ecf.eclipse.org";

    public  Client4() {
        super(new IMessageReceiver() {

            public void handleMessage(final IChatMessage chatMessage) {
                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        MessageDialog.openInformation(null, "XMPP message from " + chatMessage.getFromID().getName(), chatMessage.getBody());
                    }
                });
            }
        }, new IPresenceListener() {

            public void handlePresence(ID fromID, IPresence presence) {
                System.out.println("handlePresence(" + fromID + "," + presence + ")");
            }
        });
    }

    public void createAndConnect() throws ECFException {
        super.connect(DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }
}
