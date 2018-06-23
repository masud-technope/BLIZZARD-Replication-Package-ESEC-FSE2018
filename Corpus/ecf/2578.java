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

import java.util.List;

/**
 * A chat message that has XHTML bodies.
 */
public interface IXHTMLChatMessage extends IChatMessage {

    /**
	 * Get List of html bodies. Each element of this list will be a String
	 * minimally containing the html <body></body> elements and contents.
	 * 
	 * @return List of HTML bodies. Will not return <code>null</code>, but
	 *         may return empty list.
	 */
    public List getXTHMLBodies();
}
