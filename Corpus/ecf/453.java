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

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.Event;

/**
 * Message event class. This event interface provides a wrapper for
 * {@link IIMMessage}s received from remotes.
 */
public interface IIMMessageEvent extends Event {

    /**
	 * Get the ID of the sender of the chat message.
	 * 
	 * @return ID of the sender of the message. Will not be <code>null</code>.
	 */
    public ID getFromID();
}
