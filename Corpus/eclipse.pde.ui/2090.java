/*******************************************************************************
 * Copyright (c) 2005, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Code 9 Corporation - ongoing enhancements
 *     Benjamin Cabe <benjamin.cabe@anyware-tech.com> - bug 264462
 *     Simon Scholz <simon.scholz@vogella.com> - Bug 444808
 *******************************************************************************/
package org.eclipse.pde.internal.core.product;

import java.io.PrintWriter;
import org.eclipse.pde.core.plugin.IFragmentModel;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.internal.core.ICoreConstants;
import org.eclipse.pde.internal.core.iproduct.IProductModel;
import org.eclipse.pde.internal.core.iproduct.IProductPlugin;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ProductPlugin extends ProductObject implements IProductPlugin {

    private static final long serialVersionUID = 1L;

    private String fId;

    private String fVersion;

    /**
	 * Used to cache the fragment attribute value internally in order to not lose it in case the current
	 * plugin/fragment is not in the target platform anymore (see bug 264462)
	 */
    private boolean fFragment;

    public  ProductPlugin(IProductModel model) {
        super(model);
    }

    @Override
    public void parse(Node node) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            //$NON-NLS-1$
            fId = element.getAttribute("id");
            //$NON-NLS-1$
            fVersion = element.getAttribute("version");
            //$NON-NLS-1$
            String fragment = element.getAttribute("fragment");
            fFragment = Boolean.valueOf(fragment).booleanValue();
        }
    }

    @Override
    public void write(String indent, PrintWriter writer) {
        //$NON-NLS-1$ //$NON-NLS-2$
        writer.print(indent + "<plugin id=\"" + fId + "\"");
        if (fVersion != null && fVersion.length() > 0 && !fVersion.equals(ICoreConstants.DEFAULT_VERSION)) {
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(" version=\"" + fVersion + "\"");
        }
        // If the plugin is a known fragment or has a cached fragment setting, mark it as a fragment
        if (PluginRegistry.findModel(fId) != null) {
            if (PluginRegistry.findModel(fId) instanceof IFragmentModel) {
                //$NON-NLS-1$ //$NON-NLS-2$
                writer.print(" fragment=\"" + Boolean.TRUE.toString() + "\"");
            }
        } else if (fFragment) {
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(" fragment=\"" + Boolean.TRUE.toString() + "\"");
        }
        //$NON-NLS-1$
        writer.println("/>");
    }

    @Override
    public String getId() {
        return fId.trim();
    }

    @Override
    public void setId(String id) {
        fId = id;
    }

    @Override
    public String getVersion() {
        return fVersion;
    }

    @Override
    public void setVersion(String version) {
        String old = fVersion;
        fVersion = version;
        if (isEditable())
            //$NON-NLS-1$
            firePropertyChanged("version", old, fVersion);
    }

    @Override
    public boolean isFragment() {
        return fFragment;
    }

    @Override
    public void setFragment(boolean isFragment) {
        fFragment = isFragment;
    }
}
