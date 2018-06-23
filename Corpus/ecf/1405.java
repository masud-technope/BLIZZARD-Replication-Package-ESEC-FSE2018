/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.presence.roster;

import java.util.Collection;

/**
 * Roster group information. The roster group information is associated with
 * {@link IRosterEntry} instances.
 * 
 */
public interface IRosterGroup extends IRosterItem {

    /**
	 * Get roster entries belonging to this group. Instances of list are of type
	 * {@link IRosterEntry}
	 * 
	 * @return Collection of IRosterEntrys. Will not return <code>null</code>.
	 *         May return an empty Collection.
	 */
    public Collection getEntries();
}
