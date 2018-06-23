/*******************************************************************************
 * Copyright (c) 2005, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <Lars.Vogel@vogella.com> - Bug 486261
 *******************************************************************************/
package org.eclipse.pde.internal.ui.templates.rcp;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.internal.ui.templates.*;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.PluginReference;

public class MailTemplate extends PDETemplateSection {

    //$NON-NLS-1$
    public static final String KEY_WORKBENCH_ADVISOR = "advisor";

    //$NON-NLS-1$
    public static final String KEY_APPLICATION_CLASS = "applicationClass";

    public  MailTemplate() {
        setPageCount(1);
        createOptions();
    }

    @Override
    public void addPages(Wizard wizard) {
        WizardPage page = createPage(0, IHelpContextIds.TEMPLATE_RCP_MAIL);
        page.setTitle(PDETemplateMessages.MailTemplate_title);
        page.setDescription(PDETemplateMessages.MailTemplate_desc);
        wizard.addPage(page);
        markPagesAdded();
    }

    private void createOptions() {
        addOption(KEY_PRODUCT_NAME, PDETemplateMessages.MailTemplate_productName, VALUE_PRODUCT_NAME, 0);
        addOption(//
        KEY_PACKAGE_NAME, //
        PDETemplateMessages.MailTemplate_packageName, //
        (String) null, //
        0);
        //$NON-NLS-1$
        addOption(KEY_APPLICATION_CLASS, PDETemplateMessages.MailTemplate_appClass, "Application", 0);
    }

    @Override
    protected void initializeFields(IFieldData data) {
        // In a new project wizard, we don't know this yet - the
        // model has not been created
        String packageName = getFormattedPackageName(data.getId());
        initializeOption(KEY_PACKAGE_NAME, packageName);
    }

    @Override
    public void initializeFields(IPluginModelBase model) {
        String packageName = getFormattedPackageName(model.getPluginBase().getId());
        initializeOption(KEY_PACKAGE_NAME, packageName);
    }

    @Override
    public String getSectionId() {
        //$NON-NLS-1$
        return "mail";
    }

    @Override
    protected void updateModel(IProgressMonitor monitor) throws CoreException {
        createApplicationExtension();
        createPerspectiveExtension();
        createViewExtension();
        if (getTargetVersion() >= 3.1) {
            createCommandExtension(false);
            createBindingsExtension();
        } else {
            createCommandExtension(true);
        }
        createProductExtension();
    }

    private void createApplicationExtension() throws CoreException {
        IPluginBase plugin = model.getPluginBase();
        //$NON-NLS-1$
        IPluginExtension extension = createExtension("org.eclipse.core.runtime.applications", true);
        extension.setId(VALUE_APPLICATION_ID);
        IPluginElement element = model.getPluginFactory().createElement(extension);
        //$NON-NLS-1$
        element.setName("application");
        extension.add(element);
        IPluginElement run = model.getPluginFactory().createElement(element);
        //$NON-NLS-1$
        run.setName("run");
        //$NON-NLS-1$ //$NON-NLS-2$
        run.setAttribute("class", getStringOption(KEY_PACKAGE_NAME) + "." + getStringOption(KEY_APPLICATION_CLASS));
        element.add(run);
        if (!extension.isInTheModel())
            plugin.add(extension);
    }

    private void createPerspectiveExtension() throws CoreException {
        IPluginBase plugin = model.getPluginBase();
        //$NON-NLS-1$
        IPluginExtension extension = createExtension("org.eclipse.ui.perspectives", true);
        IPluginElement element = model.getPluginFactory().createElement(extension);
        //$NON-NLS-1$
        element.setName("perspective");
        //$NON-NLS-1$ //$NON-NLS-2$
        element.setAttribute("class", getStringOption(KEY_PACKAGE_NAME) + ".Perspective");
        //$NON-NLS-1$
        element.setAttribute("name", VALUE_PERSPECTIVE_NAME);
        //$NON-NLS-1$ //$NON-NLS-2$
        element.setAttribute("id", plugin.getId() + ".perspective");
        extension.add(element);
        if (!extension.isInTheModel())
            plugin.add(extension);
    }

