/*******************************************************************************
 * Copyright (c) 2011, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.trace.internal;

import org.eclipse.osgi.util.NLS;

/**
 * NLS messages for the Tracing preference page
 */
public class Messages extends NLS {

    /**
	 * The text to display if a component label is missing
	 */
    //$NON-NLS-1$
    public static String missingLabelValue = "missingLabelValue";

    /**
	 * Title of the tracing tree
	 */
    //$NON-NLS-1$
    public static String tracingTreeTile = "tracingTreeTitle";

    /**
	 * Description text for the preference page.
	 */
    //$NON-NLS-1$
    public static String preferencePageDescription = "preferencePageDescription";

    /**
	 * 'Enable tracing' button
	 */
    //$NON-NLS-1$
    public static String enableTracingButtonLabel = "enableTracingButtonLabel";

    /**
	 * 'Tracing Option' column header text
	 */
    //$NON-NLS-1$
    public static String columnHeaderTracingString = "columnHeaderTracingString";

    /**
	 * 'Value' column header text
	 */
    //$NON-NLS-1$
    public static String columnHeaderTracingValue = "columnHeaderTracingValue";

    /**
	 * 'Search for a trace string' filter text field value
	 */
    //$NON-NLS-1$
    public static String filterSearchText = "filterSearchText";

    /**
	 * 'Browse...' button
	 */
    //$NON-NLS-1$
    public static String tracingFileBrowseButton = "tracingFileBrowseButton";

    /**
	 * 'Tracing Options' group label
	 */
    //$NON-NLS-1$
    public static String tracingOptionsGroup = "tracingOptionsGroup";

    /**
	 * Maximum number of backup tracing files label
	 */
    //$NON-NLS-1$
    public static String tracingFileMaxCountLabel = "tracingFileMaxCountLabel";

    /**
	 * Maximum size of the tracing files label
	 */
    //$NON-NLS-1$
    public static String tracingFileMaxSizeLabel = "tracingFileMaxSizeLabel";

    /**
	 * 'Specify the tracing file' label
	 */
    //$NON-NLS-1$
    public static String tracingFileLabel = "tracingFileLabel";

    /**
	 * 'Invalid maximum value input' for the size field
	 */
    //$NON-NLS-1$
    public static String tracingFileInvalidMaxSize = "tracingFileInvalidMaxSize";

    /**
	 * An invalid empty tracing file was specified
	 */
    //$NON-NLS-1$
    public static String tracingFileInvalid = "tracingFileInvalid";

    /**
	 * 'Invalid maximum value input' for the count field
	 */
    //$NON-NLS-1$
    public static String tracingFileInvalidMaxCount = "tracingFileInvalidMaxSize";

    //$NON-NLS-1$
    private static final String BUNDLE_NAME = "org.eclipse.ui.trace.internal.messages";

    static {
        NLS.initializeMessages(Messages.BUNDLE_NAME, Messages.class);
    }

    public static String TracingComponentColumnEditingSupport_false;

    public static String TracingComponentColumnEditingSupport_true;

    public static String TracingPreferencePageStandardOutput;

    public static String TracingPreferencePage_applicationLaunchedInDebugModeWarning;
}
