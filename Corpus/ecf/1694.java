/*******************************************************************************
 * Copyright (c) 2005, 2006 Erkki Lindpere and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Erkki Lindpere - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.bulletinboard;

import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IIdentifiable;

/**
 * This class models a Bulletin Board a.k.a. Internet Forum. It contains methods
 * for accessing the forums, member groups and members of the Bulletin Board;
 * and methods for logging in and logging out of the BB.
 * 
 * @author Erkki
 */
public interface IBulletinBoardContainerAdapter extends IAdaptable, IIdentifiable {

    /**
	 * Returns the title for this Bulletin Board.
	 * 
	 * @return the title.
	 * @throws BBException
	 *             if there was a problem getting the title.
	 */
    public String getTitle() throws BBException;

    /**
	 * Returns all the forums that are visible in this Bulletin Board session.
	 * 
	 * @return all forums
	 * @throws BBException
	 *             if fetching the forums failed.
	 */
    public List getForums() throws BBException;

    /**
	 * Returns all the top-level forums (or forum categories) that are visible
	 * in this Bulletin Board session.
	 * 
	 * @return all top-level forums
	 * @throws BBException
	 *             if fetching the forums failed
	 */
    public List getTopLevelForums() throws BBException;

    /**
	 * Returns a particular forum identified by the id parameter.
	 * 
	 * @param id
	 *            the forum id
	 * @return the forum object
	 * @throws BBException
	 *             if fetching the forum failed
	 */
    public IForum getForum(ID id) throws BBException;

    /**
	 * Returns a particular thread identified by the id parameter. The
	 * information about which forum this thread is in may be unknown if the
	 * thread is obtained with this method.
	 * 
	 * @param id
	 *            the thread id
	 * @return the thread object
	 * @throws BBException
	 *             if fetching the thread failed
	 */
    public IThread getThread(ID id) throws BBException;

    /**
	 * Returns a particular message identified by he id parameter. The
	 * information about which thread this message is in may be unknown if the
	 * message is obtained with this method.
	 * 
	 * @param id
	 *            the message id
	 * @return the message object
	 * @throws BBException
	 *             if fetching the message failed
	 */
    public IThreadMessage getMessage(ID id) throws BBException;

    /**
	 * Returns all the member groups that are visible in this BB session.
	 * 
	 * The group list returned must return true for contains() if it is called
	 * for any other group object obtained from this instance of the
	 * BulletinBoard with the same logged in user.
	 * 
	 * @return all member groups
	 * @throws BBException
	 *             if fetching the groups failed
	 */
    public Collection getMemberGroups() throws BBException;

    /**
	 * Returns a particular member group identified by the id parameter
	 * 
	 * @param id
	 *            the member group id
	 * @return the member group object
	 * @throws BBException
	 *             if fetching the group failed
	 */
    public IMemberGroup getMemberGroup(ID id) throws BBException;

    /**
	 * Returns members of the Bulletin Board.
	 * 
	 * Note: this method may only return some members.
	 * 
	 * @return members
	 * @throws BBException
	 *             if fetching the members failed.
	 */
    public List getMembers() throws BBException;

    /**
	 * Returns a particular member of the Bulletin Board identified by the id
	 * parameter.
	 * 
	 * @param id
	 *            the id of the member to return
	 * @return the member object
	 * @throws BBException
	 *             if fetching the member failed
	 */
    public IMember getMember(ID id) throws BBException;

    /**
	 * Returns the member currently logged in to the Bulletin Board in this
	 * session or null if this is an anonymous session.
	 * 
	 * @return the current member or null
	 * @throws BBException
	 *             if fetching the member data failed
	 */
    public IMember getLoggedInMember() throws BBException;

    /**
	 * Logs in to the Bulletin Board using the credentials provided.
	 * 
	 * @param credentials
	 *            the credentials to use for authentication
	 * @return true if the login was successful, false otherwise
	 * @throws BBException
	 *             if there was an error while logging in
	 */
    public boolean login(IBBCredentials credentials) throws BBException;

    /**
	 * Logs the currently logged in member out of this Bulletin Board.
	 * 
	 * @return true if the logout was successful, false otherwise
	 * @throws BBException
	 *             if there was an error while logging out or if there was no
	 *             user logged in
	 */
    public boolean logout() throws BBException;

    /**
	 * Returns the status of this Bulletin Board's connection.
	 * 
	 * @return true if the connection is still alive, false otherwise
	 * @deprecated Connection status should be the business of IContainer.
	 */
    public boolean isConnected();

    /**
	 * Closes this Bulletin Board's connection.
	 * 
	 * @throws BBException
	 *             if the connection was already closed
	 * @deprecated Connection status should be the business of IContainer.
	 */
    public void close() throws BBException;
}
