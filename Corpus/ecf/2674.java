/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.xmpp.smack;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.CallbackHandler;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.internal.provider.xmpp.XmppPlugin;
import org.eclipse.ecf.provider.comm.DisconnectEvent;
import org.eclipse.ecf.provider.comm.IAsynchEventHandler;
import org.eclipse.ecf.provider.comm.IConnectionListener;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.xmpp.identity.XMPPID;
import org.eclipse.ecf.provider.xmpp.identity.XMPPRoomID;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Bind;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

public class ECFConnection implements ISynchAsynchConnection {

    /**
	 * 
	 */
    private static final String GOOGLE_TALK_HOST = "talk.google.com";

    public static final String CLIENT_TYPE = "ecf.";

    public static final boolean DEBUG = Boolean.getBoolean("smack.debug");

    protected static final String STRING_ENCODING = "UTF-8";

    public static final String OBJECT_PROPERTY_NAME = ECFConnection.class.getName() + ".object";

    protected static final int XMPP_DEFAULT_PORT = 5222;

    protected static final int XMPPS_DEFAULT_PORT = 5223;

    private XMPPConnection connection = null;

    private IAsynchEventHandler handler = null;

    private boolean isStarted = false;

    private int serverPort = -1;

    private String serverResource;

    private final Map properties = null;

    private boolean isConnected = false;

    private Namespace namespace = null;

    private boolean google = false;

    private boolean disconnecting = false;

    private int BIND_TIMEOUT = new Integer(System.getProperty("org.eclipse.ecf.provider.xmpp.ECFConnection.bindTimeout", "15000")).intValue();

    private Object bindLock = new Object();

    private String jid;

    private CallbackHandler callbackHandler;

    private final PacketListener packetListener = new PacketListener() {

        public void processPacket(Packet arg0) {
            handlePacket(arg0);
        }
    };

    private final ConnectionListener connectionListener = new ConnectionListener() {

        public void connectionClosed() {
            handleConnectionClosed(new IOException("Connection reset by peer"));
        }

        public void connectionClosedOnError(Exception e) {
            handleConnectionClosed(e);
        }

        public void reconnectingIn(int seconds) {
        }

        public void reconnectionFailed(Exception e) {
        }

        public void reconnectionSuccessful() {
        }
    };

    protected void logException(String msg, Throwable t) {
        XmppPlugin.log(msg, t);
    }

    public Map getProperties() {
        return properties;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
    public Object getAdapter(Class adapter) {
        if (adapter == null)
            return null;
        if (adapter.isInstance(this))
            return this;
        final IAdapterManager adapterManager = XmppPlugin.getDefault().getAdapterManager();
        return (adapterManager == null) ? null : adapterManager.loadAdapter(this, adapter.getName());
    }

    public XMPPConnection getXMPPConnection() {
        return connection;
    }

    public  ECFConnection(boolean google, Namespace ns, IAsynchEventHandler h) {
        this(google, ns, h, null);
    }

    public  ECFConnection(boolean google, Namespace ns, IAsynchEventHandler h, CallbackHandler ch) {
        this.handler = h;
        this.namespace = ns;
        this.google = google;
        this.callbackHandler = ch;
        if (DEBUG)
            XMPPConnection.DEBUG_ENABLED = true;
    }

    protected String getPasswordForObject(Object data) {
        String password = null;
        try {
            password = (String) data;
        } catch (final ClassCastException e) {
            return null;
        }
        return password;
    }

    private XMPPID getXMPPID(ID remote) throws ECFException {
        XMPPID jabberID = null;
        try {
            jabberID = (XMPPID) remote;
        } catch (final ClassCastException e) {
            throw new ECFException(e);
        }
        return jabberID;
    }

    public synchronized Object connect(ID remote, Object data, int timeout) throws ECFException {
        if (connection != null)
            throw new ECFException("already connected");
        if (timeout > 0)
            SmackConfiguration.setPacketReplyTimeout(timeout);
        Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);
        final XMPPID jabberURI = getXMPPID(remote);
        String username = jabberURI.getNodename();
        String hostname = jabberURI.getHostname();
        String hostnameOverride = null;
        // Check for the URI form of "joe@bloggs.org;talk.google.com", which
        // would at this point would have
        // - username = "joe"
        // - hostname = "blogs.org;talk.google.com"
        // - hostnameOverride = null
        //
        // We need to turn this into:
        // - username = "joe"
        // - hostname = "bloggs.org"
        // - hostnameOverride = "talk.google.com"
        int semiColonIdx = hostname.lastIndexOf(';');
        if (semiColonIdx != -1) {
            hostnameOverride = hostname.substring(semiColonIdx + 1);
            hostname = hostname.substring(0, semiColonIdx);
        }
        if (google && hostnameOverride == null) {
            hostnameOverride = GOOGLE_TALK_HOST;
        }
        final String serviceName = hostname;
        serverPort = jabberURI.getPort();
        serverResource = jabberURI.getResourceName();
        if (serverResource == null || serverResource.equals(XMPPID.PATH_DELIMITER)) {
            serverResource = getClientIdentifier();
            jabberURI.setResourceName(serverResource);
        }
        try {
            ConnectionConfiguration config;
            if (hostnameOverride != null) {
                config = new ConnectionConfiguration(hostnameOverride, XMPP_DEFAULT_PORT, serviceName);
            } else if (serverPort == -1) {
                config = new ConnectionConfiguration(serviceName);
            } else {
                config = new ConnectionConfiguration(serviceName, serverPort);
            }
            config.setSendPresence(true);
            // authentication; handler should provide keystore password:
            if (callbackHandler instanceof javax.security.auth.callback.CallbackHandler) {
                config.setCallbackHandler((javax.security.auth.callback.CallbackHandler) callbackHandler);
            }
            connection = new XMPPConnection(config);
            connection.connect();
            SASLAuthentication.supportSASLMechanism("PLAIN", 0);
            if (google || GOOGLE_TALK_HOST.equals(hostnameOverride)) {
                username = username + "@" + serviceName;
            }
            connection.addPacketListener(packetListener, null);
            connection.addConnectionListener(connectionListener);
            // Login
            connection.login(username, (String) data, serverResource);
            waitForBindResult();
        } catch (final XMPPException e) {
            throw new ContainerConnectException("Login attempt failed", e);
        }
        return jid;
    }

