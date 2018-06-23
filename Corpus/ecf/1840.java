/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.util;

import org.eclipse.core.runtime.IAdapterManager;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @since 3.4
 */
@SuppressWarnings("rawtypes")
public class AdapterManagerTracker extends ServiceTracker {

    @SuppressWarnings("unchecked")
    public  AdapterManagerTracker(BundleContext context, ServiceTrackerCustomizer customizer) {
        super(context, IAdapterManager.class.getName(), customizer);
    }

    public  AdapterManagerTracker(BundleContext context) {
        this(context, null);
    }

    public IAdapterManager getAdapterManager() {
        IAdapterManager adapterManager = (IAdapterManager) getService();
        // PlatformHelper class
        if (adapterManager == null)
            adapterManager = PlatformHelper.getPlatformAdapterManager();
        return adapterManager;
    }
}
