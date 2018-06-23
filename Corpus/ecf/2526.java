/*******************************************************************************
 *  Copyright (c)2010 REMAIN B.V. (http://www.remainsoftware.com).
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     Wim Jongman - initial API and implementation     
 *******************************************************************************/
package org.eclipse.ecf.internal.examples.remoteservices.hello.host;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

// referenced in component.xml
public class HelloCommandProvider implements CommandProvider {

    private HelloHostApplication helloApplication;

    public  HelloCommandProvider(HelloHostApplication app) {
        this.helloApplication = app;
    }

    public void _hello(CommandInterpreter ci) {
        String arg = ci.nextArgument();
        if (arg == null) {
            return;
        }
        if (arg.equalsIgnoreCase("stop"))
            helloApplication.unregisterHelloRemoteService();
        if (arg.equalsIgnoreCase("start"))
            helloApplication.registerHelloRemoteService();
    }

    public String getHelp() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("---ECF Hello Remote Service Example Command---\n");
        buffer.append("\thello stop - stop the remote hello service. It should be undiscovered remote\n");
        buffer.append("\thello start - start the remote hello service. It should be discovered remote\n");
        return buffer.toString();
    }
}
