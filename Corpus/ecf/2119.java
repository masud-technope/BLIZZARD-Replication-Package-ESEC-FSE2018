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

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.discovery.IServiceTypeEvent;
import org.eclipse.ecf.discovery.ServiceTypeContainerEvent;

public class CompositeServiceTypeContainerEvent extends ServiceTypeContainerEvent implements IServiceTypeEvent {

    private final ID origLocalContainerId;

    public  CompositeServiceTypeContainerEvent(final IServiceTypeEvent event, final ID connectedId) {
        super(event.getServiceTypeID(), connectedId);
        origLocalContainerId = event.getLocalContainerID();
    }

    /**
	 * @return the origLocalContainerId
	 */
    public ID getOriginalLocalContainerId() {
        return origLocalContainerId;
    }
}
