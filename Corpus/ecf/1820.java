/*******************************************************************************
 * Copyright (c) 2009 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.provider.jmdns;

import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.provider.jmdns.identity.JMDNSNamespace;
import org.eclipse.ecf.tests.discovery.ServiceInfoTest;

public class JMDNSServiceInfoTest extends ServiceInfoTest {

    public  JMDNSServiceInfoTest() {
        super(IDFactory.getDefault().getNamespaceByName(JMDNSNamespace.NAME));
    }
}
