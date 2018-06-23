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
package org.eclipse.ecf.ipc.sharedmemory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import org.eclipse.ecf.ipc.IPCException;
import org.eclipse.ecf.ipc.IPCException.Errors;

/**
 * <P>
 * A shared memory segment.
 * </P>
 * <H2>Description</H2>
 * <P>
 * This class wraps the {@link java.nio.MappedByteBuffer} class to make it a little easier
 * to use.
 * </P>
 * <P>
 * {@link #quickstart}
 * </P>
 * <P>
 * Instances of this class must correspond to a file on the host system that is read/write
 * to the running process. <A name="quickstart">
 * <H2>Quickstart</H2> </A>
 * <UL>
 * <LI>Create or connect to an existing segment via {@link #SharedMemory(String, int)}.</LI>
 * </LI>
 * <LI>Read from the segment via {@link #read(byte[], int, int, int)}.</LI>
 * <LI>Write to the segment via {@link #write(byte[], int, int, int)}.</LI>
 * <LI>You do not need to explicitly close your connection.</LI>
 * </UL>
 * 
 * @see java.nio.MappedByteBuffer
 * @author Clark N. Hobbie
 */
public class SharedMemory {

    protected File segmentFile;

    protected MappedByteBuffer byteBuffer;

    protected int size;

    protected FileChannel channel;

    private FileLock myLock;

    private boolean myFileCreator;

    public boolean isFileCreator() {
        return myFileCreator;
    }

    public void setFileCreator(boolean fileCreator) {
        myFileCreator = fileCreator;
    }

    public MappedByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public void setByteBuffer(MappedByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public boolean isConnected() {
        return byteBuffer != null;
    }

    public File getSegmentFile() {
        return segmentFile;
    }

    public int getSize() {
        return size;
    }

    public  SharedMemory() {
    }

    public  SharedMemory(File file, int size) throws IPCException {
        connect(file, size);
    }

    public  SharedMemory(String fileName, int size) throws IPCException {
        connect(fileName, size);
    }

    /**
	 * Connect this segment to the underlying shared memory segment.
	 * 
	 * @param name
	 *        The name of the file for the segment.
	 * @param size
	 *        The size of the segment.
	 */
    public void connect(File file, int size) throws IPCException {
        if (null != this.byteBuffer) {
            throw new IPCException(Errors.AlreadyConnected);
        }
        myFileCreator = false;
        try {
            myFileCreator = file.createNewFile();
        } catch (IOException e) {
            throw new IPCException(Errors.ExceptionCreatingFile, e);
        }
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
            throw new IPCException(Errors.ErrorOpeningMappingFile, e);
        }
        channel = raf.getChannel();
        try {
            this.byteBuffer = channel.map(MapMode.READ_WRITE, 0, size);
        } catch (IOException e) {
            throw new IPCException(Errors.ErrorCreatingMap, e);
        }
        this.segmentFile = file;
        this.size = size;
    }

    public void connect(String fileName, int size) throws IPCException {
        File file = new File(fileName);
        connect(file, size);
    }

    /**
	 * Write an array of bytes into a specified location in the segment, starting with a
	 * particular byte in the buffer and continuing for a specified number of bytes.
	 * 
	 * @param buf
	 *        The data to write.
	 * @param buffOffset
	 *        The location, relative to the start of the buffer, where the data should be
	 *        obtained.
	 * @param bufLength
	 *        The number of bytes to write.
	 * @param segmentOffset
	 *        The location in the buffer where the data should be written.
	 * @throws IPCException
	 *         The method will throw this exception if the segment is not actually
	 *         connected to the underlying shared memory block.
	 */
    public void write(byte[] buf, int buffOffset, int bufLength, int segmentOffset) throws IPCException {
        try {
            if (null == this.byteBuffer) {
                throw new IPCException(Errors.NotConnected);
            }
            this.byteBuffer.position(segmentOffset);
            this.byteBuffer.put(buf, buffOffset, bufLength);
        } catch (java.nio.BufferOverflowException e) {
            System.out.println("segment size = " + this.size + ", segmentOffset = " + segmentOffset + ", length = " + bufLength);
            throw e;
        }
    }

    public void write(byte value, int offset) {
        this.byteBuffer.position(offset);
        this.byteBuffer.put(value);
    }

