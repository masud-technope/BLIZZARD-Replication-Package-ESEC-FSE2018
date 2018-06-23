/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.core.tests.model;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import junit.framework.Test;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.NamingConventions;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.IJavaProject;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class NamingConventionTests extends AbstractJavaModelTests {

    IJavaProject project;

    Hashtable oldOptions;

    public  NamingConventionTests(String name) {
        super(name);
    }

    public static Test suite() {
        return buildModelTestSuite(NamingConventionTests.class);
    }

    /**
 * Setup for the next test.
 */
    public void setUp() throws Exception {
        super.setUp();
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        this.project = createJavaProject("P", new String[] { "src" }, "bin");
        this.oldOptions = JavaCore.getOptions();
    //	this.abortOnFailure = false; // some tests have failing one time on macos boxes => do not abort on failures
    }

    /**
 * Cleanup after the previous test.
 */
    public void tearDown() throws Exception {
        JavaCore.setOptions(this.oldOptions);
        //$NON-NLS-1$
        this.deleteProject("P");
        super.tearDown();
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=38111
 */
    public void testGetBaseName001() {
        String baseName = NamingConventions.getBaseName(NamingConventions.VK_INSTANCE_FIELD, //$NON-NLS-1$
        "OneName", this.project);
        assertEquals(//$NON-NLS-1$
        "oneName", baseName);
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=38111
 */
    public void testGetBaseName002() {
        String baseName = NamingConventions.getBaseName(NamingConventions.VK_STATIC_FINAL_FIELD, //$NON-NLS-1$
        "ONE_NAME", this.project);
        assertEquals(//$NON-NLS-1$
        "oneName", baseName);
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=38111
 */
    public void testGetBaseName003() {
        Hashtable options = JavaCore.getOptions();
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_PREFIXES, "pre");
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_SUFFIXES, "suf");
        JavaCore.setOptions(options);
        String baseName = NamingConventions.getBaseName(NamingConventions.VK_INSTANCE_FIELD, //$NON-NLS-1$
        "preOneNamesuf", this.project);
        assertEquals(//$NON-NLS-1$
        "oneName", baseName);
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=38111
 */
    public void testGetBaseName004() {
        Hashtable options = JavaCore.getOptions();
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_STATIC_FINAL_FIELD_PREFIXES, "pre");
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_STATIC_FINAL_FIELD_SUFFIXES, "suf");
        JavaCore.setOptions(options);
        String baseName = NamingConventions.getBaseName(NamingConventions.VK_STATIC_FINAL_FIELD, //$NON-NLS-1$
        "preONE_NAMEsuf", this.project);
        assertEquals(//$NON-NLS-1$
        "oneName", baseName);
    }

    public void testSuggestFieldName001() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "OneName", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "oneName\n" + //$NON-NLS-1$
        "name", toString(suggestions));
    }

    public void testSuggestFieldName002() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "OneClass", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "oneClass\n" + //$NON-NLS-1$
        "class1", toString(suggestions));
    }

    public void testSuggestFieldName003() {
        Hashtable options = JavaCore.getOptions();
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_PREFIXES, "f");
        JavaCore.setOptions(options);
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "OneName", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "fOneName\n" + //$NON-NLS-1$
        "fName\n" + //$NON-NLS-1$
        "oneName\n" + //$NON-NLS-1$
        "name", toString(suggestions));
    }

    public void testSuggestFieldName004() {
        Hashtable options = JavaCore.getOptions();
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_PREFIXES, "_");
        JavaCore.setOptions(options);
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "OneName", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "_oneName\n" + //$NON-NLS-1$
        "_name\n" + //$NON-NLS-1$
        "oneName\n" + //$NON-NLS-1$
        "name", toString(suggestions));
    }

    public void testSuggestFieldName005() {
        Hashtable options = JavaCore.getOptions();
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_PREFIXES, "f");
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_STATIC_FIELD_PREFIXES, "fg");
        JavaCore.setOptions(options);
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_STATIC_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "OneName", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "fgOneName\n" + //$NON-NLS-1$
        "fgName\n" + //$NON-NLS-1$
        "oneName\n" + //$NON-NLS-1$
        "name", toString(suggestions));
    }

    public void testSuggestFieldName006() {
        Hashtable options = JavaCore.getOptions();
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_PREFIXES, "pre");
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_SUFFIXES, "suf");
        JavaCore.setOptions(options);
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "OneName", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "preOneNamesuf\n" + //$NON-NLS-1$
        "preNamesuf\n" + //$NON-NLS-1$
        "preOneName\n" + //$NON-NLS-1$
        "preName\n" + //$NON-NLS-1$
        "oneNamesuf\n" + //$NON-NLS-1$
        "namesuf\n" + //$NON-NLS-1$
        "oneName\n" + //$NON-NLS-1$
        "name", toString(suggestions));
    }

    public void testSuggestFieldName007() {
        Hashtable options = JavaCore.getOptions();
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_PREFIXES, "pre");
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_SUFFIXES, "suf");
        JavaCore.setOptions(options);
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "int", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "preIsuf\n" + //$NON-NLS-1$
        "preI\n" + //$NON-NLS-1$
        "isuf\n" + //$NON-NLS-1$
        "i", toString(suggestions));
    }

    public void testSuggestFieldName008() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "OneName", this.project, 0, //$NON-NLS-1$
        new String[] { "name" }, true);
        assumeEquals(//$NON-NLS-1$
        "oneName\n" + //$NON-NLS-1$
        "name2", toString(suggestions));
    }

    public void testSuggestFieldName009() {
        Hashtable options = JavaCore.getOptions();
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_PREFIXES, "pre");
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_SUFFIXES, "suf");
        JavaCore.setOptions(options);
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "OneName", this.project, 0, //$NON-NLS-1$
        new String[] { "preNamesuf" }, true);
        assumeEquals(//$NON-NLS-1$
        "preOneNamesuf\n" + //$NON-NLS-1$
        "preName2suf\n" + //$NON-NLS-1$
        "preOneName\n" + //$NON-NLS-1$
        "preName\n" + //$NON-NLS-1$
        "oneNamesuf\n" + //$NON-NLS-1$
        "namesuf\n" + //$NON-NLS-1$
        "oneName\n" + //$NON-NLS-1$
        "name", toString(suggestions));
    }

    public void testSuggestFieldName010() {
        Hashtable options = JavaCore.getOptions();
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_PREFIXES, "pre");
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_SUFFIXES, "suf");
        JavaCore.setOptions(options);
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "OneName", this.project, 1, //$NON-NLS-1$
        new String[] { "preNamesuf" }, true);
        assumeEquals(//$NON-NLS-1$
        "preOneNamessuf\n" + //$NON-NLS-1$
        "preNamessuf\n" + //$NON-NLS-1$
        "preOneNames\n" + //$NON-NLS-1$
        "preNames\n" + //$NON-NLS-1$
        "oneNamessuf\n" + //$NON-NLS-1$
        "namessuf\n" + //$NON-NLS-1$
        "oneNames\n" + //$NON-NLS-1$
        "names", toString(suggestions));
    }

    public void testSuggestFieldName011() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "Factory", this.project, 1, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "factories", toString(suggestions));
    }

    public void testSuggestFieldName012() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "FooBar", this.project, 0, //$NON-NLS-1$
        new String[] { "bar" }, true);
        assumeEquals(//$NON-NLS-1$
        "fooBar\n" + //$NON-NLS-1$
        "bar2", toString(suggestions));
    }

    public void testSuggestFieldName013() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "Class", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "class1", toString(suggestions));
    }

    public void testSuggestFieldName014() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "Class", this.project, 0, //$NON-NLS-1$
        new String[] { "class1" }, true);
        assumeEquals(//$NON-NLS-1$
        "class2", toString(suggestions));
    }

    public void testSuggestFieldName015() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "#", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "name", toString(suggestions));
    }

    public void testSuggestFieldName016() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "#", this.project, 0, //$NON-NLS-1$
        new String[] { "name" }, true);
        assumeEquals(//$NON-NLS-1$
        "name2", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=35356
 */
    public void testSuggestFieldName017() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "names", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "names", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=35356
 */
    public void testSuggestFieldName018() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "names", this.project, 1, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "names", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=35356
 */
    public void testSuggestFieldName019() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "MyClass", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "myClass\n" + //$NON-NLS-1$
        "class1", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=35356
 */
    public void testSuggestFieldName020() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "MyClass", this.project, 1, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "myClasses\n" + //$NON-NLS-1$
        "classes", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=38111
 */
    public void testSuggestFieldName021() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_STATIC_FINAL_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "MyType", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "MY_TYPE\n" + //$NON-NLS-1$
        "TYPE", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=38111
 */
    public void testSuggestFieldName022() {
        Hashtable options = JavaCore.getOptions();
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_STATIC_FINAL_FIELD_PREFIXES, "pre");
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_STATIC_FINAL_FIELD_SUFFIXES, "suf");
        JavaCore.setOptions(options);
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_STATIC_FINAL_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "MyType", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "preMY_TYPEsuf\n" + //$NON-NLS-1$
        "preTYPEsuf\n" + //$NON-NLS-1$
        "preMY_TYPE\n" + //$NON-NLS-1$
        "preTYPE\n" + //$NON-NLS-1$
        "MY_TYPEsuf\n" + //$NON-NLS-1$
        "TYPEsuf\n" + //$NON-NLS-1$
        "MY_TYPE\n" + //$NON-NLS-1$
        "TYPE", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=38111
 */
    public void testSuggestFieldName023() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_NAME, //$NON-NLS-1$
        "oneName", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "oneName", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=38111
 */
    public void testSuggestFieldName024() {
        Hashtable options = JavaCore.getOptions();
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_PREFIXES, "pre");
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_SUFFIXES, "suf");
        JavaCore.setOptions(options);
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_NAME, //$NON-NLS-1$
        "oneName", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "preOneNamesuf\n" + //$NON-NLS-1$
        "preOneName\n" + //$NON-NLS-1$
        "oneNamesuf\n" + //$NON-NLS-1$
        "oneName", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=38111
 */
    public void testSuggestFieldName025() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_STATIC_FINAL_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "My_Type", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "MY_TYPE\n" + //$NON-NLS-1$
        "TYPE", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=38111
 */
    public void testSuggestFieldName026() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_STATIC_FINAL_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "_MyType", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "MY_TYPE\n" + //$NON-NLS-1$
        "TYPE", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=38111
 */
    public void testSuggestFieldName027() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_STATIC_FINAL_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "MyType_", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "MY_TYPE\n" + //$NON-NLS-1$
        "TYPE", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=38111
 */
    public void testSuggestFieldName028() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_STATIC_FINAL_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "MyTyp_e", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "MY_TYP_E\n" + //$NON-NLS-1$
        "TYP_E\n" + //$NON-NLS-1$
        "E", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=38111
 */
    public void testSuggestFieldName029() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "My1Type", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "my1Type\n" + //$NON-NLS-1$
        "type", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=38111
 */
    public void testSuggestFieldName030() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "M1yType", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "m1yType\n" + //$NON-NLS-1$
        "type", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=38111
 */
    public void testSuggestFieldName031() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "MY1Type", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "my1Type\n" + //$NON-NLS-1$
        "type", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=38111
 */
    public void testSuggestFieldName032() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "M1YType", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "m1yType\n" + //$NON-NLS-1$
        "type", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=38111
 */
    public void testSuggestFieldName033() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "My_First_Type", this.project, 0, new String[] {}, true);
        assumeEquals("my_First_Type\n" + "first_Type\n" + //$NON-NLS-1$
        "type", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=38111
 */
    public void testSuggestFieldName034() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "MY_FIRST_Type", this.project, 0, new String[] {}, true);
        assumeEquals("my_FIRST_Type\n" + "first_Type\n" + //$NON-NLS-1$
        "type", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=38111
 */
    public void testSuggestFieldName035() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "my_first_Type", this.project, 0, new String[] {}, true);
        assumeEquals("my_first_Type\n" + "first_Type\n" + //$NON-NLS-1$
        "type", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=38111
 */
    public void testSuggestFieldName036() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "MyFirst_9_Type", this.project, 0, new String[] {}, true);
        assumeEquals("myFirst_9_Type\n" + "first_9_Type\n" + //$NON-NLS-1$
        "type", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=38111
 */
    public void testSuggestFieldName037() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "AType", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "aType\n" + //$NON-NLS-1$
        "type", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=38111
 */
    public void testSuggestFieldName038() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "aType", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "aType\n" + //$NON-NLS-1$
        "type", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=255345
 */
    public void testSuggestFieldName039() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_STATIC_FINAL_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "A", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "A", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=255345
 */
    public void testSuggestFieldName040() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_STATIC_FINAL_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "int", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "INT", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=260840
 */
    public void testSuggestFieldName041() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "Key", this.project, 1, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "keys", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=260840
 */
    public void testSuggestFieldName042() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_STATIC_FINAL_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "Key", this.project, 1, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "KEYS", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=263786
 */
    public void testSuggestFieldName043() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "TheURI", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "theURI\n" + //$NON-NLS-1$
        "uri", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=263786
 */
    public void testSuggestFieldName044() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "URI", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "uri", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=263786
 */
    public void testSuggestFieldName045() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_TYPE_NAME, //$NON-NLS-1$
        "URIZork", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "uriZork\n" + //$NON-NLS-1$
        "zork", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=263786
 */
    public void testSuggestFieldName046() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_NAME, //$NON-NLS-1$
        "TheURI", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "theURI", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=263786
 */
    public void testSuggestFieldName047() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_NAME, //$NON-NLS-1$
        "URI", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "uri", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=263786
 */
    public void testSuggestFieldName048() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_NAME, //$NON-NLS-1$
        "URIZork", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "uriZork", toString(suggestions));
    }

    /** @deprecated */
    public void testRemovePrefixAndSuffixForFieldName001() {
        Hashtable options = JavaCore.getOptions();
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_PREFIXES, "pre");
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_SUFFIXES, "suf");
        JavaCore.setOptions(options);
        char[] name = NamingConventions.removePrefixAndSuffixForFieldName(this.project, //$NON-NLS-1$
        "preOneNamesuf".toCharArray(), 0);
        assumeEquals(//$NON-NLS-1$
        "oneName", new String(name));
    }

    /** @deprecated */
    public void testRemovePrefixAndSuffixForFieldName002() {
        Hashtable options = JavaCore.getOptions();
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_PREFIXES, "pr, pre");
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_SUFFIXES, "uf, suf");
        JavaCore.setOptions(options);
        char[] name = NamingConventions.removePrefixAndSuffixForFieldName(this.project, //$NON-NLS-1$
        "preOneNamesuf".toCharArray(), Flags.AccStatic);
        assumeEquals(//$NON-NLS-1$
        "preOneNamesuf", new String(name));
    }

    /** @deprecated */
    public void testRemovePrefixAndSuffixForFieldName003() {
        Hashtable options = JavaCore.getOptions();
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_PREFIXES, "pr, pre");
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_SUFFIXES, "uf, suf");
        JavaCore.setOptions(options);
        char[] name = NamingConventions.removePrefixAndSuffixForFieldName(this.project, //$NON-NLS-1$
        "preOneNamesuf".toCharArray(), 0);
        assumeEquals(//$NON-NLS-1$
        "oneName", new String(name));
    }

    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=114086
    /** @deprecated */
    public void testRemovePrefixAndSuffixForFieldName004() {
        Hashtable options = JavaCore.getOptions();
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_PREFIXES, "pre,");
        JavaCore.setOptions(options);
        char[] name = NamingConventions.removePrefixAndSuffixForFieldName(this.project, //$NON-NLS-1$
        "preOneName".toCharArray(), 0);
        assumeEquals(//$NON-NLS-1$
        "oneName", new String(name));
    }

    /** @deprecated */
    public void testRemovePrefixAndSuffixForLocalName001() {
        Hashtable options = JavaCore.getOptions();
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_LOCAL_PREFIXES, "pr, pre");
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_LOCAL_SUFFIXES, "uf, suf");
        JavaCore.setOptions(options);
        char[] name = NamingConventions.removePrefixAndSuffixForLocalVariableName(this.project, //$NON-NLS-1$
        "preOneNamesuf".toCharArray());
        assumeEquals(//$NON-NLS-1$
        "oneName", new String(name));
    }

    public void testSuggestGetterName001() {
        char[] suggestion = NamingConventions.suggestGetterName(this.project, //$NON-NLS-1$
        "fieldName".toCharArray(), 0, false, CharOperation.NO_CHAR_CHAR);
        assumeEquals(//$NON-NLS-1$
        "getFieldName", new String(suggestion));
    }

    public void testSuggestGetterName002() {
        char[] suggestion = NamingConventions.suggestGetterName(this.project, //$NON-NLS-1$
        "FieldName".toCharArray(), 0, false, CharOperation.NO_CHAR_CHAR);
        assumeEquals(//$NON-NLS-1$
        "getFieldName", new String(suggestion));
    }

    public void testSuggestGetterName003() {
        Hashtable options = JavaCore.getOptions();
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_PREFIXES, "pr, pre");
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_SUFFIXES, "uf, suf");
        JavaCore.setOptions(options);
        char[] suggestion = NamingConventions.suggestGetterName(this.project, //$NON-NLS-1$
        "preFieldName".toCharArray(), 0, false, CharOperation.NO_CHAR_CHAR);
        assumeEquals(//$NON-NLS-1$
        "getFieldName", new String(suggestion));
    }

    public void testSuggestGetterName004() {
        Hashtable options = JavaCore.getOptions();
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_PREFIXES, "pr, pre");
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_SUFFIXES, "uf, suf");
        JavaCore.setOptions(options);
        char[] suggestion = NamingConventions.suggestGetterName(this.project, //$NON-NLS-1$
        "preFieldNamesuf".toCharArray(), 0, false, CharOperation.NO_CHAR_CHAR);
        assumeEquals(//$NON-NLS-1$
        "getFieldName", new String(suggestion));
    }

    public void testSuggestGetterName005() {
        Hashtable options = JavaCore.getOptions();
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_PREFIXES, "pr, pre");
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_FIELD_SUFFIXES, "uf, suf");
        JavaCore.setOptions(options);
        char[] suggestion = NamingConventions.suggestGetterName(this.project, //$NON-NLS-1$
        "preFieldNamesuf".toCharArray(), 0, true, CharOperation.NO_CHAR_CHAR);
        assumeEquals(//$NON-NLS-1$
        "isFieldName", new String(suggestion));
    }

    public void testSuggestGetterName006() {
        char[] suggestion = NamingConventions.suggestGetterName(this.project, //$NON-NLS-1$
        "isSomething".toCharArray(), 0, true, CharOperation.NO_CHAR_CHAR);
        assumeEquals(//$NON-NLS-1$
        "isSomething", new String(suggestion));
    }

    public void testSuggestGetterName007() {
        char[] suggestion = NamingConventions.suggestGetterName(this.project, //$NON-NLS-1$
        "isSomething".toCharArray(), 0, false, CharOperation.NO_CHAR_CHAR);
        assumeEquals(//$NON-NLS-1$
        "getIsSomething", new String(suggestion));
    }

    //https://bugs.eclipse.org/bugs/show_bug.cgi?id=153125
    public void testSuggestGetterName008() {
        char[] suggestion = NamingConventions.suggestGetterName(this.project, //$NON-NLS-1$
        "éfield".toCharArray(), 0, false, CharOperation.NO_CHAR_CHAR);
        assumeEquals(//$NON-NLS-1$
        "getÉfield", new String(suggestion));
    }

    public void testSuggestGetterName009() {
        Hashtable options = JavaCore.getOptions();
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_STATIC_FINAL_FIELD_PREFIXES, "PRE_");
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_STATIC_FINAL_FIELD_SUFFIXES, "_SUF");
        JavaCore.setOptions(options);
        char[] suggestion = NamingConventions.suggestGetterName(this.project, //$NON-NLS-1$
        "PRE_FIELD_NAME_SUF".toCharArray(), Flags.AccStatic | Flags.AccFinal, false, CharOperation.NO_CHAR_CHAR);
        assumeEquals(//$NON-NLS-1$
        "getFieldName", new String(suggestion));
    }

    //https://bugs.eclipse.org/bugs/show_bug.cgi?id=154823
    public void testSuggestGetterName010() {
        char[] suggestion = NamingConventions.suggestGetterName(this.project, //$NON-NLS-1$
        "eMail".toCharArray(), 0, false, CharOperation.NO_CHAR_CHAR);
        assumeEquals(//$NON-NLS-1$
        "geteMail", new String(suggestion));
    }

    //https://bugs.eclipse.org/bugs/show_bug.cgi?id=154823
    public void testSuggestGetterName011() {
        char[] suggestion = NamingConventions.suggestGetterName(this.project, //$NON-NLS-1$
        "EMail".toCharArray(), 0, false, CharOperation.NO_CHAR_CHAR);
        assumeEquals(//$NON-NLS-1$
        "getEMail", new String(suggestion));
    }

    //https://bugs.eclipse.org/bugs/show_bug.cgi?id=154823
    public void testSuggestGetterName012() {
        char[] suggestion = NamingConventions.suggestGetterName(this.project, //$NON-NLS-1$
        "z".toCharArray(), 0, false, CharOperation.NO_CHAR_CHAR);
        assumeEquals(//$NON-NLS-1$
        "getZ", new String(suggestion));
    }

    //https://bugs.eclipse.org/bugs/show_bug.cgi?id=154823
    public void testSuggestGetterName013() {
        char[] suggestion = NamingConventions.suggestGetterName(this.project, //$NON-NLS-1$
        "Z".toCharArray(), 0, false, CharOperation.NO_CHAR_CHAR);
        assumeEquals(//$NON-NLS-1$
        "getZ", new String(suggestion));
    }

    public void testSuggestSetterName001() {
        char[] suggestion = NamingConventions.suggestSetterName(this.project, //$NON-NLS-1$
        "isSomething".toCharArray(), 0, true, CharOperation.NO_CHAR_CHAR);
        assumeEquals(//$NON-NLS-1$
        "setSomething", new String(suggestion));
    }

    public void testSuggestSetterName002() {
        char[] suggestion = NamingConventions.suggestSetterName(this.project, //$NON-NLS-1$
        "isSomething".toCharArray(), 0, false, CharOperation.NO_CHAR_CHAR);
        assumeEquals(//$NON-NLS-1$
        "setIsSomething", new String(suggestion));
    }

    public void testSuggestSetterName003() {
        Hashtable options = JavaCore.getOptions();
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_STATIC_FINAL_FIELD_PREFIXES, "PRE_");
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_STATIC_FINAL_FIELD_SUFFIXES, "_SUF");
        JavaCore.setOptions(options);
        char[] suggestion = NamingConventions.suggestSetterName(this.project, //$NON-NLS-1$
        "PRE_FIELD_NAME_SUF".toCharArray(), Flags.AccStatic | Flags.AccFinal, false, CharOperation.NO_CHAR_CHAR);
        assumeEquals(//$NON-NLS-1$
        "setFieldName", new String(suggestion));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=133562
 */
    public void testSuggestLocalName001() {
        Map options = this.project.getOptions(true);
        try {
            Map newOptions = new HashMap(options);
            newOptions.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_5);
            newOptions.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);
            newOptions.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);
            this.project.setOptions(newOptions);
            String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_LOCAL, NamingConventions.BK_TYPE_NAME, "Enum", this.project, 0, //$NON-NLS-1$
            new String[] { "o" }, true);
            assumeEquals(//$NON-NLS-1$
            "enum1", toString(suggestions));
        } finally {
            this.project.setOptions(options);
        }
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=133562
 */
    public void testSuggestLocalName002() {
        Map options = this.project.getOptions(true);
        try {
            Map newOptions = new HashMap(options);
            newOptions.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_5);
            newOptions.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);
            newOptions.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);
            this.project.setOptions(newOptions);
            String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_LOCAL, NamingConventions.BK_TYPE_NAME, "Enums", this.project, 0, //$NON-NLS-1$
            new String[] { "o" }, true);
            assumeEquals(//$NON-NLS-1$
            "enums", toString(suggestions));
        } finally {
            this.project.setOptions(options);
        }
    }

    public void testSuggestConstantFieldName001() {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_STATIC_FINAL_FIELD, NamingConventions.BK_NAME, //$NON-NLS-1$
        "__", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "__", toString(suggestions));
    }

    /*
 * bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=38111
 */
    public void testSuggestConstantFieldName002() {
        Hashtable options = JavaCore.getOptions();
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_STATIC_FINAL_FIELD_PREFIXES, "PRE");
        //$NON-NLS-1$
        options.put(JavaCore.CODEASSIST_STATIC_FINAL_FIELD_SUFFIXES, "SUF");
        JavaCore.setOptions(options);
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_STATIC_FINAL_FIELD, NamingConventions.BK_NAME, //$NON-NLS-1$
        "__", this.project, 0, new String[] {}, true);
        assumeEquals(//$NON-NLS-1$
        "PRE__SUF\n" + //$NON-NLS-1$
        "PRE__\n" + //$NON-NLS-1$
        "__SUF\n" + //$NON-NLS-1$
        "__", toString(suggestions));
    }

    /*
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=283539
 * Test that suggestions for parameters include the underscores as supplied 
 */
    public void testSuggestParamWithUnderscore() {
        Map options = this.project.getOptions(true);
        try {
            Map newOptions = new HashMap(options);
            this.project.setOptions(newOptions);
            String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_PARAMETER, NamingConventions.BK_TYPE_NAME, "lMin___Trigger__Period_usec", this.project, 0, //$NON-NLS-1$
            new String[] {}, true);
            assumeEquals("lMin___Trigger__Period_usec\n" + "min___Trigger__Period_usec\n" + "trigger__Period_usec\n" + "period_usec\n" + //$NON-NLS-1$
            "usec", toString(suggestions));
        } finally {
            this.project.setOptions(options);
        }
    }
}
