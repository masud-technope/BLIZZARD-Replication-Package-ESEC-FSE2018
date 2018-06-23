/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc., Peter Nehrer, Boris Bokowski. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.datashare.events;

import org.eclipse.ecf.core.identity.ID;

/**
 * Channel message event. This event is received by IChannelListeners when a
 * remote sends a message (via IChannel.sendMessage)
 * 
 */
public interface IChannelMessageEvent extends IChannelEvent {

    /**
	 * Get ID of sender container
	 * 
	 * @return ID of sender's container. Will not be <code>null</code>.
	 */
    public ID getFromContainerID();

    /**
	 * Get data associated with message. This method returns the data actually
	 * included in the IChannel.sendMessage(<data>).
	 * 
	 * @return byte [] data associated with channel message. Will not be
	 *         <code>null</code>, but may be empty array byte[0].
	 */
    public byte[] getData();
}
