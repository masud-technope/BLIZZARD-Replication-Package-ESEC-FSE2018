/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.core.user;

import java.io.Serializable;
import java.util.Map;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.core.identity.IIdentifiable;

/**
 * Interface for arbitrary ECF system user. Instances represent a user within
 * ECF providers and/or clients.
 */
public interface IUser extends IIdentifiable, Serializable, IAdaptable {

    /**
	 * Get basic name for user. Will not return <code>null</code>.
	 * @return String name
	 */
    public String getName();

    /**
	 * Get nick name for user.
	 * 
	 * @return String the user's nickname. May be <code>null</code> if user
	 *         has no nickname.
	 */
    public String getNickname();

    /**
	 * Get map of properties associated with this user. May be <code>null</code>.
	 * 
	 * @return Map properties
	 */
    public Map getProperties();
}
