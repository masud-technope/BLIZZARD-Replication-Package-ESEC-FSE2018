/*******************************************************************************
 *  Copyright (c) 2000, 2012 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.core.text.plugin;

import java.util.ArrayList;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.core.plugin.IFragment;
import org.eclipse.pde.core.plugin.IMatchRules;

public class FragmentNode extends PluginBaseNode implements IFragment {

    /**
	 * Comment for <code>serialVersionUID</code>
	 */
    private static final long serialVersionUID = 1L;

    @Override
    public String getPluginId() {
        return getXMLAttributeValue(P_PLUGIN_ID);
    }

    @Override
    public String getPluginVersion() {
        return getXMLAttributeValue(P_PLUGIN_VERSION);
    }

    @Override
    public int getRule() {
        //$NON-NLS-1$
        String match = getXMLAttributeValue("match");
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
    public void setPluginId(String id) throws CoreException {
        setXMLAttribute(P_PLUGIN_ID, id);
    }

    @Override
    public void setPluginVersion(String version) throws CoreException {
        setXMLAttribute(P_PLUGIN_VERSION, version);
    }

    @Override
    public void setRule(int rule) throws CoreException {
        //$NON-NLS-1$
        String match = "";
        switch(rule) {
            case IMatchRules.COMPATIBLE:
                //$NON-NLS-1$
                match = //$NON-NLS-1$
                "compatible";
                break;
            case IMatchRules.EQUIVALENT:
                //$NON-NLS-1$
                match = //$NON-NLS-1$
                "equivalent";
                break;
            case IMatchRules.PERFECT:
                //$NON-NLS-1$
                match = //$NON-NLS-1$
                "perfect";
                break;
            case IMatchRules.GREATER_OR_EQUAL:
                match = "greaterOrEqual";
        }
        setXMLAttribute(P_RULE, match);
    }

    @Override
    protected String[] getSpecificAttributes() {
        ArrayList<String> result = new ArrayList();
        String pluginID = getPluginId();
        if (pluginID != null && pluginID.trim().length() > 0)
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            result.add("   " + P_PLUGIN_ID + "=\"" + pluginID + "\"");
        String pluginVersion = getPluginVersion();
        if (pluginVersion != null && pluginVersion.trim().length() > 0)
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            result.add("   " + P_PLUGIN_VERSION + "=\"" + pluginVersion + "\"");
        String match = getXMLAttributeValue(P_RULE);
        if (match != null && match.trim().length() > 0)
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            result.add("   " + P_RULE + "=\"" + match + "\"");
        return result.toArray(new String[result.size()]);
    }
}
