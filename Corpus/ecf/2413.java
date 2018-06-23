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

import org.eclipse.ecf.presence.search.*;

/**
 * Get a message search mechanism for an account.
 * @since 2.0
 */
public interface IMessageSearchManager {

    /**
	 * Execute the search for a specific criteria, blocking until the search returns.
	 * This method can apply search to match the specific criteria in case if the 
	 * provider is not able to do it completely
	 * @param criteria Will not be <code>null</code>.
	 * @return {@link ICriteria} Contain the search results.
	 * @throws MessageSearchException 
	 */
    public ISearch search(ICriteria criteria) throws MessageSearchException;

    /**
	 * Execute the search for a specific criteria, not blocking until the search returns.
	 * This method can apply search to match the specific criteria in case if the 
	 * provider is not able to do it completely.
	 * The provider is free to call the methods below with an arbitrary thread, so the
	 * implementation of these methods must be appropriately prepared.
	 * @param criteria {@link ICriteria}. Must not be <code>null</code>.
	 * @param listener the listener {@link IMessageSearchListener} to search. Must not be <code>null</code>. 
	 */
    public void search(ICriteria criteria, IMessageSearchListener listener);

    /**
	 * Create a specific criteria for the provider. Each provider must 
	 * implement a specific Criteria in a convenient approach for keep, 
	 * organize and deal with the {@link ICriterion}.
	 * @return {@link ICriteria} Will not be <code>null</code>
	 */
    public ICriteria createCriteria();

    /**
	 * Create a specific {@link IRestriction} implementation for the provider. 
	 * This implementation will provide the methods to created and organize 
	 * the {@link ICriterion} that composes the search. The {@link Restriction} 
	 * is a simple implementation of this, but subclasses or new implementation 
	 * may be created as appropriate.
	 * 
	 * @return {@link IRestriction} Will not be <code>null</code>
	 */
    public IRestriction createRestriction();
}
