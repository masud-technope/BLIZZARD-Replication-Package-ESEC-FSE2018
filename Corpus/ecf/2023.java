/****************************************************************************
 * Copyright (c) 2011 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.tests.core.identity;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.URIID;

public class URIIDTest extends IDAbstractTestCase {

    public static final String URIIDNAMESPACE = URIID.class.getName();

    public static final String URI1 = "http://lala/lala/lala";

    public static final String URI2 = "zooo:barbarbarbarbarbarbarbar";

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.tests.IDTestCase#createID()
	 */
    protected ID createID() throws IDCreateException {
        return createID(URI1);
    }

    protected ID createID(String val) throws IDCreateException {
        // return IDFactory.getDefault().createID(URIIDNAMESPACE,val);
        return IDFactory.getDefault().createStringID(val);
    }

    // The tests below are commented out because the Platform is using an older
    // version of ECF
    // core identity, and these tests are not supported in that older version
    public void testCreate() throws Exception {
    // final ID newID = createID();
    // assertNotNull(newID);
    }
    /*
	 * public void testNullCreate() throws Exception { try { createID(null);
	 * fail(); } catch (final IDCreateException e) { // success } }
	 * 
	 * public void testGetName() throws Exception { final ID id =
	 * createID(URI1); assertTrue(id.getName().equals(URI1)); }
	 * 
	 * public void testToExternalForm() throws Exception { final ID id =
	 * createID(URI1); assertNotNull(id.toExternalForm()); }
	 * 
	 * public void testToString() throws Exception { final ID id =
	 * createID(URI1); assertNotNull(id.toString()); }
	 * 
	 * public void testIsEqual() throws Exception { final ID id1 = createID();
	 * final ID id2 = createID(); assertTrue(id1.equals(id2)); }
	 * 
	 * public void testHashCode() throws Exception { final ID id1 = createID();
	 * final ID id2 = createID(); assertTrue(id1.hashCode() == id2.hashCode());
	 * }
	 * 
	 * public void testCompareToEqual() throws Exception { final ID id1 =
	 * createID(); final ID id2 = createID(); assertTrue(id1.compareTo(id2) ==
	 * 0); assertTrue(id2.compareTo(id1) == 0); }
	 * 
	 * public void testCompareToNotEqual() throws Exception { final ID id1 =
	 * createID(URI1); final ID id2 = createID(URI2);
	 * assertTrue(id1.compareTo(id2) < 0); assertTrue(id2.compareTo(id1) > 0); }
	 * 
	 * public void testGetNamespace() throws Exception { final ID id =
	 * createID(); final Namespace ns = id.getNamespace(); assertNotNull(ns); }
	 * 
	 * public void testEqualNamespaces() throws Exception { final ID id1 =
	 * createID(); final ID id2 = createID(); final Namespace ns1 =
	 * id1.getNamespace(); final Namespace ns2 = id2.getNamespace();
	 * assertTrue(ns1.equals(ns2)); assertTrue(ns2.equals(ns2)); }
	 * 
	 * public void testSerializable() throws Exception { final
	 * ByteArrayOutputStream buf = new ByteArrayOutputStream(); final
	 * ObjectOutputStream out = new ObjectOutputStream(buf); try {
	 * out.writeObject(createID()); } catch (final NotSerializableException ex)
	 * { fail(ex.getLocalizedMessage()); } finally { out.close(); } }
	 * 
	 * public void testCreateFromExternalForm() throws Exception { final ID id1
	 * = createID(); final String externalForm = id1.toExternalForm(); final ID
	 * id2 = IDFactory.getDefault().createID(id1.getNamespace(), externalForm);
	 * assertTrue(id1.equals(id2)); }
	 * 
	 * public void testCreateFromURIForm() throws Exception { final ID id1 =
	 * IDFactory.getDefault().createID(URIIDNAMESPACE,new Object[] { new
	 * URI(URI1) }); final String externalForm = id1.toExternalForm(); final ID
	 * id2 = IDFactory.getDefault().createID(id1.getNamespace(), externalForm);
	 * assertTrue(id1.equals(id2)); }
	 */
}
