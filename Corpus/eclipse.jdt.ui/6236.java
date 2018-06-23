/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.ui.tests.packageview;

import java.io.ByteArrayInputStream;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.jdt.testplugin.JavaProjectHelper;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JavaElementDelta;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.packageview.LibraryContainer;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerContentProvider;
import org.eclipse.jdt.internal.ui.util.CoreUtility;

/**
 * Tests for the PackageExplorerContentProvider.
 * <ul>
 * <li>Bug 357450: Class folder in Java project have refresh problem</li>
 * </ul>
 * 
 * @since 3.9
 */
public class ContentProviderTests6 extends TestCase {

    private boolean fEnableAutoBuildAfterTesting;

    private IWorkbenchPage page;

    private MockPluginView fMyPart;

    private ITreeContentProvider fProvider;

    private IJavaProject fJProject;

    private IPackageFragmentRoot classFolder;

    public  ContentProviderTests6(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ContentProviderTests6.class.getName());
        //$JUnit-BEGIN$
        suite.addTestSuite(ContentProviderTests6.class);
        //$JUnit-END$
        return suite;
    }

    public void testAddFileToClassFolder() throws Exception {
        //$NON-NLS-1$
        IFile file = ((IFolder) classFolder.getResource()).getFile("testFile.class");
        if (!file.exists()) {
            file.create(new ByteArrayInputStream(new byte[] {}), false, null);
        }
        //send a delta indicating file added
        JavaElementDelta delta = new JavaElementDelta(classFolder.getJavaModel());
        delta.added(JavaCore.create(file));
        IElementChangedListener listener = (IElementChangedListener) fProvider;
        listener.elementChanged(new ElementChangedEvent(delta, ElementChangedEvent.POST_CHANGE));
        //force events from display
        while (fMyPart.getTreeViewer().getControl().getDisplay().readAndDispatch()) {
        }
        //$NON-NLS-1$
        assertTrue("No add happened", !fMyPart.hasAddHappened());
        assertions();
    }

    public void testAddFolderToClassFolder() throws Exception {
        //$NON-NLS-1$
        IFolder folder = ((IFolder) classFolder.getResource()).getFolder("testFolder");
        if (!folder.exists()) {
            folder.create(false, true, null);
        }
        //send a delta indicating folder added
        JavaElementDelta delta = new JavaElementDelta(classFolder.getJavaModel());
        delta.added(JavaCore.create(folder));
        IElementChangedListener listener = (IElementChangedListener) fProvider;
        listener.elementChanged(new ElementChangedEvent(delta, ElementChangedEvent.POST_CHANGE));
        //force events from display
        while (fMyPart.getTreeViewer().getControl().getDisplay().readAndDispatch()) {
        }
        //$NON-NLS-1$
        assertTrue("No add happened", !fMyPart.hasAddHappened());
        assertions();
    }

    public void testRemoveFileFromClassFolder() throws Exception {
        //$NON-NLS-1$
        IFile file = ((IFolder) classFolder.getResource()).getFile("testFile.class");
        if (!file.exists()) {
            file.create(new ByteArrayInputStream(new byte[] {}), false, null);
        }
        file.delete(false, null);
        //send a delta indicating file removed
        JavaElementDelta delta = new JavaElementDelta(classFolder.getJavaModel());
        delta.removed(JavaCore.create(file));
        IElementChangedListener listener = (IElementChangedListener) fProvider;
        listener.elementChanged(new ElementChangedEvent(delta, ElementChangedEvent.POST_CHANGE));
        //force events from display
        while (fMyPart.getTreeViewer().getControl().getDisplay().readAndDispatch()) {
        }
        //$NON-NLS-1$
        assertTrue("No remove happened", !fMyPart.hasRemoveHappened());
        assertions();
    }

    public void testRemoveFolderFromClassFolder() throws Exception {
        //$NON-NLS-1$
        IFolder folder = ((IFolder) classFolder.getResource()).getFolder("testFolder");
        if (!folder.exists()) {
            folder.create(false, true, null);
        }
        folder.delete(false, null);
        //send a delta indicating folder deleted
        JavaElementDelta delta = new JavaElementDelta(classFolder.getJavaModel());
        delta.removed(JavaCore.create(folder));
        IElementChangedListener listener = (IElementChangedListener) fProvider;
        listener.elementChanged(new ElementChangedEvent(delta, ElementChangedEvent.POST_CHANGE));
        //force events from display
        while (fMyPart.getTreeViewer().getControl().getDisplay().readAndDispatch()) {
        }
        //$NON-NLS-1$
        assertTrue("No remove happened", !fMyPart.hasRemoveHappened());
        assertions();
    }

    public void testChangeClassInProject() throws Exception {
        IPackageFragmentRoot sourceFolder = JavaProjectHelper.addSourceContainer(fJProject, "src");
        IPackageFragment pack1 = sourceFolder.createPackageFragment("pack1", false, null);
        ICompilationUnit cu = pack1.createCompilationUnit("A.java", "hello", false, null);
        //send a delta indicating file changed
        JavaElementDelta delta = new JavaElementDelta(sourceFolder.getJavaModel());
        delta.changed(cu, IJavaElementDelta.F_CHILDREN);
        IElementChangedListener listener = (IElementChangedListener) fProvider;
        listener.elementChanged(new ElementChangedEvent(delta, ElementChangedEvent.POST_CHANGE));
        //force events from display
        while (fMyPart.getTreeViewer().getControl().getDisplay().readAndDispatch()) {
        }
        //$NON-NLS-1$
        assertTrue("Refresh happened", fMyPart.hasRefreshHappened());
        //$NON-NLS-1$
        assertTrue("Project not refreshed", !fMyPart.wasObjectRefreshed(fJProject));
    }

    private void assertions() {
        //$NON-NLS-1$
        assertTrue("Refresh happened", fMyPart.hasRefreshHappened());
        //$NON-NLS-1$
        assertTrue("LibraryContainer Refreshed", fMyPart.wasObjectRefreshed(new LibraryContainer(fJProject)));
        //$NON-NLS-1$
        assertTrue("Resource Refreshed", fMyPart.wasObjectRefreshed(classFolder.getResource()));
        //$NON-NLS-1$
        assertTrue("Number of refreshed objects", fMyPart.getRefreshedObject().size() == 2);
    }

    /*
	 * @see TestCase#setUp()
	 */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        IWorkspaceDescription workspaceDesc = ResourcesPlugin.getWorkspace().getDescription();
        fEnableAutoBuildAfterTesting = workspaceDesc.isAutoBuilding();
        if (fEnableAutoBuildAfterTesting)
            CoreUtility.setAutoBuilding(false);
        //$NON-NLS-1$//$NON-NLS-2$
        fJProject = JavaProjectHelper.createJavaProject("TestProject", "bin");
        //$NON-NLS-1$
        assertNotNull("project null", fJProject);
        //set up project : Add classFolder
        //$NON-NLS-1$
        classFolder = JavaProjectHelper.addClassFolder(fJProject, "classFolder", null, null);
        //$NON-NLS-1$
        assertNotNull("class folder null", classFolder);
        //set up the mock view
        setUpMockView();
    }

    public void setUpMockView() throws Exception {
        page = JavaPlugin.getActivePage();
        assertNotNull(page);
        //$NON-NLS-1$
        IViewPart myPart = page.showView("org.eclipse.jdt.ui.tests.packageview.MockPluginView");
        if (myPart instanceof MockPluginView) {
            fMyPart = (MockPluginView) myPart;
            fMyPart.clear();
            fProvider = (ITreeContentProvider) fMyPart.getTreeViewer().getContentProvider();
            ((PackageExplorerContentProvider) fProvider).setShowLibrariesNode(true);
        } else {
            //$NON-NLS-1$
            assertTrue("Unable to get view", false);
        }
        assertNotNull(fProvider);
    }

    /**
	 * @see TestCase#tearDown()
	 */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        JavaProjectHelper.delete(fJProject);
        if (fEnableAutoBuildAfterTesting)
            CoreUtility.setAutoBuilding(true);
    }
}
