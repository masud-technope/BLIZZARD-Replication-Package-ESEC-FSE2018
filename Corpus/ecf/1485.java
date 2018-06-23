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
package org.eclipse.ecf.internal.examples.datashare.app;

import org.eclipse.ecf.datashare.IChannelListener;
import org.eclipse.ecf.datashare.events.IChannelEvent;
import org.eclipse.ecf.datashare.events.IChannelMessageEvent;

public class DatashareManagerApplication extends AbstractDatashareApplication {

    private static final String DEFAULT_CONTAINER_TYPE = "ecf.jms.activemq.tcp.manager";

    public static final String DEFAULT_CONTAINER_TARGET = "tcp://localhost:61616/exampleTopic";

    /**
	 * Create a channel listener that simply prints out messages to System.out
	 */
    protected IChannelListener createChannelListener() {
        return new IChannelListener() {

            public void handleChannelEvent(IChannelEvent event) {
                if (event instanceof IChannelMessageEvent) {
                    IChannelMessageEvent messageEvent = (IChannelMessageEvent) event;
                    // print to system out
                    System.out.println("Received message from " + messageEvent.getFromContainerID().getName() + "\n\tmessage=" + new String(messageEvent.getData()));
                }
            }
        };
    }

    protected void processArgs(String[] args) {
        containerType = DEFAULT_CONTAINER_TYPE;
        containerId = DEFAULT_CONTAINER_TARGET;
        // no connecting for manager
        targetId = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-containerType")) {
                containerType = args[i + 1];
                i++;
            } else if (args[i].equals("-containerId")) {
                containerId = args[i + 1];
                i++;
            }
        }
    }

    protected String usageApplicationId() {
        return "org.eclipse.ecf.examples.datashare.app.DatashareServer";
    }

    protected String usageParameters() {
        StringBuffer buf = new StringBuffer("\n\t-containerType <default:" + DEFAULT_CONTAINER_TYPE + ">");
        buf.append("\n\t-targetId <default:" + DEFAULT_CONTAINER_TARGET + ">");
        return buf.toString();
    }
}
