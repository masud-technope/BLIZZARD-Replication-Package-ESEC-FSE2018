/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.core;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.util.Util;

public class SetContainerOperation extends ChangeClasspathOperation {

    IPath containerPath;

    IJavaProject[] affectedProjects;

    IClasspathContainer[] respectiveContainers;

    /*
	 * Creates a new SetContainerOperation.
	 */
    public  SetContainerOperation(IPath containerPath, IJavaProject[] affectedProjects, IClasspathContainer[] respectiveContainers) {
        super(new IJavaElement[] { JavaModelManager.getJavaModelManager().getJavaModel() }, !ResourcesPlugin.getWorkspace().isTreeLocked());
        this.containerPath = containerPath;
        this.affectedProjects = affectedProjects;
        this.respectiveContainers = respectiveContainers;
    }

    protected void executeOperation() throws JavaModelException {
        checkCanceled();
        try {
            //$NON-NLS-1$
            beginTask("", 1);
            if (JavaModelManager.CP_RESOLVE_VERBOSE)
                verbose_set_container();
            if (JavaModelManager.CP_RESOLVE_VERBOSE_ADVANCED)
                verbose_set_container_invocation_trace();
            JavaModelManager manager = JavaModelManager.getJavaModelManager();
            if (manager.containerPutIfInitializingWithSameEntries(this.containerPath, this.affectedProjects, this.respectiveContainers))
                return;
            final int projectLength = this.affectedProjects.length;
            final IJavaProject[] modifiedProjects;
            System.arraycopy(this.affectedProjects, 0, modifiedProjects = new IJavaProject[projectLength], 0, projectLength);
            // filter out unmodified project containers
            int remaining = 0;
            for (int i = 0; i < projectLength; i++) {
                if (isCanceled())
                    return;
                JavaProject affectedProject = (JavaProject) this.affectedProjects[i];
                IClasspathContainer newContainer = this.respectiveContainers[i];
                // 30920 - prevent infinite loop
                if (newContainer == null)
                    newContainer = JavaModelManager.CONTAINER_INITIALIZATION_IN_PROGRESS;
                boolean found = false;
                if (JavaProject.hasJavaNature(affectedProject.getProject())) {
                    IClasspathEntry[] rawClasspath = affectedProject.getRawClasspath();
                    for (int j = 0, cpLength = rawClasspath.length; j < cpLength; j++) {
                        IClasspathEntry entry = rawClasspath[j];
                        if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER && entry.getPath().equals(this.containerPath)) {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    // filter out this project - does not reference the container path, or isnt't yet Java project
                    modifiedProjects[i] = null;
                    manager.containerPut(affectedProject, this.containerPath, newContainer);
                    continue;
                }
                IClasspathContainer oldContainer = manager.containerGet(affectedProject, this.containerPath);
                if (oldContainer == JavaModelManager.CONTAINER_INITIALIZATION_IN_PROGRESS) {
                    oldContainer = null;
                }
                if ((oldContainer != null && oldContainer.equals(this.respectiveContainers[i])) || (oldContainer == this.respectiveContainers[i])) /*handle case where old and new containers are null (see bug 149043*/
                {
                    // filter out this project - container did not change
                    modifiedProjects[i] = null;
                    continue;
                }
                remaining++;
                manager.containerPut(affectedProject, this.containerPath, newContainer);
            }
            if (remaining == 0)
                return;
            // trigger model refresh
            try {
                for (int i = 0; i < projectLength; i++) {
                    this.progressMonitor.setWorkRemaining(projectLength - i);
                    if (isCanceled())
                        return;
                    JavaProject affectedProject = (JavaProject) modifiedProjects[i];
                    // was filtered out
                    if (affectedProject == null)
                        continue;
                    if (JavaModelManager.CP_RESOLVE_VERBOSE_ADVANCED)
                        verbose_update_project(affectedProject);
                    // force resolved classpath to be recomputed
                    ClasspathChange classpathChange = affectedProject.getPerProjectInfo().resetResolvedClasspath();
                    // if needed, generate delta, update project ref, create markers, ...
                    classpathChanged(classpathChange, /*refresh external linked folder only once*/
                    i == 0);
                    if (this.canChangeResources) {
                        // touch project to force a build if needed
                        try {
                            affectedProject.getProject().touch(this.progressMonitor.split(1));
                        } catch (CoreException e) {
                            if (!ExternalJavaProject.EXTERNAL_PROJECT_NAME.equals(affectedProject.getElementName()))
                                throw e;
                        }
                    }
                }
            } catch (CoreException e) {
                if (JavaModelManager.CP_RESOLVE_VERBOSE || JavaModelManager.CP_RESOLVE_VERBOSE_FAILURE)
                    verbose_failure(e);
                if (e instanceof JavaModelException) {
                    throw (JavaModelException) e;
                } else {
                    throw new JavaModelException(e);
                }
            } finally {
                for (int i = 0; i < projectLength; i++) {
                    if (this.respectiveContainers[i] == null) {
                        // reset init in progress marker
                        manager.containerPut(this.affectedProjects[i], this.containerPath, null);
                    }
                }
            }
        } finally {
            done();
        }
    }

    private void verbose_failure(CoreException e) {
        Util.verbose(//$NON-NLS-1$
        "CPContainer SET  - FAILED DUE TO EXCEPTION\n" + "	container path: " + //$NON-NLS-1$
        this.containerPath, System.err);
        e.printStackTrace();
    }

    private void verbose_update_project(JavaProject affectedProject) {
        Util.verbose(//$NON-NLS-1$
        "CPContainer SET  - updating affected project due to setting container\n" + "	project: " + affectedProject.getElementName() + //$NON-NLS-1$
        '\n' + "	container path: " + //$NON-NLS-1$
        this.containerPath);
    }

    private void verbose_set_container() {
        Util.verbose(//$NON-NLS-1$
        "CPContainer SET  - setting container\n" + "	container path: " + this.containerPath + //$NON-NLS-1$
        '\n' + //$NON-NLS-1$
        "	projects: {" + org.eclipse.jdt.internal.compiler.util.Util.toString(this.affectedProjects, new org.eclipse.jdt.internal.compiler.util.Util.Displayable() {

            public String displayString(Object o) {
                return ((IJavaProject) o).getElementName();
            }
        }) + //$NON-NLS-1$
        "}\n	values: {\n" + org.eclipse.jdt.internal.compiler.util.Util.toString(this.respectiveContainers, new org.eclipse.jdt.internal.compiler.util.Util.Displayable() {

            public String displayString(Object o) {
                StringBuffer buffer = new //$NON-NLS-1$
                StringBuffer(//$NON-NLS-1$
                "		");
                if (o == null) {
                    //$NON-NLS-1$
                    buffer.append("<null>");
                    return buffer.toString();
                }
                IClasspathContainer container = (IClasspathContainer) o;
                buffer.append(container.getDescription());
                //$NON-NLS-1$
                buffer.append(" {\n");
                IClasspathEntry[] entries = container.getClasspathEntries();
                if (entries != null) {
                    for (int i = 0; i < entries.length; i++) {
                        //$NON-NLS-1$
                        buffer.append(//$NON-NLS-1$
                        " 			");
                        buffer.append(entries[i]);
                        buffer.append('\n');
                    }
                }
                //$NON-NLS-1$
                buffer.append(//$NON-NLS-1$
                " 		}");
                return buffer.toString();
            }
        }) + //$NON-NLS-1$
        "\n	}");
    }

    private void verbose_set_container_invocation_trace() {
        Util.verbose(//$NON-NLS-1$
        "CPContainer SET  - setting container\n" + //$NON-NLS-1$
        "	invocation stack trace:");
        //$NON-NLS-1$
        new Exception("<Fake exception>").printStackTrace(System.out);
    }
}
