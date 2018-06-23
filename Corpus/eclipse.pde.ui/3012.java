/*******************************************************************************
 * Copyright (c) 2012, 2016 Ecliptical Software Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ecliptical Software Inc. - initial API and implementation
 *     Dirk Fauth <dirk.fauth@googlemail.com> - Bug 490062
 *******************************************************************************/
package org.eclipse.pde.ds.internal.annotations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.BuildContext;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.pde.core.IBaseModel;
import org.eclipse.pde.core.build.IBuildEntry;
import org.eclipse.pde.core.build.IBuildModel;
import org.eclipse.pde.core.build.IBuildModelFactory;
import org.eclipse.pde.internal.core.WorkspaceModelManager;
import org.eclipse.pde.internal.core.ibundle.IBundleModel;
import org.eclipse.pde.internal.core.ibundle.IBundlePluginModelBase;
import org.eclipse.pde.internal.core.natures.PDE;
import org.eclipse.pde.internal.core.project.PDEProject;
import org.eclipse.pde.internal.ui.util.ModelModification;
import org.eclipse.pde.internal.ui.util.PDEModelUtility;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.component.annotations.Component;

@SuppressWarnings("restriction")
public class DSAnnotationCompilationParticipant extends CompilationParticipant {

    //$NON-NLS-1$
    private static final String DS_MANIFEST_KEY = "Service-Component";

    //$NON-NLS-1$
    private static final String AP_MANIFEST_KEY = "Bundle-ActivationPolicy";

    static final String COMPONENT_ANNOTATION = Component.class.getName();

    //$NON-NLS-1$
    private static final QualifiedName PROP_STATE = new QualifiedName(Activator.PLUGIN_ID, "state");

    //$NON-NLS-1$
    private static final String STATE_FILENAME = "state.dat";

    //$NON-NLS-1$
    private static final Debug debug = Debug.getDebug("ds-annotation-builder");

    private final Map<IJavaProject, ProjectContext> processingContext = Collections.synchronizedMap(new HashMap<IJavaProject, ProjectContext>());

    @Override
    public boolean isAnnotationProcessor() {
        return true;
    }

    @Override
    public boolean isActive(IJavaProject project) {
        boolean enabled = Platform.getPreferencesService().getBoolean(Activator.PLUGIN_ID, Activator.PREF_ENABLED, false, new IScopeContext[] { new ProjectScope(project.getProject()), InstanceScope.INSTANCE, DefaultScope.INSTANCE });
        if (!enabled)
            return false;
        IProject iproject = project.getProject();
        if (!iproject.isOpen() || !PDE.hasPluginNature(iproject))
            return false;
        if (WorkspaceModelManager.isBinaryProject(project.getProject()))
            return false;
        try {
            IType annotationType = project.findType(COMPONENT_ANNOTATION);
            return annotationType != null && annotationType.isAnnotation();
        } catch (JavaModelException e) {
            Activator.log(e);
        }
        return false;
    }

    @Override
    public int aboutToBuild(IJavaProject project) {
        if (debug.isDebugging())
            //$NON-NLS-1$
            debug.trace(String.format("About to build project: %s", project.getElementName()));
        int result = READY_FOR_BUILD;
        int[] retval = new int[1];
        ProjectState state = getState(project, retval);
        result = retval[0];
        processingContext.put(project, new ProjectContext(state));
        if (state.getFormatVersion() != ProjectState.FORMAT_VERSION) {
            state.setFormatVersion(ProjectState.FORMAT_VERSION);
            result = NEEDS_FULL_BUILD;
        }
        String path = Platform.getPreferencesService().getString(Activator.PLUGIN_ID, Activator.PREF_PATH, Activator.DEFAULT_PATH, new IScopeContext[] { new ProjectScope(project.getProject()), InstanceScope.INSTANCE });
        if (!path.equals(state.getPath())) {
            state.setPath(path);
            result = NEEDS_FULL_BUILD;
        }
        String errorLevelStr = Platform.getPreferencesService().getString(Activator.PLUGIN_ID, Activator.PREF_VALIDATION_ERROR_LEVEL, ValidationErrorLevel.error.toString(), new IScopeContext[] { new ProjectScope(project.getProject()), InstanceScope.INSTANCE });
        ValidationErrorLevel errorLevel = getEnumValue(errorLevelStr, ValidationErrorLevel.class, ValidationErrorLevel.error);
        if (errorLevel != state.getErrorLevel()) {
            state.setErrorLevel(errorLevel);
            result = NEEDS_FULL_BUILD;
        }
        String missingUnbindMethodLevelStr = Platform.getPreferencesService().getString(Activator.PLUGIN_ID, Activator.PREF_MISSING_UNBIND_METHOD_ERROR_LEVEL, errorLevelStr, new IScopeContext[] { new ProjectScope(project.getProject()), InstanceScope.INSTANCE });
        ValidationErrorLevel missingUnbindMethodLevel = getEnumValue(missingUnbindMethodLevelStr, ValidationErrorLevel.class, errorLevel);
        if (missingUnbindMethodLevel != state.getMissingUnbindMethodLevel()) {
            state.setMissingUnbindMethodLevel(missingUnbindMethodLevel);
            result = NEEDS_FULL_BUILD;
        }
        return result;
    }

