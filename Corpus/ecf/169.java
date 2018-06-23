/****************************************************************************
 * Copyright (c) 2005 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Chris Aniszczyk <zx@us.ibm.com> - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.presence;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ecf.internal.presence.PresencePlugin;
import org.eclipse.ecf.presence.chatroom.IChatRoomManager;
import org.eclipse.ecf.presence.service.IPresenceService;

/**
 * An abstract {@link IPresenceContainerAdapter} implementation. This class is
 * intended to be subclassed.
 */
public abstract class AbstractPresenceContainer implements IPresenceService {

    /**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
    public Object getAdapter(Class adapter) {
        IAdapterManager adapterManager = PresencePlugin.getDefault().getAdapterManager();
        if (adapterManager == null)
            return null;
        return adapterManager.getAdapter(this, adapter);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.IPresenceContainerAdapter#getAccountManager()
	 */
    public IAccountManager getAccountManager() {
        return null;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.presence.IPresenceContainerAdapter#getChatRoomManager()
	 */
    public IChatRoomManager getChatRoomManager() {
        return null;
    }
}
