/*******************************************************************************
 * Copyright (c) 2008, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jesper S Moller - Bug 421938: [1.8] ExecutionEnvironmentDescription#getVMArguments does not preserve VM arguments
 *******************************************************************************/
package org.eclipse.jdt.launching.environments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.launching.EEVMType;
import org.eclipse.jdt.internal.launching.LaunchingMessages;
import org.eclipse.jdt.internal.launching.LaunchingPlugin;
import org.eclipse.jdt.internal.launching.StandardVMType;
import org.eclipse.jdt.launching.LibraryLocation;
import org.eclipse.osgi.util.NLS;

/**
 * Helper class to parse and retrieve properties from execution environment description
 * files. An execution environment description file can be used to define attributes relevant
 * the launching of a specific JRE configuration. The format of the file is defined by
 * code>http://wiki.eclipse.org/Execution_Environment_Descriptions</code>.
 * 
 * @since 3.5
 */
public final class ExecutionEnvironmentDescription {

    /**
	 * Endorsed directories property name in an execution environment description file.
	 */
    //$NON-NLS-1$
    public static final String ENDORSED_DIRS = "-Dee.endorsed.dirs";

    /**
	 * Boot class path property name in an execution environment description file.
	 */
    //$NON-NLS-1$
    public static final String BOOT_CLASS_PATH = "-Dee.bootclasspath";

    /**
	 * Source archive property name in an execution environment description file.
	 * Value is a path. When present, the source attachment for each library in the boot
	 * class path will be the file specified by this property.
	 */
    //$NON-NLS-1$
    public static final String SOURCE_DEFAULT = "-Dee.src";

    /**
	 * Source map property name in an execution environment description file.
	 * <p>
	 * Maps class libraries to source attachments. Value is one or more entries of the form
	 * <code>libPath=sourcePath</code> separated by platform specific file separator. The paths
	 * can use <code>{$ee.home}</code> and <code>'..'</code> as well as the wild card characters
	 * '<code>?</code>" (any one character) and '<code>*</code>' (any number of characters).
	 * The <code>sourcePath</code> can use the wild card characters to have the source path be based on the
	 * wild card replacement in the <code>libPath</code>. In this case the wild card characters in the
	 * <code>sourcePath</code> must exist in the same order as the <code>libPath</code>.
	 * For example, <code>lib/foo*.???=source/src*foo.???</code>.
	 * </p> 
	 */
    //$NON-NLS-1$
    public static final String SOURCE_MAP = "-Dee.src.map";

    /**
	 * Javadoc location property name in an execution environment description file.
	 * <p>
	 * Specifies javadoc location for class libraries. Must be a URL. You can use
	 * <code>${ee.home}</code> and <code>'..'</code> segments to specify a file location
	 * relative to the ee file. If this property is not specified in the file,
	 * javadoc locations will be set to a default location based on the language level.
	 * </p>
	 */
    //$NON-NLS-1$
    public static final String JAVADOC_LOC = "-Dee.javadoc";

    /**
	 * Pre-built index location property in an execution environment description file.
	 * <p>
	 * Specifies the location for a pre-built search index. Must be a valid {@link URL}.
	 * 
	 * You can use <code>${ee.home}</code> and <code>'..'</code> segments to specify a file location
	 * relative to the ee file.
	 * 
	 * If this property is not specified the default value of <code>null</code> will be used.
	 * </p>
	 * @since 3.7
	 */
    //$NON-NLS-1$
    public static final String INDEX_LOC = "-Dee.index";

    /**
	 * Additional directories property name in an execution environment description file.
	 */
    //$NON-NLS-1$
    public static final String ADDITIONAL_DIRS = "-Dee.additional.dirs";

    /**
	 * Extension directories property name in an execution environment description file.
	 */
    //$NON-NLS-1$
    public static final String EXTENSION_DIRS = "-Dee.ext.dirs";

    /**
	 * Language level property name in an execution environment description file.
	 * For example, 1.4 or 1.5.
	 */
    //$NON-NLS-1$
    public static final String LANGUAGE_LEVEL = "-Dee.language.level";

