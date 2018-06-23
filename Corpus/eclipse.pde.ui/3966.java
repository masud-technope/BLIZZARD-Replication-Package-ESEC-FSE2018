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
package org.eclipse.pde.internal.core.text.plugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.core.plugin.IMatchRules;
import org.eclipse.pde.core.plugin.IPluginImport;

public class PluginImportNode extends PluginObjectNode implements IPluginImport {

    private static final long serialVersionUID = 1L;

    public  PluginImportNode(String id) {
        super();
        //$NON-NLS-1$
        String name = "plugin";
        try {
            if (id == null)
                //$NON-NLS-1$
                id = "";
            PluginAttribute attr = new PluginAttribute();
            attr.setName(name);
            attr.setEnclosingElement(this);
            getNodeAttributesMap().put(name, attr);
            attr.setValue(id);
        } catch (CoreException e) {
        }
    }

    public  PluginImportNode() {
        super();
    }

    @Override
    public boolean isReexported() {
        String value = getXMLAttributeValue(P_REEXPORTED);
        //$NON-NLS-1$
        return value != null && value.equals("true");
    }

    @Override
    public boolean isOptional() {
        String value = getXMLAttributeValue(P_OPTIONAL);
        //$NON-NLS-1$
        return value != null && value.equals("true");
    }

    @Override
    public void setReexported(boolean value) throws CoreException {
        //$NON-NLS-1$ //$NON-NLS-2$
        setXMLAttribute(P_REEXPORTED, value ? "true" : "false");
    }

    @Override
    public void setOptional(boolean value) throws CoreException {
        //$NON-NLS-1$ //$NON-NLS-2$
        setXMLAttribute(P_OPTIONAL, value ? "true" : "false");
    }

    @Override
    public int getMatch() {
        String match = getXMLAttributeValue(P_MATCH);
        if (match == null || match.trim().length() == 0)
            return IMatchRules.NONE;
        if (//$NON-NLS-1$
        match.equals("compatible"))
            return IMatchRules.COMPATIBLE;
        if (//$NON-NLS-1$
        match.equals("perfect"))
            return IMatchRules.PERFECT;
        if (//$NON-NLS-1$
        match.equals("equivalent"))
            return IMatchRules.EQUIVALENT;
        return IMatchRules.GREATER_OR_EQUAL;
    }

    @Override
    public String getVersion() {
        return getXMLAttributeValue(P_VERSION);
    }

    @Override
    public void setMatch(int match) throws CoreException {
        switch(match) {
            case IMatchRules.GREATER_OR_EQUAL:
                setXMLAttribute(//$NON-NLS-1$
                P_MATCH, //$NON-NLS-1$
                "greaterOrEqual");
                break;
            case IMatchRules.EQUIVALENT:
                setXMLAttribute(//$NON-NLS-1$
                P_MATCH, //$NON-NLS-1$
                "equivalent");
                break;
            case IMatchRules.COMPATIBLE:
                setXMLAttribute(//$NON-NLS-1$
                P_MATCH, //$NON-NLS-1$
                "compatible");
                break;
            case IMatchRules.PERFECT:
                setXMLAttribute(//$NON-NLS-1$
                P_MATCH, //$NON-NLS-1$
                "perfect");
                break;
            default:
                setXMLAttribute(P_MATCH, null);
        }
    }

    @Override
    public void setVersion(String version) throws CoreException {
        setXMLAttribute(P_VERSION, version);
    }

    @Override
    public String getId() {
        //$NON-NLS-1$
        return getXMLAttributeValue("plugin");
    }

    @Override
    public void setId(String id) throws CoreException {
        //$NON-NLS-1$
        setXMLAttribute("plugin", id);
    }

    @Override
    public String write(boolean indent) {
        return indent ? getIndent() + writeShallow(true) : writeShallow(true);
    }

    @Override
    public String writeShallow(boolean terminate) {
        //$NON-NLS-1$
        StringBuffer buffer = new StringBuffer("<import");
        //$NON-NLS-1$
        appendAttribute(buffer, "plugin");
        appendAttribute(buffer, P_VERSION);
        appendAttribute(buffer, P_MATCH);
        //$NON-NLS-1$
        appendAttribute(buffer, P_REEXPORTED, "false");
        //$NON-NLS-1$
        appendAttribute(buffer, P_OPTIONAL, "false");
        if (terminate)
            //$NON-NLS-1$
            buffer.append("/");
        //$NON-NLS-1$
        buffer.append(">");
        return buffer.toString();
    }
}
