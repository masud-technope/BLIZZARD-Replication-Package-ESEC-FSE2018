/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.datashare.mergeable;

import java.util.List;
import org.eclipse.ecf.core.identity.IIdentifiable;

public interface IItem extends IIdentifiable {

    /**
	 * Get the description associated with this item
	 * 
	 * @return String description. May be <code>null</code> if no description
	 *         (or null description) has been set
	 */
    public String getDescription();

    /**
	 * Get version number for item. From RSS SSE specification starts with '1'
	 * and is incremented each time the item is changed
	 * 
	 * @return Integer version number. Minimum of 1.
	 */
    public Integer getVersion();

    /**
	 * Get history for this item
	 * 
	 * @return IHistory instacne that describes history for this item. Will not
	 *         return <code>null</code>, but may return empty List
	 */
    public IItemHistory getHistory();

    /**
	 * Get conflicts for this item
	 * 
	 * @return List of IConflict instances that describe conflicts for this
	 *         item. Will not return <code>null</code>, but may return empty
	 *         List
	 */
    public List getConflicts();
}
