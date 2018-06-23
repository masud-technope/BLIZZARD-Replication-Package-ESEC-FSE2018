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

public class BuilderTemplate extends PDETemplateSection {

    //$NON-NLS-1$
    private static final String KEY_BUILDER_CLASS_NAME = "builderClassName";

    //$NON-NLS-1$
    private static final String KEY_BUILDER_ID = "builderId";

    //$NON-NLS-1$
    private static final String KEY_BUILDER_NAME = "builderName";

    //$NON-NLS-1$
    private static final String KEY_NATURE_CLASS_NAME = "natureClassName";

    //$NON-NLS-1$
    private static final String KEY_NATURE_ID = "natureId";

    //$NON-NLS-1$
    private static final String KEY_NATURE_NAME = "natureName";

    //$NON-NLS-1$
    private static final String KEY_GEN_ACTION = "genAction";

    private BooleanOption actionOption;

    /**
	 * Constructor for BuilderTemplate.
	 */
    public  BuilderTemplate() {
        setPageCount(1);
        createOptions();
    }

    @Override
    public String getSectionId() {
        //$NON-NLS-1$
        return "builder";
    }

    /*
	 * @see ITemplateSection#getNumberOfWorkUnits()
	 */
    @Override
    public int getNumberOfWorkUnits() {
        return super.getNumberOfWorkUnits() + 1;
    }

    private void createOptions() {
        addOption(KEY_PACKAGE_NAME, PDETemplateMessages.BuilderTemplate_packageLabel, (String) null, 0);
        //$NON-NLS-1$
        addOption(KEY_BUILDER_CLASS_NAME, PDETemplateMessages.BuilderTemplate_builderClass, "SampleBuilder", 0);
        //$NON-NLS-1$
        addOption(KEY_BUILDER_ID, PDETemplateMessages.BuilderTemplate_builderId, "sampleBuilder", 0);
        addOption(KEY_BUILDER_NAME, PDETemplateMessages.BuilderTemplate_builderName, PDETemplateMessages.BuilderTemplate_defaultBuilderName, 0);
        //$NON-NLS-1$
        addOption(KEY_NATURE_CLASS_NAME, PDETemplateMessages.BuilderTemplate_natureClass, "SampleNature", 0);
        //$NON-NLS-1$
        addOption(KEY_NATURE_ID, PDETemplateMessages.BuilderTemplate_natureId, "sampleNature", 0);
        addOption(KEY_NATURE_NAME, PDETemplateMessages.BuilderTemplate_natureName, PDETemplateMessages.BuilderTemplate_defaultNatureName, 0);
        actionOption = (BooleanOption) addOption(KEY_GEN_ACTION, PDETemplateMessages.BuilderTemplate_generateCommand, true, 0);
    }

    @Override
    public void addPages(Wizard wizard) {
        WizardPage page = createPage(0, IHelpContextIds.TEMPLATE_BUILDER);
        page.setTitle(PDETemplateMessages.BuilderTemplate_title);
        page.setDescription(PDETemplateMessages.BuilderTemplate_desc);
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
        return "org.eclipse.core.resources.builders";
    }

