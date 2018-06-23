/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
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
import org.eclipse.pde.ui.templates.PluginReference;

public class NewWizardTemplate extends PDETemplateSection {

    public  NewWizardTemplate() {
        setPageCount(1);
        createOptions();
    }

    @Override
    public String getSectionId() {
        //$NON-NLS-1$
        return "newWizard";
    }

    /*
	 * @see ITemplateSection#getNumberOfWorkUnits()
	 */
    @Override
    public int getNumberOfWorkUnits() {
        return super.getNumberOfWorkUnits() + 1;
    }

    private void createOptions() {
        // first page
        addOption(KEY_PACKAGE_NAME, PDETemplateMessages.NewWizardTemplate_packageName, (String) null, 0);
        //$NON-NLS-1$
        addOption("categoryId", PDETemplateMessages.NewWizardTemplate_categoryId, (String) null, 0);
        //$NON-NLS-1$ //$NON-NLS-2$
        addOption("categoryName", PDETemplateMessages.NewWizardTemplate_categoryName, "Sample Wizards", 0);
        //$NON-NLS-1$ //$NON-NLS-2$
        addOption("wizardClassName", PDETemplateMessages.NewWizardTemplate_className, "SampleNewWizard", 0);
        //$NON-NLS-1$ //$NON-NLS-2$
        addOption("wizardPageClassName", PDETemplateMessages.NewWizardTemplate_pageClassName, "SampleNewWizardPage", 0);
        //$NON-NLS-1$
        addOption("wizardName", PDETemplateMessages.NewWizardTemplate_wizardName, PDETemplateMessages.NewWizardTemplate_defaultName, 0);
        //$NON-NLS-1$ //$NON-NLS-2$
        addOption("extension", PDETemplateMessages.NewWizardTemplate_extension, "mpe", 0);
        //$NON-NLS-1$ //$NON-NLS-2$
        addOption("initialFileName", PDETemplateMessages.NewWizardTemplate_fileName, "new_file.mpe", 0);
    }

    @Override
    protected void initializeFields(IFieldData data) {
        // In a new project wizard, we don't know this yet - the
        // model has not been created
        String id = data.getId();
        initializeOption(KEY_PACKAGE_NAME, getFormattedPackageName(id));
        //$NON-NLS-1$
        initializeOption("categoryId", id);
    }

    @Override
    public void initializeFields(IPluginModelBase model) {
        // In the new extension wizard, the model exists so
        // we can initialize directly from it
        String pluginId = model.getPluginBase().getId();
        initializeOption(KEY_PACKAGE_NAME, getFormattedPackageName(pluginId));
        //$NON-NLS-1$
        initializeOption("categoryId", pluginId);
    }

    @Override
    public IPluginReference[] getDependencies(String schemaVersion) {
        ArrayList<PluginReference> result = new ArrayList();
        //$NON-NLS-1$
        result.add(new PluginReference("org.eclipse.core.resources"));
        //$NON-NLS-1$
        result.add(new PluginReference("org.eclipse.ui"));
        if (schemaVersion != null) {
            //$NON-NLS-1$
            result.add(new PluginReference("org.eclipse.ui.ide"));
            //$NON-NLS-1$
            result.add(new PluginReference("org.eclipse.core.runtime"));
        }
        return result.toArray(new IPluginReference[result.size()]);
    }

    @Override
    public boolean isDependentOnParentWizard() {
        return true;
    }

    @Override
    public void addPages(Wizard wizard) {
        WizardPage page = createPage(0, IHelpContextIds.TEMPLATE_NEW_WIZARD);
        page.setTitle(PDETemplateMessages.NewWizardTemplate_title);
        page.setDescription(PDETemplateMessages.NewWizardTemplate_desc);
        wizard.addPage(page);
        markPagesAdded();
    }

    @Override
    public String getUsedExtensionPoint() {
        //$NON-NLS-1$
        return "org.eclipse.ui.newWizards";
    }

    @Override
    protected void updateModel(IProgressMonitor monitor) throws CoreException {
        IPluginBase plugin = model.getPluginBase();
        //$NON-NLS-1$
        IPluginExtension extension = createExtension("org.eclipse.ui.newWizards", true);
        IPluginModelFactory factory = model.getPluginFactory();
        //$NON-NLS-1$
        String cid = getStringOption("categoryId");
        createCategory(extension, cid);
        //$NON-NLS-1$ //$NON-NLS-2$
        String fullClassName = getStringOption(KEY_PACKAGE_NAME) + "." + getStringOption("wizardClassName");
        IPluginElement viewElement = factory.createElement(extension);
        //$NON-NLS-1$
        viewElement.setName("wizard");
        //$NON-NLS-1$
        viewElement.setAttribute("id", fullClassName);
        //$NON-NLS-1$ //$NON-NLS-2$
        viewElement.setAttribute("name", getStringOption("wizardName"));
        //$NON-NLS-1$ //$NON-NLS-2$
        viewElement.setAttribute("icon", "icons/sample.gif");
        //$NON-NLS-1$
        viewElement.setAttribute("class", fullClassName);
        //$NON-NLS-1$
        viewElement.setAttribute("category", cid);
        extension.add(viewElement);
        if (!extension.isInTheModel())
            plugin.add(extension);
    }

    private void createCategory(IPluginExtension extension, String id) throws CoreException {
        IPluginObject[] elements = extension.getChildren();
        for (int i = 0; i < elements.length; i++) {
            IPluginElement element = (IPluginElement) elements[i];
            if (//$NON-NLS-1$
            element.getName().equalsIgnoreCase("category")) {
                IPluginAttribute att = //$NON-NLS-1$
                element.getAttribute(//$NON-NLS-1$
                "id");
                if (att != null) {
                    String cid = att.getValue();
                    if (cid != null && cid.equals(id))
                        return;
                }
            }
        }
        IPluginElement categoryElement = model.getFactory().createElement(extension);
        //$NON-NLS-1$
        categoryElement.setName("category");
        //$NON-NLS-1$ //$NON-NLS-2$
        categoryElement.setAttribute("name", getStringOption("categoryName"));
        //$NON-NLS-1$
        categoryElement.setAttribute("id", id);
        extension.add(categoryElement);
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
            return packageName + ".wizards";
        //$NON-NLS-1$
        return "wizards";
    }
}
