/****************************************************************************
 * Copyright (c) 2005, 2010 Jan S. Rellermeyer, Systems Group,
 * Department of Computer Science, ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jan S. Rellermeyer - initial API and implementation
 *    Markus Alexander Kuppe - enhancements and bug fixes
 *
*****************************************************************************/
package ch.ethz.iks.slp;

import java.io.Serializable;

/**
 * This exception is thrown whenever a part of the SLP framework causes an
 * exception. The error code is a hint why the exception occured.
 *
 * @author Jan S. Rellermeyer, Systems Group, ETH Zurich
 * @since 0.1
 */
public class ServiceLocationException extends Exception implements Serializable {

    /**
     * @serial for serialization.
     */
    private static final long serialVersionUID = 5718658752610460537L;

    /**
     * There is data for the service type in the scope in the AttrRqst or
     * SrvRqst, but not in the requested language.
     */
    public static final short LANGUAGE_NOT_SUPPORTED = 1;

    /**
     * The message fails to obey SLP syntax.
     */
    public static final short PARSE_ERROR = 2;

    /**
     * The SrvReg has problems -- e.g., a zero lifetime or an omitted Language
     * Tag.
     */
    public static final short INVALID_REGISTRATION = 3;

    /**
     * The SLP message did not include a scope in its scope-list supported by
     * the SA or DA.
     */
    public static final short SCOPE_NOT_SUPPORTED = 4;

    /**
     * The DA or SA receives a request for an unsupported SLP SPI.
     */
    public static final short AUTHENTICATION_UNKNOWN = 5;

    /**
     * The DA expected URL and ATTR authentication in the SrvReg and did not
     * receive it.
     */
    public static final short AUTHENTICATION_ABSENT = 6;

    /**
     * Unsupported version number in message header. INTERNAL_ERROR = 10: The DA
     * (or SA) is too sick to respond.
     */
    public static final short AUTHENTICATION_FAILED = 7;

    /**
     * The DA received a SrvReg without FRESH set, for an unregistered service
     * or with inconsistent Service Types.
     */
    public static final short INVALID_UPDATE = 13;

    /**
     * The DA rejected the update because it was within the minimal update
     * intervall.
     */
    public static final short REFRESH_REJECTED = 15;

    /**
     * The feature or extension is not implemented.
     */
    public static final short NOT_IMPLEMENTED = 16;

    /**
     * The initialization of the framework failed.
     */
    public static final short NETWORK_INIT_FAILED = 17;

    /**
     * The network timed out while the framework tried to send a message.
     */
    public static final short NETWORK_TIMED_OUT = 18;

    /**
     * The network encountered an error.
     */
    public static final short NETWORK_ERROR = 19;

    /**
     * The framework encountered an internal system error.
     */
    public static final short INTERNAL_SYSTEM_ERROR = 20;

    /**
     * The type was not well formed.
     */
    public static final short TYPE_ERROR = 21;

    /**
     * The framework encountered a buffer overflow.
     */
    public static final short BUFFER_OVERFLOW = 22;

    /**
     * the error code of this exception instance.
     */
    private short errorCode;

    /**
     * Create a new ServiceLocation instance.
     *
     * @param errcode
     *            the error code, one of the statically defined.
     * @param message
     *            the message of the exception.
     */
    public  ServiceLocationException(final short errcode, final String message) {
        super(message);
        errorCode = errcode;
    }

    /**
     * Get the error code of the exception.
     *
     * @return the error code.
     */
    public final short getErrorCode() {
        return errorCode;
    }
}
