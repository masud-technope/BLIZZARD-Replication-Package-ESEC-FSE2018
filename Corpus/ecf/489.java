/*******************************************************************************
 * Copyright (c) 2006 IBM, Inc and Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ken Gilmer <kgilmer@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tutorial.scribbleshare.toolbox;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Label provider, requires <code>AbstractTool</code> class.
 * @author kgilmer
 *
 */
public class ToolboxLabelProvider implements ITableLabelProvider {

    public Image getColumnImage(Object element, int columnIndex) {
        if (element instanceof AbstractTool) {
            return ((AbstractTool) element).getImage();
        }
        return null;
    }

    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof AbstractTool) {
            return ((AbstractTool) element).getName();
        }
        return "[UNKNOWN TYPE IN LABEL PROVIDER: " + element.getClass().toString() + "]";
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
