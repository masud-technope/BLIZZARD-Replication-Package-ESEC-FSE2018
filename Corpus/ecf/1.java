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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import org.eclipse.ecf.core.sharedobject.util.SimpleFIFOQueue;
import org.eclipse.ecf.internal.example.collab.Messages;

public class SharedObjectInputStream extends InputStream {

    protected SimpleFIFOQueue queue = new SimpleFIFOQueue();

    int currentLength;

    int currentRead;

    boolean useCompression;

    ByteArrayInputStream inputStream;

    GZIPInputStream compressor;

    public  SharedObjectInputStream(boolean compression) {
        useCompression = compression;
    }

    public  SharedObjectInputStream() {
        this(SharedObjectOutputStream.DEFAULT_COMPRESSION);
    }

    protected final void resetStreams(Data d) throws IOException {
        currentRead = 0;
        inputStream = new ByteArrayInputStream(d.getData());
        currentLength = d.getLength();
        if (useCompression) {
            compressor = new GZIPInputStream(inputStream);
        }
    }

    public final int read() throws IOException {
        if (currentRead >= currentLength) {
            Data d = (Data) queue.dequeue();
            if (d == null)
                throw new IOException(Messages.SharedObjectInputStream_EXCEPTION_NO_DATA);
            resetStreams(d);
        }
        currentRead++;
        return streamRead();
    }

    protected final int streamRead() throws IOException {
        if (useCompression) {
            return compressor.read();
        } else
            return inputStream.read();
    }

    // Method for replicated object to add data to stream
    public void add(int length, byte[] d) {
        queue.enqueue(new Data(length, d));
    }

    protected static class Data {

        int myLength;

        byte[] myData;

        protected  Data(int length, byte[] d) {
            myLength = length;
            myData = d;
        }

        protected int getLength() {
            return myLength;
        }

        protected byte[] getData() {
            return myData;
        }
    }
}
