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
package org.eclipse.ecf.internal.sync.doc.cola;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.sync.IModelSynchronizationStrategy;
import org.eclipse.ecf.sync.doc.IDocumentSynchronizationStrategyFactory;

/**
 *
 */
public class ColaSynchronizationStrategyFactory implements IDocumentSynchronizationStrategyFactory {

    public static final String SYNCHSTRATEGY_PROVIDER = "org.eclipse.ecf.internal.sync.doc.cola";

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.sync.doc.IDocumentSynchronizationStrategyFactory#disposeSynchronizationStragety(org.eclipse.ecf.core.identity.ID)
	 */
    public void disposeSynchronizationStrategy(ID uniqueID) {
        ColaSynchronizationStrategy.cleanUpFor(uniqueID);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.sync.IModelSynchronizationStrategy#getSyncronizationStrategy(org.eclipse.ecf.core.identity.ID, boolean)
	 */
    public IModelSynchronizationStrategy createDocumentSynchronizationStrategy(ID uniqueID, boolean isInitiator) {
        return ColaSynchronizationStrategy.getInstanceFor(uniqueID, isInitiator);
    }

    public void dispose() {
        ColaSynchronizationStrategy.dispose();
    }
}
