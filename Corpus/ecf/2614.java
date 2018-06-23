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

import java.util.Dictionary;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import ch.ethz.iks.slp.impl.filter.Filter;

/**
 * Platform abstraction for the OSGi implementation.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 */
public class OSGiPlatformAbstraction implements PlatformAbstraction, ServiceListener {

    /**
	 * 
	 */
    private final BundleContext context;

    /**
	 * 
	 */
    private LogService log = new NullPatternLogService();

    /**
	 * Constructor.
	 * 
	 * @param context
	 *            the bundle context from the OSGi framework.
	 * @param log
	 *            the LogService, or null.
	 * @param debug
	 *            true if debugging is enabled.
	 * @throws InvalidSyntaxException 
	 * 				may never happen
	 */
     OSGiPlatformAbstraction(BundleContext context) throws InvalidSyntaxException {
        this.context = context;
        // initially get the LogService
        final ServiceReference sref = context.getServiceReference(LogService.class.getName());
        if (sref != null) {
            this.log = (LogService) context.getService(sref);
        }
        // track the LogService for life cycle events
        context.addServiceListener(this, "(" + Constants.OBJECTCLASS + "=" + LogService.class.getName() + ")");
        logDebug("jSLP OSGi started.");
    }

    /**
	 * 
	 * @see ch.ethz.iks.slp.impl.PlatformAbstraction#createFilter(java.lang.String)
	 */
    public Filter createFilter(String filterString) {
        try {
            final org.osgi.framework.Filter filter = context.createFilter(filterString);
            return new Filter() {

                public boolean match(Dictionary values) {
                    return filter.match(values);
                }

                public String toString() {
                    return filter.toString();
                }
            };
        } catch (InvalidSyntaxException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
	 * 
	 * @see ch.ethz.iks.slp.impl.PlatformAbstraction#logDebug(java.lang.String)
	 */
    public void logDebug(String message) {
        if (SLPCore.CONFIG.getDebugEnabled()) {
            log.log(LogService.LOG_DEBUG, message);
        }
    }

    /**
	 * 
	 * @see ch.ethz.iks.slp.impl.PlatformAbstraction#logDebug(java.lang.String,
	 *      java.lang.Throwable)
	 */
    public void logDebug(String message, Throwable exception) {
        if (SLPCore.CONFIG.getDebugEnabled()) {
            log.log(LogService.LOG_DEBUG, message, exception);
        }
    }

    /**
	 * 
	 * @see ch.ethz.iks.slp.impl.PlatformAbstraction#logError(java.lang.String)
	 */
    public void logError(String message) {
        log.log(LogService.LOG_ERROR, message);
    }

    /**
	 * 
	 * @see ch.ethz.iks.slp.impl.PlatformAbstraction#logError(java.lang.String,
	 *      java.lang.Throwable)
	 */
    public void logError(String message, Throwable exception) {
        log.log(LogService.LOG_ERROR, message, exception);
    }

    /**
	 * 
	 * @see ch.ethz.iks.slp.impl.PlatformAbstraction#logTraceMessage(java.lang.String)
	 */
    public void logTraceMessage(String message) {
        if (SLPCore.CONFIG.getTraceMessage()) {
            log.log(LogService.LOG_INFO, message);
        }
    }

    /**
	 * 
	 * @see ch.ethz.iks.slp.impl.PlatformAbstraction#logTraceDrop(java.lang.String)
	 */
    public void logTraceDrop(String message) {
        if (SLPCore.CONFIG.getTraceDrop()) {
            log.log(LogService.LOG_INFO, message);
        }
    }

    /**
	 * 
	 * @see ch.ethz.iks.slp.impl.PlatformAbstraction#logTraceMessage(java.lang.String)
	 */
    public void logTraceReg(String message) {
        if (SLPCore.CONFIG.getTraceReg()) {
            log.log(LogService.LOG_INFO, message);
        }
    }

    /**
	 * 
	 * @see ch.ethz.iks.slp.impl.PlatformAbstraction#logWarning(java.lang.String)
	 */
    public void logWarning(String message) {
        log.log(LogService.LOG_WARNING, message);
    }

    /**
	 * 
	 * @see ch.ethz.iks.slp.impl.PlatformAbstraction#logWarning(java.lang.String,
	 *      java.lang.Throwable)
	 */
    public void logWarning(String message, Throwable exception) {
        log.log(LogService.LOG_WARNING, message, exception);
    }

    /**
	 * 
	 * @see org.osgi.framework.ServiceListener#serviceChanged(org.osgi.framework.ServiceEvent)
	 */
    public void serviceChanged(final ServiceEvent event) {
        switch(event.getType()) {
            case ServiceEvent.REGISTERED:
                log = (LogService) context.getService(event.getServiceReference());
                return;
            case ServiceEvent.UNREGISTERING:
                log = new NullPatternLogService();
            default:
        }
    }

    // if no LogService is present, we use a dummy log
    private class NullPatternLogService implements LogService {

        public void log(int level, String message) {
            if (level == LogService.LOG_ERROR || level == LogService.LOG_WARNING) {
                System.err.println(message);
            } else {
                System.out.println(message);
            }
        }

        public void log(int level, String message, Throwable exception) {
            log(level, message + " " + exception.toString());
        }

        public void log(ServiceReference sr, int level, String message) {
            log(null, level, message);
        }

        public void log(ServiceReference sr, int level, String message, Throwable t) {
            log(null, level, message, t);
        }
    }
}
