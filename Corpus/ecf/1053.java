/*******************************************************************************
 * Copyright (c) 2005, 2006 Erkki Lindpere and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Erkki Lindpere - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.bulletinboard.commons;

import java.net.URL;
import java.util.Map;
import org.eclipse.ecf.bulletinboard.IBBObject;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;

/**
 * Factory interface for creating IBBObjectId's and IBBObjects from id's.
 * 
 * TODO: perhaps creating objects directly shouldn't be exposed to clients?
 * 
 * @author Erkki
 */
public interface IBBObjectFactory {

    /**
	 * Creates an object that implements IBBObjectId.
	 * 
	 * @param namespace
	 *            the namespace
	 * 
	 * @param stringValue
	 *            the value that the id is to be created from
	 * @return the created IBBObjectId implementation
	 * @throws IDCreateException
	 */
    public ID createBBObjectId(Namespace namespace, String stringValue) throws IDCreateException;

    public ID createBBObjectId(Namespace namespace, URL baseURL, String stringValue) throws IDCreateException;

    /**
	 * Creates a named and identified IBBObject implementation.
	 * 
	 * @param id
	 *            the object's identifier
	 * @param name
	 *            the object's name
	 * @param parameters any parameters
	 * @return the created IBBObject implementation
	 */
    public IBBObject createBBObject(ID id, String name, Map<String, Object> parameters);
}
