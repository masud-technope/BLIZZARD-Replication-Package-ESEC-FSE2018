/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc.. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.telephony.call.events;

import org.eclipse.ecf.telephony.call.CallSessionState;
import org.eclipse.ecf.telephony.call.ICallSession;
import org.eclipse.ecf.telephony.call.ICallSessionListener;

/**
 * Event received upon changes to the {@link CallSessionState}
 * of {@link ICallSession} instances.
 */
public interface ICallSessionEvent {

    /**
	 * Get the underlying {@link ICallSession} that is
	 * responsible for this event.  Receivers of this event
	 * (via {@link ICallSessionListener}) can get the
	 * ICallSession and call methods on that ICallSession when
	 * received.
	 * 
	 * @return ICallSession of the underlying ICallSession.  Will not be <code>null</code>.
	 */
    public ICallSession getCallSession();
}
