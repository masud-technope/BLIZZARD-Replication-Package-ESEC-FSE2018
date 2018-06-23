/*******************************************************************************
 * Copyright (c) 2005, 2008 Remy Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *    Cagatay Calli <ccalli@gmail.com> - https://bugs.eclipse.org/bugs/show_bug.cgi?id=196812
 *    David Pochet <pochet.david@wanadoo.fr> - https://bugs.eclipse.org/bugs/show_bug.cgi?id=195275
 ******************************************************************************/
package org.eclipse.ecf.protocol.msn;

import java.io.*;
import java.net.ConnectException;
import java.net.URLDecoder;
import java.util.ArrayList;
import org.eclipse.ecf.protocol.msn.events.ISessionListener;
import org.eclipse.ecf.protocol.msn.internal.encode.*;
import org.eclipse.ecf.protocol.msn.internal.net.ClientTicketRequest;

/**
 * <p>
 * The NotificationSession manages all incoming and outgoing packets that
 * concerns status changes in contacts, client pings, challenge strings, and
 * others.
 * </p>
 * 
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still
 * under development and expected to change significantly before reaching
 * stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 * </p>
 */
final class NotificationSession extends DispatchSession {

    private final ContactList list;

    /**
	 * The ClientTicketRequest that this NotificationSession will use to obtain
	 * the client ticket associated with the user's MSN email address username
	 * and password.
	 */
    private ClientTicketRequest request;

    /**
	 * The ResponseCommand used to process responses from the server.
	 */
    private ResponseCommand response;

    private Thread pingingThread;

    /**
	 * The address of the alternate server that has been read in that the client
	 * should try redirecting its notification session to connect to.
	 */
    private String alternateServer;

    /**
	 * The user's MSN email address.
	 */
    private String username;

    /**
	 * Creates a new NotificationSession that will connect to the given host.
	 * 
	 * @param client
	 *            the MsnClient to hook onto
	 */
     NotificationSession(MsnClient client) {
        super(client);
        list = client.getContactList();
        listeners = new ArrayList();
        request = new ClientTicketRequest();
    }

    /**
	 * Returns whether the user has connected with the notification server
	 * successfully or not. If the connecting process failed, {@link #reset()}
	 * should be called so that a connection attempt can be made to the server
	 * that the user has been redirected to.
	 * 
	 * @param userEmail
	 *            the user's MSN email address login
	 * @param password
	 *            the user's password
	 * @return <tt>true</tt> if the login completed successfully,
	 *         <tt>false</tt> otherwise
	 * @throws IOException
	 *             If an I/O error occurs while attempting to authenticate with
	 *             the servers
	 */
    boolean login(String userEmail, String password) throws IOException {
        response = connect(userEmail);
        if (//$NON-NLS-1$
        response.getCommand().equals("USR")) {
            String ticket = request.getTicket(userEmail, password, response.getParam(3));
            password = null;
            if (ticket == null) {
                throw new //$NON-NLS-1$
                ConnectException(//$NON-NLS-1$
                "Wrong username and/or password.");
            }
            //$NON-NLS-1$ //$NON-NLS-2$
            write("USR", "TWN S " + ticket);
            ticket = null;
            String input = super.read();
            if (//$NON-NLS-1$
            !input.startsWith("USR")) {
                throw new //$NON-NLS-1$
                ConnectException(//$NON-NLS-1$
                "An error occurred while attempting to authenticate with the Tweener server.");
            }
            retrieveBuddyList();
            this.username = userEmail;
            return true;
        } else if (//$NON-NLS-1$
        !response.getCommand().equals("XFR")) {
            //$NON-NLS-1$
            throw new ConnectException("Unable to connect to the MSN server.");
        } else {
            alternateServer = response.getParam(2);
            return false;
        }
    }

