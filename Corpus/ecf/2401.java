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
import org.eclipse.ecf.internal.remoteservice.Activator;

/**
 * @since 8.1
 */
public class ObjectSerializationUtil {

    public static final int DEFAULT_BAOS_BUFFER_SIZE = 4096;

    private StreamUtil streamUtil = new StreamUtil(DEFAULT_BAOS_BUFFER_SIZE);

    public byte[] readToByteArray(InputStream input) throws IOException {
        return streamUtil.toByteArray(input);
    }

    public void writeByteArray(OutputStream outs, byte[] bytes) throws IOException {
        streamUtil.writeByteArray(outs, bytes);
    }

    public Object deserializeFromBytes(byte[] bytes) throws IOException {
        if (bytes.length == 0)
            return null;
        ByteArrayInputStream bins = new ByteArrayInputStream(bytes);
        ObjectInputStream oins = Activator.getDefault().createObjectInputStream(bins);
        Object result = null;
        try {
            result = oins.readObject();
        } catch (ClassNotFoundException e) {
            IOException t = new IOException("Class not found when deserializing object");
            t.setStackTrace(e.getStackTrace());
            throw t;
        }
        return result;
    }

    public byte[] serializeToBytes(Object object) throws IOException {
        if (object == null)
            return new byte[0];
        ByteArrayOutputStream bos = new ByteArrayOutputStream(DEFAULT_BAOS_BUFFER_SIZE);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(object);
        return bos.toByteArray();
    }
}
