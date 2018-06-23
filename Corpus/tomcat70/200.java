/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tomcat.util.http;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.log.UserDataHelper;
import org.apache.tomcat.util.res.StringManager;

/**
 * A collection of cookies - reusable and tuned for server side performance.
 * Based on RFC2965 ( and 2109 )
 *
 * This class is not synchronized.
 *
 * @author Costin Manolache
 * @author kevin seguin
 */
public final class Cookies {

    private static final Log log = LogFactory.getLog(Cookies.class);

    private static final UserDataHelper userDataLog = new UserDataHelper(log);

    static final StringManager sm = StringManager.getManager("org.apache.tomcat.util.http");

    // expected average number of cookies per request
    public static final int INITIAL_SIZE = 4;

    ServerCookie scookies[] = new ServerCookie[INITIAL_SIZE];

    int cookieCount = 0;

    private int limit = 200;

    boolean unprocessed = true;

    MimeHeaders headers;

    /**
     *  Construct a new cookie collection, that will extract
     *  the information from headers.
     *
     * @param headers Cookies are lazy-evaluated and will extract the
     *     information from the provided headers.
     */
    public  Cookies(MimeHeaders headers) {
        this.headers = headers;
    }

    public void setLimit(int limit) {
        this.limit = limit;
        if (limit > -1 && scookies.length > limit && cookieCount <= limit) {
            // shrink cookie list array
            ServerCookie scookiesTmp[] = new ServerCookie[limit];
            System.arraycopy(scookies, 0, scookiesTmp, 0, cookieCount);
            scookies = scookiesTmp;
        }
    }

    /**
     * Recycle.
     */
    public void recycle() {
        for (int i = 0; i < cookieCount; i++) {
            if (scookies[i] != null) {
                scookies[i].recycle();
            }
        }
        cookieCount = 0;
        unprocessed = true;
    }

