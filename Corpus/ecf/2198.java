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

import java.net.URLDecoder;
import java.util.*;
import org.eclipse.ecf.protocol.msn.events.IContactListener;
import org.eclipse.ecf.protocol.msn.internal.encode.StringUtils;

/**
 * <p>
 * This class represents a contact that a user has on his or her MSN list.
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
public final class Contact {

    /**
	 * The list of listeners that is attached to this.
	 */
    private final ArrayList listeners;

    /**
	 * The list of groups that this contact is in.
	 */
    private final ArrayList groups;

    /**
	 * The email address that is associated with this contact.
	 */
    private final String email;

    /**
	 * The guid of this contact.
	 */
    private final String guid;

    /**
	 * The displayed name of this contact, this is typically different from
	 * their {@link #email}.
	 */
    private String name;

    /**
	 * The personal message that the contact is currently displaying.
	 */
    private String personalMessage;

    /**
	 * The current status of this user.
	 * 
	 * @see Status#ONLINE Status.ONLINE and others
	 */
    private Status status;

    /**
	 * Creates a new Contact with the given name and email address.
	 * @param email
	 *            the user's email address
	 * @param name
	 *            the contact's MSN nickname in raw format, the name will be URL
	 *            decoded accordingly
	 */
     Contact(String email, String name) {
        this(email, name, null);
    }

     Contact(String email, String name, String guid) {
        this.name = URLDecoder.decode(name);
        this.email = email;
        this.guid = guid;
        this.status = Status.OFFLINE;
        listeners = new ArrayList();
        groups = new ArrayList();
    }

    /**
	 * Invokes the {@link IContactListener#statusChanged(Status)} method on
	 * every listener within {@link #listeners}. This method is automatically
	 * invoked when {@link #setStatus(Status)} is called.
	 * 
	 * @param newStatus
	 *            the status that this contact has now switched to
	 */
    private void fireStatusChanged(Status newStatus) {
        synchronized (listeners) {
            for (int i = 0; i < listeners.size(); i++) {
                ((IContactListener) listeners.get(i)).statusChanged(newStatus);
            }
        }
    }

    /**
	 * Invokes the {@link IContactListener#nameChanged(String)} method on every
	 * listener within {@link #listeners}. This method is automatically invoked
	 * when {@link #setDisplayName(String)} is called.
	 * 
	 * @param newName
	 *            the new name of this contact
	 */
    private void fireNameChanged(String newName) {
        synchronized (listeners) {
            for (int i = 0; i < listeners.size(); i++) {
                ((IContactListener) listeners.get(i)).nameChanged(newName);
            }
        }
    }

    private void firePersonalMessageChanged(String message) {
        synchronized (listeners) {
            for (int i = 0; i < listeners.size(); i++) {
                ((IContactListener) listeners.get(i)).personalMessageChanged(message);
            }
        }
    }

    void add(Group group) {
        groups.add(group);
    }

    void remove() {
        for (int i = 0; i < groups.size(); i++) {
            ((Group) groups.get(i)).remove(this);
        }
        groups.clear();
    }

    /**
	 * Retrieves the groups that this contact is a part of.
	 * 
	 * @return a collection of the groups that this contact is a member of
	 */
    public Collection getGroups() {
        return Collections.unmodifiableCollection(groups);
    }

    /**
	 * Sets this contact's status to the given status. Developers are highly
	 * discouraged from calling this method since if the user's status actually
	 * did change, the {@link IContactListener#nameChanged(String)} method will
	 * be invoked on all the listeners attached to this contact.
	 * 
	 * @param status
	 *            the status that this contact is now in
	 */
    void setStatus(Status status) {
        if (this.status != status) {
            this.status = status;
            fireStatusChanged(status);
        }
    }

    /**
	 * Retrieves the current status of this contact.
	 * 
	 * @return the status that this contact currently is in
	 */
    public Status getStatus() {
        return status;
    }

    /**
	 * Sets the user name of this contact with the given name. Developers are
	 * highly discouraged from calling this method since if the value of
	 * <code>newName</code> differs from the current name, the
	 * {@link IContactListener#nameChanged(String)} method will be invoked on
	 * all the listeners attached to this contact.
	 * 
	 * @param newName
	 *            the new user name of this Contact
	 */
    void setDisplayName(String newName) {
        newName = URLDecoder.decode(newName);
        if (!newName.equals(name)) {
            this.name = newName;
            fireNameChanged(newName);
        }
    }

    /**
	 * Gets the displayed name of this contact.
	 * 
	 * @return the name that this contact uses
	 */
    public String getDisplayName() {
        return name;
    }

    /**
	 * Changes the contact's personal message to the provided message if the two
	 * messages differ.
	 * 
	 * @param message
	 *            the message the contact may have set to
	 */
    void setPersonalMessage(String message) {
        message = StringUtils.xmlDecode(message);
        if (!message.equals(personalMessage)) {
            personalMessage = message;
            firePersonalMessageChanged(message);
        }
    }

    /**
	 * Returns the personal message that this contact is currently using.
	 * 
	 * @return the personal message in use
	 */
    public String getPersonalMessage() {
        return personalMessage;
    }

    /**
	 * Returns the email address of the user.
	 * 
	 * @return the user's email address
	 */
    public String getEmail() {
        return email;
    }

    String getGuid() {
        return guid;
    }

    /**
	 * Adds a IContactListener to this contact.
	 * 
	 * @param listener
	 *            the listener to be added
	 */
    public void addContactListener(IContactListener listener) {
        if (listener != null) {
            synchronized (listeners) {
                if (!listeners.contains(listener)) {
                    listeners.add(listener);
                }
            }
        }
    }

    /**
	 * Removes a IContactListener from this contact.
	 * 
	 * @param listener
	 *            the listener to be removed
	 */
    public void removeContactListener(IContactListener listener) {
        if (listener != null) {
            synchronized (listeners) {
                listeners.remove(listener);
            }
        }
    }

    /**
	 * Returns this contact's email address that's being used for identification
	 * purposes in MSN.
	 * 
	 * @return the contact's email address
	 */
    public String toString() {
        return email;
    }

    /**
	 * Returns whether the specified object is equal to this. An object is equal
	 * to this if it is also a <tt>Contact</tt> and its email addresses is
	 * equal to this contact's email address.
	 * @param obj 
	 * 
	 * @return <code>true</code> if the argument is a <tt>Contact</tt> and
	 *         also has the same email address as this
	 */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Contact) {
            return email.equals(((Contact) obj).email);
        } else {
            return false;
        }
    }

    /**
	 * Returns a unique integer hash code for this contact.
	 * 
	 * @return a integer hash code that represents this contact
	 */
    public int hashCode() {
        return 31 * -1 + email.hashCode();
    }
}
