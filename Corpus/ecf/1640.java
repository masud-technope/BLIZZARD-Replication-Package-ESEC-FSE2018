/*******************************************************************************
* Copyright (c) 2009 IBM, and others. 
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   IBM Corporation - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.tests.filetransfer;

import org.eclipse.osgi.util.NLS;

public class Trace {

    public static void trace(long elapsedTimeMillis, String msg) {
        System.out.println(NLS.bind("{0} [{1}]: {2}", new Object[] { toElapsedTime(elapsedTimeMillis), Thread.currentThread().getName(), msg }));
    }

    public static String toElapsedTime(long millis) {
        StringBuffer sb = new StringBuffer(10);
        appendElapsed(sb, millis);
        return sb.toString();
    }

    // Append an elapsed time (represented by milliseconds) to a StringBuffer
    public static void appendElapsed(StringBuffer sb, long millis) {
        long x = millis;
        int ms = (int) (x % 1000);
        x /= 1000;
        int sec = (int) (x % 60);
        appendInt2(sb, sec).append('.');
        appendInt3(sb, ms);
    }

    private static StringBuffer appendInt2(StringBuffer sb, int i) {
        if (i < 10) {
            sb.append('0');
        }
        sb.append(i);
        return sb;
    }

    private static StringBuffer appendInt3(StringBuffer sb, int i) {
        if (i < 10) {
            sb.append("00");
        } else if (i < 100) {
            sb.append('0');
        }
        sb.append(i);
        return sb;
    }
}