    private void waitForBindResult() throws XMPPException {
        // We'll wait a maximum of
        long bindTimeout = System.currentTimeMillis() + BIND_TIMEOUT;
        synchronized (bindLock) {
            while (jid == null && System.currentTimeMillis() < bindTimeout) {
                try {
                    bindLock.wait(1000);
                } catch (InterruptedException e) {
                }
            }
            if (jid == null)
                throw new XMPPException("timeout waiting for server bind result");
            isConnected = true;
        }
    }

    private String getClientIdentifier() {
        return CLIENT_TYPE + handler.getEventHandlerID().getName();
    }

    public void sendPacket(Packet packet) throws XMPPException {
        if (connection != null)
            connection.sendPacket(packet);
    }

    public synchronized void disconnect() {
        disconnecting = true;
        if (isStarted()) {
            stop();
        }
        if (connection != null) {
            connection.removePacketListener(packetListener);
            connection.removeConnectionListener(connectionListener);
            connection.disconnect();
            connection = null;
            synchronized (bindLock) {
                jid = null;
                isConnected = false;
            }
        }
    }

    public synchronized boolean isConnected() {
        return (isConnected);
    }

    public synchronized ID getLocalID() {
        if (!isConnected())
            return null;
        try {
            return IDFactory.getDefault().createID(namespace.getName(), new Object[] { connection.getConnectionID() });
        } catch (final Exception e) {
            logException("Exception in getLocalID", e);
            return null;
        }
    }

