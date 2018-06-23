/*******************************************************************************
 * Copyright (c) 2000, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Les Jones <lesojones@gmail.com> - Bug 214457
 *     Simon Scholz <simon.scholz@vogella.com> - Bug 444808
 *******************************************************************************/
package org.eclipse.pde.internal.core;

import java.util.Locale;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.pde.core.plugin.TargetPlatform;
import org.eclipse.pde.core.project.IBundleProjectDescription;
import org.eclipse.pde.core.target.ITargetPlatformService;
import org.osgi.framework.Constants;

/**
 *	Contains constants for the core PDE models including the target platform.
 */
public interface ICoreConstants {

    // Target Platform
    /**
	 * Preference key that stores the string path to the root location of the
	 * target platform.  Should be accessed through {@link TargetPlatform#getLocation()}.
	 * @deprecated Since the 4.4 Luna release, target platform information is no longer stored in preferences. Instead use {@link ITargetPlatformService}.
	 */
    @Deprecated
    String //$NON-NLS-1$
    PLATFORM_PATH = "platform_path";

    /**
	 * @deprecated Since the 4.4 Luna release, target platform information is no longer stored in preferences. Instead use {@link ITargetPlatformService}.
	 */
    @Deprecated
    String //$NON-NLS-1$
    TARGET_MODE = "target_mode";

    /**
	 * @deprecated Since the 4.4 Luna release, target platform information is no longer stored in preferences. Instead use {@link ITargetPlatformService}.
	 */
    @Deprecated
    String //$NON-NLS-1$
    VALUE_USE_THIS = "useThis";

    /**
	 * @deprecated Since the 4.4 Luna release, target platform information is no longer stored in preferences. Instead use {@link ITargetPlatformService}.
	 */
    @Deprecated
    String //$NON-NLS-1$
    VALUE_USE_OTHER = "useOther";

    /**
	 * @deprecated Since the 4.4 Luna release, target platform information is no longer stored in preferences. Instead use {@link ITargetPlatformService}.
	 */
    @Deprecated
    String //$NON-NLS-1$
    CHECKED_PLUGINS = "checkedPlugins";

    /**
	 * @deprecated Since the 4.4 Luna release, target platform information is no longer stored in preferences. Instead use {@link ITargetPlatformService}.
	 */
    @Deprecated
    String //$NON-NLS-1$
    VALUE_SAVED_NONE = "[savedNone]";

    /**
	 * @deprecated Since the 4.4 Luna release, target platform information is no longer stored in preferences. Instead use {@link ITargetPlatformService}.
	 */
    @Deprecated
    String //$NON-NLS-1$
    VALUE_SAVED_ALL = "[savedAll]";

    /**
	 * @deprecated Since the 4.4 Luna release, target platform information is no longer stored in preferences. Instead use {@link ITargetPlatformService}.
	 */
    @Deprecated
    String //$NON-NLS-1$
    VALUE_SAVED_SOME = "savedSome";

    /**
	 * @since 3.6 - Bug 282708: [target] issues with two versions of the same bundle
	 * @deprecated Since the 4.4 Luna release, target platform information is no longer stored in preferences. Instead use {@link ITargetPlatformService}.
	 */
    @Deprecated
    String //$NON-NLS-1$
    CHECKED_VERSION_PLUGINS = "checkedVersionPlugins";

    /**
	 * @deprecated Since the 4.4 Luna release, target platform information is no longer stored in preferences. Instead use {@link ITargetPlatformService}.
	 */
    @Deprecated
    String //$NON-NLS-1$
    PROGRAM_ARGS = "program_args";

    /**
	 * @deprecated Since the 4.4 Luna release, target platform information is no longer stored in preferences. Instead use {@link ITargetPlatformService}.
	 */
    @Deprecated
    String //$NON-NLS-1$
    VM_ARGS = "vm_args";

    /**
	 * @deprecated Since the 4.4 Luna release, target platform information is no longer stored in preferences. Instead use {@link ITargetPlatformService}.
	 */
    @Deprecated
    String //$NON-NLS-1$
    VM_LAUNCHER_INI = "vm_launcher_ini";

    /**
	 * @deprecated Since the 4.4 Luna release, target platform information is no longer stored in preferences. Instead use {@link ITargetPlatformService}.
	 */
    @Deprecated
    String //$NON-NLS-1$
    IMPLICIT_DEPENDENCIES = "implicit_dependencies";

