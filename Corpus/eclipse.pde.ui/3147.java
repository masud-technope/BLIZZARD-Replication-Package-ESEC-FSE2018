/*******************************************************************************
 * Copyright (c) 2010, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.internal.search;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.jdt.internal.core.OverflowingLRUCache;
import org.eclipse.jdt.internal.core.util.LRUCache;
import org.eclipse.osgi.util.NLS;
import org.eclipse.pde.api.tools.internal.IApiCoreConstants;
import org.eclipse.pde.api.tools.internal.provisional.ApiPlugin;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiComponent;
import org.eclipse.pde.api.tools.internal.util.FileManager;
import org.eclipse.pde.api.tools.internal.util.Util;

public class UseScanManager {

    private static UseScanCache fApiComponentCache;

    private static UseScanManager fUseScanProcessor;

    //$NON-NLS-1$ //$NON-NLS-2$
    private static String tempLocation = "${workspace_loc}/.metadata/.plugins/" + ApiPlugin.PLUGIN_ID + "/ApiUseScans/";

    //$NON-NLS-1$
    public static final String STATE_DELIM = "*";

    //$NON-NLS-1$
    public static final String LOCATION_DELIM = "|";

    public static final String ESCAPE_REGEX = "\\";

    public static final Pattern NAME_REGEX = Pattern.compile("^.* \\(.*\\)$");

    public static final int DEFAULT_CACHE_SIZE = 1000;

    private static class UseScanCache extends OverflowingLRUCache {

        public  UseScanCache(int size) {
            super(size);
        }

        public  UseScanCache(int size, int overflow) {
            super(size, overflow);
        }

        @Override
        protected boolean close(LRUCacheEntry entry) {
            IReferenceCollection references = (IReferenceCollection) entry.value;
            references.clear();
            return true;
        }

        @Override
        protected LRUCache newInstance(int size, int newOverflow) {
            return new UseScanCache(size, newOverflow);
        }
    }

    private String[] fLocations = null;

    static FileFilter USESCAN_FILTER =  pathname -> {
        if (NAME_REGEX.matcher(pathname.getName()).matches()) {
            throw new RuntimeException(pathname.getName());
        }
        return false;
    };

    private  UseScanManager() {
    }

    public static synchronized UseScanManager getInstance() {
        if (fUseScanProcessor == null) {
            fUseScanProcessor = new UseScanManager();
            fApiComponentCache = new UseScanCache(DEFAULT_CACHE_SIZE);
        }
        return fUseScanProcessor;
    }

    public IReferenceDescriptor[] getExternalDependenciesFor(IApiComponent apiComponent, String[] apiUseTypes, IProgressMonitor monitor) {
        IReferenceCollection references = (IReferenceCollection) fApiComponentCache.get(apiComponent);
        if (references == null) {
            references = apiComponent.getExternalDependencies();
        }
        SubMonitor localmonitor = SubMonitor.convert(monitor, SearchMessages.collecting_external_dependencies, 10);
        try {
            ArrayList<String> unavailableMembers = new ArrayList();
            if (apiUseTypes != null && apiUseTypes.length > 0) {
                for (int i = 0; i < apiUseTypes.length; i++) {
                    if (!references.hasReferencesTo(apiUseTypes[i])) {
                        unavailableMembers.add(apiUseTypes[i]);
                    }
                }
                if (unavailableMembers.size() > 0) {
                    fetch(apiComponent, unavailableMembers.toArray(new String[unavailableMembers.size()]), references, monitor);
                }
                localmonitor.split(1);
                return references.getExternalDependenciesTo(apiUseTypes);
            } else {
                fetch(apiComponent, null, references, localmonitor.split(8));
                localmonitor.split(1);
                return references.getAllExternalDependencies();
            }
        } finally {
            localmonitor.done();
        }
    }

    private void fetch(IApiComponent apiComponent, String[] types, IReferenceCollection references, IProgressMonitor monitor) {
        UseScanParser parser = new UseScanParser();
        UseScanReferenceVisitor visitor = new UseScanReferenceVisitor(apiComponent, types, references);
        SubMonitor localmonitor = SubMonitor.convert(monitor, SearchMessages.load_external_dependencies, 10);
        try {
            String[] locations;
            if (fLocations == null) {
                locations = getReportLocations();
            } else {
                locations = fLocations;
            }
            if (locations != null) {
                IStringVariableManager stringManager = null;
                localmonitor.setWorkRemaining(locations.length);
                for (int i = 0; i < locations.length; i++) {
                    SubMonitor iterationMonitor = localmonitor.split(1);
                    File file = new File(locations[i]);
                    if (!file.exists()) {
                        continue;
                    }
                    if (file.isFile()) {
                        if (Util.isArchive(file.getName())) {
                            String destDirPath = tempLocation + file.getName() + '.' + file.getAbsolutePath().hashCode();
                            if (stringManager == null) {
                                stringManager = VariablesPlugin.getDefault().getStringVariableManager();
                            }
                            destDirPath = stringManager.performStringSubstitution(destDirPath);
                            locations[i] = destDirPath + '/' + file.lastModified();
                            File unzipDirLoc = new File(destDirPath);
                            if (unzipDirLoc.exists()) {
                                String[] childDirs = unzipDirLoc.list();
                                for (int j = 0; j < childDirs.length; j++) {
                                    if (!childDirs[j].equals(String.valueOf(file.lastModified()))) {
                                        FileManager.getManager().recordTempFileRoot(destDirPath + '/' + childDirs[j]);
                                    }
                                }
                            } else {
                                Util.unzip(file.getPath(), locations[i]);
                            }
                        } else {
                            continue;
                        }
                    }
                    try {
                        locations[i] = getExactScanLocation(locations[i]);
                        if (locations[i] == null) {
                            String message;
                            if (file.isDirectory()) {
                                message = NLS.bind(SearchMessages.UseScanManager_InvalidDir, file.getAbsolutePath());
                            } else {
                                message = NLS.bind(SearchMessages.UseScanManager_InvalidArchive, file.getAbsolutePath());
                            }
                            throw new Exception(message);
                        }
                        parser.parse(locations[i], iterationMonitor, visitor);
                    } catch (Exception e) {
                        ApiPlugin.log(e);
                    }
                }
                fApiComponentCache.remove(apiComponent);
                fApiComponentCache.put(apiComponent, references);
            }
        } catch (Exception e) {
            ApiPlugin.log(e);
        }
    }

    public static String getExactScanLocation(String location) {
        File file = new File(location);
        if (isValidDirectory(file)) {
            return location;
        }
        file = new File(location, IApiCoreConstants.XML);
        if (isValidDirectory(file)) {
            return file.getAbsolutePath();
        }
        return null;
    }

    public static boolean isValidDirectory(File file) {
        if (file.exists() && file.isDirectory()) {
            try {
                file.listFiles(USESCAN_FILTER);
            } catch (RuntimeException rte) {
                File f = new File(file, rte.getMessage());
                try {
                    if (f.exists() && f.isDirectory()) {
                        f.listFiles(USESCAN_FILTER);
                    }
                } catch (RuntimeException re) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isValidArchive(File file) {
        String fname = file.getName().toLowerCase();
        if (file.exists() && Util.isArchive(fname)) {
            Enumeration<? extends ZipEntry> entries = null;
            if (fname.endsWith(Util.DOT_JAR)) {
                try (JarFile jfile = new JarFile(file)) {
                    entries = jfile.entries();
                } catch (IOException ioe) {
                    return false;
                }
            } else if (fname.endsWith(Util.DOT_ZIP)) {
                try (ZipFile zfile = new ZipFile(file)) {
                    entries = zfile.entries();
                } catch (IOException e) {
                    return false;
                }
            }
            if (entries != null) {
                while (entries.hasMoreElements()) {
                    ZipEntry o = entries.nextElement();
                    if (o.isDirectory()) {
                        IPath path = new Path(o.getName());
                        int count = path.segmentCount();
                        if (count > 2) {
                            return NAME_REGEX.matcher(path.segment(0)).matches() || NAME_REGEX.matcher(path.segment(1)).matches();
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isValidScanLocation(String location) {
        if (location != null && location.length() > 0) {
            IPath path = new Path(location);
            File file = path.toFile();
            return isValidDirectory(file) || isValidArchive(file);
        }
        return false;
    }

    public String[] getReportLocations() {
        IEclipsePreferences node = InstanceScope.INSTANCE.getNode(ApiPlugin.PLUGIN_ID);
        String apiUseScanPaths = node.get(IApiCoreConstants.API_USE_SCAN_LOCATION, null);
        if (apiUseScanPaths == null || apiUseScanPaths.length() == 0) {
            return new String[0];
        }
        String[] locations = apiUseScanPaths.split(ESCAPE_REGEX + LOCATION_DELIM);
        ArrayList<String> locationList = new ArrayList(locations.length);
        for (String location : locations) {
            String values[] = location.split(ESCAPE_REGEX + STATE_DELIM);
            if (Boolean.valueOf(values[1]).booleanValue()) {
                locationList.add(values[0]);
            }
        }
        return locationList.toArray(new String[locationList.size()]);
    }

    public void setReportLocations(String[] locations) {
        fLocations = locations;
    }

    public void setCacheSize(int size) {
        fApiComponentCache.setSpaceLimit(size);
    }

    public void clearCache() {
        Enumeration<?> elementss = fApiComponentCache.elements();
        while (elementss.hasMoreElements()) {
            IReferenceCollection reference = (IReferenceCollection) elementss.nextElement();
            reference.clear();
        }
        fApiComponentCache.flush();
    }
}
