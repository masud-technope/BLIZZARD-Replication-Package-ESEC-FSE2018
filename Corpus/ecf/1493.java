/*******************************************************************************
 * Copyright (c) 2009 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Chi Jian Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.sync.resources.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.ecf.sync.IModelChange;

/**
 * Interface representing a change to a resource within the Eclipse workspace.
 */
public interface IResourceChange extends IModelChange {

    /**
	 * Returns the path of the modified resource, relative to the workspace.
	 * 
	 * @return the workspace relative path of the modified resource
	 */
    public String getPath();

    /**
	 * Returns the type of resource that has been modified.
	 * 
	 * @return the modified resource's type
	 * @see IResource#getType()
	 */
    public int getType();

    /**
	 * Retrieves the kind of modification that has occurred.
	 * 
	 * @return the kind of modification on the resource
	 * @see IResourceDelta#getKind()
	 */
    public int getKind();

    /**
	 * Retrieves the contents of the resource that has been modified. May be
	 * <code>null</code> if the resource itself represents no content such as
	 * the case for folders. While the folder itself holds files with contains
	 * contents, the folder itself has no content representation.
	 * 
	 * @return the contents of the resource that has been changed, or
	 *         <code>null</code> if not applicable
	 */
    public byte[] getContents();
}
