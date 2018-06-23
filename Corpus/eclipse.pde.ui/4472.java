/*******************************************************************************
 * Copyright (c) 2007, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <Lars.Vogel@vogella.com> - Bug 463272
 *******************************************************************************/
package org.eclipse.pde.internal.ui.templates.ide;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.internal.ui.templates.*;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.PluginReference;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.keys.IBindingService;

public class HelloWorldCmdTemplate extends PDETemplateSection {

    //$NON-NLS-1$
    public static final String KEY_CLASS_NAME = "className";

    //$NON-NLS-1$
    public static final String KEY_MESSAGE = "message";

    //$NON-NLS-1$
    public static final String CLASS_NAME = "SampleHandler";

    /**
	 * Constructor for HelloWorldTemplate.
	 */
    public  HelloWorldCmdTemplate() {
        setPageCount(1);
        createOptions();
    }

    @Override
    public String getSectionId() {
        //$NON-NLS-1$
        return "helloWorldCmd";
    }

    /*
	 * @see ITemplateSection#getNumberOfWorkUnits()
	 */
    @Override
    public int getNumberOfWorkUnits() {
        return super.getNumberOfWorkUnits() + 1;
    }

    private void createOptions() {
        addOption(KEY_PACKAGE_NAME, PDETemplateMessages.HelloWorldCmdTemplate_packageName, (String) null, 0);
        addOption(KEY_CLASS_NAME, PDETemplateMessages.HelloWorldCmdTemplate_className, CLASS_NAME, 0);
        addOption(KEY_MESSAGE, PDETemplateMessages.HelloWorldCmdTemplate_messageText, PDETemplateMessages.HelloWorldCmdTemplate_defaultMessage, 0);
    }

