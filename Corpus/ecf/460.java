/*******************************************************************************
 * Copyright (c) 2009 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Chi Jian Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.sync.resources.views;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ecf.internal.sync.resources.core.BatchModelChange;
import org.eclipse.ecf.internal.sync.resources.core.IView;
import org.eclipse.ecf.internal.sync.resources.core.ResourceChangeMessage;
import org.eclipse.ecf.internal.sync.resources.core.SyncResourcesCore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.team.core.synchronize.SyncInfo;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;

public class RemoteResourcesView extends ViewPart implements IView {

    public static final String ID = "org.eclipse.ecf.sync.resources.core.views.RemoteResourcesView";

    private TreeViewer viewer;

    private ILabelProvider labelProvider;

    public void createPartControl(Composite parent) {
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.setContentProvider(new ViewContentProvider());
        viewer.setComparator(new ViewerComparator() {

            public int compare(Viewer viewer, Object e1, Object e2) {
                if (e1 instanceof BatchModelChange) {
                    BatchModelChange c1 = (BatchModelChange) e1;
                    BatchModelChange c2 = (BatchModelChange) e2;
                    // newest goes last
                    return (int) (c2.getTime() - c1.getTime());
                }
                // no sorting for resource changes
                return 0;
            }
        });
        viewer.getTree().setHeaderVisible(true);
        viewer.getTree().setLinesVisible(true);
        labelProvider = WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider();
        //$NON-NLS-1$
        final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        final CompareConfiguration config = new CompareConfiguration();
        TreeViewerColumn resourceColumn = new TreeViewerColumn(viewer, SWT.LEAD);
        resourceColumn.getColumn().setText("Changes");
        resourceColumn.getColumn().setWidth(100);
        resourceColumn.setLabelProvider(new ColumnLabelProvider() {

            public String getText(Object element) {
                if (element instanceof BatchModelChange) {
                    StringBuffer buffer = new StringBuffer();
                    BatchModelChange batchChange = (BatchModelChange) element;
                    buffer.append(batchChange.isOutgoing() ? "Outgoing" : "Incoming");
                    buffer.append(" (");
                    buffer.append(formatter.format(new Date(batchChange.getTime())));
                    buffer.append(')');
                    return buffer.toString();
                } else {
                    return new Path(((ResourceChangeMessage) element).getPath()).lastSegment();
                }
            }

            public Image getImage(Object element) {
                if (element instanceof BatchModelChange) {
                    Image image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
                    int kind = 0;
                    if (((BatchModelChange) element).isOutgoing()) {
                        kind = (kind & ~SyncInfo.OUTGOING) | SyncInfo.INCOMING | SyncInfo.CHANGE;
                    } else {
                        kind = (kind & ~SyncInfo.OUTGOING) | SyncInfo.OUTGOING | SyncInfo.CHANGE;
                    }
                    return config.getImage(image, kind);
                } else {
                    ResourceChangeMessage message = (ResourceChangeMessage) element;
                    int type = message.getType();
                    IPath path = new Path(message.getPath());
                    Image image = null;
                    if (type == IResource.FILE) {
                        image = labelProvider.getImage(ResourcesPlugin.getWorkspace().getRoot().getFile(path));
                    } else {
                        image = labelProvider.getImage(ResourcesPlugin.getWorkspace().getRoot().getFolder(path));
                    }
                    if (message.isConflicted()) {
                        return config.getImage(image, SyncInfo.CHANGE | SyncInfo.CONFLICTING);
                    }
                    int imageKind = 0;
                    switch(message.getKind()) {
                        case IResourceDelta.ADDED:
                            imageKind = (imageKind & ~SyncInfo.CHANGE) | SyncInfo.DELETION;
                            break;
                        case IResourceDelta.CHANGED:
                            imageKind = (imageKind & ~SyncInfo.CHANGE) | SyncInfo.CHANGE;
                            break;
                        case IResourceDelta.REMOVED:
                            imageKind = (imageKind & ~SyncInfo.CHANGE) | SyncInfo.ADDITION;
                            break;
                        default:
                            return null;
                    }
                    return config.getImage(image, imageKind);
                }
            }
        });
        TreeViewerColumn pathColumn = new TreeViewerColumn(viewer, SWT.LEAD);
        pathColumn.getColumn().setText("Path");
        pathColumn.getColumn().setWidth(200);
        pathColumn.setLabelProvider(new ColumnLabelProvider() {

            public String getText(Object element) {
                if (element instanceof BatchModelChange) {
                    return null;
                } else {
                    return ((ResourceChangeMessage) element).getPath();
                }
            }
        });
        TreeViewerColumn typeColumn = new TreeViewerColumn(viewer, SWT.LEAD);
        typeColumn.getColumn().setText("Type");
        typeColumn.getColumn().setWidth(75);
        typeColumn.setLabelProvider(new ColumnLabelProvider() {

            public String getText(Object element) {
                if (element instanceof BatchModelChange) {
                    return null;
                } else {
                    switch(((ResourceChangeMessage) element).getKind()) {
                        case IResourceDelta.ADDED:
                            return "Added";
                        case IResourceDelta.CHANGED:
                            return "Changed";
                        case IResourceDelta.REMOVED:
                            return "Removed";
                        default:
                            return null;
                    }
                }
            }
        });
        TreeViewerColumn resolutionColumn = new TreeViewerColumn(viewer, SWT.LEAD);
        resolutionColumn.getColumn().setText("Resolution");
        resolutionColumn.getColumn().setWidth(100);
        resolutionColumn.setLabelProvider(new ColumnLabelProvider() {

            public String getText(Object element) {
                if (element instanceof BatchModelChange) {
                    return null;
                } else {
                    return ((ResourceChangeMessage) element).isIgnored() ? "Ignored" : "Committed";
                }
            }
        });
        SyncResourcesCore.setView(this);
    }

    public void dispose() {
        if (labelProvider != null) {
            labelProvider.dispose();
            labelProvider = null;
        }
        SyncResourcesCore.setView(null);
        super.dispose();
    }

    public void setInput(Object input) {
        viewer.setInput(input);
        viewer.expandToLevel(2);
    }

    public void add(final Object object) {
        final Control control = viewer.getControl();
        if (!control.isDisposed()) {
            control.getDisplay().asyncExec(new Runnable() {

                public void run() {
                    if (!control.isDisposed()) {
                        viewer.add(viewer.getInput(), object);
                        viewer.expandToLevel(object, 2);
                    }
                }
            });
        }
    }

    public void remove(final Object object) {
        final Control control = viewer.getControl();
        if (!control.isDisposed()) {
            control.getDisplay().asyncExec(new Runnable() {

                public void run() {
                    if (!control.isDisposed()) {
                        viewer.remove(object);
                    }
                }
            });
        }
    }

    public void setFocus() {
        viewer.getControl().setFocus();
    }

    static class ViewContentProvider extends ArrayContentProvider implements ITreeContentProvider {

        public Object[] getChildren(Object parentElement) {
            return ((BatchModelChange) parentElement).getMessages();
        }

        public Object getParent(Object element) {
            return null;
        }

        public boolean hasChildren(Object element) {
            return element instanceof BatchModelChange;
        }
    }
}
