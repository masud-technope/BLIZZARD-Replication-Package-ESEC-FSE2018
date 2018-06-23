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
package org.eclipse.jdt.internal.core.builder;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.compiler.*;
import org.eclipse.jdt.internal.compiler.*;
import org.eclipse.jdt.internal.compiler.classfmt.*;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.problem.*;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;
import org.eclipse.jdt.internal.compiler.util.SuffixConstants;
import org.eclipse.jdt.internal.core.util.Messages;
import org.eclipse.jdt.internal.core.util.Util;
import java.io.*;
import java.net.URI;
import java.util.*;

/**
 * The incremental image builder
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class IncrementalImageBuilder extends AbstractImageBuilder {

    protected ArrayList sourceFiles;

    protected ArrayList previousSourceFiles;

    protected StringSet qualifiedStrings;

    protected StringSet simpleStrings;

    protected StringSet rootStrings;

    protected SimpleLookupTable secondaryTypesToRemove;

    protected boolean hasStructuralChanges;

    protected int compileLoop;

    protected boolean makeOutputFolderConsistent;

    // perform a full build if it takes more than ? incremental compile loops
    public static int MaxCompileLoop = 5;

    protected  IncrementalImageBuilder(JavaBuilder javaBuilder, State buildState) {
        super(javaBuilder, true, buildState);
        this.nameEnvironment.isIncrementalBuild = true;
        this.makeOutputFolderConsistent = JavaCore.ENABLED.equals(javaBuilder.javaProject.getOption(JavaCore.CORE_JAVA_BUILD_RECREATE_MODIFIED_CLASS_FILES_IN_OUTPUT_FOLDER, true));
    }

    protected  IncrementalImageBuilder(JavaBuilder javaBuilder) {
        this(javaBuilder, null);
        this.newState.copyFrom(javaBuilder.lastState);
    }

    protected  IncrementalImageBuilder(BatchImageBuilder batchBuilder) {
        this(batchBuilder.javaBuilder, batchBuilder.newState);
        resetCollections();
    }

    public boolean build(SimpleLookupTable deltas) {
        if (JavaBuilder.DEBUG)
            //$NON-NLS-1$
            System.out.println("INCREMENTAL build");
        try {
            resetCollections();
            this.notifier.subTask(Messages.build_analyzingDeltas);
            if (this.javaBuilder.hasBuildpathErrors()) {
                // we need to avoid unnecessary deltas caused by doing a full build in this case
                if (JavaBuilder.DEBUG)
                    //$NON-NLS-1$
                    System.out.println(//$NON-NLS-1$
                    "COMPILING all source files since the buildpath has errors ");
                this.javaBuilder.currentProject.deleteMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, false, IResource.DEPTH_ZERO);
                addAllSourceFiles(this.sourceFiles);
                this.notifier.updateProgressDelta(0.25f);
            } else {
                IResourceDelta sourceDelta = (IResourceDelta) deltas.get(this.javaBuilder.currentProject);
                if (sourceDelta != null)
                    if (!findSourceFiles(sourceDelta))
                        return false;
                this.notifier.updateProgressDelta(0.10f);
                Object[] keyTable = deltas.keyTable;
                Object[] valueTable = deltas.valueTable;
                for (int i = 0, l = valueTable.length; i < l; i++) {
                    IResourceDelta delta = (IResourceDelta) valueTable[i];
                    if (delta != null) {
                        IProject p = (IProject) keyTable[i];
                        ClasspathLocation[] classFoldersAndJars = (ClasspathLocation[]) this.javaBuilder.binaryLocationsPerProject.get(p);
                        if (classFoldersAndJars != null)
                            if (!findAffectedSourceFiles(delta, classFoldersAndJars, p))
                                return false;
                    }
                }
                this.notifier.updateProgressDelta(0.10f);
                this.notifier.subTask(Messages.build_analyzingSources);
                addAffectedSourceFiles();
                this.notifier.updateProgressDelta(0.05f);
            }
            this.compileLoop = 0;
            float increment = 0.40f;
            while (// added to in acceptResult
            this.sourceFiles.size() > 0) {
                if (++this.compileLoop > MaxCompileLoop) {
                    if (JavaBuilder.DEBUG)
                        System.out.println("ABORTING incremental build... exceeded loop count");
                    return false;
                }
                this.notifier.checkCancel();
                SourceFile[] allSourceFiles = new SourceFile[this.sourceFiles.size()];
                this.sourceFiles.toArray(allSourceFiles);
                resetCollections();
                this.workQueue.addAll(allSourceFiles);
                this.notifier.setProgressPerCompilationUnit(increment / allSourceFiles.length);
                increment = increment / 2;
                compile(allSourceFiles);
                removeSecondaryTypes();
                addAffectedSourceFiles();
            }
            if (this.hasStructuralChanges && this.javaBuilder.javaProject.hasCycleMarker())
                this.javaBuilder.mustPropagateStructuralChanges();
        } catch (AbortIncrementalBuildException e) {
            if (JavaBuilder.DEBUG)
                System.out.println("ABORTING incremental build... problem with " + e.qualifiedTypeName + ". Likely renamed inside its existing source file.");
            return false;
        } catch (CoreException e) {
            throw internalException(e);
        } finally {
            cleanUp();
        }
        return true;
    }

    protected void buildAfterBatchBuild() {
        if (JavaBuilder.DEBUG)
            //$NON-NLS-1$
            System.out.println("INCREMENTAL build after batch build @ " + new Date(System.currentTimeMillis()));
        // this is a copy of the incremental build loop
        try {
            addAffectedSourceFiles();
            while (this.sourceFiles.size() > 0) {
                this.notifier.checkCancel();
                SourceFile[] allSourceFiles = new SourceFile[this.sourceFiles.size()];
                this.sourceFiles.toArray(allSourceFiles);
                resetCollections();
                this.notifier.setProgressPerCompilationUnit(0.08f / allSourceFiles.length);
                this.workQueue.addAll(allSourceFiles);
                compile(allSourceFiles);
                removeSecondaryTypes();
                addAffectedSourceFiles();
            }
        } catch (CoreException e) {
            throw internalException(e);
        } finally {
            cleanUp();
        }
    }

    protected void addAffectedSourceFiles() {
        if (this.qualifiedStrings.elementSize == 0 && this.simpleStrings.elementSize == 0)
            return;
        addAffectedSourceFiles(this.qualifiedStrings, this.simpleStrings, this.rootStrings, null);
    }

    protected void addAffectedSourceFiles(StringSet qualifiedSet, StringSet simpleSet, StringSet rootSet, StringSet affectedTypes) {
        // the qualifiedStrings are of the form 'p1/p2' & the simpleStrings are just 'X'
        char[][][] internedQualifiedNames = ReferenceCollection.internQualifiedNames(qualifiedSet);
        // if a well known qualified name was found then we can skip over these
        if (internedQualifiedNames.length < qualifiedSet.elementSize)
            internedQualifiedNames = null;
        char[][] internedSimpleNames = ReferenceCollection.internSimpleNames(simpleSet, true);
        // if a well known name was found then we can skip over these
        if (internedSimpleNames.length < simpleSet.elementSize)
            internedSimpleNames = null;
        char[][] internedRootNames = ReferenceCollection.internSimpleNames(rootSet, false);
        Object[] keyTable = this.newState.references.keyTable;
        Object[] valueTable = this.newState.references.valueTable;
        next: for (int i = 0, l = valueTable.length; i < l; i++) {
            String typeLocator = (String) keyTable[i];
            if (typeLocator != null) {
                if (affectedTypes != null && !affectedTypes.includes(typeLocator))
                    continue next;
                ReferenceCollection refs = (ReferenceCollection) valueTable[i];
                if (refs.includes(internedQualifiedNames, internedSimpleNames, internedRootNames)) {
                    IFile file = this.javaBuilder.currentProject.getFile(typeLocator);
                    SourceFile sourceFile = findSourceFile(file, true);
                    if (sourceFile == null)
                        continue next;
                    if (this.sourceFiles.contains(sourceFile))
                        continue next;
                    if (this.compiledAllAtOnce && this.previousSourceFiles != null && this.previousSourceFiles.contains(sourceFile))
                        // can skip previously compiled files since already saw hierarchy related problems
                        continue next;
                    if (JavaBuilder.DEBUG)
                        System.out.println(//$NON-NLS-1$
                        "  adding affected source file " + //$NON-NLS-1$
                        typeLocator);
                    this.sourceFiles.add(sourceFile);
                }
            }
        }
    }

    protected void addDependentsOf(IPath path, boolean isStructuralChange) {
        addDependentsOf(path, isStructuralChange, this.qualifiedStrings, this.simpleStrings, this.rootStrings);
    }

    protected void addDependentsOf(IPath path, boolean isStructuralChange, StringSet qualifiedNames, StringSet simpleNames, StringSet rootNames) {
        path = path.setDevice(null);
        if (isStructuralChange) {
            String last = path.lastSegment();
            if (last.length() == TypeConstants.PACKAGE_INFO_NAME.length)
                if (CharOperation.equals(last.toCharArray(), TypeConstants.PACKAGE_INFO_NAME)) {
                    // the package-info file has changed so blame the package itself
                    path = path.removeLastSegments(1);
                    /* https://bugs.eclipse.org/bugs/show_bug.cgi?id=323785, in the case of default package,
				   there is no need to blame the package itself as there can be no annotations or documentation
				   comment tags in the package-info file that can influence the rest of the package. Just bail out
				   so we don't touch null objects below.
				 */
                    if (path.isEmpty())
                        return;
                }
        }
        if (isStructuralChange && !this.hasStructuralChanges) {
            this.newState.tagAsStructurallyChanged();
            this.hasStructuralChanges = true;
        }
        // the qualifiedStrings are of the form 'p1/p2' & the simpleStrings are just 'X'
        rootNames.add(path.segment(0));
        String packageName = path.removeLastSegments(1).toString();
        boolean wasNew = qualifiedNames.add(packageName);
        String typeName = path.lastSegment();
        int memberIndex = typeName.indexOf('$');
        if (memberIndex > 0)
            typeName = typeName.substring(0, memberIndex);
        wasNew = simpleNames.add(typeName) | wasNew;
        if (wasNew && JavaBuilder.DEBUG)
            System.out.println(//$NON-NLS-1$
            "  will look for dependents of " + typeName + " in " + //$NON-NLS-1$
            packageName);
    }

    protected boolean checkForClassFileChanges(IResourceDelta binaryDelta, ClasspathMultiDirectory md, int segmentCount) throws CoreException {
        IResource resource = binaryDelta.getResource();
        // remember that if inclusion & exclusion patterns change then a full build is done
        boolean isExcluded = (md.exclusionPatterns != null || md.inclusionPatterns != null) && Util.isExcluded(resource, md.inclusionPatterns, md.exclusionPatterns);
        switch(resource.getType()) {
            case IResource.FOLDER:
                if (isExcluded && md.inclusionPatterns == null)
                    // no need to go further with this delta since its children cannot be included
                    return true;
                IResourceDelta[] children = binaryDelta.getAffectedChildren();
                for (int i = 0, l = children.length; i < l; i++) if (!checkForClassFileChanges(children[i], md, segmentCount))
                    return false;
                return true;
            case IResource.FILE:
                if (!isExcluded && org.eclipse.jdt.internal.compiler.util.Util.isClassFileName(resource.getName())) {
                    // perform full build if a managed class file has been changed
                    IPath typePath = resource.getFullPath().removeFirstSegments(segmentCount).removeFileExtension();
                    if (this.newState.isKnownType(typePath.toString())) {
                        if (JavaBuilder.DEBUG)
                            System.out.println(//$NON-NLS-1$
                            "MUST DO FULL BUILD. Found change to class file " + //$NON-NLS-1$
                            typePath);
                        return false;
                    }
                    return true;
                }
        }
        return true;
    }

    protected void cleanUp() {
        super.cleanUp();
        this.sourceFiles = null;
        this.previousSourceFiles = null;
        this.qualifiedStrings = null;
        this.simpleStrings = null;
        this.rootStrings = null;
        this.secondaryTypesToRemove = null;
        this.hasStructuralChanges = false;
        this.compileLoop = 0;
    }

    protected void compile(SourceFile[] units, SourceFile[] additionalUnits, boolean compilingFirstGroup) {
        if (compilingFirstGroup && additionalUnits != null) {
            // add any source file from additionalUnits to units if it defines secondary types
            // otherwise its possible during testing with MAX_AT_ONCE == 1 that a secondary type
            // can cause an infinite loop as it alternates between not found and defined, see bug 146324
            ArrayList extras = null;
            for (int i = 0, l = additionalUnits.length; i < l; i++) {
                SourceFile unit = additionalUnits[i];
                if (unit != null && this.newState.getDefinedTypeNamesFor(unit.typeLocator()) != null) {
                    if (JavaBuilder.DEBUG)
                        System.out.println("About to compile file with secondary types " + //$NON-NLS-1$
                        unit.typeLocator());
                    if (extras == null)
                        extras = new ArrayList(3);
                    extras.add(unit);
                }
            }
            if (extras != null) {
                int oldLength = units.length;
                int toAdd = extras.size();
                System.arraycopy(units, 0, units = new SourceFile[oldLength + toAdd], 0, oldLength);
                for (int i = 0; i < toAdd; i++) units[oldLength++] = (SourceFile) extras.get(i);
            }
        }
        super.compile(units, additionalUnits, compilingFirstGroup);
    }

    protected void deleteGeneratedFiles(IFile[] deletedGeneratedFiles) {
        // delete generated files and recompile any affected source files
        try {
            for (int j = deletedGeneratedFiles.length; --j >= 0; ) {
                IFile deletedFile = deletedGeneratedFiles[j];
                // only delete .class files for source files that were actually deleted
                if (deletedFile.exists())
                    continue;
                SourceFile sourceFile = findSourceFile(deletedFile, false);
                String typeLocator = sourceFile.typeLocator();
                int mdSegmentCount = sourceFile.sourceLocation.sourceFolder.getFullPath().segmentCount();
                IPath typePath = sourceFile.resource.getFullPath().removeFirstSegments(mdSegmentCount).removeFileExtension();
                // add dependents of the source file since its now deleted
                addDependentsOf(typePath, true);
                // existing source files did not see it as deleted since they were compiled before it was
                this.previousSourceFiles = null;
                char[][] definedTypeNames = this.newState.getDefinedTypeNamesFor(typeLocator);
                if (// defined a single type matching typePath
                definedTypeNames == null) {
                    removeClassFile(typePath, sourceFile.sourceLocation.binaryFolder);
                } else {
                    if (// skip it if it failed to successfully define a type
                    definedTypeNames.length > 0) {
                        IPath packagePath = typePath.removeLastSegments(1);
                        for (int d = 0, l = definedTypeNames.length; d < l; d++) removeClassFile(packagePath.append(new String(definedTypeNames[d])), sourceFile.sourceLocation.binaryFolder);
                    }
                }
                this.newState.removeLocator(typeLocator);
            }
        } catch (CoreException e) {
            Util.log(e, "JavaBuilder logging CompilationParticipant's CoreException to help debugging");
        }
    }

    protected boolean findAffectedSourceFiles(IResourceDelta delta, ClasspathLocation[] classFoldersAndJars, IProject prereqProject) {
        for (int i = 0, l = classFoldersAndJars.length; i < l; i++) {
            ClasspathLocation bLocation = classFoldersAndJars[i];
            // either a .class file folder or a zip/jar file
            if (// skip unchanged output folder
            bLocation != null) {
                IPath p = bLocation.getProjectRelativePath();
                if (p != null) {
                    IResourceDelta binaryDelta = delta.findMember(p);
                    if (binaryDelta != null) {
                        if (bLocation instanceof ClasspathJar) {
                            if (JavaBuilder.DEBUG)
                                System.out.println("ABORTING incremental build... found delta to jar/zip file");
                            // do full build since jar file was changed (added/removed were caught as classpath change)
                            return false;
                        }
                        if (binaryDelta.getKind() == IResourceDelta.ADDED || binaryDelta.getKind() == IResourceDelta.REMOVED) {
                            if (JavaBuilder.DEBUG)
                                System.out.println("ABORTING incremental build... found added/removed binary folder");
                            // added/removed binary folder should not make it here (classpath change), but handle anyways
                            return false;
                        }
                        int segmentCount = binaryDelta.getFullPath().segmentCount();
                        // .class files from class folder
                        IResourceDelta[] children = binaryDelta.getAffectedChildren();
                        StringSet structurallyChangedTypes = null;
                        if (bLocation.isOutputFolder())
                            structurallyChangedTypes = this.newState.getStructurallyChangedTypes(this.javaBuilder.getLastState(prereqProject));
                        for (int j = 0, m = children.length; j < m; j++) findAffectedSourceFiles(children[j], segmentCount, structurallyChangedTypes);
                        this.notifier.checkCancel();
                    }
                }
            }
        }
        return true;
    }

    protected void findAffectedSourceFiles(IResourceDelta binaryDelta, int segmentCount, StringSet structurallyChangedTypes) {
        // When a package becomes a type or vice versa, expect 2 deltas,
        // one on the folder & one on the class file
        IResource resource = binaryDelta.getResource();
        switch(resource.getType()) {
            case IResource.FOLDER:
                switch(binaryDelta.getKind()) {
                    case IResourceDelta.ADDED:
                    case IResourceDelta.REMOVED:
                        IPath packagePath = resource.getFullPath().removeFirstSegments(segmentCount);
                        String packageName = packagePath.toString();
                        if (binaryDelta.getKind() == IResourceDelta.ADDED) {
                            // see if any known source file is from the same package... classpath already includes new package
                            if (!this.newState.isKnownPackage(packageName)) {
                                if (JavaBuilder.DEBUG)
                                    System.out.println("Found added package " + packageName);
                                addDependentsOf(packagePath, false);
                                return;
                            }
                            if (JavaBuilder.DEBUG)
                                System.out.println(//$NON-NLS-1$
                                "Skipped dependents of added package " + //$NON-NLS-1$
                                packageName);
                        } else {
                            // see if the package still exists on the classpath
                            if (!this.nameEnvironment.isPackage(packageName)) {
                                if (JavaBuilder.DEBUG)
                                    System.out.println("Found removed package " + packageName);
                                addDependentsOf(packagePath, false);
                                return;
                            }
                            if (JavaBuilder.DEBUG)
                                System.out.println(//$NON-NLS-1$
                                "Skipped dependents of removed package " + //$NON-NLS-1$
                                packageName);
                        }
                    //$FALL-THROUGH$ traverse the sub-packages and .class files
                    case IResourceDelta.CHANGED:
                        IResourceDelta[] children = binaryDelta.getAffectedChildren();
                        for (int i = 0, l = children.length; i < l; i++) findAffectedSourceFiles(children[i], segmentCount, structurallyChangedTypes);
                }
                return;
            case IResource.FILE:
                if (org.eclipse.jdt.internal.compiler.util.Util.isClassFileName(resource.getName())) {
                    IPath typePath = resource.getFullPath().removeFirstSegments(segmentCount).removeFileExtension();
                    switch(binaryDelta.getKind()) {
                        case IResourceDelta.ADDED:
                        case IResourceDelta.REMOVED:
                            if (JavaBuilder.DEBUG)
                                System.out.println("Found added/removed class file " + typePath);
                            addDependentsOf(typePath, false);
                            return;
                        case IResourceDelta.CHANGED:
                            if ((binaryDelta.getFlags() & IResourceDelta.CONTENT) == 0)
                                // skip it since it really isn't changed
                                return;
                            if (structurallyChangedTypes != null && !structurallyChangedTypes.includes(typePath.toString()))
                                // skip since it wasn't a structural change
                                return;
                            if (JavaBuilder.DEBUG)
                                System.out.println("Found changed class file " + typePath);
                            addDependentsOf(typePath, false);
                    }
                    return;
                }
        }
    }

    protected boolean findSourceFiles(IResourceDelta delta) throws CoreException {
        ArrayList visited = this.makeOutputFolderConsistent ? new ArrayList(this.sourceLocations.length) : null;
        for (int i = 0, l = this.sourceLocations.length; i < l; i++) {
            ClasspathMultiDirectory md = this.sourceLocations[i];
            if (this.makeOutputFolderConsistent && md.hasIndependentOutputFolder && !visited.contains(md.binaryFolder)) {
                // even a project which acts as its own source folder can have an independent/nested output folder
                visited.add(md.binaryFolder);
                IResourceDelta binaryDelta = delta.findMember(md.binaryFolder.getProjectRelativePath());
                if (binaryDelta != null) {
                    int segmentCount = binaryDelta.getFullPath().segmentCount();
                    IResourceDelta[] children = binaryDelta.getAffectedChildren();
                    for (int j = 0, m = children.length; j < m; j++) if (!checkForClassFileChanges(children[j], md, segmentCount))
                        return false;
                }
            }
            if (md.sourceFolder.equals(this.javaBuilder.currentProject)) {
                // skip nested source & output folders when the project is a source folder
                int segmentCount = delta.getFullPath().segmentCount();
                IResourceDelta[] children = delta.getAffectedChildren();
                for (int j = 0, m = children.length; j < m; j++) if (!isExcludedFromProject(children[j].getFullPath()))
                    if (!findSourceFiles(children[j], md, segmentCount))
                        return false;
            } else {
                IResourceDelta sourceDelta = delta.findMember(md.sourceFolder.getProjectRelativePath());
                if (sourceDelta != null) {
                    if (sourceDelta.getKind() == IResourceDelta.REMOVED) {
                        if (JavaBuilder.DEBUG)
                            System.out.println("ABORTING incremental build... found removed source folder");
                        // removed source folder should not make it here, but handle anyways (ADDED is supported)
                        return false;
                    }
                    int segmentCount = sourceDelta.getFullPath().segmentCount();
                    IResourceDelta[] children = sourceDelta.getAffectedChildren();
                    try {
                        for (int j = 0, m = children.length; j < m; j++) if (!findSourceFiles(children[j], md, segmentCount))
                            return false;
                    } catch (CoreException e) {
                        if (e.getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS) {
                            if (JavaBuilder.DEBUG)
                                System.out.println("ABORTING incremental build... found renamed package");
                            return false;
                        }
                        throw e;
                    }
                }
            }
            this.notifier.checkCancel();
        }
        return true;
    }

    protected boolean findSourceFiles(IResourceDelta sourceDelta, ClasspathMultiDirectory md, int segmentCount) throws CoreException {
        // When a package becomes a type or vice versa, expect 2 deltas,
        // one on the folder & one on the source file
        IResource resource = sourceDelta.getResource();
        // remember that if inclusion & exclusion patterns change then a full build is done
        boolean isExcluded = (md.exclusionPatterns != null || md.inclusionPatterns != null) && Util.isExcluded(resource, md.inclusionPatterns, md.exclusionPatterns);
        switch(resource.getType()) {
            case IResource.FOLDER:
                if (isExcluded && md.inclusionPatterns == null)
                    // no need to go further with this delta since its children cannot be included
                    return true;
                switch(sourceDelta.getKind()) {
                    case IResourceDelta.ADDED:
                        if (!isExcluded) {
                            IPath addedPackagePath = resource.getFullPath().removeFirstSegments(segmentCount);
                            // ensure package exists in the output folder
                            createFolder(addedPackagePath, md.binaryFolder);
                            // see if any known source file is from the same package... classpath already includes new package
                            if (this.sourceLocations.length > 1 && this.newState.isKnownPackage(addedPackagePath.toString())) {
                                if (JavaBuilder.DEBUG)
                                    System.out.println(//$NON-NLS-1$
                                    "Skipped dependents of added package " + //$NON-NLS-1$
                                    addedPackagePath);
                            } else {
                                if (JavaBuilder.DEBUG)
                                    System.out.println(//$NON-NLS-1$
                                    "Found added package " + //$NON-NLS-1$
                                    addedPackagePath);
                                addDependentsOf(addedPackagePath, true);
                            }
                        }
                    //$FALL-THROUGH$ collect all the source files
                    case IResourceDelta.CHANGED:
                        IResourceDelta[] children = sourceDelta.getAffectedChildren();
                        for (int i = 0, l = children.length; i < l; i++) if (!findSourceFiles(children[i], md, segmentCount))
                            return false;
                        return true;
                    case IResourceDelta.REMOVED:
                        if (isExcluded) {
                            // since this folder is excluded then there is nothing to delete (from this md), but must walk any included subfolders
                            children = sourceDelta.getAffectedChildren();
                            for (int i = 0, l = children.length; i < l; i++) if (!findSourceFiles(children[i], md, segmentCount))
                                return false;
                            return true;
                        }
                        IPath removedPackagePath = resource.getFullPath().removeFirstSegments(segmentCount);
                        if (this.sourceLocations.length > 1) {
                            for (int i = 0, l = this.sourceLocations.length; i < l; i++) {
                                if (this.sourceLocations[i].sourceFolder.getFolder(removedPackagePath).exists()) {
                                    // only a package fragment was removed, same as removing multiple source files
                                    if (md.hasIndependentOutputFolder)
                                        // ensure package exists in the output folder
                                        createFolder(removedPackagePath, md.binaryFolder);
                                    IResourceDelta[] removedChildren = sourceDelta.getAffectedChildren();
                                    for (int j = 0, m = removedChildren.length; j < m; j++) if (!findSourceFiles(removedChildren[j], md, segmentCount))
                                        return false;
                                    return true;
                                }
                            }
                        }
                        if ((sourceDelta.getFlags() & IResourceDelta.MOVED_TO) != 0) {
                            // same idea as moving a source file
                            // see bug 163200
                            IResource movedFolder = this.javaBuilder.workspaceRoot.getFolder(sourceDelta.getMovedToPath());
                            JavaBuilder.removeProblemsAndTasksFor(movedFolder);
                        }
                        IFolder removedPackageFolder = md.binaryFolder.getFolder(removedPackagePath);
                        if (removedPackageFolder.exists())
                            removedPackageFolder.delete(IResource.FORCE, null);
                        // add dependents even when the package thinks it does not exist to be on the safe side
                        if (JavaBuilder.DEBUG)
                            System.out.println(//$NON-NLS-1$
                            "Found removed package " + //$NON-NLS-1$
                            removedPackagePath);
                        addDependentsOf(removedPackagePath, true);
                        this.newState.removePackage(sourceDelta);
                }
                return true;
            case IResource.FILE:
                if (isExcluded)
                    return true;
                String resourceName = resource.getName();
                if (org.eclipse.jdt.internal.core.util.Util.isJavaLikeFileName(resourceName)) {
                    IPath typePath = resource.getFullPath().removeFirstSegments(segmentCount).removeFileExtension();
                    String typeLocator = resource.getProjectRelativePath().toString();
                    switch(sourceDelta.getKind()) {
                        case IResourceDelta.ADDED:
                            if (JavaBuilder.DEBUG)
                                System.out.println(//$NON-NLS-1$
                                "Compile this added source file " + //$NON-NLS-1$
                                typeLocator);
                            this.sourceFiles.add(new SourceFile((IFile) resource, md, true));
                            String typeName = typePath.toString();
                            if (// adding dependents results in 2 duplicate errors
                            !this.newState.isDuplicateLocator(typeName, typeLocator)) {
                                if (JavaBuilder.DEBUG)
                                    System.out.println("Found added source file " + typeName);
                                addDependentsOf(typePath, true);
                            }
                            return true;
                        case IResourceDelta.REMOVED:
                            char[][] definedTypeNames = this.newState.getDefinedTypeNamesFor(typeLocator);
                            if (// defined a single type matching typePath
                            definedTypeNames == null) {
                                removeClassFile(typePath, md.binaryFolder);
                                if ((sourceDelta.getFlags() & IResourceDelta.MOVED_TO) != 0) {
                                    // remove problems and tasks for a compilation unit that is being moved (to another package or renamed)
                                    // if the target file is a compilation unit, the new cu will be recompiled
                                    // if the target file is a non-java resource, then markers are removed
                                    // see bug 2857
                                    IResource movedFile = this.javaBuilder.workspaceRoot.getFile(sourceDelta.getMovedToPath());
                                    JavaBuilder.removeProblemsAndTasksFor(movedFile);
                                }
                            } else {
                                if (JavaBuilder.DEBUG)
                                    System.out.println(//$NON-NLS-1$
                                    "Found removed source file " + //$NON-NLS-1$
                                    typePath.toString());
                                // add dependents of the source file since it may be involved in a name collision
                                addDependentsOf(typePath, true);
                                if (// skip it if it failed to successfully define a type
                                definedTypeNames.length > 0) {
                                    IPath packagePath = typePath.removeLastSegments(1);
                                    for (int i = 0, l = definedTypeNames.length; i < l; i++) removeClassFile(packagePath.append(new String(definedTypeNames[i])), md.binaryFolder);
                                }
                            }
                            this.newState.removeLocator(typeLocator);
                            return true;
                        case IResourceDelta.CHANGED:
                            if ((sourceDelta.getFlags() & IResourceDelta.CONTENT) == 0 && (sourceDelta.getFlags() & IResourceDelta.ENCODING) == 0)
                                // skip it since it really isn't changed
                                return true;
                            if (JavaBuilder.DEBUG)
                                System.out.println(//$NON-NLS-1$
                                "Compile this changed source file " + //$NON-NLS-1$
                                typeLocator);
                            this.sourceFiles.add(new SourceFile((IFile) resource, md, true));
                    }
                    return true;
                } else if (org.eclipse.jdt.internal.compiler.util.Util.isClassFileName(resourceName)) {
                    // perform full build if a managed class file has been changed
                    if (this.makeOutputFolderConsistent) {
                        IPath typePath = resource.getFullPath().removeFirstSegments(segmentCount).removeFileExtension();
                        if (this.newState.isKnownType(typePath.toString())) {
                            if (JavaBuilder.DEBUG)
                                System.out.println("MUST DO FULL BUILD. Found change to class file " + typePath);
                            return false;
                        }
                    }
                    return true;
                } else if (md.hasIndependentOutputFolder) {
                    if (this.javaBuilder.filterExtraResource(resource))
                        return true;
                    // copy all other resource deltas to the output folder
                    IPath resourcePath = resource.getFullPath().removeFirstSegments(segmentCount);
                    IResource outputFile = md.binaryFolder.getFile(resourcePath);
                    switch(sourceDelta.getKind()) {
                        case IResourceDelta.ADDED:
                            if (outputFile.exists()) {
                                if (JavaBuilder.DEBUG)
                                    System.out.println("Deleting existing file " + resourcePath);
                                outputFile.delete(IResource.FORCE, null);
                            }
                            if (JavaBuilder.DEBUG)
                                System.out.println(//$NON-NLS-1$
                                "Copying added file " + //$NON-NLS-1$
                                resourcePath);
                            // ensure package exists in the output folder
                            createFolder(resourcePath.removeLastSegments(1), md.binaryFolder);
                            copyResource(resource, outputFile);
                            return true;
                        case IResourceDelta.REMOVED:
                            if (outputFile.exists()) {
                                if (JavaBuilder.DEBUG)
                                    System.out.println("Deleting removed file " + resourcePath);
                                outputFile.delete(IResource.FORCE, null);
                            }
                            return true;
                        case IResourceDelta.CHANGED:
                            if ((sourceDelta.getFlags() & IResourceDelta.CONTENT) == 0 && (sourceDelta.getFlags() & IResourceDelta.ENCODING) == 0)
                                // skip it since it really isn't changed
                                return true;
                            if (outputFile.exists()) {
                                if (JavaBuilder.DEBUG)
                                    System.out.println("Deleting existing file " + resourcePath);
                                outputFile.delete(IResource.FORCE, null);
                            }
                            if (JavaBuilder.DEBUG)
                                System.out.println(//$NON-NLS-1$
                                "Copying changed file " + //$NON-NLS-1$
                                resourcePath);
                            // ensure package exists in the output folder
                            createFolder(resourcePath.removeLastSegments(1), md.binaryFolder);
                            copyResource(resource, outputFile);
                    }
                    return true;
                }
        }
        return true;
    }

    protected void finishedWith(String sourceLocator, CompilationResult result, char[] mainTypeName, ArrayList definedTypeNames, ArrayList duplicateTypeNames) {
        char[][] previousTypeNames = this.newState.getDefinedTypeNamesFor(sourceLocator);
        if (previousTypeNames == null)
            previousTypeNames = new char[][] { mainTypeName };
        IPath packagePath = null;
        next: for (int i = 0, l = previousTypeNames.length; i < l; i++) {
            char[] previous = previousTypeNames[i];
            for (int j = 0, m = definedTypeNames.size(); j < m; j++) if (CharOperation.equals(previous, (char[]) definedTypeNames.get(j)))
                continue next;
            SourceFile sourceFile = (SourceFile) result.getCompilationUnit();
            if (packagePath == null) {
                int count = sourceFile.sourceLocation.sourceFolder.getFullPath().segmentCount();
                packagePath = sourceFile.resource.getFullPath().removeFirstSegments(count).removeLastSegments(1);
            }
            if (this.secondaryTypesToRemove == null)
                this.secondaryTypesToRemove = new SimpleLookupTable();
            ArrayList types = (ArrayList) this.secondaryTypesToRemove.get(sourceFile.sourceLocation.binaryFolder);
            if (types == null)
                types = new ArrayList(definedTypeNames.size());
            types.add(packagePath.append(new String(previous)));
            this.secondaryTypesToRemove.put(sourceFile.sourceLocation.binaryFolder, types);
        }
        super.finishedWith(sourceLocator, result, mainTypeName, definedTypeNames, duplicateTypeNames);
    }

    protected void processAnnotationResults(CompilationParticipantResult[] results) {
        for (int i = results.length; --i >= 0; ) {
            CompilationParticipantResult result = results[i];
            if (result == null)
                continue;
            IFile[] deletedGeneratedFiles = result.deletedFiles;
            if (deletedGeneratedFiles != null)
                deleteGeneratedFiles(deletedGeneratedFiles);
            IFile[] addedGeneratedFiles = result.addedFiles;
            if (addedGeneratedFiles != null) {
                for (int j = addedGeneratedFiles.length; --j >= 0; ) {
                    SourceFile sourceFile = findSourceFile(addedGeneratedFiles[j], true);
                    if (sourceFile != null && !this.sourceFiles.contains(sourceFile))
                        this.sourceFiles.add(sourceFile);
                }
            }
            recordParticipantResult(result);
        }
    }

    protected void removeClassFile(IPath typePath, IContainer outputFolder) throws CoreException {
        if (// is not a nested type
        typePath.lastSegment().indexOf('$') == -1) {
            this.newState.removeQualifiedTypeName(typePath.toString());
            // add dependents even when the type thinks it does not exist to be on the safe side
            if (JavaBuilder.DEBUG)
                //$NON-NLS-1$
                System.out.println("Found removed type " + typePath);
            // when member types are removed, their enclosing type is structurally changed
            addDependentsOf(typePath, true);
        }
        IFile classFile = outputFolder.getFile(typePath.addFileExtension(SuffixConstants.EXTENSION_class));
        if (classFile.exists()) {
            if (JavaBuilder.DEBUG)
                //$NON-NLS-1$
                System.out.println("Deleting class file of removed type " + typePath);
            classFile.delete(IResource.FORCE, null);
        }
    }

    protected void removeSecondaryTypes() throws CoreException {
        if (// delayed deleting secondary types until the end of the compile loop
        this.secondaryTypesToRemove != null) {
            Object[] keyTable = this.secondaryTypesToRemove.keyTable;
            Object[] valueTable = this.secondaryTypesToRemove.valueTable;
            for (int i = 0, l = keyTable.length; i < l; i++) {
                IContainer outputFolder = (IContainer) keyTable[i];
                if (outputFolder != null) {
                    ArrayList paths = (ArrayList) valueTable[i];
                    for (int j = 0, m = paths.size(); j < m; j++) removeClassFile((IPath) paths.get(j), outputFolder);
                }
            }
            this.secondaryTypesToRemove = null;
            if (this.previousSourceFiles != null)
                // cannot optimize recompile case when a secondary type is deleted, see 181269
                this.previousSourceFiles = null;
        }
    }

    protected void resetCollections() {
        if (this.sourceFiles == null) {
            this.sourceFiles = new ArrayList(33);
            this.previousSourceFiles = null;
            this.qualifiedStrings = new StringSet(3);
            this.simpleStrings = new StringSet(3);
            this.rootStrings = new StringSet(3);
            this.hasStructuralChanges = false;
            this.compileLoop = 0;
        } else {
            this.previousSourceFiles = this.sourceFiles.isEmpty() ? null : (ArrayList) this.sourceFiles.clone();
            this.sourceFiles.clear();
            this.qualifiedStrings.clear();
            this.simpleStrings.clear();
            this.rootStrings.clear();
            this.workQueue.clear();
        }
    }

    protected void updateProblemsFor(SourceFile sourceFile, CompilationResult result) throws CoreException {
        if (CharOperation.equals(sourceFile.getMainTypeName(), TypeConstants.PACKAGE_INFO_NAME)) {
            IResource pkgResource = sourceFile.resource.getParent();
            pkgResource.deleteMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, false, IResource.DEPTH_ZERO);
        }
        IMarker[] markers = JavaBuilder.getProblemsFor(sourceFile.resource);
        CategorizedProblem[] problems = result.getProblems();
        if (problems == null && markers.length == 0)
            return;
        this.notifier.updateProblemCounts(markers, problems);
        JavaBuilder.removeProblemsFor(sourceFile.resource);
        storeProblemsFor(sourceFile, problems);
    }

    protected void updateTasksFor(SourceFile sourceFile, CompilationResult result) throws CoreException {
        IMarker[] markers = JavaBuilder.getTasksFor(sourceFile.resource);
        CategorizedProblem[] tasks = result.getTasks();
        if (tasks == null && markers.length == 0)
            return;
        JavaBuilder.removeTasksFor(sourceFile.resource);
        storeTasksFor(sourceFile, tasks);
    }

    /**
 * @see org.eclipse.jdt.internal.core.builder.AbstractImageBuilder#writeClassFileContents(org.eclipse.jdt.internal.compiler.ClassFile, org.eclipse.core.resources.IFile, java.lang.String, boolean, org.eclipse.jdt.internal.core.builder.SourceFile)
 */
    protected void writeClassFileContents(ClassFile classfile, IFile file, String qualifiedFileName, boolean isTopLevelType, SourceFile compilationUnit) throws CoreException {
        // Before writing out the class file, compare it to the previous file
        // If structural changes occurred then add dependent source files
        byte[] bytes = classfile.getBytes();
        if (file.exists()) {
            if (// see 46093
            writeClassFileCheck(file, qualifiedFileName, bytes) || compilationUnit.updateClassFile) {
                if (JavaBuilder.DEBUG)
                    System.out.println("Writing changed class file " + //$NON-NLS-1$
                    file.getName());
                if (!file.isDerived())
                    file.setDerived(true, null);
                file.setContents(new ByteArrayInputStream(bytes), true, false, null);
            } else if (JavaBuilder.DEBUG) {
                //$NON-NLS-1$
                System.out.println("Skipped over unchanged class file " + file.getName());
            }
        } else {
            if (isTopLevelType)
                // new type
                addDependentsOf(new Path(qualifiedFileName), true);
            if (JavaBuilder.DEBUG)
                //$NON-NLS-1$
                System.out.println("Writing new class file " + file.getName());
            try {
                file.create(new ByteArrayInputStream(bytes), IResource.FORCE | IResource.DERIVED, null);
            } catch (CoreException e) {
                if (e.getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS) {
                    IStatus status = e.getStatus();
                    if (status instanceof IResourceStatus) {
                        IPath oldFilePath = ((IResourceStatus) status).getPath();
                        char[] oldTypeName = oldFilePath.removeFileExtension().lastSegment().toCharArray();
                        char[][] previousTypeNames = this.newState.getDefinedTypeNamesFor(compilationUnit.typeLocator());
                        boolean fromSameFile = false;
                        if (previousTypeNames == null) {
                            fromSameFile = CharOperation.equals(compilationUnit.getMainTypeName(), oldTypeName);
                        } else {
                            for (int i = 0, l = previousTypeNames.length; i < l; i++) {
                                if (CharOperation.equals(previousTypeNames[i], oldTypeName)) {
                                    fromSameFile = true;
                                    break;
                                }
                            }
                        }
                        if (fromSameFile) {
                            IFile collision = file.getParent().getFile(new Path(oldFilePath.lastSegment()));
                            collision.delete(true, false, null);
                            boolean success = false;
                            try {
                                file.create(new ByteArrayInputStream(bytes), IResource.FORCE | IResource.DERIVED, null);
                                success = true;
                            } catch (CoreException ignored) {
                            }
                            if (success)
                                return;
                        }
                    }
                    throw new AbortCompilation(true, new AbortIncrementalBuildException(qualifiedFileName));
                }
                throw e;
            }
        }
    }

    protected boolean writeClassFileCheck(IFile file, String fileName, byte[] newBytes) throws CoreException {
        try {
            byte[] oldBytes = Util.getResourceContentsAsByteArray(file);
            notEqual: if (newBytes.length == oldBytes.length) {
                for (int i = newBytes.length; --i >= 0; ) if (newBytes[i] != oldBytes[i])
                    break notEqual;
                // bytes are identical so skip them
                return false;
            }
            URI location = file.getLocationURI();
            // unable to determine location of this class file
            if (location == null)
                return false;
            String filePath = location.getSchemeSpecificPart();
            ClassFileReader reader = new ClassFileReader(oldBytes, filePath.toCharArray());
            // ignore local types since they're only visible inside a single method
            if (!(reader.isLocal() || reader.isAnonymous()) && reader.hasStructuralChanges(newBytes)) {
                if (JavaBuilder.DEBUG)
                    //$NON-NLS-1$
                    System.out.println(//$NON-NLS-1$
                    "Type has structural changes " + fileName);
                addDependentsOf(new Path(fileName), true);
                this.newState.wasStructurallyChanged(fileName);
            }
        } catch (ClassFormatException e) {
            addDependentsOf(new Path(fileName), true);
            this.newState.wasStructurallyChanged(fileName);
        }
        return true;
    }

    public String toString() {
        //$NON-NLS-1$
        return "incremental image builder for:\n\tnew state: " + this.newState;
    }
    /* Debug helper

static void dump(IResourceDelta delta) {
	StringBuffer buffer = new StringBuffer();
	IPath path = delta.getFullPath();
	for (int i = path.segmentCount(); --i > 0;)
		buffer.append("  ");
	switch (delta.getKind()) {
		case IResourceDelta.ADDED:
			buffer.append('+');
			break;
		case IResourceDelta.REMOVED:
			buffer.append('-');
			break;
		case IResourceDelta.CHANGED:
			buffer.append('*');
			break;
		case IResourceDelta.NO_CHANGE:
			buffer.append('=');
			break;
		default:
			buffer.append('?');
			break;
	}
	buffer.append(path);
	System.out.println(buffer.toString());
	IResourceDelta[] children = delta.getAffectedChildren();
	for (int i = 0, l = children.length; i < l; i++)
		dump(children[i]);
}
*/
}
