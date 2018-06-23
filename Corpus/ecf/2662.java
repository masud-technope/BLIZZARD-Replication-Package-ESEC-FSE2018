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

import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;

/**
 * Event received when remote call started.
 * 
 */
public interface IRemoteCallStartEvent extends IRemoteCallEvent {

    /**
	 * Get remote service reference used for call.
	 * 
	 * @return IRemoteServiceReference used to make call. Will not be
	 *         <code>null</code>.
	 */
    public IRemoteServiceReference getReference();

    /**
	 * Get the remote call itself.
	 * 
	 * @return IRemoteCall actually started. Will not be <code>null</code>
	 */
    public IRemoteCall getCall();
}
