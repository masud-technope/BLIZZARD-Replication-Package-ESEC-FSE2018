/*******************************************************************************
 * Copyright (c) 2008, 2016 Cypal Solutions.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Cypal Solutions - initial implementation
 *     Lars Vogel <Lars.Vogel@vogella.com> - Bug 473694, 486261
 *******************************************************************************/
package org.eclipse.pde.internal.ui.templates.ide;

import java.util.ArrayList;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.internal.ui.templates.PDETemplateMessages;
import org.eclipse.pde.internal.ui.templates.PDETemplateSection;
import org.eclipse.pde.ui.templates.BooleanOption;
import org.eclipse.pde.ui.templates.PluginReference;

/**
 * Template for a new view implementing the common navigator framework.
 */
public class CommonNavigatorTemplate extends PDETemplateSection {

    //$NON-NLS-1$
    public static final String KEY_VIEW_ID = "viewId";

    //$NON-NLS-1$
    public static final String KEY_VIEW_NAME = "viewName";

    //$NON-NLS-1$
    public static final String KEY_ADD_TO_PERSPECTIVE = "addToPerspective";

    private BooleanOption addToPerspective;

    private IPluginBase plugin;

    private IPluginModelFactory factory;

    private String viewId;

    public  CommonNavigatorTemplate() {
        setPageCount(1);
        createOptions();
    }

    private void createOptions() {
        //$NON-NLS-1$
        addOption(KEY_VIEW_ID, PDETemplateMessages.CommonNavigatorTemplate_viewId, "com.example.test", 0);
        addOption(KEY_VIEW_NAME, PDETemplateMessages.CommonNavigatorTemplate_viewName, PDETemplateMessages.CommonNavigatorTemplate_defaultViewName, 0);
        addToPerspective = (BooleanOption) addOption(KEY_ADD_TO_PERSPECTIVE, PDETemplateMessages.CommonNavigatorTemplate_addToPerspective, true, 0);
    }

    @Override
    public String getSectionId() {
        //$NON-NLS-1$
        return "commonNavigator";
    }

    @Override
    protected void updateModel(IProgressMonitor monitor) throws CoreException {
        plugin = model.getPluginBase();
        factory = model.getPluginFactory();
        viewId = getStringOption(KEY_VIEW_ID);
        createView();
        createViewer();
        if (addToPerspective.isSelected()) {
            createAddToPerspective();
        }
    }

    private void createAddToPerspective() throws CoreException {
        //$NON-NLS-1$
        IPluginExtension perspectiveExtension = createExtension("org.eclipse.ui.perspectiveExtensions", true);
        IPluginElement perspectiveElement = factory.createElement(perspectiveExtension);
        //$NON-NLS-1$
        perspectiveElement.setName("perspectiveExtension");
        //$NON-NLS-1$
        perspectiveElement.setAttribute(//$NON-NLS-1$
        "targetID", //$NON-NLS-1$
        "org.eclipse.ui.resourcePerspective");
        IPluginElement view = factory.createElement(perspectiveElement);
        //$NON-NLS-1$
        view.setName("view");
        //$NON-NLS-1$
        view.setAttribute("id", viewId);
        //$NON-NLS-1$ //$NON-NLS-2$
        view.setAttribute("relative", "org.eclipse.ui.navigator.ProjectExplorer");
        //$NON-NLS-1$ //$NON-NLS-2$
        view.setAttribute("relationship", "stack");
        //$NON-NLS-1$ //$NON-NLS-2$
        view.setAttribute("ratio", "0.5");
        perspectiveElement.add(view);
        perspectiveExtension.add(perspectiveElement);
        if (!perspectiveExtension.isInTheModel())
            plugin.add(perspectiveExtension);
    }

    private void createViewer() throws CoreException {
        //$NON-NLS-1$
        IPluginExtension viewerExtension = createExtension("org.eclipse.ui.navigator.viewer", true);
        if (!viewerExtension.isInTheModel())
            plugin.add(viewerExtension);
        createActionBinding(viewerExtension);
        createContentBinding(viewerExtension);
    }

