/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.provider.filetransfer.retrieve;

import java.util.StringTokenizer;

/**
 *
 */
public class HttpHelper {

    //$NON-NLS-1$
    public static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";

    public static String getRemoteFileNameFromContentDispositionHeader(String headerValue) {
        if (headerValue != null) {
            //$NON-NLS-1$
            StringTokenizer tokens = new StringTokenizer(headerValue, " \t\n\r\f=;,");
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                if (//$NON-NLS-1$
                token.equals("filename") && tokens.hasMoreTokens()) {
                    // Expect next token to be the filename
                    String fileName = tokens.nextToken();
                    if (//$NON-NLS-1$ //$NON-NLS-2$
                    fileName.startsWith("\"") && fileName.endsWith("\""))
                        fileName = fileName.substring(1, fileName.length() - 1);
                    return fileName;
                }
            }
        }
        return null;
    }
}
