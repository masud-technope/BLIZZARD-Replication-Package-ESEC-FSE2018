package org.eclipse.ecf.telephony.call;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * A type-safe enum class to represent error details for an ICallSession.  See {@link ICallSession#getErrorDetails()}.
 * 
 */
public class CallSessionErrorDetails implements Serializable {

    private static final long serialVersionUID = 1119153779628660673L;

    //$NON-NLS-1$
    private static final String SERVICE_UNAVAILABLE_NAME = "service-unavailable";

    //$NON-NLS-1$
    private static final String UNSUPPORTED_MEDIA_NAME = "unsupported-media";

    //$NON-NLS-1$
    private static final String UNSUPPORTED_TRANSPORTS_NAME = "unsupported-transports";

    //$NON-NLS-1$
    private static final String BAD_REQUEST_NAME = "bad-request";

    //$NON-NLS-1$
    private static final String UNKWOWN_ERROR_NAME = "unknown-error";

    private final transient String name;

    // instances
    protected  CallSessionErrorDetails(String name) {
        this.name = name;
    }

    public static CallSessionErrorDetails fromString(String presenceType) {
        if (presenceType == null)
            return null;
        if (presenceType.equals(SERVICE_UNAVAILABLE_NAME)) {
            return SERVICE_UNAVAILABLE;
        } else if (presenceType.equals(UNSUPPORTED_MEDIA_NAME)) {
            return UNSUPPORTED_MEDIA;
        } else if (presenceType.equals(UNSUPPORTED_TRANSPORTS_NAME)) {
            return UNSUPPORTED_TRANSPORTS;
        } else if (presenceType.equals(BAD_REQUEST_NAME)) {
            return BAD_REQUEST;
        } else if (presenceType.equals(UNKWOWN_ERROR_NAME)) {
            return UNKWOWN_ERROR;
        } else
            return null;
    }

    public static final CallSessionErrorDetails SERVICE_UNAVAILABLE = new CallSessionErrorDetails(SERVICE_UNAVAILABLE_NAME);

    public static final CallSessionErrorDetails UNSUPPORTED_MEDIA = new CallSessionErrorDetails(UNSUPPORTED_MEDIA_NAME);

    public static final CallSessionErrorDetails UNSUPPORTED_TRANSPORTS = new CallSessionErrorDetails(UNSUPPORTED_TRANSPORTS_NAME);

    public static final CallSessionErrorDetails BAD_REQUEST = new CallSessionErrorDetails(BAD_REQUEST_NAME);

    public static final CallSessionErrorDetails UNKWOWN_ERROR = new CallSessionErrorDetails(UNKWOWN_ERROR_NAME);

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

    private static final CallSessionErrorDetails[] VALUES = { SERVICE_UNAVAILABLE, UNSUPPORTED_MEDIA, UNSUPPORTED_TRANSPORTS, BAD_REQUEST, UNKWOWN_ERROR };

    /**
	 * @return Object
	 * @throws ObjectStreamException not thrown by this implementation.
	 */
    Object readResolve() throws ObjectStreamException {
        return VALUES[ordinal];
    }
}
