/*******************************************************************************
 * Copyright (c) 2005, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <Lars.Vogel@vogella.com> - Bug 473694, 486261
 *******************************************************************************/
package org.eclipse.pde.internal.ui.templates.rcp;

import java.io.File;
import java.util.ArrayList;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.internal.ui.templates.*;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.AbstractTemplateSection;
import org.eclipse.pde.ui.templates.PluginReference;

public class IntroTemplate extends PDETemplateSection {

    //$NON-NLS-1$
    private static final String DYNAMIC_SELECTED = "dynamic";

    //$NON-NLS-1$
    private static final String STATIC_SELECTED = "static";

    //$NON-NLS-1$
    private static final String KEY_GENERATE_DYNAMIC_CONTENT = "IntroTemplate.generateDynamicContent";

    private String packageName;

    private String introID;

    //$NON-NLS-1$
    private static final String APPLICATION_CLASS = "Application";

    public  IntroTemplate() {
        super();
        setPageCount(1);
        createOptions();
    }

    private void createOptions() {
        addOption(KEY_PRODUCT_NAME, PDETemplateMessages.IntroTemplate_productName, VALUE_PRODUCT_NAME, 0);
        addOption(KEY_GENERATE_DYNAMIC_CONTENT, PDETemplateMessages.IntroTemplate_generate, new String[][] { { STATIC_SELECTED, PDETemplateMessages.IntroTemplate_generateStaticContent }, { DYNAMIC_SELECTED, PDETemplateMessages.IntroTemplate_generateDynamicContent } }, STATIC_SELECTED, 0);
    }

    @Override
    public void addPages(Wizard wizard) {
        WizardPage page = createPage(0, IHelpContextIds.TEMPLATE_INTRO);
        page.setTitle(PDETemplateMessages.IntroTemplate_title);
        page.setDescription(PDETemplateMessages.IntroTemplate_desc);
        wizard.addPage(page);
        markPagesAdded();
    }

    @Override
    public boolean isDependentOnParentWizard() {
        return true;
    }

    @Override
    public String getSectionId() {
        //$NON-NLS-1$
        return "intro";
    }

    @Override
    protected void initializeFields(IFieldData data) {
        // In a new project wizard, we don't know this yet - the
        // model has not been created
        String pluginId = data.getId();
        //$NON-NLS-1$
        initializeOption(KEY_PACKAGE_NAME, getFormattedPackageName(pluginId) + ".intro");
        //$NON-NLS-1$
        packageName = getFormattedPackageName(pluginId) + ".intro";
        //$NON-NLS-1$
        introID = getFormattedPackageName(pluginId) + ".intro";
    }

    @Override
    public void initializeFields(IPluginModelBase model) {
        // In the new extension wizard, the model exists so
        // we can initialize directly from it
        String pluginId = model.getPluginBase().getId();
        //$NON-NLS-1$
        initializeOption(KEY_PACKAGE_NAME, getFormattedPackageName(pluginId) + ".intro");
        //$NON-NLS-1$
        packageName = getFormattedPackageName(pluginId) + ".intro";
        //$NON-NLS-1$
        introID = getFormattedPackageName(pluginId) + ".intro";
    }

