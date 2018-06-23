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

import java.util.List;

/**
 * Criteria helps for retrieving results from the provider search by composing Criterion objects. 
 * The {@link IUserSearchManager} is a factory for Criteria. Criterion instances are obtained via ISelection methods.
 * Criteria can deal with different kind of criterion, as logical or just simple expression.
 * 
 *  The typical usage of the Criteria is as
 * follows:
 * 
 * <pre>
 *      ISelection selection = ...
 *		ICriterion name = selection.eq("name", "value");
 *		ICriterion host = selection.eq("host", "value");
 *		ICriterion and = selection.and(name, host);
 *		ICriteria criteria = ...
 *		criteria.add(and);
 * </pre>
 * 
 * @since 2.0
 */
public interface ICriteria {

    /**
	 * Add a criterion that composes the criteria
	 * @param criterion Will not be <code>null</code>
	 */
    public void add(ICriterion criterion);

    /**
	 * A list of all criterion added to the criteria
	 * @return List of {@link ICriterion} Will not be <code>null</code>
	 */
    public List getCriterions();

    /**
	 * Notify if there is or not criterion added for this criteria
	 * @return indicate if there are or not criterion
	 */
    public boolean isEmpty();
}
