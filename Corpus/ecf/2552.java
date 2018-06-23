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
package org.eclipse.ecf.core.sharedobject.events;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.Event;
import org.eclipse.equinox.concurrent.future.IFuture;

public class SharedObjectCallEvent implements ISharedObjectCallEvent {

    ID sender;

    Event event;

    IFuture result;

    /**
	 * @since 2.0
	 */
    public  SharedObjectCallEvent(ID sender, Event evt, IFuture res) {
        super();
        this.sender = sender;
        this.event = evt;
        this.result = res;
    }

    /**
	 * @since 2.0
	 */
    public IFuture getAsyncResult() {
        return result;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.events.ISharedObjectEvent#getSenderSharedObjectID()
	 */
    public ID getSenderSharedObjectID() {
        return sender;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.events.ISharedObjectEvent#getEvent()
	 */
    public Event getEvent() {
        return event;
    }
}