    private void createViewExtension() throws CoreException {
        IPluginBase plugin = model.getPluginBase();
        String id = plugin.getId();
        //$NON-NLS-1$
        IPluginExtension extension = createExtension("org.eclipse.ui.views", true);
        IPluginElement view = model.getPluginFactory().createElement(extension);
        //$NON-NLS-1$
        view.setName("view");
        //$NON-NLS-1$ //$NON-NLS-2$
        view.setAttribute("allowMultiple", "true");
        //$NON-NLS-1$ //$NON-NLS-2$
        view.setAttribute("icon", "icons/sample2.gif");
        //$NON-NLS-1$ //$NON-NLS-2$
        view.setAttribute("class", getStringOption(KEY_PACKAGE_NAME) + ".View");
        //$NON-NLS-1$ //$NON-NLS-2$
        view.setAttribute("name", "Message");
        //$NON-NLS-1$ //$NON-NLS-2$
        view.setAttribute("id", id + ".view");
        extension.add(view);
        view = model.getPluginFactory().createElement(extension);
        //$NON-NLS-1$
        view.setName("view");
        //$NON-NLS-1$ //$NON-NLS-2$
        view.setAttribute("allowMultiple", "true");
        //$NON-NLS-1$ //$NON-NLS-2$
        view.setAttribute("icon", "icons/sample3.gif");
        //$NON-NLS-1$ //$NON-NLS-2$
        view.setAttribute("class", getStringOption(KEY_PACKAGE_NAME) + ".NavigationView");
        //$NON-NLS-1$ //$NON-NLS-2$
        view.setAttribute("name", "Mailboxes");
        //$NON-NLS-1$ //$NON-NLS-2$
        view.setAttribute("id", id + ".navigationView");
        extension.add(view);
        if (!extension.isInTheModel())
            plugin.add(extension);
    }

    private void createCommandExtension(boolean generateKeyBindings) throws CoreException {
        IPluginBase plugin = model.getPluginBase();
        String id = plugin.getId();
        //$NON-NLS-1$
        IPluginExtension extension = createExtension("org.eclipse.ui.commands", true);
        IPluginElement element = model.getPluginFactory().createElement(extension);
        //$NON-NLS-1$
        element.setName("category");
        //$NON-NLS-1$ //$NON-NLS-2$
        element.setAttribute("id", id + ".category");
        //$NON-NLS-1$ //$NON-NLS-2$
        element.setAttribute("name", "Mail");
        extension.add(element);
        element = model.getPluginFactory().createElement(extension);
        //$NON-NLS-1$
        element.setName("command");
        //$NON-NLS-1$ //$NON-NLS-2$
        element.setAttribute("description", "Opens a mailbox");
        //$NON-NLS-1$ //$NON-NLS-2$
        element.setAttribute("name", "Open Mailbox");
        //$NON-NLS-1$ //$NON-NLS-2$
        element.setAttribute("id", id + ".open");
        //$NON-NLS-1$ //$NON-NLS-2$
        element.setAttribute("categoryId", id + ".category");
        extension.add(element);
        element = model.getPluginFactory().createElement(extension);
        //$NON-NLS-1$
        element.setName("command");
        //$NON-NLS-1$ //$NON-NLS-2$
        element.setAttribute("description", "Open a message dialog");
        //$NON-NLS-1$ //$NON-NLS-2$
        element.setAttribute("name", "Open Message Dialog");
        //$NON-NLS-1$ //$NON-NLS-2$
        element.setAttribute("id", id + ".openMessage");
        //$NON-NLS-1$ //$NON-NLS-2$
        element.setAttribute("categoryId", id + ".category");
        extension.add(element);
        if (generateKeyBindings) {
            element = model.getPluginFactory().createElement(extension);
            //$NON-NLS-1$
            element.setName("keyConfiguration");
            //$NON-NLS-1$ //$NON-NLS-2$
            element.setAttribute("description", "The key configuration for this sample");
            //$NON-NLS-1$ //$NON-NLS-2$
            element.setAttribute("name", id + ".keyConfiguration");
            //$NON-NLS-1$ //$NON-NLS-2$
            element.setAttribute("id", id + ".keyConfiguration");
            extension.add(element);
            element = model.getPluginFactory().createElement(extension);
            //$NON-NLS-1$
            element.setName("keyBinding");
            //$NON-NLS-1$ //$NON-NLS-2$
            element.setAttribute("commandId", id + ".open");
            //$NON-NLS-1$ //$NON-NLS-2$
            element.setAttribute("keySequence", "CTRL+SHIFT+O");
            //$NON-NLS-1$ //$NON-NLS-2$
            element.setAttribute("keyConfigurationId", "org.eclipse.ui.defaultAcceleratorConfiguration");
            extension.add(element);
            element = model.getPluginFactory().createElement(extension);
            //$NON-NLS-1$
            element.setName("keyBinding");
            //$NON-NLS-1$ //$NON-NLS-2$
            element.setAttribute("commandId", id + ".openMessage");
            //$NON-NLS-1$ //$NON-NLS-2$
            element.setAttribute("keySequence", "CTRL+SHIFT+M");
            //$NON-NLS-1$ //$NON-NLS-2$
            element.setAttribute("keyConfigurationId", "org.eclipse.ui.defaultAcceleratorConfiguration");
            extension.add(element);
            element = model.getPluginFactory().createElement(extension);
            //$NON-NLS-1$
            element.setName("keyBinding");
            //$NON-NLS-1$ //$NON-NLS-2$
            element.setAttribute("commandId", "org.eclipse.ui.file.exit");
            //$NON-NLS-1$ //$NON-NLS-2$
            element.setAttribute("keySequence", "CTRL+SHIFT+Q");
            //$NON-NLS-1$ //$NON-NLS-2$
            element.setAttribute("keyConfigurationId", "org.eclipse.ui.defaultAcceleratorConfiguration");
            extension.add(element);
        }
        if (!extension.isInTheModel())
            plugin.add(extension);
    }

