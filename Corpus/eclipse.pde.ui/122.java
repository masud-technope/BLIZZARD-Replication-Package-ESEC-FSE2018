/*******************************************************************************
 * Copyright (c) 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.trace.internal.utils;

/**
 * A collection of constant values used by the tracing UI
 */
public class TracingConstants {

    /** The name of the bundle */
    //$NON-NLS-1$
    public static final String BUNDLE_ID = "org.eclipse.ui.trace";

    /** The separator for option paths in key=value pairs */
    //$NON-NLS-1$
    public static final String DEBUG_OPTION_PATH_SEPARATOR = "=";

    /** The value for a debug option that is disabled */
    //$NON-NLS-1$
    public static final String DEBUG_OPTION_VALUE_FALSE = "false";

    /** The value for a debug option that is enabled **/
    //$NON-NLS-1$
    public static final String DEBUG_OPTION_VALUE_TRUE = "true";

    /** Tracing Component extension point name */
    //$NON-NLS-1$
    public static final String TRACING_EXTENSION_POINT_NAME = "traceComponents";

    /** The name of the 'id' attribute for a Tracing Component */
    //$NON-NLS-1$
    public static final String TRACING_EXTENSION_ID_ATTRIBUTE = "id";

    /** The name of the 'label' attribute for a Tracing Component */
    //$NON-NLS-1$
    public static final String TRACING_EXTENSION_LABEL_ATTRIBUTE = "label";

    /** The name of the 'bundle' attribute for a Tracing Component */
    //$NON-NLS-1$
    public static final String TRACING_EXTENSION_BUNDLE_ATTRIBUTE = "bundle";

    /** The name of the 'name' attribute for a bundle in a Tracing Component */
    //$NON-NLS-1$
    public static final String TRACING_EXTENSION_BUNDLE_NAME_ATTRIBUTE = "name";

    /** The name of the 'consumed' attribute for a bundle in a Tracing Component */
    //$NON-NLS-1$
    public static final String TRACING_EXTENSION_BUNDLE_CONSUMED_ATTRIBUTE = "consumed";

    /** An empty {@link String} array **/
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    /** An empty {@link String} **/
    //$NON-NLS-1$
    public static final String EMPTY_STRING = "";

    /** The index of the label column in the tree */
    public static final int LABEL_COLUMN_INDEX = 0;

    /** The index of the value column in the tree */
    public static final int VALUE_COLUMN_INDEX = 1;

    /** The name of the .options file used to store the debug options for a bundle */
    //$NON-NLS-1$
    public static final String OPTIONS_FILENAME = ".options";

    /** The system property used to specify size a trace file can grow before it is rotated */
    //$NON-NLS-1$
    public static final String PROP_TRACE_SIZE_MAX = "eclipse.trace.size.max";

    /** The system property used to specify the maximum number of backup trace files to use */
    //$NON-NLS-1$
    public static final String PROP_TRACE_FILE_MAX = "eclipse.trace.backup.max";

    /** The separator character for a debug option represented as a string, i.e. key1=value1;key2=value2;key3=value3; */
    //$NON-NLS-1$
    public static final String DEBUG_OPTION_PREFERENCE_SEPARATOR = ";";

    /** The preference identifier for the tracing enablement state */
    //$NON-NLS-1$
    public static final String PREFERENCE_ENABLEMENT_IDENTIFIER = "tracingEnabled";

    /** The preference identifier for the list of tracing entries */
    //$NON-NLS-1$
    public static final String PREFERENCE_ENTRIES_IDENTIFIER = "tracingEntries";

    /** The preference identifier for the maximum size of the tracing files */
    //$NON-NLS-1$
    public static final String PREFERENCE_MAX_FILE_SIZE_IDENTIFIER = "tracingMaxFileSize";

    /** The preference identifier for the maximum number of tracing files */
    //$NON-NLS-1$
    public static final String PREFERENCE_MAX_FILE_COUNT_IDENTIFIER = "tracingMaxFileCount";

    /** The preference identifier for the location of tracing files */
    //$NON-NLS-1$
    public static final String PREFERENCE_FILE_PATH = "tracingFilePath";

    /** The preference identifier for standard output stream selection */
    //$NON-NLS-1$
    public static final String PREFERENCE_OUTPUT_STANDARD_STREAM = "tracingOutputStandard";
}
