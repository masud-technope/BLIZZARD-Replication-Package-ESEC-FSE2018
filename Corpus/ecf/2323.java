/****************************************************************************
 * Copyright (c) 2013 Markus Alexander Kuppe and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Markus Alexander Kuppe - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.remoteservice.eventadmin;

import org.osgi.service.event.Event;

public class TestDistributedEventAdmin extends DistributedEventAdmin {

    public void setIgnoreSerializationFailures(boolean ignore) {
        DistributedEventAdmin.ignoreSerializationExceptions = ignore;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.remoteservice.eventadmin.DistributedEventAdmin#sendMessage(org.osgi.service.event.Event)
	 */
    @Override
    public void sendMessage(Event eventToSend) {
        super.sendMessage(eventToSend);
    }
}