    private <E extends Enum<E>> E getEnumValue(String property, Class<E> enumType, E defaultValue) {
        try {
            return Enum.valueOf(enumType, property);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public static ProjectState getState(IJavaProject project) {
        return getState(project, null);
    }

    private static ProjectState getState(IJavaProject project, int[] result) {
        ProjectState state = null;
        try {
            Object value = project.getProject().getSessionProperty(PROP_STATE);
            if (value instanceof SoftReference<?>) {
                @SuppressWarnings("unchecked") SoftReference<ProjectState> ref = (SoftReference<ProjectState>) value;
                state = ref.get();
            }
        } catch (CoreException e) {
            Activator.log(e);
        }
        if (state == null) {
            try {
                state = loadState(project.getProject());
            } catch (IOException e) {
                Activator.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error loading project state.", e));
            }
            if (state == null) {
                state = new ProjectState();
                if (result != null && result.length > 0)
                    result[0] = NEEDS_FULL_BUILD;
            }
            try {
                project.getProject().setSessionProperty(PROP_STATE, new SoftReference(state));
            } catch (CoreException e) {
                Activator.log(e);
            }
        }
        return state;
    }

    private static ProjectState loadState(IProject project) throws IOException {
        File stateFile = getStateFile(project);
        if (!stateFile.canRead()) {
            if (debug.isDebugging())
                //$NON-NLS-1$
                debug.trace(//$NON-NLS-1$
                String.format("Missing or invalid project state file: %s", stateFile));
            return null;
        }
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(stateFile));
        try {
            ProjectState state = (ProjectState) in.readObject();
            if (debug.isDebugging()) {
                //$NON-NLS-1$
                debug.trace(//$NON-NLS-1$
                String.format("Loaded state for project: %s", project.getName()));
                for (String cuKey : state.getCompilationUnits()) debug.trace(String.format("%s -> %s", cuKey, //$NON-NLS-1$
                state.getModelFiles(//$NON-NLS-1$
                cuKey)));
            }
            return state;
        } catch (ClassNotFoundException e) {
            IOException ex = new IOException("Unable to deserialize project state.");
            ex.initCause(e);
            throw ex;
        } finally {
            in.close();
        }
    }

    @Override
    public void buildFinished(IJavaProject project) {
        ProjectContext projectContext = processingContext.remove(project);
        if (projectContext != null) {
            ProjectState state = projectContext.getState();
            // check if unprocessed CUs still exist; if not, their mapped files are now abandoned
            HashSet<String> abandoned = new HashSet(projectContext.getAbandoned());
            for (String cuKey : projectContext.getUnprocessed()) {
                boolean exists = false;
                try {
                    IJavaElement cu = project.findElement(new Path(cuKey));
                    IResource file;
                    if (cu != null && cu.getElementType() == IJavaElement.COMPILATION_UNIT && (file = cu.getResource()) != null && file.exists())
                        exists = true;
                } catch (JavaModelException e) {
                    Activator.log(e);
                }
                if (!exists) {
                    if (debug.isDebugging())
                        debug.trace(//$NON-NLS-1$
                        String.format(//$NON-NLS-1$
                        "Mapped CU %s no longer exists.", //$NON-NLS-1$
                        cuKey));
                    Collection<String> dsKeys = state.removeMappings(cuKey);
                    if (dsKeys != null)
                        abandoned.addAll(dsKeys);
                }
            }
            // retain abandoned files that are still mapped elsewhere
            HashSet<String> retained = new HashSet();
            for (String cuKey : state.getCompilationUnits()) {
                Collection<String> dsKeys = state.getModelFiles(cuKey);
                if (dsKeys != null)
                    retained.addAll(dsKeys);
            }
            abandoned.removeAll(retained);
            if (projectContext.isChanged()) {
                try {
                    saveState(project.getProject(), state);
                } catch (IOException e) {
                    Activator.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error saving file mappings.", e));
                }
            }
            // delete all abandoned files
            ArrayList<IStatus> deleteStatuses = new ArrayList(2);
            for (String dsKey : abandoned) {
                IPath path = Path.fromPortableString(dsKey);
                if (debug.isDebugging())
                    debug.trace(//$NON-NLS-1$
                    String.format(//$NON-NLS-1$
                    "Deleting %s", //$NON-NLS-1$
                    path));
                IFile file = PDEProject.getBundleRelativeFile(project.getProject(), path);
                if (file.exists()) {
                    try {
                        file.delete(true, null);
                    } catch (CoreException e) {
                        deleteStatuses.add(e.getStatus());
                    }
                }
            }
            if (!deleteStatuses.isEmpty())
                //$NON-NLS-1$
                Activator.log(//$NON-NLS-1$
                new MultiStatus(Activator.PLUGIN_ID, 0, deleteStatuses.toArray(new IStatus[deleteStatuses.size()]), "Error deleting generated files.", null));
            if (!retained.isEmpty() || !abandoned.isEmpty())
                updateProject(project.getProject(), retained, abandoned);
        }
        if (debug.isDebugging())
            //$NON-NLS-1$
            debug.trace(String.format("Build finished for project: %s", project.getElementName()));
    }

    private void saveState(IProject project, ProjectState state) throws IOException {
        File stateFile = getStateFile(project);
        if (debug.isDebugging()) {
            //$NON-NLS-1$
            debug.trace(String.format("Saving state for project: %s", project.getName()));
            for (String cuKey : state.getCompilationUnits()) //$NON-NLS-1$
            debug.trace(//$NON-NLS-1$
            String.format("%s -> %s", cuKey, state.getModelFiles(cuKey)));
        }
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(stateFile));
        try {
            out.writeObject(state);
        } finally {
            out.close();
        }
    }

