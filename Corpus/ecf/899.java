/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.tests.provider.xmpp.remoteservice;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.remoteservice.Constants;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.eclipse.ecf.tests.provider.xmpp.XMPPS;
import org.eclipse.ecf.tests.remoteservice.AbstractRemoteServiceTest;

public class RemoteServiceTest extends AbstractRemoteServiceTest {

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.presence.AbstractPresenceTestCase#getClientContainerName()
	 */
    protected String getClientContainerName() {
        return XMPPS.CONTAINER_NAME;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.ContainerAbstractTestCase#getServerConnectID(int)
	 */
    protected ID getServerConnectID(int client) {
        try {
            return IDFactory.getDefault().createID(getClient(client).getConnectNamespace(), getUsername(client));
        } catch (final IDCreateException e) {
            throw new RuntimeException("cannot create connect id for client " + 1, e);
        }
    }

    protected IRemoteServiceRegistration registerService(IRemoteServiceContainerAdapter adapter, String serviceInterface, Object service, Dictionary serviceProperties, int sleepTime) {
        final IRemoteServiceRegistration result = adapter.registerRemoteService(new String[] { serviceInterface }, service, serviceProperties);
        sleep(sleepTime);
        return result;
    }

    protected ID[] getIDFilter() {
        return new ID[] { getClient(0).getConnectedID() };
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
    protected void setUp() throws Exception {
        super.setUp();
        setClientCount(2);
        clients = createClients();
        setupRemoteServiceAdapters();
        connectClients();
        addRemoteServiceListeners();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
    protected void tearDown() throws Exception {
        cleanUpClients();
        super.tearDown();
    }

    protected Dictionary customizeProperties(Dictionary serviceProperties) {
        final Dictionary props = new Hashtable();
        props.put(Constants.SERVICE_REGISTRATION_TARGETS, getClient(1).getConnectedID());
        if (serviceProperties != null) {
            for (Enumeration e = serviceProperties.keys(); e.hasMoreElements(); ) {
                String key = (String) e.nextElement();
                Object val = serviceProperties.get(key);
                props.put(key, val);
            }
        }
        return props;
    }

    protected String getFilterFromServiceProperties(Dictionary serviceProperties) {
        StringBuffer filter = null;
        if (serviceProperties != null && serviceProperties.size() > 0) {
            serviceProperties.remove(Constants.SERVICE_REGISTRATION_TARGETS);
            if (serviceProperties.size() == 0)
                return null;
            filter = new StringBuffer("(&");
            for (final Enumeration e = serviceProperties.keys(); e.hasMoreElements(); ) {
                final Object key = e.nextElement();
                final Object val = serviceProperties.get(key);
                if (key != null && val != null) {
                    filter.append("(").append(key).append("=").append(val).append(")");
                }
            }
            filter.append(")");
        }
        return (filter == null) ? null : filter.toString();
    }
}