    private void retrieveBuddyList() throws IOException {
        //$NON-NLS-1$ //$NON-NLS-2$
        write("SYN", "0 0");
        //$NON-NLS-1$
        BufferedReader reader = new BufferedReader(new InputStreamReader(getInputStream(), "UTF-8"));
        String input = reader.readLine();
        while (//$NON-NLS-1$
        input == null || !input.startsWith("SYN")) {
            input = reader.readLine();
        }
        String[] split = StringUtils.splitOnSpace(input);
        int contacts = Integer.parseInt(split[4]);
        while (//$NON-NLS-1$
        !input.startsWith("LST")) {
            if (//$NON-NLS-1$
            input.startsWith("PRP MFN")) {
                //$NON-NLS-1$
                client.internalSetDisplayName(//$NON-NLS-1$
                StringUtils.splitSubstring(input, " ", 2));
            } else if (//$NON-NLS-1$
            input.startsWith("LSG")) {
                split = StringUtils.splitOnSpace(input);
                list.addGroup(split[2], new Group(URLDecoder.decode(split[1])));
            }
            input = reader.readLine();
        }
        int count = 0;
        while (true) {
            if (//$NON-NLS-1$
            input.startsWith("LST")) {
                count++;
                String[] contact = StringUtils.splitOnSpace(input);
                String email = contact[1].substring(2);
                // Check that email address is valid
                if (email.indexOf('@') != -1) {
                    switch(contact.length) {
                        case 3:
                            list.internalAddContact(email, email);
                            break;
                        case 5:
                            list.addContact(email, email, contact[3].substring(2));
                            break;
                        default:
                            list.addContact(contact[2].substring(2), email, contact[3].substring(2), contact[5]);
                            break;
                    }
                }
                if (count == contacts) {
                    break;
                }
            }
            input = reader.readLine();
        }
        //$NON-NLS-1$ //$NON-NLS-2$
        write("CHG", client.getStatus().getLiteral() + " 268435488");
        idle();
        ping();
    }

    /**
	 * This method is invoked with another user has invited the client to a
	 * switchboard session. The created session will answer back and then invoke
	 * the {@link Session#idle()} method to begin processing incoming and
	 * outgoing requests.
	 * 
	 * @param data
	 *            a String array containing the request that the other user has
	 *            sent to the client
	 * @throws IOException
	 *             If an I/O error occurs while the ChatSession is created to
	 *             handle this request.
	 */
    private void processSwitchboardRequest(String[] data) throws IOException {
        ChatSession ss = new ChatSession(data[2], client);
        //$NON-NLS-1$
        ss.write("ANS", username + ' ' + data[4] + ' ' + data[1]);
        ss.read();
        fireSwitchboardConnectedEvent(ss);
        // ss.read();
        ss.idle();
    }

    /**
	 * Sends a request to the notification server for a switchboard server's
	 * information.
	 * 
	 * @return the ResponseCommand that represents the notification server's
	 *         output
	 * @throws IOException
	 *             If an I/O error occurred while reading or writing data.
	 */
    ResponseCommand getChatSession() throws IOException {
        if (client.getStatus() == Status.APPEAR_OFFLINE || client.getStatus() == Status.OFFLINE) {
            //$NON-NLS-1$
            throw new ConnectException("Switchboards cannot be created when the user is hidden or offline.");
        }
        //$NON-NLS-1$ //$NON-NLS-2$
        write("XFR", "SB");
        String command = response.getCommand();
        while (//$NON-NLS-1$
        command == null || !command.equals("XFR")) {
            command = response.getCommand();
        }
        return response;
    }

    /**
	 * Closes the connection to the original host and then open up a new
	 * connection to the redirected host. A new call to
	 * {@link #login(String, String)} should be made after this method has
	 * returned.
	 */
    void reset() throws IOException {
        close();
        openSocket(alternateServer);
        request.setCancelled(false);
    }

