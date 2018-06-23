/*******************************************************************************
 * Copyright (c) 2006, 2016 IBM Corporation and others.
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

import java.io.File;
import java.util.StringTokenizer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.internal.ui.templates.*;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.*;

public class DecoratorTemplate extends PDETemplateSection {

    //$NON-NLS-1$
    public static final String DECORATOR_CLASS_NAME = "decoratorClassName";

    //$NON-NLS-1$
    public static final String DECORATOR_ICON_PLACEMENT = "decoratorPlacement";

    //$NON-NLS-1$
    public static final String DECORATOR_BLN_PROJECT = "decorateProjects";

    //$NON-NLS-1$
    public static final String DECORATOR_BLN_READONLY = "decorateReadOnly";

    private WizardPage page;

    private TemplateOption packageOption;

    private TemplateOption classOption;

    private BooleanOption projectOption;

    private BooleanOption readOnlyOption;

    /**
	 * Constructor for DecoratorTemplate.
	 */
    public  DecoratorTemplate() {
        setPageCount(1);
        createOptions();
        alterOptionStates();
    }

    @Override
    public IPluginReference[] getDependencies(String schemaVersion) {
        // Additional dependency required to decorate resource objects
        IPluginReference[] dep = new IPluginReference[1];
        //$NON-NLS-1$
        dep[0] = new PluginReference("org.eclipse.core.resources");
        return dep;
    }

    @Override
    public String getSectionId() {
        //$NON-NLS-1$
        return "decorator";
    }

    @Override
    public int getNumberOfWorkUnits() {
        return super.getNumberOfWorkUnits() + 1;
    }

    /**
	 * Creates the options to be displayed on the template wizard.
	 * A multiple choice option (radio buttons) and a boolean option
	 * are used.
	 */
    private void createOptions() {
        String[][] choices = fromCommaSeparated(PDETemplateMessages.DecoratorTemplate_placementChoices);
        addOption(DECORATOR_ICON_PLACEMENT, PDETemplateMessages.DecoratorTemplate_placement, choices, choices[0][0], 0);
        projectOption = (BooleanOption) addOption(DECORATOR_BLN_PROJECT, PDETemplateMessages.DecoratorTemplate_decorateProject, true, 0);
        readOnlyOption = (BooleanOption) addOption(DECORATOR_BLN_READONLY, PDETemplateMessages.DecoratorTemplate_decorateReadOnly, false, 0);
        packageOption = addOption(KEY_PACKAGE_NAME, PDETemplateMessages.DecoratorTemplate_packageName, (String) null, 0);
        classOption = addOption(//$NON-NLS-1$
        DECORATOR_CLASS_NAME, //$NON-NLS-1$
        PDETemplateMessages.DecoratorTemplate_decoratorClass, //$NON-NLS-1$
        "ReadOnly", 0);
    }

    @Override
    public void addPages(Wizard wizard) {
        int pageIndex = 0;
        page = createPage(pageIndex, IHelpContextIds.TEMPLATE_EDITOR);
        page.setTitle(PDETemplateMessages.DecoratorTemplate_title);
        page.setDescription(PDETemplateMessages.DecoratorTemplate_desc);
        wizard.addPage(page);
        markPagesAdded();
    }

    private void alterOptionStates() {
        projectOption.setEnabled(!readOnlyOption.isSelected());
        packageOption.setEnabled(!projectOption.isEnabled());
        classOption.setEnabled(!projectOption.isEnabled());
    }

    @Override
    protected boolean isOkToCreateFolder(File sourceFolder) {
        //Define rules for creating folders from the Templates_3.X folders
        boolean isOk = true;
        String folderName = sourceFolder.getName();
        if (//$NON-NLS-1$
        folderName.equals("java")) {
            isOk = readOnlyOption.isEnabled() && readOnlyOption.isSelected();
        }
        return isOk;
    }

    @Override
    protected boolean isOkToCreateFile(File sourceFile) {
        //Define rules for creating files from the Templates_3.X folders
        boolean isOk = true;
        String fileName = sourceFile.getName();
        if (//$NON-NLS-1$
        fileName.equals("read_only.gif")) {
            isOk = readOnlyOption.isEnabled() && readOnlyOption.isSelected();
        } else if (//$NON-NLS-1$
        fileName.equals("sample_decorator.gif")) {
            isOk = !readOnlyOption.isSelected();
        } else if (//$NON-NLS-1$
        fileName.equals("$decoratorClassName$.java")) {
            isOk = readOnlyOption.isEnabled() && readOnlyOption.isSelected();
        }
        return isOk;
    }

    @Override
    public void validateOptions(TemplateOption source) {
        if (source == readOnlyOption) {
            alterOptionStates();
        }
        super.validateOptions(source);
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
    protected void updateModel(IProgressMonitor monitor) throws CoreException {
        // This method creates the extension point structure through the use
        // of IPluginElement objects. The element attributes are set based on
        // user input from the wizard page as well as values required for the
        // operation of the extension point.
        IPluginBase plugin = model.getPluginBase();
        IPluginExtension extension = createExtension(getUsedExtensionPoint(), true);
        IPluginModelFactory factory = model.getPluginFactory();
        IPluginElement decoratorElement = factory.createElement(extension);
        //$NON-NLS-1$
        decoratorElement.setName("decorator");
        //$NON-NLS-1$ //$NON-NLS-2$
        decoratorElement.setAttribute("adaptable", "true");
        //$NON-NLS-1$ //$NON-NLS-2$
        decoratorElement.setAttribute("state", "true");
        //$NON-NLS-1$ //$NON-NLS-2$
        decoratorElement.setAttribute("lightweight", "true");
        if (!readOnlyOption.isSelected()) {
            //$NON-NLS-1$ //$NON-NLS-2$
            decoratorElement.setAttribute("id", plugin.getId() + "." + getSectionId());
            //$NON-NLS-1$
            decoratorElement.setAttribute("label", PDETemplateMessages.DecoratorTemplate_resourceLabel);
            //$NON-NLS-1$ //$NON-NLS-2$
            decoratorElement.setAttribute("icon", "icons/sample_decorator.gif");
            //$NON-NLS-1$
            decoratorElement.setAttribute("location", getValue(DECORATOR_ICON_PLACEMENT).toString());
        } else {
            //$NON-NLS-1$ //$NON-NLS-2$
            decoratorElement.setAttribute("id", getStringOption(KEY_PACKAGE_NAME) + "." + getStringOption(DECORATOR_CLASS_NAME));
            //$NON-NLS-1$
            decoratorElement.setAttribute("label", PDETemplateMessages.DecoratorTemplate_readOnlyLabel);
            //$NON-NLS-1$ //$NON-NLS-2$
            decoratorElement.setAttribute("class", getStringOption(KEY_PACKAGE_NAME) + "." + getStringOption(DECORATOR_CLASS_NAME));
        }
        IPluginElement enablementElement = factory.createElement(decoratorElement);
        //$NON-NLS-1$
        enablementElement.setName("enablement");
        IPluginElement andElement = factory.createElement(enablementElement);
        //$NON-NLS-1$
        andElement.setName("and");
        IPluginElement resourceObjectElement = factory.createElement(andElement);
        //$NON-NLS-1$
        resourceObjectElement.setName("objectClass");
        //$NON-NLS-1$ //$NON-NLS-2$
        resourceObjectElement.setAttribute("name", "org.eclipse.core.resources.IResource");
        IPluginElement orElement = factory.createElement(andElement);
        //$NON-NLS-1$
        orElement.setName("or");
        IPluginElement fileObjectElement = factory.createElement(orElement);
        //$NON-NLS-1$
        fileObjectElement.setName("objectClass");
        //$NON-NLS-1$ //$NON-NLS-2$
        fileObjectElement.setAttribute("name", "org.eclipse.core.resources.IFile");
        IPluginElement folderObjectElement = factory.createElement(orElement);
        //$NON-NLS-1$
        folderObjectElement.setName("objectClass");
        //$NON-NLS-1$ //$NON-NLS-2$
        folderObjectElement.setAttribute("name", "org.eclipse.core.resources.IFolder");
        IPluginElement projectObjectElement = factory.createElement(orElement);
        //$NON-NLS-1$
        projectObjectElement.setName("objectClass");
        //$NON-NLS-1$ //$NON-NLS-2$
        projectObjectElement.setAttribute("name", "org.eclipse.core.resources.IProject");
        if (readOnlyOption.isSelected())
            orElement.add(folderObjectElement);
        else if (projectOption.isSelected())
            orElement.add(projectObjectElement);
        orElement.add(fileObjectElement);
        andElement.add(resourceObjectElement);
        andElement.add(orElement);
        enablementElement.add(andElement);
        decoratorElement.add(enablementElement);
        extension.add(decoratorElement);
        if (!extension.isInTheModel())
            plugin.add(extension);
    }

    @Override
    public String[] getNewFiles() {
        //$NON-NLS-1$
        return new String[] { "icons/" };
    }

    @Override
    protected String getFormattedPackageName(String id) {
        // Package name addition to create a location for containing
        // any classes required by the decorator.
        String packageName = super.getFormattedPackageName(id);
        if (packageName.length() != 0)
            //$NON-NLS-1$
            return packageName + ".decorators";
        //$NON-NLS-1$
        return "decorators";
    }

    @Override
    public String getUsedExtensionPoint() {
        //$NON-NLS-1$
        return "org.eclipse.ui.decorators";
    }

    /**
	 * Returns a 2-D String array based on a comma seperated
	 * string of choices.
	 *
	 * @param iconLocations
	 * 				comma seperated string of icon placement options
	 * @return the 2-D array of choices
	 *
	 */
    protected String[][] fromCommaSeparated(String iconLocations) {
        //$NON-NLS-1$
        StringTokenizer tokens = new StringTokenizer(iconLocations, ",");
        String[][] choices = new String[tokens.countTokens() / 2][2];
        int x = 0, y = 0;
        while (tokens.hasMoreTokens()) {
            choices[x][y++] = tokens.nextToken();
            choices[x++][y--] = tokens.nextToken();
        }
        return choices;
    }
}
