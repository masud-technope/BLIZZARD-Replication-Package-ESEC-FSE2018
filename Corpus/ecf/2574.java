/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Red Hat - update to Mylyn 3.0 API
 *******************************************************************************/
package org.eclipse.ecf.internal.mylyn.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.datashare.*;
import org.eclipse.ecf.datashare.events.IChannelEvent;
import org.eclipse.ecf.datashare.events.IChannelMessageEvent;
import org.eclipse.ecf.presence.service.IPresenceService;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.*;

public class Activator extends AbstractUIPlugin implements IChannelListener, ServiceListener {

    //$NON-NLS-1$
    static final String PLUGIN_ID = "org.eclipse.ecf.mylyn.ui";

    private static Activator plugin;

    private BundleContext context;

    public void start(final BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        this.context = context;
        context.addServiceListener(this);
    }

    protected void initializeImageRegistry(ImageRegistry reg) {
        reg.put("IMG_SHARED_TASK", imageDescriptorFromPlugin(PLUGIN_ID, "icons/full/etool16/shared_task.gif").createImage());
    }

    public void stop(BundleContext context) throws Exception {
        plugin = null;
        context.removeServiceListener(this);
        super.stop(context);
    }

    private void registerChannel(IChannelContainerAdapter channelAdapter) {
        try {
            ID channelID = channelAdapter.getChannelNamespace().createInstance(new Object[] { Activator.PLUGIN_ID });
            IChannel channel = channelAdapter.getChannel(channelID);
            if (channel == null) {
                channel = channelAdapter.createChannel(channelID, this, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unregisterChannel(IChannelContainerAdapter channelAdapter) {
        try {
            ID channelID = channelAdapter.getChannelNamespace().createInstance(new Object[] { Activator.PLUGIN_ID });
            channelAdapter.removeChannel(channelID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void handleChannelEvent(IChannelEvent e) {
        if (e instanceof IChannelMessageEvent) {
            IChannelMessageEvent msgEvent = (IChannelMessageEvent) e;
            byte[] data = msgEvent.getData();
            File file = new File(getStateLocation().toFile(), "incoming.xml.zip");
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data);
                List tasks = TasksUiPlugin.getTaskListManager().getTaskListWriter().readTasks(file);
                final ITask task = (ITask) tasks.get(0);
                Set repositories = TasksUiPlugin.getTaskListManager().getTaskListWriter().readRepositories(file);
                TasksUiPlugin.getRepositoryManager().insertRepositories(repositories, TasksUiPlugin.getDefault().getRepositoriesFilePath());
                IInteractionContext context = ContextCore.getContextStore().importContext(task.getHandleIdentifier(), file);
                CompoundContextActivationContributionItem.enqueue(task, context);
                IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
                Shell aShell = null;
                for (int i = 0; i < windows.length; i++) {
                    aShell = windows[i].getShell();
                    if (aShell != null) {
                        break;
                    }
                }
                if (aShell == null) {
                    return;
                }
                final Shell shell = aShell;
                UIJob job = new UIJob("Notify of incoming shared task") {

                    public IStatus runInUIThread(IProgressMonitor monitor) {
                        final IncomingSharedTaskNotificationPopup popup = new IncomingSharedTaskNotificationPopup(shell);
                        popup.setTask(task);
                        popup.open();
                        new //$NON-NLS-1$
                        UIJob(//$NON-NLS-1$
                        shell.getDisplay(), //$NON-NLS-1$
                        "Close Popup Job") {

                            public IStatus runInUIThread(IProgressMonitor monitor) {
                                Shell shell = popup.getShell();
                                if (shell != null && !shell.isDisposed()) {
                                    popup.close();
                                }
                                monitor.done();
                                return Status.OK_STATUS;
                            }
                        }.schedule(5000);
                        return Status.OK_STATUS;
                    }
                };
                job.schedule();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                file.delete();
            }
        }
    }

    public void serviceChanged(ServiceEvent event) {
        Object service = context.getService(event.getServiceReference());
        if (service instanceof IAdaptable) {
            service = ((IAdaptable) service).getAdapter(IPresenceService.class);
        }
        if (service instanceof IPresenceService) {
            IPresenceService presenceService = (IPresenceService) service;
            IChannelContainerAdapter channelAdapter = (IChannelContainerAdapter) presenceService.getAdapter(IChannelContainerAdapter.class);
            if (channelAdapter != null) {
                switch(event.getType()) {
                    case ServiceEvent.REGISTERED:
                        registerChannel(channelAdapter);
                        break;
                    case ServiceEvent.UNREGISTERING:
                        unregisterChannel(channelAdapter);
                        break;
                }
            }
        }
    }

    public static Activator getDefault() {
        return plugin;
    }
}
