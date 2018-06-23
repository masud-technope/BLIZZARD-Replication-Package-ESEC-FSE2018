/******************************************************************************
 * Copyright (c) 2008 Versant Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Chi Jian Suen (Versant Corporation) - initial API and implementation
 ******************************************************************************/
package org.eclipse.team.internal.ecf.ui.wizards;

import java.util.*;
import java.util.Map.Entry;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ecf.core.*;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.roster.*;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.team.internal.ecf.ui.Messages;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

class RemotePeerSynchronizeWizardPage extends WizardPage {

    private CheckboxTreeViewer resourcesViewer;

    private TreeViewer peersViewer;

     RemotePeerSynchronizeWizardPage() {
        super(RemotePeerSynchronizeWizardPage.class.getName());
        setTitle(Messages.RemotePeerSynchronizeWizardPage_Title);
        setDescription(Messages.RemotePeerSynchronizeWizardPage_Description);
    }

    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new FillLayout(SWT.HORIZONTAL));
        createResourcesViewer(composite);
        createPeersViewer(composite);
        attachListeners();
        Dialog.applyDialogFont(composite);
        setControl(composite);
    }

    private void createResourcesViewer(Composite composite) {
        resourcesViewer = new ContainerCheckedTreeViewer(composite);
        resourcesViewer.setContentProvider(new WorkbenchContentProvider() {

            public Object[] getChildren(Object element) {
                if (element instanceof org.eclipse.core.resources.IContainer) {
                    try {
                        IResource[] members = ((org.eclipse.core.resources.IContainer) element).members();
                        List nonDerivedMembers = new ArrayList();
                        for (int i = 0; i < members.length; i++) {
                            if (!members[i].isDerived()) {
                                nonDerivedMembers.add(members[i]);
                            }
                        }
                        return nonDerivedMembers.toArray();
                    } catch (CoreException e) {
                        return new IResource[0];
                    }
                }
                return new IResource[0];
            }

            public boolean hasChildren(Object element) {
                if (element instanceof org.eclipse.core.resources.IContainer) {
                    try {
                        IResource[] members = ((org.eclipse.core.resources.IContainer) element).members();
                        for (int i = 0; i < members.length; i++) {
                            if (!members[i].isDerived()) {
                                return true;
                            }
                        }
                        return false;
                    } catch (CoreException e) {
                        return false;
                    }
                }
                return false;
            }
        });
        resourcesViewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
        resourcesViewer.setInput(ResourcesPlugin.getWorkspace().getRoot());
    }

    private void createPeersViewer(Composite composite) {
        peersViewer = new TreeViewer(composite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        peersViewer.setContentProvider(new ITreeContentProvider() {

            public Object[] getChildren(Object parentElement) {
                if (parentElement instanceof IRosterGroup) {
                    return ((IRosterGroup) parentElement).getEntries().toArray();
                } else if (parentElement instanceof IRoster) {
                    return ((IRoster) parentElement).getItems().toArray();
                } else {
                    return new Object[0];
                }
            }

            public Object getParent(Object element) {
                return ((IRosterItem) element).getParent();
            }

            public boolean hasChildren(Object element) {
                if (element instanceof IRosterGroup) {
                    return !((IRosterGroup) element).getEntries().isEmpty();
                } else if (element instanceof IRoster) {
                    return !((IRoster) element).getItems().isEmpty();
                } else {
                    return false;
                }
            }

            public Object[] getElements(Object inputElement) {
                return (Object[]) inputElement;
            }

            public void dispose() {
            // nothing to do
            }

            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            // nothing to do
            }
        });
        peersViewer.setLabelProvider(new LabelProvider() {

            public String getText(Object element) {
                Entry entry = (Entry) element;
                IRosterItem item = (IRosterItem) entry.getValue();
                return item.getName();
            }
        });
        IContainerManager manager = (IContainerManager) ContainerFactory.getDefault();
        IContainer[] containers = manager.getAllContainers();
        Map presenceContainers = new HashMap();
        for (int i = 0; i < containers.length; i++) {
            IPresenceContainerAdapter adapter = (IPresenceContainerAdapter) containers[i].getAdapter(IPresenceContainerAdapter.class);
            if (adapter != null) {
                IRosterManager rosterManager = adapter.getRosterManager();
                if (rosterManager != null) {
                    presenceContainers.put(containers[i], rosterManager.getRoster());
                }
            }
        }
        peersViewer.setInput(presenceContainers.entrySet().toArray());
    }

    private void attachListeners() {
        resourcesViewer.addCheckStateListener(new ICheckStateListener() {

            public void checkStateChanged(CheckStateChangedEvent event) {
                verify();
            }
        });
        peersViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                verify();
            }
        });
    }

    void verify() {
        if (resourcesViewer.getCheckedElements().length == 0) {
            setPageComplete(false);
            return;
        }
        IStructuredSelection selection = (IStructuredSelection) peersViewer.getSelection();
        if (!(selection.getFirstElement() instanceof IRosterEntry)) {
            setErrorMessage(Messages.RemotePeerSynchronizeWizardPage_NoRemotePeerSelectedError);
            setPageComplete(false);
            return;
        }
        setErrorMessage(null);
        setPageComplete(true);
    }

    private Entry getSelectedEntry() {
        IStructuredSelection selection = (IStructuredSelection) peersViewer.getSelection();
        return (Entry) selection.getFirstElement();
    }

    ID getContainerId() {
        Entry entry = getSelectedEntry();
        IContainer container = (IContainer) entry.getKey();
        return container.getID();
    }

    IRosterEntry getRosterEntry() {
        IStructuredSelection selection = (IStructuredSelection) peersViewer.getSelection();
        return (IRosterEntry) selection.getFirstElement();
    }

    IResource[] getSelectedResources() {
        IStructuredSelection selection = (IStructuredSelection) resourcesViewer.getSelection();
        return (IResource[]) selection.toList().toArray(new IResource[selection.size()]);
    }
}
