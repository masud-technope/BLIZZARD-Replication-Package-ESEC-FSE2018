/*******************************************************************************
 * Copyright (c) 2015 OPCoach and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Olivier Prouvost <olivier.prouvost@opcoach.com> - initial API and implementation (bug #441331)
 *     Olivier Prouvost <olivier.prouvost@opcoach.com> - Bug 463821,  466269, 466680
 *     Lars Vogel <Lars.Vogel@vogella.com> - Bug 463821, 467819, 473694
 *******************************************************************************/
package org.eclipse.pde.internal.ui.templates.e4;

import java.io.File;
import java.util.ArrayList;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.internal.ui.templates.*;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.PluginReference;
import org.eclipse.pde.ui.templates.TemplateOption;

public class E4ApplicationTemplate extends PDETemplateSection {

    //$NON-NLS-1$
    private static final String E4_SWT_APPLICATION_ID = "org.eclipse.e4.ui.workbench.swt.E4Application";

    //$NON-NLS-1$
    private static final String KEY_CREATE_SAMPLE_CONTENT = "createContent";

    //$NON-NLS-1$
    private static final String KEY_CREATE_LIFE_CYCLE = "createLifeCycle";

    //$NON-NLS-1$
    private static final String KEY_LIFE_CYCLE_CLASS_NAME = "lifeCycleClassName";

    //$NON-NLS-1$
    private static final String KEY_WINDOW_TITLE = "windowTitle";

    // Set the names of the two template files to be tested in isOkToCreateFile method
    // Those files are stored in the org.eclipse.pde.ui.templates/templates_3.5/E4Application/java folder
    //$NON-NLS-1$
    private static final String TEMPLATE_LIFECYCLE_FILENAME = "$lifeCycleClassName$.java";

    // name of the non empty application model file stored in the org.eclipse.pde.ui.templates/templates_3.5/E4Application folder
    //$NON-NLS-1$
    static final String E4_MODEL_FILE = "Application.e4xmi";

    // name of the EMPTY application model file stored in the org.eclipse.pde.ui.templates/templates_3.5/E4Application/bin folder
    //$NON-NLS-1$
    private static final String EMPTY_E4_MODEL_FILE = "bin" + File.separator + E4_MODEL_FILE;

    private TemplateOption lifeCycleClassnameOption;

    public  E4ApplicationTemplate() {
        setPageCount(1);
        createOptions();
    }

    @Override
    public void addPages(Wizard wizard) {
        WizardPage page = createPage(0, IHelpContextIds.TEMPLATE_E4_APPLICATION);
        page.setTitle(PDETemplateMessages.E4ApplicationTemplate_title);
        page.setDescription(PDETemplateMessages.E4ApplicationTemplate_desc);
        wizard.addPage(page);
        markPagesAdded();
    }

    /* Create options expected in the page */
    private void createOptions() {
        //$NON-NLS-1$
        addOption(KEY_WINDOW_TITLE, PDETemplateMessages.E4ApplicationTemplate_windowTitle, "Eclipse 4 RCP Application", 0);
        addOption(KEY_CREATE_SAMPLE_CONTENT, PDETemplateMessages.E4ApplicationTemplate_createSampleContent, false, 0);
        addOption(KEY_PACKAGE_NAME, PDETemplateMessages.E4ApplicationTemplate_packageName, (String) null, 0);
        addOption(KEY_CREATE_LIFE_CYCLE, PDETemplateMessages.E4ApplicationTemplate_createLifeCycle, false, 0);
        //$NON-NLS-1$
        lifeCycleClassnameOption = addOption(KEY_LIFE_CYCLE_CLASS_NAME, PDETemplateMessages.E4ApplicationTemplate_lifeCycleClassname, "E4LifeCycle", 0);
        lifeCycleClassnameOption.setRequired(false);
        lifeCycleClassnameOption.setEnabled(false);
    }

    @Override
    public void validateOptions(TemplateOption source) {
        if (source.getName().equals(KEY_CREATE_LIFE_CYCLE)) {
            // Check if classname is set when lifeCycle required
            lifeCycleClassnameOption.setRequired((Boolean) source.getValue());
            lifeCycleClassnameOption.setEnabled((Boolean) source.getValue());
        }
        super.validateOptions(source);
    }

    @Override
    public String getSectionId() {
        //$NON-NLS-1$
        return "E4Application";
    }

