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
package org.eclipse.ecf.filetransfer;

import java.util.Iterator;

/**
 * Remote file attributes.  These attributes represent characteristics of remote files (e.g. read only, writeable, executable, archive, etc.).
 */
public interface IRemoteFileAttributes {

    //$NON-NLS-1$
    public static String READ_ATTRIBUTE = "read";

    //$NON-NLS-1$
    public static String WRITE_ATTRIBUTE = "write";

    //$NON-NLS-1$
    public static String EXEC_ATTRIBUTE = "exec";

    //$NON-NLS-1$
    public static String ARCHIVE_ATTRIBUTE = "archive";

    //$NON-NLS-1$
    public static String HIDDEN_ATTRIBUTE = "hidden";

    //$NON-NLS-1$
    public static String SYMLINK_ATTRIBUTE = "symlink";

    //$NON-NLS-1$
    public static String SYMLINK_TARGET_ATTRIBUTE = "symlinktarget";

    /**
	 * Get file attribute with given key.  Returns <code>null</code> if attribute not in 
	 * this map of attributes.
	 * @param key to use to find the given attribute.  Must not be <code>null</code>.
	 * @return value of attribute.  <code>null</code> if not found.
	 */
    public String getAttribute(String key);

    /**
	 * Get all of the attribute keys in this map of file attributes.
	 * 
	 * @return Iterator of the attribute keys for this map.  Will not return <code>null</code>.
	 */
    public Iterator getAttributeKeys();

    /**
	 * Set a given attribute value in this remote file attributes.
	 * @param key the key to use for the attribute.  Must not be <code>null</code>.
	 * @param value the value for the given key.  Must not be <code>null</code>.
	 */
    public void setAttribute(String key, String value);
}
