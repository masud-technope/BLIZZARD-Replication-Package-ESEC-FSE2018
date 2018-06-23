/******************************************************************************* 
 * Copyright (c) 2010-2011 Naumen. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Pavel Samolisov - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.tests.remoteservice.rpc.server;

import org.eclipse.ecf.tests.remoteservice.rpc.common.IEcho;

public class EchoHandler implements IEcho {

    public String echo(String str) {
        // TODO using logger		
        System.out.println(str);
        return str;
    }
}
