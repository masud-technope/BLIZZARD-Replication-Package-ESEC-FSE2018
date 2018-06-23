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
package org.eclipse.ecf.internal.provider.vbulletin.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.httpclient.Cookie;

public class VBCookies {

    public static final String KEY_SESS_ID = "vbulletin.sessionid";

    public static final String KEY_USER_ID = "vbulletin.userid";

    /**
	 * Matches an MD5.
	 */
    private static final Pattern PAT_SESS_ID = Pattern.compile("[0-9abcdef]{32}");

    /**
	 * Matches a name.
	 */
    private static final Pattern PAT_USER_ID = Pattern.compile("bbuserid");

    public static Map<String, String> detectCookies(Cookie[] cookies) {
        Map<String, String> detected = new HashMap<String, String>();
        for (Cookie cookie : cookies) {
            if (PAT_SESS_ID.matcher(cookie.getValue()).matches()) {
                // detected session id
                detected.put(KEY_SESS_ID, cookie.getValue());
            } else if (PAT_USER_ID.matcher(cookie.getName()).matches()) {
                // detected user id
                detected.put(KEY_USER_ID, cookie.getValue());
            }
        }
        return detected;
    }
}
