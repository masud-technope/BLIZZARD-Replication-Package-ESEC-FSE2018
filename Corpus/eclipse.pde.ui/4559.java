/*******************************************************************************
 *  Copyright (c) 2000, 2013 IBM Corporation and others.
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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.internal.core.isite.ISiteCategoryDefinition;
import org.eclipse.pde.internal.core.isite.ISiteDescription;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SiteCategoryDefinition extends SiteObject implements ISiteCategoryDefinition {

    private static final long serialVersionUID = 1L;

    private String name;

    private ISiteDescription description;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid() {
        return name != null && getLabel() != null;
    }

    @Override
    public void setName(String name) throws CoreException {
        ensureModelEditable();
        Object oldValue = this.name;
        this.name = name;
        firePropertyChanged(P_NAME, oldValue, name);
    }

    @Override
    public ISiteDescription getDescription() {
        return description;
    }

    @Override
    public void setDescription(ISiteDescription description) throws CoreException {
        ensureModelEditable();
        Object oldValue = this.description;
        this.description = description;
        firePropertyChanged(P_DESCRIPTION, oldValue, description);
    }

    @Override
    protected void reset() {
        super.reset();
        name = null;
        description = null;
    }

    @Override
    protected void parse(Node node) {
        super.parse(node);
        //$NON-NLS-1$
        name = getNodeAttribute(node, "name");
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (//$NON-NLS-1$
            child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equalsIgnoreCase("description")) {
                description = getModel().getFactory().createDescription(this);
                ((SiteDescription) description).parse(child);
                ((SiteDescription) description).setInTheModel(true);
                break;
            }
        }
    }

    @Override
    public void restoreProperty(String name, Object oldValue, Object newValue) throws CoreException {
        if (name.equals(P_NAME)) {
            setName(newValue != null ? newValue.toString() : null);
        } else if (name.equals(P_DESCRIPTION) && newValue instanceof ISiteDescription) {
            setDescription((ISiteDescription) newValue);
        } else
            super.restoreProperty(name, oldValue, newValue);
    }

    @Override
    public void write(String indent, PrintWriter writer) {
        writer.print(indent);
        //$NON-NLS-1$
        writer.print("<category-def");
        if (name != null)
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(" name=\"" + SiteObject.getWritableString(name) + "\"");
        if (label != null)
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(" label=\"" + SiteObject.getWritableString(label) + "\"");
        if (description != null) {
            //$NON-NLS-1$
            writer.println(">");
            description.write(indent + Site.INDENT, writer);
            //$NON-NLS-1$
            writer.println(indent + "</category-def>");
        } else
            //$NON-NLS-1$
            writer.println("/>");
    }
}
