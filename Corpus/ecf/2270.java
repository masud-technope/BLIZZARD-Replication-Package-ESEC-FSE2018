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
 * A representation of a query criterion for a restriction in a Criteria.
 * A Criteria is formed by one or several criterion, 
 * that together are able to compose an entire expression to project 
 * a result that match the criteria. The instances of this should be 
 * created for {@link IRestriction} implementations.
 * @since 2.0
 */
public interface ICriterion {

    /**
	 * Returns a expression composed for the search.
	 * The String can be something like 'field' + 'operator' + value, 
	 * that it will be interpreted for each specific provider.
	 * @return String
	 */
    public String toExpression();
}
