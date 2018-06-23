/*******************************************************************************
 * Copyright (c) 2007 Versant Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Kuppe (mkuppe <at> versant <dot> com) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.discovery.identity;

import java.util.Arrays;
import junit.framework.TestCase;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.discovery.identity.ServiceIDFactory;
import org.eclipse.ecf.discovery.identity.ServiceTypeID;
import org.eclipse.ecf.tests.discovery.DiscoveryTestHelper;

public abstract class ServiceIDTest extends TestCase {

    protected String namespace;

    private String namingAuthority;

    private String[] protocols;

    private String[] scopes;

    private String[] services;

    public  ServiceIDTest(String string, String[] services, String[] scopes, String[] protocols, String namingAuthority) {
        namespace = string;
        this.services = services;
        this.scopes = scopes;
        this.protocols = protocols;
        this.namingAuthority = namingAuthority;
    }

    public  ServiceIDTest(String namespace) {
        this(namespace, DiscoveryTestHelper.SERVICES, new String[] { DiscoveryTestHelper.SCOPE }, new String[] { DiscoveryTestHelper.PROTOCOL }, DiscoveryTestHelper.NAMINGAUTHORITY);
    }

    protected IServiceTypeID createIDFromString(String serviceType) {
        try {
            return createIDFromStringWithEx(serviceType);
        } catch (final ClassCastException e) {
            fail(e.getMessage());
        }
        return null;
    }

    protected IServiceTypeID createIDFromStringWithEx(String serviceType) {
        Namespace namespaceByName = IDFactory.getDefault().getNamespaceByName(namespace);
        ServiceTypeID serviceTypeID = new ServiceTypeID(namespaceByName, serviceType);
        return ServiceIDFactory.getDefault().createServiceTypeID(namespaceByName, serviceTypeID);
    }

    protected IServiceTypeID createIDFromServiceTypeID(IServiceTypeID serviceType) {
        try {
            return createIDFromServiceTypeIDWithEx(serviceType);
        } catch (final ClassCastException e) {
            fail(e.getMessage());
        }
        return null;
    }

    protected IServiceTypeID createIDFromServiceTypeIDWithEx(IServiceTypeID serviceType) {
        return ServiceIDFactory.getDefault().createServiceTypeID(IDFactory.getDefault().getNamespaceByName(namespace), serviceType);
    }

    public void testServiceTypeIDWithNullString() {
        try {
            createIDFromStringWithEx(null);
        } catch (final IDCreateException ex) {
            return;
        }
        fail();
    }

    public void testServiceTypeIDWithEmptyString() {
        try {
            createIDFromStringWithEx("");
        } catch (final IDCreateException ex) {
            return;
        }
        fail();
    }

    /*
	 * use case: consumer instantiates a IServiceTypeID with the generic (ECF) String
	 */
    public void testServiceTypeIDWithECFGenericString() {
        final IServiceTypeID stid = createIDFromString(DiscoveryTestHelper.SERVICE_TYPE);
        assertEquals(stid.getName(), DiscoveryTestHelper.SERVICE_TYPE);
        assertEquals(stid.getNamingAuthority(), DiscoveryTestHelper.NAMINGAUTHORITY);
        assertTrue(Arrays.equals(stid.getProtocols(), new String[] { DiscoveryTestHelper.PROTOCOL }));
        assertTrue(Arrays.equals(stid.getScopes(), new String[] { DiscoveryTestHelper.SCOPE }));
        assertTrue(Arrays.equals(stid.getServices(), DiscoveryTestHelper.SERVICES));
    }

    /*
	 * use case: consumer instantiates a IServiceTypeID with the generic (ECF) String
	 */
    public void testServiceTypeIDWithECFGenericString2() {
        final String serviceType = "_service._dns-srv._udp.ecf.eclipse.org._IANA";
        final IServiceTypeID stid = createIDFromString(serviceType);
        assertTrue(serviceType.equalsIgnoreCase(stid.getName()));
        assertTrue("IANA".equalsIgnoreCase(stid.getNamingAuthority()));
        assertTrue(Arrays.equals(stid.getProtocols(), new String[] { "udp" }));
        assertTrue(Arrays.equals(stid.getScopes(), new String[] { "ecf.eclipse.org" }));
        assertTrue(Arrays.equals(stid.getServices(), new String[] { "service", "dns-srv" }));
    }

    /*
	 * use case: consumer instantiates a IServiceTypeID with the generic (ECF) String
	 */
    public void testServiceTypeIDWithECFGenericString3() {
        final String serviceType = "_service._dns-srv._udp.ecf.eclipse.org._ECLIPSE";
        final IServiceTypeID stid = createIDFromString(serviceType);
        assertEquals(stid.getName(), serviceType);
        assertEquals(stid.getNamingAuthority(), "ECLIPSE");
        assertTrue(Arrays.equals(stid.getProtocols(), new String[] { "udp" }));
        assertTrue(Arrays.equals(stid.getScopes(), new String[] { "ecf.eclipse.org" }));
        assertTrue(Arrays.equals(stid.getServices(), new String[] { "service", "dns-srv" }));
    }

    /*
	 * use case: conversion from one IServiceTypeID to another (provider A -> provider B)
	 */
    public void testServiceTypeIDWithServiceTypeID() {
        final IServiceTypeID aServiceTypeID = createIDFromStringWithEx("_service._ecf._foo._bar._tcp.ecf.eclipse.org._IANA");
        final IServiceTypeID stid = createIDFromServiceTypeID(aServiceTypeID);
        // this is the only differences
        assertNotSame(aServiceTypeID.getInternal(), stid.getInternal());
        // members should be the same
        assertTrue(aServiceTypeID.getNamingAuthority().equalsIgnoreCase(stid.getNamingAuthority()));
        assertTrue(Arrays.equals(aServiceTypeID.getServices(), stid.getServices()));
        assertTrue(Arrays.equals(aServiceTypeID.getScopes(), stid.getScopes()));
        assertTrue(Arrays.equals(aServiceTypeID.getProtocols(), stid.getProtocols()));
        // logically they should be the same
        assertTrue(aServiceTypeID.hashCode() == stid.hashCode());
        assertEquals(aServiceTypeID, stid);
        assertEquals(stid, aServiceTypeID);
        // should be possible to create a new instance from the string representation of the other
        createFromAnother(aServiceTypeID, stid);
        createFromAnother(stid, aServiceTypeID);
    }

    /*
	 * org.eclipse.ecf.discovery.identity.IServiceIDFactory.createServiceID(Namespace, String, String)
	 */
    public void testServiceIDFactory() {
        Namespace namespaceByName = IDFactory.getDefault().getNamespaceByName(namespace);
        IServiceTypeID serviceType = ServiceIDFactory.getDefault().createServiceTypeID(namespaceByName, services, scopes, protocols, namingAuthority);
        assertNotNull(serviceType);
        assertEquals(namingAuthority, serviceType.getNamingAuthority());
        assertTrue(Arrays.equals(services, serviceType.getServices()));
        assertTrue(Arrays.equals(scopes, serviceType.getScopes()));
        assertTrue(Arrays.equals(protocols, serviceType.getProtocols()));
    }

    /*
	 * org.eclipse.ecf.discovery.identity.IServiceIDFactory.createServiceID(Namespace, String[], String[], String[], String, String)
	 */
    public void testServiceIDFactoryNullNA() {
        try {
            Namespace namespaceByName = IDFactory.getDefault().getNamespaceByName(namespace);
            ServiceIDFactory.getDefault().createServiceTypeID(namespaceByName, services, scopes, protocols, null);
        } catch (IDCreateException e) {
            return;
        }
        fail("Invalid services may cause InvalidIDException");
    }

    /*
	 * org.eclipse.ecf.discovery.identity.IServiceIDFactory.createServiceID(Namespace, String[], String[], String[], String, String)
	 */
    public void testServiceIDFactoryNullProto() {
        try {
            Namespace namespaceByName = IDFactory.getDefault().getNamespaceByName(namespace);
            ServiceIDFactory.getDefault().createServiceTypeID(namespaceByName, services, scopes, null, namingAuthority);
        } catch (IDCreateException e) {
            return;
        }
        fail("Invalid services may cause InvalidIDException");
    }

    /*
	 * org.eclipse.ecf.discovery.identity.IServiceIDFactory.createServiceID(Namespace, String[], String[], String[], String, String)
	 */
    public void testServiceIDFactoryNullServices() {
        try {
            Namespace namespaceByName = IDFactory.getDefault().getNamespaceByName(namespace);
            ServiceIDFactory.getDefault().createServiceTypeID(namespaceByName, null, scopes, protocols, namingAuthority);
        } catch (IDCreateException e) {
            return;
        }
        fail("Invalid services may cause InvalidIDException");
    }

    /*
	 * org.eclipse.ecf.discovery.identity.IServiceIDFactory.createServiceID(Namespace, String[], String[], String[], String, String)
	 */
    public void testServiceIDFactoryNullScope() {
        try {
            Namespace namespaceByName = IDFactory.getDefault().getNamespaceByName(namespace);
            ServiceIDFactory.getDefault().createServiceTypeID(namespaceByName, services, null, protocols, namingAuthority);
        } catch (IDCreateException e) {
            return;
        }
        fail("Invalid services may cause InvalidIDException");
    }

    /*
	 * org.eclipse.ecf.discovery.identity.IServiceIDFactory.createServiceID(Namespace, String[], String)
	 */
    public void testServiceIDFactoryDefaults() {
        Namespace namespaceByName = IDFactory.getDefault().getNamespaceByName(namespace);
        IServiceTypeID serviceType = ServiceIDFactory.getDefault().createServiceTypeID(namespaceByName, services, protocols);
        assertNotNull(serviceType);
        assertTrue(Arrays.equals(services, serviceType.getServices()));
        assertEquals(IServiceTypeID.DEFAULT_NA, serviceType.getNamingAuthority());
        assertTrue(Arrays.equals(IServiceTypeID.DEFAULT_SCOPE, serviceType.getScopes()));
        assertTrue(Arrays.equals(protocols, serviceType.getProtocols()));
    }

    /*
	 * org.eclipse.ecf.discovery.identity.IServiceIDFactory.createServiceID(Namespace, String, String)
	 */
    public void testServiceIDFactory2() {
        Namespace namespaceByName = IDFactory.getDefault().getNamespaceByName(namespace);
        ServiceTypeID serviceTypeID = new ServiceTypeID(new TestNamespace(), "_service._ecf._foo._bar._tcp.ecf.eclipse.org._IANA");
        IServiceTypeID aServiceTypeID = ServiceIDFactory.getDefault().createServiceTypeID(namespaceByName, serviceTypeID);
        assertNotNull(aServiceTypeID);
        // members should be the same
        assertEquals(aServiceTypeID.getNamingAuthority(), serviceTypeID.getNamingAuthority());
        assertTrue(Arrays.equals(aServiceTypeID.getServices(), serviceTypeID.getServices()));
        assertTrue(Arrays.equals(aServiceTypeID.getScopes(), serviceTypeID.getScopes()));
        assertTrue(Arrays.equals(aServiceTypeID.getProtocols(), serviceTypeID.getProtocols()));
        assertSame(namespaceByName, aServiceTypeID.getNamespace());
    }

    /**
	 * Creates a new instance of IServiceTypeId with the Namespace of the second parameter and the instance of the first parameter 
	 * @param aServiceTypeID Used as a prototype
	 * @param stid Namespace to use
	 */
    private void createFromAnother(IServiceTypeID aServiceTypeID, IServiceTypeID stid) {
        final Namespace namespace2 = stid.getNamespace();
        IServiceTypeID instance = null;
        instance = ServiceIDFactory.getDefault().createServiceTypeID(namespace2, aServiceTypeID);
        assertNotNull("it should have been possible to create a new instance of ", instance);
        assertTrue(instance.hashCode() == stid.hashCode());
        //TODO-mkuppe decide if equality should be handled by the namespace for IServiceTypeIDs?
        assertEquals(instance, stid);
        assertEquals(stid, instance);
        assertTrue(instance.hashCode() == aServiceTypeID.hashCode());
        assertEquals(instance, aServiceTypeID);
        assertEquals(aServiceTypeID, instance);
    }

    /*
	 * use case: creates the IServiceTypeID from the internal representation of the discovery provider
	 * to be implemented by subclasses
	 */
    public abstract void testCreateServiceTypeIDWithProviderSpecificString();
}
