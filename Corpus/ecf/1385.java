/****************************************************************************
 * Copyright (c) 2007, 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.sync.resources.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.eclipse.ecf.sync.SerializationException;

public class Message implements Serializable {

    private static final long serialVersionUID = 4858801311305630711L;

    static byte[] serialize(Object object) throws SerializationException {
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    byte[] serialize() throws SerializationException {
        return serialize(this);
    }

    static Object deserialize(byte[] bytes) throws Exception {
        final ByteArrayInputStream bins = new ByteArrayInputStream(bytes);
        final ObjectInputStream oins = new ObjectInputStream(bins);
        return oins.readObject();
    }
}
