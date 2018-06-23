/*******************************************************************************
 * Copyright (c) 2005, 2007 Remy Suen
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.protocol.msn;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Iterator;
import org.eclipse.ecf.protocol.msn.events.ISessionListener;
import org.eclipse.ecf.protocol.msn.internal.encode.ResponseCommand;
import org.eclipse.ecf.protocol.msn.internal.encode.StringUtils;

/**
 * <p>
 * The MsnClient class allows a developer to easily create a client that will
 * authenticate the user and establish a connection with the MSN servers.
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
public final class MsnClient {

    /**
	 * The default hostname that will be used to connect to the MSN servers -
	 * messenger.hotmail.com
	 */
    //$NON-NLS-1$
    private static final String HOSTNAME = "messenger.hotmail.com";

    /**
	 * The default port that will be used to connect to the MSN servers - 1863
	 */
    private static final int PORT = 1863;

    /**
	 * The NotificationSession that will be connect to the notification server
	 * to handle most non-messaging related incoming and outgoing requests.
	 */
    private NotificationSession notification;

    /**
	 * The list of contacts that are on this user's list.
	 */
    private final ContactList list;

    /**
	 * The user's email address.
	 */
    private String username;

    /**
	 * The name the user displays to other contacts.
	 */
    private String displayName;

    /**
	 * The hostname to use to connect to the dispatch server.
	 */
    private final String hostname;

    /**
	 * The user's personal message.
	 */
    //$NON-NLS-1$
    private String personalMessage = "";

    /**
	 * The media that the user is currently playing.
	 */
    //$NON-NLS-1$
    private final String currentMedia = "";

    /**
	 * The port to use to connect to the dispatch server.
	 */
    private final int port;

    /**
	 * The current status of the user.
	 */
    private Status status;

    /**
	 * Instantiate a new MsnClient that defaults to setting the user as being
	 * online and available when signing in.
	 * 
	 */
    public  MsnClient() {
        this(Status.ONLINE);
    }

    /**
	 * Instantiate a new MsnClient that set the user to the specified status
	 * when signing in.
	 * 
	 * @param initialStatus
	 *            the status that a user would like to sign on to the servers
	 *            as, refer to {@link Status#ONLINE} and other static variables
	 *            for the available options.
	 */
    public  MsnClient(Status initialStatus) {
        status = initialStatus;
        hostname = HOSTNAME;
        port = PORT;
        list = new ContactList(this);
        notification = new NotificationSession(this);
    }

    /**
	 * Connects the client to the MSN servers.
	 * 
	 * @param userEmail
	 *            the user's email address that is associated with an MSN
	 *            account
	 * @param password
	 *            the email's corresponding password
	 * @throws IOException
	 *             If an I/O error occurred while connecting to the dispatch or
	 *             notification servers.
	 */
    public void connect(String userEmail, String password) throws IOException {
        this.username = userEmail;
        final DispatchSession dispatch = new DispatchSession(hostname, port);
        // get the address of the notification server by first authenticating
        // ourselves
        final String address = dispatch.authenticate(userEmail);
        // close the session
        dispatch.close();
        // connect the notification session to the received address
        notification.openSocket(address);
        try {
            // keep looping until we've connected successfully
            while (!notification.login(userEmail, password)) {
                notification.reset();
            }
        } catch (final RuntimeException e) {
            if (!notification.isClosed()) {
                throw e;
            }
        } catch (final IOException e) {
            if (!notification.isClosed()) {
                throw e;
            }
        }
    }

    /**
	 * Disconnects the user from the MSN servers. Please note that any
	 * {@link ChatSession}s that may have been created since the client was
	 * instantiated are not disconnected automatically in this method.
	 */
    public void disconnect() {
        if (notification != null) {
            try {
                //$NON-NLS-1$
                notification.write(//$NON-NLS-1$
                "OUT");
            } catch (final Exception e) {
            }
            notification.close();
        }
        notification = null;
    }

    /**
	 * Changes the user's status to the provided status flag.
	 * 
	 * @param status
	 *            the status that the user wishes to change to
	 * @throws IOException if some problem setting status (e.g. disconnected).
	 */
    public void setStatus(Status status) throws IOException {
        if (this.status != status) {
            if (status == Status.OFFLINE) {
                disconnect();
            } else {
                //$NON-NLS-1$ //$NON-NLS-2$
                notification.write("CHG", status.getLiteral() + " 268435488");
            }
            this.status = status;
        }
    }

    /**
	 * Returns the status that the user is currently in.
	 * 
	 * @return the user's current status
	 */
    public Status getStatus() {
        return status;
    }

    void add(String email, String userName) throws IOException {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        notification.write("ADC", "FL N=" + email + " F=" + userName);
    }

    void remove(Contact contact) throws IOException {
        final String guid = contact.getGuid();
        for (final Iterator it = contact.getGroups().iterator(); it.hasNext(); ) {
            notification.write("REM", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            "FL " + guid + " " + list.getGuid((Group) it.next()));
        }
        //$NON-NLS-1$ //$NON-NLS-2$
        notification.write("REM", "FL " + guid);
    }

    void remove(String guid) throws IOException {
        //$NON-NLS-1$ //$NON-NLS-2$
        notification.write("RMG", "FL " + guid);
    }

    /**
	 * Returns the contact list that is associated with this client.
	 * 
	 * @return this client's contact list
	 */
    public ContactList getContactList() {
        return list;
    }

    /**
	 * Creates a {@link ChatSession} to connect to the specified contact.
	 * 
	 * @param email
	 *            the contact to connect to
	 * @return the created ChatSession
	 * @throws IOException
	 *             If an I/O error occurred
	 */
    public ChatSession createChatSession(String email) throws IOException {
        final ResponseCommand cmd = notification.getChatSession();
        final ChatSession cs = new ChatSession(cmd.getParam(2), this, username, cmd.getParam(4));
        // reset the ResponseCommand so that the next XFR request won't conflict
        cmd.process(null);
        cs.invite(email);
        return cs;
    }

    void internalSetDisplayName(String newName) {
        displayName = newName;
    }

    /**
	 * Sets the display name of this user.
	 * 
	 * @param newName
	 *            the new name of this user
	 * @throws IOException 
	 */
    public void setDisplayName(String newName) throws IOException {
        //$NON-NLS-1$ //$NON-NLS-2$
        notification.write("PRP", "MFN " + URLEncoder.encode(newName));
        displayName = newName;
    }

    /**
	 * Returns the displayed name of this user.
	 * 
	 * @return the name that this user is using
	 */
    public String getDisplayName() {
        return displayName;
    }

    /**
	 * Returns the user's account's email address.
	 * 
	 * @return the email address the user is using for MSN login
	 */
    public String getUserEmail() {
        return username;
    }

    private void sendStatusData() throws IOException {
        final String message = //$NON-NLS-1$
        "<Data><PSM>" + personalMessage + //$NON-NLS-1$
        "</PSM><CurrentMedia>" + //$NON-NLS-1$
        currentMedia + "</CurrentMedia></Data>";
        notification.write("UUX", //$NON-NLS-1$ //$NON-NLS-2$
        message.length() + "\r\n" + message, false);
    }

    /**
	 * Sets the user's personal message to the specified string.
	 * 
	 * @param personalMessage
	 *            the new message to use as the user's personal message
	 * @throws IOException
	 *             If an I/O error occurred while sending the data to the
	 *             notification server
	 */
    public void setPersonalMessage(String personalMessage) throws IOException {
        if (personalMessage == null) {
            //$NON-NLS-1$
            personalMessage = "";
        } else {
            personalMessage = StringUtils.xmlEncode(personalMessage);
        }
        if (!this.personalMessage.equals(personalMessage)) {
            this.personalMessage = personalMessage;
            sendStatusData();
        }
    }

    /**
	 * Retrieves the user's current personal message.
	 * 
	 * @return the personal message that the user is currently using
	 */
    public String getPersonalMessage() {
        return StringUtils.xmlDecode(personalMessage);
    }

    /**
	 * Add an ISessionListener to this client.
	 * 
	 * @param listener
	 *            the listener to be added
	 */
    public void addSessionListener(ISessionListener listener) {
        notification.addSessionListener(listener);
    }

    /**
	 * Removes an ISessionListener from this client.
	 * 
	 * @param listener
	 *            the listener to be removed
	 */
    public void removeSessionListener(ISessionListener listener) {
        notification.removeSessionListener(listener);
    }
}
