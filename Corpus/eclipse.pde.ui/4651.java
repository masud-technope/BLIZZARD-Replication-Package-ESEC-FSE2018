/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <Lars.Vogel@vogella.com> - Bug 486261
 *******************************************************************************/
package org.eclipse.pde.internal.ui.templates.ide;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.internal.ui.templates.*;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.*;

public class PopupMenuTemplate extends PDETemplateSection {

    //$NON-NLS-1$
    public static final String KEY_TARGET_OBJECT = "objectClass";

    //$NON-NLS-1$
    public static final String KEY_SUBMENU_LABEL = "subMenuLabel";

    //$NON-NLS-1$
    public static final String KEY_ACTION_LABEL = "actionLabel";

    //$NON-NLS-1$
    public static final String KEY_ACTION_CLASS = "actionClass";

    //$NON-NLS-1$
    public static final String KEY_SELECTION = "selection";

    /**
	 * Constructor for PropertyPageTemplate.
	 */
    public  PopupMenuTemplate() {
        setPageCount(1);
        createOptions();
    }

    @Override
    public void addPages(Wizard wizard) {
        WizardPage page = createPage(0, IHelpContextIds.TEMPLATE_POPUP_MENU);
        page.setTitle(PDETemplateMessages.PopupMenuTemplate_title);
        page.setDescription(PDETemplateMessages.PopupMenuTemplate_desc);
        wizard.addPage(page);
        markPagesAdded();
    }

    private void createOptions() {
        addOption(//$NON-NLS-1$
        KEY_TARGET_OBJECT, //$NON-NLS-1$
        PDETemplateMessages.PopupMenuTemplate_targetClass, //$NON-NLS-1$
        "org.eclipse.core.resources.IFile", 0);
        addOption(KEY_SUBMENU_LABEL, PDETemplateMessages.PopupMenuTemplate_submenuName, PDETemplateMessages.PopupMenuTemplate_defaultSubmenuName, 0);
        addOption(KEY_ACTION_LABEL, PDETemplateMessages.PopupMenuTemplate_actionLabel, PDETemplateMessages.PopupMenuTemplate_defaultActionName, 0);
        addOption(KEY_PACKAGE_NAME, PDETemplateMessages.PopupMenuTemplate_packageName, (String) null, 0);
        addOption(KEY_ACTION_CLASS, PDETemplateMessages.PopupMenuTemplate_actionClass, PDETemplateMessages.PopupMenuTemplate_newAction, 0);
        addOption(KEY_SELECTION, PDETemplateMessages.PopupMenuTemplate_enabledFor, new String[][] { //$NON-NLS-1$
        { "singleSelection", PDETemplateMessages.PopupMenuTemplate_singleSelection }, { "multipleSelection", //$NON-NLS-1$
        PDETemplateMessages.PopupMenuTemplate_multipleSelection } }, //$NON-NLS-1$
        "singleSelection", //$NON-NLS-1$
        0);
    }

    /**
	 * @see PDETemplateSection#getSectionId()
	 */
    @Override
    public String getSectionId() {
        //$NON-NLS-1$
        return "popupMenus";
    }

    @Override
    public boolean isDependentOnParentWizard() {
        return true;
    }

    @Override
    protected void initializeFields(IFieldData data) {
        // In a new project wizard, we don't know this yet - the
        // model has not been created
        String id = data.getId();
        initializeOption(KEY_PACKAGE_NAME, getFormattedPackageName(id));
    }

    @Override
    public void initializeFields(IPluginModelBase model) {
        // In the new extension wizard, the model exists so
        // we can initialize directly from it
        String pluginId = model.getPluginBase().getId();
        initializeOption(KEY_PACKAGE_NAME, getFormattedPackageName(pluginId));
    }

    /**
	 * @see AbstractTemplateSection#updateModel(IProgressMonitor)
	 */
    @Override
    protected void updateModel(IProgressMonitor monitor) throws CoreException {
        IPluginBase plugin = model.getPluginBase();
        IPluginExtension extension = createExtension(getUsedExtensionPoint(), true);
        IPluginModelFactory factory = model.getPluginFactory();
        IPluginElement objectContributionElement = factory.createElement(extension);
        //$NON-NLS-1$
        objectContributionElement.setName("objectContribution");
        //$NON-NLS-1$
        objectContributionElement.setAttribute(//$NON-NLS-1$
        "objectClass", getStringOption(KEY_TARGET_OBJECT));
        //$NON-NLS-1$
        objectContributionElement.setAttribute(//$NON-NLS-1$
        "id", //$NON-NLS-1$
        model.getPluginBase().getId() + ".contribution1");
        IPluginElement menuElement = factory.createElement(objectContributionElement);
        //$NON-NLS-1$
        menuElement.setName("menu");
        //$NON-NLS-1$
        menuElement.setAttribute("label", getStringOption(KEY_SUBMENU_LABEL));
        //$NON-NLS-1$ //$NON-NLS-2$
        menuElement.setAttribute("path", "additions");
        //$NON-NLS-1$ //$NON-NLS-2$
        menuElement.setAttribute("id", model.getPluginBase().getId() + ".menu1");
        IPluginElement separatorElement = factory.createElement(menuElement);
        //$NON-NLS-1$
        separatorElement.setName("separator");
        //$NON-NLS-1$ //$NON-NLS-2$
        separatorElement.setAttribute("name", "group1");
        menuElement.add(separatorElement);
        objectContributionElement.add(menuElement);
        IPluginElement actionElement = factory.createElement(objectContributionElement);
        //$NON-NLS-1$
        actionElement.setName("action");
        //$NON-NLS-1$
        actionElement.setAttribute("label", getStringOption(KEY_ACTION_LABEL));
        //$NON-NLS-1$
        actionElement.setAttribute(//$NON-NLS-1$
        "class", //$NON-NLS-1$
        getStringOption(KEY_PACKAGE_NAME) + "." + getStringOption(KEY_ACTION_CLASS));
        //$NON-NLS-1$
        actionElement.setAttribute(//$NON-NLS-1$
        "menubarPath", //$NON-NLS-1$
        model.getPluginBase().getId() + ".menu1/group1");
        //$NON-NLS-1$
        actionElement.setAttribute(//$NON-NLS-1$
        "enablesFor", getValue(KEY_SELECTION).toString().equals("singleSelection") ? //$NON-NLS-1$
        "1" : //$NON-NLS-1$
        "multiple");
        //$NON-NLS-1$ //$NON-NLS-2$
        actionElement.setAttribute("id", model.getPluginBase().getId() + ".newAction");
        objectContributionElement.add(actionElement);
        extension.add(objectContributionElement);
        if (!extension.isInTheModel())
            plugin.add(extension);
    }

    /**
	 * @see ITemplateSection#getUsedExtensionPoint()
	 */
    @Override
    public String getUsedExtensionPoint() {
        //$NON-NLS-1$
        return "org.eclipse.ui.popupMenus";
    }

    @Override
    protected String getFormattedPackageName(String id) {
        String packageName = super.getFormattedPackageName(id);
        if (packageName.length() != 0)
            //$NON-NLS-1$
            return packageName + ".popup.actions";
        //$NON-NLS-1$
        return "popup.actions";
    }

    @Override
    public IPluginReference[] getDependencies(String schemaVersion) {
        IPluginReference[] result = new IPluginReference[2];
        //$NON-NLS-1$
        result[0] = new PluginReference("org.eclipse.ui");
        //$NON-NLS-1$
        result[1] = new PluginReference("org.eclipse.core.resources");
        return result;
    }
}
