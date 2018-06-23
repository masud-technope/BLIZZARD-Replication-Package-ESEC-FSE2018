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
package ch.ethz.iks.slp.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ch.ethz.iks.slp.ServiceLocationEnumeration;
import ch.ethz.iks.slp.ServiceLocationException;

/**
 * the implementation of a ServiceLocationEnumeration.
 * 
 * @see ch.ethz.iks.slp.ServiceLocationEnumeration
 * @author Jan S. Rellermeyer, IKS, ETH Z�rich
 * @since 0.1
 */
class ServiceLocationEnumerationImpl implements ServiceLocationEnumeration {

    /**
	 * a list of results.
	 */
    private List list;

    /**
	 * internal Iterator over the elements of the list.
	 */
    private Iterator iterator;

    /**
	 * creates a new ServiceLocationEnumerationImpl.
	 * 
	 * @param resultList
	 *            a list of results.
	 */
     ServiceLocationEnumerationImpl(final List resultList) {
        list = resultList != null ? resultList : new ArrayList();
        this.iterator = list.iterator();
    }

    /**
	 * returns the next element of the Enumeration.
	 * 
	 * @return the next element.
	 * @throws ServiceLocationException
	 *             if there is no more element.
	 * @see ch.ethz.iks.slp.ServiceLocationEnumeration#next()
	 */
    public synchronized Object next() throws ServiceLocationException {
        try {
            return iterator.next();
        } catch (Exception e) {
            throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR, e.getMessage());
        }
    }

    /**
	 * checks if the Enumeration has more elements.
	 * 
	 * @return true if there are more elements available.
	 */
    public synchronized boolean hasMoreElements() {
        return iterator.hasNext();
    }

    /**
	 * returns the next elenemt of the Enumeration.
	 * 
	 * @return the next element or null if there aren't any more.
	 */
    public synchronized Object nextElement() {
        try {
            return next();
        } catch (ServiceLocationException sle) {
            return null;
        }
    }

    public void testMethod() {
    // TODO Auto-generated method stub
    }
}
