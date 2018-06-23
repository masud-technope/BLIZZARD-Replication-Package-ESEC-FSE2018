/*******************************************************************************
 *  Copyright (c) 2000, 2016 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.core.feature;

import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.internal.core.ifeature.IFeatureURLElement;
import org.w3c.dom.Node;

public class FeatureURLElement extends FeatureObject implements IFeatureURLElement {

    private static final long serialVersionUID = 1L;

    private int fElementType;

    private int fSiteType = UPDATE_SITE;

    private URL fUrl;

    public  FeatureURLElement(int elementType) {
        this.fElementType = elementType;
    }

    public  FeatureURLElement(int elementType, URL url) {
        this.fElementType = elementType;
        this.fUrl = url;
    }

    @Override
    public int getElementType() {
        return fElementType;
    }

    @Override
    public URL getURL() {
        return fUrl;
    }

    @Override
    public int getSiteType() {
        return fSiteType;
    }

    @Override
    protected void parse(Node node) {
        super.parse(node);
        //$NON-NLS-1$
        String urlName = getNodeAttribute(node, "url");
        try {
            if (urlName != null)
                fUrl = new URL(urlName);
        } catch (MalformedURLException e) {
        }
        //$NON-NLS-1$
        String typeName = getNodeAttribute(node, "type");
        if (//$NON-NLS-1$
        typeName != null && typeName.equals("web"))
            fSiteType = WEB_SITE;
    }

    @Override
    public void setURL(URL url) throws CoreException {
        ensureModelEditable();
        Object oldValue = this.fUrl;
        this.fUrl = url;
        firePropertyChanged(this, P_URL, oldValue, url);
    }

    @Override
    public void setSiteType(int type) throws CoreException {
        ensureModelEditable();
        Integer oldValue = Integer.valueOf(this.fSiteType);
        this.fSiteType = type;
        firePropertyChanged(this, P_URL, oldValue, Integer.valueOf(type));
    }

    @Override
    public void restoreProperty(String name, Object oldValue, Object newValue) throws CoreException {
        if (name.equals(P_URL)) {
            setURL((URL) newValue);
        } else if (name.equals(P_SITE_TYPE)) {
            setSiteType(((Integer) newValue).intValue());
        } else
            super.restoreProperty(name, oldValue, newValue);
    }

    @Override
    public String toString() {
        if (label != null)
            return label;
        if (fUrl != null)
            return fUrl.toString();
        return super.toString();
    }

    @Override
    public void write(String indent, PrintWriter writer) {
        String tag = null;
        switch(fElementType) {
            case UPDATE:
                //$NON-NLS-1$
                tag = //$NON-NLS-1$
                "update";
                break;
            case DISCOVERY:
                //$NON-NLS-1$
                tag = //$NON-NLS-1$
                "discovery";
                break;
        }
        if (tag == null)
            return;
        //$NON-NLS-1$
        writer.print(indent + "<" + tag);
        if (label != null && label.length() > 0) {
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(" label=\"" + getWritableString(label) + "\"");
        }
        if (fUrl != null) {
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(" url=\"" + getWritableString(fUrl.toString()) + "\"");
        }
        if (fSiteType == WEB_SITE) {
            //$NON-NLS-1$
            writer.print(" type=\"web\"");
        }
        //$NON-NLS-1$
        writer.println("/>");
    }
}
