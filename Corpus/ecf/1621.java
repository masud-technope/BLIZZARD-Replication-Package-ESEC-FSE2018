/*******************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.ecf.presence.search;

import java.util.*;

/**
 * Abstract implementation of IUserSearchManager. Provides implementations of listener
 * methods that subsclasses may use to avoid having to implement them
 * themselves. This class may be subclassed as needed.
 * @since 2.0
 */
public abstract class AbstractUserSearchManager implements IUserSearchManager {

    /** keep the list of listeners */
    private final List userSearchListeners = new ArrayList(5);

    /**
	 * Add listener to {@link IUserSearchListener}. The listener's handleEvent method will be
	 * asynchronously called.
	 * @param l
	 * 				 the IUserSearchListener to add
	 */
    public void addListener(IUserSearchListener l) {
        synchronized (userSearchListeners) {
            userSearchListeners.add(l);
        }
    }

    /**
	 * Remove listener from the listener list.
	 * 
	 * @param l
	 *            the IUserSearchListener to remove
	 */
    public void removeListener(IUserSearchListener l) {
        synchronized (userSearchListeners) {
            userSearchListeners.remove(l);
        }
    }

    /**
	 * Fires a user search event, invoking the {@link IUserSearchListener}.
	 * 
	 * @param event IUserSearchEvent.
	 */
    protected void fireUserSearchEvent(IUserSearchEvent event) {
        List toNotify = null;
        // Copy array
        synchronized (userSearchListeners) {
            toNotify = new ArrayList(userSearchListeners);
        }
        // Notify all in toNotify
        for (Iterator i = toNotify.iterator(); i.hasNext(); ) {
            IUserSearchListener l = (IUserSearchListener) i.next();
            l.handleUserSearchEvent(event);
        }
    }
}
