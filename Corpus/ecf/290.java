/*******************************************************************************
 * Copyright (c)2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.ssl;

import java.io.IOException;
import java.security.cert.*;
import javax.net.ssl.*;
import org.eclipse.osgi.service.security.TrustEngine;
import org.osgi.framework.*;
import org.osgi.util.tracker.ServiceTracker;

public class ECFTrustManager implements X509TrustManager, BundleActivator {

    private static volatile BundleContext context;

    private volatile ServiceTracker trustEngineTracker = null;

    private ServiceRegistration socketFactoryRegistration;

    private ServiceRegistration serverSocketFactoryRegistration;

    public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
        // verify the cert chain
        verify(certs, authType);
        final TrustEngine[] engines = getTrustEngines();
        Certificate foundCert = null;
        for (int i = 0; i < engines.length; i++) {
            try {
                foundCert = engines[i].findTrustAnchor(certs);
                if (null != foundCert)
                    // cert chain is trust
                    return;
            } catch (final IOException e) {
                final CertificateException ce = new ECFCertificateException("Error occurs when finding trust anchor in the cert chain", certs, authType);
                ce.initCause(ce);
                throw ce;
            }
        }
        if (null == foundCert)
            throw new ECFCertificateException(//$NON-NLS-1$
            "Valid cert chain, but no trust certificate found!", //$NON-NLS-1$
            certs, //$NON-NLS-1$
            authType);
    }

    private void verify(X509Certificate[] certs, String authType) throws CertificateException {
        final int len = certs.length;
        for (int i = 0; i < len; i++) {
            final X509Certificate currentX509Cert = certs[i];
            try {
                if (i == len - 1) {
                    if (currentX509Cert.getSubjectDN().equals(currentX509Cert.getIssuerDN()))
                        currentX509Cert.verify(currentX509Cert.getPublicKey());
                } else {
                    final X509Certificate nextX509Cert = certs[i + 1];
                    currentX509Cert.verify(nextX509Cert.getPublicKey());
                }
            } catch (final Exception e) {
                final CertificateException ce = new ECFCertificateException("Certificate chain is not valid", certs, authType);
                ce.initCause(e);
                throw ce;
            }
        }
    }

    /**
	 * @throws CertificateException
	 *             not actually thrown by method, since checkClientTrusted is
	 *             unsupported.
	 */
    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        //$NON-NLS-1$
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public X509Certificate[] getAcceptedIssuers() {
        // only for client authentication
        return null;
    }

    public void start(BundleContext context1) throws Exception {
        ECFTrustManager.context = context1;
        socketFactoryRegistration = context1.registerService(SSLSocketFactory.class.getName(), new ECFSSLSocketFactory(), null);
        serverSocketFactoryRegistration = context1.registerService(SSLServerSocketFactory.class.getName(), new ECFSSLServerSocketFactory(), null);
    }

    public void stop(BundleContext context1) throws Exception {
        if (socketFactoryRegistration != null) {
            socketFactoryRegistration.unregister();
            socketFactoryRegistration = null;
        }
        if (serverSocketFactoryRegistration != null) {
            serverSocketFactoryRegistration.unregister();
            serverSocketFactoryRegistration = null;
        }
        if (trustEngineTracker != null) {
            trustEngineTracker.close();
            trustEngineTracker = null;
        }
        ECFTrustManager.context = null;
    }

    private TrustEngine[] getTrustEngines() {
        if (trustEngineTracker == null) {
            trustEngineTracker = new ServiceTracker(context, TrustEngine.class.getName(), null);
            trustEngineTracker.open();
        }
        final Object objs[] = trustEngineTracker.getServices();
        final TrustEngine[] result = new TrustEngine[objs.length];
        System.arraycopy(objs, 0, result, 0, objs.length);
        return result;
    }
}
