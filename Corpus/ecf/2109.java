/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model;

import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportRegistration;

/**
 * @since 3.3
 */
public class ImportRegistrationNode extends AbstractEndpointNode {

    private final ImportRegistration importRegistration;

    public  ImportRegistrationNode(ImportRegistration ir) {
        this.importRegistration = ir;
    }

    public ImportRegistration getImportRegistration() {
        return importRegistration;
    }

    @Override
    public String toString() {
        return //$NON-NLS-1$
        "ImportRegistrationNode [importRegistration=" + //$NON-NLS-1$
        importRegistration + //$NON-NLS-1$
        "]";
    }
}
