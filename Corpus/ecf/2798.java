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
package org.eclipse.ecf.internal.sync.resources.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ecf.sync.IModelChange;
import org.eclipse.ecf.sync.IModelChangeMessage;
import org.eclipse.ecf.sync.resources.core.IResourceChange;

public abstract class ResourceChangeMessage implements IResourceChange, IModelChangeMessage, Serializable {

    private static final long serialVersionUID = 1460461590232423466L;

    static IModelChange createResourceChange(IResource resource, int kind) {
        switch(resource.getType()) {
            case IResource.FILE:
                IFile file = (IFile) resource;
                try {
                    if (file.exists()) {
                        InputStream contents = file.getContents();
                        byte[] bytes = new byte[contents.available()];
                        contents.read(bytes);
                        return new FileChangeMessage(resource.getFullPath().toString(), kind, bytes);
                    }
                    return new FileChangeMessage(resource.getFullPath().toString(), kind, null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            case IResource.FOLDER:
                // we're not interested in folder changes
                if (kind != IResourceDelta.CHANGED) {
                    return new FolderChangeMessage(resource.getFullPath().toString(), kind);
                }
            default:
                return null;
        }
    }

    final byte[] contents;

    final int kind;

    final String path;

    private final int type;

    private boolean conflicted = false;

    private boolean ignored = false;

     ResourceChangeMessage(String path, int type, int kind, byte[] contents) {
        this.contents = contents;
        this.kind = kind;
        this.type = type;
        this.path = path;
    }

    public final String getPath() {
        return path;
    }

    public final int getType() {
        return type;
    }

    public final int getKind() {
        return kind;
    }

    public final byte[] getContents() {
        return contents;
    }

    void setConflicted(boolean conflicted) {
        this.conflicted = conflicted;
    }

    public boolean isConflicted() {
        return conflicted;
    }

    void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }

    public boolean isIgnored() {
        return ignored;
    }

    public final byte[] serialize() {
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            return bos.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }

    public Object getAdapter(Class adapter) {
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }
}
