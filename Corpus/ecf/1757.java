/******************************************************************************
 * Copyright (c) 2008 Versant Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Chi Jian Suen (Versant Corporation) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.example.collab.presence;

import java.util.Map;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.example.collab.share.EclipseCollabSharedObject;
import org.eclipse.ecf.internal.example.collab.ClientPlugin;
import org.eclipse.ecf.presence.*;
import org.eclipse.ecf.presence.history.IHistoryManager;
import org.eclipse.ecf.presence.im.*;
import org.eclipse.ecf.presence.im.IChatMessage.Type;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.presence.search.IUserSearchManager;
import org.eclipse.ecf.presence.search.message.IMessageSearchManager;
import org.eclipse.ecf.presence.service.IPresenceService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class PresenceContainer extends AbstractPresenceContainer implements IChatManager, IChatMessageSender, IPresenceSender, IPresenceService {

    private final ListenerList messageListeners = new ListenerList();

    private final EclipseCollabSharedObject sharedObject;

    private final IContainer container;

    private final IRosterManager manager;

    private ServiceRegistration serviceRegistration;

    public  PresenceContainer(EclipseCollabSharedObject sharedObject, IContainer container, IUser user) {
        this.sharedObject = sharedObject;
        this.container = container;
        manager = new RosterManager(this, user);
        BundleContext bundleContext = ClientPlugin.getDefault().getBundle().getBundleContext();
        serviceRegistration = bundleContext.registerService(IPresenceService.class.getName(), this, null);
    }

    public void unregister() {
        if (serviceRegistration != null) {
            serviceRegistration.unregister();
            serviceRegistration = null;
        }
    }

    public IChatManager getChatManager() {
        return this;
    }

    public IRosterManager getRosterManager() {
        return manager;
    }

    public Object getAdapter(Class adapter) {
        if (adapter.isInstance(this)) {
            return this;
        } else if (adapter == IContainer.class) {
            return container;
        }
        return super.getAdapter(adapter);
    }

    public void fireMessageEvent(IIMMessageEvent messageEvent) {
        Object[] listeners = messageListeners.getListeners();
        for (int i = 0; i < listeners.length; i++) {
            IIMMessageListener listener = (IIMMessageListener) listeners[i];
            listener.handleMessageEvent(messageEvent);
        }
    }

    public void addMessageListener(IIMMessageListener listener) {
        messageListeners.add(listener);
    }

    public void removeMessageListener(IIMMessageListener listener) {
        messageListeners.remove(listener);
    }

    public void sendPresenceUpdate(ID targetId, IPresence presence) throws ECFException {
    // unimplemented as we have no concept of presence support, either online or offline
    }

    public IChat createChat(ID targetUser, IIMMessageListener messageListener) throws ECFException {
        return null;
    }

    public IChatMessageSender getChatMessageSender() {
        return this;
    }

    public IHistoryManager getHistoryManager() {
        return null;
    }

    public ITypingMessageSender getTypingMessageSender() {
        return null;
    }

    public void sendChatMessage(ID toId, ID threadId, Type type, String subject, String body, Map properties) throws ECFException {
        sharedObject.sendPrivateMessageToUser(toId, body);
    }

    public void sendChatMessage(ID toId, String body) throws ECFException {
        sendChatMessage(toId, null, null, null, body, null);
    }

    public IUserSearchManager getUserSearchManager() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.ecf.presence.im.IChatManager#getMessageSearchManager()
	 */
    public IMessageSearchManager getMessageSearchManager() {
        return null;
    }
}