    private void createBindingsExtension() throws CoreException {
        IPluginBase plugin = model.getPluginBase();
        String id = plugin.getId();
        //$NON-NLS-1$
        IPluginExtension extension = createExtension("org.eclipse.ui.bindings", true);
        IPluginElement element = model.getPluginFactory().createElement(extension);
        //$NON-NLS-1$
        element.setName("key");
        //$NON-NLS-1$ //$NON-NLS-2$
        element.setAttribute("commandId", id + ".open");
        //$NON-NLS-1$ //$NON-NLS-2$
        element.setAttribute("sequence", "CTRL+SHIFT+O");
        //$NON-NLS-1$ //$NON-NLS-2$
        element.setAttribute("schemeId", "org.eclipse.ui.defaultAcceleratorConfiguration");
        extension.add(element);
        element = model.getPluginFactory().createElement(extension);
        //$NON-NLS-1$
        element.setName("key");
        //$NON-NLS-1$ //$NON-NLS-2$
        element.setAttribute("commandId", id + ".openMessage");
        //$NON-NLS-1$ //$NON-NLS-2$
        element.setAttribute("sequence", "CTRL+SHIFT+M");
        //$NON-NLS-1$ //$NON-NLS-2$
        element.setAttribute("schemeId", "org.eclipse.ui.defaultAcceleratorConfiguration");
        extension.add(element);
        element = model.getPluginFactory().createElement(extension);
        //$NON-NLS-1$
        element.setName("key");
        //$NON-NLS-1$ //$NON-NLS-2$
        element.setAttribute("commandId", "org.eclipse.ui.file.exit");
        //$NON-NLS-1$ //$NON-NLS-2$
        element.setAttribute("sequence", "CTRL+SHIFT+Q");
        //$NON-NLS-1$ //$NON-NLS-2$
        element.setAttribute("schemeId", "org.eclipse.ui.defaultAcceleratorConfiguration");
        extension.add(element);
        if (!extension.isInTheModel())
            plugin.add(extension);
    }

    private void createProductExtension() throws CoreException {
        IPluginBase plugin = model.getPluginBase();
        //$NON-NLS-1$
        IPluginExtension extension = createExtension("org.eclipse.core.runtime.products", true);
        extension.setId(VALUE_PRODUCT_ID);
        IPluginElement element = model.getFactory().createElement(extension);
        //$NON-NLS-1$
        element.setName("product");
        //$NON-NLS-1$
        element.setAttribute("name", getStringOption(KEY_PRODUCT_NAME));
        //$NON-NLS-1$ //$NON-NLS-2$
        element.setAttribute("application", plugin.getId() + "." + VALUE_APPLICATION_ID);
        IPluginElement property = model.getFactory().createElement(element);
        //$NON-NLS-1$
        property.setName("property");
        //$NON-NLS-1$ //$NON-NLS-2$
        property.setAttribute("name", "aboutText");
        //$NON-NLS-1$ //$NON-NLS-2$
        property.setAttribute("value", "RCP Mail template created by PDE");
        element.add(property);
        property = model.getFactory().createElement(element);
        //$NON-NLS-1$
        property.setName("property");
        //$NON-NLS-1$ //$NON-NLS-2$
        property.setAttribute("name", "windowImages");
        //$NON-NLS-1$ //$NON-NLS-2$
        property.setAttribute("value", "icons/sample2.gif");
        element.add(property);
        property = model.getFactory().createElement(element);
        //$NON-NLS-1$
        property.setName("property");
        //$NON-NLS-1$ //$NON-NLS-2$
        property.setAttribute("name", "aboutImage");
        //$NON-NLS-1$ //$NON-NLS-2$
        property.setAttribute("value", "product_lg.gif");
        element.add(property);
        extension.add(element);
        if (!extension.isInTheModel()) {
            plugin.add(extension);
        }
    }

    @Override
    public String getUsedExtensionPoint() {
        return null;
    }

    @Override
    public boolean isDependentOnParentWizard() {
        return true;
    }

    @Override
    public int getNumberOfWorkUnits() {
        return super.getNumberOfWorkUnits() + 1;
    }

    @Override
    public IPluginReference[] getDependencies(String schemaVersion) {
        IPluginReference[] dep = new IPluginReference[2];
        //$NON-NLS-1$
        dep[0] = new PluginReference("org.eclipse.core.runtime");
        //$NON-NLS-1$
        dep[1] = new PluginReference("org.eclipse.ui");
        return dep;
    }

    @Override
    public String[] getNewFiles() {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return new String[] { "icons/", "product_lg.gif", "splash.bmp" };
    }

    @Override
    protected boolean copyBrandingDirectory() {
        return true;
    }
}
