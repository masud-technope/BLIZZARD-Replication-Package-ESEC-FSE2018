/*******************************************************************************
 * Copyright (c) 2006, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.ui;

import java.io.File;
import org.eclipse.pde.ui.target.ITargetLocationWizard;

@Deprecated
public interface IProvisionerWizard extends IBasePluginWizard {

    /**
	 * Returns an array of locations which contain plug-ins to be added to
	 * the Target Platform.  If a location contains a "plugins" subdirectory,
	 * the subdirectory will be searched for plug-ins.  Otherwise, the location
	 * itself will be searched for new plug-ins.
	 *
	 * @return an array of Files which represent the locations to search for
	 * new plug-ins.
	 */
    public File[] getLocations();
}
