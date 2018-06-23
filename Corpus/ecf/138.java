/****************************************************************************
 * Copyright (c) 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package org.eclipse.ecf.internal.examples.loadbalancing.server;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.examples.loadbalancing.IDataProcessor;

public class DataProcessorImpl implements IDataProcessor {

    private ID containerID;

    public  DataProcessorImpl(ID containerID) {
        this.containerID = containerID;
    }

    /**
	 * Entry point for IDataProcessor service implementation
	 */
    public String processData(String data) {
        System.out.println("DataProcessorImpl(" + containerID.getName() + ").processData data=" + data);
        if (data == null)
            return null;
        return reverseString(data);
    }

    private String reverseString(String data) {
        StringBuffer buf = new StringBuffer(data);
        buf.reverse();
        return buf.toString();
    }

    public void stop() {
    }
}
