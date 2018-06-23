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
package org.eclipse.ecf.bulletinboard;

/**
 * This class models the credentials to be passed to the bulletin board when
 * logging in.
 * 
 * @author Erkki
 */
public interface IBBCredentials {

    /**
	 * Returns the username component.
	 * 
	 * @return username component
	 */
    public String getUsername();

    /**
	 * Returns the passwrod component.
	 * 
	 * @return password component
	 */
    public String getPassword();
}
