/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.sync.doc;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.sync.IModelSynchronizationStrategy;

/**
 * Factory for creating {@link IModelSynchronizationStrategy} instances for 
 * a uniquely identified entity.  This interface is exposed as a service and
 * provides an entry point for clients.
 * 
 * @since 2.1
 */
public interface IDocumentSynchronizationStrategyFactory {

    public static final String SYNCHSTRATEGY_TYPE = "org.eclipse.ecf.sync.doc";

    /**
	 * Get an IModelSynchronizationStrategy for a unique ID.  Should not be <code>null</code>.
	 * @param uniqueID the uniqueID to identify the client of the {@link IModelSynchronizationStrategy}.
	 * @param isInitiator whether the client is the initiator of the
	 * shared editing, or the receiver.
	 * @return IModelSynchronizationStrategy for the given uniqueID.
	 */
    public IModelSynchronizationStrategy createDocumentSynchronizationStrategy(ID uniqueID, boolean isInitiator);

    /**
	 * Clean up the synchronization strategy caching for a given uniqueID. Should not be <code>null</code>.
	 * @param uniqueID the ID of the 
	 */
    public void disposeSynchronizationStrategy(ID uniqueID);
}
