/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.util;

import org.eclipse.ecf.core.identity.IIDFactory;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Service tracker customized to handle tracking the ECF id factory service
 * (singleton).
 * 
 * @since 3.1
 * 
 */
@SuppressWarnings("rawtypes")
public class IDFactoryTracker extends ServiceTracker {

    @SuppressWarnings("unchecked")
    public  IDFactoryTracker(BundleContext context) {
        super(context, IIDFactory.class.getName(), null);
    }

    public IIDFactory getIDFactory() {
        return (IIDFactory) getService();
    }
}
