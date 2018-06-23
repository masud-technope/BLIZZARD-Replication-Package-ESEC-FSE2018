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
package org.eclipse.ecf.storage;

/**
 * Adapter for retrieving Namespace name for storage.
 */
public interface INamespaceStoreAdapter {

    /**
	 * Get the Namespace name for storage.
	 * @return String name for storage.  Must not return <code>null</code>.
	 */
    public String getNameForStorage();
}
