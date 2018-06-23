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
package org.eclipse.ecf.sync.ui.resources.decorators;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ecf.internal.sync.ui.resources.SyncResourcesUI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

public class RemotelyOpenedEditorsDecorator implements ILightweightLabelDecorator {

    //$NON-NLS-1$
    public static final String DECORATOR_ID = "org.eclipse.ecf.sync.ui.resources.decorators.RemotelyOpenedEditorsDecorator";

    private int quadrant = IDecoration.TOP_LEFT;

    //$NON-NLS-1$
    private String iconPath = "icons/read_only.gif";

    //$NON-NLS-1$
    private String iconPath2 = "icons/read_only2.gif";

    private ImageDescriptor descriptor;

    private ImageDescriptor descriptor2;

    public static Set set = new HashSet();

    public void decorate(Object element, IDecoration decoration) {
        IResource resource = (IResource) element;
        if (!set.contains(resource.getFullPath().toString())) {
            return;
        }
        if (resource instanceof IFile) {
            if (descriptor2 == null) {
                URL url = FileLocator.find(Platform.getBundle(SyncResourcesUI.PLUGIN_ID), new Path(iconPath2), null);
                if (url == null)
                    return;
                descriptor2 = ImageDescriptor.createFromURL(url);
            }
            decoration.addOverlay(descriptor2, quadrant);
        } else {
            if (descriptor == null) {
                URL url = FileLocator.find(Platform.getBundle(SyncResourcesUI.PLUGIN_ID), new Path(iconPath), null);
                if (url == null)
                    return;
                descriptor = ImageDescriptor.createFromURL(url);
            }
            decoration.addOverlay(descriptor, quadrant);
        }
    }

    public void addListener(ILabelProviderListener listener) {
    }

    public void dispose() {
    }

    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    public void removeListener(ILabelProviderListener listener) {
    }
}
