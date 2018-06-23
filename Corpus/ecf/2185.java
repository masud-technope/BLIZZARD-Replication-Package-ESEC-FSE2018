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
package org.eclipse.ecf.tests.core.identity;

import java.io.ByteArrayOutputStream;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.UUID;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;

public class UuIDTest extends IDAbstractTestCase {

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.tests.IDTestCase#createID()
	 */
    public ID createID() {
        return IDFactory.getDefault().createUuID();
    }

    public void testCreateRandom() throws Exception {
        final ID newID = createID();
        assertNotNull(newID);
    }

    public void testCreateUUID() throws Exception {
        final ID newID = IDFactory.getDefault().createUuID(UUID.randomUUID());
        assertNotNull(newID);
    }

    public void testCreateString() throws Exception {
        final ID newID = IDFactory.getDefault().createUuID(UUID.randomUUID().toString());
        assertNotNull(newID);
    }

    public void testCreateURI() throws Exception {
        final ID newID = IDFactory.getDefault().createUuID(new URI("uuid:" + UUID.randomUUID()));
        assertNotNull(newID);
    }

    public void testGetName() throws Exception {
        UUID uuid = UUID.randomUUID();
        final ID id1 = IDFactory.getDefault().createUuID(uuid);
        final ID id2 = IDFactory.getDefault().createUuID(uuid);
        assertTrue(id1.getName().equals(id2.getName()));
    }

    public void testToExternalForm() throws Exception {
        final ID id = createID();
        assertNotNull(id.toExternalForm());
    }

    public void testToString() throws Exception {
        final ID id = IDFactory.getDefault().createUuID();
        assertNotNull(id.toString());
    }

    public void testIsEqual() throws Exception {
        UUID uuid = UUID.randomUUID();
        final ID id1 = IDFactory.getDefault().createUuID(uuid);
        final ID id2 = IDFactory.getDefault().createUuID(uuid);
        assertTrue(id1.equals(id2));
    }

    public void testHashCode() throws Exception {
        UUID uuid = UUID.randomUUID();
        final ID id1 = IDFactory.getDefault().createUuID(uuid);
        final ID id2 = IDFactory.getDefault().createUuID(uuid);
        assertTrue(id1.hashCode() == id2.hashCode());
    }

    public void testCompareToEqual() throws Exception {
        UUID uuid = UUID.randomUUID();
        final ID id1 = IDFactory.getDefault().createUuID(uuid);
        final ID id2 = IDFactory.getDefault().createUuID(uuid);
        assertTrue(id1.compareTo(id2) == 0);
        assertTrue(id2.compareTo(id1) == 0);
    }

    public void testCompareToNotEqual() throws Exception {
        final ID id1 = createID();
        final ID id2 = createID();
        assertTrue(id1.compareTo(id2) != 0);
    }

    public void testGetNamespace() throws Exception {
        final ID id = createID();
        final Namespace ns = id.getNamespace();
        assertNotNull(ns);
    }

    public void testEqualNamespaces() throws Exception {
        final ID id1 = createID();
        final ID id2 = createID();
        final Namespace ns1 = id1.getNamespace();
        final Namespace ns2 = id2.getNamespace();
        assertTrue(ns1.equals(ns2));
        assertTrue(ns2.equals(ns2));
    }

    public void testSerializable() throws Exception {
        final ByteArrayOutputStream buf = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(buf);
        try {
            out.writeObject(createID());
        } catch (final NotSerializableException ex) {
            fail(ex.getLocalizedMessage());
        } finally {
            out.close();
        }
    }

    public void testCreateFromExternalForm() throws Exception {
        final ID id1 = createID();
        final String externalForm = id1.toExternalForm();
        final ID id2 = IDFactory.getDefault().createID(id1.getNamespace(), externalForm);
        assertTrue(id1.equals(id2));
    }
}