    @Override
    protected void updateModel(IProgressMonitor monitor) throws CoreException {
        IPluginBase plugin = model.getPluginBase();
        IPluginModelFactory factory = model.getPluginFactory();
        // org.eclipse.core.runtime.applications
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
        run.setAttribute("class", getStringOption(KEY_PACKAGE_NAME) + "." + APPLICATION_CLASS);
        element.add(run);
        if (!extension.isInTheModel())
            plugin.add(extension);
        // org.eclipse.ui.perspectives
        //$NON-NLS-1$
        IPluginExtension perspectivesExtension = createExtension("org.eclipse.ui.perspectives", true);
        IPluginElement perspectiveElement = model.getPluginFactory().createElement(perspectivesExtension);
        //$NON-NLS-1$
        perspectiveElement.setName("perspective");
        //$NON-NLS-1$ //$NON-NLS-2$
        perspectiveElement.setAttribute("class", getStringOption(KEY_PACKAGE_NAME) + ".Perspective");
        //$NON-NLS-1$
        perspectiveElement.setAttribute("name", VALUE_PERSPECTIVE_NAME);
        //$NON-NLS-1$ //$NON-NLS-2$
        perspectiveElement.setAttribute("id", plugin.getId() + ".perspective");
        perspectivesExtension.add(perspectiveElement);
        if (!perspectivesExtension.isInTheModel())
            plugin.add(perspectivesExtension);
        createProductExtension();
        // org.eclipse.ui.intro
        //$NON-NLS-1$
        IPluginExtension extension2 = createExtension("org.eclipse.ui.intro", true);
        IPluginElement introElement = factory.createElement(extension2);
        //$NON-NLS-1$
        introElement.setName("intro");
        //$NON-NLS-1$
        introElement.setAttribute("id", introID);
        //$NON-NLS-1$
        introElement.setAttribute(//$NON-NLS-1$
        "class", //$NON-NLS-1$
        "org.eclipse.ui.intro.config.CustomizableIntroPart");
        extension2.add(introElement);
        IPluginElement introProductBindingElement = factory.createElement(extension2);
        //$NON-NLS-1$
        introProductBindingElement.setName("introProductBinding");
        //$NON-NLS-1$
        introProductBindingElement.setAttribute("introId", introID);
        introProductBindingElement.setAttribute("productId", //$NON-NLS-1$
        plugin.getId() + '.' + VALUE_PRODUCT_ID);
        extension2.add(introProductBindingElement);
        if (!extension2.isInTheModel())
            plugin.add(extension2);
        // org.eclipse.ui.intro.config
        //$NON-NLS-1$
        IPluginExtension extension3 = createExtension("org.eclipse.ui.intro.config", true);
        IPluginElement configurationElement = factory.createElement(extension3);
        //$NON-NLS-1$
        configurationElement.setName("config");
        configurationElement.setAttribute("id", //$NON-NLS-1$
        plugin.getId() + '.' + //$NON-NLS-1$
        "configId");
        //$NON-NLS-1$
        configurationElement.setAttribute("introId", introID);
        //$NON-NLS-1$ //$NON-NLS-2$
        configurationElement.setAttribute("content", "introContent.xml");
        IPluginElement presentationElement = factory.createElement(configurationElement);
        //$NON-NLS-1$
        presentationElement.setName("presentation");
        //$NON-NLS-1$ //$NON-NLS-2$
        presentationElement.setAttribute("home-page-id", "root");
        IPluginElement implementationElement = factory.createElement(presentationElement);
        //$NON-NLS-1$
        implementationElement.setName("implementation");
        //$NON-NLS-1$ //$NON-NLS-2$
        implementationElement.setAttribute("os", "win32,linux,macosx");
        if (getTargetVersion() == 3.0)
            //$NON-NLS-1$//$NON-NLS-2$
            implementationElement.setAttribute("style", "content/shared.css");
        //$NON-NLS-1$ //$NON-NLS-2$
        implementationElement.setAttribute("kind", "html");
        presentationElement.add(implementationElement);
        configurationElement.add(presentationElement);
        extension3.add(configurationElement);
        if (!extension3.isInTheModel())
            plugin.add(extension3);
        // org.eclipse.ui.intro.configExtension
        if (getValue(KEY_GENERATE_DYNAMIC_CONTENT).toString().equals(DYNAMIC_SELECTED)) {
            //$NON-NLS-1$
            IPluginExtension extension4 = createExtension("org.eclipse.ui.intro.configExtension", true);
            IPluginElement configExtensionElement = factory.createElement(extension4);
            //$NON-NLS-1$
            configExtensionElement.setName("configExtension");
            //$NON-NLS-1$ //$NON-NLS-2$
            configExtensionElement.setAttribute("configId", plugin.getId() + '.' + "configId");
            //$NON-NLS-1$ //$NON-NLS-2$
            configExtensionElement.setAttribute("content", "ext.xml");
            extension4.add(configExtensionElement);
            if (!extension4.isInTheModel())
                plugin.add(extension4);
        }
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
    protected boolean isOkToCreateFolder(File sourceFolder) {
        return true;
    }

    /**
	 * @see AbstractTemplateSection#isOkToCreateFile(File)
	 */
    @Override
    protected boolean isOkToCreateFile(File sourceFile) {
        if (getValue(KEY_GENERATE_DYNAMIC_CONTENT).toString().equals(STATIC_SELECTED) && (//$NON-NLS-1$
        sourceFile.getName().equals("DynamicContentProvider.java") || //$NON-NLS-1$
        sourceFile.getName().equals("concept3.xhtml") || //$NON-NLS-1$
        sourceFile.getName().equals("extContent.xhtml") || //$NON-NLS-1$
        sourceFile.getName().equals("ext.xml"))) {
            return false;
        }
        return true;
    }

    @Override
    public String getUsedExtensionPoint() {
        // need more then one extension point //$NON-NLS-1$
        return "org.eclipse.ui.intro";
    }

    @Override
    public IPluginReference[] getDependencies(String schemaVersion) {
        ArrayList<PluginReference> result = new ArrayList();
        //$NON-NLS-1$
        result.add(new PluginReference("org.eclipse.ui.intro"));
        //$NON-NLS-1$
        result.add(new PluginReference("org.eclipse.core.runtime"));
        //$NON-NLS-1$
        result.add(new PluginReference("org.eclipse.ui"));
        if (getValue(KEY_GENERATE_DYNAMIC_CONTENT).toString().equals(DYNAMIC_SELECTED)) {
            //$NON-NLS-1$
            result.add(new PluginReference("org.eclipse.ui.forms"));
            //$NON-NLS-1$
            result.add(new PluginReference("org.eclipse.swt"));
        }
        return result.toArray(new IPluginReference[result.size()]);
    }

    @Override
    public int getNumberOfWorkUnits() {
        return super.getNumberOfWorkUnits() + 1;
    }

    @Override
    public Object getValue(String valueName) {
        if (valueName.equals(KEY_PACKAGE_NAME)) {
            return packageName;
        }
        return super.getValue(valueName);
    }

    @Override
    public String getStringOption(String name) {
        if (name.equals(KEY_PACKAGE_NAME)) {
            return packageName;
        }
        return super.getStringOption(name);
    }

    @Override
    public String[] getNewFiles() {
        if (getValue(KEY_GENERATE_DYNAMIC_CONTENT).toString().equals(STATIC_SELECTED)) {
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            return new String[] { "icons/", "content/", "splash.bmp", "introContent.xml" };
        }
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        return new String[] { "icons/", "content/", "splash.bmp", "introContent.xml", "ext.xml" };
    }

    @Override
    protected boolean copyBrandingDirectory() {
        return true;
    }
}
