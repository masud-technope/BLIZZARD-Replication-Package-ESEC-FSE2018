/*******************************************************************************
 * Copyright (c) 2009 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.discovery.listener;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.ecf.core.events.IContainerEvent;

public class TestListener {

    protected List events;

    protected int amountOfEventsToExpect;

    public  TestListener(int eventsToExpect) {
        amountOfEventsToExpect = eventsToExpect;
        events = new ArrayList(eventsToExpect);
    }

    /**
	 * @return the event that has been received by this TestListener
	 */
    public IContainerEvent[] getEvent() {
        return (IContainerEvent[]) events.toArray(new IContainerEvent[0]);
    }
}
