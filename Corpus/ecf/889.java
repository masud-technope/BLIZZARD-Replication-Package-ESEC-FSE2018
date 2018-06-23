/*******************************************************************************
 * Copyright (c) 2014 Markus Alexander Kuppe and others. All rights reserved. 
 * This program and the accompanying materials are made available under the terms 
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Markus Alexander Kuppe - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.discovery.listener;

import org.eclipse.ecf.discovery.IDiscoveryLocator;
import org.eclipse.ecf.discovery.IServiceEvent;

public class ThreadTestServiceListener extends TestServiceListener {

    private volatile Thread currentThread;

    public Thread getCallingThread() {
        return currentThread;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.listener.TestServiceListener#triggerDiscovery()
	 */
    public boolean triggerDiscovery() {
        return true;
    }

    public  ThreadTestServiceListener(int eventsToExpect, IDiscoveryLocator aLocator, String testName, String testId) {
        super(eventsToExpect, aLocator, testName, testId);
    }

    public void serviceDiscovered(IServiceEvent anEvent) {
        if (matchesExpected(anEvent)) {
            currentThread = Thread.currentThread();
            super.serviceDiscovered(anEvent);
        }
    }
}