    private void updateProject(IProject project, final Collection<String> retained, final Collection<String> abandoned) {
        PDEModelUtility.modifyModel(new ModelModification(project) {

            @Override
            protected void modifyModel(IBaseModel model, IProgressMonitor monitor) throws CoreException {
                if (model instanceof IBundlePluginModelBase)
                    updateManifest((IBundlePluginModelBase) model, retained, abandoned, project);
            }
        }, null);
        // note: we can't combine both manifest and build.properties into a single edit
        PDEModelUtility.modifyModel(new ModelModification(PDEProject.getBuildProperties(project)) {

            @Override
            protected void modifyModel(IBaseModel model, IProgressMonitor monitor) throws CoreException {
                if (model instanceof IBuildModel)
                    updateBuildProperties((IBuildModel) model, retained, abandoned);
            }
        }, null);
    }

    private void updateManifest(IBundlePluginModelBase model, Collection<String> retained, Collection<String> abandoned, IProject project) {
        IBundleModel bundleModel = model.getBundleModel();
        LinkedHashSet<IPath> entries = new LinkedHashSet();
        collectManifestEntries(bundleModel, entries);
        boolean changed = false;
        for (String dsKey : abandoned) {
            IPath path = Path.fromPortableString(dsKey);
            changed |= entries.remove(path);
        }
        for (String dsKey : retained) {
            IPath path = Path.fromPortableString(dsKey);
            if (!isManifestEntryIncluded(entries, path))
                changed |= entries.add(path);
        }
        if (!changed)
            return;
        StringBuilder buf = new StringBuilder();
        for (IPath entry : entries) {
            if (buf.length() > 0)
                //$NON-NLS-1$
                buf.append(//$NON-NLS-1$
                ",\n ");
            buf.append(entry.toString());
        }
        String value = buf.toString();
        if (debug.isDebugging())
            //$NON-NLS-1$
            debug.trace(String.format("Setting manifest header in %s to %s: %s", model.getUnderlyingResource().getFullPath(), DS_MANIFEST_KEY, value));
        // note: contrary to javadoc, setting header value to null does *not* remove it; setting it to empty string does
        bundleModel.getBundle().setHeader(DS_MANIFEST_KEY, value);
        boolean generateBAPL = Platform.getPreferencesService().getBoolean(Activator.PLUGIN_ID, Activator.PREF_GENERATE_BAPL, true, new IScopeContext[] { new ProjectScope(project.getProject()), InstanceScope.INSTANCE });
        if (generateBAPL) {
            if (debug.isDebugging())
                debug.trace(String.format("Setting manifest header in %s to %s: %s", //$NON-NLS-1$
                model.getUnderlyingResource().getFullPath(), //$NON-NLS-1$
                AP_MANIFEST_KEY, //$NON-NLS-1$
                "lazy"));
            //$NON-NLS-1$
            bundleModel.getBundle().setHeader(AP_MANIFEST_KEY, "lazy");
        }
    }

    private void collectManifestEntries(IBundleModel bundleModel, Collection<IPath> entries) {
        String header = bundleModel.getBundle().getHeader(DS_MANIFEST_KEY);
        if (header == null)
            return;
        //$NON-NLS-1$
        String[] elements = header.split("\\s*,\\s*");
        for (String element : elements) {
            if (element.length() != 0)
                entries.add(new Path(element));
        }
    }

