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
 * This interface models a forum Member.
 * 
 * IMember extends IMessageAuthor to support author relationship of an IMessage.
 * 
 * @author Erkki
 */
public interface IMember extends IBBObject {

    /**
	 * Returns true if the forum member is also a member of the group.
	 * 
	 * @param group
	 * @return <code>true</code> if this is member of given group, <code>false</code> otherwise.
	 */
    public boolean isMemberOf(IMemberGroup group);

    /**
	 * Returns the groups this member is part of or an empty list.
	 * 
	 * Note: this method may return only a partial list.
	 * 
	 * @return Collection of groups associated with this member.
	 */
    public Collection getGroups();
}
