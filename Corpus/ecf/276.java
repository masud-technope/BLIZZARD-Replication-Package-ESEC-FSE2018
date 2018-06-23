/****************************************************************************
 * Copyright (c) 2009 Jan S. Rellermeyer and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jan S. Rellermeyer - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.tests.osgi.services.distribution;

public final class TestService1 implements TestServiceInterface1, TestServiceInterface2 {

    public String doStuff1() {
        return TestServiceInterface1.TEST_SERVICE_STRING1;
    }

    public String doStuff2() {
        return TestServiceInterface2.TEST_SERVICE_STRING2;
    }
}
