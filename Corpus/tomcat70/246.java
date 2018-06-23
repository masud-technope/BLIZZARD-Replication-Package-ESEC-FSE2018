/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * HTTP header value parser implementation. Parsing HTTP headers as per RFC2616
 * is not always as simple as it first appears. For headers that only use tokens
 * the simple approach will normally be sufficient. However, for the other
 * headers, while simple code meets 99.9% of cases, there are often some edge
 * cases that make things far more complicated.
 *
 * The purpose of this parser is to let the parser worry about the edge cases.
 * It provides tolerant (where safe to do so) parsing of HTTP header values
 * assuming that wrapped header lines have already been unwrapped. (The Tomcat
 * header processing code does the unwrapping.)
 *
 * Provides parsing of the following HTTP header values as per RFC 2616:
 * - Authorization for DIGEST authentication
 * - MediaType (used for Content-Type header)
 *
 * Support for additional headers will be provided as required.
 */
public class HttpParser {

    // Unused due to buggy client implementations
    @SuppressWarnings("unused")
    private static final Integer FIELD_TYPE_TOKEN = Integer.valueOf(0);

    private static final Integer FIELD_TYPE_QUOTED_STRING = Integer.valueOf(1);

    private static final Integer FIELD_TYPE_TOKEN_OR_QUOTED_STRING = Integer.valueOf(2);

    private static final Integer FIELD_TYPE_LHEX = Integer.valueOf(3);

    private static final Integer FIELD_TYPE_QUOTED_TOKEN = Integer.valueOf(4);

    private static final Map<String, Integer> fieldTypes = new HashMap<String, Integer>();

    // Arrays used by isToken(), isHex()
    private static final boolean isToken[] = new boolean[128];

    private static final boolean isHex[] = new boolean[128];

    static {
        // Digest field types.
        // Note: These are more relaxed than RFC2617. This adheres to the
        //       recommendation of RFC2616 that servers are tolerant of buggy
        //       clients when they can be so without ambiguity.
        fieldTypes.put("username", FIELD_TYPE_QUOTED_STRING);
        fieldTypes.put("realm", FIELD_TYPE_QUOTED_STRING);
        fieldTypes.put("nonce", FIELD_TYPE_QUOTED_STRING);
        fieldTypes.put("digest-uri", FIELD_TYPE_QUOTED_STRING);
        // RFC2617 says response is <">32LHEX<">. 32LHEX will also be accepted
        fieldTypes.put("response", FIELD_TYPE_LHEX);
        // RFC2617 says algorithm is token. <">token<"> will also be accepted
        fieldTypes.put("algorithm", FIELD_TYPE_QUOTED_TOKEN);
        fieldTypes.put("cnonce", FIELD_TYPE_QUOTED_STRING);
        fieldTypes.put("opaque", FIELD_TYPE_QUOTED_STRING);
        // RFC2617 says qop is token. <">token<"> will also be accepted
        fieldTypes.put("qop", FIELD_TYPE_QUOTED_TOKEN);
        // RFC2617 says nc is 8LHEX. <">8LHEX<"> will also be accepted
        fieldTypes.put("nc", FIELD_TYPE_LHEX);
        // Setup the flag arrays
        for (int i = 0; i < 128; i++) {
            if (i <= 32) {
                // includes '\t' and ' '
                isToken[i] = false;
            } else if (i == '(' || i == ')' || i == '<' || i == '>' || i == '@' || i == ',' || i == ';' || i == ':' || i == '\\' || i == '\"' || i == '/' || i == '[' || i == ']' || i == '?' || i == '=' || i == '{' || i == '}') {
                isToken[i] = false;
            } else {
                isToken[i] = true;
            }
            if (i >= '0' && i <= '9' || i >= 'A' && i <= 'F' || i >= 'a' && i <= 'f') {
                isHex[i] = true;
            } else {
                isHex[i] = false;
            }
        }
    }

    public static Map<String, String> parseAuthorizationDigest(StringReader input) throws IllegalArgumentException, IOException {
        Map<String, String> result = new HashMap<String, String>();
        if (skipConstant(input, "Digest") != SkipConstantResult.FOUND) {
            return null;
        }
        String field = readToken(input);
        if (field == null) {
            return null;
        }
        while (!field.equals("")) {
            if (skipConstant(input, "=") != SkipConstantResult.FOUND) {
                return null;
            }
            String value = null;
            Integer type = fieldTypes.get(field.toLowerCase(Locale.ENGLISH));
            if (type == null) {
                type = FIELD_TYPE_TOKEN_OR_QUOTED_STRING;
            }
            switch(type.intValue()) {
                case 0:
                    value = readToken(input);
                    break;
                case 1:
                    value = readQuotedString(input, false);
                    break;
                case 2:
                    value = readTokenOrQuotedString(input, false);
                    break;
                case 3:
                    value = readLhex(input);
                    break;
                case 4:
                    value = readQuotedToken(input);
                    break;
                default:
                    throw new IllegalArgumentException("TODO i18n: Unsupported type");
            }
            if (value == null) {
                return null;
            }
            result.put(field, value);
            if (skipConstant(input, ",") == SkipConstantResult.NOT_FOUND) {
                return null;
            }
            field = readToken(input);
            if (field == null) {
                return null;
            }
        }
        return result;
    }

