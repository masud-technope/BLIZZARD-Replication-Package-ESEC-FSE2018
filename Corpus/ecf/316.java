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

import java.util.*;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ecf.internal.presence.PresencePlugin;

/**
 * This Class implements {@link IResultList}. Subclasses may be created as
 * appropriate.
 * @since 2.0
 */
public class ResultList implements IResultList {

    protected Set results;

    /**
	 * Create a new synchronized result list from a existing one
	 * @param existingResults
	 */
    public  ResultList(Collection /* <IResult> */
    existingResults) {
        results = Collections.synchronizedSet(new HashSet());
        if (existingResults != null)
            addAll(existingResults);
    }

    /**
	 * Create a new synchronized result list from a existing one
	 *
	 */
    public  ResultList() {
        results = Collections.synchronizedSet(new HashSet());
    }

    /**
	 * Add an item for the result list
	 * @param item
	 * @return boolean
	 */
    public boolean add(IResult item) {
        if (item == null)
            return false;
        if (results.add(item))
            return true;
        return false;
    }

    /**
	 * Add the list for the current result list
	 * @param existingResults
	 */
    public void addAll(Collection /* <IResult> */
    existingResults) {
        if (existingResults == null)
            return;
        synchronized (results) {
            for (Iterator i = existingResults.iterator(); i.hasNext(); ) {
                add((IResult) i.next());
            }
        }
    }

    public Collection getResults() {
        return results;
    }

    public boolean remove(IResult item) {
        return results.remove(item);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.ui.presence.search.IResultList#getResult
	 */
    public IResult getResult(String field, String value) {
        // TODO 
        return null;
    }

    public Object getAdapter(Class adapter) {
        if (adapter.isInstance(this)) {
            return this;
        }
        IAdapterManager adapterManager = PresencePlugin.getDefault().getAdapterManager();
        if (adapterManager == null)
            return null;
        return adapterManager.loadAdapter(this, adapter.getName());
    }

    public String toString() {
        //$NON-NLS-1$
        StringBuffer sb = new StringBuffer("ResultList[");
        //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("results=").append(getResults()).append("]");
        return sb.toString();
    }
}
