/*******************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.ecf.presence.search.message;

import org.eclipse.ecf.presence.search.ISearch;

/**
 * 
 * @since 2.0
 *
 */
public class MessageSearchCompleteEvent implements IMessageSearchCompleteEvent {

    private ISearch search;

    public  MessageSearchCompleteEvent(ISearch search) {
        this.search = search;
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.ecf.presence.search.message.IMessageSearchCompleteEvent#getSearch()
	 */
    public ISearch getSearch() {
        return search;
    }

    public String toString() {
        //$NON-NLS-1$
        StringBuffer sb = new StringBuffer("MessageSearchCompleteEvent[search=");
        //$NON-NLS-1$
        sb.append(getSearch()).append("]");
        return sb.toString();
    }
}
