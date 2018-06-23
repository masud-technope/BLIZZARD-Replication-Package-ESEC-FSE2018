/****************************************************************************
 * Copyright (c) 2004, 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.example.collab.ui;

import org.eclipse.ecf.core.user.IUser;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

class TreeDropTarget extends ChatDropTarget {

    public  TreeDropTarget(LineChatClientView view, Control control, ChatComposite comp) {
        super(view, control, comp);
    }

    protected Object getData(DropTargetEvent event) {
        Widget item = event.item;
        if (item != null) {
            return item.getData();
        } else {
            return null;
        }
    }

    public void dragEnter(DropTargetEvent event) {
        if (event.detail == DND.DROP_DEFAULT) {
            if ((event.operations & DND.DROP_COPY) != 0) {
                event.detail = DND.DROP_COPY;
            } else {
                event.detail = DND.DROP_NONE;
            }
        }
        Object item = getData(event);
        if (item == null) {
            event.detail = DND.DROP_NONE;
            return;
        } else {
            if (item instanceof IUser) {
                selectedUser = (IUser) item;
            } else {
                event.detail = DND.DROP_NONE;
                return;
            }
        }
        // will accept text but prefer to have files dropped
        for (int i = 0; i < event.dataTypes.length; i++) {
            if (fileTransfer.isSupportedType(event.dataTypes[i])) {
                event.currentDataType = event.dataTypes[i];
                // files should only be copied
                if (event.detail != DND.DROP_COPY) {
                    event.detail = DND.DROP_NONE;
                    selectedUser = null;
                }
                break;
            }
        }
    }

    public void dragOver(DropTargetEvent event) {
        if ((event.operations & DND.DROP_COPY) != 0) {
            event.detail = DND.DROP_COPY;
        } else {
            event.detail = DND.DROP_NONE;
        }
        Object item = getData(event);
        if (item == null) {
            event.detail = DND.DROP_NONE;
        } else {
            if (item instanceof IUser) {
                selectedUser = (IUser) item;
            } else {
                event.detail = DND.DROP_NONE;
            }
        }
    }
}
