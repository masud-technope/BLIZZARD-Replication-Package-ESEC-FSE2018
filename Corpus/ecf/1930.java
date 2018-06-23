/****************************************************************************
 * Copyright (c) 2005, 2010 Jan S. Rellermeyer, Systems Group,
 * Department of Computer Science, ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jan S. Rellermeyer - initial API and implementation
 *    Markus Alexander Kuppe - enhancements and bug fixes
 *
*****************************************************************************/
package ch.ethz.iks.slp.impl.filter;

import java.util.Dictionary;

/**
 * a generic LDAP filter.
 * @author Jan S. Rellermeyer, ETH Zurich
 *
 */
public interface Filter {

    /**
     * try to match a <code>Dictionary</code> of attributes.
     * @param values a <code>Dictionary</code> of attributes.
     * @return true if the filter evaluated to true;
     */
    boolean match(Dictionary values);

    /**
     * get a String representation of the filter.
     * @return the String representation.
     */
    String toString();
}
