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

/** 
 * This Interface allows for implementing classes to execute the search
 * for messages in a non block way. Note these methods will be called asynchronously not blocking a search action.
 * The provider is free to call the methods below with an arbitrary thread, so the
 * implementation of these methods must be appropriately prepared.
 * @since 2.0
 */
public interface IMessageSearchListener {

    /**
	 * Catch the event fired and proceed to complete the search.
	 * Handle the search asynchronously. Notify that the search was completed 
	 * for the specific criteria.
	 * @param event the object that contains the criteria {@link String}
	 * and deal with the results in a non-blocking way
	 */
    public void handleMessageSearchEvent(IMessageSearchEvent event);
}
