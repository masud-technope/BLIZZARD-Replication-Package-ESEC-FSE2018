/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.example.collab;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.example.collab.share.EclipseCollabSharedObject;

public class ClientEntry {

    IContainer container;

    EclipseCollabSharedObject sharedObject;

    String containerType;

    boolean isDisposed = false;

    public  ClientEntry(String type, IContainer cont) {
        this.containerType = type;
        this.container = cont;
    }

    public IContainer getContainer() {
        return container;
    }

    public String getContainerType() {
        return containerType;
    }

    public void setSharedObject(EclipseCollabSharedObject sharedObject) {
        this.sharedObject = sharedObject;
    }

    public EclipseCollabSharedObject getSharedObject() {
        return sharedObject;
    }

    public boolean isDisposed() {
        return isDisposed;
    }

    public void dispose() {
        isDisposed = true;
        if (sharedObject != null) {
            sharedObject.destroySelf();
            sharedObject = null;
        }
    }
}