    /**
	 * @deprecated Since the 4.4 Luna release, target platform information is no longer stored in preferences. Instead use {@link ITargetPlatformService}.
	 */
    @Deprecated
    String //$NON-NLS-1$
    ADDITIONAL_LOCATIONS = "additional_locations";

    /**
	 * @deprecated Since the 4.4 Luna release, target platform information is no longer stored in preferences. Instead use {@link ITargetPlatformService}.
	 */
    @Deprecated
    String //$NON-NLS-1$
    TARGET_PLATFORM_REALIZATION = "target_platform_realization";

    /**
	 * @deprecated This preference was only used during 3.5, it has been replaced in 3.6 with {@link #POOLED_URLS}
	 */
    @Deprecated
    String //$NON-NLS-1$
    POOLED_BUNDLES = "pooled_bundles";

    /**
	 * Comma separated list of bundle URLs used from the bundle pool.
	 * @since 3.6
	 * @deprecated Since the 4.4 Luna release, target platform information is no longer stored in preferences. Instead use {@link ITargetPlatformService}.
	 */
    @Deprecated
    String //$NON-NLS-1$
    POOLED_URLS = "pooled_urls";

    /**
	 * List of feature ids and versions that are available in the target platform.  Features
	 * are comma separated, with each entry taking the form of [id]@[version]
	 *
	 * @since 3.6
	 * @deprecated Since the 4.4 Luna release, target platform information is no longer stored in preferences. Instead use {@link ITargetPlatformService}.
	 */
    @Deprecated
    String //$NON-NLS-1$
    EXTERNAL_FEATURES = "external_features";

    // Target Environment
    /**
	 * @deprecated Since the 4.4 Luna release, target platform information is no longer stored in preferences. Instead use {@link ITargetPlatformService}.
	 */
    @Deprecated
    String //$NON-NLS-1$
    OS = "org.eclipse.pde.ui.os";

    /**
	 * @deprecated Since the 4.4 Luna release, target platform information is no longer stored in preferences. Instead use {@link ITargetPlatformService}.
	 */
    @Deprecated
    String //$NON-NLS-1$
    WS = "org.eclipse.pde.ui.ws";

    /**
	 * @deprecated Since the 4.4 Luna release, target platform information is no longer stored in preferences. Instead use {@link ITargetPlatformService}.
	 */
    @Deprecated
    String //$NON-NLS-1$
    NL = "org.eclipse.pde.ui.nl";

    /**
	 * @deprecated Since the 4.4 Luna release, target platform information is no longer stored in preferences. Instead use {@link ITargetPlatformService}.
	 */
    @Deprecated
    String //$NON-NLS-1$
    ARCH = "org.eclipse.pde.ui.arch";

    /**
	 * @deprecated Since the 4.4 Luna release, target platform information is no longer stored in preferences. Instead use {@link ITargetPlatformService}.
	 */
    @Deprecated
    String //$NON-NLS-1$
    TARGET_PROFILE = "target.profile";

    // Preferences to allow products to add additional options in the target environment combos
    // Unknown if they are being used
    //$NON-NLS-1$
    String OS_EXTRA = "org.eclipse.pde.os.extra";

    //$NON-NLS-1$
    String WS_EXTRA = "org.eclipse.pde.ws.extra";

    //$NON-NLS-1$
    String NL_EXTRA = "org.eclipse.pde.nl.extra";

    //$NON-NLS-1$
    String ARCH_EXTRA = "org.eclipse.pde.arch.extra";

    /** Constant for the string <code>extension</code> */
    //$NON-NLS-1$
    public static final String EXTENSION_NAME = "extension";

    /** Constant for the string <code>plugin.xml</code> */
    //$NON-NLS-1$
    public static final String PLUGIN_FILENAME_DESCRIPTOR = "plugin.xml";

    /** Constant for the string <code>feature.xml</code> */
    //$NON-NLS-1$
    public static final String FEATURE_FILENAME_DESCRIPTOR = "feature.xml";

    /** Constant for the string <code>fragment.xml</code> */
    //$NON-NLS-1$
    public static final String FRAGMENT_FILENAME_DESCRIPTOR = "fragment.xml";

    /** Constant for the string <code>META-INF/MANIFEST.MF</code> */
    //$NON-NLS-1$
    public static final String BUNDLE_FILENAME_DESCRIPTOR = "META-INF/MANIFEST.MF";

