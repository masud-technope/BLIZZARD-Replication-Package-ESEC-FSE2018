/**
 * Copyright (c) 2006 Ecliptical Software Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ecliptical Software Inc. - initial API and implementation
 */
package org.eclipse.ecf.example.pubsub;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializationUtil {

    private  SerializationUtil() {
        super();
    }

    public static byte[] serialize(Object object) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(buf);
        out.writeObject(object);
        out.close();
        return buf.toByteArray();
    }

    public static Object deserialize(byte[] buf) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buf));
        Object object = in.readObject();
        in.close();
        return object;
    }
}
