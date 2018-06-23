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
package org.eclipse.ecf.internal.sync.ui.resources;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.docshare2.DocShare;
import org.eclipse.ecf.internal.sync.resources.core.ResourcesShare;
import org.eclipse.ecf.internal.sync.resources.core.SyncResourcesCore;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.framework.BundleContext;

public class SyncResourcesUI extends AbstractUIPlugin {

    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf.sync.ui.resources";

    private static SyncResourcesUI instance;

    private IWindowListener windowListener = new IWindowListener() {

        public void windowOpened(IWorkbenchWindow window) {
            window.addPageListener(pageListener);
        }

        public void windowDeactivated(IWorkbenchWindow window) {
        // nothing to do
        }

        public void windowClosed(IWorkbenchWindow window) {
            window.removePageListener(pageListener);
        }

        public void windowActivated(IWorkbenchWindow window) {
        // nothing to do
        }
    };

    private IPageListener pageListener = new IPageListener() {

        public void pageOpened(IWorkbenchPage page) {
            page.addPartListener(partListener);
        }

        public void pageClosed(IWorkbenchPage page) {
            page.removePartListener(partListener);
        }

        public void pageActivated(IWorkbenchPage page) {
        // nothing to do
        }
    };

    private IPartListener partListener = new IPartListener() {

        public void partOpened(IWorkbenchPart part) {
            ITextEditor editor = (ITextEditor) part.getAdapter(ITextEditor.class);
            if (editor != null) {
                IFile file = (IFile) editor.getEditorInput().getAdapter(IFile.class);
                if (file != null) {
                    startSharing(editor, file);
                }
            }
        }

        public void partDeactivated(IWorkbenchPart part) {
        // nothing to do
        }

        public void partClosed(IWorkbenchPart part) {
            ITextEditor editor = (ITextEditor) part.getAdapter(ITextEditor.class);
            if (editor != null) {
                IFile file = (IFile) editor.getEditorInput().getAdapter(IFile.class);
                if (file != null) {
                    stopSharing(editor, file);
                }
            }
        }

        public void partBroughtToTop(IWorkbenchPart part) {
        // nothing to do
        }

        public void partActivated(IWorkbenchPart part) {
        // nothing to do
        }
    };

    private Map sharedFiles = new HashMap();

    private Map sharedEditors = new HashMap();

    private boolean share(IFile file) {
        IPath path = file.getFullPath();
        Integer integer = (Integer) sharedFiles.get(path);
        if (integer == null) {
            sharedFiles.put(path, new Integer(1));
            return true;
        }
        sharedFiles.put(path, new Integer(integer.intValue() + 1));
        return false;
    }

    private boolean unshare(IFile file) {
        IPath path = file.getFullPath();
        Integer integer = (Integer) sharedFiles.remove(path);
        if (integer == null) {
            // if this file isn't being shared, will be null, return false
            return false;
        }
        if (integer.intValue() == 1) {
            return true;
        }
        sharedFiles.put(path, new Integer(integer.intValue() - 1));
        return false;
    }

