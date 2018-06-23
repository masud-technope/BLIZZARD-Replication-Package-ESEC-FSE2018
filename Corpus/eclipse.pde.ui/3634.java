/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars.Vogel <Lars.Vogel@vogella.com> - Bug 486247, 486261
 *******************************************************************************/
package org.eclipse.pde.internal.ui.templates.ide;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.internal.ui.templates.IHelpContextIds;
import org.eclipse.pde.internal.ui.templates.PDETemplateMessages;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.PluginReference;
import org.eclipse.pde.ui.templates.TemplateOption;

public class MultiPageEditorTemplate extends BaseEditorTemplate {

    /**
	 * Constructor for MultiPageEditorTemplate.
	 */
    public  MultiPageEditorTemplate() {
        setPageCount(1);
        createOptions();
    }

    @Override
    public String getSectionId() {
        //$NON-NLS-1$
        return "multiPageEditor";
    }

    @Override
    public IPluginReference[] getDependencies(String schemaVersion) {
        IPluginReference[] dep = new IPluginReference[6];
        //$NON-NLS-1$
        dep[0] = new PluginReference("org.eclipse.jface.text");
        //$NON-NLS-1$
        dep[1] = new PluginReference("org.eclipse.core.resources");
        //$NON-NLS-1$
        dep[2] = new PluginReference("org.eclipse.ui");
        //$NON-NLS-1$
        dep[3] = new PluginReference("org.eclipse.ui.editors");
        //$NON-NLS-1$
        dep[4] = new PluginReference("org.eclipse.ui.ide");
        //$NON-NLS-1$
        dep[5] = new PluginReference("org.eclipse.core.runtime");
        return dep;
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
        addOption(KEY_PACKAGE_NAME, PDETemplateMessages.MultiPageEditorTemplate_packageName, (String) null, 0);
        addOption(//$NON-NLS-1$
        "editorClassName", PDETemplateMessages.MultiPageEditorTemplate_className, "MultiPageEditor", 0);
        addOption(//$NON-NLS-1$
        "contributorClassName", PDETemplateMessages.MultiPageEditorTemplate_contributor, "MultiPageEditorContributor", 0);
        addOption(//$NON-NLS-1$
        "editorName", PDETemplateMessages.MultiPageEditorTemplate_editorName, PDETemplateMessages.MultiPageEditorTemplate_defaultEditorName, 0);
        addOption(//$NON-NLS-1$
        "extensions", //$NON-NLS-1$
        PDETemplateMessages.MultiPageEditorTemplate_extensions, //$NON-NLS-1$
        "mpe", 0);
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
    public boolean isDependentOnParentWizard() {
        return true;
    }

    @Override
    public void addPages(Wizard wizard) {
        WizardPage page = createPage(0, IHelpContextIds.TEMPLATE_MULTIPAGE_EDITOR);
        page.setTitle(PDETemplateMessages.MultiPageEditorTemplate_title);
        page.setDescription(PDETemplateMessages.MultiPageEditorTemplate_desc);
        wizard.addPage(page);
        markPagesAdded();
    }

    @Override
    public void validateOptions(TemplateOption source) {
        if (source.isRequired() && source.isEmpty()) {
            flagMissingRequiredOption(source);
        } else {
            validateContainerPage(source);
        }
    }

    private void validateContainerPage(TemplateOption source) {
        TemplateOption[] allPageOptions = getOptions(0);
        for (int i = 0; i < allPageOptions.length; i++) {
            TemplateOption nextOption = allPageOptions[i];
            if (nextOption.isRequired() && nextOption.isEmpty()) {
                flagMissingRequiredOption(nextOption);
                return;
            }
        }
        resetPageState();
    }

    @Override
    protected void updateModel(IProgressMonitor monitor) throws CoreException {
        IPluginBase plugin = model.getPluginBase();
        //$NON-NLS-1$
        IPluginExtension extension = createExtension("org.eclipse.ui.editors", true);
        IPluginModelFactory factory = model.getPluginFactory();
        //$NON-NLS-1$ //$NON-NLS-2$
        String editorClassName = getStringOption(KEY_PACKAGE_NAME) + "." + getStringOption("editorClassName");
        String contributorClassName = //$NON-NLS-1$
        getStringOption(KEY_PACKAGE_NAME) + "." + getStringOption(//$NON-NLS-1$
        "contributorClassName");
        IPluginElement editorElement = factory.createElement(extension);
        //$NON-NLS-1$
        editorElement.setName("editor");
        //$NON-NLS-1$
        editorElement.setAttribute("id", editorClassName);
        //$NON-NLS-1$ //$NON-NLS-2$
        editorElement.setAttribute("name", getStringOption("editorName"));
        //$NON-NLS-1$ //$NON-NLS-2$
        editorElement.setAttribute("icon", "icons/sample.gif");
        //$NON-NLS-1$ //$NON-NLS-2$
        editorElement.setAttribute("extensions", getStringOption("extensions"));
        //$NON-NLS-1$
        editorElement.setAttribute("class", editorClassName);
        //$NON-NLS-1$
        editorElement.setAttribute("contributorClass", contributorClassName);
        extension.add(editorElement);
        if (!extension.isInTheModel())
            plugin.add(extension);
    }

    @Override
    protected String getFormattedPackageName(String id) {
        String packageName = super.getFormattedPackageName(id);
        if (packageName.length() != 0)
            //$NON-NLS-1$
            return packageName + ".editors";
        //$NON-NLS-1$
        return "editors";
    }
}
