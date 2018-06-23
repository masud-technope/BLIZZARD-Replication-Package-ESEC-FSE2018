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
 * Channel header information
 * 
 */
public interface IChannelHeader {

    /**
	 * Get title
	 * 
	 * @return String title. May be <code>null</code>.
	 */
    public String getTitle();

    /**
	 * Get link
	 * 
	 * @return String link. May be <code>null</code>.
	 */
    public String getLink();

    /**
	 * Get description
	 * 
	 * @return String description. May be <code>null</code>.
	 */
    public String getDescription();
}