    /** Constant for the string <code>MANIFEST.MF</code> */
    //$NON-NLS-1$
    public static final String MANIFEST_FILENAME = "MANIFEST.MF";

    /** Constant for the string <code>.options</code> */
    //$NON-NLS-1$
    public static final String OPTIONS_FILENAME = ".options";

    /** Constant for the string <code>manifest.mf</code> */
    public static final String MANIFEST_FILENAME_LOWER_CASE = MANIFEST_FILENAME.toLowerCase(Locale.ENGLISH);

    /** Constant for the string <code>build.properties</code> */
    //$NON-NLS-1$
    public static final String BUILD_FILENAME_DESCRIPTOR = "build.properties";

    /**
	 * Default version number for plugin and feature
	 */
    //$NON-NLS-1$
    String DEFAULT_VERSION = "0.0.0";

    /**
	 * Target version of <code>3.0</code>
	 * <p>
	 * PDE constant for the version of Eclipse a file/project/bundle is targeting.  The version of
	 * a target platform is determined by what version of Equinox (org.eclipse.osgi) is included in the
	 * target.  The version is only relevant if the user is building an Eclipse plug-in. This constant
	 * can be used to process PDE files that have changed structure between releases.
	 * </p><p>
	 * Anytime a new version constant is added, {@link ICoreConstants#TARGET_VERSION_LATEST} must be updated to
	 * the newest version.  {@link TargetPlatformHelper#getTargetVersionString()} must be updated to return the
	 * new version.
	 * </p>
	 */
    //$NON-NLS-1$
    public static final String TARGET30 = "3.0";

    /**
	 * Target version of <code>3.1</code>
	 * <p>
	 * PDE constant for the version of Eclipse a file/project/bundle is targeting.  The version of
	 * a target platform is determined by what version of Equinox (org.eclipse.osgi) is included in the
	 * target.  The version is only relevant if the user is building an Eclipse plug-in. This constant
	 * can be used to process PDE files that have changed structure between releases.
	 * </p><p>
	 * Anytime a new version constant is added, {@link ICoreConstants#TARGET_VERSION_LATEST} must be updated to
	 * the newest version.  {@link TargetPlatformHelper#getTargetVersionString()} must be updated to return the
	 * new version.
	 * </p>
	 */
    //$NON-NLS-1$
    public static final String TARGET31 = "3.1";

    /**
	 * Target version of <code>3.2</code>
	 * <p>
	 * PDE constant for the version of Eclipse a file/project/bundle is targeting.  The version of
	 * a target platform is determined by what version of Equinox (org.eclipse.osgi) is included in the
	 * target.  The version is only relevant if the user is building an Eclipse plug-in. This constant
	 * can be used to process PDE files that have changed structure between releases.
	 * </p><p>
	 * Anytime a new version constant is added, {@link ICoreConstants#TARGET_VERSION_LATEST} must be updated to
	 * the newest version.  {@link TargetPlatformHelper#getTargetVersionString()} must be updated to return the
	 * new version.
	 * </p>
	 */
    //$NON-NLS-1$
    public static final String TARGET32 = "3.2";

    /**
	 * Target version of <code>3.3</code>
	 * <p>
	 * PDE constant for the version of Eclipse a file/project/bundle is targeting.  The version of
	 * a target platform is determined by what version of Equinox (org.eclipse.osgi) is included in the
	 * target.  The version is only relevant if the user is building an Eclipse plug-in. This constant
	 * can be used to process PDE files that have changed structure between releases.
	 * </p><p>
	 * Anytime a new version constant is added, {@link ICoreConstants#TARGET_VERSION_LATEST} must be updated to
	 * the newest version.  {@link TargetPlatformHelper#getTargetVersionString()} must be updated to return the
	 * new version.
	 * </p>
	 */
    //$NON-NLS-1$
    public static final String TARGET33 = "3.3";

    /**
	 * Target version of <code>3.4</code>
	 * <p>
	 * PDE constant for the version of Eclipse a file/project/bundle is targeting.  The version of
	 * a target platform is determined by what version of Equinox (org.eclipse.osgi) is included in the
	 * target.  The version is only relevant if the user is building an Eclipse plug-in. This constant
	 * can be used to process PDE files that have changed structure between releases.
	 * </p><p>
	 * Anytime a new version constant is added, {@link ICoreConstants#TARGET_VERSION_LATEST} must be updated to
	 * the newest version.  {@link TargetPlatformHelper#getTargetVersionString()} must be updated to return the
	 * new version.
	 * </p>
	 */
    //$NON-NLS-1$
    public static final String TARGET34 = "3.4";

