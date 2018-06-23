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
 * Remote call event. Event received by service when a remote call should be
 * processed.
 */
public interface IRemoteCallEvent {

    /**
	 * Get request id for the given remote call
	 * 
	 * @return long request ID.
	 */
    public long getRequestId();
}