    /**
	 * Read the contents of the packet being sent from the server and handle any
	 * events accordingly.
	 */
    String read() throws IOException {
        String input = super.read();
        if (input == null) {
            return null;
        }
        if (//$NON-NLS-1$
        input.indexOf("ILN") != -1) {
            //$NON-NLS-1$
            String[] events = StringUtils.split(input, "\r\n");
            for (int i = 0; i < events.length; i++) {
                if (!//$NON-NLS-1$
                events[i].trim().equals(//$NON-NLS-1$
                "") && //$NON-NLS-1$
                events[i].substring(1, 3).equals(//$NON-NLS-1$
                "LN")) {
                    String[] sub = //$NON-NLS-1$
                    StringUtils.split(//$NON-NLS-1$
                    events[i], //$NON-NLS-1$
                    " ", //$NON-NLS-1$
                    3);
                    String[] split = StringUtils.splitOnSpace(sub[2]);
                    changeContactInfo(split);
                }
            }
        } else if (//$NON-NLS-1$
        input.indexOf("FLN") != -1) {
            //$NON-NLS-1$
            String[] events = StringUtils.split(input, "\r\n");
            for (int i = 0; i < events.length; i++) {
                if (//$NON-NLS-1$
                events[i].startsWith("FLN")) {
                    setContactToOffline(events[i]);
                }
            }
        } else if (//$NON-NLS-1$
        input.indexOf("LN") != -1) {
            //$NON-NLS-1$
            String[] events = StringUtils.split(input, "\r\n");
            for (int i = 0; i < events.length; i++) {
                if (//$NON-NLS-1$
                events[i].substring(0, 3).equals("NLN")) {
                    String[] sub = StringUtils.splitOnSpace(events[i].substring(4));
                    changeContactInfo(sub);
                } else if (//$NON-NLS-1$
                events[i].substring(1, 3).equals("LN")) {
                    String[] sub = //$NON-NLS-1$
                    StringUtils.split(//$NON-NLS-1$
                    events[i], //$NON-NLS-1$
                    " ", //$NON-NLS-1$
                    3);
                    String[] split = StringUtils.splitOnSpace(sub[2]);
                    changeContactInfo(split);
                }
            }
        } else if (//$NON-NLS-1$
        input.indexOf("CHL") != -1) {
            // the read input is a challenge string
            //			String query = Challenge.createQuery(StringUtils.splitSubstring(input, " ", 2)); //$NON-NLS-1$
            //$NON-NLS-1$
            String query = MD5HashImpl.getMD5(StringUtils.splitSubstring(input, " ", 2));
            //			Even this is wrong. we need to create a 64bit hash out of 128bit hash according to http://bit.ly/wdzdkd
            write("QRY", //$NON-NLS-1$ //$NON-NLS-2$
            Challenge.PRODUCT_ID + ' ' + query.length() + "\r\n" + query, false);
        } else if (//$NON-NLS-1$
        input.indexOf("RNG") != -1) {
            processSwitchboardRequest(StringUtils.splitOnSpace(input));
        }
        if (//$NON-NLS-1$
        input.indexOf("XFR") != -1) {
            //$NON-NLS-1$
            String[] split = StringUtils.split(input, "\r\n");
            for (int i = 0; i < split.length; i++) {
                if (//$NON-NLS-1$
                split[i].startsWith("XFR")) {
                    response.process(split[i]);
                }
            }
        }
        if (//$NON-NLS-1$
        input.indexOf("UBX") != -1) {
            //$NON-NLS-1$
            String[] split = StringUtils.split(input, "\r\n");
            for (int i = 0; i < split.length; i++) {
                if (//$NON-NLS-1$
                split[i].startsWith("UBX")) {
                    processContactData(split, i);
                }
            }
        }
        if (//$NON-NLS-1$
        input.indexOf("ADC") != -1) {
            //$NON-NLS-1$
            String[] split = StringUtils.split(input, "\r\n");
            for (int i = 0; i < split.length; i++) {
                if (//$NON-NLS-1$
                split[i].startsWith("ADC")) {
                    String[] subSplit = StringUtils.splitOnSpace(split[i]);
                    if (//$NON-NLS-1$
                    subSplit[2].equals(//$NON-NLS-1$
                    "FL")) {
                        processContactAdded(subSplit[3].substring(2), subSplit[4].substring(2), subSplit[5].substring(2));
                    } else if (//$NON-NLS-1$
                    subSplit[2].equals(//$NON-NLS-1$
                    "RL")) {
                        processContactAddedUser(subSplit[3].substring(2));
                    }
                }
            }
        }
        if (//$NON-NLS-1$
        input.indexOf("REM") != -1) {
            //$NON-NLS-1$
            String[] split = StringUtils.split(input, "\r\n");
            for (int i = 0; i < split.length; i++) {
                if (//$NON-NLS-1$
                split[i].startsWith("REM")) {
                    String[] subSplit = StringUtils.splitOnSpace(split[i]);
                    if (//$NON-NLS-1$
                    subSplit[2].equals(//$NON-NLS-1$
                    "FL")) {
                        processContactRemoved(subSplit[3]);
                    } else if (//$NON-NLS-1$
                    subSplit[2].equals(//$NON-NLS-1$
                    "RL")) {
                        processContactRemovedUser(subSplit[3]);
                    }
                }
            }
        }
        if (//$NON-NLS-1$
        input.indexOf("OUT OTH") != -1) {
            //$NON-NLS-1$
            String[] split = StringUtils.split(input, "\r\n");
            for (int i = 0; i < split.length; i++) {
                if (//$NON-NLS-1$
                split[i].startsWith("OUT OTH")) {
                    close();
                    break;
                }
            }
        }
        return input;
    }

