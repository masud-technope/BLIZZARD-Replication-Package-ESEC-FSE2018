/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.core.util;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;

/**
 *
 */
public class ConnectedContainerFilter implements IContainerFilter {

    private ID result;

    public  ConnectedContainerFilter() {
    // XXX nothing to do
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.util.IContainerFilter#match(org.eclipse.ecf.core.IContainer)
	 */
    public boolean match(IContainer containerToMatch) {
        result = containerToMatch.getConnectedID();
        return result != null;
    }

    public ID getResult() {
        return result;
    }
}
