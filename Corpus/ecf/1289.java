/****************************************************************************
 * Copyright (c) 2010 Remain Software, Versant Corp. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Versant Corp. - initial API and implementation
 *    Remain Software - 333137 - Show In menu implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.discovery.ui.views;

import org.eclipse.ecf.discovery.ui.model.provider.DiscoveryEditingDomainProvider;
import org.eclipse.ecf.internal.discovery.ui.statusline.AdapterFactoryStatuslineProvider;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.celleditor.AdapterFactoryTreeEditor;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.action.*;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.*;
import org.eclipse.ui.handlers.CollapseAllHandler;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.*;

public class DiscoveryView extends ViewPart implements IShowInSource, ISelectionListener {

    //$NON-NLS-1$
    public static final String ID = "org.eclipse.ecf.discovery.ui.DiscoveryView";

    private DrillDownAdapter drillDownAdapter;

    private TreeViewer selectionViewer;

    private CollapseAllHandler collapseHandler;

    private IWorkbenchPart selectedPart;

    private ISelection currentSelection;

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    /**
	 * This creates a context menu for the viewer and adds a listener as well
	 * registering the menu for extension. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 */
    protected void createContextMenuFor(StructuredViewer viewer) {
        //$NON-NLS-1$
        MenuManager contextMenu = new MenuManager("#PopUp");
        //$NON-NLS-1$
        contextMenu.add(new Separator("additions"));
        contextMenu.setRemoveAllWhenShown(true);
        Menu menu = contextMenu.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(contextMenu, viewer);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
    public void createPartControl(Composite parent) {
        ComposedAdapterFactory adapterFactory = DiscoveryEditingDomainProvider.eINSTANCE.getAdapterFactory();
        // create the viewer
        setSelectionViewer(new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL));
        getSelectionViewer().setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
        getSelectionViewer().setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));
        getSelectionViewer().setComparator(new ViewerComparator());
        getSelectionViewer().setFilters(getViewerFilters());
        getSelectionViewer().setUseHashlookup(true);
        getSite().setSelectionProvider(getSelectionViewer());
        // populate the viewer with the model if available
        EList resources = DiscoveryEditingDomainProvider.eINSTANCE.getEditingDomain().getResourceSet().getResources();
        if (resources != null) {
            getSelectionViewer().setInput(resources.get(0));
            getSelectionViewer().setSelection(new StructuredSelection(resources.get(0)), true);
        }
        new AdapterFactoryTreeEditor(getSelectionViewer().getTree(), adapterFactory);
        getSelectionViewer().addPostSelectionChangedListener(new AdapterFactoryStatuslineProvider(adapterFactory, getViewSite().getActionBars().getStatusLineManager()));
        drillDownAdapter = new DrillDownAdapter(getSelectionViewer());
        createContextMenuFor(getSelectionViewer());
        hookContextMenu();
        contributeToActionBars();
        // add collapse handler
        IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
        collapseHandler = new CollapseAllHandler(getSelectionViewer());
        handlerService.activateHandler(CollapseAllHandler.COMMAND_ID, collapseHandler);
        // add DND support
        Transfer[] supportedTransfers = { LocalSelectionTransfer.getTransfer() };
        getSelectionViewer().addDragSupport(DND.DROP_DEFAULT | DND.DROP_COPY | DND.DROP_MOVE, supportedTransfers, new DragSourceAdapter() {

            public void dragSetData(DragSourceEvent event) {
                LocalSelectionTransfer.getTransfer().setSelection(getSelectionViewer().getSelection());
            }
        });
        //$NON-NLS-1$
        PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, "org.eclipse.ecf.discovery.ui.ServiceView");
        getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(this);
    }

    /**
	 * @return a new empty ViewerFilter
	 */
    private ViewerFilter[] getViewerFilters() {
        return new ViewerFilter[0];
    }

    void fillContextMenu(IMenuManager manager) {
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
        // Other plug-ins can contribute their actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalPullDown(IMenuManager manager) {
        manager.add(new Separator());
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        drillDownAdapter.addNavigationActions(manager);
    }

    private void hookContextMenu() {
        //$NON-NLS-1$
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {

            /*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse
			 * .jface.action.IMenuManager)
			 */
            public void menuAboutToShow(IMenuManager manager) {
                // TODO https://bugs.eclipse.org/bugs/show_bug.cgi?id=151604
                // add a menu listener
                // that will fire a selection changed event, in order
                // to update the selection in contributed actions
                getSelectionViewer().setSelection(getSelectionViewer().getSelection());
                fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(getSelectionViewer().getControl());
        getSelectionViewer().getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, getSelectionViewer());
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
    public void setFocus() {
        getSelectionViewer().getControl().setFocus();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
    public void dispose() {
        super.dispose();
        if (collapseHandler != null) {
            collapseHandler.dispose();
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.IShowInSource#getShowInContext()
	 */
    public ShowInContext getShowInContext() {
        return new ShowInContext(selectedPart, currentSelection);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.
	 * IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        this.selectedPart = part;
        this.currentSelection = selection;
    }

    public void setSelectionViewer(TreeViewer selectionViewer) {
        this.selectionViewer = selectionViewer;
    }

    public TreeViewer getSelectionViewer() {
        return selectionViewer;
    }
}
