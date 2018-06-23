/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Saff (saff@mit.edu) - bug 102632: [JUnit] Support for JUnit 4.
 *******************************************************************************/
package org.eclipse.jdt.internal.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;

/**
 * Defines constants which are used to refer to values in the plugin's preference store.
 */
public class JUnitPreferencesConstants {

    /**
	 * Boolean preference controlling whether the failure stack should be
	 * filtered.
	 */
    //$NON-NLS-1$
    public static final String DO_FILTER_STACK = JUnitCorePlugin.PLUGIN_ID + ".do_filter_stack";

    /**
	 * Boolean preference controlling whether the JUnit view should be shown on
	 * errors only.
	 */
    //$NON-NLS-1$
    public static final String SHOW_ON_ERROR_ONLY = JUnitCorePlugin.PLUGIN_ID + ".show_on_error";

    /**
	 * Boolean preference controlling whether '-ea' should be added to VM arguments when creating a
	 * new JUnit launch configuration.
	 */
    //$NON-NLS-1$
    public static final String ENABLE_ASSERTIONS = JUnitCorePlugin.PLUGIN_ID + ".enable_assertions";

    public static final boolean ENABLE_ASSERTIONS_DEFAULT = true;

    /**
	 * List of active stack filters. A String containing a comma separated list
	 * of fully qualified type names/patterns.
	 */
    //$NON-NLS-1$
    public static final String PREF_ACTIVE_FILTERS_LIST = JUnitCorePlugin.PLUGIN_ID + ".active_filters";

    /**
	 * List of inactive stack filters. A String containing a comma separated
	 * list of fully qualified type names/patterns.
	 */
    //$NON-NLS-1$
    public static final String PREF_INACTIVE_FILTERS_LIST = JUnitCorePlugin.PLUGIN_ID + ".inactive_filters";

    /**
	 * Maximum number of remembered test runs.
	 */
    //$NON-NLS-1$
    public static final String MAX_TEST_RUNS = JUnitCorePlugin.PLUGIN_ID + ".max_test_runs";

    /**
	 * Javadoc location for JUnit 3
	 */
    //$NON-NLS-1$
    public static final String JUNIT3_JAVADOC = JUnitCorePlugin.PLUGIN_ID + ".junit3.javadoclocation";

    /**
	 * Javadoc location for JUnit 4
	 */
    //$NON-NLS-1$
    public static final String JUNIT4_JAVADOC = JUnitCorePlugin.PLUGIN_ID + ".junit4.javadoclocation";

    /**
	 * Javadoc location for org.hamcrest.core (JUnit 4)
	 */
    //$NON-NLS-1$
    public static final String HAMCREST_CORE_JAVADOC = JUnitCorePlugin.PLUGIN_ID + ".junit4.hamcrest.core.javadoclocation";

    private static final String[] fgDefaultFilterPatterns = new String[] { //$NON-NLS-1$
    "org.eclipse.jdt.internal.junit.runner.*", //$NON-NLS-1$
    "org.eclipse.jdt.internal.junit4.runner.*", //$NON-NLS-1$
    "org.eclipse.jdt.internal.junit.ui.*", //$NON-NLS-1$
    "junit.framework.TestCase", //$NON-NLS-1$
    "junit.framework.TestResult", //$NON-NLS-1$
    "junit.framework.TestResult$1", //$NON-NLS-1$
    "junit.framework.TestSuite", //$NON-NLS-1$
    "junit.framework.Assert", //$NON-NLS-1$ //TODO: filter all these?
    "org.junit.*", //$NON-NLS-1$
    "java.lang.reflect.Method.invoke", //$NON-NLS-1$
    "sun.reflect.*" };

    private  JUnitPreferencesConstants() {
    // no instance
    }

    /**
	 * Returns the default list of active stack filters.
	 *
	 * @return list
	 */
    public static List<String> createDefaultStackFiltersList() {
        return Arrays.asList(fgDefaultFilterPatterns);
    }

    /**
	 * Serializes the array of strings into one comma-separated string.
	 *
	 * @param list array of strings
	 * @return a single string composed of the given list
	 */
    public static String serializeList(String[] list) {
        if (list == null)
            //$NON-NLS-1$
            return "";
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < list.length; i++) {
            if (i > 0)
                buffer.append(',');
            buffer.append(list[i]);
        }
        return buffer.toString();
    }

    /**
	 * Parses the comma-separated string into an array of strings.
	 * 
	 * @param listString a comma-separated string
	 * @return an array of strings
	 */
    public static String[] parseList(String listString) {
        List<String> list = new ArrayList(10);
        //$NON-NLS-1$
        StringTokenizer tokenizer = new StringTokenizer(listString, ",");
        while (tokenizer.hasMoreTokens()) list.add(tokenizer.nextToken());
        return list.toArray(new String[list.size()]);
    }

    public static String[] getFilterPatterns() {
        return JUnitPreferencesConstants.parseList(Platform.getPreferencesService().getString(JUnitCorePlugin.CORE_PLUGIN_ID, PREF_ACTIVE_FILTERS_LIST, null, null));
    }

    public static boolean getFilterStack() {
        return Platform.getPreferencesService().getBoolean(JUnitCorePlugin.CORE_PLUGIN_ID, DO_FILTER_STACK, true, null);
    }

    public static void setFilterStack(boolean filter) {
        InstanceScope.INSTANCE.getNode(JUnitCorePlugin.CORE_PLUGIN_ID).putBoolean(DO_FILTER_STACK, filter);
    }
}