    void close() {
        request.setCancelled(true);
        if (pingingThread != null) {
            pingingThread.interrupt();
        }
        super.close();
    }

    /**
	 * Create a new thread that will ping the host every sixty seconds to keep
	 * this connection alive.
	 */
    private void ping() {
        pingingThread = new Thread() {

            public void run() {
                try {
                    while (true) {
                        sleep(60000);
                        //$NON-NLS-1$
                        write("PNG");
                    }
                } catch (IOException e) {
                } catch (InterruptedException e) {
                }
            }
        };
        pingingThread.start();
    }

    private void processContactAdded(String email, String contactName, String guid) {
        list.addContact(email, contactName, guid);
    }

    private void processContactRemoved(String guid) {
        list.fireContactRemoved(guid);
    }

    private void processContactAddedUser(String email) {
        list.fireContactAddedUser(email);
    }

    private void processContactRemovedUser(String email) {
        list.fireContactRemovedUser(email);
    }

    /**
	 * Checks whether a contact has changed his or her personal message or
	 * current media.
	 * 
	 * @param eventString
	 *            a String array containing the notification server's
	 *            information
	 * @param index
	 *            the index of the String array that processing should begin
	 */
    private void processContactData(String[] eventString, int index) {
        if (eventString.length == index + 1 || //$NON-NLS-1$
        StringUtils.splitSubstring(eventString[index], " ", 2).equals(//$NON-NLS-1$
        "0")) {
            eventString = StringUtils.splitOnSpace(eventString[index]);
            Contact contact = list.getContact(eventString[1]);
            if (contact != null) {
                //$NON-NLS-1$
                contact.setPersonalMessage(//$NON-NLS-1$
                "");
            }
            return;
        }
        String data = eventString[index + 1];
        eventString = StringUtils.splitOnSpace(eventString[index]);
        Contact contact = list.getContact(eventString[1]);
        contact.setPersonalMessage(//$NON-NLS-1$
        data.substring(//$NON-NLS-1$
        data.indexOf("<PSM>") + 5, //$NON-NLS-1$
        data.indexOf("</PSM>")));
    // TODO: set media
    }

    /**
	 * Changes the contact's status based on the string array that has been
	 * received from the notification server.
	 * 
	 * @param eventString
	 *            a formatted String literal obtained from an incoming message
	 */
    private void changeContactInfo(String[] eventString) {
        // we are not interested in our own changes
        if (!eventString[1].equals(client.getUserEmail())) {
            Contact contact = list.getContact(eventString[1]);
            contact.setStatus(Status.getStatus(eventString[0]));
            contact.setDisplayName(eventString[2]);
        }
    }

    /**
	 * Changes the contact specified by the notification server to be an offline
	 * status.
	 * 
	 * @param eventString
	 *            a formatted String literal obtained from an incoming message
	 */
    private void setContactToOffline(String eventString) {
        //$NON-NLS-1$
        String email = StringUtils.splitSubstring(eventString, " ", 1);
        list.getContact(email).setStatus(Status.OFFLINE);
    }

    /**
	 * Fires an event to all attached notification listeners to indicate that
	 * the specified chat session has been connected to.
	 * 
	 * @param session
	 *            the chat session that has been connected to
	 */
    private void fireSwitchboardConnectedEvent(ChatSession session) {
        synchronized (listeners) {
            for (int i = 0; i < listeners.size(); i++) {
                ((ISessionListener) listeners.get(i)).sessionConnected(session);
            }
        }
    }

    /**
	 * Adds the provided ISessionListener to this notification session.
	 * 
	 * @param listener
	 *            the listener to add
	 */
    public void addSessionListener(ISessionListener listener) {
        if (listener != null) {
            synchronized (listeners) {
                if (!listeners.contains(listener)) {
                    listeners.add(listener);
                }
            }
        }
    }

    /**
	 * Removes the specified ISessionListener from this notification session.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
    public void removeSessionListener(ISessionListener listener) {
        if (listener != null) {
            synchronized (listeners) {
                listeners.remove(listener);
            }
        }
    }
}
