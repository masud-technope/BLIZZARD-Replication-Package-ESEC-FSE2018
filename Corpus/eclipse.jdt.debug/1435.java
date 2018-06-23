/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jesper Steen Moller - Enhancement 254677 - filter getters/setters
 *******************************************************************************/
package org.eclipse.jdt.internal.debug.ui;

import org.eclipse.jdt.debug.ui.IJavaDebugUIConstants;

/**
 * Defines constants which are used to refer to values in the plugin's preference store.
 */
public interface IJDIPreferencesConstants {

    /**
	 * Boolean preference controlling whether to suspend
	 * execution when an uncaught Java exceptions encountered
	 * (while debugging).
	 */
    //$NON-NLS-1$
    public static final String PREF_SUSPEND_ON_UNCAUGHT_EXCEPTIONS = IJavaDebugUIConstants.PLUGIN_ID + ".javaDebug.SuspendOnUncaughtExceptions";

    /**
	 * Boolean preference controlling whether to suspend
	 * execution when a compilation error is encountered
	 * (while debugging).
	 */
    //$NON-NLS-1$
    public static final String PREF_SUSPEND_ON_COMPILATION_ERRORS = IJavaDebugUIConstants.PLUGIN_ID + ".suspend_on_compilation_errors";

    /**
	 * Boolean preference controlling whether synthetic
	 * methods are to be filtered when stepping (and step
	 * filters are enabled).
	 */
    //$NON-NLS-1$
    public static final String PREF_FILTER_SYNTHETICS = IJavaDebugUIConstants.PLUGIN_ID + ".filter_synthetics";

    /**
	 * Boolean preference controlling whether static
	 * initializers are to be filtered when stepping (and step
	 * filters are enabled).
	 */
    //$NON-NLS-1$
    public static final String PREF_FILTER_STATIC_INITIALIZERS = IJavaDebugUIConstants.PLUGIN_ID + ".filter_statics";

    /**
	 * Boolean preference controlling whether simple getters
	 * are to be filtered when stepping (and step
	 * filters are enabled).
	 */
    //$NON-NLS-1$
    public static final String PREF_FILTER_GETTERS = IJavaDebugUIConstants.PLUGIN_ID + ".filter_get";

    /**
	 * Boolean preference controlling whether simple setters
	 * are to be filtered when stepping (and step
	 * filters are enabled).
	 */
    //$NON-NLS-1$
    public static final String PREF_FILTER_SETTERS = IJavaDebugUIConstants.PLUGIN_ID + ".filter_setters";

    /**
	 * Boolean preference controlling whether constructors
	 * are to be filtered when stepping (and step
	 * filters are enabled).
	 */
    //$NON-NLS-1$
    public static final String PREF_FILTER_CONSTRUCTORS = IJavaDebugUIConstants.PLUGIN_ID + ".filter_constructors";

    /**
	 * Boolean preference controlling whether a step landing in a filtered
	 * location proceeds through to an un-filtered location, or returns.
	 * 
	 * @since 3.3
	 */
    //$NON-NLS-1$
    public static final String PREF_STEP_THRU_FILTERS = IJavaDebugUIConstants.PLUGIN_ID + ".step_thru_filters";

    /**
	 * List of active step filters. A String containing a comma
	 * separated list of fully qualified type names/patterns.
	 */
    //$NON-NLS-1$
    public static final String PREF_ACTIVE_FILTERS_LIST = IJavaDebugUIConstants.PLUGIN_ID + ".active_filters";

    /**
	 * List of inactive step filters. A String containing a comma
	 * separated list of fully qualified type names/patterns.
	 */
    //$NON-NLS-1$	
    public static final String PREF_INACTIVE_FILTERS_LIST = IJavaDebugUIConstants.PLUGIN_ID + ".inactive_filters";

    /**
	 * Boolean preference controlling whether to alert
	 * with a dialog when hot code replace fails.
	 */
    //$NON-NLS-1$
    public static final String PREF_ALERT_HCR_FAILED = IJavaDebugUIConstants.PLUGIN_ID + ".javaDebug.alertHCRFailed";

    /**
	 * Boolean preference controlling whether to alert
	 * with a dialog when hot code replace is not supported.
	 */
    //$NON-NLS-1$
    public static final String PREF_ALERT_HCR_NOT_SUPPORTED = IJavaDebugUIConstants.PLUGIN_ID + ".javaDebug.alertHCRNotSupported";

    /**
	 * Boolean preference controlling whether to alert
	 * with a dialog when hot code replace results in 
	 * obsolete methods.
	 */
    //$NON-NLS-1$
    public static final String PREF_ALERT_OBSOLETE_METHODS = IJavaDebugUIConstants.PLUGIN_ID + "javaDebug.alertObsoleteMethods";

    /**
	 * Boolean preference controlling whether the debugger shows 
	 * qualifed names. When <code>true</code> the debugger
	 * will show qualified names in newly opened views.
	 * 
	 * @since 2.0
	 */
    //$NON-NLS-1$
    public static final String PREF_SHOW_QUALIFIED_NAMES = IJavaDebugUIConstants.PLUGIN_ID + ".show_qualified_names";

    /**
	 * List of defined detail formatters.A String containing a comma
	 * separated list of fully qualified type names, the associated
	 * code snippet and an 'enabled' flag.
	 */
    //$NON-NLS-1$
    public static final String PREF_DETAIL_FORMATTERS_LIST = IJavaDebugUIConstants.PLUGIN_ID + ".detail_formatters";

