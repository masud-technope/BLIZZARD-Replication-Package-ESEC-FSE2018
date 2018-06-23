/****************************************************************************
 * Copyright (c) 2012 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.tests;

public abstract class SSLContainerAbstractTestCase extends ContainerAbstractTestCase {

    protected void setUp() throws Exception {
        genericServerName = "ecf.generic.ssl.server";
        genericClientName = "ecf.generic.ssl.client";
        genericServerPort = 40000;
        genericServerIdentity = "ecfssl://localhost:{0}/secureserver";
        super.setUp();
    }
}
