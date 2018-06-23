/****************************************************************************
* Copyright (c) 2004 Composent, Inc. and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Composent, Inc. - initial API and implementation
*****************************************************************************/
package org.eclipse.ecf.tests.provider.jmdns;

import org.eclipse.ecf.tests.discovery.DiscoveryServiceTest;

public class JMDNSDiscoveryServiceTest extends DiscoveryServiceTest {

    public  JMDNSDiscoveryServiceTest() {
        super("ecf.discovery.jmdns");
        setHostname(System.getProperty("net.mdns.interface", "127.0.0.1"));
    }
}
