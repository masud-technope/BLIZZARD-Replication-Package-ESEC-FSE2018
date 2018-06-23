/*******************************************************************************
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.tutorial.internal.distribution.common;

import org.eclipse.osgi.service.environment.EnvironmentInfo;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

    private static Activator activator;

    private BundleContext context;

    private ServiceTracker environmentInfoServiceTracker;

    public static Activator getDefault() {
        return activator;
    }

    public void start(BundleContext context) throws Exception {
        activator = this;
        this.context = context;
    }

    public void stop(BundleContext context) throws Exception {
        if (environmentInfoServiceTracker != null) {
            environmentInfoServiceTracker.close();
            environmentInfoServiceTracker = null;
        }
        this.context = null;
    }

    public EnvironmentInfo getEnvironmentInfo() {
        if (environmentInfoServiceTracker == null) {
            environmentInfoServiceTracker = new ServiceTracker(context, EnvironmentInfo.class.getName(), null);
            environmentInfoServiceTracker.open();
        }
        return (EnvironmentInfo) environmentInfoServiceTracker.getService();
    }
}
