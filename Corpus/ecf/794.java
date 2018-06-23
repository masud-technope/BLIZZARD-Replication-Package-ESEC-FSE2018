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
package org.eclipse.ecf.presence;

/**
 * ID adapter interface that supplies access to 'fully qualified' ID information.
 */
public interface IFQID {

    /**
	 * Get the fully qualified name.  Will not return <code>null</code>.  The result
	 * may be the same as ID.getName(), or may include additional information.  The
	 * result must be longer than or equal to ID.getName().
	 * @return String that is the fully qualified name.  Will not return <code>null</code>.  The result
	 * may be the same as ID.getName(), or may include additional information.  The
	 * result must be longer than or equal to ID.getName().
	 */
    public String getFQName();

    /**
	 * Get resource name.  May return <code>null</code>.
	 * @return String that is the resource for this IFQID.  May be <code>null</code>.
	 */
    public String getResourceName();
}