    public void write(byte[] buf) throws IPCException {
        write(buf, 0, buf.length, 0);
    }

    public void write(int value, int offset) {
        byte b = (byte) value;
        write(b, offset);
    }

    public void write(byte[] buf, int segmentOffset) throws IPCException {
        write(buf, 0, buf.length, segmentOffset);
    }

    /**
	 * <P>
	 * Read bytes up to a specified number of bytes into the provided buffer.
	 * </P>
	 * <P>
	 * This method returns the number of bytes that were actually read. If fewer bytes are
	 * left in the segment than requested, then the method returns the number of bytes
	 * read.
	 * </P>
	 * 
	 * @param buf
	 *        The buffer where the bytes read should be stored.
	 * @param boffset
	 *        The offset into buf where the bytes should be placed.
	 * @param length
	 *        The number of bytes to try to read.
	 * @param soffset
	 *        The offset within the segment where the read should start.
	 * @return The number of bytes actually read. See description for details.
	 * @throws IPCException
	 *         This method throws this exception with the error set to
	 *         {@link Errors#NotConnected} if the instance has not been connected
	 *         to a segment yet.
	 */
    public int read(int soffset, byte[] buf, int boffset, int length) throws IPCException {
        if (null == byteBuffer) {
            throw new IPCException(Errors.NotConnected);
        }
        if (length > byteBuffer.remaining())
            length = byteBuffer.remaining();
        byteBuffer.position(soffset);
        byteBuffer.get(buf, boffset, length);
        return length;
    }

    /**
	 * <P>
	 * Read bytes from the segment into a buffer, storing them in a location offset from
	 * the start of the buffer.
	 * </P>
	 * <P>
	 * This method is equivalent to calling:
	 * </P>
	 * <CODE>
	 * <PRE>
	 *     read(0, buf, bufferOffset, buf.length);
	 * </PRE>
	 * </CODE>
	 * 
	 * @param buf
	 *        The buffer where the bytes read should be placed.
	 * @param bufferOffset
	 *        The location in the buffer where the bytes read should start.
	 * @return The number of bytes actually read.
	 * @throws IPCException
	 *         {@link #read(int, byte[], int, int)}.
	 * @see #read(int, byte[], int, int)
	 */
    public int read(byte[] buf, int bufferOffset) throws IPCException {
        return read(0, buf, bufferOffset, buf.length);
    }

    /**
	 * Read bytes from the start of the segment.
	 * <P>
	 * This method is equivalent to calling
	 * </P>
	 * <CODE>
	 * <PRE>
	 *     read(0, buf, 0, buf.length);
	 * </PRE>
	 * </CODE>
	 * <P>
	 * See that method for details.
	 * </P>
	 * 
	 * @param buf
	 *        The buffer where bytes read should be placed.
	 * @return The number of bytes read.
	 * @throws IPCException
	 *         see {@link #read(int, byte[], int, int)}.
	 * @see {@link #read(int, byte[], int, int)}
	 */
    public int read(byte[] buf) throws IPCException {
        return read(0, buf, 0, buf.length);
    }

    /**
	 * Read in data from the segment, starting at a particular location in the segment.
	 * <P>
	 * This method is equivalent to calling
	 * </P>
	 * <CODE>
	 * <PRE>
	 *     read(segmentOffset, buf, 0, buf.length);
	 * </PRE>
	 * </CODE>
	 * <P>
	 * See that method for details.
	 * </P>
	 * 
	 * @param segmentOffset
	 *        The byte index into the segment where reading should start.
	 * @param buf
	 *        Where the bytes read should be placed.
	 * @return The number of bytes actually read.
	 * @throws IPCException
	 *         see {@link #read(int, byte[], int, int)}
	 * @see #read(int, byte[], int, int)
	 */
    public int read(int segmentOffset, byte[] buf) throws IPCException {
        return read(segmentOffset, buf, 0, buf.length);
    }

    /**
	 * Return the byte at a particular location in the segment.
	 * 
	 * @param offset
	 *        The location of the byte in the segment.
	 * @return The value of the byte.
	 * @throws IndexOutOfBoundsException
	 *         If offset is negative or larger than the size of the segment minus 1.
	 */
    public byte getByte(int offset) throws IPCException {
        return byteBuffer.get(offset);
    }

