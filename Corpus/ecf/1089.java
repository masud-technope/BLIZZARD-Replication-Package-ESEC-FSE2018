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
package org.eclipse.ecf.telephony.call.events;

import java.util.Map;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.telephony.call.CallException;
import org.eclipse.ecf.telephony.call.CallSessionState;
import org.eclipse.ecf.telephony.call.ICallSession;
import org.eclipse.ecf.telephony.call.ICallSessionListener;

/**
 * Event received when a call request is received.
 */
public interface ICallSessionRequestEvent {

    /**
	 * Get the ID of the call initiator.
	 * 
	 * @return ID of initiator. Will not be <code>null</code>.
	 */
    public ID getInitiator();

    /**
	 * Get the ID of the intended call receiver. Should be an ID known to this
	 * receiver.
	 * 
	 * @return ID of intended receiver. Will not be <code>null</code>.
	 */
    public ID getReceiver();

    /**
	 * Get ID uniquely identifying the call session.
	 * 
	 * @return ID identifying the call session. Will not be <code>null</code>.
	 */
    public ID getSessionID();

    /**
	 * Get map of properties associated with the call request.
	 * 
	 * @return Map of properties associated with the call request. Will not be
	 *         <code>null</code>.
	 */
    public Map getProperties();

    /**
	 * Get CallSessionState for this request. Typically, the returned
	 * CallSessionState instance will be {@link CallSessionState#PENDING},
	 * meaning that the call is pending subsequent acceptance by this request
	 * listener (via athe {@link #accept(ICallSessionListener, Map)} call. Can
	 * be other values, however, depending upon the actual call state.
	 * 
	 * @return CallSessionState the
	 */
    public CallSessionState getCallSessionState();

    /**
	 * Accept the incoming call request. If the call is to be answered,
	 * receivers of this event should call this method and provide the
	 * appropriate listener and optional properties. Either this method or the
	 * {@link #reject()} method should be called, depending upon whether the
	 * call should be accepted (answered) or not.
	 * 
	 * @param listener
	 *            the {@link ICallSessionListener} to handle
	 *            {@link ICallSessionEvent}s. Must not be <code>null</code>.
	 * @return ICallSession that represents the call session once
	 *         answered/accepted.
	 * @throws CallException
	 *             if some problem accepting the requested call (e.g. the
	 *             initiator has dropped).
	 */
    public ICallSession accept(ICallSessionListener listener, Map properties) throws CallException;

    /**
	 * Reject the incoming call request. If the call is to be rejected,
	 * receivers of this event should call this method. Either this method or
	 * the {@link #accept(ICallSessionListener, Map)} method should be called,
	 * depending upon whether the call should be rejected or accepted
	 * (answered). If {@link #accept(ICallSessionListener, Map)} has previously
	 * been called successfully, calling this method has no effect.
	 */
    public void reject();
}