    @Override
    protected void updateModel(IProgressMonitor monitor) throws CoreException {
        // There is only a product extension
        createProductExtension();
    // Other files located in 'org.eclipse.pde.ui.templates/templates_3.5/E4Application' are copied automatically
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
        element.setAttribute("application", E4_SWT_APPLICATION_ID);
        //$NON-NLS-1$
        element.setAttribute("name", getStringOption(KEY_PACKAGE_NAME));
        IPluginElement property;
        if (getBooleanOption(KEY_CREATE_LIFE_CYCLE)) {
            property = model.getFactory().createElement(element);
            //$NON-NLS-1$
            property.setName("property");
            //$NON-NLS-1$ //$NON-NLS-2$
            property.setAttribute("name", "lifeCycleURI");
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            property.setAttribute("value", "bundleclass://" + getValue(KEY_PLUGIN_ID) + "/" + getValue(KEY_PACKAGE_NAME) + "." + getStringOption(KEY_LIFE_CYCLE_CLASS_NAME));
            element.add(property);
        }
        property = model.getFactory().createElement(element);
        //$NON-NLS-1$
        property.setName("property");
        //$NON-NLS-1$ //$NON-NLS-2$
        property.setAttribute("name", "applicationCSS");
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        property.setAttribute("value", "platform:/plugin/" + getValue(KEY_PLUGIN_ID) + "/css/default.css");
        element.add(property);
        extension.add(element);
        if (!extension.isInTheModel())
            plugin.add(extension);
    }

    @Override
    protected boolean isOkToCreateFolder(File sourceFolder) {
        // We copy the files in 'org.eclipse.pde.ui.templates/templates_3.5/E4Application/*/handlers' or 'parts' if content required
        String fname = sourceFolder.getName();
        if (//$NON-NLS-1$//$NON-NLS-2$
        fname.endsWith("handlers") || fname.endsWith("parts"))
            return getBooleanOption(KEY_CREATE_SAMPLE_CONTENT);
        return super.isOkToCreateFolder(sourceFolder);
    }

    @Override
    protected boolean isOkToCreateFile(File sourceFile) {
        // If file is the lifeCycleClassname (with a $ at the end) we keep it only if life cycle must be created.
        String fname = sourceFile.getName();
        if (fname.equals(TEMPLATE_LIFECYCLE_FILENAME))
            return getBooleanOption(KEY_CREATE_LIFE_CYCLE);
        if (fname.endsWith(E4_MODEL_FILE)) {
            boolean createSampleContent = getBooleanOption(KEY_CREATE_SAMPLE_CONTENT);
            // application model is expected
            if (sourceFile.getAbsolutePath().endsWith(EMPTY_E4_MODEL_FILE)) {
                return !createSampleContent;
            }
            // This is the root file (containing the customized content), must copy it if content expected
            return createSampleContent;
        }
        return super.isOkToCreateFile(sourceFile);
    }

    @Override
    public String getUsedExtensionPoint() {
        return null;
    }

    @Override
    public boolean isDependentOnParentWizard() {
        // Must return true here to call the initializeFields(IFieldData) from ancestor
        return true;
    }

    @Override
    protected void initializeFields(IFieldData data) {
        // This is called because isDependentOnParentWizard returns true
        // We can get values entered in previous pages and put them in the local options.
        // At this point, we need to get package name for this plugin
        String initialPackage = getFormattedPackageName(data.getId());
        initializeOption(KEY_PACKAGE_NAME, initialPackage);
    }

    @Override
    public void initializeFields(IPluginModelBase model) {
        String initialPackage = getFormattedPackageName(model.getPluginBase().getId());
        initializeOption(KEY_PACKAGE_NAME, initialPackage);
    }

    @Override
    public int getNumberOfWorkUnits() {
        return super.getNumberOfWorkUnits() + 1;
    }

    @Override
    public IPluginReference[] getDependencies(String schemaVersion) {
        String[] dependencies = new String[] { //$NON-NLS-1$
        "javax.inject", //$NON-NLS-1$//$NON-NLS-2$
        "org.eclipse.core.runtime", //$NON-NLS-1$//$NON-NLS-2$
        "org.eclipse.swt", //$NON-NLS-1$ //$NON-NLS-2$
        "org.eclipse.e4.ui.model.workbench", //$NON-NLS-1$ //$NON-NLS-2$
        "org.eclipse.jface", //$NON-NLS-1$ //$NON-NLS-2$
        "org.eclipse.e4.ui.services", //$NON-NLS-1$ //$NON-NLS-2$
        "org.eclipse.e4.ui.workbench", //$NON-NLS-1$ //$NON-NLS-2$
        "org.eclipse.e4.core.di", //$NON-NLS-1$ //$NON-NLS-2$
        "org.eclipse.e4.ui.di", //$NON-NLS-1$
        "org.eclipse.e4.core.contexts" };
        final ArrayList<IPluginReference> result = new ArrayList(dependencies.length);
        for (final String dependency : dependencies) {
            //$NON-NLS-1$
            String versionString = "0.0.0";
            result.add(new PluginReference(dependency, versionString, IMatchRules.GREATER_OR_EQUAL));
        }
        return result.toArray(new IPluginReference[0]);
    }

    @Override
    public String[] getNewFiles() {
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return new String[] { "icons/", "css/default.css", "Application.e4xmi" };
    }
}
