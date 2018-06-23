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
import junit.framework.TestCase;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.identity.StringID;

public class NamespaceTest extends TestCase {

    private static final String DESCRIPTION = "description";

    private Namespace fixture;

    /*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
    protected void setUp() throws Exception {
        super.setUp();
        fixture = IDFactory.getDefault().getNamespaceByName(StringID.class.getName());
        assertNotNull(fixture);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
    protected void tearDown() throws Exception {
        super.tearDown();
        fixture = null;
    }

    protected Namespace createNamespace() {
        Namespace namespace = new Namespace(this.getClass().getName(), DESCRIPTION) {

            private static final long serialVersionUID = 0L;

            public ID createInstance(Object[] args) throws IDCreateException {
                throw new IDCreateException("can't create instance");
            }

            public String getScheme() {
                return NamespaceTest.this.getClass().getName();
            }

            public Class[][] getSupportedParameterTypes() {
                return new Class[][] { { String.class } };
            }
        };
        return namespace;
    }

    public void testNamespaceGetScheme() {
        String scheme = fixture.getScheme();
        assertNotNull(scheme);
    }

    public void testNamespaceGetName() {
        String name = fixture.getName();
        assertNotNull(name);
        assertTrue(name.equals(StringID.class.getName()));
    }

    public void testNewNamespaceCreateInstance() {
        Namespace ns = createNamespace();
        try {
            ns.createInstance(null);
            fail("createInstance did not return null");
        } catch (IDCreateException e) {
        }
    }

    public void testNewNamespaceGetScheme() {
        Namespace ns = createNamespace();
        String scheme = ns.getScheme();
        assertTrue(scheme.equals(this.getClass().getName()));
    }

    public void testNewNamespaceGetDescription() {
        Namespace ns = createNamespace();
        String desc = ns.getDescription();
        assertTrue(desc.equals(DESCRIPTION));
    }

    public void testNewNamespaceGetSupportedParameterTypesForCreateInstance() {
        Namespace ns = createNamespace();
        Class[][] result = ns.getSupportedParameterTypes();
        assertTrue(result.length == 1);
        assertTrue(result[0][0].equals(String.class));
    }

    public final void testSerializable() throws Exception {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(buf);
        try {
            out.writeObject(fixture);
        } catch (NotSerializableException ex) {
            fail(ex.getLocalizedMessage());
        } finally {
            out.close();
        }
    }
}
