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
package org.eclipse.pde.launching;

/**
 * Constant definitions for PDE launch configurations.
 * <p>
 * Constant definitions only; not to be implemented.
 * </p>
 * <p>
 * This class originally existed in 3.2 as
 * <code>org.eclipse.pde.ui.launcher.IPDELauncherConstants</code>.
 * </p>
 * @since 3.6
 *
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IPDELauncherConstants {

    /**
	 * Launch configuration attribute key. The value is a string specifying
	 * workspace data location for an Eclipse application.
	 */
    //$NON-NLS-1$
    String LOCATION = "location";

    /**
	 * Launch configuration attribute key. The value is a boolean specifying
	 * workspace data location for an Eclipse application should be cleared
	 * prior to launching.
	 */
    //$NON-NLS-1$
    String DOCLEAR = "clearws";

    /**
	 * Launch configuration attribute key. The value is a boolean specifying
	 * whether the user should be prompted prior to clearing the workspace.
	 *
	 * @see IPDELauncherConstants#DOCLEAR
	 */
    //$NON-NLS-1$
    String ASKCLEAR = "askclear";

    /**
	 * Launch configuration attribute key. The value is a string specifying
	 * the application to run.  If the value is <code>null</code>, the default
	 * application as specified in the target platform will be used.
	 */
    //$NON-NLS-1$
    String APPLICATION = "application";

    /**
	 * Launch configuration attribute key. The value is a string specifying
	 * the product to run.
	 *
	 * @see IPDELauncherConstants#APPLICATION
	 */
    //$NON-NLS-1$
    String PRODUCT = "product";

    /**
	 * Launch configuration attribute key. The value is a boolean specifying
	 * if the launch should appear in product-mode.  If the value is <code>false</code>,
	 * the launch takes place in application-mode.
	 *
	 * @see IPDELauncherConstants#PRODUCT
	 * @see IPDELauncherConstants#APPLICATION
	 */
    //$NON-NLS-1$
    String USE_PRODUCT = "useProduct";

    /**
	 * Launch configuration attribute key used in Plug-in JUnit launch configurations only.
	 * The value is a string specifying the application to be tested.
	 * If the value is <code>null</code>, the default UI workbench application is tested.
	 */
    //$NON-NLS-1$
    String APP_TO_TEST = "testApplication";

    /**
	 * Launch configuration attribute key. The value is a string specifying
	 * the name of the VM to launch with.  If the value is <code>null</code>,
	 * the default workspace VM is used.
	 *
	 * @deprecated use IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH
	 */
    @Deprecated
    String //$NON-NLS-1$
    VMINSTALL = "vminstall";

    /**
	 * Launch configuration attribute key. The value is a string specifying
	 * the user-entered bootstrap classpath entries.
	 */
    //$NON-NLS-1$
    String BOOTSTRAP_ENTRIES = "bootstrap";

    /**
	 * Launch configuration attribute key. The value is a boolean specifying
	 * if the default self-hosting mode should be used when launching.
	 * The default being to launch with all workspace plug-ins and all the
	 * plug-ins that are explicitly checked on the Target Platform preference page.
	 */
    //$NON-NLS-1$
    String USE_DEFAULT = "default";

    /**
	 * Launch configuration attribute key. The value is a boolean specifying
	 * if the feature-based self-hosting mode should be used.
	 * The workspace must be set up properly for the feature-based self-hosting
	 * to succeed.
	 * Check the PDE Tips and Tricks section for how to set up feature-based self-hosting.
	 *
	 * @deprecated As of 3.6 the feature-based self-hosting option is not supported
	 */
    @Deprecated
    String //$NON-NLS-1$
    USEFEATURES = "usefeatures";

    /**
	 * Launch configuration attribute key. The value is a string specifying
	 * a comma-separated list of IDs of workspace plug-ins to launch with.
	 * This value is only used when the Automatic Add option is off.
	 *
	 * @see IPDELauncherConstants#AUTOMATIC_ADD
	 */
    //$NON-NLS-1$
    String SELECTED_WORKSPACE_PLUGINS = "selected_workspace_plugins";

    /**
	 * Launch configuration attribute key. The value is a string specifying
	 * a comma-separated list of IDs of workspace plug-ins that are to be excluded from
	 * the launch.
	 * This value is only used when the Automatic Add option is on.
	 *
	 * @see IPDELauncherConstants#AUTOMATIC_ADD
	 */
    //$NON-NLS-1$
    String DESELECTED_WORKSPACE_PLUGINS = "deselected_workspace_plugins";

    /**
	 * Launch configuration attribute key. The value is a boolean specifying
	 * whether workspace plug-in created after the creation of a launch configuration
	 * should be added to the list of plug-ins to launch with.
	 *
	 * If the value is <code>true</code>, then DESELECTED_WORKSPACE_PLUGINS should be used.
	 * Otherwise, SELECTED_WORKSPACE_PLUGINS should be used.
	 *
	 * @see IPDELauncherConstants#DESELECTED_WORKSPACE_PLUGINS
	 * @see IPDELauncherConstants#SELECTED_WORKSPACE_PLUGINS
	 */
    //$NON-NLS-1$
    String AUTOMATIC_ADD = "automaticAdd";

    /**
	 * Launch configuration attribute key. The value is a boolean specifying
	 * whether the list of plug-ins to run should be validate prior to launching.
	 * If problems are found, they will be reported and the user will be able to cancel or
	 * continue.
	 * If no problems are found, the launch continues as normal.
	 */
    //$NON-NLS-1$
    String AUTOMATIC_VALIDATE = "automaticValidate";

    /**
	 * Launch configuration attribute key. The value is a string specifying
	 * a comma-separated list of IDs of target platform plug-ins to launch with.
	 * This value is only used when the Automatic Add option is off.
	 */
    //$NON-NLS-1$
    String SELECTED_TARGET_PLUGINS = "selected_target_plugins";

    /**
	 * Launch configuration attribute key. The value is a boolean indicating
	 * whether the computation of required plug-ins on the Plug-ins tab should include
	 * the traversal of optional dependencies.
	 */
    //$NON-NLS-1$
    String INCLUDE_OPTIONAL = "includeOptional";

    /**
	 * Launch configuration attribute key. The value is a boolean indicating
	 * whether tracing is enabled or disabled.
	 */
    //$NON-NLS-1$
    String TRACING = "tracing";

    /**
	 * Launch configuration attribute key. The value is a map containing the list
	 * of options to debug with.
	 */
    //$NON-NLS-1$
    String TRACING_OPTIONS = "tracingOptions";

    /**
	 * Launch configuration attribute key. The value is the id of the last plug-in
	 * that was selected on the Tracing tab.
	 *
	 * @deprecated This option is no longer supported in the launch config.  A recent selection is stored
	 * in dialog settings.
	 */
    @Deprecated
    String //$NON-NLS-1$
    TRACING_SELECTED_PLUGIN = "selectedPlugin";

    /**
	 * Launch configuration attribute key. The value is the IDs of all plug-ins
	 * checked on the Tracing tab.  The value may also be "[NONE]"
	 *
	 * @see IPDELauncherConstants#TRACING_NONE
	 */
    //$NON-NLS-1$
    String TRACING_CHECKED = "checked";

    /**
	 * Launch configuration attribute value indicating that, although tracing is enabled,
	 * no plug-ins have been selected to be traced.
	 */
    //$NON-NLS-1$
    String TRACING_NONE = "[NONE]";

    /**
	 * Launch configuration attribute key. The value is a boolean specifying
	 * if PDE should generate a default configuration area for the launch.
	 *
	 * If <code>true</code>, a configuration location in the PDE metadata area
	 * is created.  Otherwise, the user is expected to specify a location.
	 *
	 * @see IPDELauncherConstants#CONFIG_LOCATION
	 */
    //$NON-NLS-1$
    String CONFIG_USE_DEFAULT_AREA = "useDefaultConfigArea";

    /**
	 * Launch configuration attribute key. The value is a string specifying
	 * the configuration area location for an Eclipse application launch.
	 *
	 * This key is only used when CONFIG_USE_DEFAULT_AREA is <code>false</code>.
	 *
	 * @see IPDELauncherConstants#CONFIG_USE_DEFAULT_AREA
	 */
    //$NON-NLS-1$
    String CONFIG_LOCATION = "configLocation";

    /**
	 * Launch configuration attribute key. The value is a boolean specifying
	 * if the configuration area location should be cleared prior to launching
	*/
    //$NON-NLS-1$
    String CONFIG_CLEAR_AREA = "clearConfig";

    /**
	 * Launch configuration atribute key.  The value is a boolean specifying
	 * if PDE should generate a default config.ini file for the launch.
	 *
	 * If <code>true</code>, a configuration file is created.
	 * Otherwise, the user is expected to specify a config.ini to be used as a template.
	 *
	 * @see IPDELauncherConstants#CONFIG_TEMPLATE_LOCATION
	 */
    //$NON-NLS-1$
    String CONFIG_GENERATE_DEFAULT = "useDefaultConfig";

    /**
	 * Launch configuration attribute key. The value is a string specifying
	 * the location of the config.ini file to be used as a template for an
	 * Eclipse application launch.
	 *
	 * This key is only used when CONFIG_GENERATE_DEFAULT is <code>false</code>.
	 *
	 * @see IPDELauncherConstants#CONFIG_GENERATE_DEFAULT
	 */
    //$NON-NLS-1$
    String CONFIG_TEMPLATE_LOCATION = "templateConfig";

    /**
	 * Launch configuration attribute key. The value is a string specifying
	 * the location of the .product file with which this launch configuration
	 * is associated.
	 */
    //$NON-NLS-1$
    String PRODUCT_FILE = "productFile";

    /**
	 * Launch configuration attribute key.  The value is the ID of an OSGi framework
	 * declared in an <code>org.eclipse.pde.ui.osgiLaunchers</code> extension point.
	 *
	 * @since 3.3
	 */
    //$NON-NLS-1$
    String OSGI_FRAMEWORK_ID = "osgi_framework_id";

    /**
	 * Launch configuration attribute key.  The value is a boolean specifying
	 * if the default Auto-Start for an OSGi Framework launch configuration
	 * is <code>true</code> or <code>false</code>
	 *
	 * @see IPDELauncherConstants#DEFAULT_START_LEVEL
	 */
    //$NON-NLS-1$
    String DEFAULT_AUTO_START = "default_auto_start";

    /**
	 * Launch configuration attribute key.  The value is an integer specifying
	 * the default start level for bundles in an OSGi Framework launch configuration.
	 *
	 * @see IPDELauncherConstants#DEFAULT_AUTO_START
	 */
    //$NON-NLS-1$
    String DEFAULT_START_LEVEL = "default_start_level";

    /**
	 * Launch configuration attribute key.  The value is a comma-separated list
	 * of workspace bundles to launch with the OSGi framework.
	 *
	 * Each token in the list is of the format:
	 * <plugin-id>@<start-level>:<auto-start>
	 *
	 * @see IPDELauncherConstants#DEFAULT_AUTO_START
	 * @see IPDELauncherConstants#DEFAULT_START_LEVEL
	 */
    //$NON-NLS-1$
    String WORKSPACE_BUNDLES = "workspace_bundles";

    /**
	 * Launch configuration attribute key.  The value is a comma-separated list
	 * of non-workspace bundles to launch with the OSGi framework.
	 *
	 * Each token in the list is of the format:
	 * <plugin-id>@<start-level>:<auto-start>
	 *
	 * @see IPDELauncherConstants#DEFAULT_AUTO_START
	 * @see IPDELauncherConstants#DEFAULT_START_LEVEL
	 */
    //$NON-NLS-1$
    String TARGET_BUNDLES = "target_bundles";

    /**
	 * Launch configuration attribute key.  The value can be either the full path
	 * to the workspace location of a Target Definition (ie. .target file), or
	 * the ID of a target defined in an org.eclipse.pde.core.targets extension.
	 */
    //$NON-NLS-1$
    String DEFINED_TARGET = "defined_target";

    /**
	 * Launch configuration attribute key. The value is a boolean indicating
	 * whether or not to display only selected plug-ins.
	 *
	 * @since 3.4
	 */
    //$NON-NLS-1$
    String SHOW_SELECTED_ONLY = "show_selected_only";

    /**
	 * The unique tab identifier for the bundles tab
	 *
	 * @since 3.5
	 */
    //$NON-NLS-1$
    String TAB_BUNDLES_ID = "org.eclipse.pde.ui.launch.tab.osgi.bundles";

    /**
	 * The unique tab identifier for the configuration tab
	 *
	 * @since 3.5
	 */
    //$NON-NLS-1$
    String TAB_CONFIGURATION_ID = "org.eclipse.pde.ui.launch.tab.configuration";

    /**
	 * The unique tab identifier for the main tab
	 *
	 * @since 3.5
	 */
    //$NON-NLS-1$
    String TAB_MAIN_ID = "org.eclipse.pde.ui.launch.tab.main";

    /**
	 * The unique tab identifier for the osgi settings tab
	 *
	 * @since 3.5
	 */
    //$NON-NLS-1$
    String TAB_OSGI_SETTINGS_ID = "org.eclipse.pde.ui.launch.tab.osgi.settings";

    /**
	 * The unique tab identifier for the plug-in junit tab
	 *
	 * @since 3.5
	 */
    //$NON-NLS-1$
    String TAB_PLUGIN_JUNIT_MAIN_ID = "org.eclipse.pde.ui.launch.tab.junit.main";

    /**
	 * The unique tab identifier for the plug-ins tab
	 *
	 * @since 3.5
	 */
    //$NON-NLS-1$
    String TAB_PLUGINS_ID = "org.eclipse.pde.ui.launch.tab.plugins";

    /**
	 * The unique tab identifier for the tracing tab
	 *
	 * @since 3.5
	 */
    //$NON-NLS-1$
    String TAB_TRACING_ID = "org.eclipse.pde.ui.launch.tab.tracing";

    /**
	 * The unique tab identifier for the tracing tab
	 *
	 * @since 3.5
	 */
    //$NON-NLS-1$
    String TAB_TEST_ID = "org.eclipse.pde.ui.launch.tab.test";

    /**
	 * The launch configuration type id for OSGi launches.
	 *
	 * @since 3.5
	 */
    //$NON-NLS-1$
    String OSGI_CONFIGURATION_TYPE = "org.eclipse.pde.ui.EquinoxLauncher";

    /**
	 * Launch configuration attribute key. The value is a boolean specifying
	 * whether the tests should run on the UI thread.
	 *
	 * The default value is <code>true</code>
	 *
	 * @since 3.5
	 */
    //$NON-NLS-1$
    String RUN_IN_UI_THREAD = "run_in_ui_thread";

    /**
	 * The launch configuration type for Eclipse application launches.
	 *
	 * @since 3.6
	 */
    //$NON-NLS-1$
    String ECLIPSE_APPLICATION_LAUNCH_CONFIGURATION_TYPE = "org.eclipse.pde.ui.RuntimeWorkbench";

    /**
	 * Launch configuration attribute key.  The value is a boolean specifying
	 * whether a p2 profile should be
	 *
	 * @since 3.6
	 */
    //$NON-NLS-1$
    String GENERATE_PROFILE = "generateProfile";

    /**
	 * Launch configuration attribute key. The value is a List specifying the features
	 * to include when launching (when {@link #USE_CUSTOM_FEATURES} is set to <code>true</code>.
	 * The values in the List are strings that contain the id and plugin resolution value as follows:
	 * <pre>
	 * [feature_id]:[resolution]
	 * </pre>
	 * The resolution must be one of {@link #LOCATION_DEFAULT}, {@link #LOCATION_EXTERNAL}, {@link #LOCATION_WORKSPACE}
	 *
	 * @since 3.6
	 */
    //$NON-NLS-1$
    String SELECTED_FEATURES = "selected_features";

    /**
	 * Launch configuration attribute key. The value is a boolean specifying
	 * if the feature-based launching mode should be used.
	 * This mode will launch with all the workspace and external features
	 * that have been explicitly selected in the Plug-ins Tab.
	 *
	 *  @since 3.6
	 */
    //$NON-NLS-1$
    String USE_CUSTOM_FEATURES = "useCustomFeatures";

    /**
	 * Launch configuration attribute key. The value is a String specifying
	 * if the default location for a feature is {@link #LOCATION_WORKSPACE}
	 * or {@link #LOCATION_EXTERNAL}
	 *
	 *  @since 3.6
	 */
    //$NON-NLS-1$
    String FEATURE_DEFAULT_LOCATION = "featureDefaultLocation";

    /**
	 * Launch configuration attribute key. The value is a String specifying
	 * if the default plug-in resolution location for a feature
	 * is {@link #LOCATION_WORKSPACE} or {@link #LOCATION_EXTERNAL}
	 *
	 *  @since 3.6
	 */
    //$NON-NLS-1$
    String FEATURE_PLUGIN_RESOLUTION = "featurePluginResolution";

    /**
	 * Value for a launch configuration attribute used when the object should be
	 * obtained from whatever the default location is for this works
	 *
	 * @since 3.6
	 * @see #FEATURE_PLUGIN_RESOLUTION
	 */
    //$NON-NLS-1$
    String LOCATION_DEFAULT = "default";

    /**
	 * Value for a launch configuration attribute used when the object should
	 * be obtained from an external location over the workspace.
	 *
	 * @since 3.6
	 * @see #FEATURE_DEFAULT_LOCATION
	 * @see #FEATURE_PLUGIN_RESOLUTION
	 */
    //$NON-NLS-1$
    String LOCATION_EXTERNAL = "external";

    /**
	 * Value for a launch configuration attribute used when the object should
	 * be obtained from the workspace over an external location.
	 *
	 * @since 3.6
	 * @see #FEATURE_DEFAULT_LOCATION
	 * @see #FEATURE_PLUGIN_RESOLUTION
	 */
    //$NON-NLS-1$
    String LOCATION_WORKSPACE = "workspace";

    /**
	 * Launch configuration attribute key. The value is a List specifying the additional plug-ins that
	 * will be included along with the features in the list {@link #SELECTED_FEATURES}
	 * when launching (when {@link #USE_CUSTOM_FEATURES} is set to <code>true</code>.
	 * The values in the List are strings that contain the id and versions as follows:
	 * <pre>
	 * [plugin_id]:[version]
	 * </pre>
	 *
	 * @since 3.6
	 */
    //$NON-NLS-1$
    String ADDITIONAL_PLUGINS = "additional_plugins";
}