    private boolean isManifestEntryIncluded(Collection<IPath> entries, IPath path) {
        for (IPath entry : entries) {
            if (entry.equals(path))
                return true;
            if (entry.removeLastSegments(1).equals(path.removeLastSegments(1))) {
                // check if wildcard match (last path segment)
                Filter filter;
                try {
                    //$NON-NLS-1$ //$NON-NLS-2$
                    filter = FrameworkUtil.createFilter("(filename=" + sanitizeFilterValue(entry.lastSegment()) + ")");
                } catch (InvalidSyntaxException e) {
                    continue;
                }
                if (//$NON-NLS-1$
                filter.matches(//$NON-NLS-1$
                Collections.singletonMap("filename", path.lastSegment())))
                    return true;
            }
        }
        return false;
    }

    private String sanitizeFilterValue(String value) {
        return value.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    private void updateBuildProperties(IBuildModel model, Collection<String> retained, Collection<String> abandoned) throws CoreException {
        IBuildEntry includes = model.getBuild().getEntry(IBuildEntry.BIN_INCLUDES);
        if (includes != null) {
            for (String dsKey : abandoned) {
                String path = Path.fromPortableString(dsKey).toString();
                if (includes.contains(path))
                    includes.removeToken(path);
            }
        }
        if (!retained.isEmpty()) {
            if (includes == null) {
                IBuildModelFactory factory = model.getFactory();
                includes = factory.createEntry(IBuildEntry.BIN_INCLUDES);
                model.getBuild().add(includes);
            }
            LinkedHashSet<IPath> entries = new LinkedHashSet();
            collectBuildEntries(includes, entries);
            for (String dsKey : retained) {
                IPath path = Path.fromPortableString(dsKey);
                if (!isBuildEntryIncluded(entries, path))
                    includes.addToken(path.toString());
            }
        }
    }

    private void collectBuildEntries(IBuildEntry includes, Collection<IPath> entries) {
        if (includes == null)
            return;
        for (String include : includes.getTokens()) {
            if ((include = include.trim()).length() != 0)
                entries.add(new Path(include));
        }
    }

    private boolean isBuildEntryIncluded(Collection<IPath> entries, IPath path) {
        for (IPath entry : entries) {
            if (entry.equals(path))
                return true;
            if (entry.hasTrailingSeparator() && entry.isPrefixOf(path))
                return true;
        }
        return false;
    }

    @Override
    public void processAnnotations(BuildContext[] files) {
        HashMap<IJavaProject, Map<ICompilationUnit, BuildContext>> filesByProject = new HashMap();
        for (BuildContext file : files) {
            if (debug.isDebugging())
                debug.trace(String.format("Creating compilation unit from file %s.", file.getFile().getFullPath()));
            ICompilationUnit cu = JavaCore.createCompilationUnitFrom(file.getFile());
            if (cu == null) {
                if (debug.isDebugging())
                    debug.trace(String.format("Unable to create compilation unit from file %s.", file.getFile().getFullPath()));
                continue;
            }
            Map<ICompilationUnit, BuildContext> map = filesByProject.get(cu.getJavaProject());
            if (map == null) {
                map = new HashMap();
                filesByProject.put(cu.getJavaProject(), map);
            }
            map.put(cu, file);
        }
        for (Map.Entry<IJavaProject, Map<ICompilationUnit, BuildContext>> entry : filesByProject.entrySet()) {
            if (debug.isDebugging())
                debug.trace(String.format("Processing compilation units in project %s.", entry.getKey().getElementName()));
            processAnnotations(entry.getKey(), entry.getValue());
        }
    }

    private void processAnnotations(IJavaProject javaProject, Map<ICompilationUnit, BuildContext> fileMap) {
        @SuppressWarnings("deprecation") ASTParser parser = ASTParser.newParser(AST.JLS4);
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
        parser.setProject(javaProject);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        ProjectContext projectContext = processingContext.get(javaProject);
        ProjectState state = projectContext.getState();
        parser.setIgnoreMethodBodies(state.getErrorLevel() == ValidationErrorLevel.ignore);
        ICompilationUnit[] cuArr = fileMap.keySet().toArray(new ICompilationUnit[fileMap.size()]);
        parser.createASTs(cuArr, new String[0], new AnnotationProcessor(projectContext, fileMap), null);
    }

    public static boolean isManaged(IProject project) {
        try {
            if (project.getSessionProperty(PROP_STATE) != null)
                return true;
            File stateFile = getStateFile(project);
            return stateFile.canRead();
        } catch (CoreException e) {
            return false;
        }
    }

    private static File getStateFile(IProject project) {
        File workDir = project.getWorkingLocation(Activator.PLUGIN_ID).toFile();
        File stateFile = new File(workDir, STATE_FILENAME);
        return stateFile;
    }
}
