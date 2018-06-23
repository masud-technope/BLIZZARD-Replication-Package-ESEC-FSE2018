/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core;

import org.eclipse.ecf.core.identity.ID;

/**
 * Contract for reliable container. Extends IContainer
 * 
 * @see IContainer
 */
public interface IReliableContainer extends IContainer {

    /**
	 * Get the current membership of the joined group. This method will
	 * accurately report the current group membership of the connected group.
	 * 
	 * @return ID[] the IDs of the current group membership
	 */
    public ID[] getGroupMemberIDs();

    /**
	 * @return true if this IReliableContainer instance is in the 'manager' role
	 *         for the group, false otherwise.
	 */
    public boolean isGroupManager();
}
