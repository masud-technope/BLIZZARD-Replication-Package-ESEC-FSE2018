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
package org.eclipse.ecf.tests.core.util;

import org.eclipse.ecf.core.util.Base64;
import junit.framework.TestCase;

public class Base64Test extends TestCase {

    private static final String INPUT = "the quick brown fox jumped over the lazy dog";

    protected String encode() {
        return Base64.encode(INPUT.getBytes());
    }

    public void testEncode() {
        String encoded = encode();
        assertNotNull(encoded);
        assertTrue(encoded.length() > 0);
    }

    public void testDecode() {
        String encoded = encode();
        byte[] bytes = Base64.decode(encoded);
        assertNotNull(bytes);
        assertTrue(INPUT.equals(new String(bytes)));
    }
}
