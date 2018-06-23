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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Util class for de-serializing objects serialized in PHP.
 * 
 * @author Erkki
 */
public class PHPUtil {

    /**
	 * Matches an array.
	 */
    private static final Pattern PHP_STRING_ARRAY = Pattern.compile("a:(?:[0-9]+):\\{(.*)\\}");

    /**
	 * Matches a string.
	 */
    private static final Pattern PHP_STRING = Pattern.compile("s:(?:[0-9]+):\"(.*?)\"");

    /**
	 * Deserializes a PHP array that has string key and value pairs as a HashMap
	 * or null if it was not an array.
	 * 
	 * @param serializedArray
	 *            the character sequence that contains the serialized array
	 * @return the deserialized array as a HashMap or null
	 */
    public static Map<String, String> deserializeStringArray(CharSequence serializedArray) {
        Matcher m = PHP_STRING_ARRAY.matcher(serializedArray);
        if (m.matches()) {
            Map<String, String> map = new HashMap<String, String>();
            Matcher strMatcher = PHP_STRING.matcher(m.group(1));
            String key = null;
            while (strMatcher.find()) {
                if (key == null) {
                    key = strMatcher.group(1);
                } else {
                    map.put(key, strMatcher.group(1));
                    key = null;
                }
            }
            return map;
        } else {
            return null;
        }
    }
}
