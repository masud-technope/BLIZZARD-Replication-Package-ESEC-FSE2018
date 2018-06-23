/*******************************************************************************
 *  Copyright (c) 2000, 2008 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.core.plugin;

import java.io.PrintWriter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.pde.core.plugin.IPlugin;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginExtensionPoint;
import org.eclipse.pde.internal.core.PDEState;
import org.w3c.dom.Node;

public class Plugin extends PluginBase implements IPlugin {

    private static final long serialVersionUID = 1L;

    private String fClassname;

    private boolean fHasExtensibleAPI;

    public  Plugin(boolean readOnly) {
        super(readOnly);
    }

    @Override
    public String getClassName() {
        return fClassname;
    }

    public IPlugin getPlugin() {
        return this;
    }

    @Override
    void load(BundleDescription bundleDescription, PDEState state) {
        fClassname = state.getClassName(bundleDescription.getBundleId());
        fHasExtensibleAPI = state.hasExtensibleAPI(bundleDescription.getBundleId());
        super.load(bundleDescription, state);
    }

    @Override
    void load(Node node, String schemaVersion) {
        //$NON-NLS-1$
        fClassname = getNodeAttribute(node, "class");
        super.load(node, schemaVersion);
    }

    @Override
    public void reset() {
        fClassname = null;
        super.reset();
    }

    @Override
    public void setClassName(String newClassName) throws CoreException {
        ensureModelEditable();
        String oldValue = fClassname;
        fClassname = newClassName;
        firePropertyChanged(P_CLASS_NAME, oldValue, fClassname);
    }

    @Override
    public void restoreProperty(String name, Object oldValue, Object newValue) throws CoreException {
        if (name.equals(P_CLASS_NAME)) {
            setClassName(newValue != null ? newValue.toString() : null);
            return;
        }
        super.restoreProperty(name, oldValue, newValue);
    }

    @Override
    public void write(String indent, PrintWriter writer) {
        //$NON-NLS-1$
        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        if (getSchemaVersion() != null) {
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.println("<?eclipse version=\"" + getSchemaVersion() + "\"?>");
        }
        //$NON-NLS-1$
        writer.print("<plugin");
        if (getId() != null) {
            writer.println();
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print("   id=\"" + getId() + "\"");
        }
        if (getName() != null) {
            writer.println();
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print("   name=\"" + getWritableString(getName()) + "\"");
        }
        if (getVersion() != null) {
            writer.println();
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print("   version=\"" + getVersion() + "\"");
        }
        if (getProviderName() != null) {
            writer.println();
            writer.print(//$NON-NLS-1$
            "   provider-name=\"" + //$NON-NLS-1$
            getWritableString(getProviderName()) + //$NON-NLS-1$
            "\"");
        }
        if (getClassName() != null) {
            writer.println();
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print("   class=\"" + getClassName() + "\"");
        }
        //$NON-NLS-1$
        writer.println(">");
        writer.println();
        //$NON-NLS-1$
        String firstIndent = "   ";
        // add runtime
        Object[] children = getLibraries();
        if (children.length > 0) {
            //$NON-NLS-1$
            writeChildren(firstIndent, "runtime", children, writer);
            writer.println();
        }
        // add requires
        children = getImports();
        if (children.length > 0) {
            //$NON-NLS-1$
            writeChildren(firstIndent, "requires", children, writer);
            writer.println();
        }
        children = getExtensionPoints();
        for (int i = 0; i < children.length; i++) {
            ((IPluginExtensionPoint) children[i]).write(firstIndent, writer);
        }
        if (children.length > 0)
            writer.println();
        // add extensions
        children = getExtensions();
        for (int i = 0; i < children.length; i++) {
            ((IPluginExtension) children[i]).write(firstIndent, writer);
        }
        if (children.length > 0)
            writer.println();
        //$NON-NLS-1$
        writer.println("</plugin>");
    }

    public boolean hasExtensibleAPI() {
        return fHasExtensibleAPI;
    }
}
