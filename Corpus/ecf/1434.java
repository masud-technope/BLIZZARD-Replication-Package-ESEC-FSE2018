/*******************************************************************************
* Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.tests.filetransfer;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.util.Proxy;
import org.eclipse.ecf.filetransfer.IFileRangeSpecification;
import org.eclipse.ecf.filetransfer.IFileTransferListener;
import org.eclipse.ecf.filetransfer.IncomingFileTransferException;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveDoneEvent;
import org.eclipse.ecf.filetransfer.identity.IFileID;
import org.eclipse.ecf.filetransfer.service.IRetrieveFileTransfer;
import org.eclipse.ecf.filetransfer.service.IRetrieveFileTransferFactory;
import org.eclipse.ecf.provider.filetransfer.IFileTransferProtocolToFactoryMapper;

public class URIProtocolFactoryRetrieveTest extends AbstractRetrieveTestCase {

    private static final String FOO_URI = "foo:bar";

    private IFileTransferProtocolToFactoryMapper mapper;

    private IRetrieveFileTransferFactory retrieveFactory;

    private URI uri;

    protected void setUp() throws Exception {
        super.setUp();
        mapper = getProtocolToFactoryMapper();
        retrieveFactory = createRetrieveFactory();
        uri = new URI(FOO_URI);
    }

    protected void tearDown() throws Exception {
        mapper = null;
        retrieveFactory = null;
        uri = null;
        super.tearDown();
    }

    public static IFileTransferProtocolToFactoryMapper getProtocolToFactoryMapper() {
        return Activator.getDefault().getProtocolToFactoryMapper();
    }

    private boolean setRetrieveFileTransferFactory() {
        return mapper.setRetrieveFileTransferFactory(uri.getScheme(), retrieveFactory.getClass().getName(), retrieveFactory, 0, true);
    }

    private boolean removeRetrieveFileTransferFactory() {
        return mapper.removeRetrieveFileTransferFactory(retrieveFactory.getClass().getName());
    }

    public void testSetRetrieveFileTransferFactory() throws Exception {
        assertTrue(setRetrieveFileTransferFactory());
        removeRetrieveFileTransferFactory();
    }

    public void testURIProtocolFactoryRetrieve() throws Exception {
        setRetrieveFileTransferFactory();
        // Call sendRetrieveRequest on retrieveAdapter
        retrieveAdapter.sendRetrieveRequest(createFileID(uri), createFileTransferListener(), null);
        // no waiting for events necessary, as it should be using the dummy IRetrieveFileTransfer
        // instance below
        removeRetrieveFileTransferFactory();
    }

    public void testURIProtocolFactoryRetrieveFail() throws Exception {
        // Call sendRetrieveRequest on retrieveAdapter...this should fail as the 
        // protocol factory hasn't been set
        retrieveAdapter.sendRetrieveRequest(createFileID(uri), createFileTransferListener(), null);
        waitForDone(1000);
        assertTrue(doneEvents.size() > 0);
        IIncomingFileTransferReceiveDoneEvent doneEvent = (IIncomingFileTransferReceiveDoneEvent) doneEvents.get(0);
        Exception e = doneEvent.getException();
        assertNotNull(e);
        assertTrue(e instanceof MalformedURLException);
    }

    private IRetrieveFileTransferFactory createRetrieveFactory() {
        return new IRetrieveFileTransferFactory() {

            public IRetrieveFileTransfer newInstance() {
                return new IRetrieveFileTransfer() {

                    public void sendRetrieveRequest(IFileID remoteFileID, IFileTransferListener transferListener, Map options) throws IncomingFileTransferException {
                        System.out.println("sendRetrieveRequest(" + remoteFileID + "," + transferListener + "," + options);
                    }

                    public void sendRetrieveRequest(IFileID remoteFileID, IFileRangeSpecification rangeSpecification, IFileTransferListener transferListener, Map options) throws IncomingFileTransferException {
                        System.out.println("sendRetrieveRequest(" + remoteFileID + "," + transferListener + "," + options);
                    }

                    public Namespace getRetrieveNamespace() {
                        return null;
                    }

                    public void setConnectContextForAuthentication(IConnectContext connectContext) {
                    }

                    public void setProxy(Proxy proxy) {
                    }

                    public Object getAdapter(Class adapter) {
                        return null;
                    }
                };
            }
        };
    }
}