    private void startSharing(ITextEditor editor, IFile file) {
        String projectName = file.getProject().getName();
        for (Iterator it = SyncResourcesCore.getResourceShares().iterator(); it.hasNext(); ) {
            ResourcesShare share = (ResourcesShare) it.next();
            if (share.isSharing(projectName) && share(file)) {
                DocShare docShare = getDocShare(share.getContainerID());
                try {
                    IAnnotationModel annotationModel = editor.getDocumentProvider().getAnnotationModel(editor.getEditorInput());
                    docShare.startSharing(share.getLocalID(), share.getReceiverID(), file.getFullPath().toString(), annotationModel);
                    ISelectionProvider provider = editor.getSelectionProvider();
                    if (provider instanceof IPostSelectionProvider) {
                        ISelectionChangedListener listener = new SelectionChangedListener(share.getReceiverID(), file.getFullPath().toString(), docShare);
                        ((IPostSelectionProvider) provider).addPostSelectionChangedListener(listener);
                        sharedEditors.put(editor, listener);
                    }
                } catch (ECFException e) {
                    IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, "Could not send initiation request to " + share.getReceiverID(), e);
                    log(status);
                    StatusManager.getManager().handle(status, StatusManager.SHOW);
                } catch (CoreException e) {
                    IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, "Could not connect to the file buffer of " + file.getFullPath(), e);
                    log(status);
                    StatusManager.getManager().handle(status, StatusManager.SHOW);
                }
            }
        }
    }

    private void stopSharing(ITextEditor editor, IFile file) {
        String projectName = file.getProject().getName();
        for (Iterator it = SyncResourcesCore.getResourceShares().iterator(); it.hasNext(); ) {
            ResourcesShare share = (ResourcesShare) it.next();
            if (share.isSharing(projectName) && unshare(file)) {
                DocShare docShare = getDocShare(share.getContainerID());
                stopSharing(docShare, share.getReceiverID(), editor, file);
            }
        }
    }

    private void stopSharing(DocShare share, ID id, ITextEditor editor, IFile file) {
        try {
            share.stopSharing(id, file.getFullPath().toString());
        } catch (ECFException e) {
            log(new Status(IStatus.ERROR, PLUGIN_ID, "Could not send stop message to " + id, e));
        }
        ISelectionChangedListener listener = (ISelectionChangedListener) sharedEditors.remove(editor);
        if (listener != null) {
            ISelectionProvider provider = editor.getSelectionProvider();
            if (provider instanceof IPostSelectionProvider) {
                ((IPostSelectionProvider) provider).removePostSelectionChangedListener(listener);
            }
        }
    }

    public  SyncResourcesUI() {
        instance = this;
    }

    public void start(BundleContext context) throws Exception {
        super.start(context);
        getWorkbench().addWindowListener(windowListener);
        IWorkbenchWindow[] workbenchWindows = getWorkbench().getWorkbenchWindows();
        for (int i = 0; i < workbenchWindows.length; i++) {
            workbenchWindows[i].addPageListener(pageListener);
            IWorkbenchPage[] pages = workbenchWindows[i].getPages();
            for (int j = 0; j < pages.length; j++) {
                pages[j].addPartListener(partListener);
            }
        }
    }

    public static SyncResourcesUI getDefault() {
        return instance;
    }

    public Display getDisplay() {
        return getWorkbench().getDisplay();
    }

    public static void log(IStatus status) {
        instance.getLog().log(status);
    }

    public void stop(BundleContext context) throws Exception {
        getWorkbench().removeWindowListener(windowListener);
        IWorkbenchWindow[] workbenchWindows = getWorkbench().getWorkbenchWindows();
        for (int i = 0; i < workbenchWindows.length; i++) {
            workbenchWindows[i].removePageListener(pageListener);
            IWorkbenchPage[] pages = workbenchWindows[i].getPages();
            for (int j = 0; j < pages.length; j++) {
                pages[j].removePartListener(partListener);
            }
        }
        super.stop(context);
    }

    private static Map docshareChannels = new HashMap();

    public static DocShare getDocShare(ID containerID) {
        return (DocShare) docshareChannels.get(containerID);
    }

    public static DocShare addDocShare(ID containerID, IChannelContainerAdapter channelAdapter) throws ECFException {
        DocShare docShare = (DocShare) docshareChannels.get(containerID);
        if (docShare == null) {
            return (DocShare) docshareChannels.put(containerID, new WorkbenchAwareDocShare(channelAdapter));
        }
        return docShare;
    }

    public static DocShare removeDocShare(ID containerID) {
        return (DocShare) docshareChannels.remove(containerID);
    }

    public static ResourcesShare getResourceShare(ID containerID) {
        return SyncResourcesCore.getResourcesShare(containerID);
    }

    public static ResourcesShare addResourceShare(ID containerID, IChannelContainerAdapter channelAdapter) throws ECFException {
        ResourcesShare resourcesShare = SyncResourcesCore.getResourcesShare(containerID);
        if (resourcesShare == null) {
            resourcesShare = new WorkbenchAwareResourcesShare(containerID, channelAdapter);
            SyncResourcesCore.addResourcesShare(containerID, resourcesShare);
        }
        return resourcesShare;
    }

    public static ResourcesShare removeResourceShare(ID containerID) {
        return SyncResourcesCore.removeResourcesShare(containerID);
    }

    private class SelectionChangedListener implements ISelectionChangedListener {

        private ID targetID;

        private String path;

        private DocShare share;

        public  SelectionChangedListener(ID targetID, String path, DocShare share) {
            this.targetID = targetID;
            this.path = path;
            this.share = share;
        }

        public void selectionChanged(SelectionChangedEvent e) {
            ISelection selection = e.getSelection();
            if (selection instanceof ITextSelection) {
                ITextSelection textSelection = (ITextSelection) selection;
                try {
                    share.sendSelection(targetID, path, textSelection.getOffset(), textSelection.getLength());
                } catch (ECFException exception) {
                    log(new Status(IStatus.ERROR, PLUGIN_ID, "Could not send selection message to " + targetID, exception));
                }
            }
        }
    }
}
