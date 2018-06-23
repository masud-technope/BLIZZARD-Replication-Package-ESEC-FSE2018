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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ecf.sync.IModelChange;
import org.eclipse.ecf.sync.IModelChangeMessage;
import org.eclipse.ecf.sync.IModelSynchronizationStrategy;
import org.eclipse.ecf.sync.SerializationException;
import org.eclipse.ecf.sync.resources.core.IResourceChange;

public class ResourcesSynchronizationStrategy implements IModelSynchronizationStrategy {

    private static final IModelSynchronizationStrategy strategy = new ResourcesSynchronizationStrategy();

    public static IModelSynchronizationStrategy getInstance() {
        return strategy;
    }

    public IModelChange deserializeRemoteChange(byte[] bytes) throws SerializationException {
        try {
            Object object = new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
            if (object instanceof BatchModelChange || object instanceof IResourceChange) {
                return (IModelChange) object;
            } else {
                throw new SerializationException("Resources synchronization does not support deserializing " + object.getClass() + " instances");
            }
        } catch (IOException e) {
            throw new SerializationException("Failed to read object");
        } catch (ClassNotFoundException e) {
            throw new SerializationException("Failed to load object class");
        }
    }

    public IModelChangeMessage[] registerLocalChange(IModelChange localChange) {
        if (localChange instanceof ResourceChangeMessage) {
            return new IModelChangeMessage[] { (IModelChangeMessage) localChange };
        }
        return new IModelChangeMessage[0];
    }

    public IModelChange[] transformRemoteChange(IModelChange remoteChange) {
        if (remoteChange instanceof BatchModelChange) {
            IModelChangeMessage[] messages = ((BatchModelChange) remoteChange).getMessages();
            IModelChange[] changes = new IModelChange[messages.length];
            System.arraycopy(messages, 0, changes, 0, messages.length);
            return changes;
        }
        return new IModelChange[] { remoteChange };
    }

    public Object getAdapter(Class adapter) {
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }
}