    private void createContentBinding(IPluginExtension viewerExtension) throws CoreException {
        IPluginElement viewerContentBindingElement = factory.createElement(viewerExtension);
        //$NON-NLS-1$
        viewerContentBindingElement.setName("viewerContentBinding");
        //$NON-NLS-1$
        viewerContentBindingElement.setAttribute("viewerId", viewId);
        IPluginElement includesElement = factory.createElement(viewerContentBindingElement);
        //$NON-NLS-1$
        includesElement.setName("includes");
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        createChild(includesElement, "contentExtension", "pattern", "org.eclipse.ui.navigator.resourceContent");
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        createChild(includesElement, "contentExtension", "pattern", "org.eclipse.ui.navigator.resources.filters.*");
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        createChild(includesElement, "contentExtension", "pattern", "org.eclipse.ui.navigator.resources.linkHelper");
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        createChild(includesElement, "contentExtension", "pattern", "org.eclipse.ui.navigator.resources.workingSets");
        viewerContentBindingElement.add(includesElement);
        viewerExtension.add(viewerContentBindingElement);
    }

    private void createActionBinding(IPluginExtension viewerExtension) throws CoreException {
        IPluginElement viewerActionBindingElement = factory.createElement(viewerExtension);
        //$NON-NLS-1$
        viewerActionBindingElement.setName("viewerActionBinding");
        //$NON-NLS-1$
        viewerActionBindingElement.setAttribute("viewerId", viewId);
        IPluginElement includesElement = factory.createElement(viewerActionBindingElement);
        //$NON-NLS-1$
        includesElement.setName("includes");
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        createChild(includesElement, "actionExtension", "pattern", "org.eclipse.ui.navigator.resources.*");
        viewerActionBindingElement.add(includesElement);
        viewerExtension.add(viewerActionBindingElement);
    }

    private void createChild(IPluginElement parent, String name, String attrName, String attrValue) throws CoreException {
        IPluginElement child = factory.createElement(parent);
        child.setName(name);
        child.setAttribute(attrName, attrValue);
        parent.add(child);
    }

    private void createView() throws CoreException {
        //$NON-NLS-1$
        IPluginExtension viewExtension = createExtension("org.eclipse.ui.views", true);
        IPluginElement viewElement = factory.createElement(viewExtension);
        //$NON-NLS-1$
        viewElement.setName("view");
        //$NON-NLS-1$
        viewElement.setAttribute("id", viewId);
        //$NON-NLS-1$
        viewElement.setAttribute("name", getStringOption(KEY_VIEW_NAME));
        //$NON-NLS-1$ //$NON-NLS-2$
        viewElement.setAttribute("icon", "icons/sample.gif");
        //$NON-NLS-1$  //$NON-NLS-2$
        viewElement.setAttribute("class", "org.eclipse.ui.navigator.CommonNavigator");
        viewExtension.add(viewElement);
        if (!viewExtension.isInTheModel())
            plugin.add(viewExtension);
    }

    @Override
    public String[] getNewFiles() {
        return new String[0];
    }

    @Override
    public String getUsedExtensionPoint() {
        //$NON-NLS-1$
        return "org.eclipse.ui.navigator.CommonNavigator";
    }

    @Override
    public void addPages(Wizard wizard) {
        WizardPage page0 = createPage(0);
        page0.setTitle(PDETemplateMessages.CommonNavigatorTemplate_pagetitle);
        page0.setDescription(PDETemplateMessages.CommonNavigatorTemplate_pagedescription);
        wizard.addPage(page0);
        markPagesAdded();
    }

    @Override
    public IPluginReference[] getDependencies(String schemaVersion) {
        ArrayList<PluginReference> result = new ArrayList();
        //$NON-NLS-1$
        result.add(new PluginReference("org.eclipse.core.resources"));
        //$NON-NLS-1$
        result.add(new PluginReference("org.eclipse.ui.navigator"));
        //$NON-NLS-1$
        result.add(new PluginReference("org.eclipse.ui.navigator.resources"));
        return result.toArray(new IPluginReference[result.size()]);
    }
}
