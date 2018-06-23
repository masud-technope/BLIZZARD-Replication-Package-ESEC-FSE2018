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
package org.eclipse.ecf.internal.provider.phpbb;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.httpclient.Cookie;

public class PHPBBCookies {

    public static final String KEY_SESS_ID = "phpbb.sessionid";

    public static final String KEY_USER_ID = "phpbb.userid";

    /**
	 * Matches an MD5.
	 */
    private static final Pattern PAT_SESS_ID = Pattern.compile("[0-9abcdef]{32}");

    public static Map<String, String> detectCookies(Cookie[] cookies) {
        Map<String, String> detected = new HashMap<String, String>();
        for (Cookie cookie : cookies) {
            if (PAT_SESS_ID.matcher(cookie.getValue()).matches()) {
                // detected session id
                detected.put(KEY_SESS_ID, cookie.getValue());
            } else {
                // try to detect user id
                try {
                    String value = URLDecoder.decode(cookie.getValue(), "UTF-8");
                    Map<String, String> map = PHPUtil.deserializeStringArray(value);
                    if (map != null && map.containsKey("userid")) {
                        detected.put(KEY_USER_ID, map.get("userid"));
                    }
                } catch (UnsupportedEncodingException e) {
                }
            }
        }
        return detected;
    }
}
