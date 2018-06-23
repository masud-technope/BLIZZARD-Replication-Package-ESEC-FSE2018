/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.ui.tests.browsing;

import java.io.File;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.jdt.testplugin.JavaProjectHelper;
import org.eclipse.jdt.testplugin.JavaTestPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.internal.ui.browsing.LogicalPackage;
import org.eclipse.jdt.internal.ui.util.CoreUtility;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.CPListElement;

public class PackagesViewContentProviderTests extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite(PackagesViewContentProviderTests.class.getName());
        //$JUnit-BEGIN$
        suite.addTestSuite(PackagesViewContentProviderTests.class);
        //$JUnit-END$
        return suite;
    }

    private IJavaProject fJProject1;

    private IJavaProject fJProject2;

    private IPackageFragmentRoot fRoot1;

    private IWorkspace fWorkspace;

    private IWorkbench fWorkbench;

    private MockPluginView fMyPart;

    private ITreeContentProvider fProvider;

    private IPackageFragmentRoot fArchiveFragmentRoot;

    private IPackageFragment fPackJunit;

    private IPackageFragment fPackJunitSamples;

    private IPackageFragment fPackJunitSamplesMoney;

    private IWorkbenchPage page;

    private IPackageFragmentRoot fRoot2;

    private IPackageFragment fPack12;

    private IPackageFragment fPack32;

    private IPackageFragment fPack42;

    private IPackageFragment fPack52;

    private IPackageFragment fPack62;

    private IPackageFragment fPack21;

    private IPackageFragment fPack61;

    private IPackageFragment fPack51;

    private IPackageFragment fPack41;

    private IPackageFragment fPack31;

    private IPackageFragment fPackDefault1;

    private IPackageFragment fPackDefault2;

    private IPackageFragment fPack17;

    private IPackageFragment fPack81;

    private IPackageFragment fPack91;

    private IPackageFragmentRoot fInternalJarRoot;

    private IPackageFragment fInternalPackDefault;

    private IPackageFragment fInternalPack3;

    private IPackageFragment fInternalPack4;

    private IPackageFragment fInternalPack5;

    private IPackageFragment fInternalPack10;

    private boolean fEnableAutoBuildAfterTesting;

    public  PackagesViewContentProviderTests(String name) {
        super(name);
    }

    //---------Test for getChildren-------------------
    public void testGetChildrenProjectWithSourceFolders() throws Exception {
        //create a logical package for packages with name "pack3"
        LogicalPackage cp = new LogicalPackage(fPack31);
        cp.add(fPack32);
        cp.add(fInternalPack3);
        //create a logical package for default package
        LogicalPackage defaultCp = new LogicalPackage(fPackDefault1);
        defaultCp.add(fPackDefault2);
        defaultCp.add(fInternalPackDefault);
        Object[] expectedChildren = new Object[] { fPack21, fPack12, cp, defaultCp };
        Object[] children = fProvider.getChildren(fJProject2);
        //$NON-NLS-1$
        assertTrue("Wrong children found for project", compareArrays(children, expectedChildren));
    }

    public void testGetChildrentMidLevelFragmentNotLogicalPackage() throws Exception {
        //initialise map
        fProvider.getChildren(fJProject2);
        Object[] expectedChildren = new Object[] { fPack17 };
        Object[] children = fProvider.getChildren(fPack12);
        //$NON-NLS-1$
        assertTrue("Wrong children found for PackageFragment", compareArrays(children, expectedChildren));
    }

    public void testGetChildrenBottomLevelFragment() throws Exception {
        Object[] expectedChildren = new Object[] {};
        Object[] children = fProvider.getChildren(fPack21);
        //$NON-NLS-1$
        assertTrue("Wrong children found for PackageFragment", compareArrays(children, expectedChildren));
    }

    public void testGetChildrenLogicalPackage() throws Exception {
        //create a logical package for packages with name "pack3"
        LogicalPackage cp = new LogicalPackage(fPack31);
        cp.add(fPack32);
        cp.add(fInternalPack3);
        //create a logical package for packages with name "pack3.pack4"
        LogicalPackage ChildCp1 = new LogicalPackage(fPack41);
        ChildCp1.add(fPack42);
        ChildCp1.add(fInternalPack4);
        //create a logical package for packages with name "pack3.pack5"
        LogicalPackage ChildCp2 = new LogicalPackage(fPack51);
        ChildCp2.add(fPack52);
        ChildCp2.add(fInternalPack5);
        //initialize Map
        fProvider.getChildren(fJProject2);
        Object[] expectedChildren = new Object[] { fPack81, ChildCp1, ChildCp2 };
        Object[] children = fProvider.getChildren(cp);
        //$NON-NLS-1$
        assertTrue("Wrong children found for project", compareArrays(children, expectedChildren));
    }

    public void testGetChildrenLogicalPackage2() throws Exception {
        //create a logical package for packages with name "pack3.pack4"
        LogicalPackage cp = new LogicalPackage(fPack41);
        cp.add(fPack42);
        cp.add(fInternalPack4);
        //create a logical package for packages with name "pack3"
        LogicalPackage cp3 = new LogicalPackage(fPack31);
        cp3.add(fPack32);
        cp3.add(fInternalPack3);
        fProvider.getChildren(fJProject2);
        fProvider.getChildren(cp3);
        Object[] expectedChildren = new Object[] { fPack91, fInternalPack10 };
        Object[] children = fProvider.getChildren(cp);
        //$NON-NLS-1$
        assertTrue("Wrong children found for project", compareArrays(children, expectedChildren));
    }

    public void testGetChildrenMidLevelFragmentInArchive() throws Exception {
        //initialise map
        fProvider.getChildren(fArchiveFragmentRoot);
        fProvider.getChildren(fPackJunit);
        Object[] expectedChildren = new Object[] { fPackJunitSamplesMoney };
        Object[] children = fProvider.getChildren(fPackJunitSamples);
        //$NON-NLS-1$
        assertTrue("wrong children found for a NON bottom PackageFragment in PackageFragmentRoot Archive", compareArrays(children, expectedChildren));
    }

    public void testGetChildrenBottomLevelFragmentInArchive() throws Exception {
        Object[] expectedChildren = new Object[] {};
        Object[] children = fProvider.getChildren(fPackJunitSamplesMoney);
        //$NON-NLS-1$
        assertTrue("wrong children found for a bottom PackageFragment in PackageFragmentRoot Archive", compareArrays(children, expectedChildren));
    }

    public void testGetChildrenSourceFolder() throws Exception {
        Object[] expectedChildren = new Object[] { fPack21, fPack31, fPackDefault1 };
        Object[] children = fProvider.getChildren(fRoot1);
        //$NON-NLS-1$
        assertTrue("Wrong children found for PackageFragmentRoot", compareArrays(children, expectedChildren));
    }

    public void testGetChildrenArchive() {
        //$NON-NLS-1$
        Object[] expectedChildren = new Object[] { fPackJunit, fArchiveFragmentRoot.getPackageFragment("") };
        Object[] children = fProvider.getChildren(fArchiveFragmentRoot);
        //$NON-NLS-1$
        assertTrue("Wrong child found for PackageFragmentRoot Archive", compareArrays(children, expectedChildren));
    }

    //---------------Get Parent Tests-----------------------------
    public void testGetParentArchive() throws Exception {
        Object parent = fProvider.getParent(fArchiveFragmentRoot);
        //$NON-NLS-1$
        assertTrue("Wrong parent found for PackageFragmentRoot Archive", parent == null);
    }

    public void testGetParentMidLevelFragmentInArchive() throws Exception {
        //initialise Map
        fProvider.getChildren(fArchiveFragmentRoot);
        fProvider.getChildren(fPackJunit);
        fProvider.getChildren(fPackJunitSamples);
        Object expectedParent = fPackJunitSamples;
        Object parent = fProvider.getParent(fPackJunitSamplesMoney);
        //$NON-NLS-1$
        assertTrue("Wrong parent found for a NON top level PackageFragment in an Archive", expectedParent.equals(parent));
    }

    public void testGetParentTopLevelFragmentInArchive() throws Exception {
        Object expectedParent = fPackJunit;
        Object parent = fProvider.getParent(fPackJunitSamples);
        //$NON-NLS-1$
        assertTrue("Wrong parent found for a top level PackageFragment in an Archive", expectedParent.equals(parent));
    }

    public void testGetParentTopLevelLogicalPackage() throws Exception {
        LogicalPackage cp = new LogicalPackage(fPack31);
        cp.add(fPack32);
        cp.add(fInternalPack3);
        Object parent = fProvider.getParent(cp);
        //$NON-NLS-1$
        assertTrue("Wrong parent found for a top level LogicalPackage", fJProject2.equals(parent));
    }

    public void testGetParentPackageFragmentWithLogicalPackageParent() throws Exception {
        LogicalPackage cp = new LogicalPackage(fPack31);
        cp.add(fPack32);
        cp.add(fInternalPack3);
        //initialize map
        fProvider.getChildren(fJProject2);
        Object parent = fProvider.getParent(fPack81);
        //$NON-NLS-1$
        assertTrue("Wrong parent found for a top level LogicalPackage", cp.equals(parent));
    }

    public void testGetParentOfLogicalPackagetWithLogicalPackageParent() throws Exception {
        LogicalPackage cp = new LogicalPackage(fPack41);
        cp.add(fPack42);
        cp.add(fInternalPack4);
        LogicalPackage parentCp = new LogicalPackage(fPack31);
        parentCp.add(fPack32);
        parentCp.add(fInternalPack3);
        //initialize map
        fProvider.getChildren(fJProject2);
        Object parent = fProvider.getParent(cp);
        //$NON-NLS-1$
        assertTrue("Wrong parent found for a top level LogicalPackage", parentCp.equals(parent));
    }

    //	public void testGetParentWithPFRootFocus(){
    //		//set up map
    //		Object[] children= fProvider.getChildren(fRoot1);
    //		for (int i= 0; i < children.length; i++) {
    //			Object object= children[i];
    //			fProvider.getChildren(object);
    //		}
    //
    //		Object parent= fProvider.getParent(fPack41);
    //		Object expectedParent= fPack31;
    //		assertTrue("Wrong parent found for a mid level Fragment with Root Focus", expectedParent.equals(parent));		//$NON-NLS-1$
    //	}
    public void testGetParentPFwithProjectFocus() {
        Object[] children = fProvider.getChildren(fJProject2);
        for (int i = 0; i < children.length; i++) {
            Object object = children[i];
            fProvider.getChildren(object);
        }
        LogicalPackage cp = new LogicalPackage(fPack31);
        cp.add(fPack32);
        cp.add(fInternalPack3);
        Object parent = fProvider.getParent(fPack41);
        //$NON-NLS-1$
        assertTrue("Wrong parent found for a mid level Fragment with Root Focus", cp.equals(parent));
    }

    public void testGetParentWithRootFocusAfterProjectFocus() {
        //set up map with project focus
        Object[] children = fProvider.getChildren(fJProject2);
        for (int i = 0; i < children.length; i++) {
            Object object = children[i];
            fProvider.getChildren(object);
        }
        LogicalPackage cp = new LogicalPackage(fPack31);
        cp.add(fPack32);
        cp.add(fInternalPack3);
        //create a logical package for default package
        LogicalPackage defaultCp = new LogicalPackage(fPackDefault1);
        defaultCp.add(fPackDefault2);
        defaultCp.add(fInternalPackDefault);
        Object[] expectedChildren = new Object[] { fPack21, fPack12, cp, defaultCp };
        //$NON-NLS-1$
        assertTrue("expected children of project with focus", compareArrays(children, expectedChildren));
        //set up map with root focus
        children = fProvider.getChildren(fRoot1);
        for (int i = 0; i < children.length; i++) {
            Object object = children[i];
            fProvider.getChildren(object);
        }
    //		Object parent= fProvider.getParent(fPack41);
    //		Object expectedParent= fPack31;
    //		assertTrue("Wrong parent found for a mid level Fragment with Project Focus", expectedParent.equals(parent));//$NON-NLS-1$
    }

    public void testGetParentTopLevelFragment() throws Exception {
        Object parent = fProvider.getParent(fPack21);
        Object expectedParent = fRoot1;
        //$NON-NLS-1$
        assertTrue("Wrong parent found for a top level PackageFragment", expectedParent.equals(parent));
    }

    public void testGetParentMidLevelFragment() throws Exception {
        Object expectedParent = fPack12;
        Object parent = fProvider.getParent(fPack17);
        //$NON-NLS-1$
        assertTrue("Wrong parent found for a NON top level PackageFragment", expectedParent.equals(parent));
    }

    public void testBug123424_1() throws Exception {
        IClasspathEntry[] rawClasspath = fJProject2.getRawClasspath();
        IClasspathEntry src1 = rawClasspath[1];
        CPListElement element = CPListElement.createFromExisting(src1, fJProject2);
        element.setAttribute(CPListElement.INCLUSION, new IPath[] { new Path("pack3/pack5/") });
        fJProject2.setRawClasspath(new IClasspathEntry[] { rawClasspath[0], element.getClasspathEntry(), rawClasspath[2] }, null);
        Object[] expectedChildren = new Object[] { fPack12.getResource(), fPack32.getResource() };
        Object[] children = fProvider.getChildren(fRoot2);
        //$NON-NLS-1$
        assertTrue("Wrong children found for project", compareArrays(children, expectedChildren));
    }

    public void testBug123424_2() throws Exception {
        IClasspathEntry[] rawClasspath = fJProject2.getRawClasspath();
        IClasspathEntry src1 = rawClasspath[1];
        CPListElement element = CPListElement.createFromExisting(src1, fJProject2);
        element.setAttribute(CPListElement.INCLUSION, new IPath[] { new Path("pack3/pack5/") });
        fJProject2.setRawClasspath(new IClasspathEntry[] { rawClasspath[0], element.getClasspathEntry(), rawClasspath[2] }, null);
        Object[] expectedChildren = new Object[] { fPack42.getResource(), fPack52 };
        Object[] children = fProvider.getChildren(fPack32.getResource());
        //$NON-NLS-1$
        assertTrue("Wrong children found for project", compareArrays(children, expectedChildren));
    }

    /*
	 * @see TestCase#setUp()
	 */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        fWorkspace = ResourcesPlugin.getWorkspace();
        assertNotNull(fWorkspace);
        IWorkspaceDescription workspaceDesc = fWorkspace.getDescription();
        fEnableAutoBuildAfterTesting = workspaceDesc.isAutoBuilding();
        if (fEnableAutoBuildAfterTesting)
            CoreUtility.setAutoBuilding(false);
        //$NON-NLS-1$//$NON-NLS-2$
        fJProject1 = JavaProjectHelper.createJavaProject("TestProject1", "bin");
        //$NON-NLS-1$//$NON-NLS-2$
        fJProject2 = JavaProjectHelper.createJavaProject("TestProject2", "bin");
        //$NON-NLS-1$
        assertNotNull("project1 null", fJProject1);
        //$NON-NLS-1$
        assertNotNull("project2 null", fJProject2);
        //------------set up project #1 : External Jar and zip file-------
        //$NON-NLS-1$
        IPackageFragmentRoot jdk = JavaProjectHelper.addVariableRTJar(fJProject1, "JRE_LIB_TEST", null, null);
        //$NON-NLS-1$
        assertTrue("jdk not found", jdk != null);
        //---Create zip file-------------------
        java.io.File junitSrcArchive = JavaTestPlugin.getDefault().getFileInPlugin(JavaProjectHelper.JUNIT_SRC_381);
        //$NON-NLS-1$
        assertTrue("junit src not found", junitSrcArchive != null && junitSrcArchive.exists());
        //$NON-NLS-1$
        fArchiveFragmentRoot = JavaProjectHelper.addSourceContainerWithImport(fJProject1, "src", junitSrcArchive, JavaProjectHelper.JUNIT_SRC_ENCODING);
        //$NON-NLS-1$
        fPackJunit = fArchiveFragmentRoot.getPackageFragment("junit");
        //$NON-NLS-1$
        fPackJunitSamples = fArchiveFragmentRoot.getPackageFragment("junit.samples");
        //$NON-NLS-1$
        fPackJunitSamplesMoney = fArchiveFragmentRoot.getPackageFragment("junit.samples.money");
        //$NON-NLS-1$
        assertNotNull("creating fPackJunit", fPackJunit);
        //$NON-NLS-1$
        assertNotNull("creating fPackJunitSamples", fPackJunitSamples);
        //$NON-NLS-1$
        assertNotNull("creating fPackJunitSamplesMoney", fPackJunitSamplesMoney);
        //$NON-NLS-1$
        fPackJunitSamplesMoney.getCompilationUnit("IMoney.java");
        //$NON-NLS-1$
        fPackJunitSamplesMoney.getCompilationUnit("Money.java");
        //$NON-NLS-1$
        fPackJunitSamplesMoney.getCompilationUnit("MoneyBag.java");
        //$NON-NLS-1$
        fPackJunitSamplesMoney.getCompilationUnit("MoneyTest.java");
        //java.io.File mylibJar= JavaTestPlugin.getDefault().getFileInPlugin(JavaProjectHelper.MYLIB);
        //assertTrue("lib not found", mylibJar != null && mylibJar.exists());//$NON-NLS-1$
        //JavaProjectHelper.addLibraryWithImport(fJProject1, new Path(mylibJar.getPath()), null, null);
        //----------------Set up internal jar----------------------------
        //$NON-NLS-1$
        File myInternalJar = JavaTestPlugin.getDefault().getFileInPlugin(new Path("testresources/compoundtest.jar"));
        //$NON-NLS-1$
        assertTrue("lib not found", myInternalJar != null && myInternalJar.exists());
        fInternalJarRoot = JavaProjectHelper.addLibraryWithImport(fJProject2, Path.fromOSString(myInternalJar.getPath()), null, null);
        //$NON-NLS-1$
        fInternalPackDefault = fInternalJarRoot.getPackageFragment("");
        //$NON-NLS-1$
        fInternalPack3 = fInternalJarRoot.getPackageFragment("pack3");
        //$NON-NLS-1$
        fInternalPack4 = fInternalJarRoot.getPackageFragment("pack3.pack4");
        //$NON-NLS-1$
        fInternalPack5 = fInternalJarRoot.getPackageFragment("pack3.pack5");
        //$NON-NLS-1$
        fInternalJarRoot.getPackageFragment("pack3.pack5.pack6");
        //$NON-NLS-1$
        fInternalPack10 = fInternalJarRoot.getPackageFragment("pack3.pack4.pack10");
        //-----------------Set up source folder--------------------------
        //$NON-NLS-1$
        fRoot2 = JavaProjectHelper.addSourceContainer(fJProject2, "src2");
        //$NON-NLS-1$
        fPackDefault2 = fRoot2.createPackageFragment("", true, null);
        //$NON-NLS-1$
        fPack12 = fRoot2.createPackageFragment("pack1", true, null);
        //$NON-NLS-1$
        fPack17 = fRoot2.createPackageFragment("pack1.pack7", true, null);
        //$NON-NLS-1$
        fPack32 = fRoot2.createPackageFragment("pack3", true, null);
        //$NON-NLS-1$
        fPack42 = fRoot2.createPackageFragment("pack3.pack4", true, null);
        //$NON-NLS-1$
        fPack52 = fRoot2.createPackageFragment("pack3.pack5", true, null);
        //$NON-NLS-1$
        fPack62 = fRoot2.createPackageFragment("pack3.pack5.pack6", true, null);
        //$NON-NLS-1$//$NON-NLS-2$
        fPack12.createCompilationUnit("Object.java", "", true, null);
        //$NON-NLS-1$//$NON-NLS-2$
        fPack62.createCompilationUnit("Object.java", "", true, null);
        //set up project #2: file system structure with in a source folder
        //	JavaProjectHelper.addVariableEntry(fJProject2, new Path("JRE_LIB_TEST"), null, null);
        //----------------Set up source folder--------------------------
        //$NON-NLS-1$
        fRoot1 = JavaProjectHelper.addSourceContainer(fJProject2, "src1");
        //$NON-NLS-1$
        fPackDefault1 = fRoot1.createPackageFragment("", true, null);
        //$NON-NLS-1$
        fPack21 = fRoot1.createPackageFragment("pack2", true, null);
        //$NON-NLS-1$
        fPack31 = fRoot1.createPackageFragment("pack3", true, null);
        //$NON-NLS-1$
        fPack41 = fRoot1.createPackageFragment("pack3.pack4", true, null);
        //$NON-NLS-1$
        fPack91 = fRoot1.createPackageFragment("pack3.pack4.pack9", true, null);
        //$NON-NLS-1$
        fPack51 = fRoot1.createPackageFragment("pack3.pack5", true, null);
        //$NON-NLS-1$
        fPack61 = fRoot1.createPackageFragment("pack3.pack5.pack6", true, null);
        //$NON-NLS-1$
        fPack81 = fRoot1.createPackageFragment("pack3.pack8", true, null);
        //$NON-NLS-1$//$NON-NLS-2$
        fPack21.createCompilationUnit("Object.java", "", true, null);
        //$NON-NLS-1$//$NON-NLS-2$
        fPack61.createCompilationUnit("Object.java", "", true, null);
        //set up the mock view
        setUpMockView();
    }

    public void setUpMockView() throws Exception {
        fWorkbench = PlatformUI.getWorkbench();
        assertNotNull(fWorkbench);
        page = fWorkbench.getActiveWorkbenchWindow().getActivePage();
        assertNotNull(page);
        MockPluginView.setListState(false);
        //$NON-NLS-1$
        IViewPart myPart = page.showView("org.eclipse.jdt.ui.tests.browsing.MockPluginView");
        if (myPart instanceof MockPluginView) {
            fMyPart = (MockPluginView) myPart;
            fProvider = (ITreeContentProvider) fMyPart.getTreeViewer().getContentProvider();
            //create map and set listener
            fProvider.inputChanged(null, null, fJProject2);
        } else {
            //$NON-NLS-1$
            assertTrue("Unable to get view", false);
        }
        assertNotNull(fProvider);
    }

    /*
	 * @see TestCase#tearDown()
	 */
    @Override
    protected void tearDown() throws Exception {
        JavaProjectHelper.delete(fJProject1);
        JavaProjectHelper.delete(fJProject2);
        fProvider.inputChanged(null, null, null);
        page.hideView(fMyPart);
        if (fEnableAutoBuildAfterTesting)
            CoreUtility.setAutoBuilding(true);
        super.tearDown();
    }

    private boolean compareArrays(Object[] children, Object[] expectedChildren) {
        if (children.length != expectedChildren.length)
            return false;
        for (int i = 0; i < children.length; i++) {
            Object child = children[i];
            if (child instanceof IJavaElement) {
                IJavaElement el = (IJavaElement) child;
                if (!contains(el, expectedChildren))
                    return false;
            } else if (child instanceof IResource) {
                IResource res = (IResource) child;
                if (!contains(res, expectedChildren)) {
                    return false;
                }
            } else if (child instanceof LogicalPackage) {
                if (!contains((LogicalPackage) child, expectedChildren))
                    return false;
            }
        }
        return true;
    }

    private boolean contains(IResource res, Object[] expectedChildren) {
        for (int i = 0; i < expectedChildren.length; i++) {
            Object object = expectedChildren[i];
            if (object instanceof IResource) {
                IResource expres = (IResource) object;
                if (expres.equals(res))
                    return true;
            }
        }
        return false;
    }

    private boolean contains(IJavaElement fragment, Object[] expectedChildren) {
        for (int i = 0; i < expectedChildren.length; i++) {
            Object object = expectedChildren[i];
            if (object instanceof IJavaElement) {
                IJavaElement expfrag = (IJavaElement) object;
                if (expfrag.equals(fragment))
                    return true;
            }
        }
        return false;
    }

    private boolean contains(LogicalPackage lp, Object[] expectedChildren) {
        for (int i = 0; i < expectedChildren.length; i++) {
            Object object = expectedChildren[i];
            if (object instanceof LogicalPackage) {
                LogicalPackage explp = (LogicalPackage) object;
                if (explp.equals(lp))
                    return true;
            }
        }
        return false;
    }
}
