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

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class ECFCertificateException extends CertificateException {

    private static final long serialVersionUID = 3726926966308967473L;

    private X509Certificate[] certs;

    private String type;

    public  ECFCertificateException(String msg, X509Certificate[] certs, String type) {
        super(msg);
        this.certs = certs;
        this.type = type;
    }

    public X509Certificate[] getCertificates() {
        return certs;
    }

    public String getType() {
        return type;
    }
}
