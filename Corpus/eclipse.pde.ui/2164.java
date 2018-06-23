/*******************************************************************************
 * Copyright (c) 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.internal;

import org.eclipse.pde.api.tools.internal.provisional.model.IApiBaseline;

/**
 * Constants used by API tools core plugin
 *
 * @since 1.0.0
 */
public interface IApiCoreConstants {

    /**
	 * Constant representing the name of a component XML file. Value is:
	 * <code>component.xml</code>
	 */
    //$NON-NLS-1$
    public static final String COMPONENT_XML_NAME = "component.xml";

    /**
	 * Constant representing the value for UTF-8 encoding. Value is:
	 * <code>UTF-8</code>
	 */
    //$NON-NLS-1$
    public static final String UTF_8 = "UTF-8";

    /**
	 * Constant representing the name of a plugin.xml file. Value is:
	 * <code>plugin.xml</code>
	 */
    //$NON-NLS-1$
    public static final String PLUGIN_XML_NAME = "plugin.xml";

    /**
	 * Constant representing the name of a fragment.xml file. Value is:
	 * <code>fragment.xml</code>
	 */
    //$NON-NLS-1$
    public static final String FRAGMENT_XML_NAME = "fragment.xml";

    /**
	 * Constant representing the name of API description XML file. Value is
	 * <code>.api_description</code>
	 */
    //$NON-NLS-1$
    public static final String API_DESCRIPTION_XML_NAME = ".api_description";

    /**
	 * Constant representing the name of API description XML file. Value is
	 * <code>.api_description</code>
	 */
    //$NON-NLS-1$
    public static final String SYSTEM_API_DESCRIPTION_XML_NAME = "system.api_description";

    /**
	 * Constant representing the name of the API filters XML file. Value is
	 * <code>.api_filters</code>
	 */
    //$NON-NLS-1$
    public static final String API_FILTERS_XML_NAME = ".api_filters";

    /**
	 * Constant representing the name of the source bundle manifest header.
	 * Value is: <code>Eclipse-SourceBundle</code>
	 */
    //$NON-NLS-1$
    public static final String ECLIPSE_SOURCE_BUNDLE = "Eclipse-SourceBundle";

    /**
	 * Constant representing the name of the {@link IApiBaseline} used in
	 * headless ant builds. Value is: <code>ant_build_profile</code>
	 */
    //$NON-NLS-1$
    public static final String ANT_BUILD_PROFILE_NAME = "ant_build_profile";

    /**
	 * Preference to store the API Use Scan report location
	 */
    //$NON-NLS-1$
    public static final String API_USE_SCAN_LOCATION = "API_USE_SCAN_LOCATION";

    /**
	 * Constant representing <code>XML</code>
	 */
    //$NON-NLS-1$
    public static final String XML = "xml";
}
