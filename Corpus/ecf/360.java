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
package org.eclipse.ecf.tests.discovery.identity;

import java.net.URI;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.discovery.identity.ServiceID;

public class TestServiceID extends ServiceID {

    private static final long serialVersionUID = 2115324301690822446L;

    public  TestServiceID(TestNamespace aNamespace, IServiceTypeID aServiceTypeID, URI anURI) {
        super(aNamespace, aServiceTypeID, anURI);
    }
}
