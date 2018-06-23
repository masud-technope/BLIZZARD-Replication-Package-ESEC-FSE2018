/**
 * Copyright (c) 2006 Parity Communications, Inc. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sergey Yakovlev - initial API and implementation
 */
package org.eclipse.ecf.internal.provider.rss.container;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.provider.generic.GenericContainerInstantiator;

/**
 * 
 */
public class RssContainerInstantiator extends GenericContainerInstantiator {

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#createInstance(org.eclipse.ecf.core.ContainerDescription,
	 *      java.lang.Class[], java.lang.Object[])
	 */
    public IContainer createInstance(ContainerTypeDescription description, Object[] args) throws ContainerCreateException {
        try {
            Integer keepAlive = new Integer(RssClientSOContainer.DEFAULT_KEEPALIVE);
            String name = null;
            if (args != null) {
                if (args.length > 0) {
                    name = (String) args[0];
                    if (args.length > 1) {
                        keepAlive = getIntegerFromArg(args[1]);
                    }
                }
            }
            if (name == null) {
                if (keepAlive == null) {
                    return new RssClientSOContainer();
                } else {
                    return new RssClientSOContainer(keepAlive.intValue());
                }
            } else {
                if (keepAlive == null) {
                    keepAlive = new Integer(RssClientSOContainer.DEFAULT_KEEPALIVE);
                }
                return new RssClientSOContainer(name, keepAlive.intValue());
            }
        } catch (Exception e) {
            throw new ContainerCreateException("Exception creating RSS container", e);
        }
    }

    protected Integer getIntegerFromArg(Object arg) throws NumberFormatException {
        if (arg instanceof Integer) {
            return (Integer) arg;
        } else if (arg != null) {
            return new Integer((String) arg);
        } else {
            return new Integer(-1);
        }
    }
}
