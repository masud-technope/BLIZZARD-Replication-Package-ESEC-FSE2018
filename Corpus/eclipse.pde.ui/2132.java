/*******************************************************************************
 *  Copyright (c) 2000, 2008 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.core.util;

import java.util.regex.Pattern;

public class PatternConstructor {

    private static final Pattern PATTERN_BACK_SLASH = Pattern.compile("\\\\");

    private static final Pattern PATTERN_QUESTION = Pattern.compile("\\?");

    private static final Pattern PATTERN_STAR = Pattern.compile("\\*");

    private static final Pattern PATTERN_LBRACKET = Pattern.compile("\\(");

    private static final Pattern PATTERN_RBRACKET = Pattern.compile("\\)");

    private static String asRegEx(String pattern, boolean group) {
        String result1 = PATTERN_BACK_SLASH.matcher(pattern).replaceAll("\\\\E\\\\\\\\\\\\Q");
        String result2 = PATTERN_STAR.matcher(result1).replaceAll("\\\\E.*\\\\Q");
        String result3 = PATTERN_QUESTION.matcher(result2).replaceAll("\\\\E.\\\\Q");
        if (group) {
            result3 = PATTERN_LBRACKET.matcher(result3).replaceAll("\\\\E(\\\\Q");
            result3 = PATTERN_RBRACKET.matcher(result3).replaceAll("\\\\E)\\\\Q");
        }
        return "\\Q" + result3 + "\\E";
    }

    public static Pattern createPattern(String pattern, boolean isCaseSensitive) {
        if (isCaseSensitive)
            return Pattern.compile(asRegEx(pattern, false));
        return Pattern.compile(asRegEx(pattern, false), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }

    public static Pattern createGroupedPattern(String pattern, boolean isCaseSensitive) {
        if (isCaseSensitive)
            return Pattern.compile(asRegEx(pattern, true));
        return Pattern.compile(asRegEx(pattern, true), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }

    private  PatternConstructor() {
    }
}
