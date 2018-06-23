/*******************************************************************************
 * Copyright (c) 2008 Versant Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Kuppe (mkuppe <at> versant <dot> com) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.discovery.ui.property;

import java.util.Arrays;
import java.util.List;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.ecf.discovery.ui.model.IServiceInfo;
import org.eclipse.ecf.discovery.ui.model.IServiceTypeID;

public class ServiceTypeTester extends PropertyTester {

    /* (non-Javadoc)
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
	 */
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (args == null || !(expectedValue instanceof Boolean)) {
            return false;
        }
        boolean expected = ((Boolean) expectedValue).booleanValue();
        boolean isServiceType = isServiceType(receiver, args);
        if (expected && isServiceType) {
            return true;
        } else if (expected && !isServiceType) {
            return false;
        } else if (!expected && isServiceType) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isServiceType(Object receiver, Object[] args) {
        // convert the args to a list we can use for comparison
        List asList = Arrays.asList(args);
        // extract the sublist from the service types
        IServiceInfo serviceInfo = (IServiceInfo) receiver;
        IServiceTypeID serviceTypeId = serviceInfo.getServiceID().getServiceTypeID();
        List services = serviceTypeId.getEcfServices();
        if (services.size() < args.length) {
            return false;
        }
        List ecfServices = services.subList(0, args.length);
        return asList.equals(ecfServices);
    }
}
