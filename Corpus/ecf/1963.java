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
import org.eclipse.ecf.ipc.IPCException;
import org.eclipse.ecf.ipc.IPCPackage;
import org.eclipse.ecf.ipc.Utils;

/**
 * An internal class that provides the underlying system calls needed to implement 
 * named pipes.
 * <P>
 * This class is implementation dependent and not intended for general use.
 * </P>
 * 
 * @author Clark N. Hobbie
 */
public class FIFOImpl {

    static {
        IPCPackage.initializePackage();
    }

    public static final int DIRECTION_READER = 0;

    public static final int DIRECTION_WRITER = 1;

    public static final int BLOCKING_MODE_BLOCKING = 1;

    public static final int BLOCKING_MODE_NON_BLOCKING = 2;

    public static final int SELECT_READING = 1;

    public static final int SELECT_WRITING = 2;

    public static final int SELECT_BOTH = 3;

    /**
	 * The role a client is taking (reader/writer) when using a named pipe. <H2>NOTE</H2>
	 * This class really belongs in {@link FIFO} and will probably be moved there in
	 * the near future.
	 * 
	 * @author Clark N. Hobbie
	 */
    public enum PipeDirection implements  {

        Reader(DIRECTION_READER) {
        }
        , Writer(DIRECTION_WRITER) {
        }
        ;

        public int jniValue;

        private  PipeDirection(int direction) {
            jniValue = direction;
        }
    }

    public  FIFOImpl(String virtualName) {
        initialize(virtualName);
    }

    protected void initialize(String virtualName) {
        setVirtualName(virtualName);
    }

    private native void createImpl(FIFOResult result);

    public FIFOResult create() throws IPCException {
        setCreator(true);
        processVirtualActualFile();
        FIFOResult result = new FIFOResult();
        createImpl(result);
        if (result.resultCode == FIFOResult.SUCCESS) {
            setHandle(result.handle);
        }
        return result;
    }

    private void processVirtualActualFile() throws IPCException {
        String suffix = Long.toString(System.currentTimeMillis());
        //
        // if the platform DOES NOT have any particular requirements for the name
        // of a pipe, then use the virtual name as the actual name.
        //
        String actual = toActualName(suffix);
        if (null == actual) {
            actual = getVirtualName();
        } else //
        // otherwise, write the actual name into the virtual file.
        //
        {
            try {
                actual = Utils.readOrCreate(getVirtualName(), actual);
            } catch (IOException e) {
                String msg = "Error trying to read/write virtual file: " + getVirtualName();
                throw new IPCException(msg, e);
            }
        }
        setActualName(actual);
    }

    private native void writeImpl(FIFOResult result, byte[] buffer, int offset, int length, int timeoutMsec);

    /**
	 * Try to write a buffer of data within the specified time frame.
	 * <P>
	 * It is possible for a FIFO to have enough data written to it that it runs
	 * out of buffer space. When that happens the writer must wait until the
	 * reader reads enough of the data to free up some more buffer space.
	 * </P>
	 * 
	 * <P>
	 * The timeout specifies how long the caller is willing to wait for buffer
	 * space to free up. A value of less than 0 means that the caller will wait
	 * "forever." A value of 0 means that the caller will not wait at all --- if
	 * it is not possible to write immediately, then throw a timeout.
	 * </P>
	 * 
	 * <P>
	 * Any other value specifies the number of milliseconds that the caller will
	 * wait for the space to free up.
	 * </P>
	 * 
	 * <P>
	 * Note that a wait will only occur if the FIFO is full --- in all other
	 * situations, the write will complete without delay.
	 * </P>
	 * 
	 * @param buffer
	 *            The buffer that contains the data to write.
	 * @param offset
	 *            The offset within the buffer that the data starts.
	 * @param length
	 *            The number of bytes to try and write.
	 * @param timeoutMsec
	 *            See description. The number of milliseconds to wait for a
	 *            write to become possible.
	 * @return The number of bytes actually written.
	 * @throws IPCException
	 *             If the caller specified a wait time and this elapsed before
	 *             anything could be written, then this exception is thrown.
	 */
    public int write(byte[] buffer, int offset, int length, int timeoutMsec) throws IPCException {
        if (getDirection() != PipeDirection.Writer) {
            throw new IPCException("Attempt to write to a read-only pipe.");
        }
        FIFOResult result = new FIFOResult();
        writeImpl(result, buffer, offset, length, timeoutMsec);
        if (result.resultCode != FIFOResult.SUCCESS) {
            String msg = "Error writing named pipe, error code = " + result.errorCode;
            throw new IPCException(msg);
        }
        return result.byteCount;
    }

    private native void readImpl(FIFOResult result, byte[] buffer, int offset, int length, int timeoutMsec);

