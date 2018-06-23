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

import org.eclipse.ecf.presence.search.ICriteria;
import org.eclipse.ecf.presence.search.IResultList;
import org.eclipse.ecf.presence.search.ISearch;
import org.eclipse.ecf.presence.search.ResultList;

/**
 * Implement ISearch for XMPP
 *@since 3.0
 */
public class XMPPSearch implements ISearch {

    protected IResultList resultList;

    private ICriteria criteria;

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.ecf.presence.search.ISearch#getResultList()
	 */
    public IResultList getResultList() {
        return resultList;
    }

    protected  XMPPSearch(ICriteria criteria) {
        this.criteria = criteria;
    }

    public  XMPPSearch(ResultList resultList) {
        this.resultList = resultList;
    }

    public ICriteria getCriteria() {
        return this.criteria;
    }

    public void setResultList(IResultList resultList) {
        this.resultList = resultList;
    }

    public String toString() {
        //$NON-NLS-1$
        StringBuffer sb = new StringBuffer("XMPPSearch[");
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        sb.append("criteria=").append(getCriteria()).append(";resultlist=").append(getResultList()).append("]");
        return sb.toString();
    }
}
