/*******************************************************************************
* Copyright (c) 2009 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.examples.internal.remoteservices.hello.ds.host;

import org.eclipse.ecf.examples.remoteservices.hello.HelloMessage;
import org.eclipse.ecf.examples.remoteservices.hello.IHello;

public class HelloComponent implements IHello {

    public String hello(String from) {
        // This is the implementation of the IHello service
        // This method can be executed via remote proxies
        System.out.println("received hello from=" + from);
        return "Hello service host says 'Hi' back to " + from;
    }

    public String helloMessage(HelloMessage message) {
        System.out.println("received HelloMessage=" + message);
        return "Server says 'Hi' back to " + message.getFrom();
    }
}
