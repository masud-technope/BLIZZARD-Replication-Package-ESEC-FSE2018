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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import org.apache.commons.httpclient.NameValuePair;
import org.eclipse.ecf.bulletinboard.IMember;
import org.eclipse.ecf.bulletinboard.IMemberGroup;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.internal.bulletinboard.commons.AbstractBBObject;
import org.eclipse.ecf.internal.bulletinboard.commons.webapp.GetRequest;
import org.eclipse.ecf.internal.provider.phpbb.identity.MemberGroupID;

public class MemberGroup extends PHPBBObject implements IMemberGroup {

    private String description;

    private MemberGroupID id;

    private boolean detailsFetched;

    public  MemberGroup(MemberGroupID id, String name) {
        super(name, READ_ONLY);
        this.id = id;
        this.detailsFetched = false;
    }

    private void attainDetailsFetched() {
        if (!detailsFetched) {
            GetRequest request = new GetRequest(bb.getHttpClient(), bb.getURL(), "groupcp.php");
            request.addParameter(new NameValuePair("g", String.valueOf(id.getLongValue())));
            String resp = null;
            try {
                request.execute();
                resp = request.getResponseBodyAsString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            request.releaseConnection();
            if (resp != null) {
                MemberGroup group = ((PHPBBParser) bb.getParser()).parseMemberGroup(resp);
                group.detailsFetched = true;
                this.description = group.getDescription();
            }
        }
    }

    public String getDescription() {
        attainDetailsFetched();
        return description;
    }

    public Collection<IMember> getMembers() {
        Map<ID, IMember> map = Collections.emptyMap();
        GetRequest request = new GetRequest(bb.getHttpClient(), bb.getURL(), "groupcp.php");
        request.addParameter(new NameValuePair("g", String.valueOf(id.getLongValue())));
        try {
            request.execute();
            String str = request.getResponseBodyAsString();
            request.releaseConnection();
            if (str != null) {
                map = bb.getParser().parseMembers(str);
                for (IMember member : map.values()) {
                    ((AbstractBBObject) member).setBulletinBoard(bb);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashSet<IMember>(map.values());
    }

    public ID getID() {
        return id;
    }

    protected void setDescription(String desc) {
        this.description = desc;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MemberGroup) {
            MemberGroup grp = (MemberGroup) obj;
            return id.equals(grp.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