    /**
	 * Target version of <code>3.5</code>
	 * <p>
	 * PDE constant for the version of Eclipse a file/project/bundle is targeting.  The version of
	 * a target platform is determined by what version of Equinox (org.eclipse.osgi) is included in the
	 * target.  The version is only relevant if the user is building an Eclipse plug-in. This constant
	 * can be used to process PDE files that have changed structure between releases.
	 * </p><p>
	 * Anytime a new version constant is added, {@link ICoreConstants#TARGET_VERSION_LATEST} must be updated to
	 * the newest version.  {@link TargetPlatformHelper#getTargetVersionString()} must be updated to return the
	 * new version.
	 * </p>
	 */
    //$NON-NLS-1$
    public static final String TARGET35 = "3.5";

    /**
	 * Target version of <code>3.6</code>
	 * <p>
	 * PDE constant for the version of Eclipse a file/project/bundle is targeting.  The version of
	 * a target platform is determined by what version of Equinox (org.eclipse.osgi) is included in the
	 * target.  The version is only relevant if the user is building an Eclipse plug-in. This constant
	 * can be used to process PDE files that have changed structure between releases.
	 * </p><p>
	 * Anytime a new version constant is added, {@link ICoreConstants#TARGET_VERSION_LATEST} must be updated to
	 * the newest version.  {@link TargetPlatformHelper#getTargetVersionString()} must be updated to return the
	 * new version.
	 * </p>
	 */
    //$NON-NLS-1$
    public static final String TARGET36 = "3.6";

    /**
	 * Target version of <code>3.7</code>
	 * <p>
	 * PDE constant for the version of Eclipse a file/project/bundle is targeting.  The version of
	 * a target platform is determined by what version of Equinox (org.eclipse.osgi) is included in the
	 * target.  The version is only relevant if the user is building an Eclipse plug-in. This constant
	 * can be used to process PDE files that have changed structure between releases.
	 * </p><p>
	 * Anytime a new version constant is added, {@link ICoreConstants#TARGET_VERSION_LATEST} must be updated to
	 * the newest version.  {@link TargetPlatformHelper#getTargetVersionString()} must be updated to return the
	 * new version.
	 * </p>
	 */
    //$NON-NLS-1$
    public static final String TARGET37 = "3.7";

    /**
	 * Target version of <code>3.8</code>
	 * <p>
	 * PDE constant for the version of Eclipse a file/project/bundle is targeting.  The version of
	 * a target platform is determined by what version of Equinox (org.eclipse.osgi) is included in the
	 * target.  The version is only relevant if the user is building an Eclipse plug-in. This constant
	 * can be used to process PDE files that have changed structure between releases.
	 * </p><p>
	 * Anytime a new version constant is added, {@link ICoreConstants#TARGET_VERSION_LATEST} must be updated to
	 * the newest version.  {@link TargetPlatformHelper#getTargetVersionString()} must be updated to return the
	 * new version.
	 * </p>
	 */
    //$NON-NLS-1$
    public static final String TARGET38 = "3.8";

    /**
	 * The highest version of of Eclipse that PDE recognizes as having a special file structure. The value of
	 * this constant may change between releases.
	 * <p>
	 * Currently the latest version is {@link #TARGET38}.  If a new version constant is added to PDE, this
	 * constant must be updated to the latest version.  Also, {@link TargetPlatformHelper#getTargetVersionString()}
	 * must be updated to return the new version.
	 * </p><p>
	 * If the set of target versions available when creating a project changes, NewLibraryPluginCreationPage,
	 * NewProjectCreationPage, and {@link IBundleProjectDescription} must all be updated.
	 * </p>
	 */
    public static final String TARGET_VERSION_LATEST = TARGET38;

    /**
	 * Preference key that stores a list of user specified source locations.
	 * No longer supported in the UI.
	 * @deprecated Not supported in the UI.
	 */
    @Deprecated
    String //$NON-NLS-1$
    P_SOURCE_LOCATIONS = "source_locations";

    //$NON-NLS-1$
    public static final String EQUINOX = "Equinox";

    // project preferences
    //$NON-NLS-1$
    public static final String SELFHOSTING_BIN_EXCLUDES = "selfhosting.binExcludes";

