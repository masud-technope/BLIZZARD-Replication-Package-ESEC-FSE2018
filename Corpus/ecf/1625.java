/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jacek Pospychala <jacek.pospychala@pl.ibm.com> - bug 197329
 *****************************************************************************/
package org.eclipse.ecf.presence.ui.chatroom;

import org.eclipse.swt.custom.StyleRange;

/**
 * Renders chat line, by arranging text content to be finally printed to 
 * Chat room output together with it's formatting.
 *
 */
public interface IMessageRenderer {

    /**
	 * Returns text content to be finally printed to chat room output.
	 * @param message chat message to be processed
	 * @param originator name of message sender
	 * @param localUserName local user name 
	 * @return text to be printed to output, nothing will be printed if null
	 */
    String render(String message, String originator, String localUserName);

    /**
	 * Returns formatting to be applied to rendered final output, returned by {@link #render(String, String, String)}.
	 * @return formatting to be applied to output, or null if no formatting
	 */
    StyleRange[] getStyleRanges();
}
