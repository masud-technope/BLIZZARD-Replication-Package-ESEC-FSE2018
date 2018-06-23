package org.eclipse.ecf.tests.provider.filetransfer.scp;

import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.tests.ContainerAbstractTestCase;

public abstract class AbstractSCPTest extends ContainerAbstractTestCase {

    //$NON-NLS-1$ //$NON-NLS-2$
    protected String host = System.getProperty("host", "localhost");

    //$NON-NLS-1$ //$NON-NLS-2$
    protected String username = System.getProperty("username", "nobody");

    //$NON-NLS-1$ //$NON-NLS-2$
    protected String password = System.getProperty("password", "password");

    protected IContainer baseContainer;

    /*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
    protected void setUp() throws Exception {
        super.setUp();
        this.baseContainer = ContainerFactory.getDefault().createContainer();
    }

    protected void tearDown() throws Exception {
        this.baseContainer.dispose();
        this.baseContainer = null;
        super.tearDown();
    }
}