    //$NON-NLS-1$
    public static final String EQUINOX_PROPERTY = "pluginProject.equinox";

    //$NON-NLS-1$
    public static final String EXTENSIONS_PROPERTY = "pluginProject.extensions";

    //$NON-NLS-1$
    public static final String RESOLVE_WITH_REQUIRE_BUNDLE = "resolve.requirebundle";

    /**
	 * Configures launch shortcuts visible in the manifest editor for a project.
	 * Value is a comma separated list of <code>org.eclipse.pde.ui.launchShortcuts</code>
	 * extension identifiers.
	 *
	 * @since 3.6
	 */
    //$NON-NLS-1$
    public static final String MANIFEST_LAUNCH_SHORTCUTS = "manifest.launchShortcuts";

    /**
	 * Configures the export wizard used in the manifest editor for a project.
	 * Value is an <code>org.eclipse.ui.exportWizards</code> extension identifier.
	 *
	 * @since 3.6
	 */
    //$NON-NLS-1$
    public static final String MANIFEST_EXPORT_WIZARD = "manifest.exportWizard";

    // for backwards compatibility with Eclipse 3.0 bundle manifest files
    //$NON-NLS-1$
    public static final String PROVIDE_PACKAGE = "Provide-Package";

    //$NON-NLS-1$
    public static final String REPROVIDE_ATTRIBUTE = "reprovide";

    //$NON-NLS-1$
    public static final String OPTIONAL_ATTRIBUTE = "optional";

    //$NON-NLS-1$
    public static final String REQUIRE_PACKAGES_ATTRIBUTE = "require-packages";

    //$NON-NLS-1$
    public static final String SINGLETON_ATTRIBUTE = "singleton";

    //$NON-NLS-1$
    public static final String PACKAGE_SPECIFICATION_VERSION = "specification-version";

    //$NON-NLS-1$
    public static final String IMPORT_SERVICE = "Import-Service";

    //$NON-NLS-1$
    public static final String EXPORT_SERVICE = "Export-Service";

    // Equinox-specific headers
    //$NON-NLS-1$
    public static final String EXTENSIBLE_API = "Eclipse-ExtensibleAPI";

    //$NON-NLS-1$
    public static final String PATCH_FRAGMENT = "Eclipse-PatchFragment";

    //$NON-NLS-1$
    public static final String PLUGIN_CLASS = "Plugin-Class";

    /**
	 * The 'Eclipse-AutoStart=true' header was used up to and including 3.1 to mean LAZY.
	 * However, this was a poorly named header, since "auto-start" sounds UN-LAZY, and
	 * was replaced with 'Eclipse-LazyStart=true' in 3.2.
	 */
    //$NON-NLS-1$
    public static final String ECLIPSE_AUTOSTART = "Eclipse-AutoStart";

    /**
	 * The 'Eclipse-LazyStart=true' header replaced the 'Eclipse-AutoStart' header
	 * with a better name in 3.2. And since 3.4 (OSGi R4.1), the 'Bundle-ActivationPolicy: lazy'
	 * replaces all of these.
	 */
    //$NON-NLS-1$
    public static final String ECLIPSE_LAZYSTART = "Eclipse-LazyStart";

    //$NON-NLS-1$
    public static final String ECLIPSE_JREBUNDLE = "Eclipse-JREBundle";

    //$NON-NLS-1$
    public static final String ECLIPSE_BUDDY_POLICY = "Eclipse-BuddyPolicy";

    //$NON-NLS-1$
    public static final String ECLIPSE_REGISTER_BUDDY = "Eclipse-RegisterBuddy";

    //$NON-NLS-1$
    public static final String ECLIPSE_GENERIC_CAPABILITY = "Eclipse-GenericCapability";

    //$NON-NLS-1$
    public static final String ECLIPSE_GENERIC_REQUIRED = "Eclipse-GenericRequire";

    //$NON-NLS-1$
    public static final String PLATFORM_FILTER = "Eclipse-PlatformFilter";

    //$NON-NLS-1$
    public static final String ECLIPSE_SOURCE_BUNDLE = "Eclipse-SourceBundle";

    //$NON-NLS-1$
    public static final String ECLIPSE_SYSTEM_BUNDLE = "Eclipse-SystemBundle";

    //$NON-NLS-1$
    public static final String ECLIPSE_BUNDLE_SHAPE = "Eclipse-BundleShape";

