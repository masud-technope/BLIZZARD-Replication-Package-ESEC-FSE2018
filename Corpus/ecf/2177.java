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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ch.ethz.iks.slp.impl.filter.Filter;
import ch.ethz.iks.slp.impl.filter.RFC1960Filter;

/**
 * Platform abstraction for the standalone implementation.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 */
public class StandalonePlatformAbstraction implements PlatformAbstraction {

    /**
	 * 
	 */
    private final Log log;

    /**
	 * 
	 */
    public  StandalonePlatformAbstraction() {
        log = LogFactory.getLog(SLPCore.class);
    }

    /**
	 * 
	 * @see ch.ethz.iks.slp.impl.PlatformAbstraction#createFilter(java.lang.String)
	 */
    public Filter createFilter(String filterString) throws IllegalArgumentException {
        return RFC1960Filter.fromString(filterString);
    }

    /**
	 * 
	 * @see ch.ethz.iks.slp.impl.PlatformAbstraction#logDebug(java.lang.String)
	 */
    public void logDebug(final String message) {
        log.debug(message);
    }

    /**
	 * 
	 * @see ch.ethz.iks.slp.impl.PlatformAbstraction#logDebug(java.lang.String,
	 *      java.lang.Throwable)
	 */
    public void logDebug(final String message, final Throwable exception) {
        log.debug(message, exception);
    }

    /**
	 * 
	 * @see ch.ethz.iks.slp.impl.PlatformAbstraction#logError(java.lang.String)
	 */
    public void logError(final String message) {
        log.error(message);
    }

    /**
	 * 
	 * @see ch.ethz.iks.slp.impl.PlatformAbstraction#logError(java.lang.String,
	 *      java.lang.Throwable)
	 */
    public void logError(final String message, final Throwable exception) {
        log.error(message, exception);
    }

    /* (non-Javadoc)
	 * @see ch.ethz.iks.slp.impl.PlatformAbstraction#logTraceDrop(java.lang.String)
	 */
    public void logTraceDrop(String string) {
        if (SLPCore.CONFIG.getTraceDrop()) {
            log.trace(string);
        }
    }

    /* (non-Javadoc)
	 * @see ch.ethz.iks.slp.impl.PlatformAbstraction#logTraceMessage(java.lang.String)
	 */
    public void logTraceMessage(String string) {
        if (SLPCore.CONFIG.getTraceMessage()) {
            log.trace(string);
        }
    }

    /* (non-Javadoc)
	 * @see ch.ethz.iks.slp.impl.PlatformAbstraction#logTraceReg(java.lang.String)
	 */
    public void logTraceReg(String string) {
        if (SLPCore.CONFIG.getTraceReg()) {
            log.trace(string);
        }
    }

    /**
	 * 
	 * @see ch.ethz.iks.slp.impl.PlatformAbstraction#logWarning(java.lang.String)
	 */
    public void logWarning(final String message) {
        log.warn(message);
    }

    /**
	 * 
	 * @see ch.ethz.iks.slp.impl.PlatformAbstraction#logWarning(java.lang.String,
	 *      java.lang.Throwable)
	 */
    public void logWarning(final String message, final Throwable exception) {
        log.warn(message, exception);
    }
}
