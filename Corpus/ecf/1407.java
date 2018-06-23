/****************************************************************************
* Copyright (c) 2004 Composent, Inc. and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Composent, Inc. - initial API and implementation
*****************************************************************************/
package org.eclipse.ecf.provider.comm.tcp;

import java.io.*;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.ECFProviderDebugOptions;
import org.eclipse.ecf.internal.provider.ProviderPlugin;

public class ExObjectOutputStream extends ObjectOutputStream {

    public  ExObjectOutputStream(OutputStream out) throws IOException {
        super(out);
    }

    public  ExObjectOutputStream(OutputStream out, boolean backwardCompatibility) throws IOException, SecurityException {
        this(out);
        if (backwardCompatibility) {
            try {
                super.enableReplaceObject(true);
                debug(//$NON-NLS-1$
                "replaceObject");
            } catch (Exception e) {
                throw new IOException("Exception setting up ExObjectOutputStream: " + e.getMessage());
            }
        }
    }

    protected void debug(String msg) {
        Trace.trace(ProviderPlugin.PLUGIN_ID, ECFProviderDebugOptions.DEBUG, msg);
    }

    protected void traceStack(String msg, Throwable e) {
        Trace.catching(ProviderPlugin.PLUGIN_ID, ECFProviderDebugOptions.EXCEPTIONS_CATCHING, ExObjectOutputStream.class, msg, e);
    }
}
