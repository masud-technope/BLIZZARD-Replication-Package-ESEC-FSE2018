package org.eclipse.ecf.remoteservice.rest.client;

import java.util.Dictionary;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.provider.BaseContainerInstantiator;
import org.eclipse.ecf.core.provider.IRemoteServiceContainerInstantiator;

public abstract class RestClientContainerInstantiator extends BaseContainerInstantiator implements IRemoteServiceContainerInstantiator {

    //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    public static final String[] intents = { "passByValue", "exactlyOnce", "ordered" };

    public String[] getSupportedConfigs(ContainerTypeDescription description) {
        return null;
    }

    public Dictionary getPropertiesForImportedConfigs(ContainerTypeDescription description, String[] importedConfigs, Dictionary exportedProperties) {
        return null;
    }

    public String[] getSupportedIntents(ContainerTypeDescription description) {
        return intents;
    }
}
