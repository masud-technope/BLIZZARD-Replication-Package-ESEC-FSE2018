/*******************************************************************************
 * Copyright (c) 2009  Clark N. Hobbie
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Clark N. Hobbie - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.ipc.fifo;

import java.io.IOException;
import java.io.InputStream;
import org.eclipse.ecf.ipc.IPCException;

public class FIFOInputStream extends InputStream {

    private FIFO myPipe;

    private byte[] myBuffer;

    private int myIndex;

    private boolean myEndOfFile;

    private int myDataSize;

    private int myTimeout;

    public boolean isEndOfFile() {
        return myEndOfFile;
    }

    public void setEndOfFile(boolean endOfFile) {
        myEndOfFile = endOfFile;
    }

    public  FIFOInputStream(FIFO pipe) {
        initialize(pipe, -1);
    }

    public  FIFOInputStream(FIFO fifo, int timeoutMsec) {
        initialize(fifo, timeoutMsec);
    }

    protected void initialize(FIFO pipe, int timeoutMsec) {
        myPipe = pipe;
        myBuffer = new byte[8192];
        myIndex = -1;
        setEndOfFile(false);
        myTimeout = timeoutMsec;
    }

    public int read() throws IOException {
        if (isEndOfFile())
            return -1;
        if (-1 == myIndex || myIndex >= myDataSize) {
            loadBuffer();
        }
        if (isEndOfFile())
            return -1;
        int bvalue = myBuffer[myIndex];
        myIndex++;
        return bvalue;
    }

    protected int bytesAvailable() {
        if (myIndex < 0)
            return 0;
        return myDataSize - myIndex;
    }

    public int read(byte[] buffer, int offset, int length) throws IOException {
        if (isEndOfFile())
            return -1;
        //
        if (bytesAvailable() < 1)
            loadBuffer();
        if (isEndOfFile())
            return -1;
        int count = bytesAvailable();
        int index = 0;
        while (index < count && index < length && index < buffer.length) {
            buffer[index + offset] = (byte) read();
            index++;
        }
        return count;
    }

    protected void loadBuffer() throws IOException {
        try {
            if (-1 == myTimeout) {
                myDataSize = myPipe.read(myBuffer);
            } else {
            }
            myIndex = 0;
            if (-1 == myDataSize) {
                setEndOfFile(true);
            }
        } catch (IPCException e) {
            throw new IOException("Error reading data from pipe", e);
        }
    }
}
