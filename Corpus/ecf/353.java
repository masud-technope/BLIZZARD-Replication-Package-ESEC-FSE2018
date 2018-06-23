/****************************************************************************
 * Copyright (c) 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.examples.internal.eventadmin.app;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class TestEventHandler implements EventHandler {

    private String name;

    public  TestEventHandler(String name) {
        this.name = name;
    }

    public void handleEvent(Event event) {
        String extra = "";
        if (event.getProperty("nonserializable") != null) {
            extra = "\n\twrapped in non-serializable=" + ((NonSerializable) event.getProperty("nonserializable")).getPayload();
        }
        System.out.println("handleEvent by: " + name + "\n\ttopic=" + event.getTopic() + "\n\tmessage=" + event.getProperty("message") + "\n\tsender=" + event.getProperty("sender") + extra);
    }
}
