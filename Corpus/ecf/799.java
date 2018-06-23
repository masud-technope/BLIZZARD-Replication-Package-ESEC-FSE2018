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
package org.eclipse.ecf.provider.discovery;

import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.discovery.IServiceEvent;
import org.eclipse.ecf.discovery.ServiceContainerEvent;

public class CompositeServiceContainerEvent extends ServiceContainerEvent implements IServiceEvent {

    private final ID origId;

    public  CompositeServiceContainerEvent(final IServiceEvent event, final ID connectedId) {
        this(event.getServiceInfo(), connectedId, event.getLocalContainerID());
    }

    public  CompositeServiceContainerEvent(final IServiceInfo info, final ID connectedId, final ID origId) {
        super(info, connectedId);
        this.origId = origId;
    }

    /**
	 * @return the origId
	 */
    public ID getOriginalLocalContainerID() {
        return origId;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.ServiceContainerEvent#toString()
	 */
    public String toString() {
        //$NON-NLS-1$
        return origId.toString() + ": " + super.toString();
    }
}
