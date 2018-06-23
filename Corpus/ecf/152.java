/*******************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.xmpp.search;

import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.presence.search.IResult;

/**
 * Implement a specific result for XMPP
 *@since 3.0
 */
public class XMPPResultItem implements IResult {

    /** contain a IUser */
    protected IUser user;

    /**
	 * Create a XMPP result with a IUser
	 * @param user IUser
	 */
    public  XMPPResultItem(IUser user) {
        this.user = user;
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.ecf.presence.search.IResult#getUser()
	 */
    public IUser getUser() {
        return user;
    }

    /**
	 * Verify if there is the same IUser. Compare using ID
	 */
    public boolean equals(Object o) {
        if (o instanceof XMPPResultItem)
            return ((XMPPResultItem) o).getUser().getID().toString().equals(user.getID().toString());
        else
            return false;
    }

    public int hashCode() {
        return (user.getID().toString() != null ? user.getID().toString().hashCode() : 0);
    }

    public Object getAdapter(Class adapter) {
        // TODO Auto-generated method stub
        return null;
    }
}
