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
package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.InputBuffer;
import org.apache.coyote.Request;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SocketWrapper;
import org.apache.tomcat.util.res.StringManager;

public abstract class AbstractInputBuffer<S> implements InputBuffer {

    protected static final boolean[] HTTP_TOKEN_CHAR = new boolean[128];

    /**
     * The string manager for this package.
     */
    protected static final StringManager sm = StringManager.getManager(Constants.Package);

    static {
        for (int i = 0; i < 128; i++) {
            if (i < 32) {
                HTTP_TOKEN_CHAR[i] = false;
            } else if (i == 127) {
                HTTP_TOKEN_CHAR[i] = false;
            } else if (i == '(') {
                HTTP_TOKEN_CHAR[i] = false;
            } else if (i == ')') {
                HTTP_TOKEN_CHAR[i] = false;
            } else if (i == '<') {
                HTTP_TOKEN_CHAR[i] = false;
            } else if (i == '>') {
                HTTP_TOKEN_CHAR[i] = false;
            } else if (i == '@') {
                HTTP_TOKEN_CHAR[i] = false;
            } else if (i == ',') {
                HTTP_TOKEN_CHAR[i] = false;
            } else if (i == ';') {
                HTTP_TOKEN_CHAR[i] = false;
            } else if (i == ':') {
                HTTP_TOKEN_CHAR[i] = false;
            } else if (i == '\\') {
                HTTP_TOKEN_CHAR[i] = false;
            } else if (i == '\"') {
                HTTP_TOKEN_CHAR[i] = false;
            } else if (i == '/') {
                HTTP_TOKEN_CHAR[i] = false;
            } else if (i == '[') {
                HTTP_TOKEN_CHAR[i] = false;
            } else if (i == ']') {
                HTTP_TOKEN_CHAR[i] = false;
            } else if (i == '?') {
                HTTP_TOKEN_CHAR[i] = false;
            } else if (i == '=') {
                HTTP_TOKEN_CHAR[i] = false;
            } else if (i == '{') {
                HTTP_TOKEN_CHAR[i] = false;
            } else if (i == '}') {
                HTTP_TOKEN_CHAR[i] = false;
            } else if (i == ' ') {
                HTTP_TOKEN_CHAR[i] = false;
            } else {
                HTTP_TOKEN_CHAR[i] = true;
            }
        }
    }

    protected Request request;

    protected MimeHeaders headers;

    protected boolean parsingHeader;

    protected boolean swallowInput;

    protected byte[] buf;

    protected int lastValid;

    protected int pos;

    protected int end;

    protected InputBuffer inputStreamInputBuffer;

    protected InputFilter[] filterLibrary;

    protected InputFilter[] activeFilters;

    protected int lastActiveFilter;

    public void addFilter(InputFilter filter) {
        InputFilter[] newFilterLibrary = new InputFilter[filterLibrary.length + 1];
        for (int i = 0; i < filterLibrary.length; i++) {
            newFilterLibrary[i] = filterLibrary[i];
        }
        newFilterLibrary[filterLibrary.length] = filter;
        filterLibrary = newFilterLibrary;
        activeFilters = new InputFilter[filterLibrary.length];
    }

    public InputFilter[] getFilters() {
        return filterLibrary;
    }

    public void addActiveFilter(InputFilter filter) {
        if (lastActiveFilter == -1) {
            filter.setBuffer(inputStreamInputBuffer);
        } else {
            for (int i = 0; i <= lastActiveFilter; i++) {
                if (activeFilters[i] == filter)
                    return;
            }
            filter.setBuffer(activeFilters[lastActiveFilter]);
        }
        activeFilters[++lastActiveFilter] = filter;
        filter.setRequest(request);
    }

    public void setSwallowInput(boolean swallowInput) {
        this.swallowInput = swallowInput;
    }

    public abstract boolean parseRequestLine(boolean useAvailableDataOnly) throws IOException;

    public abstract boolean parseHeaders() throws IOException;

    protected abstract boolean fill(boolean block) throws IOException;

    protected abstract void init(SocketWrapper<S> socketWrapper, AbstractEndpoint<S> endpoint) throws IOException;

    public void recycle() {
        request.recycle();
        for (int i = 0; i <= lastActiveFilter; i++) {
            activeFilters[i].recycle();
        }
        lastValid = 0;
        pos = 0;
        lastActiveFilter = -1;
        parsingHeader = true;
        swallowInput = true;
    }

    public void nextRequest() {
        request.recycle();
        if (lastValid - pos > 0 && pos > 0) {
            System.arraycopy(buf, pos, buf, 0, lastValid - pos);
        }
        lastValid = lastValid - pos;
        pos = 0;
        for (int i = 0; i <= lastActiveFilter; i++) {
            activeFilters[i].recycle();
        }
        lastActiveFilter = -1;
        parsingHeader = true;
        swallowInput = true;
    }

    public void endRequest() throws IOException {
        if (swallowInput && (lastActiveFilter != -1)) {
            int extraBytes = (int) activeFilters[lastActiveFilter].end();
            pos = pos - extraBytes;
        }
    }

    public int available() {
        int result = (lastValid - pos);
        if ((result == 0) && (lastActiveFilter >= 0)) {
            for (int i = 0; (result == 0) && (i <= lastActiveFilter); i++) {
                result = activeFilters[i].available();
            }
        }
        return result;
    }

    @Override
    public int doRead(ByteChunk chunk, Request req) throws IOException {
        if (lastActiveFilter == -1)
            return inputStreamInputBuffer.doRead(chunk, req);
        else
            return activeFilters[lastActiveFilter].doRead(chunk, req);
    }
}