    public synchronized void start() {
        if (isStarted())
            return;
        isStarted = true;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public synchronized void stop() {
        isStarted = false;
    }

    protected void handleConnectionClosed(Exception e) {
        if (!disconnecting) {
            disconnecting = true;
            handler.handleDisconnectEvent(new DisconnectEvent(this, e, null));
        }
    }

    protected void handlePacket(Packet arg0) {
        handleJidPacket(arg0);
        try {
            final Object val = arg0.getProperty(OBJECT_PROPERTY_NAME);
            if (val != null) {
                handler.handleAsynchEvent(new ECFConnectionObjectPacketEvent(this, arg0, val));
            } else {
                handler.handleAsynchEvent(new ECFConnectionPacketEvent(this, arg0));
            }
        } catch (final IOException e) {
            logException("Exception in handleAsynchEvent", e);
            try {
                disconnect();
            } catch (final Exception e1) {
                logException("Exception in disconnect()", e1);
            }
        }
    }

    private void handleJidPacket(Packet packet) {
        if (jid != null)
            return;
        if (packet instanceof IQ) {
            IQ iqPacket = (IQ) packet;
            if (iqPacket.getType().equals(IQ.Type.RESULT) && iqPacket instanceof Bind) {
                Bind bindPacket = (Bind) iqPacket;
                synchronized (bindLock) {
                    jid = bindPacket.getJid();
                    bindLock.notify();
                }
            }
        }
    }

    public synchronized void sendAsynch(ID receiver, byte[] data) throws IOException {
        if (data == null)
            throw new IOException("no data");
        final Message aMsg = new Message();
        aMsg.setProperty(OBJECT_PROPERTY_NAME, data);
        sendMessage(receiver, aMsg);
    }

    protected void sendMessage(ID receiver, Message aMsg) throws IOException {
        synchronized (this) {
            if (!isConnected())
                throw new IOException("not connected");
            try {
                if (receiver == null)
                    throw new IOException("receiver cannot be null for xmpp instant messaging");
                else if (receiver instanceof XMPPID) {
                    final XMPPID rcvr = (XMPPID) receiver;
                    aMsg.setType(Message.Type.chat);
                    final String receiverName = rcvr.getFQName();
                    final Chat localChat = connection.getChatManager().createChat(receiverName, new MessageListener() {

                        public void processMessage(Chat chat, Message message) {
                        }
                    });
                    localChat.sendMessage(aMsg);
                } else if (receiver instanceof XMPPRoomID) {
                    final XMPPRoomID roomID = (XMPPRoomID) receiver;
                    aMsg.setType(Message.Type.groupchat);
                    final String to = roomID.getMucString();
                    aMsg.setTo(to);
                    connection.sendPacket(aMsg);
                } else
                    throw new IOException("receiver must be of type XMPPID or XMPPRoomID");
            } catch (final XMPPException e) {
                final IOException result = new IOException("XMPPException in sendMessage: " + e.getMessage());
                result.setStackTrace(e.getStackTrace());
                throw result;
            }
        }
    }

    public synchronized Object sendSynch(ID receiver, byte[] data) throws IOException {
        if (data == null)
            throw new IOException("data cannot be null");
        // disconnect();
        return null;
    }

    public void addListener(IConnectionListener listener) {
    // XXX Not yet implemented
    }

    public void removeListener(IConnectionListener listener) {
    // XXX Not yet implemented
    }

    public void sendMessage(ID target, String message) throws IOException {
        if (target == null)
            throw new IOException("target cannot be null");
        if (message == null)
            throw new IOException("message cannot be null");
        final Message aMsg = new Message();
        aMsg.setBody(message);
        sendMessage(target, aMsg);
    }

    public static Map getPropertiesFromPacket(Packet packet) {
        final Map result = new HashMap();
        final Iterator i = packet.getPropertyNames().iterator();
        for (; i.hasNext(); ) {
            final String name = (String) i.next();
            result.put(name, packet.getProperty(name));
        }
        return result;
    }

    public static Packet setPropertiesInPacket(Packet input, Map properties) {
        if (properties != null) {
            for (final Iterator i = properties.keySet().iterator(); i.hasNext(); ) {
                final Object keyo = i.next();
                final Object val = properties.get(keyo);
                final String key = (keyo instanceof String) ? (String) keyo : keyo.toString();
                input.setProperty(key, val);
            }
        }
        return input;
    }

    public void sendMessage(ID target, ID thread, Type type, String subject, String body, Map properties2) throws IOException {
        if (target == null)
            throw new IOException("XMPP target for message cannot be null");
        if (body == null)
            body = "";
        final Message aMsg = new Message();
        aMsg.setBody(body);
        if (thread != null)
            aMsg.setThread(thread.getName());
        if (type != null)
            aMsg.setType(type);
        if (subject != null)
            aMsg.setSubject(subject);
        setPropertiesInPacket(aMsg, properties2);
        sendMessage(target, aMsg);
    }

    public void sendPresenceUpdate(ID target, Presence presence) throws IOException {
        if (presence == null)
            throw new IOException("presence cannot be null");
        presence.setFrom(connection.getUser());
        if (target != null)
            presence.setTo(target.getName());
        synchronized (this) {
            if (!isConnected())
                throw new IOException("not connected");
            connection.sendPacket(presence);
        }
    }

    public void sendRosterAdd(String user, String name, String[] groups) throws IOException, XMPPException {
        final Roster r = getRoster();
        r.createEntry(user, name, groups);
    }

    public void sendRosterRemove(String user) throws XMPPException, IOException {
        final Roster r = getRoster();
        final RosterEntry re = r.getEntry(user);
        r.removeEntry(re);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.provider.xmpp.IIMMessageSender#getRoster()
	 */
    public Roster getRoster() throws IOException {
        if (connection == null || !connection.isConnected())
            return null;
        return connection.getRoster();
    }
}
