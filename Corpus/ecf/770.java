/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.sharedobject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.ecf.core.identity.ID;

/**
 * Description of a local ISharedObject instance.
 * 
 */
public class SharedObjectDescription implements Serializable {

    private static final long serialVersionUID = -999672007680512082L;

    protected SharedObjectTypeDescription typeDescription;

    protected ID id;

    protected Map<String, ?> properties = null;

    /**
	 * @since 2.3
	 */
    public  SharedObjectDescription(SharedObjectTypeDescription typeDescription, ID id, Map<String, ?> properties) {
        this.typeDescription = typeDescription;
        this.id = id;
        this.properties = (properties == null) ? new HashMap<String, Object>() : properties;
    }

    /**
	 * @since 2.3
	 */
    public  SharedObjectDescription(SharedObjectTypeDescription typeDescription, ID id) {
        this(typeDescription, id, null);
    }

    /**
	 * @since 2.3
	 */
    public  SharedObjectDescription(String typeName, ID id) {
        this(typeName, id, null);
    }

    public  SharedObjectDescription(String typeName, ID id, Map<String, ?> properties) {
        this.typeDescription = new SharedObjectTypeDescription(typeName, null, null, null);
        this.id = id;
        this.properties = (properties == null) ? new HashMap<String, Object>() : properties;
    }

    public  SharedObjectDescription(Class clazz, ID id, Map<String, ?> properties) {
        this.typeDescription = new SharedObjectTypeDescription(clazz.getName(), null);
        this.id = id;
        this.properties = (properties == null) ? new HashMap<String, Object>() : properties;
    }

    public SharedObjectTypeDescription getTypeDescription() {
        return typeDescription;
    }

    public ID getID() {
        return id;
    }

    public Map<String, ?> getProperties() {
        return properties;
    }

    public String toString() {
        //$NON-NLS-1$
        StringBuffer sb = new StringBuffer("SharedObjectDescription[");
        //$NON-NLS-1$ 
        sb.append("typeDescription=").append(typeDescription);
        //$NON-NLS-1$ 
        sb.append(";id=").append(id);
        //$NON-NLS-1$ //$NON-NLS-2$ 
        sb.append(";props=").append(properties).append("]");
        return sb.toString();
    }
}
