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

import java.text.DateFormat;
import java.util.Calendar;
import org.eclipse.ecf.discovery.IServiceInfo;

public class PrettyPrinter {

    //	private static final String prompt = "ZooDiscovery> ";//$NON-NLS-1$ 
    //$NON-NLS-1$ 
    private static final String prompt = "";

    public static final int PUBLISHED = 1;

    public static final int UNPUBLISHED = 2;

    public static final int ACTIVATED = 3;

    public static final int DEACTIVATED = 4;

    public static final int REMOTE_AVAILABLE = 5;

    public static final int REMOTE_UNAVAILABLE = 6;

    public static final int PUBLISH_DELAYED = 7;

    public static final int UNPUBLISH_DELAYED = 8;

    public static String prompt(int type, IServiceInfo serviceInfo) {
        //$NON-NLS-1$
        String token = "";
        //		String time = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()) + ". ";//$NON-NLS-1$
        String time = "";
        switch(type) {
            case PUBLISHED:
                token = "Service Published: ";
                break;
            case UNPUBLISHED:
                token = "Service Unpublished: ";
                break;
            case ACTIVATED:
                token = "Discovery Service Activated. ";
                break;
            case DEACTIVATED:
                token = "Discovery Service Deactivated.";
                break;
            case PUBLISH_DELAYED:
                token = "Service Publication Delayed: ";
                break;
            case UNPUBLISH_DELAYED:
                token = "Service Unpublication Delayed: ";
                break;
            case REMOTE_AVAILABLE:
                token = "Service Discovered: ";
                break;
            case REMOTE_UNAVAILABLE:
                token = "Service Undiscovered: ";
                break;
        }
        //$NON-NLS-1$ 
        return (prompt + token + time + ((serviceInfo != null) ? serviceInfo : ""));
    }

    public static String attemptingConnectionTo(String ip) {
        return (prompt + "INFO - Attempting connection to server: /" + ip);
    }

    public static String connectionLost(String ip) {
        return (prompt + "INFO - Connection Lost: /" + ip);
    }
}
