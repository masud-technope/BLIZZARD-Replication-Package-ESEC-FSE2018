/**
 * Copyright (c) 2006 Ecliptical Software Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ecliptical Software Inc. - initial API and implementation
 */
package org.eclipse.ecf.pubsub;

import java.io.Serializable;
import java.util.Map;
import org.eclipse.ecf.core.identity.ID;

public class PublishedServiceDescriptor implements Serializable {

    private static final long serialVersionUID = -3152226289167000325L;

    private final ID containerID;

    private final ID sharedObjectID;

    private final Map properties;

    public  PublishedServiceDescriptor(ID containerID, ID sharedObjectID, Map properties) {
        this.containerID = containerID;
        this.sharedObjectID = sharedObjectID;
        this.properties = properties;
    }

    public ID getContainerID() {
        return containerID;
    }

    public ID getSharedObjectID() {
        return sharedObjectID;
    }

    public Map getProperties() {
        return properties;
    }

    public int hashCode() {
        int c = 17;
        c = 37 * c + containerID.hashCode();
        c = 37 * c + sharedObjectID.hashCode();
        c = 37 * c + properties.hashCode();
        return c;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PublishedServiceDescriptor other = (PublishedServiceDescriptor) obj;
        return containerID.equals(other.containerID) && sharedObjectID.equals(other.sharedObjectID) && properties.equals(other.properties);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("PublishedServiceDescriptor[");
        buf.append("containerID=").append(containerID).append(';');
        buf.append("sharedObjectID=").append(sharedObjectID).append(';');
        buf.append("properties=").append(properties).append(']');
        return buf.toString();
    }
}
