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
package org.eclipse.ecf.example.clients;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.presence.ui.MultiRosterView;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * XMPP client with user interface for roster. This class is an example XMPP
 * client that has a user interface. Subclasses may be created as desired.
 */
public class XMPPClientUI {

    private static final String PRESENCE_UI_VIEW1 = "org.eclipse.ecf.presence.ui.view1";

    protected static String CONTAINER_TYPE = "ecf.xmpp.smack";

    IContainer container;

    MultiRosterView view;

    public void initialize() throws ContainerCreateException {
        if (container == null) {
            container = ContainerFactory.getDefault().createContainer(CONTAINER_TYPE);
        }
    }

    protected IContainer getContainer() {
        return container;
    }

    public void dispose() {
        if (container != null) {
            container.dispose();
            container = null;
        }
    }

    public void connect(String target, String password) throws ContainerConnectException, IDCreateException {
        container.connect(IDFactory.getDefault().createID(container.getConnectNamespace(), target), ConnectContextFactory.createPasswordConnectContext(password));
    }

    public void showUI() throws PartInitException {
        IWorkbenchWindow ww = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IWorkbenchPage wp = ww.getActivePage();
        MultiRosterView rv = (MultiRosterView) wp.showView(PRESENCE_UI_VIEW1);
        rv.addContainer(container);
    }
}
