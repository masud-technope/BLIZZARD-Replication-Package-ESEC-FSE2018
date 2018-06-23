/**
 * Copyright (c) 2006 Ecliptical Software Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ecliptical Software Inc. - initial API and implementation
 */
package org.eclipse.ecf.pubsub;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IIdentifiable;

public interface ISubscription extends IIdentifiable {

    ID getHomeContainerID();

    ISubscribedService getSubscribedService();

    void dispose();
}
