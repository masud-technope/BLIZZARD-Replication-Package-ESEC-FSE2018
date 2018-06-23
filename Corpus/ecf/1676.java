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
package org.eclipse.ecf.presence.im;

import org.eclipse.ecf.presence.IIMMessageEvent;

/**
 * Chat message event.
 */
public interface IXHTMLChatMessageEvent extends IIMMessageEvent {

    /**
	 * Get XHTML chat message sent.
	 * 
	 * @return IXHTMLChatMessage sent to the receiver. Will not be
	 *         <code>null</code>.
	 */
    public IXHTMLChatMessage getXHTMLChatMessage();
}
