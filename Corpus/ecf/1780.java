/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.util;

import org.eclipse.core.runtime.IStatus;

/**
 * Contract for general exception handler
 */
public interface IExceptionHandler {

    /** 
	 * Handle given exception 
	 * @param exception the exception to handle. If null, no exception occurred.
	 * @return IStatus any status to return as part of asynchronous job.  Should not be null.
	 */
    public IStatus handleException(Throwable exception);
}