    /**
     * EXPENSIVE!!!  only for debugging.
     */
    @Override
    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("=== Cookies ===");
        int count = getCookieCount();
        for (int i = 0; i < count; ++i) {
            pw.println(getCookie(i).toString());
        }
        return sw.toString();
    }

    // -------------------- Indexed access --------------------
    public ServerCookie getCookie(int idx) {
        if (unprocessed) {
            // will also update the cookies
            getCookieCount();
        }
        return scookies[idx];
    }

    public int getCookieCount() {
        if (unprocessed) {
            unprocessed = false;
            processCookies(headers);
        }
        return cookieCount;
    }

    // -------------------- Adding cookies --------------------
    /** Register a new, initialized cookie. Cookies are recycled, and
     *  most of the time an existing ServerCookie object is returned.
     *  The caller can set the name/value and attributes for the cookie
     */
    private ServerCookie addCookie() {
        if (limit > -1 && cookieCount >= limit) {
            throw new IllegalArgumentException(sm.getString("cookies.maxCountFail", Integer.valueOf(limit)));
        }
        if (cookieCount >= scookies.length) {
            int newSize = Math.min(2 * cookieCount, limit);
            ServerCookie scookiesTmp[] = new ServerCookie[newSize];
            System.arraycopy(scookies, 0, scookiesTmp, 0, cookieCount);
            scookies = scookiesTmp;
        }
        ServerCookie c = scookies[cookieCount];
        if (c == null) {
            c = new ServerCookie();
            scookies[cookieCount] = c;
        }
        cookieCount++;
        return c;
    }

    // code from CookieTools
    /** Add all Cookie found in the headers of a request.
     */
    public void processCookies(MimeHeaders headers) {
        if (headers == null) {
            // nothing to process
            return;
        }
        // process each "cookie" header
        int pos = 0;
        while (pos >= 0) {
            // Cookie2: version ? not needed
            pos = headers.findHeader("Cookie", pos);
            // no more cookie headers headers
            if (pos < 0) {
                break;
            }
            MessageBytes cookieValue = headers.getValue(pos);
            if (cookieValue == null || cookieValue.isNull()) {
                pos++;
                continue;
            }
            if (cookieValue.getType() != MessageBytes.T_BYTES) {
                Exception e = new Exception();
                log.warn("Cookies: Parsing cookie as String. Expected bytes.", e);
                cookieValue.toBytes();
            }
            if (log.isDebugEnabled()) {
                log.debug("Cookies: Parsing b[]: " + cookieValue.toString());
            }
            ByteChunk bc = cookieValue.getByteChunk();
            if (CookieSupport.PRESERVE_COOKIE_HEADER) {
                int len = bc.getLength();
                if (len > 0) {
                    byte[] buf = new byte[len];
                    System.arraycopy(bc.getBytes(), bc.getOffset(), buf, 0, len);
                    processCookieHeader(buf, 0, len);
                }
            } else {
                processCookieHeader(bc.getBytes(), bc.getOffset(), bc.getLength());
            }
            // search from the next position
            pos++;
        }
    }

    // XXX will be refactored soon!
    private static boolean equals(String s, byte b[], int start, int end) {
        int blen = end - start;
        if (b == null || blen != s.length()) {
            return false;
        }
        int boff = start;
        for (int i = 0; i < blen; i++) {
            if (b[boff++] != s.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the byte is a whitespace character as
     * defined in RFC2619
     * JVK
     */
    private static final boolean isWhiteSpace(final byte c) {
        /*
        switch (c) {
        case ' ':;
        case '\t':;
        case '\n':;
        case '\r':;
        case '\f':;
            return true;
        default:;
            return false;
        }
        */
        if (c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\f') {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Unescapes any double quotes in the given cookie value.
     *
     * @param bc The cookie value to modify
     */
    private static void unescapeDoubleQuotes(ByteChunk bc) {
        if (bc == null || bc.getLength() == 0 || bc.indexOf('"', 0) == -1) {
            return;
        }
        int src = bc.getStart();
        int end = bc.getEnd();
        int dest = src;
        byte[] buffer = bc.getBuffer();
        while (src < end) {
            if (buffer[src] == '\\' && src < end && buffer[src + 1] == '"') {
                src++;
            }
            buffer[dest] = buffer[src];
            dest++;
            src++;
        }
        bc.setEnd(dest);
    }

    protected final void processCookieHeader(byte bytes[], int off, int len) {
        if (len <= 0 || bytes == null) {
            return;
        }
        int end = off + len;
        int pos = off;
        int nameStart = 0;
        int nameEnd = 0;
        int valueStart = 0;
        int valueEnd = 0;
        int version = 0;
        ServerCookie sc = null;
        boolean isSpecial;
        boolean isQuoted;
        while (pos < end) {
            isSpecial = false;
            isQuoted = false;
            while (pos < end && (CookieSupport.isHttpSeparator((char) bytes[pos]) && !CookieSupport.ALLOW_HTTP_SEPARATORS_IN_V0 || CookieSupport.isV0Separator((char) bytes[pos]) || isWhiteSpace(bytes[pos]))) {
                pos++;
            }
            if (pos >= end) {
                return;
            }
            if (bytes[pos] == '$') {
                isSpecial = true;
                pos++;
            }
            valueEnd = valueStart = nameStart = pos;
            pos = nameEnd = getTokenEndPosition(bytes, pos, end, version, true);
            while (pos < end && isWhiteSpace(bytes[pos])) {
                pos++;
            }
            if (pos < (end - 1) && bytes[pos] == '=') {
                do {
                    pos++;
                } while (pos < end && isWhiteSpace(bytes[pos]));
                if (pos >= end) {
                    return;
                }
                switch(bytes[pos]) {
                    case '"':
                        isQuoted = true;
                        valueStart = pos + 1;
                        valueEnd = getQuotedValueEndPosition(bytes, valueStart, end);
                        pos = valueEnd;
                        if (pos >= end) {
                            return;
                        }
                        break;
                    case ';':
                    case ',':
                        valueStart = valueEnd = -1;
                        break;
                    default:
                        if (version == 0 && !CookieSupport.isV0Separator((char) bytes[pos]) && CookieSupport.ALLOW_HTTP_SEPARATORS_IN_V0 || !CookieSupport.isHttpSeparator((char) bytes[pos]) || bytes[pos] == '=' && CookieSupport.ALLOW_EQUALS_IN_VALUE) {
                            valueStart = pos;
                            valueEnd = getTokenEndPosition(bytes, valueStart, end, version, false);
                            pos = valueEnd;
                        } else {
                            UserDataHelper.Mode logMode = userDataLog.getNextMode();
                            if (logMode != null) {
                                String message = sm.getString("cookies.invalidCookieToken");
                                switch(logMode) {
                                    case INFO_THEN_DEBUG:
                                        message += sm.getString("cookies.fallToDebug");
                                    case INFO:
                                        log.info(message);
                                        break;
                                    case DEBUG:
                                        log.debug(message);
                                }
                            }
                            while (pos < end && bytes[pos] != ';' && bytes[pos] != ',') {
                                pos++;
                            }
                            pos++;
                            sc = null;
                            continue;
                        }
                }
            } else {
                valueStart = valueEnd = -1;
                pos = nameEnd;
            }
            while (pos < end && isWhiteSpace(bytes[pos])) {
                pos++;
            }
            while (pos < end && bytes[pos] != ';' && bytes[pos] != ',') {
                pos++;
            }
            pos++;
            if (isSpecial) {
                isSpecial = false;
                if (equals("Version", bytes, nameStart, nameEnd) && sc == null) {
                    if (bytes[valueStart] == '1' && valueEnd == (valueStart + 1)) {
                        version = 1;
                    } else {
                    }
                    continue;
                }
                if (sc == null) {
                    continue;
                }
                if (equals("Domain", bytes, nameStart, nameEnd)) {
                    sc.getDomain().setBytes(bytes, valueStart, valueEnd - valueStart);
                    continue;
                }
                if (equals("Path", bytes, nameStart, nameEnd)) {
                    sc.getPath().setBytes(bytes, valueStart, valueEnd - valueStart);
                    continue;
                }
                if (equals("Port", bytes, nameStart, nameEnd)) {
                    continue;
                }
                if (equals("CommentURL", bytes, nameStart, nameEnd)) {
                    continue;
                }
                UserDataHelper.Mode logMode = userDataLog.getNextMode();
                if (logMode != null) {
                    String message = sm.getString("cookies.invalidSpecial");
                    switch(logMode) {
                        case INFO_THEN_DEBUG:
                            message += sm.getString("cookies.fallToDebug");
                        case INFO:
                            log.info(message);
                            break;
                        case DEBUG:
                            log.debug(message);
                    }
                }
            } else {
                if (valueStart == -1 && !CookieSupport.ALLOW_NAME_ONLY) {
                    continue;
                }
                sc = addCookie();
                sc.setVersion(version);
                sc.getName().setBytes(bytes, nameStart, nameEnd - nameStart);
                if (valueStart != -1) {
                    sc.getValue().setBytes(bytes, valueStart, valueEnd - valueStart);
                    if (isQuoted) {
                        unescapeDoubleQuotes(sc.getValue().getByteChunk());
                    }
                } else {
                    sc.getValue().setString("");
                }
                continue;
            }
        }
    }

    private static final int getTokenEndPosition(byte bytes[], int off, int end, int version, boolean isName) {
        int pos = off;
        while (pos < end && (!CookieSupport.isHttpSeparator((char) bytes[pos]) || version == 0 && CookieSupport.ALLOW_HTTP_SEPARATORS_IN_V0 && bytes[pos] != '=' && !CookieSupport.isV0Separator((char) bytes[pos]) || !isName && bytes[pos] == '=' && CookieSupport.ALLOW_EQUALS_IN_VALUE)) {
            pos++;
        }
        if (pos > end) {
            return end;
        }
        return pos;
    }

    private static final int getQuotedValueEndPosition(byte bytes[], int off, int end) {
        int pos = off;
        while (pos < end) {
            if (bytes[pos] == '"') {
                return pos;
            } else if (bytes[pos] == '\\' && pos < (end - 1)) {
                pos += 2;
            } else {
                pos++;
            }
        }
        return end;
    }
}
