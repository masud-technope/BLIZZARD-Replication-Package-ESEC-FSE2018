/*******************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.ecf.presence.search;

/**
 * 
 * @since 2.0
 */
public class UserSearchCompleteEvent implements IUserSearchCompleteEvent {

    private ISearch search;

    public  UserSearchCompleteEvent(ISearch search) {
        this.search = search;
    }

    public ISearch getSearch() {
        return search;
    }

    public String toString() {
        //$NON-NLS-1$
        StringBuffer sb = new StringBuffer("UserSearchCompleteEvent[search=");
        //$NON-NLS-1$
        sb.append(getSearch()).append("]");
        return sb.toString();
    }
}
