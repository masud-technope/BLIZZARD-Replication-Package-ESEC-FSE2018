/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.discovery;

import org.eclipse.ecf.core.events.IContainerEvent;

/**
 * Service discovery event that provides access to IServiceInfo instance
 */
public interface IServiceEvent extends IContainerEvent {

    /**
	 * Get the service info associated with this event
	 * 
	 * @return IServiceInfo any info associated with this event. May be <code>null</code>.
	 */
    public IServiceInfo getServiceInfo();
}
