/*******************************************************************************
* Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.examples.internal.loadbalancing.ds.consumer;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class DataProcessorClientApplication implements IApplication {

    boolean done = false;

    Object appLock = new Object();

    public Object start(IApplicationContext context) throws Exception {
        // We just wait...everything is done by DS and HelloComponent
        synchronized (appLock) {
            while (!done) {
                try {
                    appLock.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return IApplication.EXIT_OK;
    }

    public void stop() {
        synchronized (appLock) {
            done = true;
            appLock.notifyAll();
        }
    }
}
