/*******************************************************************************
* Copyright (c) 2013 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.server.generic.app;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * @since 6.0
 */
public class SSLGenericServerApplication extends SSLAbstractGenericServerApplication implements IApplication {

    protected final Object appLock = new Object();

    protected boolean done = false;

    public Object start(IApplicationContext context) throws Exception {
        String[] args = getArguments(context);
        processArguments(args);
        initialize();
        if (configURL != null)
            //$NON-NLS-1$
            System.out.println("SSL Generic server started with config from " + configURL);
        else
            //$NON-NLS-1$
            System.out.println("SSL Generic server started with id=" + serverName);
        waitForDone();
        return IApplication.EXIT_OK;
    }

    public void stop() {
        shutdown();
        synchronized (appLock) {
            done = true;
            appLock.notifyAll();
        }
    }

    protected void waitForDone() {
        // then just wait here
        synchronized (appLock) {
            while (!done) {
                try {
                    appLock.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    protected String[] getArguments(IApplicationContext context) {
        //$NON-NLS-1$
        String[] originalArgs = (String[]) context.getArguments().get("application.args");
        if (originalArgs == null)
            return new String[0];
        final List l = new ArrayList();
        for (int i = 0; i < originalArgs.length; i++) if (//$NON-NLS-1$
        !originalArgs[i].equals("-pdelaunch"))
            l.add(originalArgs[i]);
        return (String[]) l.toArray(new String[] {});
    }
}
