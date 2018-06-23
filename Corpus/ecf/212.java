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

public class ExObjectInputStream extends ObjectInputStream {

    public  ExObjectInputStream(InputStream in) throws IOException, SecurityException {
        super(in);
    }

    public  ExObjectInputStream(InputStream in, boolean backwardCompatibility) throws IOException, SecurityException {
        super(in);
        if (backwardCompatibility) {
            try {
                super.enableResolveObject(true);
                debug(//$NON-NLS-1$
                "resolveObject");
            } catch (Exception e) {
                throw new IOException("Exception setting up ExObjectInputStream: " + e.getMessage());
            }
        }
    }

    protected void debug(String msg) {
        Trace.trace(ProviderPlugin.PLUGIN_ID, ECFProviderDebugOptions.DEBUG, msg);
    }

    protected void traceStack(String msg, Throwable e) {
        Trace.catching(ProviderPlugin.PLUGIN_ID, ECFProviderDebugOptions.EXCEPTIONS_CATCHING, ExObjectInputStream.class, msg, e);
    }
}
