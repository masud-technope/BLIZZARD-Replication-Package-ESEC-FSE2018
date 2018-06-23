/****************************************************************************
 * Copyright (c) 2004, 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.remoteservice;

import org.osgi.framework.Filter;

/**
 * Filter for remote service references.
 *
 */
public interface IRemoteFilter extends Filter {

    /**
	 * Filter using a remote service's properties.
	 * <p>
	 * The filter is executed using the keys and values of the referenced
	 * service's properties. The keys are case insensitively matched with the
	 * filter.
	 * 
	 * @param reference
	 *            The reference to the service whose properties are used in the
	 *            match.
	 * 
	 * @return <code>true</code> if the service's properties match this
	 *         filter; <code>false</code> otherwise.
	 */
    public boolean match(IRemoteServiceReference reference);
}
