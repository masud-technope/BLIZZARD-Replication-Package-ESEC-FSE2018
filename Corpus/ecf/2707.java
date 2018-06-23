/****************************************************************************
 * Copyright (c) 2007, 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 *
 */
public class SystemLogService implements LogService {

    //$NON-NLS-1$
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("Z yyyy.MM.dd HH:mm:ss:S");

    private final String pluginName;

    public  SystemLogService(String pluginName) {
        this.pluginName = pluginName;
    }

    private static final String getLogCode(int level) {
        switch(level) {
            case LogService.LOG_INFO:
                //$NON-NLS-1$
                return "INFO";
            case LogService.LOG_ERROR:
                //$NON-NLS-1$
                return "ERROR";
            case LogService.LOG_DEBUG:
                //$NON-NLS-1$
                return "DEBUG";
            case LogService.LOG_WARNING:
                //$NON-NLS-1$
                return "WARNING";
            default:
                //$NON-NLS-1$
                return "UNKNOWN";
        }
    }

    private final void doLog(@SuppressWarnings("rawtypes") ServiceReference sr, int level, String message, Throwable t) {
        //$NON-NLS-1$
        final StringBuffer buf = new StringBuffer("[log;");
        //$NON-NLS-1$
        buf.append(dateFormat.format(new Date())).append(";");
        //$NON-NLS-1$
        buf.append(getLogCode(level)).append(";");
        if (sr != null)
            //$NON-NLS-1$
            buf.append(sr.getBundle().getSymbolicName()).append(";");
        else
            //$NON-NLS-1$
            buf.append(pluginName).append(";");
        //$NON-NLS-1$
        buf.append(message).append("]");
        if (t != null) {
            System.err.println(buf.toString());
            t.printStackTrace(System.err);
        } else
            System.out.println(buf.toString());
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.log.LogService#log(int, java.lang.String)
	 */
    public void log(int level, String message) {
        log(null, level, message, null);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.log.LogService#log(int, java.lang.String,
	 * java.lang.Throwable)
	 */
    public void log(int level, String message, Throwable exception) {
        doLog(null, level, message, exception);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.service.log.LogService#log(org.osgi.framework.ServiceReference,
	 * int, java.lang.String)
	 */
    public void log(@SuppressWarnings("rawtypes") ServiceReference sr, int level, String message) {
        log(sr, level, message, null);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.service.log.LogService#log(org.osgi.framework.ServiceReference,
	 * int, java.lang.String, java.lang.Throwable)
	 */
    public void log(@SuppressWarnings("rawtypes") ServiceReference sr, int level, String message, Throwable exception) {
        doLog(sr, level, message, exception);
    }
}
