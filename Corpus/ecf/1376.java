/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.presence.collab.ui.view;

import java.util.HashMap;
import java.util.Iterator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewCategory;
import org.eclipse.ui.views.IViewDescriptor;

public class ShowViewDialogLabelProvider extends LabelProvider {

    private HashMap images = new HashMap();

    public Image getImage(Object element) {
        ImageDescriptor desc = null;
        if (element instanceof IViewCategory)
            desc = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER);
        else if (element instanceof IViewDescriptor)
            desc = ((IViewDescriptor) element).getImageDescriptor();
        if (desc == null)
            return null;
        Image image = (Image) images.get(desc);
        if (image == null) {
            image = desc.createImage();
            images.put(desc, image);
        }
        return image;
    }

    public String getText(Object element) {
        String label;
        if (element instanceof IViewCategory)
            label = ((IViewCategory) element).getLabel();
        else if (element instanceof IViewDescriptor)
            label = ((IViewDescriptor) element).getLabel();
        else
            label = super.getText(element);
        for (int i = label.indexOf('&'); i >= 0 && i < label.length() - 1; i = label.indexOf('&', i + 1)) if (!Character.isWhitespace(label.charAt(i + 1)))
            return label.substring(0, i) + label.substring(i + 1);
        return label;
    }

    public void dispose() {
        for (Iterator i = images.values().iterator(); i.hasNext(); ) ((Image) i.next()).dispose();
        images = null;
        super.dispose();
    }
}
