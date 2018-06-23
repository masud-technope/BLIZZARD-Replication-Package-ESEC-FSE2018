/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.navigator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.IExtensionStateModel;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.actions.MultiActionGroup;
import org.eclipse.jdt.internal.ui.navigator.IExtensionStateConstants.Values;
import org.eclipse.jdt.internal.ui.packageview.PackagesMessages;

/**
 * Adds view menus to switch between flat and hierarchical layout.
 *
 * @since 3.2
 */
public class CommonLayoutActionGroup extends MultiActionGroup {

    //$NON-NLS-1$
    public static final String LAYOUT_GROUP_NAME = "layout";

    private IExtensionStateModel fStateModel;

    private StructuredViewer fStructuredViewer;

    private boolean fHasContributedToViewMenu = false;

    private IAction fHierarchicalLayout = null;

    private IAction fFlatLayoutAction = null;

    private IAction[] fActions;

    private IMenuManager fLayoutSubMenu;

    private class CommonLayoutAction extends Action {

        private final boolean fIsFlatLayout;

        public  CommonLayoutAction(boolean flat) {
            //$NON-NLS-1$
            super("", AS_RADIO_BUTTON);
            fIsFlatLayout = flat;
            if (fIsFlatLayout) {
                PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.LAYOUT_FLAT_ACTION);
            } else {
                PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.LAYOUT_HIERARCHICAL_ACTION);
            }
        }

        /*
		 * @see org.eclipse.jface.action.IAction#run()
		 */
        @Override
        public void run() {
            if (fStateModel.getBooleanProperty(Values.IS_LAYOUT_FLAT) != fIsFlatLayout) {
                fStateModel.setBooleanProperty(Values.IS_LAYOUT_FLAT, fIsFlatLayout);
                fStructuredViewer.getControl().setRedraw(false);
                try {
                    fStructuredViewer.refresh();
                } finally {
                    fStructuredViewer.getControl().setRedraw(true);
                }
            }
        }
    }

    public  CommonLayoutActionGroup(StructuredViewer structuredViewer, IExtensionStateModel stateModel) {
        super();
        fStateModel = stateModel;
        fStructuredViewer = structuredViewer;
    }

    @Override
    public void fillActionBars(IActionBars actionBars) {
        if (!fHasContributedToViewMenu) {
            IMenuManager viewMenu = actionBars.getMenuManager();
            // Create layout sub menu
            if (fLayoutSubMenu == null) {
                fLayoutSubMenu = new MenuManager(PackagesMessages.LayoutActionGroup_label);
                addActions(fLayoutSubMenu);
                viewMenu.insertAfter(IWorkbenchActionConstants.MB_ADDITIONS, new Separator(LAYOUT_GROUP_NAME));
            }
            viewMenu.appendToGroup(LAYOUT_GROUP_NAME, fLayoutSubMenu);
            fHasContributedToViewMenu = true;
        }
    }

    public void unfillActionBars(IActionBars actionBars) {
        if (fHasContributedToViewMenu) {
            // Create layout sub menu
            if (fLayoutSubMenu != null) {
                actionBars.getMenuManager().remove(fLayoutSubMenu);
                fLayoutSubMenu.dispose();
                fLayoutSubMenu = null;
            }
            fHasContributedToViewMenu = false;
        }
    }

    private IAction[] createActions() {
        fFlatLayoutAction = new CommonLayoutAction(true);
        fFlatLayoutAction.setText(PackagesMessages.LayoutActionGroup_flatLayoutAction_label);
        //$NON-NLS-1$
        JavaPluginImages.setLocalImageDescriptors(fFlatLayoutAction, "flatLayout.png");
        fHierarchicalLayout = new CommonLayoutAction(false);
        fHierarchicalLayout.setText(PackagesMessages.LayoutActionGroup_hierarchicalLayoutAction_label);
        //$NON-NLS-1$
        JavaPluginImages.setLocalImageDescriptors(fHierarchicalLayout, "hierarchicalLayout.png");
        return new IAction[] { fFlatLayoutAction, fHierarchicalLayout };
    }

    public void setFlatLayout(boolean flatLayout) {
        if (fActions == null) {
            fActions = createActions();
            // indicates check the flat action
            setActions(fActions, flatLayout ? 0 : 1);
        }
        fHierarchicalLayout.setChecked(!flatLayout);
        fFlatLayoutAction.setChecked(flatLayout);
    }
}
