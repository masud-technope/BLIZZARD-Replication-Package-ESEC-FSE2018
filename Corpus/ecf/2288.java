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
 * Base class implementation of {@link ICriterion} for simple comparisons. Subclasses may be created as
 * appropriate.
 * @since 2.0
 */
public class SimpleCriterion implements ICriterion {

    protected final String field;

    protected final String value;

    protected final String operator;

    protected final boolean ignoreCase;

    /**
	 * Constructor for a instance of {@link ICriterion}
	 * @param field Name of the field
	 * @param value Value that match the search
	 * @param operator Operator opportune for the search 
	 * @param ignoreCase Consider case or not
	 */
    public  SimpleCriterion(String field, String value, String operator, boolean ignoreCase) {
        this.field = field;
        this.value = value;
        this.operator = operator;
        this.ignoreCase = ignoreCase;
    }

    /**
	 * Constructor for a instance of {@link ICriterion}. This is 
	 * ignore case
	 * @param field Name of the field
	 * @param value Value that match the search
	 * @param operator Operator opportune for the search 
	 */
    public  SimpleCriterion(String field, String value, String operator) {
        this.field = field;
        this.value = value;
        this.operator = operator;
        this.ignoreCase = false;
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.ecf.presence.search.ICriterion#toExpression()
	 */
    public String toExpression() {
        return field + ' ' + operator + ' ' + value;
    }

    public String toString() {
        //$NON-NLS-1$
        StringBuffer sb = new StringBuffer("SimpleCriterion[");
        //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("field=").append(field).append(";");
        //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("value=").append(value).append(";");
        //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("operator=").append(operator).append(";");
        //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("ignoreCase=").append(ignoreCase).append("]");
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof String) {
            return o.equals(field);
        }
        return false;
    }

    public int hashCode() {
        return (field != null ? field.hashCode() : 0);
    }
}
