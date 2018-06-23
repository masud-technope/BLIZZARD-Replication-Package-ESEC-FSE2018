/*******************************************************************************
 *  Copyright (c) 2000, 2015 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.core.site;

import java.io.PrintWriter;
import java.util.Vector;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.pde.core.IModelChangedEvent;
import org.eclipse.pde.internal.core.isite.ISiteCategory;
import org.eclipse.pde.internal.core.isite.ISiteFeature;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SiteFeature extends VersionableObject implements ISiteFeature {

    private static final long serialVersionUID = 1L;

    private Vector<ISiteCategory> fCategories = new Vector();

    private String fType;

    private String fUrl;

    private String fOS;

    private String fWS;

    private String fArch;

    private String fNL;

    private boolean fIsPatch;

    @Override
    public boolean isValid() {
        if (fUrl == null)
            return false;
        for (int i = 0; i < fCategories.size(); i++) {
            ISiteCategory category = fCategories.get(i);
            if (!category.isValid())
                return false;
        }
        return true;
    }

    @Override
    public void addCategories(ISiteCategory[] newCategories) throws CoreException {
        ensureModelEditable();
        for (int i = 0; i < newCategories.length; i++) {
            ISiteCategory category = newCategories[i];
            ((SiteCategory) category).setInTheModel(true);
            fCategories.add(newCategories[i]);
        }
        fireStructureChanged(newCategories, IModelChangedEvent.INSERT);
    }

    @Override
    public void removeCategories(ISiteCategory[] newCategories) throws CoreException {
        ensureModelEditable();
        for (int i = 0; i < newCategories.length; i++) {
            ISiteCategory category = newCategories[i];
            ((SiteCategory) category).setInTheModel(false);
            fCategories.remove(newCategories[i]);
        }
        fireStructureChanged(newCategories, IModelChangedEvent.REMOVE);
    }

    @Override
    public ISiteCategory[] getCategories() {
        return fCategories.toArray(new ISiteCategory[fCategories.size()]);
    }

    @Override
    public String getType() {
        return fType;
    }

    @Override
    public String getURL() {
        return fUrl;
    }

    @Override
    public void setType(String type) throws CoreException {
        ensureModelEditable();
        Object oldValue = this.fType;
        this.fType = type;
        firePropertyChanged(P_TYPE, oldValue, fType);
    }

    @Override
    public void setURL(String url) throws CoreException {
        ensureModelEditable();
        Object oldValue = this.fUrl;
        this.fUrl = url;
        firePropertyChanged(P_TYPE, oldValue, url);
    }

    @Override
    protected void parse(Node node) {
        super.parse(node);
        //$NON-NLS-1$
        fType = getNodeAttribute(node, "type");
        //$NON-NLS-1$
        fUrl = getNodeAttribute(node, "url");
        //$NON-NLS-1$
        fOS = getNodeAttribute(node, "os");
        //$NON-NLS-1$
        fNL = getNodeAttribute(node, "nl");
        //$NON-NLS-1$
        fWS = getNodeAttribute(node, "ws");
        //$NON-NLS-1$
        fArch = getNodeAttribute(node, "arch");
        //$NON-NLS-1$
        String value = getNodeAttribute(node, "patch");
        //$NON-NLS-1$
        fIsPatch = value != null && value.equals("true");
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (//$NON-NLS-1$
            child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equalsIgnoreCase("category")) {
                SiteCategory category = (SiteCategory) getModel().getFactory().createCategory(this);
                category.parse(child);
                category.setInTheModel(true);
                fCategories.add(category);
            }
        }
    }

    @Override
    protected void reset() {
        super.reset();
        fType = null;
        fUrl = null;
        fOS = null;
        fWS = null;
        fArch = null;
        fNL = null;
        fIsPatch = false;
        fCategories.clear();
    }

    @Override
    public void restoreProperty(String name, Object oldValue, Object newValue) throws CoreException {
        if (name.equals(P_TYPE)) {
            setType(newValue != null ? newValue.toString() : null);
        } else if (name.equals(P_URL)) {
            setURL(newValue != null ? newValue.toString() : null);
        } else if (name.equals(P_ARCH)) {
            setArch(newValue != null ? newValue.toString() : null);
        } else if (name.equals(P_NL)) {
            setNL(newValue != null ? newValue.toString() : null);
        } else if (name.equals(P_OS)) {
            setOS(newValue != null ? newValue.toString() : null);
        } else if (name.equals(P_WS)) {
            setWS(newValue != null ? newValue.toString() : null);
        } else if (name.equals(P_PATCH)) {
            setIsPatch(((Boolean) newValue).booleanValue());
        } else {
            super.restoreProperty(name, oldValue, newValue);
        }
    }

    @Override
    public void write(String indent, PrintWriter writer) {
        writer.print(indent);
        //$NON-NLS-1$
        writer.print("<feature");
        if (fType != null)
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(" type=\"" + fType + "\"");
        if (fUrl != null)
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(" url=\"" + fUrl + "\"");
        if (id != null)
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(" id=\"" + getId() + "\"");
        if (version != null)
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(" version=\"" + getVersion() + "\"");
        if (label != null)
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(" label=\"" + getLabel() + "\"");
        if (fOS != null)
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(" os=\"" + fOS + "\"");
        if (fWS != null)
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(" ws=\"" + fWS + "\"");
        if (fNL != null)
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(" nl=\"" + fNL + "\"");
        if (fArch != null)
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(" arch=\"" + fArch + "\"");
        if (fIsPatch)
            //$NON-NLS-1$
            writer.print(" patch=\"true\"");
        if (fCategories.size() > 0) {
            //$NON-NLS-1$
            writer.println(">");
            //$NON-NLS-1$
            String indent2 = indent + "   ";
            for (int i = 0; i < fCategories.size(); i++) {
                ISiteCategory category = fCategories.get(i);
                category.write(indent2, writer);
            }
            //$NON-NLS-1$
            writer.println(indent + "</feature>");
        } else
            //$NON-NLS-1$
            writer.println("/>");
    }

    @Override
    public IFile getArchiveFile() {
        if (fUrl == null)
            return null;
        IResource resource = getModel().getUnderlyingResource();
        if (resource == null)
            return null;
        IProject project = resource.getProject();
        IFile file = project.getFile(new Path(fUrl));
        if (file.exists())
            return file;
        return null;
    }

    @Override
    public String getOS() {
        return fOS;
    }

    @Override
    public String getNL() {
        return fNL;
    }

    @Override
    public String getArch() {
        return fArch;
    }

    @Override
    public String getWS() {
        return fWS;
    }

    @Override
    public void setOS(String os) throws CoreException {
        ensureModelEditable();
        Object oldValue = fOS;
        fOS = os;
        firePropertyChanged(P_OS, oldValue, fOS);
    }

    @Override
    public void setWS(String ws) throws CoreException {
        ensureModelEditable();
        Object oldValue = fWS;
        fWS = ws;
        firePropertyChanged(P_WS, oldValue, fWS);
    }

    @Override
    public void setArch(String arch) throws CoreException {
        ensureModelEditable();
        Object oldValue = fArch;
        fArch = arch;
        firePropertyChanged(P_ARCH, oldValue, fArch);
    }

    @Override
    public void setNL(String nl) throws CoreException {
        ensureModelEditable();
        Object oldValue = fNL;
        fNL = nl;
        firePropertyChanged(P_NL, oldValue, fNL);
    }

    @Override
    public boolean isPatch() {
        return fIsPatch;
    }

    @Override
    public void setIsPatch(boolean patch) throws CoreException {
        ensureModelEditable();
        Object oldValue = Boolean.valueOf(fIsPatch);
        fIsPatch = patch;
        firePropertyChanged(P_PATCH, oldValue, Boolean.valueOf(fIsPatch));
    }
}
