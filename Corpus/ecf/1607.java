/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc., Peter Nehrer, Boris Bokowski. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.datashare;

import org.eclipse.ecf.datashare.events.IChannelContainerEvent;

/**
 * Listener for channel container events. The following types of events can be
 * received via this listener:
 * <p>
 * IChannelContainerChannelActivatedEvent - delivered when a channel within this
 * container is activated
 * <p>
 * IChannelContainerChannelDeactivatedEvent - delivered when a channel within
 * this container is deactivated
 * 
 */
public interface IChannelContainerListener {

    /**
	 * Handle channel container events.
	 * 
	 * @param event
	 *            IChannelContainerAdapter event. Will not be <code>null</code>.
	 */
    public void handleChannelContainerEvent(IChannelContainerEvent event);
}