    public static MediaType parseMediaType(StringReader input) throws IOException {
        String type = readToken(input);
        if (type == null || type.length() == 0) {
            return null;
        }
        if (skipConstant(input, "/") == SkipConstantResult.NOT_FOUND) {
            return null;
        }
        String subtype = readToken(input);
        if (subtype == null || subtype.length() == 0) {
            return null;
        }
        LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
        SkipConstantResult lookForSemiColon = skipConstant(input, ";");
        if (lookForSemiColon == SkipConstantResult.NOT_FOUND) {
            return null;
        }
        while (lookForSemiColon == SkipConstantResult.FOUND) {
            String attribute = readToken(input);
            String value = "";
            if (skipConstant(input, "=") == SkipConstantResult.FOUND) {
                value = readTokenOrQuotedString(input, true);
            }
            if (attribute != null) {
                parameters.put(attribute.toLowerCase(Locale.ENGLISH), value);
            }
            lookForSemiColon = skipConstant(input, ";");
            if (lookForSemiColon == SkipConstantResult.NOT_FOUND) {
                return null;
            }
        }
        return new MediaType(type, subtype, parameters);
    }

    public static String unquote(String input) {
        if (input == null || input.length() < 2) {
            return input;
        }
        int start;
        int end;
        if (input.charAt(0) == '"') {
            start = 1;
            end = input.length() - 1;
        } else {
            start = 0;
            end = input.length();
        }
        StringBuilder result = new StringBuilder();
        for (int i = start; i < end; i++) {
            char c = input.charAt(i);
            if (input.charAt(i) == '\\') {
                i++;
                result.append(input.charAt(i));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private static boolean isToken(int c) {
        try {
            return isToken[c];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }

    private static boolean isHex(int c) {
        try {
            return isHex[c];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }

    private static int skipLws(StringReader input, boolean withReset) throws IOException {
        if (withReset) {
            input.mark(1);
        }
        int c = input.read();
        while (c == 32 || c == 9 || c == 10 || c == 13) {
            if (withReset) {
                input.mark(1);
            }
            c = input.read();
        }
        if (withReset) {
            input.reset();
        }
        return c;
    }

    private static SkipConstantResult skipConstant(StringReader input, String constant) throws IOException {
        int len = constant.length();
        int c = skipLws(input, false);
        for (int i = 0; i < len; i++) {
            if (i == 0 && c == -1) {
                return SkipConstantResult.EOF;
            }
            if (c != constant.charAt(i)) {
                input.skip(-(i + 1));
                return SkipConstantResult.NOT_FOUND;
            }
            if (i != (len - 1)) {
                c = input.read();
            }
        }
        return SkipConstantResult.FOUND;
    }

    private static String readToken(StringReader input) throws IOException {
        StringBuilder result = new StringBuilder();
        int c = skipLws(input, false);
        while (c != -1 && isToken(c)) {
            result.append((char) c);
            c = input.read();
        }
        input.skip(-1);
        if (c != -1 && result.length() == 0) {
            return null;
        } else {
            return result.toString();
        }
    }

    private static String readQuotedString(StringReader input, boolean returnQuoted) throws IOException {
        int c = skipLws(input, false);
        if (c != '"') {
            return null;
        }
        StringBuilder result = new StringBuilder();
        if (returnQuoted) {
            result.append('\"');
        }
        c = input.read();
        while (c != '"') {
            if (c == -1) {
                return null;
            } else if (c == '\\') {
                c = input.read();
                if (returnQuoted) {
                    result.append('\\');
                }
                result.append(c);
            } else {
                result.append((char) c);
            }
            c = input.read();
        }
        if (returnQuoted) {
            result.append('\"');
        }
        return result.toString();
    }

    private static String readTokenOrQuotedString(StringReader input, boolean returnQuoted) throws IOException {
        int c = skipLws(input, true);
        if (c == '"') {
            return readQuotedString(input, returnQuoted);
        } else {
            return readToken(input);
        }
    }

    private static String readQuotedToken(StringReader input) throws IOException {
        StringBuilder result = new StringBuilder();
        boolean quoted = false;
        int c = skipLws(input, false);
        if (c == '"') {
            quoted = true;
        } else if (c == -1 || !isToken(c)) {
            return null;
        } else {
            result.append((char) c);
        }
        c = input.read();
        while (c != -1 && isToken(c)) {
            result.append((char) c);
            c = input.read();
        }
        if (quoted) {
            if (c != '"') {
                return null;
            }
        } else {
            input.skip(-1);
        }
        if (c != -1 && result.length() == 0) {
            return null;
        } else {
            return result.toString();
        }
    }

    private static String readLhex(StringReader input) throws IOException {
        StringBuilder result = new StringBuilder();
        boolean quoted = false;
        int c = skipLws(input, false);
        if (c == '"') {
            quoted = true;
        } else if (c == -1 || !isHex(c)) {
            return null;
        } else {
            if ('A' <= c && c <= 'F') {
                c -= ('A' - 'a');
            }
            result.append((char) c);
        }
        c = input.read();
        while (c != -1 && isHex(c)) {
            if ('A' <= c && c <= 'F') {
                c -= ('A' - 'a');
            }
            result.append((char) c);
            c = input.read();
        }
        if (quoted) {
            if (c != '"') {
                return null;
            }
        } else {
            input.skip(-1);
        }
        if (c != -1 && result.length() == 0) {
            return null;
        } else {
            return result.toString();
        }
    }

    private static enum SkipConstantResult implements  {

        FOUND() {
        }
        , NOT_FOUND() {
        }
        , EOF() {
        }
        ;
    }
}
