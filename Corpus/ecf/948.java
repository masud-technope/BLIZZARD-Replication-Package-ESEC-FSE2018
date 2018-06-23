/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.identity;

import java.net.URI;

/**
 * Resource id. ID instances that implement this interface are expected to be
 * resources (files, directories, URLs, etc) and so can be identified via a
 * {@link URI}.
 * 
 * @since 3.0
 * 
 */
public interface IResourceID extends ID {

    /**
	 * Convert this resource ID to a {@link URI}.
	 * 
	 * @return URI for this resource ID
	 */
    public URI toURI();
}
