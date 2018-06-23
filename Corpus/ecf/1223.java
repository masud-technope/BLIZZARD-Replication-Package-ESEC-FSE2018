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

import org.eclipse.ecf.presence.search.ISearch;

/**
 * 
 * @since 2.0
 *
 */
public interface IMessageSearchCompleteEvent extends IMessageSearchEvent {

    /**
	 * Provide the result for a non-blocking search
	 * @return ISearch
	 */
    public ISearch getSearch();
}
