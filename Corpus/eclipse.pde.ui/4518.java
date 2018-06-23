/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jacek Pospychala <jacek.pospychala@pl.ibm.com> - bugs 202583, 207344
 *     Benjamin Cabe <benjamin.cabe@anyware-tech.com> - bug 218648
 *******************************************************************************/
package org.eclipse.ui.internal.views.log;

import com.ibm.icu.text.SimpleDateFormat;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;

/**
 * Group of entries with additional Session data.
 */
public class LogSession extends Group {

    /**
	 * Describes the !SESSION header name
	 *
	 * @since 3.5
	 */
    //$NON-NLS-1$
    public static final String SESSION = "!SESSION";

    private String sessionData;

    private Date date;

    public  LogSession() {
        super(Messages.LogViewLabelProvider_Session);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(String dateString) {
        //$NON-NLS-1$
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            date = formatter.parse(dateString);
        } catch (ParseException // do nothing
        e) {
        }
    }

    public String getSessionData() {
        return sessionData;
    }

    void setSessionData(String data) {
        this.sessionData = data;
    }

    public void processLogLine(String line) {
        // process "!SESSION <dateUnknownFormat> ----------------------------"
        if (line.startsWith(SESSION)) {
            // strip "!SESSION "
            line = line.substring(SESSION.length()).trim();
            //$NON-NLS-1$ // single "-" may be in date, so take few for sure
            int delim = line.indexOf("----");
            if (delim == -1) {
                return;
            }
            String dateBuffer = line.substring(0, delim).trim();
            setDate(dateBuffer);
        }
    }

    @Override
    public void write(PrintWriter writer) {
        writer.write(sessionData);
        writer.println();
        super.write(writer);
    }
}
