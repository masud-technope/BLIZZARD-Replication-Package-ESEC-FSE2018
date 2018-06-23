/*******************************************************************************
 * Copyright (c) 2009 Versant Corp and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.discovery.ui.userinput;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;

public class UserInputNamespace extends Namespace {

    //$NON-NLS-1$
    public static final String NAME = "ecf.namespace.UserInputNamespace";

    private static final long serialVersionUID = 607013788248925596L;

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.Namespace#createInstance(java.lang.Object[])
	 */
    public ID createInstance(Object[] parameters) throws IDCreateException {
        if (parameters == null || parameters.length == 0 || parameters.length > 2) {
            throw new IDCreateException(Messages.UserInputNameSpace_INVALID_PARAMS);
        } else {
            throw new IDCreateException(Messages.UserInputNameSpace_INVALID_PARAMS);
        }
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.Namespace#getScheme()
	 */
    public String getScheme() {
        //$NON-NLS-1$
        return "userinput";
    }
}