    //$NON-NLS-1$
    public static final String ECLIPSE_SOURCE_REFERENCES = "Eclipse-SourceReferences";

    //$NON-NLS-1$
    public static final String SERVICE_COMPONENT = "Service-Component";

    // Equinox-specific system properties
    //$NON-NLS-1$
    public static final String OSGI_SYSTEM_BUNDLE = "osgi.system.bundle";

    //$NON-NLS-1$
    public static final String OSGI_OS = "osgi.os";

    //$NON-NLS-1$
    public static final String OSGI_WS = "osgi.ws";

    //$NON-NLS-1$
    public static final String OSGI_NL = "osgi.nl";

    //$NON-NLS-1$
    public static final String OSGI_ARCH = "osgi.arch";

    //$NON-NLS-1$
    public static final String OSGI_RESOLVE_OPTIONAL = "osgi.resolveOptional";

    //$NON-NLS-1$
    public static final String OSGI_RESOLVER_MODE = "osgi.resolverMode";

    // Equinox-specific directives
    //$NON-NLS-1$
    public static final String INTERNAL_DIRECTIVE = "x-internal";

    //$NON-NLS-1$
    public static final String FRIENDS_DIRECTIVE = "x-friends";

    //$NON-NLS-1$
    public static final String SHAPE_JAR = "jar";

    //$NON-NLS-1$
    public static final String SHAPE_DIR = "dir";

    public static final String[] SHAPE_VALUES = new String[] { SHAPE_DIR, SHAPE_JAR };

    public static final String[] TRANSLATABLE_HEADERS = new String[] { Constants.BUNDLE_VENDOR, Constants.BUNDLE_NAME, Constants.BUNDLE_DESCRIPTION, Constants.BUNDLE_COPYRIGHT, Constants.BUNDLE_CATEGORY, Constants.BUNDLE_CONTACTADDRESS };

    // EASTER EGG
    public static final String[] EE_TOKENS = new String[] { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    "wassim", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    "zx", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    "bbauman", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    "cherie", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    "jlb", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    "cwindatt" };

    // Common folder names
    //$NON-NLS-1$
    public static String MANIFEST_FOLDER_NAME = "META-INF/";

    //$NON-NLS-1$
    public static String OSGI_INF_FOLDER_NAME = "OSGI-INF/";

    //$NON-NLS-1$
    public static String FEATURE_FOLDER_NAME = "features";

    // Common paths
    public static IPath MANIFEST_PATH = new Path(BUNDLE_FILENAME_DESCRIPTOR);

    public static IPath PLUGIN_PATH = new Path(PLUGIN_FILENAME_DESCRIPTOR);

    public static IPath FRAGMENT_PATH = new Path(FRAGMENT_FILENAME_DESCRIPTOR);

    public static IPath FEATURE_PATH = new Path(FEATURE_FILENAME_DESCRIPTOR);

    public static IPath BUILD_PROPERTIES_PATH = new Path(BUILD_FILENAME_DESCRIPTOR);

    public static IPath OSGI_INF_PATH = new Path(OSGI_INF_FOLDER_NAME);

    // Extension point identifiers
    //$NON-NLS-1$
    public static final String EXTENSION_POINT_SOURCE = PDECore.PLUGIN_ID + ".source";

    //$NON-NLS-1$
    public static final String EXTENSION_POINT_BUNDLE_IMPORTERS = PDECore.PLUGIN_ID + ".bundleImporters";

    // file extensions
    /**
	 * File extension for target definitions
	 */
    //$NON-NLS-1$
    public static final String TARGET_FILE_EXTENSION = "target";

    /**
	 * Preference key for the active workspace target platform handle memento.  If not set,
	 * old target preferences will be used to create a default.  If no external bundles are
	 * wanted, this value should be set to {@link #NO_TARGET}.
	 */
    //$NON-NLS-1$
    public static final String WORKSPACE_TARGET_HANDLE = "workspace_target_handle";

    /**
	 * Explicit preference value for {@link #WORKSPACE_TARGET_HANDLE} when the user chooses no
	 * target for the workspace (no external bundles).
	 */
    //$NON-NLS-1$
    public static final String NO_TARGET = "NO_TARGET";

    /**
	 * Constant representing the value for UTF-8 encoding.
	 * Value is: <code>UTF-8</code>
	 */
    //$NON-NLS-1$
    public static final String UTF_8 = "UTF-8";
}
