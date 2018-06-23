/*******************************************************************************
 * Copyright (c) 2006 Remy Suen, Composent Inc., and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Remy Suen - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.tests.sharedobject;

import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.eclipse.ecf.core.sharedobject.SharedObjectAddAbortException;

public class SharedObjectAddAbortExceptionTest extends TestCase {

    public void testGetTimeout() {
        try {
            throw new SharedObjectAddAbortException(null, (Throwable) null, 10);
        } catch (SharedObjectAddAbortException e) {
            assertEquals(10, e.getTimeout());
        }
        try {
            // Regression test for bug #167019
            throw new SharedObjectAddAbortException(null, (Map) null, 10);
        } catch (SharedObjectAddAbortException e) {
            assertEquals(10, e.getTimeout());
        }
        try {
            // Regression test for bug #167019
            throw new SharedObjectAddAbortException(null, (List) null, (Map) null, 10);
        } catch (SharedObjectAddAbortException e) {
            assertEquals(10, e.getTimeout());
        }
    }
}
