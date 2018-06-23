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
 * This Class implements {@link IRestriction}. Subclasses may be created as
 * appropriate. It is just a simple implementation that can be considered 
 * for some provider or even used as base for some implementation.
 * @since 2.0
 */
public class Restriction implements IRestriction {

    /** Operator Equal */
    //$NON-NLS-1$
    public static final String OPERATOR_EQ = "=";

    /** Operator Not Equal */
    //$NON-NLS-1$
    public static final String OPERATOR_NE = "!=";

    /** Operator AND */
    //$NON-NLS-1$
    public static final String OPERATOR_AND = "&&";

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.ecf.presence.search.ISelection#and(org.eclipse.ecf.presence.search.ICriterion, org.eclipse.ecf.presence.search.ICriterion)
	 */
    public ICriterion and(ICriterion left, ICriterion right) {
        return new LogicalCriterion(left, right, OPERATOR_AND);
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.ecf.presence.search.ISelection#eq(java.lang.String, java.lang.String)
	 */
    public ICriterion eq(String field, String value) {
        return new SimpleCriterion(field, value, OPERATOR_EQ);
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.ecf.presence.search.ISelection#ne(java.lang.String, java.lang.String)
	 */
    public ICriterion ne(String field, String value) {
        return new SimpleCriterion(field, value, OPERATOR_NE);
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.ecf.presence.search.ISelection#eq(java.lang.String, java.lang.String, boolean)
	 */
    public ICriterion eq(String field, String value, boolean ignoreCase) {
        //TODO implement a criterion that supports ignore case approach
        return new SimpleCriterion(field, value, OPERATOR_EQ);
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.ecf.presence.search.ISelection#ne(java.lang.String, java.lang.String, boolean)
	 */
    public ICriterion ne(String field, String value, boolean ignoreCase) {
        //TODO implement a criterion that supports ignore case approach
        return new SimpleCriterion(field, value, OPERATOR_NE);
    }
}
