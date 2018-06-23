/*******************************************************************************
 * Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.util;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.Assert;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class BundleUtil {

    private BundleContext context;

    public  BundleUtil(BundleContext context) {
        Assert.isNotNull(context);
        this.context = context;
    }

    public Bundle[] getBundles(String bundleSymbolicName, String version) {
        if (bundleSymbolicName == null)
            return new Bundle[0];
        Bundle[] bundles = context.getBundles();
        List results = new ArrayList();
        for (int i = 0; i < bundles.length; i++) {
            if (bundles[i].getSymbolicName().equals(bundleSymbolicName)) {
                if (version == null || !bundles[i].getVersion().toString().equals(version))
                    results.add(bundles[i]);
            }
        }
        return (Bundle[]) results.toArray(new Bundle[] {});
    }

    public Bundle[] getBundles(String bundleSymbolicName) {
        return getBundles(bundleSymbolicName, null);
    }

    public void startBundles(String bundleSymbolicName, String version) throws BundleException {
        Bundle[] bundles = getBundles(bundleSymbolicName, version);
        if (bundles != null && bundles.length > 0) {
            for (int i = 0; i < bundles.length; i++) {
                bundles[i].start();
            }
        }
    }

    public void startBundles(String bundleSymbolicName) throws BundleException {
        startBundles(bundleSymbolicName, null);
    }

    public void startBundle(String bundleSymbolicName, String version) throws BundleException {
        Bundle[] bundles = getBundles(bundleSymbolicName, version);
        if (bundles.length > 1)
            throw new BundleException("More than one bundle with symbolicname=" + bundleSymbolicName + ", version=" + version);
        if (bundles.length < 1)
            throw new BundleException("No bundle with symbolicname=" + bundleSymbolicName + ", version=" + version);
        bundles[0].start();
    }

    public void startBundle(String bundleSymbolicName) throws BundleException {
        startBundle(bundleSymbolicName, null);
    }

    public void stopBundles(String bundleSymbolicName, String version) throws BundleException {
        Bundle[] bundles = getBundles(bundleSymbolicName, version);
        if (bundles != null && bundles.length > 0) {
            for (int i = 0; i < bundles.length; i++) {
                bundles[i].stop();
            }
        }
    }

    public void stopBundles(String bundleSymbolicName) throws BundleException {
        stopBundles(bundleSymbolicName, null);
    }

    public void stopBundle(String bundleSymbolicName, String version) throws BundleException {
        Bundle[] bundles = getBundles(bundleSymbolicName, version);
        if (bundles.length > 1)
            throw new BundleException("More than one bundle with symbolicname=" + bundleSymbolicName + ", version=" + version);
        if (bundles.length < 1)
            throw new BundleException("No bundle with symbolicname=" + bundleSymbolicName + ", version=" + version);
        bundles[0].stop();
    }

    public void stopBundle(String bundleSymbolicName) throws BundleException {
        stopBundle(bundleSymbolicName, null);
    }

    public void close() {
        this.context = null;
    }
}