    public int read(byte[] buffer, int offset, int length) throws IPCException {
        return read(buffer, offset, length, -1);
    }

    public int read(byte[] buffer, int offset, int length, int timeoutMsec) throws IPCException {
        if (PipeDirection.Reader != getDirection()) {
            throw new IPCException("Attempt to read a write-only pipe.");
        }
        FIFOResult result = new FIFOResult();
        readImpl(result, buffer, offset, length, timeoutMsec);
        if (result.resultCode == FIFOResult.ERROR_PIPE_CLOSED) {
            return -1;
        }
        if (result.resultCode != FIFOResult.SUCCESS) {
            String msg = "Error reading named pipe, code = " + result.errorCode;
            throw new IPCException(msg);
        }
        return result.byteCount;
    }

    private native void openImpl(FIFOResult result, int direction);

    public FIFOResult open(PipeDirection direction) throws IPCException {
        setDirection(direction);
        if (null == getActualName()) {
            processVirtualActualFile();
        }
        FIFOResult result = new FIFOResult();
        openImpl(result, direction.jniValue);
        setHandle(result.handle);
        return result;
    }

    private String myActualName;

    private boolean myCreator;

    private PipeDirection myDirection;

    private long myHandle;

    private int myBufferSize;

    private String myVirtualName;

    public String getVirtualName() {
        return myVirtualName;
    }

    public void setVirtualName(String virtualName) {
        myVirtualName = virtualName;
    }

    protected int getBufferSize() {
        return myBufferSize;
    }

    protected void setBufferSize(int bufferSize) {
        myBufferSize = bufferSize;
    }

    protected String getActualName() {
        return myActualName;
    }

    protected PipeDirection getDirection() {
        return myDirection;
    }

    protected int getDirectionInt() {
        return myDirection.jniValue;
    }

    protected long getHandle() {
        return myHandle;
    }

    protected void setActualName(String actualName) {
        myActualName = actualName;
    }

    public void setCreator(boolean creator) {
        myCreator = creator;
    }

    protected void setDirection(PipeDirection direction) {
        myDirection = direction;
    }

    protected void setHandle(long handle) {
        myHandle = handle;
    }

    protected boolean isCreator() {
        return myCreator;
    }

    public native void createNonBlocking(FIFOResult result, String name);

    public native void readNonBlocking(FIFOResult result, byte[] buffer, int start, int length, int timeoutMsec);

    public native void writeNonBlocking(FIFOResult result, byte[] buffer, int start, int length, int timeoutMsec);

    /**
	 * Return a name that the underlying OS can use for a named pipe, given a string
	 * that is more or less unique.
	 * <P>
	 * Currently, all platforms that support named pipes use a file name to identify 
	 * the named pipe.  This method does several things.  First of all, it signals 
	 * to ECF IPC whether or not the OS uses special names for pipes.
	 * </P>
	 * <P>
	 * Windows, for example, requires that all named pipes occur in the directory, 
	 * "\\.\pipe" whereas linux has no such requirement.
	 * </P>
	 * <P>
	 * If the underlying OS has a special naming requirement, then it should return 
	 * a name that it can use, given a suffix string.
	 * </P>
	 * <P>
	 * On windows, for example, if one were to supply "123456" as the suffix, the method
	 * should return "\\.\pipe\123456"
	 * </P>
	 * <P>
	 * If the underlying OS does not have a special naming requirement, then it should 
	 * return null.  Therefore on linux, this method should return null.
	 * </P>
	 * 
	 * @param suffix A string that the actual name should be based on.  See description for 
	 * details.
	 * @return A suitable name if the platform requires that named pipes use a particular
	 * format, otherwise null.
	 */
    public static native String toActualName(String virtualName);

    /**
	 * Connect to the underlying FIFO.
	 */
    public native void connectNonBlocking(FIFOResult result);

    /**
	 * Determine if the FIFO is ready for reading, writing, or both.
	 * <P>
	 * This method is similar to the old Unix select system call, which would look 
	 * for file handles read to read/write.
	 * </P>
	 * <P>
	 * The call takes the amount of time the caller wants to wait for I/O to 
	 * be ready on the FIFO.  A value of less than 0 means wait until ready.  
	 * A value of 0 means poll the FIFO but do not wait.  Any other value is 
	 * the number of milliseconds to wait for the FIFO to be ready.
	 * </P>
	 * <P>
	 * If the client is the reader, then the method will wait until data is 
	 * ready.  If the client is a writer, it will wait for writing to become
	 * non-blocking.
	 * </P>
	 *   
	 * @param timeouitMsec
	 */
    public native void select(FIFOResult result, int timeoutMsec);
}
