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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.ecf.internal.sync.resources.core.SyncResourcesCore;
import org.osgi.service.prefs.Preferences;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

    public void initializeDefaultPreferences() {
        Preferences preferences = new DefaultScope().getNode(SyncResourcesCore.PLUGIN_ID);
        preferences.putInt(PreferenceConstants.LOCAL_RESOURCE_ADDITION, PreferenceConstants.COMMIT_VALUE);
        preferences.putInt(PreferenceConstants.LOCAL_RESOURCE_CHANGE, PreferenceConstants.COMMIT_VALUE);
        preferences.putInt(PreferenceConstants.LOCAL_RESOURCE_DELETION, PreferenceConstants.COMMIT_VALUE);
        preferences.putInt(PreferenceConstants.REMOTE_RESOURCE_ADDITION, PreferenceConstants.COMMIT_VALUE);
        preferences.putInt(PreferenceConstants.REMOTE_RESOURCE_CHANGE, PreferenceConstants.COMMIT_VALUE);
        preferences.putInt(PreferenceConstants.REMOTE_RESOURCE_DELETION, PreferenceConstants.COMMIT_VALUE);
    }
}