    /**
	 * Reserve the shared memory segment.
	 * <P>
	 * It is not clear from the JRE documentation whether or not locking the segment will
	 * stop modifications to the segment or not. {@linkplain FileChannel#lock() for
	 * details.}
	 * 
	 * @throws IOException
	 */
    public void lock() throws IOException {
        myLock = channel.lock();
    }

    public void unlock() throws IOException {
        myLock.release();
    }

    /**
	 * Put some data at a location.
	 * <P>
	 * The entire contents of the buffer are written to a position in the segment defined
	 * by the offset parameter. If the amount of data exceeds the size of the segment, the
	 * method throws an exception.
	 * </P>
	 * 
	 * @param offset
	 *        The location in the segment, in bytes, where the data will be written.
	 * @param data
	 *        The data to write. The contents of the array will be written.
	 * @throws IPCException
	 */
    public void put(int offset, byte[] data) throws IPCException {
        write(data, offset);
    }

    /**
	 * <P>
	 * Retrieve a line of text from the segment.
	 * </P>
	 * <P>
	 * This method goes to the specified location in the segment and tries to read in
	 * characters. It continues until it hits the end of the segment, or it encounters the
	 * end of the string. The end of a string is denoted by any of the following sequences
	 * of characters:
	 * </P>
	 * <UL>
	 * <LI>'\n'</LI>
	 * <LI>'\r'</LI>
	 * <LI>'\n\r'</LI>
	 * </UL>
	 * <P>
	 * The method uses the underlying character set as defined by
	 * {@link Charset#defaultCharset()}
	 * </P>
	 * <P>
	 * The supplied buffer is used to determine the maximum length of the string in that,
	 * if no line termination has been encountered before a number of bytes equal to the
	 * size of the buffer are read, then the characters accumulated so far are returned.
	 * </P>
	 * <P>
	 * Any line termination character(s) are not included in the string returned.
	 * </P>
	 * 
	 * @param offset
	 *        The location in the segment to start reading.
	 * @param buffer
	 *        A buffer to contain the bytes from the shared memory segment.
	 * @return The resulting string.
	 * @throws IOException
	 *         If the method tries to read past the end of the segment, either because it
	 *         started outside the segment or because the string did not have any line
	 *         termination.
	 * @throws IPCException
	 *         This exception is thrown if the object is not currently connected to a
	 *         shared memory segment.
	 */
    public String getLine(int offset, byte[] buffer) throws IOException, IPCException {
        StringBuffer sb = new StringBuffer();
        //
        // For the sake of efficiency, try to read a block of 256 bytes
        //
        int count = read(offset, buffer);
        ByteBuffer bb = ByteBuffer.wrap(buffer, 0, count);
        CharBuffer cb = Charset.defaultCharset().decode(bb);
        int charCount = cb.length();
        for (int index = 0; index < charCount; index++) {
            char c = cb.get();
            if (c == '\n' || c == '\r')
                break;
            sb.append(c);
        }
        return sb.toString();
    }

    /**
	 * Return a string from the specified location in the segment; the string may only
	 * take the specified number of bytes.
	 * <P>
	 * This method is equivalent to calling
	 * </P>
	 * <CODE>
	 * <PRE>
	 *     byte[] buf = new byte[maxBytes];
	 *     getLine(offset, buf);
	 * </PRE>
	 * </CODE>
	 * <P>
	 * See that method for details.
	 * </P>
	 * <P>
	 * If the underlying string does not end after maxBytes, the method returns the
	 * substring that takes up that number of bytes.
	 * </P>
	 * 
	 * @param offset
	 *        The location within the segment where the string starts.
	 * @param maxBytes
	 *        The maximum number of bytes for the string.
	 * @return The string found.
	 * @throws IOException
	 *         See {@link #getLine(int, byte[])}.
	 * @throws IPCException
	 *         See {@link #getLine(int, byte[])}.
	 * @see #getLine(int, byte[])
	 */
    public String getLine(int offset, int maxBytes) throws IOException, IPCException {
        byte[] buffer = new byte[maxBytes];
        return getLine(offset, buffer);
    }

    public void putString(int offset, String s) {
        byte[] buff = s.getBytes();
        byteBuffer.position(offset);
        byteBuffer.put(buff);
    }

    public void putLine(int offset, String s) {
        s = s + '\n';
        putString(offset, s);
    }
}
