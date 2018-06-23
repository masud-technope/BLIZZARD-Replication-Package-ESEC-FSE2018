/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc.. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.telephony.call;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IIdentifiable;

/**
 * Contract for interacting with an arbitrary call session.
 * 
 */
public interface ICallSession extends IIdentifiable, IAdaptable {

    /**
	 * Get ID of initiator. Will not be <code>null</code>.
	 * 
	 * @return ID of initiator. Will not be <code>null</code>.
	 */
    public ID getInitiator();

    /**
	 * Get ID of receiver. Will not be <code>null</code>.
	 * 
	 * @return ID of receiver. Will not be <code>null</code>.
	 */
    public ID getReceiver();

    /**
	 * Get listener for this call session instance.
	 * 
	 * @return ICallSessionListener for this ICallSession. Will not be
	 *         <code>null</code>.
	 */
    public ICallSessionListener getListener();

    /**
	 * Get call session state
	 * 
	 * @return CallSessionState current call session state
	 */
    public CallSessionState getState();

    /**
	 * Get the reason for failure.  Will be <code>null</code> unless
	 * the returned value from {@link #getState()} is {@link CallSessionState#FAILED}.
	 * 
	 * @return CallSessionFailureReason associated with previous call failure.  Will be
	 * <code>null</code> unless the current CallSessionState is CallSessionState.FAILED.
	 */
    public CallSessionFailureReason getFailureReason();

    /**
	 * Get error information.  Will be <code>null</code> unless the returned value from
	 * {@link #getState()} is {@link CallSessionState#ERROR}
	 * 
	 * @return CallSessionErrorDetails instance associated with {@link CallSessionState#ERROR}
	 */
    public CallSessionErrorDetails getErrorDetails();

    /**
	 * Send terminate message to given receiver
	 * 
	 * @throws CallException
	 *             exception if cannot send terminate
	 */
    public void sendTerminate() throws CallException;
}
