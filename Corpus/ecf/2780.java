/*******************************************************************************
* Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.tests.remoteservice;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.eclipse.ecf.tests.ContainerAbstractTestCase;

public abstract class AbstractLocalRemoteServiceTest extends ContainerAbstractTestCase {

    protected IContainer container;

    protected IRemoteServiceContainerAdapter containerAdapter;

    public void testGetAllRemoteServiceReferencesEmpty() throws Exception {
        IRemoteServiceReference[] remoteRefs = containerAdapter.getAllRemoteServiceReferences(null, null);
        assertNull(remoteRefs);
    }

    public void testGetLocalRemoteServiceReferences() throws Exception {
        // First register some service locally
        IRemoteServiceRegistration registration = containerAdapter.registerRemoteService(new String[] { IConcatService.class.getName() }, createConcatService(), null);
        // Now lookup all references
        IRemoteServiceReference[] allRefs = containerAdapter.getAllRemoteServiceReferences(IConcatService.class.getName(), null);
        assertNotNull(allRefs);
        assertTrue(allRefs.length == 1);
        // Unregister
        registration.unregister();
    }

    public void testGetAllLocalRemoteServiceReference() throws Exception {
        // First register some service locally
        IRemoteServiceRegistration registration = containerAdapter.registerRemoteService(new String[] { IConcatService.class.getName() }, createConcatService(), null);
        // Now lookup all references
        IRemoteServiceReference[] allRefs = containerAdapter.getAllRemoteServiceReferences(null, null);
        assertNotNull(allRefs);
        assertTrue(allRefs.length == 1);
        // Unregister
        registration.unregister();
    }

    protected Object createConcatService() {
        return new IConcatService() {

            public String concat(String string1, String string2) {
                return string1 + string2;
            }
        };
    }
}
