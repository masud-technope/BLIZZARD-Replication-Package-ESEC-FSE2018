package org.eclipse.ecf.remoteservices.internal.tooling.pde;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginReference;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.OptionTemplateSection;
import org.osgi.framework.Bundle;

public class RemoteServiceConsumerExample1Template extends OptionTemplateSection {

    private String packageName;

    public  RemoteServiceConsumerExample1Template() {
        setPageCount(1);
        addOption("consumerName", "User Name", System.getProperty("user.name"), 0);
    }

    public void addPages(Wizard wizard) {
        WizardPage page = createPage(0, "org.eclipse.pde.doc.user.rcp_mail");
        page.setTitle("Hello Remote Service Consumer");
        page.setDescription("This template creates a Hello remote service consumer");
        wizard.addPage(page);
        markPagesAdded();
    }

    public URL getTemplateLocation() {
        Bundle b = Activator.getDefault().getBundle();
        String path = "/templates/" + getSectionId();
        URL url = b.getEntry(path);
        if (url != null)
            try {
                return new URL(getInstallURL(), path);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        return null;
    }

    public String getSectionId() {
        //$NON-NLS-1$
        return "helloRemoteServiceConsumerExample1";
    }

    protected void updateModel(IProgressMonitor monitor) {
        setManifestHeader("Require-Bundle", "org.eclipse.equinox.common");
    }

    public String getUsedExtensionPoint() {
        return null;
    }

    public boolean isDependentOnParentWizard() {
        return true;
    }

    public int getNumberOfWorkUnits() {
        return super.getNumberOfWorkUnits() + 1;
    }

    public IPluginReference[] getDependencies(String schemaVersion) {
        return new IPluginReference[0];
    }

    protected String getFormattedPackageName(String id) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < id.length(); i++) {
            char ch = id.charAt(i);
            if (buffer.length() == 0) {
                if (Character.isJavaIdentifierStart(ch))
                    buffer.append(Character.toLowerCase(ch));
            } else {
                if (Character.isJavaIdentifierPart(ch) || ch == '.')
                    buffer.append(ch);
            }
        }
        return buffer.toString().toLowerCase(Locale.ENGLISH);
    }

    protected void initializeFields(IFieldData data) {
        // In a new project wizard, we don't know this yet - the
        // model has not been created
        String packageName = getFormattedPackageName(data.getId());
        initializeOption(KEY_PACKAGE_NAME, packageName);
        this.packageName = getFormattedPackageName(data.getId());
    }

    public void initializeFields(IPluginModelBase model) {
        String id = model.getPluginBase().getId();
        String packageName = getFormattedPackageName(id);
        initializeOption(KEY_PACKAGE_NAME, packageName);
        this.packageName = getFormattedPackageName(id);
    }

    public String getStringOption(String name) {
        if (name.equals(KEY_PACKAGE_NAME)) {
            return packageName;
        }
        return super.getStringOption(name);
    }

    @Override
    public String[] getNewFiles() {
        return new String[0];
    }

    @Override
    protected URL getInstallURL() {
        return Activator.getDefault().getBundle().getEntry("/");
    }

    @Override
    protected ResourceBundle getPluginResourceBundle() {
        return Platform.getResourceBundle(Activator.getDefault().getBundle());
    }
}
