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
package org.eclipse.pde.internal.core.plugin;

import java.io.PrintWriter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.HostSpecification;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.eclipse.pde.core.plugin.IFragment;
import org.eclipse.pde.core.plugin.IMatchRules;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginExtensionPoint;
import org.eclipse.pde.internal.core.PDEState;
import org.w3c.dom.Node;

public class Fragment extends PluginBase implements IFragment {

    private static final long serialVersionUID = 1L;

    //$NON-NLS-1$
    private String fPluginId = "";

    //$NON-NLS-1$
    private String fPluginVersion = "";

    private int fMatchRule = IMatchRules.NONE;

    private boolean fPatch;

    public  Fragment(boolean readOnly) {
        super(readOnly);
    }

    @Override
    public String getPluginId() {
        return fPluginId;
    }

    @Override
    public String getPluginVersion() {
        return fPluginVersion;
    }

    @Override
    public int getRule() {
        return fMatchRule;
    }

    @Override
    protected boolean hasRequiredAttributes() {
        if (fPluginId == null || fPluginVersion == null)
            return false;
        return super.hasRequiredAttributes();
    }

    @Override
    void load(BundleDescription bundleDescription, PDEState state) {
        HostSpecification host = bundleDescription.getHost();
        fPluginId = host.getName();
        VersionRange versionRange = host.getVersionRange();
        if (versionRange != null) {
            fPluginVersion = versionRange.getMinimum() != null ? versionRange.getMinimum().toString() : null;
            fMatchRule = PluginBase.getMatchRule(versionRange);
        }
        fPatch = state.isPatchFragment(bundleDescription.getBundleId());
        super.load(bundleDescription, state);
    }

    @Override
    void load(Node node, String schemaVersion) {
        //$NON-NLS-1$
        fPluginId = getNodeAttribute(node, "plugin-id");
        //$NON-NLS-1$
        fPluginVersion = getNodeAttribute(node, "plugin-version");
        //$NON-NLS-1$
        String match = getNodeAttribute(node, "match");
        if (match != null) {
            String[] table = IMatchRules.RULE_NAME_TABLE;
            for (int i = 0; i < table.length; i++) {
                if (match.equalsIgnoreCase(table[i])) {
                    fMatchRule = i;
                    break;
                }
            }
        }
        super.load(node, schemaVersion);
    }

    @Override
    public void reset() {
        //$NON-NLS-1$
        fPluginId = "";
        //$NON-NLS-1$
        fPluginVersion = "";
        fMatchRule = IMatchRules.NONE;
        super.reset();
    }

    @Override
    public void setPluginId(String newPluginId) throws CoreException {
        ensureModelEditable();
        String oldValue = this.fPluginId;
        fPluginId = newPluginId;
        firePropertyChanged(P_PLUGIN_ID, oldValue, fPluginId);
    }

    @Override
    public void setPluginVersion(String newPluginVersion) throws CoreException {
        ensureModelEditable();
        String oldValue = this.fPluginVersion;
        fPluginVersion = newPluginVersion;
        firePropertyChanged(P_PLUGIN_VERSION, oldValue, fPluginVersion);
    }

    @Override
    public void setRule(int rule) throws CoreException {
        ensureModelEditable();
        Integer oldValue = Integer.valueOf(this.fMatchRule);
        fMatchRule = rule;
        firePropertyChanged(P_RULE, oldValue, Integer.valueOf(rule));
    }

    @Override
    public void restoreProperty(String name, Object oldValue, Object newValue) throws CoreException {
        if (name.equals(P_PLUGIN_ID)) {
            setPluginId(newValue != null ? newValue.toString() : null);
            return;
        }
        if (name.equals(P_PLUGIN_VERSION)) {
            setPluginVersion(newValue != null ? newValue.toString() : null);
            return;
        }
        if (name.equals(P_RULE)) {
            setRule(((Integer) newValue).intValue());
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
        writer.print("<fragment");
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
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print("   provider-name=\"" + getWritableString(getProviderName()) + "\"");
        }
        String pid = getPluginId();
        if (pid != null && pid.length() > 0) {
            writer.println();
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print("   plugin-id=\"" + getPluginId() + "\"");
        }
        String pver = getPluginVersion();
        if (pver != null && pver.length() > 0) {
            writer.println();
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print("   plugin-version=\"" + getPluginVersion() + "\"");
        }
        if (getRule() != IMatchRules.NONE) {
            writer.println();
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print("   match=\"" + IMatchRules.RULE_NAME_TABLE[getRule()] + "\"");
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
        if (children.length > 0) {
            for (int i = 0; i < children.length; i++) {
                ((IPluginExtensionPoint) children[i]).write(firstIndent, writer);
            }
            writer.println();
        }
        // add extensions
        children = getExtensions();
        for (int i = 0; i < children.length; i++) {
            ((IPluginExtension) children[i]).write(firstIndent, writer);
        }
        //$NON-NLS-1$
        writer.println("</fragment>");
    }

    public boolean isPatch() {
        return fPatch;
    }
}
