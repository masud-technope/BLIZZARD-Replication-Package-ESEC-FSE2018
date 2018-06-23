/*******************************************************************************
 * Copyright (c) 2007 Versant Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Kuppe (mkuppe <at> versant <dot> com) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.provider.jslp;

import org.eclipse.ecf.core.util.StringUtils;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.internal.provider.jslp.NullPatternAdvertiser;
import org.eclipse.ecf.internal.provider.jslp.NullPatternLocator;
import org.eclipse.ecf.provider.jslp.container.JSLPDiscoveryContainer;
import org.eclipse.ecf.tests.discovery.Activator;
import org.eclipse.ecf.tests.discovery.DiscoveryContainerTest;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class JSLPDiscoveryTest extends DiscoveryContainerTest {

    static {
        // tests need root privileges to bind to slp port 427 in SA mode
        int port;
        try {
            port = Integer.parseInt(System.getProperty("net.slp.port", "427"));
        } catch (NumberFormatException e) {
            port = 427;
        }
        if (port <= 1024) {
            System.err.println("jSLP tests require root privileges to bind to port 427 (Alternatively the port can be set to a high port via -Dnet.slp.port=theHighPort");
        }
    }

    public  JSLPDiscoveryTest() {
        super(JSLPDiscoveryContainer.NAME);
        setWaitTimeForProvider(JSLPDiscoveryContainer.REDISCOVER);
        //TODO-mkuppe https://bugs.eclipse.org/bugs/show_bug.cgi?id=230182
        setComparator(new JSLPTestComparator());
        //TODO-mkuppe https://bugs.eclipse.org/bugs/show_bug.cgi?id=218308
        setScope(IServiceTypeID.DEFAULT_SCOPE[0]);
        String[] ips;
        // tests need root privileges to bind to slp port 427 in SA mode
        try {
            String str = System.getProperty("net.slp.interfaces", "127.0.0.1");
            ips = StringUtils.split(str, ",");
        } catch (Exception e) {
            ips = new String[] { "127.0.0.1" };
        }
        setHostname(ips[0]);
    }

    /**
	 * Test that the {@link NullPatternLocator} and {@link NullPatternAdvertiser} take
	 * over when the jSLP bundle gets stopped and that the SLP provider handles this gracefully
	 * @throws BundleException
	 */
    public void testJSLPBundleBecomesUnavailable() throws BundleException {
        Bundle bundle = null;
        try {
            // stop the bundle assuming there is only one installed
            BundleContext context = Activator.getDefault().getContext();
            Bundle[] bundles = context.getBundles();
            for (int i = 0; i < bundles.length; i++) {
                Bundle aBundle = bundles[i];
                if (aBundle.getSymbolicName().equals("ch.ethz.iks.slp")) {
                    bundle = aBundle;
                    break;
                }
            }
            assertNotNull("ch.ethz.iks.slp bundle not found", bundle);
            assertTrue(bundle.getState() == Bundle.ACTIVE);
            bundle.stop();
            assertTrue(bundle.getState() == Bundle.RESOLVED);
            assertEquals("No service should have been found since NullPatternLocator is active", discoveryLocator.getServices().length, 0);
        } finally {
            if (bundle != null) {
                bundle.start();
            }
        }
    }
}
