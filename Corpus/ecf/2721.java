/*******************************************************************************
 * Copyright (c) 2008, 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.sync.resources.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.sync.IModelChange;
import org.eclipse.ecf.sync.IModelChangeMessage;
import org.eclipse.ecf.sync.resources.core.preferences.PreferenceConstants;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.Preferences;

public class SyncResourcesCore extends Plugin implements IResourceChangeListener, IResourceDeltaVisitor {

    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.ecf.sync.resources.core";

    private static final int LIMIT = 25;

    private static final Hashtable channels = new Hashtable();

    private static LinkedList enqueuedChanges = new LinkedList();

    private static SyncResourcesCore instance;

    private Map resourceChanges = new HashMap();

    public static ResourcesShare getResourcesShare(ID containerID) {
        return (ResourcesShare) channels.get(containerID);
    }

    public static void addResourcesShare(ID containerID, ResourcesShare share) {
        channels.put(containerID, share);
    }

    public static ResourcesShare removeResourcesShare(ID containerID) {
        return (ResourcesShare) channels.remove(containerID);
    }

    public static Collection getResourceShares() {
        return Collections.unmodifiableCollection(channels.values());
    }

    /**
	 * Checks and returns whether the specified project is currently being
	 * shared.
	 */
    public static boolean isSharing(String projectName) {
        for (Iterator it = channels.values().iterator(); it.hasNext(); ) {
            ResourcesShare share = (ResourcesShare) it.next();
            if (share.isSharing(projectName)) {
                return true;
            }
        }
        return false;
    }

    private static boolean locked = false;

    /**
	 * Requests that all resource changes be ignored. That is, they should not
	 * be monitored for distribution to remote peers.
	 */
    public static void lock() {
        locked = true;
    }

    /**
	 * Unlocks by requesting that that all resource changes be monitored for
	 * distribution to remote peers.
	 */
    public static void unlock() {
        locked = false;
    }

    public boolean visit(IResourceDelta delta) throws CoreException {
        if (locked) {
            return false;
        }
        IResource resource = delta.getResource();
        int type = resource.getType();
        if (type == IResource.ROOT) {
            return true;
        }
        String projectName = resource.getProject().getName();
        boolean isSharing = isSharing(projectName);
        if (isSharing) {
            if (type == IResource.PROJECT) {
                return true;
            }
        } else {
            // changes, return false
            return false;
        }
        // we are only interested in non-derived resources
        if (!resource.isDerived() && checkDelta(delta)) {
            for (Iterator it = channels.values().iterator(); it.hasNext(); ) {
                ResourcesShare share = (ResourcesShare) it.next();
                if (share.isSharing(projectName)) {
                    IModelChange change = ResourceChangeMessage.createResourceChange(resource, delta.getKind());
                    if (change != null) {
                        List changes = (List) resourceChanges.get(share);
                        if (changes == null) {
                            changes = new ArrayList();
                            resourceChanges.put(share, changes);
                        }
                        changes.add(change);
                    }
                }
            }
        }
        return type == IResource.FOLDER;
    }

    private static boolean checkDelta(IResourceDelta delta) {
        return checkFlags(delta.getFlags()) || checkKind(delta.getKind());
    }

    private static boolean checkKind(int kind) {
        return kind == IResourceDelta.ADDED || kind == IResourceDelta.REMOVED;
    }

    /**
	 * Checks the flags of a how a resource has been modified and returns
	 * whether this change should be propagated to remote peers.
	 * 
	 * @param flags
	 *            the detail flags of a resource delta
	 * @return <code>true</code> if the delta should be propagated,
	 *         <code>false</code> otherwise
	 */
    private static boolean checkFlags(int flags) {
        // infinite loops
        return (flags & IResourceDelta.CONTENT) != 0;
    }

    public void resourceChanged(IResourceChangeEvent event) {
        try {
            event.getDelta().accept(this);
        } catch (CoreException e) {
        } finally {
            try {
                // we're done, at least, "partially", distribute changes
                distributeChanges();
            } finally {
                // clear the cached changes
                resourceChanges.clear();
            }
        }
    }

    private void distributeChanges() {
        for (Iterator it = resourceChanges.entrySet().iterator(); it.hasNext(); ) {
            Entry entry = (Entry) it.next();
            ResourcesShare share = (ResourcesShare) entry.getKey();
            List changes = (List) entry.getValue();
            List messages = new ArrayList();
            for (int i = 0; i < changes.size(); i++) {
                ResourceChangeMessage change = (ResourceChangeMessage) changes.get(i);
                switch(change.getKind()) {
                    case IResourceDelta.ADDED:
                        if (getInt(PreferenceConstants.LOCAL_RESOURCE_ADDITION) == PreferenceConstants.IGNORE_VALUE) {
                            change.setIgnored(true);
                            continue;
                        }
                        break;
                    case IResourceDelta.CHANGED:
                        if (getInt(PreferenceConstants.LOCAL_RESOURCE_CHANGE) == PreferenceConstants.IGNORE_VALUE) {
                            change.setIgnored(true);
                            continue;
                        }
                        break;
                    case IResourceDelta.REMOVED:
                        if (getInt(PreferenceConstants.LOCAL_RESOURCE_DELETION) == PreferenceConstants.IGNORE_VALUE) {
                            change.setIgnored(true);
                            continue;
                        }
                        break;
                }
                IModelChangeMessage[] changeMessages = ResourcesSynchronizationStrategy.getInstance().registerLocalChange(change);
                messages.addAll(Arrays.asList(changeMessages));
            }
            try {
                if (!messages.isEmpty()) {
                    IModelChangeMessage[] messagesArray = (IModelChangeMessage[]) messages.toArray(new IModelChangeMessage[messages.size()]);
                    BatchModelChange batchChange = new BatchModelChange(messagesArray);
                    share.send(Message.serialize(batchChange));
                    add(batchChange);
                }
            } catch (ECFException e) {
                getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, "Could not send resource change message", e));
            }
        }
    }

    private static IView viewInstance;

    public static synchronized void setView(IView resourcesView) {
        viewInstance = resourcesView;
        if (resourcesView != null) {
            resourcesView.setInput(enqueuedChanges);
        }
    }

    public static synchronized void add(Object object) {
        if (enqueuedChanges.size() == LIMIT) {
            remove();
        }
        enqueuedChanges.addFirst(object);
        if (viewInstance != null) {
            viewInstance.add(object);
        }
    }

    private static synchronized void remove() {
        Object object = enqueuedChanges.removeLast();
        if (viewInstance != null) {
            viewInstance.remove(object);
        }
    }

    private static Preferences preferences;

    private static Preferences defaultPreferences;

    public  SyncResourcesCore() {
        instance = this;
    }

    void attachListener() {
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
    }

    void detachListener() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
    public void start(BundleContext ctxt) throws Exception {
        super.start(ctxt);
        attachListener();
        preferences = new InstanceScope().getNode(SyncResourcesCore.PLUGIN_ID);
        defaultPreferences = new DefaultScope().getNode(SyncResourcesCore.PLUGIN_ID);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext context) throws Exception {
        instance = null;
        detachListener();
        super.stop(context);
    }

    public static SyncResourcesCore getDefault() {
        return instance;
    }

    public static int getInt(String key) {
        return preferences.getInt(key, defaultPreferences.getInt(key, PreferenceConstants.COMMIT_VALUE));
    }
}
