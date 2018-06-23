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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.ecf.storage.IIDEntry;
import org.eclipse.ecf.storage.INamespaceEntry;
import org.eclipse.equinox.security.storage.ISecurePreferences;

/**
 *
 */
public class NamespaceEntry implements INamespaceEntry {

    private final ISecurePreferences prefs;

    public  NamespaceEntry(ISecurePreferences prefs) {
        this.prefs = prefs;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.storage.INamespaceEntry#getIDEntries()
	 */
    public IIDEntry[] getIDEntries() {
        String[] names = prefs.childrenNames();
        List results = new ArrayList();
        for (int i = 0; i < names.length; i++) results.add(new IDEntry(prefs.node(names[i])));
        return (IIDEntry[]) results.toArray(new IIDEntry[] {});
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.storage.INamespaceEntry#getPreferences()
	 */
    public ISecurePreferences getPreferences() {
        return prefs;
    }

    public void delete() {
        prefs.removeNode();
    }
}
