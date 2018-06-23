/******************************************************************************* 
 * Copyright (c) 2009 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.remoteservice.soap.identity;

import java.net.URI;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.identity.URIID;

public class SoapID extends URIID {

    private static final long serialVersionUID = -694490773145158986L;

    public  SoapID(Namespace namespace, URI uri) {
        super(namespace, uri);
    }

    public String toString() {
        //$NON-NLS-1$
        StringBuffer sb = new StringBuffer("SoapID[");
        //$NON-NLS-1$
        sb.append(getName()).append("]");
        return sb.toString();
    }
}
