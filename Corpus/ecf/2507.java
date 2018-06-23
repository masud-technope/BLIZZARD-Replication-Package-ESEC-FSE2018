/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteserviceadmin.ui.rsa.model;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;
import org.eclipse.ui.model.IWorkbenchAdapter3;

/**
 * @since 3.3
 */
public class RSAAdapterFactory implements IAdapterFactory {

    private ExportedServicesRootNodeWorkbenchAdapter exportedServicesRootAdapter = new ExportedServicesRootNodeWorkbenchAdapter();

    private ImportedEndpointsRootNodeWorkbenchAdapter importedEndpointsRootAdapter = new ImportedEndpointsRootNodeWorkbenchAdapter();

    private ExportRegistrationNodeWorkbenchAdapter exportRegistrationAdapter = new ExportRegistrationNodeWorkbenchAdapter();

    private ImportRegistrationNodeWorkbenchAdapter importRegistrationAdapter = new ImportRegistrationNodeWorkbenchAdapter();

    private NameValuePropertyNodeWorkbenchAdapter nameValuePropertyAdapter = new NameValuePropertyNodeWorkbenchAdapter();

    private EndpointDescriptionRSANodeWorkbenchAdapter edAdapter = new EndpointDescriptionRSANodeWorkbenchAdapter();

    private ExceptionNodeWorkbenchAdapter enAdapter = new ExceptionNodeWorkbenchAdapter();

    @Override
    public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
        if (adapterType.isInstance(adaptableObject)) {
            return adaptableObject;
        }
        if (adapterType == IWorkbenchAdapter.class || adapterType == IWorkbenchAdapter2.class || adapterType == IWorkbenchAdapter3.class) {
            return getWorkbenchElement(adaptableObject);
        }
        return null;
    }

    protected Object getWorkbenchElement(Object adaptableObject) {
        if (adaptableObject instanceof ExceptionNode || adaptableObject instanceof StackTraceElementNode)
            return enAdapter;
        if (adaptableObject instanceof ExportedServicesRootNode)
            return exportedServicesRootAdapter;
        if (adaptableObject instanceof ImportedEndpointsRootNode)
            return importedEndpointsRootAdapter;
        if (adaptableObject instanceof ExportRegistrationNode)
            return exportRegistrationAdapter;
        if (adaptableObject instanceof ImportRegistrationNode)
            return importRegistrationAdapter;
        if (adaptableObject instanceof NameValuePropertyNode)
            return nameValuePropertyAdapter;
        if (adaptableObject instanceof EndpointDescriptionRSANode)
            return edAdapter;
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class[] getAdapterList() {
        return new Class[] { IWorkbenchAdapter.class, IWorkbenchAdapter2.class, IWorkbenchAdapter3.class };
    }
}