    /**
	 * OSGi profile property name in an execution environment description file.
	 * <p>
	 * The value is the identifier of an OSGi profile, such as <code>J2SE-1.4</code>.
	 * </p>
	 */
    //$NON-NLS-1$
    public static final String CLASS_LIB_LEVEL = "-Dee.class.library.level";

    /**
	 * Executable property name in an execution environment description file.
	 * For example, <code>javaw.exe</code>.
	 */
    //$NON-NLS-1$
    public static final String EXECUTABLE = "-Dee.executable";

    /**
	 * Console executable property name in an execution environment description file.
	 * For example, <code>java.exe</code>.
	 */
    //$NON-NLS-1$
    public static final String EXECUTABLE_CONSOLE = "-Dee.executable.console";

    /**
	 * Java home property name in an execution environment description file.
	 * <p>
	 * The root install directory of the runtime environment or development kit. Corresponds to a value
	 * that could be used for <code>JAVA_HOME</code> environment variable
	 * </p>
	 */
    //$NON-NLS-1$
    public static final String JAVA_HOME = "-Djava.home";

    /**
	 * Debug arguments property name in an execution environment description file.
	 * <p>
	 * The arguments to use to launch the VM in debug mode. For example 
	 * <code>"-agentlib:jdwp=transport=dt_socket,suspend=y,address=localhost:${port}"</code>.
	 * The <code>${port}</code> variable will be substituted with a free port at launch time.
	 * When unspecified, default arguments are constructed based on the language level of the VM. 
	 * </p>
	 */
    //$NON-NLS-1$
    public static final String DEBUG_ARGS = "-Dee.debug.args";

    /**
	 * VM name property name in an execution environment description file.
	 * <p>
	 * The name is used as the JRE name when installing an EE JRE into Eclipse.
	 * </p>
	 */
    //$NON-NLS-1$	
    public static final String EE_NAME = "-Dee.name";

    /**
	 * The directory containing the execution environment description file. Relative paths are resolved
	 * relative to this location. This property will be set if not present, it does not need to be
	 * specified in the file.
	 */
    //$NON-NLS-1$
    public static final String EE_HOME = "-Dee.home";

    /**
	 * Substitution in EE file - replaced with directory of EE file,
	 * to support absolute path names where needed. If the value is not in the
	 * file, it is set when properties are created.
	 */
    //$NON-NLS-1$	
    private static final String VAR_EE_HOME = "${ee.home}";

    /**
	 * Any line found in the description starting with this string will not be added to the
	 * VM argument list
	 */
    //$NON-NLS-1$	
    private static final String EE_ARG_FILTER = "-Dee.";

    // Regex constants for handling the source mapping
    private static final Character WILDCARD_SINGLE_CHAR = new Character('?');

    private static final Character WILDCARD_MULTI_CHAR = new Character('*');

    private static final String REGEX_SPECIAL_CHARS = "+()^$.{}[]|\\";

    private Map<String, String> fProperties = null;

    public  ExecutionEnvironmentDescription(File eeFile) throws CoreException {
        initProperties(eeFile);
    }

    public Map<String, String> getProperties() {
        return fProperties;
    }

    public String getProperty(String property) {
        return fProperties.get(property);
    }