    /**
	 * Boolean preference indicating whether (non-final) static varibles should be shown
	 * in variable views. A view may over-ride this preference, and if so, stores
	 * its preference, prefixed by view id.
	 * 
	 * @since 2.1
	 */
    //$NON-NLS-1$
    public static final String PREF_SHOW_STATIC_VARIABLES = IJavaDebugUIConstants.PLUGIN_ID + ".show_static_variables";

    /**
	 * Boolean preference indicating whether constant (final static) varibles should be shown
	 * in variable views. A view may over-ride this preference, and if so, stores
	 * its preference, prefixed by view id.
	 * 
	 * @since 2.1
	 */
    //$NON-NLS-1$
    public static final String PREF_SHOW_CONSTANTS = IJavaDebugUIConstants.PLUGIN_ID + ".show_constants";

    /**
	 * Boolean preference indicating whether null array entries should be shown
	 * in variable views. A view may over-ride this preference, and if so, stores
	 * its preference, prefixed by view id.
	 * 
	 * @since 3.0
	 */
    //$NON-NLS-1$	
    public static final String PREF_SHOW_NULL_ARRAY_ENTRIES = IJavaDebugUIConstants.PLUGIN_ID + ".show_null_entries";

    /**
	 * Boolean preference indicating whether hex values should be shown for primitives
	 * in variable views. A view may over-ride this preference, and if so, stores
	 * its preference, prefixed by view id.
	 * 
	 * @since 2.1
	 */
    //$NON-NLS-1$	
    public static final String PREF_SHOW_HEX = IJavaDebugUIConstants.PLUGIN_ID + ".show_hex";

    /**
	 * Boolean preference indicating whether char values should be shown for primitives
	 * in variable views. A view may over-ride this preference, and if so, stores
	 * its preference, prefixed by view id.
	 * 
	 * @since 2.1
	 */
    //$NON-NLS-1$
    public static final String PREF_SHOW_CHAR = IJavaDebugUIConstants.PLUGIN_ID + ".show_char";

    /**
	 * Boolean preference indicating whether unsigned values should be shown for primitives
	 * in variable views. A view may over-ride this preference, and if so, stores
	 * its preference, prefixed by view id.
	 * 
	 * @since 2.1
	 */
    //$NON-NLS-1$	
    public static final String PREF_SHOW_UNSIGNED = IJavaDebugUIConstants.PLUGIN_ID + ".show_unsigned";

    /**
	 * String preference indication when and where variable details should appear.
	 * Valid values include: 
	 * <ul>
	 *   <li><code>INLINE_ALL</code> to show inline details for all variables
	 *   <li><code>INLINE_FORMATTERS</code> to show inline details for variables with formatters
	 *   <li><code>DETAIL_PANE</code> to show details only in the detail pane
	 * </ul> 
	 */
    //$NON-NLS-1$
    public static final String PREF_SHOW_DETAILS = IJavaDebugUIConstants.PLUGIN_ID + ".show_details";

    /**
	 * "Show detail" preference values.
	 */
    //$NON-NLS-1$
    public static final String INLINE_ALL = "INLINE_ALL";

    //$NON-NLS-1$
    public static final String INLINE_FORMATTERS = "INLINE_FORMATTERS";

    //$NON-NLS-1$
    public static final String DETAIL_PANE = "DETAIL_PANE";

    /**
	 * Common dialog settings
	 */
    //$NON-NLS-1$
    public static final String DIALOG_ORIGIN_X = "DIALOG_ORIGIN_X";

    //$NON-NLS-1$
    public static final String DIALOG_ORIGIN_Y = "DIALOG_ORIGIN_Y";

    //$NON-NLS-1$
    public static final String DIALOG_WIDTH = "DIALOG_WIDTH";

    //$NON-NLS-1$	
    public static final String DIALOG_HEIGHT = "DIALOG_HEIGHT";

    /**
	 * Boolean preference controlling whether to alert
	 * with a dialog when unable to install a breakpoint
	 * (line info not available, ...)
	 */
    //$NON-NLS-1$
    public static final String PREF_ALERT_UNABLE_TO_INSTALL_BREAKPOINT = IJavaDebugUIConstants.PLUGIN_ID + ".prompt_unable_to_install_breakpoint";

    //$NON-NLS-1$
    public static final String PREF_THREAD_MONITOR_IN_DEADLOCK_COLOR = "org.eclipse.jdt.debug.ui.InDeadlockColor";

    /**
	 * @since 3.2
	 */
    //$NON-NLS-1$
    public static final String PREF_OPEN_INSPECT_POPUP_ON_EXCEPTION = IJavaDebugUIConstants.PLUGIN_ID + ".open_inspect_popup_on_exception";

    /**
	 * Boolean  preference controlling whether the java stack trace
	 * console should be formatted when ever a paste occurs.
	 * @since 3.3
	 */
    //$NON-NLS-1$;
    public static final String PREF_AUTO_FORMAT_JSTCONSOLE = IJavaDebugUIConstants.PLUGIN_ID + ".auto_format_jstconsole";

    /**
	 * Boolean preference controlling whether to prompt with a dialog when deleting a conditional
	 * breakpoint.
	 */
    //$NON-NLS-1$
    public static final String PREF_PROMPT_DELETE_CONDITIONAL_BREAKPOINT = IJavaDebugUIConstants.PLUGIN_ID + ".prompt_delete_conditional_breakpoint";
}
