package org.eclipse.ecf.internal.storage;

import org.eclipse.ecf.core.*;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.provider.BaseContainerInstantiator;
import org.eclipse.ecf.storage.IStorableContainerAdapter;

public class StorableContainerInstantiator extends BaseContainerInstantiator {

    private static long nextBaseContainerID = 0L;

    public IContainer createInstance(ContainerTypeDescription description, Object[] parameters) throws ContainerCreateException {
        try {
            if (parameters != null && parameters.length > 0) {
                if (parameters[0] instanceof ID)
                    return new StorableBaseContainer((ID) parameters[0]);
                if (parameters[0] instanceof String)
                    return new StorableBaseContainer(IDFactory.getDefault().createStringID((String) parameters[0]));
            }
        } catch (IDCreateException e) {
            throw new ContainerCreateException("Could not create StorableBaseContainer");
        }
        return new StorableBaseContainer(nextBaseContainerID++);
    }

    public String[] getSupportedAdapterTypes(ContainerTypeDescription description) {
        return new String[] { IStorableContainerAdapter.class.getName() };
    }
}