    public LibraryLocation[] getLibraryLocations() {
        List<LibraryLocation> allLibs = new ArrayList<LibraryLocation>();
        String dirs = getProperty(ENDORSED_DIRS);
        if (dirs != null) {
            allLibs.addAll(StandardVMType.gatherAllLibraries(resolvePaths(dirs)));
        }
        dirs = getProperty(BOOT_CLASS_PATH);
        if (dirs != null) {
            String[] bootpath = resolvePaths(dirs);
            List<LibraryLocation> boot = new ArrayList<LibraryLocation>(bootpath.length);
            IPath src = getSourceLocation();
            URL url = getJavadocLocation();
            URL indexurl = getIndexLocation();
            for (int i = 0; i < bootpath.length; i++) {
                IPath path = new Path(bootpath[i]);
                File lib = path.toFile();
                if (lib.exists() && lib.isFile()) {
                    LibraryLocation libraryLocation = new LibraryLocation(path, src, Path.EMPTY, url, indexurl);
                    boot.add(libraryLocation);
                }
            }
            allLibs.addAll(boot);
        }
        dirs = getProperty(ADDITIONAL_DIRS);
        if (dirs != null) {
            allLibs.addAll(StandardVMType.gatherAllLibraries(resolvePaths(dirs)));
        }
        dirs = getProperty(EXTENSION_DIRS);
        if (dirs != null) {
            allLibs.addAll(StandardVMType.gatherAllLibraries(resolvePaths(dirs)));
        }
        HashSet<String> set = new HashSet<String>();
        LibraryLocation lib = null;
        for (ListIterator<LibraryLocation> liter = allLibs.listIterator(); liter.hasNext(); ) {
            lib = liter.next();
            if (!set.add(lib.getSystemLibraryPath().toOSString())) {
                liter.remove();
            }
        }
        addSourceLocationsToLibraries(getSourceMap(), allLibs);
        return allLibs.toArray(new LibraryLocation[allLibs.size()]);
    }

    public String getVMArguments() {
        StringBuffer arguments = new StringBuffer();
        Iterator<Entry<String, String>> entries = fProperties.entrySet().iterator();
        while (entries.hasNext()) {
            Entry<String, String> entry = entries.next();
            String key = entry.getKey();
            String value = entry.getValue();
            boolean appendArgument = !key.startsWith(EE_ARG_FILTER);
            if (appendArgument) {
                arguments.append(key);
                if (!value.equals("")) {
                    arguments.append('=');
                    value = resolveHome(value);
                    if (value.indexOf(' ') > -1) {
                        arguments.append('"').append(value).append('"');
                    } else {
                        arguments.append(value);
                    }
                }
                arguments.append(' ');
            }
        }
        if (arguments.charAt(arguments.length() - 1) == ' ') {
            arguments.deleteCharAt(arguments.length() - 1);
        }
        return arguments.toString();
    }

    public File getExecutable() {
        String property = getProperty(ExecutionEnvironmentDescription.EXECUTABLE);
        if (property != null) {
            String[] paths = resolvePaths(property);
            if (paths.length == 1) {
                return new File(paths[0]);
            }
        }
        return null;
    }

    public File getConsoleExecutable() {
        String property = getProperty(ExecutionEnvironmentDescription.EXECUTABLE_CONSOLE);
        if (property != null) {
            String[] paths = resolvePaths(property);
            if (paths.length == 1) {
                return new File(paths[0]);
            }
        }
        return null;
    }

