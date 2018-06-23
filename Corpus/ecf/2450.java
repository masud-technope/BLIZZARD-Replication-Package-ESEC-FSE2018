/* Copyright (c) 2006-2009 Jan S. Rellermeyer
 * Systems Group,
 * Institute for Pervasive Computing, ETH Zurich.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of ETH Zurich nor the names of its contributors may be
 *      used to endorse or promote products derived from this software without
 *      specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package ch.ethz.iks.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Smart object input stream that is able to deserialize classes which do not
 * implement Serializable. It only rejects classes which have native code parts
 * and the OSGi ServiceReference and ServiceRegistration classes.
 * 
 * @author Jan S. Rellermeyer
 * 
 */
public final class SmartObjectInputStream extends ObjectInputStream {

    private final ObjectInputStream in;

    public  SmartObjectInputStream(final InputStream in) throws IOException {
        // implicitly: super();
        // thereby, enableOverride is set
        this.in = new ObjectInputStream(in);
    }

    protected final Object readObjectOverride() throws IOException, ClassNotFoundException {
        final byte cat = in.readByte();
        switch(cat) {
            case 0:
                // null
                return null;
            case 1:
                // TODO: cache constructors
                try {
                    final String type = in.readUTF();
                    final Class test = (Class) SmartConstants.idToClass.get(type);
                    final Class clazz = test != null ? test : Class.forName(type);
                    final Constructor constr = clazz.getConstructor(new Class[] { String.class });
                    return constr.newInstance(new Object[] { in.readUTF() });
                } catch (final Exception e) {
                    e.printStackTrace();
                    throw new IOException(e.getMessage());
                }
            case 2:
                // java serialized object
                return in.readObject();
            case 3:
                return readSmartSerializedObject();
            case 4:
                final int length = in.readByte();
                final String clazzName = in.readUTF();
                final Class clazz = Class.forName(clazzName);
                final Object[] array = (Object[]) java.lang.reflect.Array.newInstance(clazz, length);
                for (int i = 0; i < length; i++) {
                    final byte b = in.readByte();
                    if (b == -1) {
                        array[i] = null;
                    } else {
                        array[i] = readSmartSerializedObject();
                    }
                }
                return array;
            default:
                //$NON-NLS-1$
                throw new IllegalStateException("Unhandled case " + cat);
        }
    }

    private Object readSmartSerializedObject() throws IOException, ClassNotFoundException {
        // smart serialized object
        final String clazzName = in.readUTF();
        // TODO: cache this information...
        Class clazz = Class.forName(clazzName);
        try {
            final Constructor constr = clazz.getDeclaredConstructor(null);
            constr.setAccessible(true);
            final Object newInstance = constr.newInstance(null);
            int fieldCount = in.readInt();
            while (fieldCount > -1) {
                for (int i = 0; i < fieldCount; i++) {
                    final String fieldName = in.readUTF();
                    final Object value = readObjectOverride();
                    final Field field = clazz.getDeclaredField(fieldName);
                    final int mod = field.getModifiers();
                    if (!Modifier.isPublic(mod)) {
                        field.setAccessible(true);
                    }
                    field.set(newInstance, value);
                }
                clazz = clazz.getSuperclass();
                fieldCount = in.readInt();
            }
            return newInstance;
        } catch (final Exception e) {
            e.printStackTrace();
            throw new IOException("Error while deserializing " + clazzName + ": " + e.getMessage());
        }
    }

    /**
	 * 
	 * @see java.io.ObjectInputStream#read()
	 */
    public final int read() throws IOException {
        return in.read();
    }

    /**
	 * 
	 * @see java.io.ObjectInputStream#read(byte[], int, int)
	 */
    public final int read(final byte[] buf, final int off, final int len) throws IOException {
        return in.read(buf, off, len);
    }

    /**
	 * 
	 * @see java.io.ObjectInputStream#available()
	 */
    public final int available() throws IOException {
        return in.available();
    }

    /**
	 * 
	 * @see java.io.ObjectInputStream#close()
	 */
    public final void close() throws IOException {
        in.close();
    }

    /**
	 * 
	 * @see java.io.ObjectInputStream#readBoolean()
	 */
    public final boolean readBoolean() throws IOException {
        return in.readBoolean();
    }

    /**
	 * 
	 * @see java.io.ObjectInputStream#readByte()
	 */
    public final byte readByte() throws IOException {
        return in.readByte();
    }

    /**
	 * 
	 * @see java.io.ObjectInputStream#readUnsignedByte()
	 */
    public final int readUnsignedByte() throws IOException {
        return in.readUnsignedByte();
    }

    /**
	 * 
	 * @see java.io.ObjectInputStream#readChar()
	 */
    public final char readChar() throws IOException {
        return in.readChar();
    }

    /**
	 * 
	 * @see java.io.ObjectInputStream#readShort()
	 */
    public final short readShort() throws IOException {
        return in.readShort();
    }

    /**
	 * 
	 * @see java.io.ObjectInputStream#readUnsignedShort()
	 */
    public final int readUnsignedShort() throws IOException {
        return in.readUnsignedShort();
    }

    /**
	 * 
	 * @see java.io.ObjectInputStream#readInt()
	 */
    public final int readInt() throws IOException {
        return in.readInt();
    }

    /**
	 * 
	 * @see java.io.ObjectInputStream#readLong()
	 */
    public final long readLong() throws IOException {
        return in.readLong();
    }

    /**
	 * 
	 * @see java.io.ObjectInputStream#readFloat()
	 */
    public final float readFloat() throws IOException {
        return in.readFloat();
    }

    /**
	 * 
	 * @see java.io.ObjectInputStream#readDouble()
	 */
    public final double readDouble() throws IOException {
        return in.readDouble();
    }

    /**
	 * 
	 * @see java.io.ObjectInputStream#readFully(byte[])
	 */
    public final void readFully(final byte[] buf) throws IOException {
        in.readFully(buf);
    }

    /**
	 * 
	 * @see java.io.ObjectInputStream#readFully(byte[], int, int)
	 */
    public final void readFully(final byte[] buf, final int off, final int len) throws IOException {
        in.readFully(buf, off, len);
    }

    /**
	 * 
	 * @see java.io.ObjectInputStream#skipBytes(int)
	 */
    public final int skipBytes(final int len) throws IOException {
        return in.skipBytes(len);
    }

    /**
	 * @return String
	 * @throws IOException 
	 * @deprecated
	 */
    public final String readLine() throws IOException {
        return in.readLine();
    }

    /**
	 * 
	 * @see java.io.ObjectInputStream#readUTF()
	 */
    public final String readUTF() throws IOException {
        return in.readUTF();
    }
}
