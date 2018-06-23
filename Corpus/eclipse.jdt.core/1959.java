/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Tal Lev-Ami - added package cache for zip files
 *     Stephan Herrmann - Contribution for
 *								Bug 440477 - [null] Infrastructure for feeding external annotations into compilation
 *******************************************************************************/
package org.eclipse.jdt.internal.core.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;
import org.eclipse.jdt.internal.compiler.util.SimpleSet;
import org.eclipse.jdt.internal.compiler.util.SuffixConstants;
import org.eclipse.jdt.internal.core.util.Util;
import java.io.*;
import java.util.*;
import java.util.zip.*;

@SuppressWarnings("rawtypes")
public class ClasspathJar extends ClasspathLocation {

    static class PackageCacheEntry {

        long lastModified;

        long fileSize;

        SimpleSet packageSet;

         PackageCacheEntry(long lastModified, long fileSize, SimpleSet packageSet) {
            this.lastModified = lastModified;
            this.fileSize = fileSize;
            this.packageSet = packageSet;
        }
    }

    static SimpleLookupTable PackageCache = new SimpleLookupTable();

    /**
 * Calculate and cache the package list available in the zipFile.
 * @param jar The ClasspathJar to use
 * @return A SimpleSet with the all the package names in the zipFile.
 */
    static SimpleSet findPackageSet(ClasspathJar jar) {
        String zipFileName = jar.zipFilename;
        long lastModified = jar.lastModified();
        long fileSize = new File(zipFileName).length();
        PackageCacheEntry cacheEntry = (PackageCacheEntry) PackageCache.get(zipFileName);
        if (cacheEntry != null && cacheEntry.lastModified == lastModified && cacheEntry.fileSize == fileSize)
            return cacheEntry.packageSet;
        SimpleSet packageSet = new SimpleSet(41);
        //$NON-NLS-1$
        packageSet.add("");
        nextEntry: for (Enumeration e = jar.zipFile.entries(); e.hasMoreElements(); ) {
            String fileName = ((ZipEntry) e.nextElement()).getName();
            // add the package name & all of its parent packages
            int last = fileName.lastIndexOf('/');
            while (last > 0) {
                // extract the package name
                String packageName = fileName.substring(0, last);
                if (packageSet.addIfNotIncluded(packageName) == null)
                    // already existed
                    continue nextEntry;
                last = packageName.lastIndexOf('/');
            }
        }
        PackageCache.put(zipFileName, new PackageCacheEntry(lastModified, fileSize, packageSet));
        return packageSet;
    }

    // keep for equals
    String zipFilename;

    IFile resource;

    ZipFile zipFile;

    ZipFile annotationZipFile;

    long lastModified;

    boolean closeZipFileAtEnd;

    SimpleSet knownPackageNames;

    AccessRuleSet accessRuleSet;

    String externalAnnotationPath;

     ClasspathJar(IFile resource, AccessRuleSet accessRuleSet, IPath externalAnnotationPath) {
        this.resource = resource;
        try {
            java.net.URI location = resource.getLocationURI();
            if (location == null) {
                //$NON-NLS-1$
                this.zipFilename = "";
            } else {
                File localFile = Util.toLocalFile(location, null);
                this.zipFilename = localFile.getPath();
            }
        } catch (CoreException e) {
        }
        this.zipFile = null;
        this.knownPackageNames = null;
        this.accessRuleSet = accessRuleSet;
        if (externalAnnotationPath != null)
            this.externalAnnotationPath = externalAnnotationPath.toString();
    }

     ClasspathJar(String zipFilename, long lastModified, AccessRuleSet accessRuleSet, IPath externalAnnotationPath) {
        this.zipFilename = zipFilename;
        this.lastModified = lastModified;
        this.zipFile = null;
        this.knownPackageNames = null;
        this.accessRuleSet = accessRuleSet;
        if (externalAnnotationPath != null)
            this.externalAnnotationPath = externalAnnotationPath.toString();
    }

