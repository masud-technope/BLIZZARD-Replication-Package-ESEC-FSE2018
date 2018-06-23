/*******************************************************************************
 * Copyright (c) 2013 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package com.mycorp.examples.timeservice.host;

import com.mycorp.examples.timeservice.ITimeService;

public class TimeServiceImpl implements ITimeService {

    /**
	 * Implementation of my time service. 
	 */
    public Long getCurrentTime() {
        // Print out to host std out that a call to this service was received.
        System.out.println("TimeServiceImpl:  Received call to getCurrentTime()");
        // this host.
        return new Long(System.currentTimeMillis());
    }
}
