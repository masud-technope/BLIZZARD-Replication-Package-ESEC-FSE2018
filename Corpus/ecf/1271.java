/*******************************************************************************
 * Copyright (c) 2005, 2007 Remy Suen
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.protocol.msn.internal;

import junit.framework.TestCase;
import org.eclipse.ecf.protocol.msn.internal.encode.StringUtils;

public class StringUtilsTest extends TestCase {

    public void testSplitOnSpace() {
        //$NON-NLS-1$
        String[] ret = StringUtils.splitOnSpace("");
        assertNotNull(ret);
        assertEquals(1, ret.length);
        //$NON-NLS-1$
        assertEquals("", ret[0]);
        //$NON-NLS-1$
        ret = StringUtils.splitOnSpace("VER 1 MSNP11 CVR0");
        assertNotNull(ret);
        assertEquals(4, ret.length);
        //$NON-NLS-1$
        assertEquals("VER", ret[0]);
        //$NON-NLS-1$
        assertEquals("1", ret[1]);
        //$NON-NLS-1$
        assertEquals("MSNP11", ret[2]);
        //$NON-NLS-1$
        assertEquals("CVR0", ret[3]);
    }

    public void testSplitChar() {
        //$NON-NLS-1$
        String[] ret = StringUtils.split("", ' ');
        assertNotNull(ret);
        assertEquals(1, ret.length);
        //$NON-NLS-1$
        assertEquals("", ret[0]);
        //$NON-NLS-1$
        ret = StringUtils.split("VER 1 MSNP11 CVR0", ' ');
        assertNotNull(ret);
        assertEquals(4, ret.length);
        //$NON-NLS-1$
        assertEquals("VER", ret[0]);
        //$NON-NLS-1$
        assertEquals("1", ret[1]);
        //$NON-NLS-1$
        assertEquals("MSNP11", ret[2]);
        //$NON-NLS-1$
        assertEquals("CVR0", ret[3]);
    }

    public void testSplitSubstring() {
        //$NON-NLS-1$ //$NON-NLS-2$
        String ret = StringUtils.splitSubstring("", " ", 1);
        //$NON-NLS-1$
        assertEquals("", ret);
        //$NON-NLS-1$ //$NON-NLS-2$
        ret = StringUtils.splitSubstring("VER 1 MSNP11 CVR0", " ", 0);
        //$NON-NLS-1$
        assertEquals("VER", ret);
        //$NON-NLS-1$ //$NON-NLS-2$
        ret = StringUtils.splitSubstring("VER 1 MSNP11 CVR0", " ", 1);
        //$NON-NLS-1$
        assertEquals("1", ret);
        //$NON-NLS-1$ //$NON-NLS-2$
        ret = StringUtils.splitSubstring("VER 1 MSNP11 CVR0", " ", 2);
        //$NON-NLS-1$
        assertEquals("MSNP11", ret);
        //$NON-NLS-1$ //$NON-NLS-2$
        ret = StringUtils.splitSubstring("VER 1 MSNP11 CVR0", " ", 3);
        //$NON-NLS-1$
        assertEquals("CVR0", ret);
    }

    public void testXmlDecode() {
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("", StringUtils.xmlDecode(""));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("&&&", StringUtils.xmlDecode("&amp;&amp;&amp;"));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("<>", StringUtils.xmlDecode("&lt;&gt;"));
        assertEquals("<><><>", //$NON-NLS-1$
        StringUtils.xmlDecode(//$NON-NLS-1$
        "&lt;&gt;&lt;&gt;&lt;&gt;"));
        assertEquals("'\"'\"'", //$NON-NLS-1$
        StringUtils.xmlDecode(//$NON-NLS-1$
        "&apos;&quot;&apos;&quot;&apos;"));
        assertEquals("I like <xml> tags", //$NON-NLS-1$
        StringUtils.xmlDecode(//$NON-NLS-1$
        "I like &lt;xml&gt; tags"));
    }

    public void testXmlEncode() {
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("", StringUtils.xmlEncode(""));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("&amp;&amp;&amp;", StringUtils.xmlEncode("&&&"));
        //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("&lt;&gt;", StringUtils.xmlEncode("<>"));
        assertEquals("&lt;&gt;&lt;&gt;&lt;&gt;", //$NON-NLS-1$
        StringUtils.xmlEncode(//$NON-NLS-1$
        "<><><>"));
        assertEquals("&apos;&quot;&apos;&quot;&apos;", //$NON-NLS-1$
        StringUtils.xmlEncode(//$NON-NLS-1$
        "'\"'\"'"));
        assertEquals("I like &lt;xml&gt; tags", //$NON-NLS-1$
        StringUtils.xmlEncode(//$NON-NLS-1$
        "I like <xml> tags"));
    }
}