    public  ClasspathJar(ZipFile zipFile, AccessRuleSet accessRuleSet, IPath externalAnnotationPath) {
        this.zipFilename = zipFile.getName();
        this.zipFile = zipFile;
        this.closeZipFileAtEnd = false;
        this.knownPackageNames = null;
        this.accessRuleSet = accessRuleSet;
        if (externalAnnotationPath != null)
            this.externalAnnotationPath = externalAnnotationPath.toString();
    }

    public void cleanup() {
        if (this.closeZipFileAtEnd) {
            if (this.zipFile != null) {
                try {
                    this.zipFile.close();
                } catch (IOException // ignore it
                e) {
                }
                this.zipFile = null;
            }
            if (this.annotationZipFile != null) {
                try {
                    this.annotationZipFile.close();
                } catch (IOException // ignore it
                e) {
                }
                this.annotationZipFile = null;
            }
        }
        this.knownPackageNames = null;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ClasspathJar))
            return false;
        ClasspathJar jar = (ClasspathJar) o;
        if (this.accessRuleSet != jar.accessRuleSet)
            if (this.accessRuleSet == null || !this.accessRuleSet.equals(jar.accessRuleSet))
                return false;
        return this.zipFilename.equals(jar.zipFilename) && lastModified() == jar.lastModified();
    }

    public NameEnvironmentAnswer findClass(String binaryFileName, String qualifiedPackageName, String qualifiedBinaryFileName) {
        // most common case
        if (!isPackage(qualifiedPackageName))
            return null;
        try {
            ClassFileReader reader = ClassFileReader.read(this.zipFile, qualifiedBinaryFileName);
            if (reader != null) {
                String fileNameWithoutExtension = qualifiedBinaryFileName.substring(0, qualifiedBinaryFileName.length() - SuffixConstants.SUFFIX_CLASS.length);
                if (this.externalAnnotationPath != null) {
                    try {
                        this.annotationZipFile = reader.setExternalAnnotationProvider(this.externalAnnotationPath, fileNameWithoutExtension, this.annotationZipFile, null);
                    } catch (IOException e) {
                    }
                }
                if (this.accessRuleSet == null)
                    return new NameEnvironmentAnswer(reader, null);
                return new NameEnvironmentAnswer(reader, this.accessRuleSet.getViolatedRestriction(fileNameWithoutExtension.toCharArray()));
            }
        } catch (IOException // treat as if class file is missing
        e) {
        } catch (ClassFormatException // treat as if class file is missing
        e) {
        }
        return null;
    }

    public IPath getProjectRelativePath() {
        if (this.resource == null)
            return null;
        return this.resource.getProjectRelativePath();
    }

    public int hashCode() {
        return this.zipFilename == null ? super.hashCode() : this.zipFilename.hashCode();
    }

    public boolean isPackage(String qualifiedPackageName) {
        if (this.knownPackageNames != null)
            return this.knownPackageNames.includes(qualifiedPackageName);
        try {
            if (this.zipFile == null) {
                if (org.eclipse.jdt.internal.core.JavaModelManager.ZIP_ACCESS_VERBOSE) {
                    //$NON-NLS-1$	//$NON-NLS-2$
                    System.out.println("(" + Thread.currentThread() + ") [ClasspathJar.isPackage(String)] Creating ZipFile on " + this.zipFilename);
                }
                this.zipFile = new ZipFile(this.zipFilename);
                this.closeZipFileAtEnd = true;
            }
            this.knownPackageNames = findPackageSet(this);
        } catch (Exception e) {
            this.knownPackageNames = new SimpleSet();
        }
        return this.knownPackageNames.includes(qualifiedPackageName);
    }

    public long lastModified() {
        if (this.lastModified == 0)
            this.lastModified = new File(this.zipFilename).lastModified();
        return this.lastModified;
    }

    public String toString() {
        //$NON-NLS-1$
        String start = "Classpath jar file " + this.zipFilename;
        if (this.accessRuleSet == null)
            return start;
        //$NON-NLS-1$
        return start + " with " + this.accessRuleSet;
    }

    public String debugPathString() {
        long time = lastModified();
        if (time == 0)
            return this.zipFilename;
        //$NON-NLS-1$
        return this.zipFilename + '(' + (new Date(time)) + " : " + time + ')';
    }
}
