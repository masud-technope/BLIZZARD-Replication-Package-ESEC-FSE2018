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

import org.eclipse.ecf.presence.IIMMessage;

/**
 * Typing message. This object represents information about a using typing
 * during chat.
 */
public interface ITypingMessage extends IIMMessage {

    /**
	 * Indicates whether remote user is actually typing.
	 * 
	 * @return true if currently typing, false if currently stopped.
	 */
    public boolean isTyping();

    /**
	 * Get the contents of the typing
	 * 
	 * @return String contents of the typing. May return <code>null</code>.
	 */
    public String getBody();
}
