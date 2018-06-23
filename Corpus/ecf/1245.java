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
package org.eclipse.ecf.presence.ui.chatroom;

/**
 * Listener for chat room view closing events.
 */
public interface IChatRoomViewCloseListener {

    /**
	 * If a non-<code>null</code> instance of this listener is provided to
	 * the {@link ChatRoomManagerView} this method will be called when the view
	 * is closing. 
	 * 
	 */
    public void chatRoomViewClosing();
}
