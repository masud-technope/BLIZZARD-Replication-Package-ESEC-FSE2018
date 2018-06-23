/*******************************************************************************
 * Copyright (c) 2007, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.ui.internal;

/**
 * @since 1.0.0
 */
public interface IApiToolsConstants {

    /**
	 * Empty String constant
	 */
    //$NON-NLS-1$
    public static final String EMPTY_STRING = "";

    /**
	 * Plug-in identifier
	 */
    //$NON-NLS-1$
    public static final String ID_API_TOOLS_UI_PLUGIN = "org.eclipse.pde.api.tools.ui";

    /**
	 * Id for the API baselines preference page. <br>
	 * Value is: <code>org.eclipse.pde.api.tools.ui.apiprofiles.prefpage</code>
	 */
    //$NON-NLS-1$
    public static final String ID_BASELINES_PREF_PAGE = "org.eclipse.pde.api.tools.ui.apiprofiles.prefpage";

    /**
	 * Id for the API errors / warnings preference page <br>
	 * Value is:
	 * <code>org.eclipse.pde.api.tools.ui.apitools.errorwarnings.prefpage</code>
	 */
    //$NON-NLS-1$
    public static final String ID_ERRORS_WARNINGS_PREF_PAGE = "org.eclipse.pde.api.tools.ui.apitools.errorwarnings.prefpage";

    /**
	 * The id for the API errors / warnings property page <br>
	 * Value is: <code>org.eclipse.pde.api.tools.ui.apitools.warningspage</code>
	 */
    //$NON-NLS-1$
    public static final String ID_ERRORS_WARNINGS_PROP_PAGE = "org.eclipse.pde.api.tools.ui.apitools.warningspage";

    /**
	 * The id for the API problem filters property page <br>
	 * Value is: <code>org.eclipse.pde.api.tools.ui.apitools.filterspage</code>
	 */
    //$NON-NLS-1$
    public static final String ID_FILTERS_PROP_PAGE = "org.eclipse.pde.api.tools.ui.apitools.filterspage";

    /**
	 * Key for a compare api image
	 */
    //$NON-NLS-1$
    public static final String IMG_ELCL_COMPARE_APIS = "IMG_ELCL_COMPARE_APIS";

    /**
	 * Key for a compare api disabled image
	 */
    //$NON-NLS-1$
    public static final String IMG_ELCL_COMPARE_APIS_DISABLED = "IMG_ELCL_COMPARE_APIS_DISABLED";

    /**
	 * Key for filter resolution image
	 */
    //$NON-NLS-1$
    public static final String IMG_ELCL_FILTER = "IMG_ELCL_FILTER";

    /**
	 * Key for the PDE Tools menu item for setting up API Tools
	 */
    //$NON-NLS-1$
    public static final String IMG_ELCL_SETUP_APITOOLS = "IMG_ELCL_SETUP_APITOOLS";

    /**
	 * Key for the open page image
	 */
    //$NON-NLS-1$
    public static final String IMG_ELCL_OPEN_PAGE = "IMG_ELCL_OPEN_PAGE";

    /**
	 * Key for enabled remove image
	 */
    //$NON-NLS-1$
    public static final String IMG_ELCL_REMOVE = "IMG_ELCL_REMOVE";

    /**
	 * key for text edit image
	 */
    //$NON-NLS-1$
    public static final String IMG_ELCL_TEXT_EDIT = "IMG_ELCL_TEXT_EDIT";

    /**
	 * Key for API component image.
	 */
    //$NON-NLS-1$
    public static final String IMG_OBJ_API_COMPONENT = "IMG_OBJ_API_COMPONENT";

    /**
	 * Key for API search image
	 */
    //$NON-NLS-1$
    public static final String IMG_OBJ_API_SEARCH = "IMG_OBJ_API_SEARCH";

    /**
	 * Key for API system component image
	 */
    //$NON-NLS-1$
    public static final String IMG_OBJ_API_SYSTEM_LIBRARY = "IMG_OBJ_API_SYSTEM_LIBRARY";

    /**
	 * Key for bundle image
	 */
    //$NON-NLS-1$
    public static final String IMG_OBJ_BUNDLE = "IMG_OBJ_BUNDLE";

    /**
	 * Key for a bundle version image
	 */
    //$NON-NLS-1$
    public static final String IMG_OBJ_BUNDLE_VERSION = "IMG_OBJ_BUNDLE_VERSION";

    /**
	 * Key for Eclipse SDK/API profile image
	 */
    //$NON-NLS-1$
    public static final String IMG_OBJ_ECLIPSE_PROFILE = "IMG_OBJ_ECLIPSE_PROFILE";

    /**
	 * Key for fragment image
	 */
    //$NON-NLS-1$
    public static final String IMG_OBJ_FRAGMENT = "IMG_OBJ_FRAGMENT";

    /**
	 * Key for a correction change
	 *
	 * @since 1.0.500
	 */
    //$NON-NLS-1$
    public static final String IMG_OBJ_CHANGE_CORRECTION = "IMG_OBJ_CHANGE_CORRECTION";

    /**
	 * Error overlay.
	 */
    //$NON-NLS-1$
    public static final String IMG_OVR_ERROR = "IMG_OVR_ERROR";

    /**
	 * Success overlay
	 */
    //$NON-NLS-1$
    public static final String IMG_OVR_SUCCESS = "IMG_OVR_SUCCESS";

    /**
	 * Warning overlay
	 */
    //$NON-NLS-1$
    public static final String IMG_OVR_WARNING = "IMG_OVR_WARNING";

    /**
	 * Wizard banner for editing an API baseline
	 */
    //$NON-NLS-1$
    public static final String IMG_WIZBAN_PROFILE = "IMG_WIZBAN_PROFILE";

    /**
	 * Wizard banner for comparing a selected set of projects to a selected
	 * baseline
	 *
	 * @since 1.0.l
	 */
    //$NON-NLS-1$
    public static final String IMG_WIZBAN_COMPARE_TO_BASELINE = "IMG_WIZBAN_COMPARE_TO_BASELINE";

    /**
	 * Key for enabled export image
	 */
    //$NON-NLS-1$
    public static final String IMG_ELCL_EXPORT = "IMG_ELCL_EXPORT";

    /**
	 * Key for disabled export image
	 */
    //$NON-NLS-1$
    public static final String IMG_DLCL_EXPORT = "IMG_DLCL_EXPORT";

    /**
	 * Key for enabled next navigation image
	 */
    //$NON-NLS-1$
    public static final String IMG_ELCL_NEXT_NAV = "IMG_ELCL_NEXT_NAV";

    /**
	 * Key for enabled previous navigation image
	 */
    //$NON-NLS-1$
    public static final String IMG_ELCL_PREV_NAV = "IMG_ELCL_PREV_NAV";

    /**
	 * Key for disabled next navigation image
	 */
    //$NON-NLS-1$
    public static final String IMG_DLCL_NEXT_NAV = "IMG_DLCL_NEXT_NAV";

    /**
	 * Key for disabled previous navigation image
	 */
    //$NON-NLS-1$
    public static final String IMG_DLCL_PREV_NAV = "IMG_DLCL_PREV_NAV";

    /**
	 * Key for enabled expand all image
	 */
    //$NON-NLS-1$
    public static final String IMG_ELCL_EXPANDALL = "IMG_ELCL_EXPANDALL";

    /**
	 * Key for disabled expand all image
	 */
    //$NON-NLS-1$
    public static final String IMG_DLCL_EXPANDALL = "IMG_DLCL_EXPANDALL";
}
