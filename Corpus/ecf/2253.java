/****************************************************************************
 * Copyright (c) 2004, 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.remoteservice.events;

/**
 * Event received when remote call is complete.
 */
public interface IRemoteCallCompleteEvent extends IRemoteCallEvent {

    /**
	 * Get response object.
	 * 
	 * @return Object that is response to remmote call. May be <code>null</code>.
	 */
    public Object getResponse();

    /**
	 * Whether remote call resulted in exception. If returns true, then remote
	 * call ended in exception, false if no exception.
	 * 
	 * @return true if remote call ended in exception, false if no exception.
	 */
    public boolean hadException();

    /**
	 * @return Throwable that was exception thrown during remote call. Will
	 *         return <code>null</code> if {@link #hadException()} returns
	 *         false. Will be non-<code>null</code> if
	 *         {@link #hadException()} returns true.
	 * 
	 */
    public Throwable getException();
}
