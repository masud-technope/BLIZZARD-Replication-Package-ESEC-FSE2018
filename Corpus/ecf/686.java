/*******************************************************************************
* Copyright (c) 2013 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice.util;

import java.io.*;

/**
 * @since 8.1
 */
public class StreamUtil {

    private static final int DEFAULT_BUFFER_SIZE = 4096;

    private int bufferSize;

    public  StreamUtil(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public  StreamUtil() {
        this(DEFAULT_BUFFER_SIZE);
    }

    public long copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[bufferSize];
        long count = 0;
        int n = 0;
        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream(bufferSize);
        copy(input, output);
        return output.toByteArray();
    }

    public void writeByteArray(OutputStream outs, byte[] bytes) throws IOException {
        copy(new ByteArrayInputStream(bytes), outs);
    }
}
