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

import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.provider.jslp.identity.JSLPNamespace;
import org.eclipse.ecf.tests.discovery.ServiceInfoTest;

public class JSLPServiceInfoTest extends ServiceInfoTest {

    public  JSLPServiceInfoTest() {
        super(IDFactory.getDefault().getNamespaceByName(JSLPNamespace.NAME));
    }
}
