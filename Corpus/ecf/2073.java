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
package org.eclipse.ecf.core.util;

import org.eclipse.ecf.core.IContainer;

/**
 * Container filter contract.  Classes implementing this interface
 * will define specific rules for whether or not a the given container
 * matches some set of criteria...e.g. whether the container is
 * currently connected or not.
 */
public interface IContainerFilter {

    /**
	 * Match a given containerToMatch against some set of implementation-defined criteria.
	 * @param containerToMatch the containerToMatch.  Will not be <code>null</code>.
	 * @return boolean true if the given containerToMatch fulfills some
	 * implementation-dependent criteria.  false if not.
	 */
    public boolean match(IContainer containerToMatch);
}
