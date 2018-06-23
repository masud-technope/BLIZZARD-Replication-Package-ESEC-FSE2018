/*******************************************************************************
 * Copyright (c) 2009 IBM, and others. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.filetransfer;

import junit.framework.TestCase;
import org.eclipse.osgi.util.NLS;

public abstract class AbstractFileTransferTestCase extends TestCase {

    protected long startTime;

    protected void trace(String msg) {
        Trace.trace(System.currentTimeMillis() - startTime, msg);
    }

    private static final String BANNER = "==================== {0} {1} ===================={2}";

    protected String getFullName() {
        String name = getClass().getName();
        return name.substring(name.lastIndexOf('.') + 1) + '.' + getName();
    }

    public void runBare() throws Throwable {
        startTime = System.currentTimeMillis();
        trace(NLS.bind(BANNER, new Object[] { "RUNNING", getFullName(), "" }));
        Throwable throwable = null;
        try {
            super.runBare();
        } catch (Throwable t) {
            throwable = t;
            throw t;
        } finally {
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            trace(NLS.bind(BANNER, new Object[] { (throwable != null ? "FAILED " : "PASSED "), getFullName(), " (elapsed time: " + elapsedTime + " ms)" }));
        }
    }
}
