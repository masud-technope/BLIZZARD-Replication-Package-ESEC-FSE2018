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

import org.eclipse.ecf.presence.search.ICriterion;
import org.eclipse.ecf.presence.search.Restriction;

/**
 * Implement a specific Selection for XMPP
 * @since 3.0
 */
public class XMPPSelection extends Restriction {

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.ecf.presence.search.ISelection#eq(java.lang.String, java.lang.String)
	 */
    public ICriterion eq(String field, String value) {
        //the operator is ignored for XMPP
        return new XMPPSimpleCriterion(field, value, "");
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.ecf.presence.search.ISelection#eq(java.lang.String, java.lang.String)
	 */
    public ICriterion eq(String field, String value, boolean ignoreCase) {
        //the operator is ignored for XMPP
        return new XMPPSimpleCriterion(field, value, "");
    }
}
