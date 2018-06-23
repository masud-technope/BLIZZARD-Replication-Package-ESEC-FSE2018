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
package org.eclipse.ecf.example.collab.share;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.ISharedObjectConfig;
import org.eclipse.ecf.core.sharedobject.ReplicaSharedObjectDescription;
import org.eclipse.ecf.core.sharedobject.SharedObjectInitException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;

public class HelloMessageSharedObject extends GenericSharedObject {

    private String message;

    private String sender;

    public  HelloMessageSharedObject() {
        //$NON-NLS-1$
        sender = "<unknown>";
        //$NON-NLS-1$
        message = " says hello";
    }

    public  HelloMessageSharedObject(String message, String sender) {
        this.message = message;
        this.sender = sender;
    }

    public void init(ISharedObjectConfig soconfig) throws SharedObjectInitException {
        super.init(soconfig);
        Map aMap = soconfig.getProperties();
        Object[] args = (Object[]) aMap.get(ARGS_PROPERTY_NAME);
        if (args != null && args.length == 2) {
            this.message = (String) args[0];
            this.sender = (String) args[1];
        }
    }

    public void activated(ID[] others) {
        // Note: be sure to call super.activated first so
        // replication gets done
        super.activated(others);
        showMessage();
    }

    protected ReplicaSharedObjectDescription getReplicaDescription(ID remoteID) {
        Object[] remoteArgs = { message, sender };
        HashMap map = new HashMap();
        map.put(ARGS_PROPERTY_NAME, remoteArgs);
        return new ReplicaSharedObjectDescription(getClass(), getID(), getConfig().getHomeContainerID(), map, getNextReplicateID());
    }

    protected void showMessage() {
        try {
            if (!getContext().isGroupManager()) {
                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        Display.getDefault().beep();
                        MessageDialog.openInformation(null, NLS.bind(//$NON-NLS-1$
                        "Message from ", //$NON-NLS-1$
                        sender), NLS.bind(//$NON-NLS-1$
                        "{0} says {1}", //$NON-NLS-1$
                        sender, //$NON-NLS-1$
                        message));
                    }
                });
            }
        } catch (Exception e) {
            log("Exception showing message dialog ", e);
        }
        destroySelf();
    }
}
