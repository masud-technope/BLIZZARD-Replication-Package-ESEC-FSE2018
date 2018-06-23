/*******************************************************************************
 * Copyright (c) 2006, 2016 IBM Corporation and others.
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
import org.eclipse.pde.ui.templates.AbstractTemplateSection;
import org.eclipse.pde.ui.templates.PluginReference;

public class UniversalWelcomeTemplate extends PDETemplateSection {

    //$NON-NLS-1$
    private static final String KEY_LINK_ID = "linkId";

    //$NON-NLS-1$
    private static final String KEY_EXTENSION_ID = "extensionId";

    //$NON-NLS-1$
    private static final String KEY_INTRO_DIR = "introDir";

    //$NON-NLS-1$
    private static final String KEY_PATH = "path";

    //$NON-NLS-1$
    private static final String KEY_LINK_URL = "linkUrl";

    private String pluginId;

    public  UniversalWelcomeTemplate() {
        setPageCount(1);
        createOptions();
    }

    private void createOptions() {
        // options
        //$NON-NLS-1$
        addOption(KEY_INTRO_DIR, PDETemplateMessages.UniversalWelcomeTemplate_key_directoryName, "intro", 0);
        addOption(KEY_PATH, PDETemplateMessages.UniversalWelcomeTemplate_key_targetPage, new String[][] { //$NON-NLS-1$ //$NON-NLS-2$
        { "overview/@", PDETemplateMessages.UniversalWelcomeTemplate_page_Overview }, //$NON-NLS-1$ //$NON-NLS-2$
        { "tutorials/@", PDETemplateMessages.UniversalWelcomeTemplate_page_Tutorials }, //$NON-NLS-1$ //$NON-NLS-2$
        { "firststeps/@", PDETemplateMessages.UniversalWelcomeTemplate_page_FirstSteps }, //$NON-NLS-1$ //$NON-NLS-2$
        { "samples/@", PDETemplateMessages.UniversalWelcomeTemplate_page_Samples }, //$NON-NLS-1$ //$NON-NLS-2$
        { "whatsnew/@", PDETemplateMessages.UniversalWelcomeTemplate_page_Whatsnew }, //$NON-NLS-1$ //$NON-NLS-2$
        { "migrate/@", PDETemplateMessages.UniversalWelcomeTemplate_page_Migrate }, { "webresources/@", PDETemplateMessages.UniversalWelcomeTemplate_page_WebResources } }, "overview/@", //$NON-NLS-1$ //$NON-NLS-2$
        0);
        //$NON-NLS-1$
        addOption(KEY_LINK_URL, PDETemplateMessages.UniversalWelcomeTemplate_linkUrl, "http://www.eclipse.org", 0);
    }

    @Override
    public void addPages(Wizard wizard) {
        WizardPage page = createPage(0, IHelpContextIds.TEMPLATE_UNIVERSAL_WELCOME);
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
        return "universalWelcome";
    }

    @Override
    protected void initializeFields(IFieldData data) {
        // In a new project wizard, we don't know this yet - the
        // model has not been created
        pluginId = data.getId();
    }

    @Override
    public void initializeFields(IPluginModelBase model) {
        pluginId = model.getPluginBase().getId();
    }

    @Override
    protected void updateModel(IProgressMonitor monitor) throws CoreException {
        IPluginBase plugin = model.getPluginBase();
        //$NON-NLS-1$
        IPluginExtension extension = createExtension("org.eclipse.ui.intro.configExtension", false);
        IPluginElement element = model.getPluginFactory().createElement(extension);
        //$NON-NLS-1$
        element.setName("configExtension");
        //$NON-NLS-1$
        element.setAttribute(//$NON-NLS-1$
        "configId", //$NON-NLS-1$
        "org.eclipse.ui.intro.universalConfig");
        element.setAttribute("content", //$NON-NLS-1$
        getStringOption(KEY_INTRO_DIR) + //$NON-NLS-1$
        "/sample.xml");
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
        return true;
    }

    @Override
    public String getUsedExtensionPoint() {
        //$NON-NLS-1$
        return "org.eclipse.ui.intro.configExtension";
    }

    @Override
    public IPluginReference[] getDependencies(String schemaVersion) {
        ArrayList<PluginReference> result = new ArrayList();
        // We really need Eclipse 3.2 or higher but since Universal
        // appears in 3.2 for the first time, just depending on
        // its presence has the same effect.
        //$NON-NLS-1$
        result.add(new PluginReference("org.eclipse.ui.intro"));
        //$NON-NLS-1$
        result.add(new PluginReference("org.eclipse.ui.intro.universal"));
        //$NON-NLS-1$
        result.add(new PluginReference("org.eclipse.ui"));
        return result.toArray(new IPluginReference[result.size()]);
    }

    @Override
    public int getNumberOfWorkUnits() {
        return super.getNumberOfWorkUnits() + 1;
    }

    /*
	 * We are going to compute some values even though we are
	 * not exposing them as options.
	 */
    @Override
    public String getStringOption(String name) {
        if (name.equals(KEY_EXTENSION_ID)) {
            //$NON-NLS-1$
            return stripNonAlphanumeric(pluginId) + "-introExtension";
        }
        if (name.equals(KEY_LINK_ID)) {
            //$NON-NLS-1$
            return stripNonAlphanumeric(pluginId) + "-introLink";
        }
        return super.getStringOption(name);
    }

    /*
	 * Strips any non alphanumeric characters from the string so as not to break the css
	 */
    private String stripNonAlphanumeric(String id) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < id.length(); i++) {
            char next = id.charAt(i);
            if (Character.isLetterOrDigit(next)) {
                result.append(next);
            }
        }
        return result.toString();
    }

    @Override
    public String[] getNewFiles() {
        //$NON-NLS-1$
        return new String[] { getStringOption(KEY_INTRO_DIR) + "/" };
    }
}
