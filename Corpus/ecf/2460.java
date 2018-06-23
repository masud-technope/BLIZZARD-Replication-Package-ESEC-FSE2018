/******************************************************************************* 
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.remoteservice.rest.identity;

import java.net.URI;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.identity.URIID;

public class RestID extends URIID {

    private static final long serialVersionUID = 2082626839598770167L;

    private long rsId = 0;

    public  RestID(Namespace namespace, URI uri) {
        super(namespace, uri);
    }

    public long getRsId() {
        return rsId;
    }

    public void setRsId(long rsId) {
        this.rsId = rsId;
    }

    public String toString() {
        //$NON-NLS-1$
        StringBuffer sb = new StringBuffer("RestID[");
        //$NON-NLS-1$
        sb.append(getName()).append("]");
        return sb.toString();
    }
}
