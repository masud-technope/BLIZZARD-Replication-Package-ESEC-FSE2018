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
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;

public class LongIDTest extends IDAbstractTestCase {

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.tests.IDTestCase#createID()
	 */
    protected ID createID() throws IDCreateException {
        return createLongID(Long.MAX_VALUE);
    }

    protected ID createLongID(long val) throws IDCreateException {
        return IDFactory.getDefault().createLongID(val);
    }

    public void testCreate() throws Exception {
        final ID newID = createID();
        assertNotNull(newID);
    }

    public void testMinCreate() throws Exception {
        final ID newID = createLongID(Long.MIN_VALUE);
        assertNotNull(newID);
    }

    public void testMAXCreate() throws Exception {
        final ID newID = createLongID(Long.MAX_VALUE);
        assertNotNull(newID);
    }

    public void testZeroCreate() throws Exception {
        final ID id = createLongID(0);
        assertTrue(id.getName().equals(String.valueOf(0)));
    }

    public void testGetName() throws Exception {
        final ID id1 = createID();
        final ID id2 = createID();
        assertTrue(id1.getName().equals(id2.getName()));
    }

    public void testToExternalForm() throws Exception {
        final ID id = createID();
        assertNotNull(id.toExternalForm());
    }

    public void testToString() throws Exception {
        final ID id = createID();
        assertNotNull(id.toString());
    }

    public void testIsEqual() throws Exception {
        final ID id1 = createID();
        final ID id2 = createID();
        assertTrue(id1.equals(id2));
    }

    public void testHashCode() throws Exception {
        final ID id1 = createID();
        final ID id2 = createID();
        assertTrue(id1.hashCode() == id2.hashCode());
    }

    public void testCompareToEqual() throws Exception {
        final ID id1 = createID();
        final ID id2 = createID();
        assertTrue(id1.compareTo(id2) == 0);
        assertTrue(id2.compareTo(id1) == 0);
    }

    public void testCompareToNotEqual() throws Exception {
        final ID id1 = createLongID(0);
        final ID id2 = createLongID(1);
        assertTrue(id1.compareTo(id2) < 0);
        assertTrue(id2.compareTo(id1) > 0);
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
