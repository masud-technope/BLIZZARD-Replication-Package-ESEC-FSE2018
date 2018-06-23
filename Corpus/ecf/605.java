/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc., Peter Nehrer, Boris Bokowski. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.datashare;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainerConfig;

public class DatashareContainerConfig implements ISharedObjectContainerConfig {

    ID containerID = null;

    Map properties = null;

    public  DatashareContainerConfig(ID containerID, Map properties) {
        this.containerID = containerID;
        this.properties = (properties == null) ? new HashMap() : properties;
    }

    public  DatashareContainerConfig(ID containerID) {
        this(containerID, null);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.ISharedObjectContainerConfig#getProperties()
	 */
    public Map getProperties() {
        return properties;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.ISharedObjectContainerConfig#getAdapter(java.lang.Class)
	 */
    public Object getAdapter(Class clazz) {
        return null;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IIdentifiable#getID()
	 */
    public ID getID() {
        return containerID;
    }
}