    private void initProperties(File eeFile) throws CoreException {
        Map<String, String> properties = new LinkedHashMap<String, String>();
        String eeHome = eeFile.getParentFile().getAbsolutePath();
        try (FileReader reader = new FileReader(eeFile);
            BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line = bufferedReader.readLine();
            while (line != null) {
                if (!line.startsWith("#")) {
                    if (line.trim().length() > 0) {
                        int eq = line.indexOf('=');
                        if (eq > 0) {
                            String key = line.substring(0, eq);
                            String value = null;
                            if (line.length() > eq + 1) {
                                value = line.substring(eq + 1).trim();
                            }
                            properties.put(key, value);
                        } else {
                            properties.put(line, "");
                        }
                    }
                }
                line = bufferedReader.readLine();
            }
        } catch (FileNotFoundException e) {
            throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.ID_PLUGIN, NLS.bind(LaunchingMessages.ExecutionEnvironmentDescription_0, new String[] { eeFile.getPath() }), e));
        } catch (IOException e) {
            throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.ID_PLUGIN, NLS.bind(LaunchingMessages.ExecutionEnvironmentDescription_1, new String[] { eeFile.getPath() }), e));
        }
        if (!properties.containsKey(EE_HOME)) {
            properties.put(EE_HOME, eeHome);
        }
        fProperties = properties;
        Iterator<Entry<String, String>> entries = properties.entrySet().iterator();
        Map<String, String> resolved = new LinkedHashMap<String, String>(properties.size());
        while (entries.hasNext()) {
            Entry<String, String> entry = entries.next();
            String key = entry.getKey();
            String value = entry.getValue();
            if (value != null) {
                value = resolveHome(value);
                resolved.put(key, value);
            } else {
                resolved.put(key, "");
            }
        }
        fProperties = resolved;
    }

    private String resolveHome(String value) {
        int start = 0;
        int index = value.indexOf(VAR_EE_HOME, start);
        StringBuffer replaced = null;
        String eeHome = getProperty(EE_HOME);
        while (index >= 0) {
            if (replaced == null) {
                replaced = new StringBuffer();
            }
            replaced.append(value.substring(start, index));
            replaced.append(eeHome);
            start = index + VAR_EE_HOME.length();
            index = value.indexOf(VAR_EE_HOME, start);
        }
        if (replaced != null) {
            replaced.append(value.substring(start));
            return replaced.toString();
        }
        return value;
    }

    /**
	 * Returns all path strings contained in the given string based on system
	 * path delimiter, resolved relative to the <code>${ee.home}</code> property.
	 * 
	 * @param paths the paths to resolve
	 * @return array of individual paths
	 */
    private String[] resolvePaths(String paths) {
        String[] strings = paths.split(File.pathSeparator, -1);
        String eeHome = getProperty(EE_HOME);
        IPath root = new Path(eeHome);
        for (int i = 0; i < strings.length; i++) {
            strings[i] = makePathAbsolute(strings[i], root);
        }
        return strings;
    }

    /**
	 * Returns a string representing the absolute form of the given path.  If the
	 * given path is not absolute, it is appended to the given root path.  The returned
	 * path will always be the OS specific string form of the path.
	 * 
	 * @param pathString string representing the path to make absolute
	 * @param root root to append non-absolute paths to
	 * @return absolute, OS specific path
	 */
    private String makePathAbsolute(String pathString, IPath root) {
        IPath path = new Path(pathString.trim());
        if (!path.isEmpty() && !path.isAbsolute()) {
            IPath filePath = root.append(path);
            return filePath.toOSString();
        }
        return path.toOSString();
    }

    /**
	 * Creates a map (regex string to regex string) mapping library locations to their
	 * source locations.  This is done by taking the ee.src.map property from the ee file
	 * which allows a list of mappings that can use the wildcards ? (any one char) and *
	 * (any series of chars).  The property is converted to a map of regex strings used by 
	 * {@link #addSourceLocationsToLibraries(Map, List)}.
	 * <pre>
	 * Example property, separated onto separate lines for easier reading
	 * -Dee.src.map=${ee.home}\lib\charconv?.zip=lib\charconv?-src.zip;
	 *              ${ee.home}\lib\jclDEE\classes.zip=lib\jclDEE\source\source.zip;
	 *              ${ee.home}\lib\jclDEE\*.zip=lib\jclDEE\source\*-src.zip;
	 *              ${ee.home}\lib\jclDEE\ext\*.???=lib\jclDEE\source\*-src.???;
	 * </pre>
	 * 
	 * 
	 * @return map containing regexs mapping library locations to their source locations
	 */
    private Map<String, String> getSourceMap() {
        String srcMapString = getProperty(SOURCE_MAP);
        Map<String, String> srcMap = new HashMap<String, String>();
        if (srcMapString != null) {
            // Entries must be separated by the file separator and have an equals splitting the lib location from the src location
            String[] entries = srcMapString.split(File.pathSeparator);
            for (int i = 0; i < entries.length; i++) {
                int index = entries[i].indexOf('=');
                if (index > 0 && index < entries[i].length() - 1) {
                    IPath root = new Path(getProperty(EE_HOME));
                    String key = entries[i].substring(0, index);
                    String value = entries[i].substring(index + 1);
                    key = makePathAbsolute(key, root);
                    value = makePathAbsolute(value, root);
                    List<Character> wildcards = new ArrayList<Character>();
                    StringBuffer keyBuffer = new StringBuffer();
                    char[] chars = key.toCharArray();
                    // Convert lib location to a regex, replace wildcards with grouped equivalents, keep track of used wildcards, allow '\' and '/' to be used, escape special chars
                    for (int j = 0; j < chars.length; j++) {
                        if (chars[j] == WILDCARD_MULTI_CHAR.charValue()) {
                            wildcards.add(WILDCARD_MULTI_CHAR);
                            //$NON-NLS-1$
                            keyBuffer.append("(.*)");
                        } else if (chars[j] == WILDCARD_SINGLE_CHAR.charValue()) {
                            wildcards.add(WILDCARD_SINGLE_CHAR);
                            //$NON-NLS-1$
                            keyBuffer.append("(.)");
                        } else if (REGEX_SPECIAL_CHARS.indexOf(chars[j]) != -1) {
                            keyBuffer.append('\\').append(chars[j]);
                        } else {
                            keyBuffer.append(chars[j]);
                        }
                    }
                    int currentWild = 0;
                    StringBuffer valueBuffer = new StringBuffer();
                    chars = value.toCharArray();
                    for (int j = 0; j < chars.length; j++) {
                        if (chars[j] == WILDCARD_MULTI_CHAR.charValue() || chars[j] == WILDCARD_SINGLE_CHAR.charValue()) {
                            if (currentWild < wildcards.size()) {
                                Character wild = wildcards.get(currentWild);
                                if (chars[j] == wild.charValue()) {
                                    valueBuffer.append('$').append(currentWild + 1);
                                    currentWild++;
                                } else {
                                    LaunchingPlugin.log(NLS.bind(LaunchingMessages.EEVMType_5, new String[] { entries[i] }));
                                    break;
                                }
                            } else {
                                LaunchingPlugin.log(NLS.bind(LaunchingMessages.EEVMType_5, new String[] { entries[i] }));
                                break;
                            }
                        } else if (REGEX_SPECIAL_CHARS.indexOf(chars[j]) != -1) {
                            valueBuffer.append('\\').append(chars[j]);
                        } else {
                            valueBuffer.append(chars[j]);
                        }
                    }
                    srcMap.put(keyBuffer.toString(), valueBuffer.toString());
                } else {
                    LaunchingPlugin.log(NLS.bind(LaunchingMessages.EEVMType_6, new String[] { entries[i] }));
                }
            }
        }
        return srcMap;
    }

    private void addSourceLocationsToLibraries(Map<String, String> srcMap, List<LibraryLocation> libraries) {
        for (Iterator<String> patternIterator = srcMap.keySet().iterator(); patternIterator.hasNext(); ) {
            String currentKey = patternIterator.next();
            Pattern currentPattern = Pattern.compile(currentKey);
            Matcher matcher = currentPattern.matcher("");
            for (Iterator<LibraryLocation> locationIterator = libraries.iterator(); locationIterator.hasNext(); ) {
                LibraryLocation currentLibrary = locationIterator.next();
                matcher.reset(currentLibrary.getSystemLibraryPath().toOSString());
                if (matcher.find()) {
                    String sourceLocation = matcher.replaceAll(srcMap.get(currentKey));
                    IPath sourcePath = new Path(sourceLocation);
                    if (sourcePath.toFile().exists()) {
                        currentLibrary.setSystemLibrarySource(sourcePath);
                    }
                }
            }
        }
    }

    private IPath getSourceLocation() {
        String src = getProperty(ExecutionEnvironmentDescription.SOURCE_DEFAULT);
        if (src != null) {
            String eeHome = getProperty(ExecutionEnvironmentDescription.EE_HOME);
            src = makePathAbsolute(src, new Path(eeHome));
            return new Path(src);
        }
        return Path.EMPTY;
    }

    private URL getJavadocLocation() {
        return EEVMType.getJavadocLocation(fProperties);
    }

    private URL getIndexLocation() {
        return EEVMType.getIndexLocation(fProperties);
    }
}
