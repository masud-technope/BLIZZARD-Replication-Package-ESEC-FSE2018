/*******************************************************************************
 * Copyright (c) 2009 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Chi Jian Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.docshare2.messages;

import java.io.*;
import org.eclipse.ecf.sync.IModelChangeMessage;
import org.eclipse.ecf.sync.SerializationException;

public class FileSystemDocumentChangeMessage implements IModelChangeMessage, Serializable {

    private static final long serialVersionUID = -7047856986307256766L;

    private String path;

    private IModelChangeMessage message;

    public  FileSystemDocumentChangeMessage(String path, IModelChangeMessage message) {
        this.path = path;
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public IModelChangeMessage getMessage() {
        return message;
    }

    public Object getAdapter(Class adapter) {
        return null;
    }

    public byte[] serialize() throws SerializationException {
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            return bos.toByteArray();
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }
}
