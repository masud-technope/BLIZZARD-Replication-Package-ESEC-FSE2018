/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.datashare.mergeable;

/**
 * Item conflict structure
 * 
 */
public interface IConflict extends IUpdateInfo {

    /**
	 * Get version number for conflict.
	 * 
	 * @return Integer version number. Minimum of 1.
	 */
    public Integer getVersion();
}
