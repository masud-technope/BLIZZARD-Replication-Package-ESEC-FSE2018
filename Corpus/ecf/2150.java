/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc.. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.telephony.call;

import java.util.Map;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.telephony.call.events.ICallSessionEvent;

/**
 * Call session container entry point adapter. This interface is an adapter to
 * allow providers to expose call sessions to clients. It may be used in the
 * following way:
 * <p>
 * 
 * <pre>
 *  ICallSessionContainerAdapter callcontainer = (ICallSessionContainerAdapter) container.getAdapter(ICallSessionContainerAdapter.class);
 *  if (callcontainer != null) {
 *     // use callcontainer to create call sessions
 *     ...
 *  } else {
 *     // container does not support call container functionality
 *  }
 * </pre>
 * 
 */
public interface ICallSessionContainerAdapter extends IAdaptable {

    /**
	 * Get namespace for creating IDs for receiving call initiation requests.
	 * 
	 * @return Namespace for creating IDs for call initiation requests.
	 */
    public Namespace getReceiverNamespace();

    /**
	 * Add listener for call session request event reception
	 * 
	 * @param listener
	 *            the ICallSessionRequestListener to receive call session request events
	 */
    public void addCallSessionRequestListener(ICallSessionRequestListener listener);

    /**
	 * Remove listener for call session request event reception
	 * 
	 * @param listener
	 *            the ICallSessionRequestListener to remove
	 */
    public void removeCallSessionRequestListener(ICallSessionRequestListener listener);

    /**
	 * Initiate call. This is the entry point for initiating calls.
	 * 
	 * @param receivers
	 *            the intended receivers of the call initiation request. Must
	 *            not be <code>null</code>.
	 * @param listener
	 *            a listener for asynchronous {@link ICallSessionEvent}. Must
	 *            not be <code>null</code>.
	 * @param properties an optional Map of properties.  May be <code>null</code>.
	 * @throws CallException if call request cannot be sent
	 */
    public void sendCallRequest(ID[] receivers, ICallSessionListener listener, Map properties) throws CallException;

    /**
	 * Initiate call. This is the entry point for initiating calls.
	 * 
	 * @param receiver
	 *            the intended receiver of the call initiation request. Must not
	 *            be <code>null</code>.
	 * @param listener
	 *            a listener for asynchronous {@link ICallSessionEvent}. Must
	 *            not be <code>null</code>.
	 * @param properties an optional Map of properties.  May be <code>null</code>.
	 * @throws CallException if call request cannot be sent
	 */
    public void sendCallRequest(ID receiver, ICallSessionListener listener, Map properties) throws CallException;
}
