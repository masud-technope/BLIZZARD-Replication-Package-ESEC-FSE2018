/*******************************************************************************
 * Copyright (c) 2008 Code 9 Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Code 9 Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.core.product;

import java.io.PrintWriter;
import org.eclipse.pde.internal.core.iproduct.ILicenseInfo;
import org.eclipse.pde.internal.core.iproduct.IProductModel;
import org.w3c.dom.*;

public class LicenseInfo extends ProductObject implements ILicenseInfo {

    //$NON-NLS-1$
    public static final String P_URL = "url";

    //$NON-NLS-1$
    public static final String P_LICENSE = "text";

    private static final long serialVersionUID = 1L;

    private String fURL;

    private String fLicense;

    public  LicenseInfo(IProductModel model) {
        super(model);
    }

    @Override
    public void setURL(String url) {
        String old = fURL;
        fURL = url;
        if (isEditable())
            firePropertyChanged(P_URL, old, fURL);
    }

    @Override
    public String getURL() {
        return fURL;
    }

    @Override
    public String getLicense() {
        return fLicense;
    }

    @Override
    public void setLicense(String text) {
        String old = fLicense;
        fLicense = text;
        if (isEditable())
            firePropertyChanged(P_LICENSE, old, fLicense);
    }

    @Override
    public void parse(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (child.getNodeName().equals(P_LICENSE)) {
                    child.normalize();
                    if (child.getChildNodes().getLength() > 0) {
                        Node text = child.getFirstChild();
                        if (text.getNodeType() == Node.TEXT_NODE) {
                            fLicense = ((Text) text).getData().trim();
                        }
                    }
                }
                if (child.getNodeName().equals(P_URL)) {
                    child.normalize();
                    if (child.getChildNodes().getLength() > 0) {
                        Node text = child.getFirstChild();
                        if (text.getNodeType() == Node.TEXT_NODE) {
                            fURL = ((Text) text).getData().trim();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void write(String indent, PrintWriter writer) {
        if (isURLDefined() || isLicenseTextDefined()) {
            //$NON-NLS-1$
            writer.println(indent + "<license>");
            if (isURLDefined()) {
                //$NON-NLS-1$ //$NON-NLS-2$
                writer.println(indent + "     <url>" + getWritableString(fURL.trim()) + "</url>");
            }
            if (isLicenseTextDefined()) {
                //$NON-NLS-1$
                writer.println(//$NON-NLS-1$
                indent + "     <text>");
                writer.println(indent + getWritableString(fLicense.trim()));
                //$NON-NLS-1$
                writer.println(//$NON-NLS-1$
                indent + "      </text>");
            }
            //$NON-NLS-1$
            writer.println(indent + "</license>");
        }
    }

    private boolean isURLDefined() {
        return fURL != null && fURL.length() > 0;
    }

    private boolean isLicenseTextDefined() {
        return fLicense != null && fLicense.length() > 0;
    }
}
