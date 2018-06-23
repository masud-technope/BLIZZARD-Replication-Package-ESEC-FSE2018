/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.core.search.indexing;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.zip.CRC32;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.search.*;
import org.eclipse.jdt.internal.compiler.ISourceElementRequestor;
import org.eclipse.jdt.internal.compiler.SourceElementParser;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;
import org.eclipse.jdt.internal.compiler.util.SimpleSet;
import org.eclipse.jdt.internal.core.*;
import org.eclipse.jdt.internal.core.index.*;
import org.eclipse.jdt.internal.core.search.BasicSearchEngine;
import org.eclipse.jdt.internal.core.search.PatternSearchJob;
import org.eclipse.jdt.internal.core.search.processing.IJob;
import org.eclipse.jdt.internal.core.search.processing.JobManager;
import org.eclipse.jdt.internal.core.util.Messages;
import org.eclipse.jdt.internal.core.util.Util;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class IndexManager extends JobManager implements IIndexConstants {

    // key = containerPath, value = indexLocation path
    // indexLocation path is created by appending an index file name to the getJavaPluginWorkingLocation() path
    public SimpleLookupTable indexLocations = new SimpleLookupTable();

    // key = indexLocation path, value = an index
    private SimpleLookupTable indexes = new SimpleLookupTable();

    /* need to save ? */
    private boolean needToSave = false;

    private IPath javaPluginLocation = null;

    /* can only replace a current state if its less than the new one */
    // key = indexLocation path, value = index state integer
    private SimpleLookupTable indexStates = null;

    //$NON-NLS-1$
    private File indexNamesMapFile = new File(getSavedIndexesDirectory(), "indexNamesMap.txt");

    //$NON-NLS-1$
    private File savedIndexNamesFile = new File(getSavedIndexesDirectory(), "savedIndexNames.txt");

    //$NON-NLS-1$
    private File participantIndexNamesFile = new File(getSavedIndexesDirectory(), "participantsIndexNames.txt");

    private boolean javaLikeNamesChanged = true;

    public static final Integer SAVED_STATE = 0;

    public static final Integer UPDATING_STATE = 1;

    public static final Integer UNKNOWN_STATE = 2;

    public static final Integer REBUILDING_STATE = 3;

    public static final Integer REUSE_STATE = 4;

    // search participants who register indexes with the index manager
    private SimpleLookupTable participantsContainers = null;

    private boolean participantUpdated = false;

    // should JDT manage (update, delete as needed) pre-built indexes?
    //$NON-NLS-1$
    public static final String MANAGE_PRODUCT_INDEXES_PROPERTY = "jdt.core.manageProductIndexes";

    private static final boolean IS_MANAGING_PRODUCT_INDEXES_PROPERTY = Boolean.getBoolean(MANAGE_PRODUCT_INDEXES_PROPERTY);

    // Debug
    public static boolean DEBUG = false;

    public synchronized void aboutToUpdateIndex(IPath containerPath, Integer newIndexState) {
        // newIndexState is either UPDATING_STATE or REBUILDING_STATE
        // must tag the index as inconsistent, in case we exit before the update job is started
        IndexLocation indexLocation = computeIndexLocation(containerPath);
        Object state = getIndexStates().get(indexLocation);
        Integer currentIndexState = state == null ? UNKNOWN_STATE : (Integer) state;
        // already rebuilding the index
        if (currentIndexState.compareTo(REBUILDING_STATE) >= 0)
            return;
        int compare = newIndexState.compareTo(currentIndexState);
        if (compare > 0) {
            // so UPDATING_STATE replaces SAVED_STATE and REBUILDING_STATE replaces everything
            updateIndexState(indexLocation, newIndexState);
        } else if (compare < 0 && this.indexes.get(indexLocation) == null) {
            // if already cached index then there is nothing more to do
            rebuildIndex(indexLocation, containerPath);
        }
    }

    /**
 * Trigger addition of a resource to an index
 * Note: the actual operation is performed in background
 */
    public void addBinary(IFile resource, IPath containerPath) {
        if (JavaCore.getPlugin() == null)
            return;
        SearchParticipant participant = SearchEngine.getDefaultSearchParticipant();
        SearchDocument document = participant.getDocument(resource.getFullPath().toString());
        IndexLocation indexLocation = computeIndexLocation(containerPath);
        scheduleDocumentIndexing(document, containerPath, indexLocation, participant);
    }

    /**
 * Trigger addition of a resource to an index
 * Note: the actual operation is performed in background
 */
    public void addSource(IFile resource, IPath containerPath, SourceElementParser parser) {
        if (JavaCore.getPlugin() == null)
            return;
        SearchParticipant participant = SearchEngine.getDefaultSearchParticipant();
        SearchDocument document = participant.getDocument(resource.getFullPath().toString());
        document.setParser(parser);
        IndexLocation indexLocation = computeIndexLocation(containerPath);
        scheduleDocumentIndexing(document, containerPath, indexLocation, participant);
    }

    /*
 * Removes unused indexes from disk.
 */
    public void cleanUpIndexes() {
        SimpleSet knownPaths = new SimpleSet();
        IJavaSearchScope scope = BasicSearchEngine.createWorkspaceScope();
        PatternSearchJob job = new PatternSearchJob(null, SearchEngine.getDefaultSearchParticipant(), scope, null);
        Index[] selectedIndexes = job.getIndexes(null);
        for (int i = 0, l = selectedIndexes.length; i < l; i++) {
            IndexLocation IndexLocation = selectedIndexes[i].getIndexLocation();
            knownPaths.add(IndexLocation);
        }
        if (this.indexStates != null) {
            Object[] keys = this.indexStates.keyTable;
            IndexLocation[] locations = new IndexLocation[this.indexStates.elementSize];
            int count = 0;
            for (int i = 0, l = keys.length; i < l; i++) {
                IndexLocation key = (IndexLocation) keys[i];
                if (key != null && !knownPaths.includes(key))
                    locations[count++] = key;
            }
            if (count > 0)
                removeIndexesState(locations);
        }
        deleteIndexFiles(knownPaths);
    }

    /**
 * Compute the pre-built index location for a specified URL
 */
    public synchronized IndexLocation computeIndexLocation(IPath containerPath, final URL newIndexURL) {
        IndexLocation indexLocation = (IndexLocation) this.indexLocations.get(containerPath);
        if (indexLocation == null) {
            if (newIndexURL != null) {
                indexLocation = IndexLocation.createIndexLocation(newIndexURL);
                // update caches
                indexLocation = (IndexLocation) getIndexStates().getKey(indexLocation);
                this.indexLocations.put(containerPath, indexLocation);
            }
        } else {
            // an existing index location exists - make sure it has not changed (i.e. the URL has not changed)
            URL existingURL = indexLocation.getUrl();
            if (newIndexURL != null) {
                // if either URL is different then the index location has been updated so rebuild.
                if (!newIndexURL.equals(existingURL)) {
                    // URL has changed so remove the old index and create a new one
                    this.removeIndex(containerPath);
                    // create a new one
                    indexLocation = IndexLocation.createIndexLocation(newIndexURL);
                    // update caches
                    indexLocation = (IndexLocation) getIndexStates().getKey(indexLocation);
                    this.indexLocations.put(containerPath, indexLocation);
                }
            }
        }
        return indexLocation;
    }

    public synchronized IndexLocation computeIndexLocation(IPath containerPath) {
        IndexLocation indexLocation = (IndexLocation) this.indexLocations.get(containerPath);
        if (indexLocation == null) {
            String pathString = containerPath.toOSString();
            CRC32 checksumCalculator = new CRC32();
            checksumCalculator.update(pathString.getBytes());
            //$NON-NLS-1$
            String fileName = Long.toString(checksumCalculator.getValue()) + ".index";
            if (VERBOSE)
                //$NON-NLS-1$ //$NON-NLS-2$
                Util.verbose("-> index name for " + pathString + " is " + fileName);
            // to share the indexLocation between the indexLocations and indexStates tables, get the key from the indexStates table
            indexLocation = (IndexLocation) getIndexStates().getKey(new FileIndexLocation(new File(getSavedIndexesDirectory(), fileName)));
            this.indexLocations.put(containerPath, indexLocation);
        }
        return indexLocation;
    }

    public void deleteIndexFiles() {
        if (DEBUG)
            //$NON-NLS-1$
            Util.verbose("Deleting index files");
        // forget saved indexes & delete each index file
        this.savedIndexNamesFile.delete();
        deleteIndexFiles(null);
    }

    private void deleteIndexFiles(SimpleSet pathsToKeep) {
        File[] indexesFiles = getSavedIndexesDirectory().listFiles();
        if (indexesFiles == null)
            return;
        for (int i = 0, l = indexesFiles.length; i < l; i++) {
            String fileName = indexesFiles[i].getAbsolutePath();
            if (pathsToKeep != null && pathsToKeep.includes(new FileIndexLocation(indexesFiles[i])))
                continue;
            //$NON-NLS-1$
            String suffix = ".index";
            if (fileName.regionMatches(true, fileName.length() - suffix.length(), suffix, 0, suffix.length())) {
                if (VERBOSE || DEBUG)
                    //$NON-NLS-1$
                    Util.verbose(//$NON-NLS-1$
                    "Deleting index file " + indexesFiles[i]);
                indexesFiles[i].delete();
            }
        }
    }

    /*
 * Creates an empty index at the given location, for the given container path, if none exist.
 */
    public synchronized void ensureIndexExists(IndexLocation indexLocation, IPath containerPath) {
        SimpleLookupTable states = getIndexStates();
        Object state = states.get(indexLocation);
        if (state == null) {
            updateIndexState(indexLocation, REBUILDING_STATE);
            getIndex(containerPath, indexLocation, true, true);
        }
    }

    public SourceElementParser getSourceElementParser(IJavaProject project, ISourceElementRequestor requestor) {
        // disable task tags to speed up parsing
        Map options = project.getOptions(true);
        //$NON-NLS-1$
        options.put(JavaCore.COMPILER_TASK_TAGS, "");
        SourceElementParser parser = new IndexingParser(requestor, new DefaultProblemFactory(Locale.getDefault()), new CompilerOptions(options), // index local declarations
        true, // optimize string literals
        true, // do not use source javadoc parser to speed up parsing
        false);
        parser.reportOnlyOneSyntaxError = true;
        // Always check javadoc while indexing
        parser.javadocParser.checkDocComment = true;
        parser.javadocParser.reportProblems = false;
        return parser;
    }

    /**
 * Returns the index for a given index location
 *
 * @param indexLocation The path of the index file
 * @return The corresponding index or <code>null</code> if not found
 */
    public synchronized Index getIndex(IndexLocation indexLocation) {
        // is null if unknown, call if the containerPath must be computed
        return (Index) this.indexes.get(indexLocation);
    }

    /**
 * Returns the index for a given project, according to the following algorithm:
 * - if index is already in memory: answers this one back
 * - if (reuseExistingFile) then read it and return this index and record it in memory
 * - if (createIfMissing) then create a new empty index and record it in memory
 *
 * Warning: Does not check whether index is consistent (not being used)
 */
    public synchronized Index getIndex(IPath containerPath, boolean reuseExistingFile, boolean createIfMissing) {
        IndexLocation indexLocation = computeIndexLocation(containerPath);
        return getIndex(containerPath, indexLocation, reuseExistingFile, createIfMissing);
    }

    /**
 * Returns the index for a given project, according to the following algorithm:
 * - if index is already in memory: answers this one back
 * - if (reuseExistingFile) then read it and return this index and record it in memory
 * - if (createIfMissing) then create a new empty index and record it in memory
 *
 * Warning: Does not check whether index is consistent (not being used)
 */
    public synchronized Index getIndex(IPath containerPath, IndexLocation indexLocation, boolean reuseExistingFile, boolean createIfMissing) {
        // Path is already canonical per construction
        Index index = getIndex(indexLocation);
        if (index == null) {
            Object state = getIndexStates().get(indexLocation);
            Integer currentIndexState = state == null ? UNKNOWN_STATE : (Integer) state;
            if (currentIndexState == UNKNOWN_STATE) {
                // should only be reachable for query jobs
                // IF you put an index in the cache, then AddJarFileToIndex fails because it thinks there is nothing to do
                rebuildIndex(indexLocation, containerPath);
                return null;
            }
            // index isn't cached, consider reusing an existing index file
            String containerPathString = containerPath.getDevice() == null ? containerPath.toString() : containerPath.toOSString();
            if (reuseExistingFile) {
                if (// check before creating index so as to avoid creating a new empty index if file is missing
                indexLocation.exists()) {
                    try {
                        index = new Index(indexLocation, containerPathString, /*reuse index file*/
                        true);
                        this.indexes.put(indexLocation, index);
                        return index;
                    } catch (IOException e) {
                        if (currentIndexState != REBUILDING_STATE && currentIndexState != REUSE_STATE) {
                            if (VERBOSE)
                                Util.verbose("-> cannot reuse existing index: " + indexLocation + " path: " + containerPathString);
                            rebuildIndex(indexLocation, containerPath);
                            return null;
                        }
                    }
                }
                if (// rebuild index if existing file is missing
                currentIndexState == SAVED_STATE) {
                    rebuildIndex(indexLocation, containerPath);
                    return null;
                }
                if (currentIndexState == REUSE_STATE) {
                    // supposed to be in reuse state but error in the index file, so reindex.
                    if (VERBOSE)
                        //$NON-NLS-1$ //$NON-NLS-2$
                        Util.verbose("-> cannot reuse given index: " + indexLocation + " path: " + containerPathString);
                    if (!IS_MANAGING_PRODUCT_INDEXES_PROPERTY) {
                        this.indexLocations.put(containerPath, null);
                        indexLocation = computeIndexLocation(containerPath);
                        rebuildIndex(indexLocation, containerPath);
                    } else {
                        rebuildIndex(indexLocation, containerPath, true);
                    }
                    return null;
                }
            }
            // index wasn't found on disk, consider creating an empty new one
            if (createIfMissing) {
                try {
                    if (VERBOSE)
                        //$NON-NLS-1$ //$NON-NLS-2$
                        Util.verbose("-> create empty index: " + indexLocation + " path: " + containerPathString);
                    index = new Index(indexLocation, containerPathString, /*do not reuse index file*/
                    false);
                    this.indexes.put(indexLocation, index);
                    return index;
                } catch (IOException e) {
                    if (VERBOSE)
                        Util.verbose("-> unable to create empty index: " + indexLocation + " path: " + containerPathString);
                    return null;
                }
            }
        }
        //System.out.println(" index name: " + path.toOSString() + " <----> " + index.getIndexFile().getName());
        return index;
    }

    /**
 * Returns all the existing indexes for a list of index locations.
 * Note that this may trigger some indexes recreation work
 *
 * @param locations The list of of the index files path
 * @return The corresponding indexes list.
 */
    public Index[] getIndexes(IndexLocation[] locations, IProgressMonitor progressMonitor) {
        // acquire the in-memory indexes on the fly
        int length = locations.length;
        Index[] locatedIndexes = new Index[length];
        int count = 0;
        if (this.javaLikeNamesChanged) {
            this.javaLikeNamesChanged = hasJavaLikeNamesChanged();
        }
        for (int i = 0; i < length; i++) {
            if (progressMonitor != null && progressMonitor.isCanceled()) {
                throw new OperationCanceledException();
            }
            // may trigger some index recreation work
            IndexLocation indexLocation = locations[i];
            Index index = getIndex(indexLocation);
            if (index == null) {
                // only need containerPath if the index must be built
                IPath containerPath = (IPath) this.indexLocations.keyForValue(indexLocation);
                if (// sanity check
                containerPath != null) {
                    index = getIndex(containerPath, indexLocation, /*reuse index file*/
                    true, /*do not create if none*/
                    false);
                    if (index != null && this.javaLikeNamesChanged && !index.isIndexForJar()) {
                        // When a change in java like names extension has been detected, all
                        // non jar files indexes (i.e. containing sources) need to be rebuilt.
                        // see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=286379
                        File indexFile = index.getIndexFile();
                        if (indexFile.exists()) {
                            if (DEBUG)
                                Util.verbose(//$NON-NLS-1$
                                "Change in javaLikeNames - removing index file for " + //$NON-NLS-1$
                                containerPath);
                            indexFile.delete();
                        }
                        this.indexes.put(indexLocation, null);
                        rebuildIndex(indexLocation, containerPath);
                        index = null;
                    }
                } else {
                    if (// the index belongs to non-jdt search participant
                    indexLocation.isParticipantIndex() && indexLocation.exists()) {
                        try {
                            IPath container = getParticipantsContainer(indexLocation);
                            if (container != null) {
                                index = new Index(indexLocation, container.toOSString(), true);
                                this.indexes.put(indexLocation, index);
                            }
                        } catch (IOException e) {
                        }
                    }
                }
            }
            if (index != null)
                // only consider indexes which are ready
                locatedIndexes[count++] = index;
        }
        if (this.javaLikeNamesChanged) {
            writeJavaLikeNamesFile();
            this.javaLikeNamesChanged = false;
        }
        if (count < length) {
            System.arraycopy(locatedIndexes, 0, locatedIndexes = new Index[count], 0, count);
        }
        return locatedIndexes;
    }

    public synchronized Index getIndexForUpdate(IPath containerPath, boolean reuseExistingFile, boolean createIfMissing) {
        IndexLocation indexLocation = computeIndexLocation(containerPath);
        if (getIndexStates().get(indexLocation) == REBUILDING_STATE)
            return getIndex(containerPath, indexLocation, reuseExistingFile, createIfMissing);
        // abort the job since the index has been removed from the REBUILDING_STATE
        return null;
    }

    private SimpleLookupTable getIndexStates() {
        if (this.indexStates != null)
            return this.indexStates;
        this.indexStates = new SimpleLookupTable();
        File indexesDirectoryPath = getSavedIndexesDirectory();
        char[][] savedNames = readIndexState(getJavaPluginWorkingLocation().toOSString());
        if (savedNames != null) {
            for (// first name is saved signature, see readIndexState()
            int i = 1, l = savedNames.length; // first name is saved signature, see readIndexState()
            i < l; // first name is saved signature, see readIndexState()
            i++) {
                char[] savedName = savedNames[i];
                if (savedName.length > 0) {
                    // shares indexesDirectoryPath's segments
                    IndexLocation indexLocation = new FileIndexLocation(new File(indexesDirectoryPath, String.valueOf(savedName)));
                    if (VERBOSE)
                        Util.verbose(//$NON-NLS-1$
                        "Reading saved index file " + //$NON-NLS-1$
                        indexLocation);
                    this.indexStates.put(indexLocation, SAVED_STATE);
                }
            }
        } else {
            // All the index files are getting deleted and hence there is no need to 
            // further check for change in javaLikeNames. 
            writeJavaLikeNamesFile();
            this.javaLikeNamesChanged = false;
            deleteIndexFiles();
        }
        readIndexMap();
        return this.indexStates;
    }

    private IPath getParticipantsContainer(IndexLocation indexLocation) {
        if (this.participantsContainers == null) {
            readParticipantsIndexNamesFile();
        }
        return (IPath) this.participantsContainers.get(indexLocation);
    }

    private IPath getJavaPluginWorkingLocation() {
        if (this.javaPluginLocation != null)
            return this.javaPluginLocation;
        IPath stateLocation = JavaCore.getPlugin().getStateLocation();
        return this.javaPluginLocation = stateLocation;
    }

    private File getSavedIndexesDirectory() {
        return new File(getJavaPluginWorkingLocation().toOSString());
    }

    /*
 * see https://bugs.eclipse.org/bugs/show_bug.cgi?id=286379
 * Returns true if there is a change in javaLikeNames since it
 * has been last stored. 
 * The javaLikeNames stored in the file javaLikeNames.txt 
 * is compared with the current javaLikeNames and if there is a change, this 
 * function returns true. If the file javaLikeNames.txt doesn't exist and there 
 * is only one javaLikeName (.java), then this returns false so that no-reindexing 
 * happens. 
 */
    private boolean hasJavaLikeNamesChanged() {
        char[][] currentNames = Util.getJavaLikeExtensions();
        int current = currentNames.length;
        char[][] prevNames = readJavaLikeNamesFile();
        if (prevNames == null) {
            if (VERBOSE && current != 1)
                //$NON-NLS-1$
                Util.verbose("No Java like names found and there is atleast one non-default javaLikeName", System.err);
            //Ignore if only java
            return (current != 1);
        }
        int prev = prevNames.length;
        if (current != prev) {
            if (VERBOSE)
                //$NON-NLS-1$
                Util.verbose("Java like names have changed", System.err);
            return true;
        }
        if (current > 1) {
            // Sort the current java like names. 
            // Copy the array to avoid modifying the Util static variable
            System.arraycopy(currentNames, 0, currentNames = new char[current][], 0, current);
            Util.sort(currentNames);
        }
        // hence just do a direct compare.
        for (int i = 0; i < current; i++) {
            if (!CharOperation.equals(currentNames[i], prevNames[i])) {
                if (VERBOSE)
                    //$NON-NLS-1$
                    Util.verbose(//$NON-NLS-1$
                    "Java like names have changed", //$NON-NLS-1$
                    System.err);
                return true;
            }
        }
        return false;
    }

    public void indexDocument(SearchDocument searchDocument, SearchParticipant searchParticipant, Index index, IPath indexLocation) {
        try {
            searchDocument.setIndex(index);
            searchParticipant.indexDocument(searchDocument, indexLocation);
        } finally {
            searchDocument.setIndex(null);
        }
    }

    public void indexResolvedDocument(SearchDocument searchDocument, SearchParticipant searchParticipant, Index index, IPath indexLocation) {
        searchParticipant.resolveDocument(searchDocument);
        ReadWriteMonitor monitor = index.monitor;
        if (monitor == null)
            // index got deleted since acquired
            return;
        try {
            // ask permission to write
            monitor.enterWrite();
            searchDocument.setIndex(index);
            searchParticipant.indexResolvedDocument(searchDocument, indexLocation);
        } finally {
            searchDocument.setIndex(null);
            monitor.exitWrite();
        }
    }

    /**
 * Trigger addition of the entire content of a project
 * Note: the actual operation is performed in background
 */
    public void indexAll(IProject project) {
        if (JavaCore.getPlugin() == null)
            return;
        // determine the new children
        try {
            JavaModel model = JavaModelManager.getJavaModelManager().getJavaModel();
            JavaProject javaProject = (JavaProject) model.getJavaProject(project);
            // only consider immediate libraries - each project will do the same
            // NOTE: force to resolve CP variables before calling indexer - 19303, so that initializers
            // will be run in the current thread.
            IClasspathEntry[] entries = javaProject.getResolvedClasspath();
            for (int i = 0; i < entries.length; i++) {
                IClasspathEntry entry = entries[i];
                if (entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY)
                    indexLibrary(entry.getPath(), project, ((ClasspathEntry) entry).getLibraryIndexLocation());
            }
        } catch (JavaModelException // cannot retrieve classpath info
        e) {
        }
        // check if the same request is not already in the queue
        IndexRequest request = new IndexAllProject(project, this);
        if (!isJobWaiting(request))
            request(request);
    }

    public void indexLibrary(IPath path, IProject requestingProject, URL indexURL) {
        this.indexLibrary(path, requestingProject, indexURL, false);
    }

    /**
 * Trigger addition of a library to an index
 * Note: the actual operation is performed in background
 */
    public void indexLibrary(IPath path, IProject requestingProject, URL indexURL, final boolean updateIndex) {
        // requestingProject is no longer used to cancel jobs but leave it here just in case
        IndexLocation indexFile = null;
        boolean forceIndexUpdate = false;
        if (indexURL != null) {
            if (IS_MANAGING_PRODUCT_INDEXES_PROPERTY) {
                indexFile = computeIndexLocation(path, indexURL);
                if (!updateIndex && !indexFile.exists()) {
                    forceIndexUpdate = true;
                } else {
                    forceIndexUpdate = updateIndex;
                }
            } else {
                indexFile = IndexLocation.createIndexLocation(indexURL);
            }
        }
        if (JavaCore.getPlugin() == null)
            return;
        IndexRequest request = null;
        Object target = JavaModel.getTarget(path, true);
        if (target instanceof IFile) {
            request = new AddJarFileToIndex((IFile) target, indexFile, this, forceIndexUpdate);
        } else if (target instanceof File) {
            request = new AddJarFileToIndex(path, indexFile, this, forceIndexUpdate);
        } else if (target instanceof IContainer) {
            request = new IndexBinaryFolder((IContainer) target, this);
        } else {
            return;
        }
        // check if the same request is not already in the queue
        if (!isJobWaiting(request))
            request(request);
    }

    synchronized boolean addIndex(IPath containerPath, IndexLocation indexFile) {
        getIndexStates().put(indexFile, REUSE_STATE);
        this.indexLocations.put(containerPath, indexFile);
        Index index = getIndex(containerPath, indexFile, true, false);
        if (index == null) {
            indexFile.close();
            this.indexLocations.put(containerPath, null);
            return false;
        }
        writeIndexMapFile();
        return true;
    }

    /**
 * Index the content of the given source folder.
 */
    public void indexSourceFolder(JavaProject javaProject, IPath sourceFolder, char[][] inclusionPatterns, char[][] exclusionPatterns) {
        IProject project = javaProject.getProject();
        if (this.jobEnd > this.jobStart) {
            // skip it if a job to index the project is already in the queue
            IndexRequest request = new IndexAllProject(project, this);
            if (isJobWaiting(request))
                return;
        }
        request(new AddFolderToIndex(sourceFolder, project, inclusionPatterns, exclusionPatterns, this));
    }

    public synchronized void jobWasCancelled(IPath containerPath) {
        IndexLocation indexLocation = computeIndexLocation(containerPath);
        Index index = getIndex(indexLocation);
        if (index != null) {
            index.monitor = null;
            this.indexes.removeKey(indexLocation);
        }
        updateIndexState(indexLocation, UNKNOWN_STATE);
    }

    /**
 * Advance to the next available job, once the current one has been completed.
 * Note: clients awaiting until the job count is zero are still waiting at this point.
 */
    protected synchronized void moveToNextJob() {
        // remember that one job was executed, and we will need to save indexes at some point
        this.needToSave = true;
        super.moveToNextJob();
    }

    /**
 * No more job awaiting.
 */
    protected void notifyIdle(long idlingTime) {
        if (idlingTime > 1000 && this.needToSave)
            saveIndexes();
    }

    /**
 * Name of the background process
 */
    public String processName() {
        return Messages.process_name;
    }

    private char[][] readJavaLikeNamesFile() {
        try {
            String pathName = getJavaPluginWorkingLocation().toOSString();
            //$NON-NLS-1$
            File javaLikeNamesFile = new File(pathName, "javaLikeNames.txt");
            if (!javaLikeNamesFile.exists())
                return null;
            char[] javaLikeNames = org.eclipse.jdt.internal.compiler.util.Util.getFileCharContent(javaLikeNamesFile, null);
            if (javaLikeNames.length > 0) {
                char[][] names = CharOperation.splitOn('\n', javaLikeNames);
                return names;
            }
        } catch (IOException ignored) {
            if (VERBOSE)
                Util.verbose("Failed to read javaLikeNames file");
        }
        return null;
    }

    private void rebuildIndex(IndexLocation indexLocation, IPath containerPath) {
        rebuildIndex(indexLocation, containerPath, false);
    }

    private void rebuildIndex(IndexLocation indexLocation, IPath containerPath, final boolean updateIndex) {
        Object target = JavaModel.getTarget(containerPath, true);
        if (target == null)
            return;
        if (VERBOSE)
            //$NON-NLS-1$ //$NON-NLS-2$
            Util.verbose("-> request to rebuild index: " + indexLocation + " path: " + containerPath);
        updateIndexState(indexLocation, REBUILDING_STATE);
        IndexRequest request = null;
        if (target instanceof IProject) {
            IProject p = (IProject) target;
            if (JavaProject.hasJavaNature(p))
                request = new IndexAllProject(p, this);
        } else if (target instanceof IFolder) {
            request = new IndexBinaryFolder((IFolder) target, this);
        } else if (target instanceof IFile) {
            request = new AddJarFileToIndex((IFile) target, null, this, updateIndex);
        } else if (target instanceof File) {
            request = new AddJarFileToIndex(containerPath, null, this, updateIndex);
        }
        if (request != null)
            request(request);
    }

    /**
 * Recreates the index for a given path, keeping the same read-write monitor.
 * Returns the new empty index or null if it didn't exist before.
 * Warning: Does not check whether index is consistent (not being used)
 */
    public synchronized Index recreateIndex(IPath containerPath) {
        // only called to over write an existing cached index...
        String containerPathString = containerPath.getDevice() == null ? containerPath.toString() : containerPath.toOSString();
        try {
            // Path is already canonical
            IndexLocation indexLocation = computeIndexLocation(containerPath);
            Index index = getIndex(indexLocation);
            ReadWriteMonitor monitor = index == null ? null : index.monitor;
            if (VERBOSE)
                //$NON-NLS-1$ //$NON-NLS-2$
                Util.verbose("-> recreating index: " + indexLocation + " for path: " + containerPathString);
            index = new Index(indexLocation, containerPathString, /*do not reuse index file*/
            false);
            this.indexes.put(indexLocation, index);
            index.monitor = monitor;
            return index;
        } catch (IOException e) {
            if (VERBOSE) {
                Util.verbose("-> failed to recreate index for path: " + containerPathString);
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
 * Trigger removal of a resource to an index
 * Note: the actual operation is performed in background
 */
    public void remove(String containerRelativePath, IPath indexedContainer) {
        request(new RemoveFromIndex(containerRelativePath, indexedContainer, this));
    }

    /**
 * Removes the index for a given path.
 * This is a no-op if the index did not exist.
 */
    public synchronized void removeIndex(IPath containerPath) {
        if (VERBOSE || DEBUG)
            //$NON-NLS-1$
            Util.verbose("removing index " + containerPath);
        IndexLocation indexLocation = computeIndexLocation(containerPath);
        Index index = getIndex(indexLocation);
        File indexFile = null;
        if (index != null) {
            index.monitor = null;
            indexFile = index.getIndexFile();
        }
        if (indexFile == null)
            // index is not cached yet, but still want to delete the file
            indexFile = indexLocation.getIndexFile();
        if (this.indexStates.get(indexLocation) == REUSE_STATE) {
            indexLocation.close();
            this.indexLocations.put(containerPath, null);
        } else if (indexFile != null && indexFile.exists()) {
            if (DEBUG)
                //$NON-NLS-1$
                Util.verbose("removing index file " + indexFile);
            indexFile.delete();
        }
        this.indexes.removeKey(indexLocation);
        if (IS_MANAGING_PRODUCT_INDEXES_PROPERTY) {
            this.indexLocations.removeKey(containerPath);
        }
        updateIndexState(indexLocation, null);
    }

    /**
 * Removes all indexes whose paths start with (or are equal to) the given path.
 */
    public synchronized void removeIndexPath(IPath path) {
        if (VERBOSE || DEBUG)
            //$NON-NLS-1$
            Util.verbose("removing index path " + path);
        Object[] keyTable = this.indexes.keyTable;
        Object[] valueTable = this.indexes.valueTable;
        IndexLocation[] locations = null;
        int max = this.indexes.elementSize;
        int count = 0;
        for (int i = 0, l = keyTable.length; i < l; i++) {
            IndexLocation indexLocation = (IndexLocation) keyTable[i];
            if (indexLocation == null)
                continue;
            if (indexLocation.startsWith(path)) {
                Index index = (Index) valueTable[i];
                index.monitor = null;
                if (locations == null)
                    locations = new IndexLocation[max];
                locations[count++] = indexLocation;
                if (this.indexStates.get(indexLocation) == REUSE_STATE) {
                    indexLocation.close();
                } else {
                    if (DEBUG)
                        Util.verbose(//$NON-NLS-1$
                        "removing index file " + //$NON-NLS-1$
                        indexLocation);
                    indexLocation.delete();
                }
            } else {
                max--;
            }
        }
        if (locations != null) {
            for (int i = 0; i < count; i++) this.indexes.removeKey(locations[i]);
            removeIndexesState(locations);
            if (this.participantsContainers != null) {
                boolean update = false;
                for (int i = 0; i < count; i++) {
                    if (this.participantsContainers.get(locations[i]) != null) {
                        update = true;
                        this.participantsContainers.removeKey(locations[i]);
                    }
                }
                if (update)
                    writeParticipantsIndexNamesFile();
            }
        }
    }

    /**
 * Removes all indexes whose paths start with (or are equal to) the given path.
 */
    public synchronized void removeIndexFamily(IPath path) {
        // only finds cached index files... shutdown removes all non-cached index files
        ArrayList toRemove = null;
        Object[] containerPaths = this.indexLocations.keyTable;
        for (int i = 0, length = containerPaths.length; i < length; i++) {
            IPath containerPath = (IPath) containerPaths[i];
            if (containerPath == null)
                continue;
            if (path.isPrefixOf(containerPath)) {
                if (toRemove == null)
                    toRemove = new ArrayList();
                toRemove.add(containerPath);
            }
        }
        if (toRemove != null)
            for (int i = 0, length = toRemove.size(); i < length; i++) removeIndex((IPath) toRemove.get(i));
    }

    /**
 * Remove the content of the given source folder from the index.
 */
    public void removeSourceFolderFromIndex(JavaProject javaProject, IPath sourceFolder, char[][] inclusionPatterns, char[][] exclusionPatterns) {
        IProject project = javaProject.getProject();
        if (this.jobEnd > this.jobStart) {
            // skip it if a job to index the project is already in the queue
            IndexRequest request = new IndexAllProject(project, this);
            if (isJobWaiting(request))
                return;
        }
        request(new RemoveFolderFromIndex(sourceFolder, inclusionPatterns, exclusionPatterns, project, this));
    }

    /**
 * Flush current state
 */
    public synchronized void reset() {
        super.reset();
        if (this.indexes != null) {
            this.indexes = new SimpleLookupTable();
            this.indexStates = null;
        }
        this.indexLocations = new SimpleLookupTable();
        this.javaPluginLocation = null;
    }

    /**
 * Resets the index for a given path.
 * Returns true if the index was reset, false otherwise.
 */
    public synchronized boolean resetIndex(IPath containerPath) {
        // only called to over write an existing cached index...
        String containerPathString = containerPath.getDevice() == null ? containerPath.toString() : containerPath.toOSString();
        try {
            // Path is already canonical
            IndexLocation indexLocation = computeIndexLocation(containerPath);
            Index index = getIndex(indexLocation);
            if (VERBOSE) {
                //$NON-NLS-1$ //$NON-NLS-2$
                Util.verbose("-> reseting index: " + indexLocation + " for path: " + containerPathString);
            }
            if (index == null) {
                // the index does not exist, try to recreate it
                return recreateIndex(containerPath) != null;
            }
            index.reset();
            return true;
        } catch (IOException e) {
            if (VERBOSE) {
                Util.verbose("-> failed to reset index for path: " + containerPathString);
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
 * {@link #saveIndex(Index)} will only update the state if there are no other jobs running against the same
 * underlying resource for this index.  Pre-built indexes must be in a {@link #REUSE_STATE} state even if
 * there is another job to run against it as the subsequent job will find the index and not save it in the
 * right state.
 * Refer to https://bugs.eclipse.org/bugs/show_bug.cgi?id=405932
 */
    public void savePreBuiltIndex(Index index) throws IOException {
        if (index.hasChanged()) {
            if (VERBOSE)
                //$NON-NLS-1$
                Util.verbose("-> saving pre-build index " + index.getIndexLocation());
            index.save();
        }
        synchronized (this) {
            updateIndexState(index.getIndexLocation(), REUSE_STATE);
        }
    }

    public void saveIndex(Index index) throws IOException {
        // must have permission to write from the write monitor
        if (index.hasChanged()) {
            if (VERBOSE)
                //$NON-NLS-1$
                Util.verbose("-> saving index " + index.getIndexLocation());
            index.save();
        }
        synchronized (this) {
            IPath containerPath = new Path(index.containerPath);
            if (this.jobEnd > this.jobStart) {
                for (// skip the current job
                int i = this.jobEnd; // skip the current job
                i > this.jobStart; // skip the current job
                i--) {
                    IJob job = this.awaitingJobs[i];
                    if (job instanceof IndexRequest)
                        if (((IndexRequest) job).containerPath.equals(containerPath))
                            return;
                }
            }
            IndexLocation indexLocation = computeIndexLocation(containerPath);
            updateIndexState(indexLocation, SAVED_STATE);
        }
    }

    /**
 * Commit all index memory changes to disk
 */
    public void saveIndexes() {
        // only save cached indexes... the rest were not modified
        ArrayList toSave = new ArrayList();
        synchronized (this) {
            Object[] valueTable = this.indexes.valueTable;
            for (int i = 0, l = valueTable.length; i < l; i++) {
                Index index = (Index) valueTable[i];
                if (index != null)
                    toSave.add(index);
            }
        }
        boolean allSaved = true;
        for (int i = 0, length = toSave.size(); i < length; i++) {
            Index index = (Index) toSave.get(i);
            ReadWriteMonitor monitor = index.monitor;
            // index got deleted since acquired
            if (monitor == null)
                continue;
            try {
                // take read lock before checking if index has changed
                // don't take write lock yet since it can cause a deadlock (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=50571)
                monitor.enterRead();
                if (index.hasChanged()) {
                    if (monitor.exitReadEnterWrite()) {
                        try {
                            saveIndex(index);
                        } catch (IOException e) {
                            if (VERBOSE) {
                                Util.verbose("-> got the following exception while saving:", System.err);
                                e.printStackTrace();
                            }
                            allSaved = false;
                        } finally {
                            monitor.exitWriteEnterRead();
                        }
                    } else {
                        allSaved = false;
                    }
                }
            } finally {
                monitor.exitRead();
            }
        }
        if (this.participantsContainers != null && this.participantUpdated) {
            writeParticipantsIndexNamesFile();
            this.participantUpdated = false;
        }
        this.needToSave = !allSaved;
    }

    public void scheduleDocumentIndexing(final SearchDocument searchDocument, IPath container, final IndexLocation indexLocation, final SearchParticipant searchParticipant) {
        request(new IndexRequest(container, this) {

            public boolean execute(IProgressMonitor progressMonitor) {
                if (this.isCancelled || progressMonitor != null && progressMonitor.isCanceled())
                    return true;
                /* ensure no concurrent write access to index */
                Index index = getIndex(this.containerPath, indexLocation, /*reuse index file*/
                true, /*create if none*/
                true);
                if (index == null)
                    return true;
                ReadWriteMonitor monitor = index.monitor;
                // index got deleted since acquired
                if (monitor == null)
                    return true;
                final Path indexPath = new Path(indexLocation.getCanonicalFilePath());
                try {
                    // ask permission to write
                    monitor.enterWrite();
                    indexDocument(searchDocument, searchParticipant, index, indexPath);
                } finally {
                    // free write lock
                    monitor.exitWrite();
                }
                if (searchDocument.shouldIndexResolvedDocument()) {
                    indexResolvedDocument(searchDocument, searchParticipant, index, indexPath);
                }
                return true;
            }

            public String toString() {
                //$NON-NLS-1$
                return "indexing " + searchDocument.getPath();
            }
        });
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(10);
        buffer.append(super.toString());
        //$NON-NLS-1$
        buffer.append("In-memory indexes:\n");
        int count = 0;
        Object[] valueTable = this.indexes.valueTable;
        for (int i = 0, l = valueTable.length; i < l; i++) {
            Index index = (Index) valueTable[i];
            if (index != null)
                //$NON-NLS-1$
                buffer.append(++count).append(" - ").append(index.toString()).append('\n');
        }
        return buffer.toString();
    }

    private void readIndexMap() {
        try {
            char[] indexMaps = org.eclipse.jdt.internal.compiler.util.Util.getFileCharContent(this.indexNamesMapFile, null);
            char[][] names = CharOperation.splitOn('\n', indexMaps);
            if (names.length >= 3) {
                // First line is DiskIndex signature (see writeIndexMapFile())
                String savedSignature = DiskIndex.SIGNATURE;
                if (savedSignature.equals(new String(names[0]))) {
                    for (int i = 1, l = names.length - 1; i < l; i += 2) {
                        IndexLocation indexPath = IndexLocation.createIndexLocation(new URL(new String(names[i])));
                        if (indexPath == null)
                            continue;
                        this.indexLocations.put(new Path(new String(names[i + 1])), indexPath);
                        this.indexStates.put(indexPath, REUSE_STATE);
                    }
                }
            }
        } catch (IOException ignored) {
            if (VERBOSE)
                Util.verbose("Failed to read saved index file names");
        }
        return;
    }

    private char[][] readIndexState(String dirOSString) {
        try {
            char[] savedIndexNames = org.eclipse.jdt.internal.compiler.util.Util.getFileCharContent(this.savedIndexNamesFile, null);
            if (savedIndexNames.length > 0) {
                char[][] names = CharOperation.splitOn('\n', savedIndexNames);
                if (names.length > 1) {
                    // First line is DiskIndex signature + saved plugin working location (see writeSavedIndexNamesFile())
                    String savedSignature = //$NON-NLS-1$
                    DiskIndex.SIGNATURE + "+" + //$NON-NLS-1$
                    dirOSString;
                    if (savedSignature.equals(new String(names[0])))
                        return names;
                }
            }
        } catch (IOException ignored) {
            if (VERBOSE)
                Util.verbose("Failed to read saved index file names");
        }
        return null;
    }

    private void readParticipantsIndexNamesFile() {
        SimpleLookupTable containers = new SimpleLookupTable(3);
        try {
            char[] participantIndexNames = org.eclipse.jdt.internal.compiler.util.Util.getFileCharContent(this.participantIndexNamesFile, null);
            if (participantIndexNames.length > 0) {
                char[][] names = CharOperation.splitOn('\n', participantIndexNames);
                if (names.length >= 3) {
                    // First line is DiskIndex signature  (see writeParticipantsIndexNamesFile())
                    if (DiskIndex.SIGNATURE.equals(new String(names[0]))) {
                        for (int i = 1, l = names.length - 1; i < l; i += 2) {
                            IndexLocation indexLocation = new FileIndexLocation(new File(new String(names[i])), true);
                            containers.put(indexLocation, new Path(new String(names[i + 1])));
                        }
                    }
                }
            }
        } catch (IOException ignored) {
            if (VERBOSE)
                Util.verbose("Failed to read participant index file names");
        }
        this.participantsContainers = containers;
        return;
    }

    private synchronized void removeIndexesState(IndexLocation[] locations) {
        // ensure the states are initialized
        getIndexStates();
        int length = locations.length;
        boolean changed = false;
        for (int i = 0; i < length; i++) {
            if (locations[i] == null)
                continue;
            if ((this.indexStates.removeKey(locations[i]) != null)) {
                changed = true;
                if (VERBOSE) {
                    //$NON-NLS-1$
                    Util.verbose(//$NON-NLS-1$
                    "-> index state updated to: ? for: " + locations[i]);
                }
            }
        }
        if (!changed)
            return;
        writeSavedIndexNamesFile();
        writeIndexMapFile();
    }

    private synchronized void updateIndexState(IndexLocation indexLocation, Integer indexState) {
        if (indexLocation == null)
            throw new IllegalArgumentException();
        // ensure the states are initialized
        getIndexStates();
        if (indexState != null) {
            // not changed
            if (indexState.equals(this.indexStates.get(indexLocation)))
                return;
            this.indexStates.put(indexLocation, indexState);
        } else {
            // did not exist anyway
            if (!this.indexStates.containsKey(indexLocation))
                return;
            this.indexStates.removeKey(indexLocation);
        }
        writeSavedIndexNamesFile();
        if (VERBOSE) {
            if (indexState == null) {
                //$NON-NLS-1$
                Util.verbose("-> index state removed for: " + indexLocation);
            } else {
                //$NON-NLS-1$
                String state = "?";
                if (//$NON-NLS-1$
                indexState == SAVED_STATE)
                    //$NON-NLS-1$
                    state = "SAVED";
                else if (//$NON-NLS-1$
                indexState == UPDATING_STATE)
                    //$NON-NLS-1$
                    state = "UPDATING";
                else if (//$NON-NLS-1$
                indexState == UNKNOWN_STATE)
                    //$NON-NLS-1$
                    state = "UNKNOWN";
                else if (//$NON-NLS-1$
                indexState == REBUILDING_STATE)
                    //$NON-NLS-1$
                    state = "REBUILDING";
                else //$NON-NLS-1$
                if (indexState == REUSE_STATE)
                    state = "REUSE";
                //$NON-NLS-1$ //$NON-NLS-2$
                Util.verbose("-> index state updated to: " + state + " for: " + indexLocation);
            }
        }
    }

    public void updateParticipant(IPath indexPath, IPath containerPath) {
        if (this.participantsContainers == null) {
            readParticipantsIndexNamesFile();
        }
        IndexLocation indexLocation = new FileIndexLocation(indexPath.toFile(), true);
        if (this.participantsContainers.get(indexLocation) == null) {
            this.participantsContainers.put(indexLocation, containerPath);
            this.participantUpdated = true;
        }
    }

    private void writeJavaLikeNamesFile() {
        BufferedWriter writer = null;
        String pathName = getJavaPluginWorkingLocation().toOSString();
        try {
            char[][] currentNames = Util.getJavaLikeExtensions();
            int length = currentNames.length;
            if (length > 1) {
                // Sort the current java like names. 
                // Copy the array to avoid modifying the Util static variable
                System.arraycopy(currentNames, 0, currentNames = new char[length][], 0, length);
                Util.sort(currentNames);
            }
            //$NON-NLS-1$
            File javaLikeNamesFile = new File(pathName, "javaLikeNames.txt");
            writer = new BufferedWriter(new FileWriter(javaLikeNamesFile));
            for (int i = 0; i < length - 1; i++) {
                writer.write(currentNames[i]);
                writer.write('\n');
            }
            if (length > 0)
                writer.write(currentNames[length - 1]);
        } catch (IOException ignored) {
            if (VERBOSE)
                Util.verbose("Failed to write javaLikeNames file", System.err);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void writeIndexMapFile() {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(this.indexNamesMapFile));
            writer.write(DiskIndex.SIGNATURE);
            writer.write('\n');
            Object[] keys = this.indexStates.keyTable;
            Object[] states = this.indexStates.valueTable;
            for (int i = 0, l = states.length; i < l; i++) {
                IndexLocation location = (IndexLocation) keys[i];
                if (location != null && states[i] == REUSE_STATE) {
                    IPath container = (IPath) this.indexLocations.keyForValue(location);
                    if (container != null) {
                        writer.write(location.toString());
                        writer.write('\n');
                        writer.write(container.toOSString());
                        writer.write('\n');
                    }
                }
            }
        } catch (IOException ignored) {
            if (VERBOSE)
                Util.verbose("Failed to write saved index file names", System.err);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void writeParticipantsIndexNamesFile() {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(this.participantIndexNamesFile));
            writer.write(DiskIndex.SIGNATURE);
            writer.write('\n');
            Object[] indexFiles = this.participantsContainers.keyTable;
            Object[] containers = this.participantsContainers.valueTable;
            for (int i = 0, l = indexFiles.length; i < l; i++) {
                IndexLocation indexFile = (IndexLocation) indexFiles[i];
                if (indexFile != null) {
                    writer.write(indexFile.getIndexFile().getPath());
                    writer.write('\n');
                    writer.write(((IPath) containers[i]).toOSString());
                    writer.write('\n');
                }
            }
        } catch (IOException ignored) {
            if (VERBOSE)
                Util.verbose("Failed to write participant index file names", System.err);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void writeSavedIndexNamesFile() {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(this.savedIndexNamesFile));
            writer.write(DiskIndex.SIGNATURE);
            writer.write('+');
            writer.write(getJavaPluginWorkingLocation().toOSString());
            writer.write('\n');
            Object[] keys = this.indexStates.keyTable;
            Object[] states = this.indexStates.valueTable;
            for (int i = 0, l = states.length; i < l; i++) {
                IndexLocation key = (IndexLocation) keys[i];
                if (key != null && states[i] == SAVED_STATE) {
                    writer.write(key.fileName());
                    writer.write('\n');
                }
            }
        } catch (IOException ignored) {
            if (VERBOSE)
                Util.verbose("Failed to write saved index file names", System.err);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
