/*******************************************************************************
 * Copyright (c) 2005, 2006 Erkki Lindpere and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Erkki Lindpere - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.bulletinboard;

import java.util.Collection;

/**
 * Models a member group of a Bulletin Board.
 * 
 * @author Erkki
 */
public interface IMemberGroup extends IBBObject {

    /**
	 * Returns the description of this group.
	 * 
	 * @return description
	 */
    public String getDescription();

    /**
	 * Returns all of the group members or an empty list.
	 * 
	 * @return all group members
	 */
    public Collection getMembers();
}
