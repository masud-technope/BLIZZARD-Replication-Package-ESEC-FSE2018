/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.start;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * Interface that must be implemented by extensions of the org.eclipse.ecf.start
 * extension point. Such extensions will have their start method called by a new
 * Job upon ECF startup.
 */
public interface IECFStart {

    /**
	 * Run some startup task.
	 * @param monitor 
	 * 
	 * @return IStatus the status of the start
	 */
    public IStatus run(IProgressMonitor monitor);
}
