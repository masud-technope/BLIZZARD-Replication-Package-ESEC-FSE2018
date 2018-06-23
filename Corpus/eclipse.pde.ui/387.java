/*******************************************************************************
 *  Copyright (c) 2005, 2015 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.ui.tests.wizards;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.pde.internal.core.natures.PDE;
import org.eclipse.pde.internal.core.site.WorkspaceSiteModel;
import org.eclipse.pde.internal.ui.wizards.site.NewSiteProjectCreationOperation;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

public class NewSiteProjectTestCase extends TestCase {

    //$NON-NLS-1$
    private static final String EXISTING_PROJECT_NAME = "ExistingSiteProject";

    public static Test suite() {
        return new TestSuite(NewSiteProjectTestCase.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if (//$NON-NLS-1$
        "testExistingSiteProject".equalsIgnoreCase(getName())) {
            IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(EXISTING_PROJECT_NAME);
            project.create(new NullProgressMonitor());
            project.open(new NullProgressMonitor());
            //$NON-NLS-1$
            IFile file = project.getFile(new Path("site.xml"));
            String content = //$NON-NLS-1$
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + //$NON-NLS-1$
            "<site>" + "<category-def name=\"new_category_1\" label=\"New Category 1\"/>" + //$NON-NLS-1$
            "</site>";
            //$NON-NLS-1$
            ByteArrayInputStream source = new ByteArrayInputStream(content.getBytes("ASCII"));
            if (file.exists())
                file.setContents(source, true, false, new NullProgressMonitor());
            else
                file.create(source, true, new NullProgressMonitor());
            project.delete(false, true, new NullProgressMonitor());
        }
    }

    @Override
    protected void tearDown() throws Exception {
        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        IProject[] projects = workspaceRoot.getProjects();
        try {
            for (IProject project : projects) {
                project.delete(true, new NullProgressMonitor());
            }
        } catch (CoreException e) {
        }
        super.tearDown();
    }

    private void createSite(IProject project, IPath path, String webLocation) throws InvocationTargetException, InterruptedException {
        NewSiteProjectCreationOperation createOperation = new NewSiteProjectCreationOperation(Display.getDefault(), project, path, webLocation);
        IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
        progressService.runInUI(progressService, createOperation, null);
    }

    /**
	 * @param project
	 */
    private void ensureCreated(IProject project) {
        //$NON-NLS-1$
        assertTrue("Project not created.", project.exists());
        //$NON-NLS-1$
        assertTrue("Project not open.", project.isOpen());
        try {
            assertTrue(//$NON-NLS-1$
            "Site nature not added.", project.hasNature(PDE.SITE_NATURE));
        } catch (Exception e) {
        }
        assertTrue("site.xml not created.", //$NON-NLS-1$
        project.exists(//$NON-NLS-1$
        new Path("site.xml")));
        //$NON-NLS-1$
        WorkspaceSiteModel model = new WorkspaceSiteModel(project.getFile(new Path("site.xml")));
        model.load();
        //$NON-NLS-1$
        assertTrue("Model cannot be loaded.", model.isLoaded());
        //$NON-NLS-1$
        assertTrue("Model is not valid.", model.isValid());
        //$NON-NLS-1$
        assertFalse("ISite is null.", model.getSite() == null);
        model.dispose();
    }

    public void testExistingSiteProject() {
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(EXISTING_PROJECT_NAME);
        IPath path = Platform.getLocation();
        try {
            createSite(project, path, null);
        } catch (Exception e) {
            e.printStackTrace();
            fail("testExistingSiteProject: " + e);
        }
        ensureCreated(project);
        //$NON-NLS-1$
        WorkspaceSiteModel model = new WorkspaceSiteModel(project.getFile(new Path("site.xml")));
        model.load();
        assertTrue("Existig site overwritten.", //$NON-NLS-1$
        model.getSite().getCategoryDefinitions().length > 0);
        model.dispose();
    }

    public void testSiteProject() {
        //$NON-NLS-1$
        String projectName = "SiteProject";
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        IPath path = Platform.getLocation();
        try {
            createSite(project, path, null);
        } catch (Exception e) {
            e.printStackTrace();
            fail("testSiteProject: " + e);
        }
        ensureCreated(project);
        assertFalse("index.html should have not been generated.", //$NON-NLS-1$
        project.exists(//$NON-NLS-1$
        new Path("index.html")));
    }

    public void testSiteProjectWithWeb() {
        //$NON-NLS-1$
        String projectName = "SiteProjectWithWeb";
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        IPath path = Platform.getLocation();
        try {
            //$NON-NLS-1$
            createSite(project, path, "testWeb");
        } catch (Exception e) {
            e.printStackTrace();
            fail("testSiteProjectWithWeb: " + e);
        }
        ensureCreated(project);
        assertTrue("index.html not generated.", project.exists(new //$NON-NLS-1$
        Path(//$NON-NLS-1$
        "index.html")));
        //$NON-NLS-1$
        IFolder webFolder = project.getFolder(new Path("testWeb"));
        //$NON-NLS-1$
        assertTrue("Web folder not generated.", webFolder.exists());
        assertTrue("site.xsl not generated.", webFolder.exists(new //$NON-NLS-1$
        Path(//$NON-NLS-1$
        "site.xsl")));
        assertTrue("site.css not generated.", webFolder.exists(new //$NON-NLS-1$
        Path(//$NON-NLS-1$
        "site.css")));
    }
}
