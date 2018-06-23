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
 * Base class implementation of {@link ICriterion} for logical criterion. 
 * Subclasses may be created as appropriate. It is just a simple 
 * implementation that can be considered for some provider or even used 
 * as base for some implementation.
 * @since 2.0
 */
public class LogicalCriterion implements ICriterion {

    final ICriterion left;

    final ICriterion right;

    final String operator;

    /**
	 * Creates a logic operation for compose a {@link ICriteria}
	 * @param left Criterion on the left side of the expression
	 * @param right Criterion on the right side of the expression
	 * @param operator Operator considered on the logical. Ex: And
	 */
    public  LogicalCriterion(ICriterion left, ICriterion right, String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public String toExpression() {
        return left.toExpression() + ' ' + operator + ' ' + right.toExpression();
    }

    public String toString() {
        //$NON-NLS-1$
        StringBuffer sb = new StringBuffer("LogicalCriterion[");
        //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("left=").append(left).append(";");
        //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("right=").append(right).append(";");
        //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("operator=").append(operator).append("]");
        return sb.toString();
    }
}
