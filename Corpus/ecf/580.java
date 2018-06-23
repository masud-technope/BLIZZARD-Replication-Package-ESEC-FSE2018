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

import org.eclipse.ecf.presence.search.SimpleCriterion;

/**
 * Implement specific for ICriterion
 * @since 3.0
 */
public class XMPPSimpleCriterion extends SimpleCriterion {

    public  XMPPSimpleCriterion(String field, String value, String operator, boolean ignoreCase) {
        super(field, value, operator, ignoreCase);
    }

    public  XMPPSimpleCriterion(String field, String value, String operator) {
        super(field, value, operator);
    }

    /**
	 * Provide the expression compose just for the value
	 */
    public String toExpression() {
        return value;
    }
}
