/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.storage;

import java.util.*;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.storage.IDStoreException;
import org.eclipse.ecf.storage.IIDEntry;
import org.eclipse.equinox.security.storage.EncodingUtils;
import org.eclipse.equinox.security.storage.ISecurePreferences;

/**
 *
 */
public class IDEntry implements IIDEntry {

    /**
	 * 
	 */
    //$NON-NLS-1$
    private static final String DELIMITER = ":";

    private final ISecurePreferences prefs;

    public  IDEntry(ISecurePreferences prefs) {
        this.prefs = prefs;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.storage.IIDEntry#getPreferences()
	 */
    public ISecurePreferences getPreferences() {
        return prefs;
    }

    public void putAssociate(String key, IIDEntry entry, boolean encrypt) throws IDStoreException {
        if (key == null)
            //$NON-NLS-1$
            throw new IDStoreException("key cannot be null");
        if (entry == null)
            //$NON-NLS-1$
            throw new IDStoreException("entry cannot be null");
        ISecurePreferences associateNode = prefs.node(key);
        ISecurePreferences prefs = entry.getPreferences();
        // This is where associates are created with form:
        // <index>:<namespace>:<idname>
        String entryAssociate = String.valueOf(associateNode.childrenNames().length) + DELIMITER + prefs.parent().name() + DELIMITER + prefs.name();
        associateNode.node(entryAssociate);
    }

    private ISecurePreferences getNamespaceRoot() {
        // namespace)
        return prefs.parent().parent();
    }

    private ISecurePreferences getPreferences(ISecurePreferences parent, String name) {
        List names = Arrays.asList(parent.childrenNames());
        if (names.contains(name))
            return parent.node(name);
        return null;
    }

    private void addAssociateFromName(String name, SortedMap results) {
        try {
            // Get index of first :
            int index = name.indexOf(DELIMITER);
            // If not found then the name is not well-formed
            if (index == -1)
                throw new //$NON-NLS-1$
                IDStoreException(//$NON-NLS-1$
                "Associate ID not well-formed");
            // Get the index string
            String indexStr = name.substring(0, index);
            Integer resultIndex = null;
            // Create resultIndex from indexStr
            try {
                resultIndex = Integer.valueOf(indexStr);
            } catch (NumberFormatException e) {
                throw new IDStoreException("Associate ID not well-formed", e);
            }
            // get remainder string
            name = name.substring(index + 1);
            // Get index of second :
            index = name.indexOf(DELIMITER);
            if (index == -1)
                throw new //$NON-NLS-1$
                IDStoreException(//$NON-NLS-1$
                "Associate ID not well-formed");
            // Get namespace name before index
            String namespaceName = name.substring(0, index);
            ISecurePreferences namespacePrefs = getPreferences(getNamespaceRoot(), namespaceName);
            if (namespacePrefs == null)
                throw new IDStoreException(//$NON-NLS-1$
                "Cannot find Namespace=" + //$NON-NLS-1$
                namespaceName);
            // Get ID name after index
            String idName = name.substring(index + 1);
            ISecurePreferences idPrefs = getPreferences(namespacePrefs, idName);
            if (idPrefs == null)
                throw new IDStoreException(//$NON-NLS-1$ //$NON-NLS-2$
                "ID=" + idName + " not found in Namespace=" + namespaceName);
            // Put new IDEntry in sorted collection ordered by resultIndex
            results.put(resultIndex, new IDEntry(idPrefs));
        } catch (IDStoreException e) {
            Activator.getDefault().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR, "Unable to create associate ID", e));
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.storage.IIDEntry#createID()
	 */
    public ID createID() throws IDCreateException {
        return IDFactory.getDefault().createID(prefs.parent().name(), EncodingUtils.decodeSlashes(prefs.name()));
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.storage.IIDEntry#delete()
	 */
    public void delete() {
        prefs.removeNode();
    }

    public IIDEntry[] getAssociates(String key) {
        if (key == null)
            return new IIDEntry[0];
        ISecurePreferences associateNode = prefs.node(key);
        String[] childrenNames = associateNode.childrenNames();
        SortedMap results = new TreeMap();
        for (int i = 0; i < childrenNames.length; i++) {
            addAssociateFromName(childrenNames[i], results);
        }
        return (IIDEntry[]) results.values().toArray(new IIDEntry[] {});
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        //$NON-NLS-1$
        StringBuffer sb = new StringBuffer("IDEntry[");
        //$NON-NLS-1$
        sb.append(prefs.name()).append("]");
        return sb.toString();
    }
}
