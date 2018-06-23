/*******************************************************************************
 * Copyright (c) 2005, 2006 Erkki Lindpere and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Erkki Lindpere - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.bulletinboard.commons.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class IDUtil {

    public static URI composeURI(URL baseURL, String tail) throws URISyntaxException {
        String url = baseURL.toString();
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        if (tail.startsWith("/")) {
            tail = tail.substring(1);
        }
        url = url + tail;
        return new URI(url);
    }
}
