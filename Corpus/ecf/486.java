/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.presence;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ecf.internal.presence.PresencePlugin;

/**
 * Base presence class implementing {@link IPresence}. Subclasses may be
 * created as appropriate.
 * 
 */
public class Presence implements IPresence {

    private static final long serialVersionUID = 3906369346107618354L;

    protected Type type;

    protected Mode mode;

    protected String status;

    protected Map properties;

    protected byte[] pictureData;

    public  Presence() {
        this(Type.AVAILABLE);
    }

    public  Presence(Type type) {
        this(type, null, Mode.AVAILABLE);
    }

    public  Presence(Type type, String status, Mode mode, Map props, byte[] picture) {
        this.type = (type == null) ? IPresence.Type.AVAILABLE : type;
        this.status = status;
        this.mode = (mode == null) ? IPresence.Mode.AVAILABLE : mode;
        this.properties = (props == null) ? new HashMap() : props;
        this.pictureData = (picture == null) ? new byte[0] : picture;
    }

    public  Presence(Type type, String status, Mode mode, Map props) {
        this(type, status, mode, props, null);
    }

    public  Presence(Type type, String status, Mode mode) {
        this(type, status, mode, null);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.roster.IPresence#getMode()
	 */
    public Mode getMode() {
        return mode;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.roster.IPresence#getProperties()
	 */
    public Map getProperties() {
        return properties;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.roster.IPresence#getStatus()
	 */
    public String getStatus() {
        return status;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.roster.IPresence#getType()
	 */
    public Type getType() {
        return type;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
    public Object getAdapter(Class adapter) {
        IAdapterManager adapterManager = PresencePlugin.getDefault().getAdapterManager();
        if (adapterManager == null)
            return null;
        return adapterManager.loadAdapter(this, adapter.getName());
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.IPresence#getPictureData()
	 */
    public byte[] getPictureData() {
        return pictureData;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        //$NON-NLS-1$
        StringBuffer sb = new StringBuffer("Presence[");
        //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("type=").append(type).append(";");
        //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("mode=").append(mode).append(";");
        //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("status=").append(status).append(";");
        //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("props=").append(properties).append(";");
        //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("picture=").append(pictureData).append("]");
        return sb.toString();
    }
}
