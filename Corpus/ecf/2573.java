/*******************************************************************************
 * Copyright (c) 2009 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Chi Jian Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.sync.resources.core.preferences;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

    //$NON-NLS-1$
    public static final String LOCAL_RESOURCE_ADDITION = "localResourceAddition";

    //$NON-NLS-1$
    public static final String LOCAL_RESOURCE_CHANGE = "localResourceChange";

    //$NON-NLS-1$
    public static final String LOCAL_RESOURCE_DELETION = "localResourceDeletion";

    //$NON-NLS-1$
    public static final String REMOTE_RESOURCE_ADDITION = "remoteResourceAddition";

    //$NON-NLS-1$
    public static final String REMOTE_RESOURCE_CHANGE = "remoteResourceChange";

    //$NON-NLS-1$
    public static final String REMOTE_RESOURCE_DELETION = "remoteResourceDeletion";

    public static final int COMMIT_VALUE = 0;

    public static final int IGNORE_CONFLICTS_VALUE = 1;

    public static final int IGNORE_VALUE = 2;
}
