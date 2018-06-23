package org.eclipse.ecf.tests.sync;

import junit.framework.TestCase;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.internal.tests.sync.Activator;
import org.eclipse.ecf.sync.IModelSynchronizationStrategy;
import org.eclipse.ecf.sync.doc.IDocumentSynchronizationStrategyFactory;

public class SyncServiceTests extends TestCase {

    protected IDocumentSynchronizationStrategyFactory getColaSyncStrategyFactory() {
        return Activator.getDefault().getColaSynchronizationStrategyFactory();
    }

    public void testGetAllSyncFactory() throws Exception {
        IDocumentSynchronizationStrategyFactory[] factories = Activator.getDefault().getSynchStrategyFactories();
        assertNotNull(factories);
        assertTrue(factories.length == 2);
    }

    public void testGetColaSyncFactory() throws Exception {
        IDocumentSynchronizationStrategyFactory factory = getColaSyncStrategyFactory();
        assertNotNull(factory);
        IModelSynchronizationStrategy strategy = factory.createDocumentSynchronizationStrategy(IDFactory.getDefault().createStringID("cola"), true);
        assertNotNull(strategy);
    }
}
