/*******************************************************************************
 * Copyright (c) 2000, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.core.tests.compiler.regression;

import java.io.File;
//import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.compiler.CharOperation;
//import junit.framework.AssertionFailedError;
import junit.framework.Test;

@SuppressWarnings({ "rawtypes" })
public class UtilTest extends AbstractRegressionTest {

    StringBuffer camelCaseErrors;

    public  UtilTest(String name) {
        super(name);
    }

    static {
    //	TESTS_RANGE = new int[] { 62, -1 };
    }

    public static Test suite() {
        return buildAllCompliancesTestSuite(testClass());
    }

    /**
 * Assert that a pattern and a name matches or not.
 * If result is invalid then store warning in buffer and display it.
 */
    void assertCamelCase(String pattern, String name, boolean match) {
        assertCamelCase(pattern, name, false, /* name may have more parts*/
        match);
    }

    /**
 * Assert that a pattern and a name matches or not.
 * If result is invalid then store warning in buffer and display it.
 */
    void assertCamelCase(String pattern, String name, boolean prefixMatch, boolean match) {
        boolean camelCase = CharOperation.camelCaseMatch(pattern == null ? null : pattern.toCharArray(), name == null ? null : name.toCharArray(), prefixMatch);
        if (match != camelCase) {
            StringBuffer line = new StringBuffer("'");
            line.append(name);
            line.append("' SHOULD");
            if (!match)
                line.append(" NOT");
            line.append(" match pattern '");
            line.append(pattern);
            line.append("', but it DOES");
            if (!camelCase)
                line.append(" NOT");
            if (this.camelCaseErrors.length() == 0) {
                System.out.println("Invalid results in test " + getName() + ":");
            }
            System.out.println("	- " + line);
            this.camelCaseErrors.append('\n');
            this.camelCaseErrors.append(line);
        }
    }

    /* (non-Javadoc)
 * @see org.eclipse.jdt.core.tests.compiler.regression.AbstractRegressionTest#setUp()
 */
    protected void setUp() throws Exception {
        super.setUp();
        this.camelCaseErrors = new StringBuffer();
    }

    public boolean checkPathMatch(char[] pattern, char[] path, boolean isCaseSensitive) {
        CharOperation.replace(pattern, '/', File.separatorChar);
        CharOperation.replace(pattern, '\\', File.separatorChar);
        CharOperation.replace(path, '/', File.separatorChar);
        CharOperation.replace(path, '\\', File.separatorChar);
        boolean result = CharOperation.pathMatch(pattern, path, isCaseSensitive, File.separatorChar);
        return result;
    }

    public void test01() {
        assertTrue("Pattern matching failure", !CharOperation.match("X".toCharArray(), "Xyz".toCharArray(), true));
    }

    public void test02() {
        assertTrue("Pattern matching failure", CharOperation.match("X*".toCharArray(), "Xyz".toCharArray(), true));
    }

    public void test03() {
        assertTrue("Pattern matching failure", CharOperation.match("X".toCharArray(), "X".toCharArray(), true));
    }

    public void test04() {
        assertTrue("Pattern matching failure", CharOperation.match("X*X".toCharArray(), "XYX".toCharArray(), true));
    }

    public void test05() {
        assertTrue("Pattern matching failure", CharOperation.match("XY*".toCharArray(), "XYZ".toCharArray(), true));
    }

    public void test06() {
        assertTrue("Pattern matching failure", CharOperation.match("*XY*".toCharArray(), "XYZ".toCharArray(), true));
    }

    public void test07() {
        assertTrue("Pattern matching failure", CharOperation.match("*".toCharArray(), "XYZ".toCharArray(), true));
    }

    public void test08() {
        assertTrue("Pattern matching failure", !CharOperation.match("a*".toCharArray(), "XYZ".toCharArray(), true));
    }

    public void test09() {
        assertTrue("Pattern matching failure", !CharOperation.match("abc".toCharArray(), "XYZ".toCharArray(), true));
    }

    public void test10() {
        assertTrue("Pattern matching failure", !CharOperation.match("ab*c".toCharArray(), "abX".toCharArray(), true));
    }

    public void test11() {
        assertTrue("Pattern matching failure", CharOperation.match("a*b*c".toCharArray(), "aXXbYYc".toCharArray(), true));
    }

    public void test12() {
        assertTrue("Pattern matching failure", !CharOperation.match("*a*bc".toCharArray(), "aXXbYYc".toCharArray(), true));
    }

    public void test13() {
        assertTrue("Pattern matching failure", !CharOperation.match("*foo*bar".toCharArray(), "".toCharArray(), true));
    }

    public void test14() {
        assertTrue("Pattern matching failure", CharOperation.match("*foo*bar".toCharArray(), "ffoobabar".toCharArray(), true));
    }

    public void test15() {
        assertTrue("Pattern matching failure", !CharOperation.match("*fol*bar".toCharArray(), "ffoobabar".toCharArray(), true));
    }

    public void test16() {
        assertTrue("Pattern matching failure", CharOperation.match("*X*Y*".toCharArray(), "XY".toCharArray(), true));
    }

    public void test17() {
        assertTrue("Pattern matching failure", CharOperation.match("*X*Y*".toCharArray(), "XYZ".toCharArray(), true));
    }

    public void test18() {
        assertTrue("Pattern matching failure", CharOperation.match("main(*)".toCharArray(), "main(java.lang.String[] argv)".toCharArray(), true));
    }

    public void test19() {
        assertTrue("Pattern matching failure", CharOperation.match("*rr*".toCharArray(), "ARRAY".toCharArray(), false));
    }

    public void test20() {
        assertTrue("Pattern matching failure", CharOperation.match("hello*World".toCharArray(), "helloWorld".toCharArray(), true));
    }

    public void test21() {
        assertEquals("Trim failure", "hello", new String(CharOperation.trim("hello".toCharArray())));
    }

    public void test22() {
        assertEquals("Trim failure", "hello", new String(CharOperation.trim("   hello".toCharArray())));
    }

    public void test23() {
        assertEquals("Trim failure", "hello", new String(CharOperation.trim("   hello   ".toCharArray())));
    }

    public void test24() {
        assertEquals("Trim failure", "hello", new String(CharOperation.trim("hello   ".toCharArray())));
    }

    public void test25() {
        assertEquals("Trim failure", "", new String(CharOperation.trim("   ".toCharArray())));
    }

    public void test26() {
        assertEquals("Trim failure", "hello world", new String(CharOperation.trim(" hello world  ".toCharArray())));
    }

    public void test27() {
        char[][] tokens = CharOperation.splitAndTrimOn(',', " hello,world".toCharArray());
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < tokens.length; i++) {
            buffer.append('[').append(tokens[i]).append(']');
        }
        assertEquals("SplitTrim failure", "[hello][world]", buffer.toString());
    }

    public void test28() {
        char[][] tokens = CharOperation.splitAndTrimOn(',', " hello , world".toCharArray());
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < tokens.length; i++) {
            buffer.append('[').append(tokens[i]).append(']');
        }
        assertEquals("SplitTrim failure", "[hello][world]", buffer.toString());
    }

    public void test29() {
        char[][] tokens = CharOperation.splitAndTrimOn(',', " hello, world   ".toCharArray());
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < tokens.length; i++) {
            buffer.append('[').append(tokens[i]).append(']');
        }
        assertEquals("SplitTrim failure", "[hello][world]", buffer.toString());
    }

    public void test30() {
        char[][] tokens = CharOperation.splitAndTrimOn(',', " hello, world   ,zork/, aaa bbb".toCharArray());
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < tokens.length; i++) {
            buffer.append('[').append(tokens[i]).append(']');
        }
        assertEquals("SplitTrim failure", "[hello][world][zork/][aaa bbb]", buffer.toString());
    }

    public void test31() {
        char[][] tokens = CharOperation.splitAndTrimOn(',', "  ,  ".toCharArray());
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < tokens.length; i++) {
            buffer.append('[').append(tokens[i]).append(']');
        }
        assertEquals("SplitTrim failure", "[][]", buffer.toString());
    }

    public void test32() {
        char[][] tokens = CharOperation.splitAndTrimOn(',', "   ".toCharArray());
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < tokens.length; i++) {
            buffer.append('[').append(tokens[i]).append(']');
        }
        assertEquals("SplitTrim failure", "[]", buffer.toString());
    }

    public void test33() {
        char[][] tokens = CharOperation.splitAndTrimOn(',', "  , hello  ".toCharArray());
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < tokens.length; i++) {
            buffer.append('[').append(tokens[i]).append(']');
        }
        assertEquals("SplitTrim failure", "[][hello]", buffer.toString());
    }

    public void test34() {
        assertTrue("Path pattern matching failure", checkPathMatch("hello/*/World".toCharArray(), "hello/zzz/World".toCharArray(), true));
    }

    public void test35() {
        assertTrue("Path pattern matching failure", checkPathMatch("hello/**/World".toCharArray(), "hello/x/y/z/World".toCharArray(), true));
    }

    public void test36() {
        assertTrue("Path pattern matching failure", checkPathMatch("hello/**/World/**/*.java".toCharArray(), "hello/x/y/z/World/X.java".toCharArray(), true));
    }

    public void test37() {
        assertTrue("Path pattern matching failure", checkPathMatch("**/World/**/*.java".toCharArray(), "hello/x/y/z/World/X.java".toCharArray(), true));
    }

    public void test38() {
        assertTrue("Path pattern matching failure", !checkPathMatch("/*.java".toCharArray(), "/hello/x/y/z/World/X.java".toCharArray(), true));
    }

    public void test39() {
        assertTrue("Path pattern matching failure-1", checkPathMatch("**/CVS/*".toCharArray(), "CVS/Repository".toCharArray(), true));
        assertTrue("Path pattern matching failure-2", checkPathMatch("**/CVS/*".toCharArray(), "org/apache/CVS/Entries".toCharArray(), true));
        assertTrue("Path pattern matching failure-3", checkPathMatch("**/CVS/*".toCharArray(), "org/apache/jakarta/tools/ant/CVS/Entries".toCharArray(), true));
        assertTrue("Path pattern matching failure-4", !checkPathMatch("**/CVS/*".toCharArray(), "org/apache/CVS/foo/bar/Entries".toCharArray(), true));
    }

    public void test40() {
        assertTrue("Path pattern matching failure-1", checkPathMatch("org/apache/jakarta/**".toCharArray(), "org/apache/jakarta/tools/ant/docs/index.html".toCharArray(), true));
        assertTrue("Path pattern matching failure-2", checkPathMatch("org/apache/jakarta/**".toCharArray(), "org/apache/jakarta/test.xml".toCharArray(), true));
        assertTrue("Path pattern matching failure-3", !checkPathMatch("org/apache/jakarta/**".toCharArray(), "org/apache/xyz.java".toCharArray(), true));
    }

    public void test41() {
        assertTrue("Path pattern matching failure-1", checkPathMatch("org/apache/**/CVS/*".toCharArray(), "org/apache/CVS/Entries".toCharArray(), true));
        assertTrue("Path pattern matching failure-2", checkPathMatch("org/apache/**/CVS/*".toCharArray(), "org/apache/jakarta/tools/ant/CVS/Entries".toCharArray(), true));
        assertTrue("Path pattern matching failure-3", !checkPathMatch("org/apache/**/CVS/*".toCharArray(), "org/apache/CVS/foo/bar/Entries".toCharArray(), true));
    }

    public void test42() {
        assertTrue("Path pattern matching failure-1", checkPathMatch("**/test/**".toCharArray(), "org/apache/test/CVS/Entries".toCharArray(), true));
        assertTrue("Path pattern matching failure-2", checkPathMatch("**/test/**".toCharArray(), "test".toCharArray(), true));
        assertTrue("Path pattern matching failure-3", checkPathMatch("**/test/**".toCharArray(), "a/test".toCharArray(), true));
        assertTrue("Path pattern matching failure-4", checkPathMatch("**/test/**".toCharArray(), "test/a.java".toCharArray(), true));
        assertTrue("Path pattern matching failure-5", !checkPathMatch("**/test/**".toCharArray(), "org/apache/test.java".toCharArray(), true));
    }

    public void test43() {
        assertTrue("Path pattern matching failure-1", checkPathMatch("/test/".toCharArray(), "/test/CVS/Entries".toCharArray(), true));
        assertTrue("Path pattern matching failure-2", checkPathMatch("/test/**".toCharArray(), "/test/CVS/Entries".toCharArray(), true));
    }

    public void test44() {
        assertTrue("Path pattern matching failure-1", !checkPathMatch("test".toCharArray(), "test/CVS/Entries".toCharArray(), true));
        assertTrue("Path pattern matching failure-2", !checkPathMatch("**/test".toCharArray(), "test/CVS/Entries".toCharArray(), true));
    }

    public void test45() {
        assertTrue("Path pattern matching failure-1", checkPathMatch("/test/test1/".toCharArray(), "/test/test1/test/test1".toCharArray(), true));
        assertTrue("Path pattern matching failure-2", !checkPathMatch("/test/test1".toCharArray(), "/test/test1/test/test1".toCharArray(), true));
    }

    public void test46() {
        assertTrue("Path pattern matching failure", checkPathMatch("hello/**/World".toCharArray(), "hello/World".toCharArray(), true));
    }

    public void test47() {
        assertTrue("Pattern matching failure", CharOperation.match("*x".toCharArray(), "x.X".toCharArray(), false));
    }

    public void test48() {
        assertTrue("Pattern matching failure", CharOperation.match("*a*".toCharArray(), "abcd".toCharArray(), false));
    }

    public void test49() {
        assertTrue("Path pattern matching failure", checkPathMatch("**/hello".toCharArray(), "hello/hello".toCharArray(), true));
    }

    public void test50() {
        assertTrue("Path pattern matching failure", checkPathMatch("**/hello/**".toCharArray(), "hello/hello".toCharArray(), true));
    }

    public void test51() {
        assertTrue("Path pattern matching failure", checkPathMatch("**/hello/".toCharArray(), "hello/hello".toCharArray(), true));
    }

    public void test52() {
        assertTrue("Path pattern matching failure", checkPathMatch("hello/".toCharArray(), "hello/hello".toCharArray(), true));
    }

    public void test53() {
        assertTrue("Path pattern matching failure", !checkPathMatch("/".toCharArray(), "hello/hello".toCharArray(), true));
    }

    public void test54() {
        assertTrue("Path pattern matching failure-1", !checkPathMatch("x/".toCharArray(), "hello/x".toCharArray(), true));
        assertTrue("Path pattern matching failure-2", checkPathMatch("**/x/".toCharArray(), "hello/x".toCharArray(), true));
        assertTrue("Path pattern matching failure-3", !checkPathMatch("/x/".toCharArray(), "hello/x".toCharArray(), true));
    }

    public void test56() {
        assertTrue("Path pattern matching failure", !checkPathMatch("/**".toCharArray(), "hello/hello".toCharArray(), true));
    }

    public void test57() {
        assertTrue("Path pattern matching failure", checkPathMatch("/".toCharArray(), "/hello/hello".toCharArray(), true));
    }

    public void test58() {
        assertTrue("Path pattern matching failure", checkPathMatch("/**".toCharArray(), "/hello/hello".toCharArray(), true));
    }

    public void test59() {
        assertTrue("Path pattern matching failure", !checkPathMatch("**".toCharArray(), "/hello/hello".toCharArray(), true));
    }

    public void test60() {
        assertTrue("Path pattern matching failure-1", !checkPathMatch("/P/src".toCharArray(), "/P/src/X".toCharArray(), true));
        assertTrue("Path pattern matching failure-2", !checkPathMatch("/P/**/src".toCharArray(), "/P/src/X".toCharArray(), true));
        assertTrue("Path pattern matching failure-3", checkPathMatch("/P/src".toCharArray(), "/P/src".toCharArray(), true));
        assertTrue("Path pattern matching failure-4", !checkPathMatch("A.java".toCharArray(), "/P/src/A.java".toCharArray(), true));
    }

    public void test61() {
        assertTrue("Path pattern matching failure-1", checkPathMatch("/P/src/**/CVS".toCharArray(), "/P/src/CVS".toCharArray(), true));
        assertTrue("Path pattern matching failure-2", checkPathMatch("/P/src/**/CVS/".toCharArray(), "/P/src/CVS".toCharArray(), true));
    }

    public void test62() {
        assertCamelCase("NPE", "NullPointerException", true);
        assertCamelCase("NPExc", "NullPointerException", true);
        assertCamelCase("NPoE", "NullPointerException", true);
        assertCamelCase("NuPExc", "NullPointerException", true);
        assertTrue(this.camelCaseErrors.toString(), this.camelCaseErrors.length() == 0);
    }

    public void test63() {
        assertCamelCase("NPEX", "NullPointerException", false);
        assertCamelCase("NPex", "NullPointerException", false);
        assertCamelCase("npe", "NullPointerException", false);
        assertCamelCase("npe", "NPException", false);
        assertCamelCase("NPointerE", "NullPointerException", true);
        assertTrue(this.camelCaseErrors.toString(), this.camelCaseErrors.length() == 0);
    }

    public void test64() {
        assertCamelCase("IAE", "IgnoreAllErrorHandler", true);
        assertCamelCase("IAE", "IAnchorElement", true);
        assertCamelCase("IAnchorEleme", "IAnchorElement", true);
        assertCamelCase("", "IAnchorElement", false);
        assertCamelCase(null, "IAnchorElement", true);
        assertCamelCase("", "", true);
        assertCamelCase("IAnchor", null, false);
        assertTrue(this.camelCaseErrors.toString(), this.camelCaseErrors.length() == 0);
    }

    public void test65() {
        assertCamelCase("iSCDCo", "invokeStringConcatenationDefaultConstructor", true);
        assertCamelCase("inVOke", "invokeStringConcatenationDefaultConstructor", false);
        assertCamelCase("i", "invokeStringConcatenationDefaultConstructor", true);
        assertCamelCase("I", "invokeStringConcatenationDefaultConstructor", false);
        assertCamelCase("iStringCD", "invokeStringConcatenationDefaultConstructor", true);
        assertCamelCase("NPE", "NullPointerException/java.lang", true);
        assertCamelCase("NPE", "NullPointer/lang.Exception", false);
        assertCamelCase("NPE", "Null_Pointer$Exception", true);
        assertCamelCase("NPE", "Null1Pointer2Exception", true);
        assertCamelCase("NPE", "Null.Pointer.Exception", false);
        assertCamelCase("NPE", "aNullPointerException", false);
        assertCamelCase("nullP", "nullPointerException", true);
        assertCamelCase("nP", "nullPointerException", true);
        assertTrue(this.camelCaseErrors.toString(), this.camelCaseErrors.length() == 0);
    }

    public void test66() {
        String[][] MATCHES = { { "TZ", "TimeZone" }, { "TiZ", "TimeZone" }, { "TiZon", "TimeZone" }, { "TZon", "TimeZone" }, { "TZone", "TimeZone" }, { "TimeZone", "TimeZone" }, { "TimeZ", "TimeZ" }, { "TZ", "TimeZ" }, { "T", "TimeZ" }, { "T", "TimeZone" }, { "TZ", "TZ" }, { "aT", "aTimeZone" }, { "aTi", "aTimeZone" }, { "aTiZ", "aTimeZone" }, { "aTZ", "aTimeZone" }, { "aT", "artTimeZone" }, { "aTi", "artTimeZone" }, { "aTiZ", "artTimeZone" }, { "aTZ", "artTimeZone" } };
        for (int i = 0; i < MATCHES.length; i++) {
            String[] match = MATCHES[i];
            assertCamelCase(match[0], match[1], true);
        }
        String[][] MIS_MATCHES = { { "TZ", "Timezone" }, { "aTZ", "TimeZone" }, { "aTZ", "TZ" }, { "arT", "aTimeZone" }, { "arTi", "aTimeZone" }, { "arTiZ", "aTimeZone" }, { "arTZ", "aTimeZone" }, { "aT", "atimeZone" } };
        for (int i = 0; i < MIS_MATCHES.length; i++) {
            String[] match = MIS_MATCHES[i];
            assertCamelCase(match[0], match[1], false);
        }
        assertTrue(this.camelCaseErrors.toString(), this.camelCaseErrors.length() == 0);
    }

    public void test67() {
        assertCamelCase("runtimeEx", "RuntimeException", false);
        assertCamelCase("Runtimeex", "RuntimeException", false);
        assertCamelCase("runtimeexception", "RuntimeException", false);
        assertCamelCase("Runtimexception", "RuntimeException", false);
        assertCamelCase("illegalMSException", "IllegalMonitorStateException", false);
        assertCamelCase("illegalMsException", "IllegalMonitorStateException", false);
        assertCamelCase("IllegalMSException", "IllegalMonitorStateException", true);
        assertCamelCase("IllegalMsException", "IllegalMonitorStateException", false);
        assertCamelCase("clonenotsupportedex", "CloneNotSupportedException", false);
        assertCamelCase("CloneNotSupportedEx", "CloneNotSupportedException", true);
        assertCamelCase("cloneNotsupportedEx", "CloneNotSupportedException", false);
        assertCamelCase("ClonenotSupportedexc", "CloneNotSupportedException", false);
        assertCamelCase("cloneNotSupportedExcep", "CloneNotSupportedException", false);
        assertCamelCase("Clonenotsupportedexception", "CloneNotSupportedException", false);
        assertCamelCase("CloneNotSupportedException", "CloneNotSupportedException", true);
        assertTrue(this.camelCaseErrors.toString(), this.camelCaseErrors.length() == 0);
    }

    public void test68() {
        assertCamelCase("aMe", "aMethod", true);
        assertCamelCase("ame", "aMethod", false);
        assertCamelCase("longNOM", "longNameOfMethod", true);
        assertCamelCase("longNOMeth", "longNameOfMethod", true);
        assertCamelCase("longNOMethod", "longNameOfMethod", true);
        assertCamelCase("longNoMethod", "longNameOfMethod", false);
        assertTrue(this.camelCaseErrors.toString(), this.camelCaseErrors.length() == 0);
    }

    public void test69() {
        assertCamelCase("aa", "AxxAyy", false);
        assertCamelCase("Aa", "AxxAyy", false);
        assertCamelCase("aA", "AxxAyy", false);
        assertCamelCase("AA", "AxxAyy", true);
        assertCamelCase("aa", "AbcdAbcdefAbcAbcdefghAbAAzzzzAbcdefghijklmnopqrstuvwxyzAbcdefghijklmnAbcAbcdefghijklm", false);
        assertCamelCase("AA", "AbcdAbcdefAbcAbcdefghAbAAzzzzAbcdefghijklmnopqrstuvwxyzAbcdefghijklmnAbcAbcdefghijklm", true);
        assertTrue(this.camelCaseErrors.toString(), this.camelCaseErrors.length() == 0);
    }

    public void test70() throws CoreException {
        assertCamelCase("IDE3", "IDocumentExtension", true, false);
        assertCamelCase("IDE3", "IDocumentExtension2", true, false);
        assertCamelCase("IDE3", "IDocumentExtension3", true, true);
        assertCamelCase("IDE3", "IDocumentExtension135", true, true);
        assertCamelCase("IDE3", "IDocumentExtension315", true, true);
        assertCamelCase("IDPE3", "IDocumentProviderExtension", true, false);
        assertCamelCase("IDPE3", "IDocumentProviderExtension2", true, false);
        assertCamelCase("IDPE3", "IDocumentProviderExtension4", true, false);
        assertCamelCase("IDPE3", "IDocumentProviderExtension3", true, true);
        assertCamelCase("IDPE3", "IDocumentProviderExtension5", true, false);
        assertCamelCase("IDPE3", "IDocumentProviderExtension54321", true, true);
        assertCamelCase("IDPE3", "IDocumentProviderExtension12345", true, true);
        assertCamelCase("IPL3", "IPerspectiveListener", true, false);
        assertCamelCase("IPL3", "IPerspectiveListener2", true, false);
        assertCamelCase("IPL3", "IPerspectiveListener3", true, true);
        assertCamelCase("IPS2", "IPropertySource", true, false);
        assertCamelCase("IPS2", "IPropertySource2", true, true);
        assertCamelCase("IWWPD2", "IWorkbenchWindowPulldownDelegate", true, false);
        assertCamelCase("IWWPD2", "IWorkbenchWindowPulldownDelegate2", true, true);
        assertCamelCase("UTF16DSS", "UTF16DocumentScannerSupport", true, true);
        assertCamelCase("UTF16DSS", "UTF1DocScannerSupport", true, false);
        assertCamelCase("UTF16DSS", "UTF6DocScannerSupport", true, false);
        assertCamelCase("UTF16DSS", "UTFDocScannerSupport", true, false);
        assertCamelCase("UTF1DSS", "UTF16DocumentScannerSupport", true, true);
        assertCamelCase("UTF1DSS", "UTF1DocScannerSupport", true, true);
        assertCamelCase("UTF1DSS", "UTF6DocScannerSupport", true, false);
        assertCamelCase("UTF1DSS", "UTFDocScannerSupport", true, false);
        assertCamelCase("UTF6DSS", "UTF16DocumentScannerSupport", true, true);
        assertCamelCase("UTF6DSS", "UTF1DocScannerSupport", true, false);
        assertCamelCase("UTF6DSS", "UTF6DocScannerSupport", true, true);
        assertCamelCase("UTF6DSS", "UTFDocScannerSupport", true, false);
        assertCamelCase("UTFDSS", "UTF16DocumentScannerSupport", true, true);
        assertCamelCase("UTFDSS", "UTF1DocScannerSupport", true, true);
        assertCamelCase("UTFDSS", "UTF6DocScannerSupport", true, true);
        assertCamelCase("UTFDSS", "UTFDocScannerSupport", true, true);
        assertTrue(this.camelCaseErrors.toString(), this.camelCaseErrors.length() == 0);
    }

    public void test71() {
        assertCamelCase("HM", "HashMap", true, true);
        assertCamelCase("HM", "HtmlMapper", true, true);
        assertCamelCase("HM", "HashMapEntry", true, false);
        assertCamelCase("HaM", "HashMap", true, true);
        assertCamelCase("HaM", "HtmlMapper", true, false);
        assertCamelCase("HaM", "HashMapEntry", true, false);
        assertCamelCase("HashM", "HashMap", true, true);
        assertCamelCase("HashM", "HtmlMapper", true, false);
        assertCamelCase("HashM", "HashMapEntry", true, false);
        assertTrue(this.camelCaseErrors.toString(), this.camelCaseErrors.length() == 0);
    }

    public void test71b() {
        assertCamelCase("HM", "HashMap", true);
        assertCamelCase("HM", "HtmlMapper", true);
        assertCamelCase("HM", "HashMapEntry", true);
        assertCamelCase("HaM", "HashMap", true);
        assertCamelCase("HaM", "HtmlMapper", false);
        assertCamelCase("HaM", "HashMapEntry", true);
        assertCamelCase("HashM", "HashMap", true);
        assertCamelCase("HashM", "HtmlMapper", false);
        assertCamelCase("HashM", "HashMapEntry", true);
        assertTrue(this.camelCaseErrors.toString(), this.camelCaseErrors.length() == 0);
    }

    public void test72() {
        assertCamelCase("HMa", "HashMap", true, true);
        assertCamelCase("HMa", "HtmlMapper", true, true);
        assertCamelCase("HMa", "HashMapEntry", true, false);
        assertCamelCase("HaMa", "HashMap", true, true);
        assertCamelCase("HaMa", "HtmlMapper", true, false);
        assertCamelCase("HaMa", "HashMapEntry", true, false);
        assertCamelCase("HashMa", "HashMap", true, true);
        assertCamelCase("HashMa", "HtmlMapper", true, false);
        assertCamelCase("HashMa", "HashMapEntry", true, false);
        assertTrue(this.camelCaseErrors.toString(), this.camelCaseErrors.length() == 0);
    }

    public void test72b() {
        assertCamelCase("HMa", "HashMap", true);
        assertCamelCase("HMa", "HtmlMapper", true);
        assertCamelCase("HMa", "HashMapEntry", true);
        assertCamelCase("HaMa", "HashMap", true);
        assertCamelCase("HaMa", "HtmlMapper", false);
        assertCamelCase("HaMa", "HashMapEntry", true);
        assertCamelCase("HashMa", "HashMap", true);
        assertCamelCase("HashMa", "HtmlMapper", false);
        assertCamelCase("HashMa", "HashMapEntry", true);
        assertTrue(this.camelCaseErrors.toString(), this.camelCaseErrors.length() == 0);
    }

    public void test73() {
        assertCamelCase("HMap", "HashMap", true, true);
        assertCamelCase("HMap", "HtmlMapper", true, true);
        assertCamelCase("HMap", "HashMapEntry", true, false);
        assertCamelCase("HaMap", "HashMap", true, true);
        assertCamelCase("HaMap", "HtmlMapper", true, false);
        assertCamelCase("HaMap", "HashMapEntry", true, false);
        assertCamelCase("HashMap", "HashMap", true, true);
        assertCamelCase("HashMap", "HtmlMapper", true, false);
        assertCamelCase("HashMap", "HashMapEntry", true, false);
        assertTrue(this.camelCaseErrors.toString(), this.camelCaseErrors.length() == 0);
    }

    public void test73b() {
        assertCamelCase("HMap", "HashMap", true);
        assertCamelCase("HMap", "HtmlMapper", true);
        assertCamelCase("HMap", "HashMapEntry", true);
        assertCamelCase("HaMap", "HashMap", true);
        assertCamelCase("HaMap", "HtmlMapper", false);
        assertCamelCase("HaMap", "HashMapEntry", true);
        assertCamelCase("HashMap", "HashMap", true);
        assertCamelCase("HashMap", "HtmlMapper", false);
        assertCamelCase("HashMap", "HashMapEntry", true);
        assertTrue(this.camelCaseErrors.toString(), this.camelCaseErrors.length() == 0);
    }

    public static Class testClass() {
        return UtilTest.class;
    }
}
