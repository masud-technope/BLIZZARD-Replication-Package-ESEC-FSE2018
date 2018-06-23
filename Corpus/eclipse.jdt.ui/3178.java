/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.ui.tests.refactoring.nls;

import java.util.Properties;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.jdt.testplugin.JavaProjectHelper;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.resources.IFile;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.RefactoringStatusEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.internal.corext.refactoring.nls.NLSMessages;
import org.eclipse.jdt.internal.corext.refactoring.nls.NLSRefactoring;
import org.eclipse.jdt.internal.corext.refactoring.nls.NLSSubstitution;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.ui.tests.core.ProjectTestSetup;
import org.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;

public class NlsRefactoringCheckFinalConditionsTest extends TestCase {

    private static final Class<NlsRefactoringCheckFinalConditionsTest> THIS = NlsRefactoringCheckFinalConditionsTest.class;

    //private IPath fPropertyFilePath;
    private IPackageFragment fAccessorPackage;

    private String fAccessorClassName;

    private String fSubstitutionPattern;

    private NlsRefactoringTestHelper fHelper;

    private IJavaProject javaProject;

    private IPackageFragment fResourceBundlePackage;

    private String fResourceBundleName;

    public  NlsRefactoringCheckFinalConditionsTest(String name) {
        super(name);
    }

    public static Test suite() {
        return setUpTest(new TestSuite(THIS));
    }

    public static Test setUpTest(Test test) {
        return new ProjectTestSetup(test);
    }

    @Override
    protected void setUp() throws Exception {
        javaProject = ProjectTestSetup.getProject();
        fHelper = new NlsRefactoringTestHelper(javaProject);
    }

    @Override
    protected void tearDown() throws Exception {
        JavaProjectHelper.clear(javaProject, ProjectTestSetup.getDefaultClasspath());
    }

    public void testCheckInputWithoutExistingPropertiesFile() throws Exception {
        ICompilationUnit cu = fHelper.getCu("/TestSetupProject/src1/p/WithStrings.java");
        IFile propertyFile = fHelper.getFile("/TestSetupProject/src2/p/test.properties");
        propertyFile.delete(false, fHelper.fNpm);
        initDefaultValues();
        RefactoringStatus res = createCheckInputStatus(cu);
        assertFalse("should info about properties", res.isOK());
        assertEquals("one info", 1, res.getEntries().length);
        RefactoringStatusEntry help = res.getEntryAt(0);
        assertEquals("info", RefactoringStatus.INFO, help.getSeverity());
        assertEquals(Messages.format(NLSMessages.NLSRefactoring_will_be_created, BasicElementLabels.getPathLabel(propertyFile.getFullPath(), false)), help.getMessage());
    }

    /*
	 * no substitutions -> nothing to do
	 */
    public void testCheckInputWithNoSubstitutions() throws Exception {
        //$NON-NLS-1$
        ICompilationUnit cu = fHelper.getCu("/TestSetupProject/src1/p/WithoutStrings.java");
        initDefaultValues();
        checkNothingToDo(createCheckInputStatus(cu));
    }

    /*
	 * substitution checks
	 */
    public void testCheckInputWithSubstitutionPatterns() throws Exception {
        //$NON-NLS-1$
        ICompilationUnit cu = fHelper.getCu("/TestSetupProject/src1/p/WithStrings.java");
        initDefaultValues();
        //$NON-NLS-1$
        fSubstitutionPattern = "";
        RefactoringStatus res = createCheckInputStatus(cu);
        RefactoringStatusEntry[] results = res.getEntries();
        //$NON-NLS-1$
        assertEquals("substitution pattern must be given", 2, results.length);
        //$NON-NLS-1$
        assertEquals("first is fatal", RefactoringStatus.ERROR, results[0].getSeverity());
        assertEquals(//$NON-NLS-1$
        "right fatal message", NLSMessages.NLSRefactoring_pattern_empty, results[0].getMessage());
        assertEquals(//$NON-NLS-1$
        "warning no key given", //$NON-NLS-1$
        RefactoringStatus.WARNING, results[1].getSeverity());
        assertEquals(//$NON-NLS-1$
        "right warning message", Messages.format(NLSMessages.NLSRefactoring_pattern_does_not_contain, "${key}"), //$NON-NLS-1$
        results[1].getMessage());
        //$NON-NLS-1$
        fSubstitutionPattern = "blabla${key}";
        res = createCheckInputStatus(cu);
        //$NON-NLS-1$
        assertTrue("substitution pattern ok", res.isOK());
        //$NON-NLS-1$
        fSubstitutionPattern = "${key}blabla${key}";
        res = createCheckInputStatus(cu);
        //$NON-NLS-1$
        assertFalse("substitution pattern ko", res.isOK());
        results = res.getEntries();
        //$NON-NLS-1$
        assertEquals("one warning", 1, results.length);
        //$NON-NLS-1$
        assertEquals("warning", RefactoringStatus.WARNING, results[0].getSeverity());
        assertEquals(//$NON-NLS-1$
        "warning message", Messages.format(NLSMessages.NLSRefactoring_Only_the_first_occurrence_of, "${key}"), //$NON-NLS-1$
        results[0].getMessage());
    // check for duplicate keys????
    // check for keys already defined
    // check for keys
    }

    private RefactoringStatus createCheckInputStatus(ICompilationUnit cu) throws CoreException {
        NLSRefactoring refac = prepareRefac(cu);
        RefactoringStatus res = refac.checkFinalConditions(fHelper.fNpm);
        return res;
    }

    private void initDefaultValues() {
        //fPropertyFilePath= fHelper.getFile("/TestSetupProject/src2/p/test.properties").getFullPath(); //$NON-NLS-1$
        fResourceBundlePackage = fHelper.getPackageFragment("/TestSetupProject/src2/p");
        fResourceBundleName = "test.properties";
        //$NON-NLS-1$
        fAccessorPackage = fHelper.getPackageFragment("/TestSetupProject/src1/p");
        //$NON-NLS-1$
        fAccessorClassName = "Help";
        //$NON-NLS-1$
        fSubstitutionPattern = "${key}";
    }

    private NLSRefactoring prepareRefac(ICompilationUnit cu) {
        NLSRefactoring refac = NLSRefactoring.create(cu);
        NLSSubstitution[] subs = refac.getSubstitutions();
        refac.setPrefix("");
        for (int i = 0; i < subs.length; i++) {
            subs[i].setState(NLSSubstitution.EXTERNALIZED);
            subs[i].generateKey(subs, new Properties());
        }
        fillInValues(refac);
        return refac;
    }

    private void checkNothingToDo(RefactoringStatus status) {
        //$NON-NLS-1$
        assertEquals("fatal error expected", 1, status.getEntries().length);
        RefactoringStatusEntry fatalError = status.getEntryAt(0);
        //$NON-NLS-1$
        assertEquals("fatalerror", RefactoringStatus.FATAL, fatalError.getSeverity());
        assertEquals(//$NON-NLS-1$
        "errormessage", NLSMessages.NLSRefactoring_nothing_to_do, fatalError.getMessage());
    }

    private void fillInValues(NLSRefactoring refac) {
        refac.setAccessorClassPackage(fAccessorPackage);
        //refac.setPropertyFilePath(fPropertyFilePath);
        refac.setResourceBundleName(fResourceBundleName);
        refac.setResourceBundlePackage(fResourceBundlePackage);
        refac.setAccessorClassName(fAccessorClassName);
        refac.setSubstitutionPattern(fSubstitutionPattern);
    }
}
