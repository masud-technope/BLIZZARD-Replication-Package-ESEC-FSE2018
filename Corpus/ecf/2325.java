/*******************************************************************************
 * Copyright (c) 2015 Remain Software and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Willem Sietse Jongman - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.remoteservices.ui;

import java.util.List;
import org.eclipse.ecf.remoteserviceadmin.ui.endpoint.EndpointDiscoveryView;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.RemoteServiceAdminView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class RemoteServicePerspective implements IPerspectiveFactory {

    //$NON-NLS-1$
    public static final String ID = "org.eclipse.ecf.remoteservices.ui.RemoteServicePerspective";

    public void createInitialLayout(IPageLayout layout) {
        defineActions(layout);
        defineLayout(layout);
    }

    private void defineActions(IPageLayout layout) {
        // Add "new wizards".
        //$NON-NLS-1$
        layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
        //$NON-NLS-1$
        layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");
        // Add "show views".
        // to be replaced by IPageLayout.ID_PROJECT_EXPLORER
        //$NON-NLS-1$
        layout.addShowViewShortcut("org.eclipse.ui.navigator.ProjectExplorer");
        layout.addShowViewShortcut(EndpointDiscoveryView.ID_VIEW);
        layout.addShowViewShortcut(RemoteServiceAdminView.ID_VIEW);
        layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
        layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
        layout.addShowViewShortcut(IPageLayout.ID_PROGRESS_VIEW);
    }

    private void defineLayout(IPageLayout layout) {
        // Editors are placed for free.
        String editorArea = layout.getEditorArea();
        // Folder for views at bottom
        IFolderLayout bottom = //$NON-NLS-1$
        layout.createFolder(//$NON-NLS-1$
        "bottom", IPageLayout.BOTTOM, 0.60f, editorArea);
        // Folder for views at left bottom
        //$NON-NLS-1$ //$NON-NLS-2$
        IFolderLayout leftBottom = layout.createFolder("leftBottom", IPageLayout.LEFT, 0.30f, "bottom");
        // The ECF Endpoint Discovery view
        leftBottom.addView(EndpointDiscoveryView.ID_VIEW);
        // The ECF Service Discovery view
        //$NON-NLS-1$
        leftBottom.addView("org.eclipse.ecf.discovery.ui.DiscoveryView");
        // Create folder for right bottom
        IFolderLayout rightBottom = //$NON-NLS-1$
        layout.createFolder(//$NON-NLS-1$
        "rightBottom", //$NON-NLS-1$
        IPageLayout.RIGHT, //$NON-NLS-1$
        0.5f, //$NON-NLS-1$
        "bottom");
        List<ServicesViewExtension> sves = Activator.getDefault().getLocalServicesViewExtensions();
        //$NON-NLS-1$
        String viewId = "org.eclipse.pde.runtime.RegistryBrowser";
        if (sves.size() > 0) {
            viewId = sves.get(0).getViewId();
            Activator.getDefault().setLocalServicesViewId(viewId);
        }
        rightBottom.addView(viewId);
        // Add properties view in the middle
        bottom.addView(IPageLayout.ID_PROP_SHEET);
        // Create folder for left side above Endpoint Discover/Service Discovery
        // views
        //$NON-NLS-1$
        IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.30f, editorArea);
        left.addView(RemoteServiceAdminView.ID_VIEW);
        // Add error log view
        left.addView("org.eclipse.pde.runtime.LogView");
        // Top right.
        //$NON-NLS-1$
        IFolderLayout topRight = layout.createFolder("topRight", IPageLayout.RIGHT, 0.70f, editorArea);
        // Add Project Explorer view
        //$NON-NLS-1$
        topRight.addView("org.eclipse.ui.navigator.ProjectExplorer");
        // Add Outline view
        topRight.addView(IPageLayout.ID_OUTLINE);
    }
}