    @Override
    protected void updateModel(IProgressMonitor monitor) throws CoreException {
        IPluginBase plugin = model.getPluginBase();
        IPluginModelFactory factory = model.getPluginFactory();
        // Builder
        //$NON-NLS-1$
        IPluginExtension extension1 = createExtension("org.eclipse.core.resources.builders", true);
        extension1.setId(getStringOption(KEY_BUILDER_ID));
        extension1.setName(getStringOption(KEY_BUILDER_NAME));
        IPluginElement builder = factory.createElement(extension1);
        //$NON-NLS-1$
        builder.setName("builder");
        //$NON-NLS-1$ //$NON-NLS-2$
        builder.setAttribute("hasNature", "true");
        extension1.add(builder);
        IPluginElement run = factory.createElement(builder);
        //$NON-NLS-1$
        run.setName("run");
        run.setAttribute("class", //$NON-NLS-1$
        getStringOption(KEY_PACKAGE_NAME) + "." + //$NON-NLS-1$
        getStringOption(KEY_BUILDER_CLASS_NAME));
        builder.add(run);
        if (!extension1.isInTheModel())
            plugin.add(extension1);
        // Nature
        //$NON-NLS-1$
        IPluginExtension extension2 = createExtension("org.eclipse.core.resources.natures", true);
        extension2.setId(getStringOption(KEY_NATURE_ID));
        extension2.setName(getStringOption(KEY_NATURE_NAME));
        IPluginElement runtime = factory.createElement(extension2);
        //$NON-NLS-1$
        runtime.setName("runtime");
        extension2.add(runtime);
        IPluginElement run2 = factory.createElement(runtime);
        //$NON-NLS-1$
        run2.setName("run");
        run2.setAttribute("class", //$NON-NLS-1$
        getStringOption(KEY_PACKAGE_NAME) + "." + //$NON-NLS-1$
        getStringOption(KEY_NATURE_CLASS_NAME));
        runtime.add(run2);
        IPluginElement builder2 = factory.createElement(extension2);
        //$NON-NLS-1$
        builder2.setName("builder");
        builder2.setAttribute("id", //$NON-NLS-1$
        model.getPluginBase().getId() + "." + //$NON-NLS-1$
        getStringOption(KEY_BUILDER_ID));
        extension2.add(builder2);
        if (!extension2.isInTheModel())
            plugin.add(extension2);
        // Popup Action
        if (actionOption.isSelected()) {
            //$NON-NLS-1$
            IPluginExtension extension3 = createExtension("org.eclipse.ui.commands", true);
            IPluginElement category = factory.createElement(extension3);
            //$NON-NLS-1$
            category.setName("category");
            category.setAttribute("id", //$NON-NLS-1$
            model.getPluginBase().getId() + "." + getStringOption(KEY_NATURE_ID) + //$NON-NLS-1$ //$NON-NLS-2$
            ".category");
            //$NON-NLS-1$ //$NON-NLS-2$
            category.setAttribute("name", getStringOption(KEY_NATURE_NAME) + " commands");
            extension3.add(category);
            IPluginElement command = factory.createElement(extension3);
            //$NON-NLS-1$
            command.setName("command");
            command.setAttribute("categoryId", //$NON-NLS-1$
            model.getPluginBase().getId() + "." + getStringOption(KEY_NATURE_ID) + //$NON-NLS-1$ //$NON-NLS-2$
            ".category");
            command.setAttribute("defaultHandler", //$NON-NLS-1$
            getStringOption(KEY_PACKAGE_NAME) + ".AddRemove" + getStringOption(KEY_NATURE_CLASS_NAME) + //$NON-NLS-1$ //$NON-NLS-2$
            "Handler");
            command.setAttribute("id", //$NON-NLS-1$
            model.getPluginBase().getId() + ".addRemove" + getStringOption(KEY_NATURE_CLASS_NAME//$NON-NLS-1$
            ));
            //$NON-NLS-1$
            command.setAttribute("name", PDETemplateMessages.BuilderTemplate_commandName + getStringOption(KEY_NATURE_NAME));
            extension3.add(command);
            if (!extension3.isInTheModel())
                plugin.add(extension3);
            //$NON-NLS-1$
            IPluginExtension extension4 = createExtension("org.eclipse.ui.menus", true);
            IPluginElement menuContribution = factory.createElement(extension4);
            //$NON-NLS-1$
            menuContribution.setName("menuContribution");
            //$NON-NLS-1$
            menuContribution.setAttribute(//$NON-NLS-1$
            "locationURI", "popup:org.eclipse.ui.projectConfigure?after=additions");
            extension4.add(menuContribution);
            IPluginElement disableCommand = factory.createElement(menuContribution);
            //$NON-NLS-1$
            disableCommand.setName("command");
            //$NON-NLS-1$
            disableCommand.setAttribute("label", PDETemplateMessages.BuilderTemplate_disableLabel);
            disableCommand.setAttribute("commandId", //$NON-NLS-1$
            model.getPluginBase().getId() + ".addRemove" + getStringOption(KEY_NATURE_CLASS_NAME//$NON-NLS-1$
            ));
            //$NON-NLS-1$ //$NON-NLS-2$
            disableCommand.setAttribute("style", "push");
            menuContribution.add(disableCommand);
            IPluginElement visibleWhen = factory.createElement(disableCommand);
            //$NON-NLS-1$
            visibleWhen.setName("visibleWhen");
            //$NON-NLS-1$ //$NON-NLS-2$
            visibleWhen.setAttribute("checkEnabled", "false");
            disableCommand.add(visibleWhen);
            IPluginElement with = factory.createElement(visibleWhen);
            //$NON-NLS-1$
            with.setName("with");
            //$NON-NLS-1$ //$NON-NLS-2$
            with.setAttribute("variable", "selection");
            visibleWhen.add(with);
            IPluginElement count = factory.createElement(with);
            //$NON-NLS-1$
            count.setName("count");
            //$NON-NLS-1$ //$NON-NLS-2$
            count.setAttribute("value", "1");
            with.add(count);
            IPluginElement iterate = factory.createElement(with);
            //$NON-NLS-1$
            iterate.setName("iterate");
            with.add(iterate);
            IPluginElement adapt = factory.createElement(iterate);
            //$NON-NLS-1$
            adapt.setName("adapt");
            //$NON-NLS-1$ //$NON-NLS-2$
            adapt.setAttribute("type", "org.eclipse.core.resources.IProject");
            iterate.add(adapt);
            IPluginElement test = factory.createElement(adapt);
            //$NON-NLS-1$
            test.setName("test");
            //$NON-NLS-1$ //$NON-NLS-2$
            test.setAttribute("property", "org.eclipse.core.resources.projectNature");
            test.setAttribute("value", //$NON-NLS-1$
            model.getPluginBase().getId() + "." + getStringOption(//$NON-NLS-1$
            KEY_NATURE_ID));
            adapt.add(test);
            IPluginElement enableCommand = factory.createElement(menuContribution);
            //$NON-NLS-1$
            enableCommand.setName("command");
            //$NON-NLS-1$
            enableCommand.setAttribute("label", PDETemplateMessages.BuilderTemplate_enableLabel);
            enableCommand.setAttribute("commandId", //$NON-NLS-1$
            model.getPluginBase().getId() + ".addRemove" + getStringOption(KEY_NATURE_CLASS_NAME//$NON-NLS-1$
            ));
            //$NON-NLS-1$ //$NON-NLS-2$
            enableCommand.setAttribute("style", "push");
            menuContribution.add(enableCommand);
            IPluginElement visibleWhen2 = factory.createElement(enableCommand);
            //$NON-NLS-1$
            visibleWhen2.setName("visibleWhen");
            //$NON-NLS-1$ //$NON-NLS-2$
            visibleWhen2.setAttribute("checkEnabled", "false");
            enableCommand.add(visibleWhen2);
            IPluginElement with2 = factory.createElement(visibleWhen2);
            //$NON-NLS-1$
            with2.setName("with");
            //$NON-NLS-1$ //$NON-NLS-2$
            with2.setAttribute("variable", "selection");
            visibleWhen2.add(with2);
            IPluginElement count2 = factory.createElement(with2);
            //$NON-NLS-1$
            count2.setName("count");
            //$NON-NLS-1$ //$NON-NLS-2$
            count2.setAttribute("value", "1");
            with2.add(count2);
            IPluginElement iterate2 = factory.createElement(with2);
            //$NON-NLS-1$
            iterate2.setName("iterate");
            with2.add(iterate2);
            IPluginElement adapt2 = factory.createElement(iterate2);
            //$NON-NLS-1$
            adapt2.setName("adapt");
            //$NON-NLS-1$ //$NON-NLS-2$
            adapt2.setAttribute("type", "org.eclipse.core.resources.IProject");
            iterate2.add(adapt2);
            IPluginElement not = factory.createElement(adapt2);
            //$NON-NLS-1$
            not.setName("not");
            adapt2.add(not);
            IPluginElement test2 = factory.createElement(not);
            //$NON-NLS-1$
            test2.setName("test");
            //$NON-NLS-1$ //$NON-NLS-2$
            test2.setAttribute("property", "org.eclipse.core.resources.projectNature");
            test2.setAttribute("value", //$NON-NLS-1$
            model.getPluginBase().getId() + "." + getStringOption(//$NON-NLS-1$
            KEY_NATURE_ID));
            not.add(test2);
            if (!extension4.isInTheModel())
                plugin.add(extension4);
        }
        // Marker
        //$NON-NLS-1$
        IPluginExtension extension8 = createExtension("org.eclipse.core.resources.markers", false);
        //$NON-NLS-1$
        extension8.setId("xmlProblem");
        extension8.setName(PDETemplateMessages.BuilderTemplate_markerName);
        IPluginElement superElement = factory.createElement(extension8);
        //$NON-NLS-1$
        superElement.setName("super");
        //$NON-NLS-1$
        superElement.setAttribute(//$NON-NLS-1$
        "type", //$NON-NLS-1$
        "org.eclipse.core.resources.problemmarker");
        extension8.add(superElement);
        IPluginElement persistent = factory.createElement(extension8);
        //$NON-NLS-1$
        persistent.setName("persistent");
        //$NON-NLS-1$ //$NON-NLS-2$
        persistent.setAttribute("value", "true");
        extension8.add(persistent);
        if (!extension8.isInTheModel())
            plugin.add(extension8);
    }

