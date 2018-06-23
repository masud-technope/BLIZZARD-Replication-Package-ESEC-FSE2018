/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Benjamin Cabe <benjamin.cabe@anyware-tech.com> - bug 234376
 *     Lars Vogel <Lars.Vogel@vogella.com> - Bug 473694, 486261
 *******************************************************************************/
package org.eclipse.pde.internal.ui.templates.ide;

import java.util.ArrayList;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.internal.ui.templates.*;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.*;

public class PropertyPageTemplate extends PDETemplateSection {

    //$NON-NLS-1$
    public static final String KEY_CLASSNAME = "className";

    //$NON-NLS-1$
    public static final String KEY_PAGE_NAME = "pageName";

    //$NON-NLS-1$
    public static final String KEY_TARGET_CLASS = "targetClass";

    //$NON-NLS-1$
    public static final String KEY_NAME_FILTER = "nameFilter";

    /**
	 * Constructor for PropertyPageTemplate.
	 */
    public  PropertyPageTemplate() {
        setPageCount(1);
        createOptions();
    }

    @Override
    public void addPages(Wizard wizard) {
        WizardPage page = createPage(0, IHelpContextIds.TEMPLATE_PROPERTY_PAGE);
        page.setTitle(PDETemplateMessages.PropertyPageTemplate_title);
        page.setDescription(PDETemplateMessages.PropertyPageTemplate_desc);
        wizard.addPage(page);
        markPagesAdded();
    }

    private void createOptions() {
        addOption(KEY_PACKAGE_NAME, PDETemplateMessages.PropertyPageTemplate_packageName, (String) null, 0);
        addOption(//$NON-NLS-1$
        KEY_CLASSNAME, //$NON-NLS-1$
        PDETemplateMessages.PropertyPageTemplate_pageClass, //$NON-NLS-1$
        "SamplePropertyPage", 0);
        addOption(KEY_PAGE_NAME, PDETemplateMessages.PropertyPageTemplate_pageName, PDETemplateMessages.PropertyPageTemplate_defaultPageName, 0);
        addOption(//$NON-NLS-1$
        KEY_TARGET_CLASS, //$NON-NLS-1$
        PDETemplateMessages.PropertyPageTemplate_targetClass, //$NON-NLS-1$
        "org.eclipse.core.resources.IFile", 0);
        addOption(//$NON-NLS-1$
        KEY_NAME_FILTER, //$NON-NLS-1$
        PDETemplateMessages.PropertyPageTemplate_nameFilter, //$NON-NLS-1$
        "*.*", 0);
    }

    /**
	 * @see PDETemplateSection#getSectionId()
	 */
    @Override
    public String getSectionId() {
        //$NON-NLS-1$
        return "propertyPages";
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
        IPluginElement pageElement = factory.createElement(extension);
        //$NON-NLS-1$
        pageElement.setName("page");
        //$NON-NLS-1$
        pageElement.setAttribute(//$NON-NLS-1$
        "id", //$NON-NLS-1$
        getStringOption(KEY_PACKAGE_NAME) + ".samplePropertyPage");
        //$NON-NLS-1$
        pageElement.setAttribute("name", getStringOption(KEY_PAGE_NAME));
        if (getTargetVersion() < 3.3) {
            //$NON-NLS-1$
            pageElement.setAttribute("objectClass", getStringOption(KEY_TARGET_CLASS));
        } else {
            IPluginElement enabledWhen = factory.createElement(pageElement);
            pageElement.add(enabledWhen);
            //$NON-NLS-1$
            enabledWhen.setName("enabledWhen");
            IPluginElement instanceOf = factory.createElement(enabledWhen);
            enabledWhen.add(instanceOf);
            //$NON-NLS-1$
            instanceOf.setName("instanceof");
            //$NON-NLS-1$
            instanceOf.setAttribute("value", getStringOption(KEY_TARGET_CLASS));
        }
        //$NON-NLS-1$
        pageElement.setAttribute(//$NON-NLS-1$
        "class", //$NON-NLS-1$
        getStringOption(KEY_PACKAGE_NAME) + "." + getStringOption(KEY_CLASSNAME));
        //$NON-NLS-1$
        pageElement.setAttribute("nameFilter", getStringOption(KEY_NAME_FILTER));
        extension.add(pageElement);
        if (!extension.isInTheModel())
            plugin.add(extension);
    }

    /**
	 * @see ITemplateSection#getUsedExtensionPoint()
	 */
    @Override
    public String getUsedExtensionPoint() {
        //$NON-NLS-1$
        return "org.eclipse.ui.propertyPages";
    }

    @Override
    public IPluginReference[] getDependencies(String schemaVersion) {
        ArrayList<PluginReference> result = new ArrayList();
        //$NON-NLS-1$
        result.add(new PluginReference("org.eclipse.core.resources"));
        if (schemaVersion != null)
            //$NON-NLS-1$
            result.add(new PluginReference("org.eclipse.core.runtime"));
        //$NON-NLS-1$
        result.add(new PluginReference("org.eclipse.ui"));
        return result.toArray(new IPluginReference[result.size()]);
    }

    @Override
    protected String getFormattedPackageName(String id) {
        String packageName = super.getFormattedPackageName(id);
        if (packageName.length() != 0)
            //$NON-NLS-1$
            return packageName + ".properties";
        //$NON-NLS-1$
        return "properties";
    }
}
