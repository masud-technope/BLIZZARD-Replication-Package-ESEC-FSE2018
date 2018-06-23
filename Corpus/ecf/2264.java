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
package org.eclipse.ecf.internal.provider.phpbb;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.eclipse.ecf.bulletinboard.IMember;
import org.eclipse.ecf.bulletinboard.IMemberGroup;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.internal.bulletinboard.commons.AbstractBulletinBoard;

public class Member extends PHPBBObject implements IMember {

    // private static final Logger log = Logger.getLogger(Member.class);
    private static final long serialVersionUID = -4327906596939705648L;

    private ID id;

    public  Member(ID id, String name) {
        super(name, READ_ONLY);
        this.id = id;
    }

    public ID getID() {
        return id;
    }

    public boolean isMemberOf(IMemberGroup group) {
        // TODO maybe a better way?
        return group.getMembers().contains(this);
    }

    public Collection<IMemberGroup> getGroups() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Member) {
            Member grp = (Member) obj;
            return id.equals(grp.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public Map getProperties() {
        return Collections.emptyMap();
    }
}
