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
import java.io.OutputStream;
import org.eclipse.ecf.ipc.IPCException;

public class FIFOOutputStream extends OutputStream {

    private byte[] myBuffer;

    private int myNumberOfBytes;

    private FIFO myNamedPipe;

    public  FIFOOutputStream(FIFO pipe) {
        initialize(pipe);
    }

    public void initialize(FIFO pipe) {
        myNamedPipe = pipe;
        myBuffer = new byte[8192];
        myNumberOfBytes = 0;
    }

    @Override
    public void write(int b) throws IOException {
        if (myNumberOfBytes >= myBuffer.length) {
            flush();
        }
        myBuffer[myNumberOfBytes] = (byte) b;
        myNumberOfBytes++;
    }

    @Override
    public void flush() throws IOException {
        try {
            myNamedPipe.write(myBuffer, 0, myNumberOfBytes);
            myNumberOfBytes = 0;
        } catch (IPCException e) {
            throw new IOException("Error writing out data", e);
        }
    }

    @Override
    public void close() throws IOException {
        flush();
    }
}
