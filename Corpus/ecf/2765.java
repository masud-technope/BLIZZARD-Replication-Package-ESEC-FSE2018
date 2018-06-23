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
package org.eclipse.ecf.presence.im;

import java.util.Map;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.chatroom.IChatRoomContainer;

/**
 * A two-person chat. Instances are created via
 * {@link IChatManager#createChat(ID, org.eclipse.ecf.presence.IIMMessageListener)}.
 */
public interface IChat extends IAdaptable {

    /**
	 * Get the receiver for this chat.
	 * 
	 * @return ID of receiver. Will not return <code>null</code>.
	 */
    public ID getReceiverID();

    /**
	 * Get the thread ID for this chat.
	 * 
	 * @return ID of this chat thread. Will not return <code>null</code>.
	 */
    public ID getThreadID();

    /**
	 * Send chat message to receiver.
	 * 
	 * @param type
	 *            the IChatMessage.Type of the message. May not be
	 *            <code>null</code>.
	 * 
	 * @param subject
	 *            the subject of the message. May be <code>null</code>.
	 * 
	 * @param messageBody
	 *            the body of the message to send. May be <code>null</code>.
	 * 
	 * @param properties
	 *            any properties to be associated with message. May be
	 *            <code>null</code>.
	 * 
	 * @throws ECFException
	 *             thrown if currently disconnected or some transport error
	 */
    public void sendChatMessage(IChatMessage.Type type, String subject, String messageBody, Map properties) throws ECFException;

    /**
	 * Send chat message to receiver.
	 * 
	 * @param messageBody
	 *            the body of the message to send. May be <code>null</code>.
	 * @throws ECFException
	 *             thrown if disconnected or some transport error.
	 */
    public void sendChatMessage(String messageBody) throws ECFException;

    /**
	 * Send typing message to a remote receiver.
	 * 
	 * @param isTyping
	 *            true if user is typing, false if they've stopped typing.
	 * 
	 * @param body
	 *            the content of what has been/is being typed. May be
	 *            <code>null</code>.
	 * 
	 * @throws ECFException
	 *             thrown if disconnected or some other communications error.
	 */
    public void sendTypingMessage(boolean isTyping, String body) throws ECFException;

    /**
	 * Create a new IChatRoomContainer instance from this chat. This method can
	 * be used to convert this two-way chat into an n-way chat room. If not
	 * supported by the provider, this method should return <code>null</code>.
	 * <p>
	 * </p>
	 * If supported by the provider, this allows moving from a two-way chat
	 * represented by this IChat instance to an n-way chat room container. The
	 * initial set of participants will be the two participants in this two way
	 * chat, and the {@link IContainer#getConnectedID()} will be non-null, and
	 * equal to {@link IChat#getThreadID()}.
	 * <p>
	 * </p>
	 * If this method is called succesfully (no exception and non-<code>null</code>
	 * instance returned) then the other participant in this IChat (i.e.
	 * identified by the {@link IChat#getReceiverID()} will be notified
	 * asynchronously via the delivery of an {@link IChatRoomCreationEvent} to
	 * the {@link IIMMessageListener} for the remote {@link IChat} instance.
	 * 
	 * @return a new IChatRoomContainer instance. Will return <code>null</code>
	 *         if underlying provider does not support this functionality.
	 * @throws ContainerCreateException
	 *             if chat room container cannot be made (e.g. due to
	 *             disconnection or other failure).
	 */
    public IChatRoomContainer createChatRoomContainer() throws ContainerCreateException;

    /**
	 * Get presence container adapter for this chat instance.
	 * 
	 * @return IPresenceContainerAdapter for this chat instance. Will not return
	 *         <code>null</code>.
	 */
    public IPresenceContainerAdapter getPresenceContainerAdapter();

    /**
	 * Dispose this chat, making it incapable of receiving any more messages or
	 * being the source of any more messages. Also results in removing any
	 * listeners associated with this chat.
	 */
    public void dispose();
}
