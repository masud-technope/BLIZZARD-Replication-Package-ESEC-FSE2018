/*******************************************************************************
 * Copyright (c) 2005, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.launching.launcher;

import java.io.*;
import java.net.URL;
import java.util.*;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.*;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.core.target.ITargetPlatformService;
import org.eclipse.pde.internal.build.IPDEBuildConstants;
import org.eclipse.pde.internal.core.*;
import org.eclipse.pde.internal.launching.ILaunchingPreferenceConstants;
import org.eclipse.pde.internal.launching.PDELaunchingPlugin;
import org.eclipse.pde.launching.IPDELauncherConstants;
import org.osgi.framework.Bundle;

public class LaunchArgumentsHelper {

    /**
	 * Returns the location that will be used as the workspace when launching or
	 * an empty string if the user has specified the <code>-data &#64none</code>
	 * argument for no workspace.  Will replace variables, so this method should
	 * only be called when variable substitution (may prompt the user) is appropriate.
	 *
	 * @param configuration the launch configuration to get the workspace value for
	 * @return workspace location path as a string or an empty if no workspace will be used
	 * @throws CoreException if there is a problem with the configuration
	 */
    public static String getWorkspaceLocation(ILaunchConfiguration configuration) throws CoreException {
        // Check if -data @none is specified
        String[] userArgs = getUserProgramArgumentArray(configuration);
        for (int i = 0; i < userArgs.length; i++) {
            if (//$NON-NLS-1$ //$NON-NLS-2$
            userArgs[i].equals("-data") && (i + 1) < userArgs.length && userArgs[i + 1].equals("@none")) {
                //$NON-NLS-1$
                return "";
            }
        }
        String location = configuration.getAttribute(IPDELauncherConstants.LOCATION, (String) null);
        if (location == null) {
            // backward compatibility
            //$NON-NLS-1$
            location = configuration.getAttribute(IPDELauncherConstants.LOCATION + "0", (String) null);
            if (location != null) {
                ILaunchConfigurationWorkingCopy wc = null;
                if (configuration.isWorkingCopy()) {
                    wc = (ILaunchConfigurationWorkingCopy) configuration;
                } else {
                    wc = configuration.getWorkingCopy();
                }
                //$NON-NLS-1$
                wc.setAttribute(//$NON-NLS-1$
                IPDELauncherConstants.LOCATION + "0", //$NON-NLS-1$
                (String) null);
                wc.setAttribute(IPDELauncherConstants.LOCATION, location);
                wc.doSave();
            }
        }
        return getSubstitutedString(location);
    }

    public static String[] getUserProgramArgumentArray(ILaunchConfiguration configuration) throws CoreException {
        String args = getUserProgramArguments(configuration);
        //$NON-NLS-1$
        return new ExecutionArguments("", args).getProgramArgumentsArray();
    }

    public static String getUserProgramArguments(ILaunchConfiguration configuration) throws CoreException {
        String args = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, (String) null);
        if (args == null) {
            // backward compatibility
            //$NON-NLS-1$
            args = configuration.getAttribute("progargs", (String) null);
            if (args != null) {
                ILaunchConfigurationWorkingCopy wc = null;
                if (configuration.isWorkingCopy()) {
                    wc = (ILaunchConfigurationWorkingCopy) configuration;
                } else {
                    wc = configuration.getWorkingCopy();
                }
                //$NON-NLS-1$
                wc.setAttribute(//$NON-NLS-1$
                "progargs", //$NON-NLS-1$
                (String) null);
                wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, args);
                wc.doSave();
            }
        }
        //$NON-NLS-1$
        return args == null ? "" : getSubstitutedString(args);
    }

    public static String getUserVMArguments(ILaunchConfiguration configuration) throws CoreException {
        String args = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, (String) null);
        if (args == null) {
            // backward compatibility
            //$NON-NLS-1$
            args = configuration.getAttribute("vmargs", (String) null);
            if (args != null) {
                ILaunchConfigurationWorkingCopy wc = null;
                if (configuration.isWorkingCopy()) {
                    wc = (ILaunchConfigurationWorkingCopy) configuration;
                } else {
                    wc = configuration.getWorkingCopy();
                }
                //$NON-NLS-1$
                wc.setAttribute(//$NON-NLS-1$
                "vmargs", //$NON-NLS-1$
                (String) null);
                wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, args);
                wc.doSave();
            }
        }
        //$NON-NLS-1$
        return args == null ? "" : getSubstitutedString(args);
    }

    /**
	 * Fetches the VM Arguments from the current Target Platform
	 *
	 * @return	VM Arguments from the current Target Platform or empty string if none found
	 */
    public static String getInitialVMArguments() {
        try {
            ITargetPlatformService service = (ITargetPlatformService) PDECore.getDefault().acquireService(ITargetPlatformService.class.getName());
            if (service != null) {
                String result = service.getWorkspaceTargetDefinition().getVMArguments();
                result = //$NON-NLS-1$
                result != null ? //$NON-NLS-1$
                result : //$NON-NLS-1$
                "";
                return result;
            }
        } catch (CoreException e) {
            PDECore.log(e);
        }
        //$NON-NLS-1$
        return "";
    }

    public static String getInitialProgramArguments() {
        //$NON-NLS-1$
        StringBuffer buffer = new StringBuffer("-os ${target.os} -ws ${target.ws} -arch ${target.arch} -nl ${target.nl} -consoleLog");
        try {
            ITargetPlatformService service = (ITargetPlatformService) PDECore.getDefault().acquireService(ITargetPlatformService.class.getName());
            if (service != null) {
                String result = service.getWorkspaceTargetDefinition().getProgramArguments();
                if (result != null) {
                    buffer.append(' ').append(result);
                }
            }
        } catch (CoreException e) {
            PDECore.log(e);
        }
        return buffer.toString();
    }

    public static File getWorkingDirectory(ILaunchConfiguration configuration) throws CoreException {
        String working;
        try {
            //$NON-NLS-1$
            working = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, new File(".").getCanonicalPath());
        } catch (IOException e) {
            working = "${workspace_loc}/../";
        }
        File dir = new File(getSubstitutedString(working));
        if (!dir.exists())
            dir.mkdirs();
        return dir;
    }

    public static Map<String, Object> getVMSpecificAttributesMap(ILaunchConfiguration config) throws CoreException {
        Map<String, Object> map = new HashMap(2);
        String javaCommand = config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_JAVA_COMMAND, (String) null);
        map.put(IJavaLaunchConfigurationConstants.ATTR_JAVA_COMMAND, javaCommand);
        if (//$NON-NLS-1$
        TargetPlatform.getOS().equals("macosx")) {
            //$NON-NLS-1$
            ModelEntry entry = PluginRegistry.findEntry("org.eclipse.jdt.debug");
            if (entry != null) {
                IPluginModelBase[] models = entry.getExternalModels();
                for (IPluginModelBase model : models) {
                    File file = new File(model.getInstallLocation());
                    if (!file.isFile())
                        file = new //$NON-NLS-1$
                        File(//$NON-NLS-1$
                        file, //$NON-NLS-1$
                        "jdi.jar");
                    if (file.exists()) {
                        map.put(IJavaLaunchConfigurationConstants.ATTR_BOOTPATH_PREPEND, new String[] { file.getAbsolutePath() });
                        break;
                    }
                }
            }
        }
        return map;
    }

    public static String getTracingFileArgument(ILaunchConfiguration config, String optionsFileName) {
        try {
            TracingOptionsManager mng = PDECore.getDefault().getTracingOptionsManager();
            Map<String, String> options = config.getAttribute(IPDELauncherConstants.TRACING_OPTIONS, (Map<String, String>) null);
            String selected = config.getAttribute(IPDELauncherConstants.TRACING_CHECKED, (String) null);
            if (selected == null) {
                mng.save(optionsFileName, options);
            } else if (!selected.equals(IPDELauncherConstants.TRACING_NONE)) {
                HashSet<String> result = new HashSet();
                StringTokenizer tokenizer = new //$NON-NLS-1$
                StringTokenizer(//$NON-NLS-1$
                selected, //$NON-NLS-1$
                ",");
                while (tokenizer.hasMoreTokens()) {
                    result.add(tokenizer.nextToken());
                }
                mng.save(optionsFileName, options, result);
            }
        } catch (CoreException e) {
            return "";
        }
        return optionsFileName;
    }

    public static String[] constructClasspath(ILaunchConfiguration configuration) throws CoreException {
        double targetVersion = TargetPlatformHelper.getTargetVersion();
        String jarPath = targetVersion >= 3.3 ? getEquinoxStartupPath(IPDEBuildConstants.BUNDLE_EQUINOX_LAUNCHER) : getStartupJarPath();
        if (jarPath == null && targetVersion < 3.3)
            //$NON-NLS-1$
            jarPath = getEquinoxStartupPath("org.eclipse.core.launcher");
        if (jarPath == null)
            return null;
        ArrayList<String> entries = new ArrayList();
        entries.add(jarPath);
        //$NON-NLS-1$
        String bootstrap = configuration.getAttribute(IPDELauncherConstants.BOOTSTRAP_ENTRIES, "");
        //$NON-NLS-1$
        StringTokenizer tok = new StringTokenizer(getSubstitutedString(bootstrap), ",");
        while (tok.hasMoreTokens()) entries.add(tok.nextToken().trim());
        return entries.toArray(new String[entries.size()]);
    }

    /**
	 * Returns the path to the equinox launcher jar.  If the launcher is available
	 * in the workspace, the packageName will be used to determine the expected output
	 * location.
	 *
	 * @param packageName name of the launcher package, typically {@link IPDEBuildConstants#BUNDLE_EQUINOX_LAUNCHER}
	 * @return the path to the equinox launcher jar or <code>null</code>
	 * @throws CoreException
	 */
    private static String getEquinoxStartupPath(String packageName) throws CoreException {
        // See if PDE has the launcher in the workspace or target
        IPluginModelBase model = PluginRegistry.findModel(IPDEBuildConstants.BUNDLE_EQUINOX_LAUNCHER);
        if (model != null) {
            IResource resource = model.getUnderlyingResource();
            if (resource == null) {
                // Found in the target
                String installLocation = model.getInstallLocation();
                if (installLocation == null) {
                    return null;
                }
                File bundleFile = new File(installLocation);
                if (!bundleFile.isDirectory()) {
                    // The launcher bundle is usually jarred, just return the bundle root
                    return installLocation;
                }
                // Unjarred bundle, search for the built jar at the root of the folder
                File[] files = bundleFile.listFiles(new FilenameFilter() {

                    @Override
                    public boolean accept(File dir, String name) {
                        return name.indexOf(IPDEBuildConstants.BUNDLE_EQUINOX_LAUNCHER) >= 0;
                    }
                });
                for (File file : files) {
                    if (file.isFile()) {
                        return file.getPath();
                    }
                }
                // Source bundle from git://git.eclipse.org/gitroot/equinox/rt.equinox.framework.git
                File binFolder = new //$NON-NLS-1$
                File(//$NON-NLS-1$
                bundleFile, //$NON-NLS-1$
                "bin");
                if (binFolder.isDirectory()) {
                    return binFolder.getPath();
                }
                return null;
            }
            // Found in the workspace
            IProject project = resource.getProject();
            if (project.hasNature(JavaCore.NATURE_ID)) {
                IJavaProject jProject = JavaCore.create(project);
                IClasspathEntry[] entries = jProject.getRawClasspath();
                for (IClasspathEntry entrie : entries) {
                    int kind = entrie.getEntryKind();
                    if (kind == IClasspathEntry.CPE_SOURCE || kind == IClasspathEntry.CPE_LIBRARY) {
                        IPackageFragmentRoot[] roots = jProject.findPackageFragmentRoots(entrie);
                        for (IPackageFragmentRoot root : roots) {
                            if (root.getPackageFragment(packageName).exists()) {
                                // if source folder, find the output folder
                                if (kind == IClasspathEntry.CPE_SOURCE) {
                                    IPath path = entrie.getOutputLocation();
                                    if (path == null)
                                        path = jProject.getOutputLocation();
                                    path = path.removeFirstSegments(1);
                                    return project.getLocation().append(path).toOSString();
                                }
                                // else if is a library jar, then get the location of the jar itself
                                IResource jar = root.getResource();
                                if (jar != null) {
                                    return jar.getLocation().toOSString();
                                }
                            }
                        }
                    }
                }
            }
        }
        // No PDE model, see if the launcher bundle is installed
        Bundle bundle = Platform.getBundle(IPDEBuildConstants.BUNDLE_EQUINOX_LAUNCHER);
        if (bundle != null) {
            try {
                URL url = //$NON-NLS-1$
                FileLocator.resolve(//$NON-NLS-1$
                bundle.getEntry("/"));
                url = FileLocator.toFileURL(url);
                String path = url.getFile();
                if (//$NON-NLS-1$
                path.startsWith(//$NON-NLS-1$
                "file:"))
                    path = path.substring(5);
                path = new File(path).getAbsolutePath();
                if (//$NON-NLS-1$
                path.endsWith(//$NON-NLS-1$
                "!"))
                    path = path.substring(0, path.length() - 1);
                return path;
            } catch (IOException e) {
            }
        }
        return null;
    }

    private static String getStartupJarPath() throws CoreException {
        //$NON-NLS-1$
        IPluginModelBase model = PluginRegistry.findModel("org.eclipse.platform");
        if (model != null && model.getUnderlyingResource() != null) {
            IProject project = model.getUnderlyingResource().getProject();
            if (project.hasNature(JavaCore.NATURE_ID)) {
                IJavaProject jProject = JavaCore.create(project);
                IPackageFragmentRoot[] roots = jProject.getPackageFragmentRoots();
                for (IPackageFragmentRoot root : roots) {
                    if (root.getKind() == IPackageFragmentRoot.K_SOURCE && //$NON-NLS-1$
                    root.getPackageFragment("org.eclipse.core.launcher").exists()) {
                        IPath path = jProject.getOutputLocation().removeFirstSegments(1);
                        return project.getLocation().append(path).toOSString();
                    }
                }
            }
            if (//$NON-NLS-1$
            project.getFile("startup.jar").exists())
                return //$NON-NLS-1$
                project.getFile("startup.jar").getLocation().toOSString();
        }
        //$NON-NLS-1$
        File startupJar = new Path(TargetPlatform.getLocation()).append("startup.jar").toFile();
        // in the running eclipse.
        if (!startupJar.exists())
            //$NON-NLS-1$
            startupJar = new Path(TargetPlatform.getDefaultLocation()).append("startup.jar").toFile();
        return startupJar.exists() ? startupJar.getAbsolutePath() : null;
    }

    private static String getSubstitutedString(String text) throws CoreException {
        if (text == null)
            //$NON-NLS-1$
            return "";
        IStringVariableManager mgr = VariablesPlugin.getDefault().getStringVariableManager();
        return mgr.performStringSubstitution(text);
    }

    public static String getDefaultWorkspaceLocation(String uniqueName) {
        return getDefaultWorkspaceLocation(uniqueName, false);
    }

    public static String getDefaultWorkspaceLocation(String uniqueName, boolean isJUnit) {
        PDEPreferencesManager launchingStore = PDELaunchingPlugin.getDefault().getPreferenceManager();
        String location = launchingStore.getString(isJUnit ? ILaunchingPreferenceConstants.PROP_JUNIT_WORKSPACE_LOCATION : ILaunchingPreferenceConstants.PROP_RUNTIME_WORKSPACE_LOCATION);
        if (launchingStore.getBoolean(isJUnit ? ILaunchingPreferenceConstants.PROP_JUNIT_WORKSPACE_LOCATION_IS_CONTAINER : ILaunchingPreferenceConstants.PROP_RUNTIME_WORKSPACE_LOCATION_IS_CONTAINER)) {
            //$NON-NLS-1$ //$NON-NLS-2$
            return location + uniqueName.replaceAll("\\s", "");
        }
        return location;
    }

    public static boolean getDefaultJUnitWorkspaceIsContainer() {
        PDEPreferencesManager launchingStore = PDELaunchingPlugin.getDefault().getPreferenceManager();
        return launchingStore.getBoolean(ILaunchingPreferenceConstants.PROP_JUNIT_WORKSPACE_LOCATION_IS_CONTAINER);
    }

    public static String getDefaultJUnitConfigurationLocation() {
        //$NON-NLS-1$
        return "${workspace_loc}/.metadata/.plugins/org.eclipse.pde.core/pde-junit";
    }
}