    @Override
    public IPluginReference[] getDependencies(String schemaVersion) {
        ArrayList<PluginReference> result = new ArrayList();
        //$NON-NLS-1$
        result.add(new PluginReference("org.eclipse.core.resources"));
        if (schemaVersion != null)
            //$NON-NLS-1$
            result.add(new PluginReference("org.eclipse.core.runtime"));
        if (actionOption.isSelected())
            //$NON-NLS-1$
            result.add(new PluginReference("org.eclipse.ui"));
        return result.toArray(new IPluginReference[result.size()]);
    }

    @Override
    protected String getFormattedPackageName(String id) {
        String packageName = super.getFormattedPackageName(id);
        if (packageName.length() != 0)
            //$NON-NLS-1$
            return packageName + ".builder";
        //$NON-NLS-1$
        return "builder";
    }

    /**
	 * @see AbstractTemplateSection#isOkToCreateFile(File)
	 */
    @Override
    protected boolean isOkToCreateFile(File sourceFile) {
        String fileName = sourceFile.getName();
        if (//$NON-NLS-1$
        fileName.equals("AddRemove$natureClassName$Handler.java")) {
            return actionOption.isSelected();
        }
        if (//$NON-NLS-1$
        fileName.equals("ToggleNatureAction.java")) {
            return false;
        }
        return true;
    }

    @Override
    public String getLabel() {
        //$NON-NLS-1$
        return getPluginResourceString("newExtension.templates.builder.name");
    }

    @Override
    public String getDescription() {
        //$NON-NLS-1$
        return getPluginResourceString("newExtension.templates.builder.desc");
    }
}
