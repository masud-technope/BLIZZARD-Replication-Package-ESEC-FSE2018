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
package org.eclipse.ecf.internal.provider.vbulletin;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.eclipse.ecf.bulletinboard.IMember;
import org.eclipse.ecf.bulletinboard.IMemberGroup;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.internal.bulletinboard.commons.AbstractBulletinBoard;
import org.eclipse.ecf.internal.provider.vbulletin.identity.MemberID;

public class Member extends VBObject implements IMember {

    private static final long serialVersionUID = -1931142128734519538L;

    private ID id;

    public  Member(ID id, String name) {
        super(name, READ_ONLY);
        this.id = id;
    }

    public  Member(String name) {
        super(name, READ_ONLY);
        this.id = null;
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

    @Override
    public void setBulletinBoard(AbstractBulletinBoard bb) {
        super.setBulletinBoard(bb);
        if (this.id == null) {
            this.id = bb.getID();
        }
    }
}
