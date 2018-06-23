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
package ch.ethz.iks.slp.impl;

import ch.ethz.iks.slp.impl.filter.Filter;

/**
 * Platform abstraction interface. Used to hide the different implementations
 * for the OSGi platform and for stand-alone Java.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich.
 */
public interface PlatformAbstraction {

    /**
	 * Write a debug message to the log.
	 * 
	 * @param message
	 *            the message.
	 */
    void logDebug(String message);

    /**
	 * Write a debug message to the log.
	 * 
	 * @param message
	 *            the message.
	 * @param exception
	 *            an exception.
	 */
    void logDebug(String message, Throwable exception);

    /**
	 * Trace a generic message to the log.
	 * 
	 * @param message
	 *            the message.
	 */
    void logTraceMessage(String string);

    /**
	 * Trace a registration to the log.
	 * 
	 * @param message
	 *            the message.
	 */
    void logTraceReg(String string);

    /**
	 * Trace a drop to the log.
	 * 
	 * @param message
	 *            the message.
	 */
    void logTraceDrop(String string);

    /**
	 * Write a warning message to the log.
	 * 
	 * @param message
	 *            the message.
	 */
    void logWarning(String message);

    /**
	 * Write a warning message to the log.
	 * 
	 * @param message
	 *            the message.
	 * @param exception
	 *            an exception.
	 */
    void logWarning(String message, Throwable exception);

    /**
	 * Write an error message to the log.
	 * 
	 * @param message
	 *            the message.
	 */
    void logError(String message);

    /**
	 * Write an error message to the log.
	 * 
	 * @param message
	 *            the message.
	 * @param exception
	 *            an exception.
	 */
    void logError(String message, Throwable exception);

    /**
	 * Create an LDAP filter.
	 * 
	 * @param filterString
	 *            the filter string.
	 * @return an LDAP filter object.
	 */
    Filter createFilter(String filterString);
}
