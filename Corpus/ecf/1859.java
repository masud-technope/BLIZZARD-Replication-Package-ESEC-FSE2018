/*******************************************************************************
 * Copyright (c) 2009, 2010 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.provider.discovery;

public class WithoutJSLPCompositeDiscoveryServiceContainerTest extends SingleCompositeDiscoveryServiceContainerTest {

    public  WithoutJSLPCompositeDiscoveryServiceContainerTest() {
        super("ecf.discovery.jslp", "org.eclipse.ecf.provider.jmdns.container.JMDNSDiscoveryContainer");
        setHostname(System.getProperty("net.mdns.interface", "127.0.0.1"));
    }
}
