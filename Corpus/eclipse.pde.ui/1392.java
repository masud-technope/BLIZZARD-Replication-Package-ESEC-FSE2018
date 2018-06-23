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

public class HelloRCPTemplate extends PDETemplateSection {

    //$NON-NLS-1$
    public static final String KEY_APPLICATION_CLASS = "applicationClass";

    //$NON-NLS-1$
    public static final String KEY_WINDOW_TITLE = "windowTitle";

    public  HelloRCPTemplate() {
        setPageCount(1);
        createOptions();
    }

    @Override
    public void addPages(Wizard wizard) {
        WizardPage page = createPage(0, IHelpContextIds.TEMPLATE_RCP_MAIL);
        page.setTitle(PDETemplateMessages.HelloRCPTemplate_title);
        page.setDescription(PDETemplateMessages.HelloRCPTemplate_desc);
        wizard.addPage(page);
        markPagesAdded();
    }

    private void createOptions() {
        //$NON-NLS-1$
        addOption(KEY_WINDOW_TITLE, PDETemplateMessages.HelloRCPTemplate_windowTitle, "Hello RCP", 0);
        addOption(KEY_PACKAGE_NAME, PDETemplateMessages.MailTemplate_packageName, (String) null, 0);
        //$NON-NLS-1$
        addOption(KEY_APPLICATION_CLASS, PDETemplateMessages.HelloRCPTemplate_appClass, "Application", 0);
        createBrandingOptions();
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
        return "helloRCP";
    }

    @Override
    protected void updateModel(IProgressMonitor monitor) throws CoreException {
        createApplicationExtension();
        createPerspectiveExtension();
        if (getBooleanOption(KEY_PRODUCT_BRANDING))
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

    private void createProductExtension() throws CoreException {
        IPluginBase plugin = model.getPluginBase();
        //$NON-NLS-1$
        IPluginExtension extension = createExtension("org.eclipse.core.runtime.products", true);
        extension.setId(VALUE_PRODUCT_ID);
        IPluginElement element = model.getFactory().createElement(extension);
        //$NON-NLS-1$
        element.setName("product");
        //$NON-NLS-1$
        element.setAttribute("name", getStringOption(KEY_WINDOW_TITLE));
        //$NON-NLS-1$ //$NON-NLS-2$
        element.setAttribute("application", plugin.getId() + "." + VALUE_APPLICATION_ID);
        IPluginElement property = model.getFactory().createElement(element);
        property = model.getFactory().createElement(element);
        //$NON-NLS-1$
        property.setName("property");
        //$NON-NLS-1$ //$NON-NLS-2$
        property.setAttribute("name", "windowImages");
        //$NON-NLS-1$
        property.setAttribute(//$NON-NLS-1$
        "value", //$NON-NLS-1$
        "icons/eclipse16.png,icons/eclipse32.png,icons/eclipse48.png,icons/eclipse64.png, icons/eclipse128.png,icons/eclipse256.png,icons/eclipse512.png");
        element.add(property);
        extension.add(element);
        if (!extension.isInTheModel())
            plugin.add(extension);
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
        if (copyBrandingDirectory())
            //$NON-NLS-1$ //$NON-NLS-2$
            return new String[] { "icons/", "splash.bmp" };
        return super.getNewFiles();
    }
}
