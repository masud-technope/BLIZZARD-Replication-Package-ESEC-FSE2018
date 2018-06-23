/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.ui.perspectives;

import org.eclipse.ui.*;

public class CommunicationPerspective implements IPerspectiveFactory {

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
        layout.addShowViewShortcut(IPageLayout.ID_BOOKMARKS);
        layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
        layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
        layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
        layout.addShowViewShortcut(IPageLayout.ID_PROGRESS_VIEW);
        layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
    }

    private void defineLayout(IPageLayout layout) {
        // Editors are placed for free.
        String editorArea = layout.getEditorArea();
        // Top left.
        //$NON-NLS-1$
        IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.26f, editorArea);
        // to be replaced by IPageLayout.ID_PROJECT_EXPLORER
        //$NON-NLS-1$
        topLeft.addView("org.eclipse.ui.navigator.ProjectExplorer");
        // Bottom left.
        IFolderLayout bottomLeft = //$NON-NLS-1$
        layout.createFolder(//$NON-NLS-1$
        "bottomLeft", //$NON-NLS-1$
        IPageLayout.BOTTOM, //$NON-NLS-1$
        0.50f, //$NON-NLS-1$
        "topLeft");
        bottomLeft.addView(IPageLayout.ID_OUTLINE);
        // Bottom right.
        IFolderLayout bottomRight = //$NON-NLS-1$
        layout.createFolder(//$NON-NLS-1$
        "bottomRight", IPageLayout.BOTTOM, 0.66f, editorArea);
        bottomRight.addView(IPageLayout.ID_PROBLEM_VIEW);
        bottomRight.addView(IPageLayout.ID_TASK_LIST);
    }
}
