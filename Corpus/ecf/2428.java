package org.eclipse.ecf.tests.core.identity;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.internal.tests.core.Activator;
import org.osgi.framework.ServiceRegistration;
import junit.framework.TestCase;

public class NoRegistryNamespaceTest extends TestCase {

    protected Namespace createNamespace() {
        Namespace namespace = new Namespace(this.getClass().getName(), null) {

            private static final long serialVersionUID = -1223105025297785358L;

            public ID createInstance(Object[] args) throws IDCreateException {
                throw new IDCreateException("can't create instance");
            }

            public String getScheme() {
                return getClass().getName();
            }

            public Class[][] getSupportedParameterTypes() {
                return new Class[][] { { String.class } };
            }
        };
        return namespace;
    }

    private ServiceRegistration r;

    protected void setUp() throws Exception {
        super.setUp();
        r = Activator.getContext().registerService(Namespace.class, createNamespace(), null);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        if (r != null)
            r.unregister();
    }

    public void testGetNamespace() throws Exception {
        Namespace n = IDFactory.getDefault().getNamespaceByName(this.getClass().getName());
        assertNotNull(n);
        assertTrue(n.getName().equals(getClass().getName()));
    }
}
