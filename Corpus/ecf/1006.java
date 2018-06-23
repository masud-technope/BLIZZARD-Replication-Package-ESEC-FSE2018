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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.ecf.protocol.msn.events.IContactListListener;
import org.eclipse.ecf.protocol.msn.internal.encode.StringUtils;

/**
 * <p>
 * A ContactList stores a list of {@link Contact}s.
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
public final class ContactList {

    private final Map groups;

    private final ArrayList contacts;

    /**
	 * The list of listeners that is associated with this.
	 */
    private final ArrayList listeners;

    private final MsnClient client;

    /**
	 * Creates a new ContactList to store Contacts.
	 * 
	 * @param client
	 *            the client that this list is for
	 */
     ContactList(MsnClient client) {
        this.client = client;
        groups = new HashMap();
        contacts = new ArrayList();
        listeners = new ArrayList();
    }

    /**
	 * Notifies all listeners attached to this contact list that the given
	 * contact has been added.
	 * 
	 * @param contact
	 *            the contact that has been added
	 */
    private void fireContactAdded(Contact contact) {
        synchronized (listeners) {
            for (int i = 0; i < listeners.size(); i++) {
                ((IContactListListener) listeners.get(i)).contactAdded(contact);
            }
        }
    }

    void fireContactRemoved(String guid) {
        Contact contact = findContactByGuid(guid);
        if (!contact.getGroups().isEmpty()) {
            contact.remove();
        }
        synchronized (listeners) {
            for (int i = 0; i < listeners.size(); i++) {
                ((IContactListListener) listeners.get(i)).contactRemoved(contact);
            }
        }
    }

    void fireContactAddedUser(String email) {
        synchronized (listeners) {
            for (int i = 0; i < listeners.size(); i++) {
                ((IContactListListener) listeners.get(i)).contactAddedUser(email);
            }
        }
    }

    void fireContactRemovedUser(String email) {
        synchronized (listeners) {
            for (int i = 0; i < listeners.size(); i++) {
                ((IContactListListener) listeners.get(i)).contactRemovedUser(email);
            }
        }
    }

    private void fireGroupAdded(Group group) {
        synchronized (listeners) {
            for (int i = 0; i < listeners.size(); i++) {
                ((IContactListListener) listeners.get(i)).groupAdded(group);
            }
        }
    }

    void internalAddContact(String email, String contactName) {
        addContact(email, contactName, null);
    }

    void addContact(String email, String contactName, String guid) {
        Contact contact;
        if (guid == null) {
            contact = new Contact(email, contactName);
            contacts.add(contact);
        } else {
            contact = findContactByGuid(guid);
            if (contact == null) {
                contact = new Contact(email, contactName, guid);
            }
            contacts.add(contact);
        }
        fireContactAdded(contact);
    }

    void addContact(String contactName, String email, String guid, String groupGUID) {
        Contact contact = new Contact(email, contactName, guid);
        contacts.add(contact);
        String[] split = StringUtils.split(groupGUID, ',');
        for (int i = 0; i < split.length; i++) {
            ((Group) groups.get(split[i])).add(contact);
        }
        fireContactAdded(contact);
    }

    void addGroup(String guid, Group group) {
        groups.put(guid, group);
        fireGroupAdded(group);
    }

    /**
	 * Adds the contact with the specified email to this list.
	 * 
	 * @param email
	 *            the contact's email address
	 * @param userName
	 *            the name to be assigned to this contact, or <tt>null</tt> if
	 *            one does not need to be assigned
	 * @throws IOException
	 *             If an I/O error occurs while attempting to send the request
	 *             to the server
	 */
    public void addContact(String email, String userName) throws IOException {
        if (//$NON-NLS-1$
        userName == null || userName.equals("")) {
            client.add(email, email);
        } else {
            client.add(email, userName);
        }
    }

    /**
	 * Removes the specified contact from the user's list.
	 * 
	 * @param contact
	 *            the contact to remove
	 * @throws IOException
	 *             If an I/O error occurs while attempting to send the request
	 *             to the server
	 */
    public void removeContact(Contact contact) throws IOException {
        client.remove(contact);
    }

    /**
	 * Removes the specified group from the user's list.
	 * 
	 * @param group
	 *            the group to remove
	 * @throws IOException
	 *             If an I/O error occurs while attempting to send the request
	 *             to the server
	 */
    public void removeGroup(Group group) throws IOException {
        String guid = getGuid(group);
        if (guid != null) {
            client.remove(guid);
        }
    }

    /**
	 * Returns the contact that uses the specified email address. The search
	 * performed is case-sensitive.
	 * 
	 * @param email
	 *            the email address of the desired contact
	 * @return the contact that is associated with the given email address, or
	 *         <code>null</code> if none could be found
	 */
    public Contact getContact(String email) {
        for (int i = 0; i < contacts.size(); i++) {
            Contact contact = (Contact) contacts.get(i);
            if (contact.getEmail().equals(email)) {
                return contact;
            }
        }
        return null;
    }

    private Contact findContactByGuid(String guid) {
        for (int i = 0; i < contacts.size(); i++) {
            Contact contact = (Contact) contacts.get(i);
            if (guid.equals(contact.getGuid())) {
                return contact;
            }
        }
        return null;
    }

    public Collection getContacts() {
        return Collections.unmodifiableCollection(contacts);
    }

    public Collection getGroups() {
        return Collections.unmodifiableCollection(groups.values());
    }

    String getGuid(Group group) {
        for (Iterator it = groups.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getValue() == group) {
                return (String) entry.getKey();
            }
        }
        return null;
    }

    /**
	 * Adds a IContactListener to this.
	 * 
	 * @param listener
	 *            the listener to be added
	 */
    public void addContactListListener(IContactListListener listener) {
        if (listener != null) {
            synchronized (listeners) {
                if (!listeners.contains(listener)) {
                    listeners.add(listener);
                }
            }
        }
    }

    /**
	 * Removes a IContactListener from this.
	 * 
	 * @param listener
	 *            the listener to be removed
	 */
    public void removeContactsListListener(IContactListListener listener) {
        if (listener != null) {
            synchronized (listeners) {
                listeners.remove(listener);
            }
        }
    }

    public String toString() {
        return contacts.toString();
    }
}
