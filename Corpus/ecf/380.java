/*******************************************************************************
 * Copyright (c) 2013 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package com.mycorp.examples.timeservice;

/**
 * Example OSGi service for retrieving current time in milliseconds from January
 * 1, 1970.
 * 
 */
public interface ITimeService {

    /**
	 * Get current time.
	 * 
	 * @return Long current time in milliseconds since Jan 1, 1970. Will not
	 *         return <code>null</code>.
	 */
    public Long getCurrentTime();
}
