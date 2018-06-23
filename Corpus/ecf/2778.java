/*******************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.ecf.presence.search;

import org.eclipse.ecf.core.util.Event;

/**
 * An event received by a user search. This interface address the events 
 * that happens on user search API. There be different sub-interfaces of IUserSearchEvent to
 * represent different types of events.
 * @since 2.0
 */
public interface IUserSearchEvent extends Event {
    // no methods for interface
}
