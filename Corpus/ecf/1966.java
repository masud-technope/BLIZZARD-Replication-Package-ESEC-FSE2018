/****************************************************************************
 * Copyright (c) 2008, 2009 Composent, Inc., IBM and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *    Henrich Kraemer - bug 263869, testHttpsReceiveFile fails using HTTP proxy
 *****************************************************************************/
package org.eclipse.ecf.internal.provider.filetransfer.httpclient4.ssl;

import java.io.IOException;
import java.net.Socket;
import javax.net.ssl.SSLSocketFactory;
import org.eclipse.ecf.filetransfer.events.socketfactory.INonconnectedSocketFactory;
import org.eclipse.ecf.internal.provider.filetransfer.httpclient4.Activator;
import org.eclipse.ecf.internal.provider.filetransfer.httpclient4.ISSLSocketFactoryModifier;

public class SSLSocketFactoryModifier implements ISSLSocketFactoryModifier, INonconnectedSocketFactory {

    public void dispose() {
    // nothing to do
    }

    public SSLSocketFactory getSSLSocketFactory() throws IOException {
        final SSLSocketFactory factory = Activator.getDefault().getSSLSocketFactory();
        if (factory == null)
            //$NON-NLS-1$
            throw new IOException("Cannot get socket factory");
        return factory;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.internal.provider.filetransfer.httpclient.ISSLSocketFactoryModifier#getNonconnnectedSocketFactory()
	 */
    public INonconnectedSocketFactory getNonconnnectedSocketFactory() {
        return this;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.filetransfer.events.socketfactory.INonconnectedSocketFactory#createSocket()
	 */
    public Socket createSocket() throws IOException {
        final SSLSocketFactory factory = getSSLSocketFactory();
        return factory.createSocket();
    }
}
