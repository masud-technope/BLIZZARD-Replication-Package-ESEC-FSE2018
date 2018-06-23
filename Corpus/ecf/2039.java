/****************************************************************************
 * Copyright (c) 2008 Versant Corp. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Versant Corp. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.discovery.ui.model;

public interface IItemStatusLineProvider {

    /**
	 * This fetches the status line text specific to this object instance.
	 * 
	 * @param object
	 * @return String the status line text
	 */
    public String getStatusLineText(Object object);
}
