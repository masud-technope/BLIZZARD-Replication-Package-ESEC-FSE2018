/*******************************************************************************
 *  Copyright (c) 2000, 2016 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <Lars.Vogel@vogella.com> - Bug 473694, 486261
 *******************************************************************************/
package org.eclipse.pde.internal.ui.templates.ide;

import java.io.File;
import java.util.ArrayList;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.internal.ui.templates.*;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.*;

public class ViewTemplate extends PDETemplateSection {

    private BooleanOption addToPerspective;

    private BooleanOption contextHelp;

    /**
	 * Constructor for HelloWorldTemplate.
	 */
    public  ViewTemplate() {
        setPageCount(1);
        createOptions();
    }

    @Override
    public String getSectionId() {
        //$NON-NLS-1$
        return "view";
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
        addOption(KEY_PACKAGE_NAME, PDETemplateMessages.ViewTemplate_packageName, (String) null, 0);
        //$NON-NLS-1$ //$NON-NLS-2$
        addOption("className", PDETemplateMessages.ViewTemplate_className, "SampleView", 0);
        //$NON-NLS-1$
        addOption("viewName", PDETemplateMessages.ViewTemplate_name, PDETemplateMessages.ViewTemplate_defaultName, 0);
        //$NON-NLS-1$
        addOption("viewCategoryId", PDETemplateMessages.ViewTemplate_categoryId, (String) null, 0);
        //$NON-NLS-1$
        addOption("viewCategoryName", PDETemplateMessages.ViewTemplate_categoryName, PDETemplateMessages.ViewTemplate_defaultCategoryName, 0);
        addOption(//$NON-NLS-1$
        "viewType", //$NON-NLS-1$
        PDETemplateMessages.ViewTemplate_select, new String[][] { { //$NON-NLS-1$
        "tableViewer", //$NON-NLS-1$
        PDETemplateMessages.ViewTemplate_table }, { "treeViewer", //$NON-NLS-1$
        PDETemplateMessages.ViewTemplate_tree } }, //$NON-NLS-1$
        "tableViewer", //$NON-NLS-1$
        0);
        //$NON-NLS-1$
        addOption("addViewID", PDETemplateMessages.ViewTemplate_addViewID, true, 0);
        //$NON-NLS-1$
        addToPerspective = (BooleanOption) addOption("addToPerspective", PDETemplateMessages.ViewTemplate_addToPerspective, true, 0);
        //$NON-NLS-1$
        contextHelp = (BooleanOption) addOption("contextHelp", PDETemplateMessages.ViewTemplate_contextHelp, true, 0);
    }

    @Override
    protected void initializeFields(IFieldData data) {
        // In a new project wizard, we don't know this yet - the
        // model has not been created
        initializeFields(data.getId());
    }

    @Override
    public void initializeFields(IPluginModelBase model) {
        // In the new extension wizard, the model exists so
        // we can initialize directly from it
        initializeFields(model.getPluginBase().getId());
    }

    public void initializeFields(String id) {
        initializeOption(KEY_PACKAGE_NAME, getFormattedPackageName(id));
        //$NON-NLS-1$
        initializeOption("viewCategoryId", id);
    }

    @Override
    public boolean isDependentOnParentWizard() {
        return true;
    }

    @Override
    public void addPages(Wizard wizard) {
        WizardPage page0 = createPage(0, IHelpContextIds.TEMPLATE_VIEW);
        page0.setTitle(PDETemplateMessages.ViewTemplate_title0);
        page0.setDescription(PDETemplateMessages.ViewTemplate_desc0);
        wizard.addPage(page0);
        markPagesAdded();
    }

    /**
	 * @see AbstractTemplateSection#isOkToCreateFile(File)
	 */
    @Override
    protected boolean isOkToCreateFile(File sourceFile) {
        boolean isOk = true;
        String fileName = sourceFile.getName();
        if (//$NON-NLS-1$
        fileName.equals("contexts.xml")) {
            isOk = contextHelp.isSelected();
        }
        return isOk;
    }

    @Override
    public String getUsedExtensionPoint() {
        //$NON-NLS-1$
        return "org.eclipse.ui.views";
    }

