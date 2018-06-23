/*******************************************************************************
 * Copyright (c) 2014 Rapicorp Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rapicorp Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.core.product;

import java.io.PrintWriter;
import org.eclipse.pde.internal.core.iproduct.IPreferencesInfo;
import org.eclipse.pde.internal.core.iproduct.IProductModel;
import org.w3c.dom.*;

public class PreferencesInfo extends ProductObject implements IPreferencesInfo {

    private static final long serialVersionUID = 1L;

    private String fSourceFilePath;

    private String fPreferenceCustomizationPath;

    private String fOverwrite;

    public  PreferencesInfo(IProductModel model) {
        super(model);
    }

    @Override
    public void setSourceFilePath(String text) {
        String old = fSourceFilePath;
        fSourceFilePath = text;
        if (isEditable()) {
            if (old != null && text != null) {
                if (!old.equals(text))
                    firePropertyChanged(P_SOURCEFILEPATH, old, fSourceFilePath);
            } else if (old != text)
                firePropertyChanged(P_SOURCEFILEPATH, old, fSourceFilePath);
        }
    }

    @Override
    public String getSourceFilePath() {
        return fSourceFilePath;
    }

    @Override
    public void setOverwrite(String text) {
        String old = fOverwrite;
        fOverwrite = text;
        if (isEditable()) {
            if (old != null && text != null) {
                if (!old.equals(text))
                    firePropertyChanged(P_OVERWRITE, old, fOverwrite);
            } else if (old != text)
                firePropertyChanged(P_OVERWRITE, old, fOverwrite);
        }
    }

    @Override
    public String getOverwrite() {
        return fOverwrite;
    }

    @Override
    public void setPreferenceCustomizationPath(String text) {
        String old = fPreferenceCustomizationPath;
        fPreferenceCustomizationPath = text;
        if (isEditable()) {
            if (old != null && text != null) {
                if (!old.equals(text))
                    firePropertyChanged(P_TARGETFILEPATH, old, fPreferenceCustomizationPath);
            } else if (old != text)
                firePropertyChanged(P_TARGETFILEPATH, old, fPreferenceCustomizationPath);
        }
    }

    @Override
    public String getPreferenceCustomizationPath() {
        return fPreferenceCustomizationPath;
    }

    @Override
    public void write(String indent, PrintWriter writer) {
        //$NON-NLS-1$
        writer.println(indent + "<preferencesInfo>");
        if (fSourceFilePath != null && fSourceFilePath.length() > 0) {
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.println(indent + "   <sourcefile path=\"" + getWritableString(fSourceFilePath.trim()) + "\"/>");
        }
        //$NON-NLS-1$
        boolean overwrite = fOverwrite != null && "true".equals(fOverwrite);
        //$NON-NLS-1$ //$NON-NLS-2$
        String targetFile = indent + "   <targetfile overwrite=\"" + Boolean.toString(overwrite) + "\"";
        if (fPreferenceCustomizationPath != null && fPreferenceCustomizationPath.length() > 0) {
            //$NON-NLS-1$ //$NON-NLS-2$
            targetFile += " path=\"" + getWritableString(fPreferenceCustomizationPath.trim()) + "\"";
        }
        //$NON-NLS-1$
        targetFile += "/>";
        writer.println(targetFile);
        //$NON-NLS-1$
        writer.println(indent + "</preferencesInfo>");
    }

    @Override
    public void parse(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (//$NON-NLS-1$
                child.getNodeName().equals("sourcefile")) {
                    fSourceFilePath = //$NON-NLS-1$
                    ((Element) child).getAttribute(//$NON-NLS-1$
                    "path");
                    if (fSourceFilePath.length() == 0)
                        fSourceFilePath = null;
                } else if (//$NON-NLS-1$
                child.getNodeName().equals("targetfile")) {
                    fOverwrite = //$NON-NLS-1$
                    ((Element) child).getAttribute(//$NON-NLS-1$
                    "overwrite");
                    fPreferenceCustomizationPath = //$NON-NLS-1$
                    ((Element) child).getAttribute(//$NON-NLS-1$
                    "path");
                    if (fPreferenceCustomizationPath.length() == 0)
                        fPreferenceCustomizationPath = null;
                }
            }
        }
    }
}
