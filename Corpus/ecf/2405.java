package org.eclipse.ecf.remoteservices.internal.tooling.pde;

import org.eclipse.pde.core.plugin.IPluginReference;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.IPluginContentWizard;
import org.eclipse.pde.ui.templates.ITemplateSection;
import org.eclipse.pde.ui.templates.NewPluginTemplateWizard;

public class RemoteServiceHostExample1Wizard extends NewPluginTemplateWizard implements IPluginContentWizard {

    public void init(IFieldData data) {
        super.init(data);
        setWindowTitle("Remote Services Host Example Wizard");
    }

    public ITemplateSection[] createTemplateSections() {
        return new ITemplateSection[] { new RemoteServiceHostExample1Template() };
    }

    public String[] getImportPackages() {
        return new String[] { "org.eclipse.ecf.osgi.services.distribution", "org.eclipse.ecf.examples.remoteservices.hello", "org.eclipse.ecf.remoteservice" };
    }

    public IPluginReference[] getDependencies(String schemaVersion) {
        return new IPluginReference[0];
    }
}
