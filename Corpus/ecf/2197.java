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
package org.eclipse.ecf.internal.remoteservices.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;

public abstract class ConnectionHandler extends AbstractHandler {

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Job job = getJob(event);
        job.setUser(true);
        job.schedule();
        return null;
    }

    protected abstract Job getJob(ExecutionEvent event) throws ExecutionException;

    protected void showException(final Throwable t) {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                String msg = t.toString();
                if (t.getCause() != null) {
                    msg += t.getCause().toString();
                }
                //$NON-NLS-1$
                MessageDialog.openError(//$NON-NLS-1$
                null, //$NON-NLS-1$
                t.getLocalizedMessage(), //$NON-NLS-1$
                NLS.bind("Exception: {0}", msg));
            }
        });
    }
}
