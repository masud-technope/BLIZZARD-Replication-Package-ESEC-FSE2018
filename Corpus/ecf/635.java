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
package org.eclipse.ecf.telephony.call;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Type-safe enumeration class to represent call session state information. See
 * {@link ICallSession#getState()}.
 */
public class CallSessionState implements Serializable {

    protected static final long serialVersionUID = -3223811084140684042L;

    //$NON-NLS-1$
    protected static final String UNPLACED_NAME = "unplaced";

    //$NON-NLS-1$
    protected static final String REDIRECTED_NAME = "redirected";

    //$NON-NLS-1$
    protected static final String ROUTING_NAME = "routing";

    //$NON-NLS-1$
    protected static final String PREPENDING_NAME = "prepending";

    //$NON-NLS-1$
    protected static final String FAILED_NAME = "failed";

    //$NON-NLS-1$
    protected static final String PENDING_NAME = "pending";

    //$NON-NLS-1$
    protected static final String ACTIVE_NAME = "active";

    //$NON-NLS-1$
    protected static final String ONHOLD_NAME = "onhold";

    //$NON-NLS-1$
    protected static final String FINISHED_NAME = "finished";

    //$NON-NLS-1$
    protected static final String MISSED_NAME = "missed";

    //$NON-NLS-1$
    protected static final String REFUSED_NAME = "refused";

    //$NON-NLS-1$
    protected static final String BUSY_NAME = "busy";

    //$NON-NLS-1$
    protected static final String CANCELLED_NAME = "cancelled";

    //$NON-NLS-1$
    protected static final String UNKNOWN_NAME = "unknown";

    //$NON-NLS-1$
    protected static final String ERROR_NAME = "error";

    private final transient String name;

    protected  CallSessionState(String name) {
        this.name = name;
    }

    public static CallSessionState fromString(String state) {
        if (state == null)
            return null;
        if (state.equals(UNPLACED_NAME))
            return UNPLACED;
        if (state.equals(REDIRECTED_NAME))
            return REDIRECTED;
        else if (state.equals(ROUTING_NAME))
            return ROUTING;
        else if (state.equals(PREPENDING_NAME))
            return PREPENDING;
        else if (state.equals(FAILED_NAME))
            return FAILED;
        else if (state.equals(PENDING_NAME))
            return PENDING;
        else if (state.equals(ACTIVE_NAME))
            return ACTIVE;
        else if (state.equals(ONHOLD_NAME))
            return ONHOLD;
        else if (state.equals(FINISHED_NAME))
            return FINISHED;
        else if (state.equals(MISSED_NAME))
            return MISSED;
        else if (state.equals(REFUSED_NAME))
            return REFUSED;
        else if (state.equals(BUSY_NAME))
            return BUSY;
        else if (state.equals(CANCELLED_NAME))
            return CANCELLED;
        else if (state.equals(ERROR_NAME))
            return ERROR;
        else
            return UNKNOWN;
    }

    /**
	 * For calls where the request has not yet been sent to the target receiver.
	 */
    public static final CallSessionState UNPLACED = new CallSessionState(UNPLACED_NAME);

    /**
	 * For calls that have been redirected to a new target receiver.
	 */
    public static final CallSessionState REDIRECTED = new CallSessionState(REDIRECTED_NAME);

    /**
	 * For calls where the routing is in progress, and the target has not yet
	 * received the request.
	 */
    public static final CallSessionState ROUTING = new CallSessionState(ROUTING_NAME);

    /**
	 * To indicate that the call request has not yet been delivered to the
	 * receiver (which results in a PENDING state), but has completed routing.
	 */
    public static final CallSessionState PREPENDING = new CallSessionState(PREPENDING_NAME);

    /**
	 * For calls where the call has failed, either due to network error, sender
	 * and/or receiver going offline, or some other failure to deliver or answer
	 * a call request.
	 */
    public static final CallSessionState FAILED = new CallSessionState(FAILED_NAME);

    /**
	 * For a call where the request has been received, and the receiver has not
	 * yet responded to the request. Also known as 'ringing'.
	 */
    public static final CallSessionState PENDING = new CallSessionState(PENDING_NAME);

    /**
	 * For calls where the call has been successfully answered and the parties
	 * are speaking.
	 */
    public static final CallSessionState ACTIVE = new CallSessionState(ACTIVE_NAME);

    /**
	 * For calls that have been put on hold by one of the two parties.
	 */
    public static final CallSessionState ONHOLD = new CallSessionState(ONHOLD_NAME);

    /**
	 * To indicate that the call (previously ACTIVE) is now finished. This state
	 * can be reached by either partie(s) ending the call in a normal manner
	 * (e.g. hangup).
	 */
    public static final CallSessionState FINISHED = new CallSessionState(FINISHED_NAME);

    /**
	 * To indicate that a call request has been missed (the receiver did not
	 * answer in time).
	 */
    public static final CallSessionState MISSED = new CallSessionState(MISSED_NAME);

    /**
	 * To indicate that a call request has been explicitly refused by the
	 * receiver.
	 */
    public static final CallSessionState REFUSED = new CallSessionState(REFUSED_NAME);

    /**
	 * For calls where the receiver of the call request is busy and not able to
	 * answer the call.
	 */
    public static final CallSessionState BUSY = new CallSessionState(BUSY_NAME);

    /**
	 * For calls where the initial requester has cancelled the call request.
	 */
    public static final CallSessionState CANCELLED = new CallSessionState(CANCELLED_NAME);

    /**
	 * For calls where the state is not known.
	 */
    public static final CallSessionState UNKNOWN = new CallSessionState(UNKNOWN_NAME);

    /**
	 * For calls where there has been an error, resulting in loss of connection.
	 */
    public static final CallSessionState ERROR = new CallSessionState(ERROR_NAME);

    public String toString() {
        return name;
    }

    // This is to make sure that subclasses don't screw up these methods
    public final boolean equals(Object that) {
        return super.equals(that);
    }

    public final int hashCode() {
        return super.hashCode();
    }

    // For serialization
    private static int nextOrdinal = 0;

    private final int ordinal = nextOrdinal++;

    private static final CallSessionState[] VALUES = { UNPLACED, REDIRECTED, ROUTING, PREPENDING, FAILED, PENDING, ACTIVE, ONHOLD, FINISHED, MISSED, REFUSED, BUSY, CANCELLED, UNKNOWN, ERROR };

    /**
	 * @return Object
	 * @throws ObjectStreamException not thrown by this implementation.
	 */
    Object readResolve() throws ObjectStreamException {
        return VALUES[ordinal];
    }
}
