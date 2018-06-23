/*******************************************************************************
 * Copyright (c) 2008 Versant Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Kuppe (mkuppe <at> versant <dot> com) - initial API and implementation
 ******************************************************************************/
package ch.ethz.iks.slp.impl;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ch.ethz.iks.slp.ServiceLocationException;
import junit.framework.TestCase;

public class AttributeParserTest extends TestCase {

    private static final String NEG_INTEGER = "(IntegerMax=-2147483648)";

    private static final String POS_INTEGER = "(IntegerMin=2147483647)";

    private static final String VENDOR_WITH_EN = "(x-28392-FOOBAR=FOOBAR)";

    private static final String STRING = "(String=foobar asdflj24 aslkujh2)";

    private static final String BOOLEAN = "(Boolean=false)";

    private static final String BYTE = "(Byte=\\FF\\00\\01\\02\\A1\\99\\AA\\EE)";

    /**
	 * Test method for {@link ch.ethz.iks.slp.impl.SLPMessage#attributeStringToList(java.lang.String)}.
	 */
    public void testByteAttributeType() {
        SLPTestMessage stm = new SLPTestMessage();
        String input = BYTE;
        List attributeStringToList = new ArrayList();
        try {
            attributeStringToList = stm.attributeStringToList(input);
        } catch (ServiceLocationException e) {
            fail("Input " + input + " must be valid");
        }
        assertEquals(1, attributeStringToList.size());
        assertTrue(attributeStringToList.contains(BYTE));
    }

    /**
	 * Test method for {@link ch.ethz.iks.slp.impl.SLPMessage#attributeStringToList(java.lang.String)}.
	 */
    public void testStringAttributeType() {
        SLPTestMessage stm = new SLPTestMessage();
        String input = STRING;
        List attributeStringToList = new ArrayList();
        try {
            attributeStringToList = stm.attributeStringToList(input);
        } catch (ServiceLocationException e) {
            fail("Input " + input + " must be valid");
        }
        assertEquals(1, attributeStringToList.size());
        assertTrue(attributeStringToList.contains(STRING));
    }

    public void testBadTag() {
        SLPTestMessage stm = new SLPTestMessage();
        // #bad-tag 		= CR / LF / HTAB / "_";
        List inputs = new ArrayList();
        inputs.add("(bad_tag=foo)");
        inputs.add("(bat\ttag=foo)");
        inputs.add("(bad\rtag=foo)");
        inputs.add("(bad\ntag=foo)");
        for (Iterator iterator = inputs.iterator(); iterator.hasNext(); ) {
            String input = (String) iterator.next();
            try {
                stm.attributeStringToList(input);
            } catch (ServiceLocationException e) {
                continue;
            }
            fail("Input " + input + " must throw an Exception");
        }
    }

    /**
	 * Test method for {@link ch.ethz.iks.slp.impl.SLPMessage#attributeStringToList(java.lang.String)}.
	 */
    public void testAllAttributeType() {
        SLPTestMessage stm = new SLPTestMessage();
        String input = STRING + "," + BOOLEAN + "," + BYTE + "," + VENDOR_WITH_EN + "," + POS_INTEGER + "," + NEG_INTEGER;
        List attributeStringToList = new ArrayList();
        try {
            attributeStringToList = stm.attributeStringToList(input);
        } catch (ServiceLocationException e) {
            fail("Input " + input + " must be valid");
        }
        assertEquals(6, attributeStringToList.size());
        assertTrue(attributeStringToList.contains(NEG_INTEGER));
        assertTrue(attributeStringToList.contains(POS_INTEGER));
        assertTrue(attributeStringToList.contains(VENDOR_WITH_EN));
        assertTrue(attributeStringToList.contains(STRING));
        assertTrue(attributeStringToList.contains(BOOLEAN));
        assertTrue(attributeStringToList.contains(BYTE));
    }

    // just make attributeStringToList public
    private class SLPTestMessage extends SLPMessage {

        protected int getSize() {
            return 0;
        }

        protected void writeTo(final DataOutputStream out) throws IOException {
        // no op
        }

        /* (non-Javadoc)
		 * @see ch.ethz.iks.slp.impl.SLPMessage#attributeStringToList(java.lang.String)
		 */
        public List attributeStringToList(String input) throws ServiceLocationException {
            return super.attributeStringToList(input);
        }

        /* (non-Javadoc)
		 * @see ch.ethz.iks.slp.impl.SLPMessage#attributeStringToListLiberal(java.lang.String)
		 */
        public List attributeStringToListLiberal(String input) {
            return super.attributeStringToListLiberal(input);
        }
    }
}
