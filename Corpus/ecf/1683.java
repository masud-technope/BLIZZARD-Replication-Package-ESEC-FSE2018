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

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.osgi.util.NLS;

public class JobChangeTraceListener implements IJobChangeListener {

    private long startTime;

    public  JobChangeTraceListener(long startTime) {
        this.startTime = startTime;
    }

    private String toString(IJobChangeEvent event) {
        return NLS.bind("job={0}", event.getJob());
    }

    private void trace(String msg) {
        Trace.trace(System.currentTimeMillis() - startTime, msg);
    }

    public void aboutToRun(IJobChangeEvent event) {
        trace("aboutToRun(" + toString(event) + ")");
    }

    public void awake(IJobChangeEvent event) {
        trace("awake(" + toString(event) + ")");
    }

    public void done(IJobChangeEvent event) {
        trace("done(" + toString(event) + ")");
    }

    public void running(IJobChangeEvent event) {
        trace("running(" + toString(event) + ")");
    }

    public void scheduled(IJobChangeEvent event) {
        trace("scheduled(" + toString(event) + ")");
    }

    public void sleeping(IJobChangeEvent event) {
        trace("sleeping(" + toString(event) + ")");
    }
}