    @Override
    public void addPages(Wizard wizard) {
        WizardPage page = createPage(0, IHelpContextIds.TEMPLATE_HELLO_WORLD);
        page.setTitle(PDETemplateMessages.HelloWorldCmdTemplate_title);
        page.setDescription(PDETemplateMessages.HelloWorldCmdTemplate_desc);
        wizard.addPage(page);
        markPagesAdded();
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

    @Override
    public String getUsedExtensionPoint() {
        //$NON-NLS-1$
        return "org.eclipse.ui.commands";
    }

    @Override
    public IPluginReference[] getDependencies(String schemaVersion) {
        //$NON-NLS-1$
        return new IPluginReference[] { new PluginReference("org.eclipse.ui") };
    }

    @Override
    protected void updateModel(IProgressMonitor monitor) throws CoreException {
        IPluginBase plugin = model.getPluginBase();
        //$NON-NLS-1$
        IPluginExtension commandsExtension = createExtension("org.eclipse.ui.commands", true);
        IPluginModelFactory factory = model.getPluginFactory();
        IPluginElement category = factory.createElement(commandsExtension);
        //$NON-NLS-1$
        category.setName("category");
        //$NON-NLS-1$
        String categoryId = plugin.getId() + ".commands.category";
        //$NON-NLS-1$
        category.setAttribute("id", categoryId);
        //$NON-NLS-1$
        category.setAttribute("name", PDETemplateMessages.HelloWorldCmdTemplate_sampleCategory);
        commandsExtension.add(category);
        IPluginElement command = factory.createElement(commandsExtension);
        //$NON-NLS-1$
        command.setName("command");
        //$NON-NLS-1$
        command.setAttribute("categoryId", categoryId);
        //$NON-NLS-1$
        command.setAttribute(//$NON-NLS-1$
        "name", PDETemplateMessages.HelloWorldCmdTemplate_sampleAction_name);
        //$NON-NLS-1$
        String commandId = plugin.getId() + ".commands.sampleCommand";
        //$NON-NLS-1$
        command.setAttribute("id", commandId);
        commandsExtension.add(command);
        //$NON-NLS-1$
        String fullClassName = getStringOption(KEY_PACKAGE_NAME) + "." + getStringOption(KEY_CLASS_NAME);
        //$NON-NLS-1$
        IPluginExtension handlersExtension = createExtension("org.eclipse.ui.handlers", true);
        IPluginElement handler = factory.createElement(handlersExtension);
        //$NON-NLS-1$
        handler.setName("handler");
        //$NON-NLS-1$
        handler.setAttribute("class", fullClassName);
        //$NON-NLS-1$
        handler.setAttribute("commandId", commandId);
        handlersExtension.add(handler);
        //$NON-NLS-1$
        IPluginExtension bindingsExtension = createExtension("org.eclipse.ui.bindings", true);
        IPluginElement binding = factory.createElement(bindingsExtension);
        //$NON-NLS-1$
        binding.setName("key");
        //$NON-NLS-1$
        binding.setAttribute("commandId", commandId);
        //$NON-NLS-1$
        binding.setAttribute("schemeId", IBindingService.DEFAULT_DEFAULT_ACTIVE_SCHEME_ID);
        //$NON-NLS-1$
        binding.setAttribute("contextId", IContextService.CONTEXT_ID_WINDOW);
        //$NON-NLS-1$ //$NON-NLS-2$
        binding.setAttribute("sequence", "M1+6");
        bindingsExtension.add(binding);
        //$NON-NLS-1$
        IPluginExtension menusExtension = createExtension("org.eclipse.ui.menus", true);
        IPluginElement menuAddition = factory.createElement(menusExtension);
        //$NON-NLS-1$
        menuAddition.setName("menuContribution");
        //$NON-NLS-1$
        menuAddition.setAttribute(//$NON-NLS-1$
        "locationURI", //$NON-NLS-1$
        "menu:org.eclipse.ui.main.menu?after=additions");
        IPluginElement menu = factory.createElement(menuAddition);
        //$NON-NLS-1$
        menu.setName("menu");
        //$NON-NLS-1$
        String menuId = plugin.getId() + ".menus.sampleMenu";
        //$NON-NLS-1$
        menu.setAttribute("id", menuId);
        //$NON-NLS-1$
        menu.setAttribute(//$NON-NLS-1$
        "label", PDETemplateMessages.HelloWorldCmdTemplate_sampleMenu_name);
        //$NON-NLS-1$
        menu.setAttribute(//$NON-NLS-1$
        "mnemonic", PDETemplateMessages.HelloWorldCmdTemplate_sampleMenu_mnemonic);
        IPluginElement menuCommand = factory.createElement(menu);
        //$NON-NLS-1$
        menuCommand.setName("command");
        //$NON-NLS-1$
        menuCommand.setAttribute("commandId", commandId);
        //$NON-NLS-1$ //$NON-NLS-2$
        menuCommand.setAttribute("id", plugin.getId() + ".menus.sampleCommand");
        //$NON-NLS-1$
        menuCommand.setAttribute(//$NON-NLS-1$
        "mnemonic", PDETemplateMessages.HelloWorldCmdTemplate_sampleAction_mnemonic);
        menu.add(menuCommand);
        menuAddition.add(menu);
        menusExtension.add(menuAddition);
        IPluginElement toolbarAddition = factory.createElement(menusExtension);
        //$NON-NLS-1$
        toolbarAddition.setName("menuContribution");
        //$NON-NLS-1$
        toolbarAddition.setAttribute(//$NON-NLS-1$
        "locationURI", //$NON-NLS-1$
        "toolbar:org.eclipse.ui.main.toolbar?after=additions");
        IPluginElement toolbar = factory.createElement(toolbarAddition);
        //$NON-NLS-1$
        toolbar.setName("toolbar");
        //$NON-NLS-1$
        String toolbarId = plugin.getId() + ".toolbars.sampleToolbar";
        //$NON-NLS-1$
        toolbar.setAttribute("id", toolbarId);
        IPluginElement toolbarCommand = factory.createElement(toolbar);
        //$NON-NLS-1$
        toolbarCommand.setName("command");
        //$NON-NLS-1$ //$NON-NLS-2$
        toolbarCommand.setAttribute("id", plugin.getId() + ".toolbars.sampleCommand");
        //$NON-NLS-1$
        toolbarCommand.setAttribute("commandId", commandId);
        //$NON-NLS-1$ //$NON-NLS-2$
        toolbarCommand.setAttribute("icon", "icons/sample.gif");
        //$NON-NLS-1$
        toolbarCommand.setAttribute(//$NON-NLS-1$
        "tooltip", PDETemplateMessages.HelloWorldCmdTemplate_sampleAction_tooltip);
        toolbar.add(toolbarCommand);
        toolbarAddition.add(toolbar);
        menusExtension.add(toolbarAddition);
        if (!commandsExtension.isInTheModel()) {
            plugin.add(commandsExtension);
        }
        if (!handlersExtension.isInTheModel()) {
            plugin.add(handlersExtension);
        }
        if (!bindingsExtension.isInTheModel()) {
            plugin.add(bindingsExtension);
        }
        if (!menusExtension.isInTheModel()) {
            plugin.add(menusExtension);
        }
    }

    @Override
    public String[] getNewFiles() {
        //$NON-NLS-1$
        return new String[] { "icons/" };
    }

    @Override
    protected String getFormattedPackageName(String id) {
        String packageName = super.getFormattedPackageName(id);
        if (packageName.length() != 0)
            //$NON-NLS-1$
            return packageName + ".handlers";
        //$NON-NLS-1$
        return "handlers";
    }
}
