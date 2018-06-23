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
 * Instances implementing this interface provide methods to compose the corrects operations for each
 * provide
 * @since 2.0
 */
public interface IRestriction {

    /**
	 * Apply an "add" constraint to the two criterion
	 * @param left Will not be <code>null</code>
	 * @param right Will not be <code>null</code>
	 * @return ICriterion
	 */
    public ICriterion and(ICriterion left, ICriterion right);

    /**
	 * Apply an "equal" constraint to the field, ignoring case
	 * @param field Will not be <code>null</code>
	 * @param value May be <code>null</code> 
	 * @return ICriterion
	 */
    public ICriterion eq(String field, String value);

    /**
	 * Apply an "not equal" constraint to the field ignoring case
	 * @param field Will not be <code>null</code>
	 * @param value May be <code>null</code>
	 * @return ICriterion
	 */
    public ICriterion ne(String field, String value);

    /**
	 * Apply an "equal" constraint to the field, taking into consideration ignore case
	 * @param field Will not be <code>null</code>
	 * @param value May be <code>null</code>
	 * @param ignoreCase
	 * @return ICriterion
	 */
    public ICriterion eq(String field, String value, boolean ignoreCase);

    /**
	 * Apply an "not equal" constraint to the field, taking into consideration ignore case
	 * @param field Will not be <code>null</code>
	 * @param value May be <code>null</code>
	 * @param ignoreCase
	 * @return ICriterion
	 */
    public ICriterion ne(String field, String value, boolean ignoreCase);
}