    @Override
    protected void updateModel(IProgressMonitor monitor) throws CoreException {
        IPluginBase plugin = model.getPluginBase();
        //$NON-NLS-1$
        IPluginExtension extension = createExtension("org.eclipse.ui.views", true);
        IPluginModelFactory factory = model.getPluginFactory();
        //$NON-NLS-1$
        String cid = getStringOption("viewCategoryId");
        createCategory(extension, cid);
        //$NON-NLS-1$ //$NON-NLS-2$
        String fullClassName = getStringOption(KEY_PACKAGE_NAME) + "." + getStringOption("className");
        IPluginElement viewElement = factory.createElement(extension);
        //$NON-NLS-1$
        viewElement.setName("view");
        //$NON-NLS-1$
        viewElement.setAttribute("id", fullClassName);
        //$NON-NLS-1$ //$NON-NLS-2$
        viewElement.setAttribute("name", getStringOption("viewName"));
        //$NON-NLS-1$ //$NON-NLS-2$
        viewElement.setAttribute("icon", "icons/sample.gif");
        //$NON-NLS-1$
        viewElement.setAttribute("class", fullClassName);
        //$NON-NLS-1$
        viewElement.setAttribute("category", cid);
        extension.add(viewElement);
        if (!extension.isInTheModel())
            plugin.add(extension);
        if (addToPerspective.isSelected()) {
            //$NON-NLS-1$
            IPluginExtension perspectiveExtension = createExtension("org.eclipse.ui.perspectiveExtensions", true);
            IPluginElement perspectiveElement = factory.createElement(perspectiveExtension);
            //$NON-NLS-1$
            perspectiveElement.setName("perspectiveExtension");
            //$NON-NLS-1$
            perspectiveElement.setAttribute(//$NON-NLS-1$
            "targetID", "org.eclipse.jdt.ui.JavaPerspective");
            IPluginElement view = factory.createElement(perspectiveElement);
            //$NON-NLS-1$
            view.setName("view");
            //$NON-NLS-1$
            view.setAttribute("id", fullClassName);
            //$NON-NLS-1$ //$NON-NLS-2$
            view.setAttribute("relative", "org.eclipse.ui.views.ProblemView");
            //$NON-NLS-1$ //$NON-NLS-2$
            view.setAttribute("relationship", "right");
            //$NON-NLS-1$ //$NON-NLS-2$
            view.setAttribute("ratio", "0.5");
            perspectiveElement.add(view);
            perspectiveExtension.add(perspectiveElement);
            if (!perspectiveExtension.isInTheModel())
                plugin.add(perspectiveExtension);
        }
        if (contextHelp.isSelected()) {
            //$NON-NLS-1$
            IPluginExtension contextExtension = createExtension("org.eclipse.help.contexts", true);
            IPluginElement contextsElement = factory.createElement(contextExtension);
            //$NON-NLS-1$
            contextsElement.setName("contexts");
            //$NON-NLS-1$ //$NON-NLS-2$
            contextsElement.setAttribute("file", "contexts.xml");
            contextExtension.add(contextsElement);
            if (!contextExtension.isInTheModel())
                plugin.add(contextExtension);
        }
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
        categoryElement.setAttribute("name", getStringOption("viewCategoryName"));
        //$NON-NLS-1$
        categoryElement.setAttribute("id", id);
        extension.add(categoryElement);
    }

    @Override
    public String[] getNewFiles() {
        if (contextHelp.isSelected())
            //$NON-NLS-1$ //$NON-NLS-2$
            return new String[] { "icons/", "contexts.xml" };
        //$NON-NLS-1$
        return new String[] { "icons/" };
    }

    @Override
    public IPluginReference[] getDependencies(String schemaVersion) {
        ArrayList<PluginReference> result = new ArrayList();
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
            return packageName + ".views";
        //$NON-NLS-1$
        return "views";
    }

    @Override
    public Object getValue(String name) {
        if (//$NON-NLS-1$
        name.equals("useEnablement"))
            return Boolean.valueOf(getTargetVersion() >= 3.3);
        return super.getValue(name);
    }
}
