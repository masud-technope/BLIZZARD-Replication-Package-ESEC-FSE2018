package org.eclipse.ecf.tests.core;

import junit.framework.TestCase;
import org.eclipse.ecf.core.BaseContainer;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerFactory;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.provider.IContainerInstantiator;
import org.eclipse.ecf.internal.tests.core.Activator;
import org.osgi.framework.ServiceRegistration;

public class NoRegistryContainerFactoryTest extends TestCase {

    static class TestContainer extends BaseContainer {

        public  TestContainer(ID id) {
            super(id);
        }
    }

    protected ContainerTypeDescription createContainerTypeDescription() {
        return new ContainerTypeDescription(getClass().getName(), new IContainerInstantiator() {

            public IContainer createInstance(ContainerTypeDescription description, Object[] parameters) throws ContainerCreateException {
                return new TestContainer(IDFactory.getDefault().createGUID());
            }

            public String[] getSupportedAdapterTypes(ContainerTypeDescription description) {
                return null;
            }

            public Class[][] getSupportedParameterTypes(ContainerTypeDescription description) {
                return null;
            }

            public String[] getSupportedIntents(ContainerTypeDescription description) {
                return null;
            }
        });
    }

    private ServiceRegistration r;

    protected void setUp() throws Exception {
        super.setUp();
        r = Activator.getContext().registerService(ContainerTypeDescription.class, createContainerTypeDescription(), null);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        if (r != null)
            r.unregister();
    }

    public void testCreateContainer() throws Exception {
        IContainerFactory f = Activator.getDefault().getContainerFactory();
        IContainer c = f.createContainer(getClass().getName());
        assertNotNull(c);
    }
}
