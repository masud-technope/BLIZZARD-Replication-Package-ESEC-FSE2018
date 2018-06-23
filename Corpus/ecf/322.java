/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.example.collab.share.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public class SharedObjectOutputStream extends OutputStream {

    public static final int DEFAULT_BUFF_SIZE = 900;

    public static final boolean DEFAULT_COMPRESSION = true;

    protected StreamSender sender;

    protected ByteArrayOutputStream outputStream;

    protected GZIPOutputStream compressor;

    protected int defaultLength;

    protected int bytesSentCount = 0;

    protected boolean useCompression;

    public  SharedObjectOutputStream(StreamSender sender, int size, boolean compression) throws IOException {
        this.sender = sender;
        this.defaultLength = size;
        this.useCompression = compression;
        resetStreams();
    }

    public  SharedObjectOutputStream(StreamSender obj) throws IOException {
        this(obj, DEFAULT_BUFF_SIZE, DEFAULT_COMPRESSION);
    }

    public  SharedObjectOutputStream(StreamSender obj, int size) throws IOException {
        this(obj, size, DEFAULT_COMPRESSION);
    }

    public  SharedObjectOutputStream(StreamSender obj, boolean compression) throws IOException {
        this(obj, DEFAULT_BUFF_SIZE, compression);
    }

    protected void resetStreams() throws IOException {
        outputStream = new ByteArrayOutputStream(defaultLength);
        if (useCompression)
            compressor = new GZIPOutputStream(outputStream);
    }

    public void close() throws IOException {
        if (useCompression) {
            compressor.close();
        } else
            outputStream.close();
    }

    public void flush() throws IOException {
        sendMsgAndResetStream();
    }

    protected final void sendMsgAndResetStream() throws IOException {
        if (useCompression) {
            compressor.flush();
            compressor.finish();
        } else
            outputStream.flush();
        // Actually ask our StreamSender to send msg with count of size and data
        sender.sendDataMsg(bytesSentCount, outputStream.toByteArray());
        resetStreams();
        bytesSentCount = 0;
    }

    public void write(int a) throws IOException {
        if (bytesSentCount >= defaultLength) {
            sendMsgAndResetStream();
        }
        bytesSentCount++;
        streamWrite(a);
    }

    protected void streamWrite(int a) throws IOException {
        if (useCompression) {
            compressor.write(a);
        } else
            outputStream.write(a);
    }
}
