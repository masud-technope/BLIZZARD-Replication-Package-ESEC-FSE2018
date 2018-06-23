/******************************************************************************* 
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andre Dietisheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.remoteservice.rest.util;

import org.osgi.framework.*;

public class DSUtil {

    /**
	 * Checks whether the declarative services daemon is running.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return <tt>true</tt>, if is declarative services are running
	 */
    public static boolean isRunning(BundleContext context) {
        ServiceReference[] serviceReferences = null;
        try {
            serviceReferences = context.getServiceReferences(IDSPresent.class.getName(), null);
        } catch (InvalidSyntaxException e) {
        }
        return serviceReferences != null && serviceReferences.length > 0;
    }
}
