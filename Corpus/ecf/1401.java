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
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import ch.ethz.iks.r_osgi.types.BoxedPrimitive;

/**
 * Smart object output stream that is able to deserialize classes which do not
 * implement Serializable. It only rejects classes which have native code parts
 * and the OSGi ServiceReference and ServiceRegistration classes.
 * 
 * @author Jan S. Rellermeyer
 * 
 */
public final class SmartObjectOutputStream extends ObjectOutputStream {

    private final ObjectOutputStream out;

    public  SmartObjectOutputStream(final OutputStream out) throws IOException {
        // implicitly: super();
        // thereby, enableOverride is set
        this.out = new ObjectOutputStream(out);
    }

    protected final void writeObjectOverride(final Object o) throws IOException {
        if (o == null) {
            out.writeByte(0);
            return;
        }
        final Object obj = o instanceof BoxedPrimitive ? ((BoxedPrimitive) o).getBoxed() : o;
        final String clazzName = obj.getClass().getName();
        if (SmartConstants.positiveList.contains(clazzName)) {
            // string serializable classes
            out.writeByte(1);
            final String id = (String) SmartConstants.classToId.get(clazzName);
            out.writeUTF(id != null ? id : clazzName);
            out.writeUTF(obj.toString());
            return;
        } else if (isNestedSmartSerializedObject(obj)) {
            final Object[] objArray = (Object[]) obj;
            final String clazzname = objArray.getClass().getName();
            out.write(4);
            out.writeByte(objArray.length);
            out.writeUTF(clazzname.substring(2, clazzname.length() - 1));
            for (int i = 0; i < objArray.length; i++) {
                final Object elem = objArray[i];
                if (elem == null) {
                    out.writeByte(-1);
                } else {
                    writeSmartSerializedObject(elem);
                }
            }
        } else if (obj instanceof Serializable) {
            // java serializable classes
            out.writeByte(2);
            out.writeObject(obj);
            return;
        } else {
            writeSmartSerializedObject(obj);
        }
    }

    private boolean isNestedSmartSerializedObject(final Object obj) {
        if (obj != null && obj instanceof Object[]) {
            Object[] objArray = (Object[]) obj;
            if (objArray.length > 0) {
                // iterate to skip null
                for (int i = 0; i < objArray.length; i++) {
                    if (objArray[i] instanceof Serializable) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private void writeSmartSerializedObject(final Object obj) throws IOException, NotSerializableException {
        out.writeByte(3);
        // all other classes: try smart serialization
        Class clazz = obj.getClass();
        if (SmartConstants.blackList.contains(clazz.getName())) {
            throw new NotSerializableException(//$NON-NLS-1$
            "Class " + clazz.getName() + " is not serializable");
        }
        out.writeUTF(clazz.getName());
        // TODO: cache this information...
        while (clazz != Object.class) {
            // check for native methods
            final Method[] methods = clazz.getDeclaredMethods();
            for (int j = 0; j < methods.length; j++) {
                final int mod = methods[j].getModifiers();
                if (Modifier.isNative(mod)) {
                    throw new NotSerializableException(//$NON-NLS-1$
                    "Class " + clazz.getName() + " contains native methods and is therefore not serializable.");
                }
            }
            try {
                final Field[] fields = clazz.getDeclaredFields();
                final int fieldCount = fields.length;
                int realFieldCount = 0;
                for (int i = 0; i < fieldCount; i++) {
                    final int mod = fields[i].getModifiers();
                    if (!(Modifier.isStatic(mod) || Modifier.isTransient(mod))) {
                        realFieldCount++;
                    }
                }
                out.writeInt(realFieldCount);
                for (int i = 0; i < fieldCount; i++) {
                    final int mod = fields[i].getModifiers();
                    if (Modifier.isStatic(mod) || Modifier.isTransient(mod)) {
                        continue;
                    } else if (!Modifier.isPublic(mod)) {
                        fields[i].setAccessible(true);
                    }
                    out.writeUTF(fields[i].getName());
                    writeObjectOverride(fields[i].get(obj));
                }
            } catch (final Exception e) {
                throw new NotSerializableException("Exception while serializing " + obj.toString() + ":\n" + e.getMessage());
            }
            clazz = clazz.getSuperclass();
        }
        out.writeInt(-1);
    }

    /**
	 * 
	 * @see java.io.ObjectOutputStream#write(int)
	 */
    public final void write(final int val) throws IOException {
        out.write(val);
    }

    /**
	 * 
	 * @see java.io.ObjectOutputStream#write(byte[])
	 */
    public final void write(final byte[] buf) throws IOException {
        out.write(buf);
    }

    /**
	 * 
	 * @see java.io.ObjectOutputStream#write(byte[], int, int)
	 */
    public final void write(final byte[] buf, final int off, final int len) throws IOException {
        out.write(buf, off, len);
    }

    /**
	 * 
	 * @see java.io.ObjectOutputStream#flush()
	 */
    public final void flush() throws IOException {
        out.flush();
    }

    /**
	 * 
	 * @see java.io.ObjectOutputStream#reset()
	 */
    public final void reset() throws IOException {
        out.reset();
    }

    /**
	 * 
	 * @see java.io.ObjectOutputStream#close()
	 */
    public final void close() throws IOException {
        out.close();
    }

    /**
	 * 
	 * @see java.io.ObjectOutputStream#writeBoolean(boolean)
	 */
    public final void writeBoolean(final boolean val) throws IOException {
        out.writeBoolean(val);
    }

    /**
	 * 
	 * @see java.io.ObjectOutputStream#writeByte(int)
	 */
    public final void writeByte(final int val) throws IOException {
        out.writeByte(val);
    }

    /**
	 * 
	 * @see java.io.ObjectOutputStream#writeShort(int)
	 */
    public final void writeShort(final int val) throws IOException {
        out.writeShort(val);
    }

    /**
	 * 
	 * @see java.io.ObjectOutputStream#writeChar(int)
	 */
    public final void writeChar(final int val) throws IOException {
        out.writeChar(val);
    }

    /**
	 * 
	 * @see java.io.ObjectOutputStream#writeInt(int)
	 */
    public final void writeInt(final int val) throws IOException {
        out.writeInt(val);
    }

    /**
	 * 
	 * @see java.io.ObjectOutputStream#writeLong(long)
	 */
    public final void writeLong(final long val) throws IOException {
        out.writeLong(val);
    }

    /**
	 * 
	 * @see java.io.ObjectOutputStream#writeFloat(float)
	 */
    public final void writeFloat(final float val) throws IOException {
        out.writeFloat(val);
    }

    /**
	 * 
	 * @see java.io.ObjectOutputStream#writeDouble(double)
	 */
    public final void writeDouble(final double val) throws IOException {
        out.writeDouble(val);
    }

    /**
	 * 
	 * @see java.io.ObjectOutputStream#writeBytes(java.lang.String)
	 */
    public final void writeBytes(final String str) throws IOException {
        out.writeBytes(str);
    }

    /**
	 * 
	 * @see java.io.ObjectOutputStream#writeChars(java.lang.String)
	 */
    public final void writeChars(final String str) throws IOException {
        out.writeChars(str);
    }

    /**
	 * 
	 * @see java.io.ObjectOutputStream#writeUTF(java.lang.String)
	 */
    public final void writeUTF(final String str) throws IOException {
        out.writeUTF(str);
    }
}
