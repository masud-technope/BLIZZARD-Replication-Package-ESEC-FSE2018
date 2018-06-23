/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.example.clients.applications;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.example.clients.IMessageReceiver;
import org.eclipse.ecf.example.clients.XMPPChatClient;
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.IPresenceListener;
import org.eclipse.ecf.presence.im.IChatID;
import org.eclipse.ecf.presence.im.IChatMessage;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class ChatRobotApplication implements IApplication, IMessageReceiver, IPresenceListener {

    // this map contains the account -> XMPPID.  Items are added to it via the IPresenceListener.handlePresence method
    private Map rosterUsers = Collections.synchronizedMap(new HashMap());

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.
	 * IApplicationContext)
	 */
    public Object start(IApplicationContext context) throws Exception {
        // process program arguments
        String[] originalArgs = (String[]) context.getArguments().get("application.args");
        if (originalArgs.length < 3) {
            System.out.println("Parameters:  <senderAccount> <senderPassword> <targetAccount> [<message>].  e.g. sender@gmail.com senderpassword receiver@gmail.com \"Hello there\"");
            return new Integer(-1);
        }
        String message = null;
        if (originalArgs.length > 3)
            message = originalArgs[3];
        // Create client
        XMPPChatClient client = new XMPPChatClient(this, this);
        // connect
        client.connect(originalArgs[0], originalArgs[1]);
        // Wait for 5s for the roster/presence information to be received
        final Object lock = new Object();
        synchronized (lock) {
            lock.wait(5000);
        }
        // Get desired user ID from rosterUsers map.  This is just looking for a user that's active and on our contacts list
        ID targetID = (ID) rosterUsers.get(originalArgs[2]);
        if (targetID == null) {
            System.out.println("target user=" + originalArgs[2] + " is not on active on your contacts list.  Cannot send message to this user");
            return new Integer(0);
        }
        // Construct message
        String msgToSend = (message == null) ? "Hi, I'm an ECF chat robot." : message;
        System.out.println("ECF chat robot example sending to targetAccount=" + originalArgs[2] + " message=" + msgToSend);
        // Send message to targetID
        client.sendChat(targetID, msgToSend);
        // Close up nicely and return
        client.close();
        return IApplication.EXIT_OK;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
    public void stop() {
    }

    public void handleMessage(IChatMessage chatMessage) {
        System.out.println("handleMessage(" + chatMessage + ")");
    }

    /**
	 * @since 2.0
	 */
    public void handlePresence(ID fromID, IPresence presence) {
        System.out.println("handlePresence fromID=" + fromID + " presence=" + presence);
        IChatID fromChatID = (IChatID) fromID.getAdapter(IChatID.class);
        if (fromChatID != null) {
            rosterUsers.put(fromChatID.getUsername() + "@" + fromChatID.getHostname(), fromID);
        }
    }
}
