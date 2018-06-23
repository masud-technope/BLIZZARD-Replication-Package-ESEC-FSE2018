/****************************************************************************
 * Copyright (c) 2004 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.provider.generic;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainerConfig;

public class SOContainerConfig implements ISharedObjectContainerConfig {

    ID id;

    Map properties;

    public  SOContainerConfig(ID id, Map props) {
        Assert.isNotNull(id);
        this.id = id;
        this.properties = (props == null) ? new HashMap() : props;
    }

    public  SOContainerConfig(ID id) {
        Assert.isNotNull(id);
        this.id = id;
        this.properties = new HashMap();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContainerConfig#getProperties()
	 */
    public Map getProperties() {
        return properties;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContainerConfig#getAdapter(java.lang.Class)
	 */
    public Object getAdapter(Class clazz) {
        return null;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IIdentifiable#getID()
	 */
    public ID getID() {
        return id;
    }
}
