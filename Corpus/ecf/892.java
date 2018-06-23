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
package org.eclipse.ecf.example.collab.share.url;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.ISharedObjectConfig;
import org.eclipse.ecf.core.sharedobject.ReplicaSharedObjectDescription;
import org.eclipse.ecf.core.sharedobject.SharedObjectInitException;
import org.eclipse.ecf.example.collab.share.GenericSharedObject;
import org.eclipse.ecf.internal.example.collab.ClientPlugin;
import org.eclipse.ecf.internal.example.collab.Messages;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

public class ShowURLSharedObject extends GenericSharedObject {

    private ID receiver;

    private String url;

    // Host
    public  ShowURLSharedObject(ID rcvr, String url) {
        this.receiver = rcvr;
        this.url = url;
    }

    public  ShowURLSharedObject() {
    }

    public void init(ISharedObjectConfig config) throws SharedObjectInitException {
        super.init(config);
        Map props = config.getProperties();
        Object[] args = (Object[]) props.get(ARGS_PROPERTY_NAME);
        if (args != null && args.length > 1) {
            receiver = (ID) args[0];
            url = (String) args[1];
        }
    }

    public  ShowURLSharedObject(String url) {
        this.url = url;
    }

    protected String getURL() {
        return url;
    }

    protected ReplicaSharedObjectDescription getReplicaDescription(ID remoteMember) {
        Object args[] = { receiver, url };
        HashMap map = new HashMap();
        map.put(ARGS_PROPERTY_NAME, args);
        return new ReplicaSharedObjectDescription(getClass(), getID(), getHomeContainerID(), map, getNextReplicateID());
    }

    protected void replicate(ID remoteMember) {
        // handle replication.
        if (receiver == null) {
            super.replicate(remoteMember);
            return;
        } else // if we're replicating on activation
        if (remoteMember == null) {
            try {
                ReplicaSharedObjectDescription createInfo = getReplicaDescription(receiver);
                if (createInfo != null)
                    getContext().sendCreate(receiver, createInfo);
            } catch (IOException e) {
                log("Exception in replicateSelf", e);
            }
        }
    }

    public void activated(ID[] others) {
        if (!getContext().isGroupManager()) {
            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
                    IWebBrowser browser;
                    try {
                        browser = support.createBrowser(null);
                        browser.openURL(new URL(url));
                    } catch (Exception e) {
                        MessageDialog.openError(null, Messages.ShowURLSharedObject_MSGBOX_OPENURL_ERROR_TITLE, NLS.bind(Messages.ShowURLSharedObject_MSGBOX_OPENURL_ERROR_TEXT, e.getLocalizedMessage()));
                        ClientPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, ClientPlugin.PLUGIN_ID, IStatus.ERROR, Messages.ShowURLSharedObject_STATUS_OPENURL_MESSAGE, e));
                    }
                }
            });
        }
        super.activated(others);
        destroySelfLocal();
    }
}
