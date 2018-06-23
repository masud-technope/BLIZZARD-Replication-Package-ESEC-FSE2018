/*******************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.ecf.presence.search.message;

import org.eclipse.ecf.presence.im.IChatMessage;
import org.eclipse.ecf.presence.search.IResult;

/**
 * Each result returned for the message search will be reach through this interface.
 * The result contain the message that match the search.
 * @since 2.0
 */
public interface IMessageResult extends IResult {

    /**
	 * Get message for the search. This is the message that comes from the search.
	 * 
	 * @return {@link IChatMessage} message associated with the search. Will not be <code>null</code>
	 */
    public IChatMessage getMessage();
}
