/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteserviceadmin.ui.rsa;

import java.util.List;
import org.eclipse.ecf.internal.remoteservices.ui.DiscoveryComponent;
import org.eclipse.ecf.internal.remoteservices.ui.Messages;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ExportReference;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ExportRegistration;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportReference;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportRegistration;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.AbstractRSAContentProvider;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.AbstractRSANode;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.AbstractRegistrationNode;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.EndpointDescriptionRSANode;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.ExportRegistrationNode;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.ExportedServicesRootNode;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.ImportRegistrationNode;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.ImportedEndpointsRootNode;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.RSAContentProvider;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.ServiceIdNode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.IViewSite;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;

/**
 * @since 3.3
 */
public class RemoteServiceAdminView extends AbstractRemoteServiceAdminView {

    //$NON-NLS-1$
    public static final String ID_VIEW = "org.eclipse.ecf.remoteserviceadmin.ui.views.RSAView";

    private Action closeExportAction;

    private Action closeImportAction;

    public  RemoteServiceAdminView() {
    }

    @Override
    public void dispose() {
        super.dispose();
        DiscoveryComponent discovery = DiscoveryComponent.getDefault();
        if (discovery != null) {
            discovery.setRSAView(null);
            discovery = null;
        }
    }

    @Override
    protected void updateModel() {
        updateModel(0);
    }

    @Override
    protected AbstractRSAContentProvider createContentProvider(IViewSite viewSite) {
        return new RSAContentProvider(viewSite);
    }

    @Override
    protected void setupListeners() {
        DiscoveryComponent.getDefault().setRSAView(this);
    }

    @Override
    protected void fillContextMenu(IMenuManager manager) {
        ITreeSelection selection = (ITreeSelection) viewer.getSelection();
        if (selection != null) {
            Object e = selection.getFirstElement();
            if (e instanceof ImportRegistrationNode) {
                manager.add(closeImportAction);
            } else if (e instanceof ExportRegistrationNode) {
                manager.add(closeExportAction);
            }
        }
    }

    @Override
    protected void makeActions() {
        RemoteServiceAdmin rsa = getLocalRSA();
        closeExportAction = createCloseAction();
        closeExportAction.setText(Messages.RemoteServiceAdminView_0);
        closeExportAction.setEnabled(rsa != null);
        closeImportAction = createCloseAction();
        closeImportAction.setText(Messages.RemoteServiceAdminView_1);
        closeImportAction.setEnabled(rsa != null);
    }

    private void updateExports(ExportedServicesRootNode exportedRoot) {
        RemoteServiceAdmin rsa = getLocalRSA();
        if (rsa != null && exportedRoot != null) {
            exportedRoot.clearChildren();
            List<ExportRegistration> exportRegistrations = rsa.getExportedRegistrations();
            for (ExportRegistration er : exportRegistrations) {
                ExportRegistrationNode exportRegistrationNode = new ExportRegistrationNode(er);
                ExportReference eRef = (ExportReference) er.getExportReference();
                if (eRef != null) {
                    exportRegistrationNode.addChild(new ServiceIdNode(eRef.getExportedService(), Messages.RSAView_SERVICE_ID_LABEL));
                    EndpointDescription ed = (EndpointDescription) eRef.getExportedEndpoint();
                    if (ed != null)
                        exportRegistrationNode.addChild(new EndpointDescriptionRSANode(ed));
                }
                exportedRoot.addChild(exportRegistrationNode);
            }
        }
    }

    private void updateImports(ImportedEndpointsRootNode importedRoot) {
        RemoteServiceAdmin rsa = getLocalRSA();
        if (rsa != null && importedRoot != null) {
            importedRoot.clearChildren();
            List<ImportRegistration> importRegistrations = rsa.getImportedRegistrations();
            for (ImportRegistration ir : importRegistrations) {
                ImportRegistrationNode importRegistrationNode = new ImportRegistrationNode(ir);
                ImportReference iRef = (ImportReference) ir.getImportReference();
                if (iRef != null) {
                    importRegistrationNode.addChild(new ServiceIdNode(iRef.getImportedService(), Messages.RSAView_PROXY_SERVICE_ID_LABEL));
                    EndpointDescription ed = (EndpointDescription) iRef.getImportedEndpoint();
                    if (ed != null)
                        importRegistrationNode.addChild(new EndpointDescriptionRSANode(ed, ir));
                }
                importedRoot.addChild(importRegistrationNode);
            }
        }
    }

    private ExportedServicesRootNode getExportedServicesRoot() {
        return ((RSAContentProvider) contentProvider).getExportedServicesRoot();
    }

    private ImportedEndpointsRootNode getImportedServicesRoot() {
        return ((RSAContentProvider) contentProvider).getImportedEndpointsRoot();
    }

    private void updateExports() {
        updateExports(getExportedServicesRoot());
    }

    private void updateImports() {
        updateImports(getImportedServicesRoot());
    }

    private void updateModel(final int type) {
        if (viewer == null)
            return;
        viewer.getControl().getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                switch(type) {
                    // both
                    case 0:
                        updateExports();
                        updateImports();
                        break;
                    // exports
                    case 1:
                        updateExports();
                        break;
                    // imports
                    case 2:
                        updateImports();
                        break;
                }
                viewer.setExpandedState(getExportedServicesRoot(), true);
                viewer.setExpandedState(getImportedServicesRoot(), true);
                viewer.refresh();
            }
        });
    }

    private Action createCloseAction() {
        return new Action() {

            public void run() {
                AbstractRegistrationNode n = getSelectedRegistrationNode();
                if (n != null)
                    n.close();
            }
        };
    }

    private AbstractRegistrationNode getSelectedRegistrationNode() {
        AbstractRSANode aen = getSelectedNode();
        return (aen instanceof AbstractRegistrationNode) ? (AbstractRegistrationNode) aen : null;
    }

    public void handleRSAEvent(final RemoteServiceAdminEvent event) {
        if (viewer == null)
            return;
        viewer.getControl().getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                RemoteServiceAdmin rsa = getLocalRSA();
                if (rsa != null) {
                    switch(event.getType()) {
                        case RemoteServiceAdminEvent.EXPORT_REGISTRATION:
                        case RemoteServiceAdminEvent.EXPORT_UNREGISTRATION:
                        case RemoteServiceAdminEvent.EXPORT_ERROR:
                        case RemoteServiceAdminEvent.EXPORT_UPDATE:
                        case RemoteServiceAdminEvent.EXPORT_WARNING:
                            updateModel(1);
                            break;
                        case RemoteServiceAdminEvent.IMPORT_REGISTRATION:
                        case RemoteServiceAdminEvent.IMPORT_UNREGISTRATION:
                        case RemoteServiceAdminEvent.IMPORT_ERROR:
                        case RemoteServiceAdminEvent.IMPORT_UPDATE:
                        case RemoteServiceAdminEvent.IMPORT_WARNING:
                            updateModel(2);
                            break;
                    }
                }
            }
        });
    }
}
