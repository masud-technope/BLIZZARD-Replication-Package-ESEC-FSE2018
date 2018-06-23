/*******************************************************************************
 *  Copyright (c)2010 REMAIN B.V. The Netherlands. (http://www.remainsoftware.com).
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     Wim Jongman - initial API and implementation 
 *     Ahmed Aadel - initial API and implementation     
 *******************************************************************************/
package org.eclipse.ecf.provider.zookeeper.util;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.ecf.core.util.SystemLogService;
import org.eclipse.ecf.provider.zookeeper.DiscoveryActivator;
import org.eclipse.ecf.provider.zookeeper.core.DefaultDiscoveryConfig;
import org.eclipse.ecf.provider.zookeeper.core.ZooDiscoveryContainer;
import org.osgi.service.log.LogService;

public class Logger {

    private static SystemLogService nativeLogger = new SystemLogService("org.eclipse.ecf.provider.zookeeper");

    private static Set<LogService> logServices = new HashSet<LogService>();

    public static void bindLogService(org.osgi.service.log.LogService ls) {
        logServices.add(ls);
    }

    public static void unbindLogService(org.osgi.service.log.LogService ls) {
        logServices.remove(ls);
    }

    public static void log(int level, String message, Exception e) {
        nativeLog(level, message, e);
        for (LogService ls : logServices) {
            if (ls == null) {
                nativeLog(level, message, e);
                continue;
            }
            ls.log(DiscoveryActivator.getContext().getServiceReference(ZooDiscoveryContainer.class.getName()), level, message, e);
        }
    }

    private static void nativeLog(int level, String message, Throwable e) {
        if (DefaultDiscoveryConfig.getConsoleLog()) {
            if (e == null) {
                nativeLogger.log(level, message);
            } else {
                nativeLogger.log(level, message, e);
            }
        }
    }

    public static void log(int level, String message, Throwable t) {
        nativeLog(level, message, t);
        for (LogService ls : logServices) {
            if (ls == null) {
                continue;
            }
            ls.log(DiscoveryActivator.getContext().getServiceReference(ZooDiscoveryContainer.class.getName()), level, message, t);
        }
    }
}
