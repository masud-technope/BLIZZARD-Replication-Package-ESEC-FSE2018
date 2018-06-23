/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.filetransfer.identity;

import java.net.*;
import org.eclipse.ecf.core.identity.ID;

/**
 * ID for a remote file.
 * 
 */
public interface IFileID extends ID {

    /**
	 * Get the file name from this IFileID. This will return just the filename
	 * portion of a more complex file ID, e.g. index.html from IFileID created
	 * with value &quot;http://www.composent.com/index.html&quot;
	 * 
	 * @return String just the file name and extension (if any) for this given
	 *         IFileID. Will not be <code>null</code>.
	 */
    public String getFilename();

    /**
	 * Get the url associated with the file identified by this IFileID.
	 * 
	 * @return URL associated with this IFileID.  Will not be null.
	 * @exception MalformedURLException thrown if URL cannot be created for this IFileID
	 */
    public URL getURL() throws MalformedURLException;

    /**
	 * Get the URI associated with the file identified by this IFileID.
	 * 
	 * @return URI associated with this IFileID.  Will not be <code>null</code>.
	 * @throws URISyntaxException thrown if URI cannot be created for this IFileID.
	 * @since 5.0
	 */
    public URI getURI() throws URISyntaxException;
}
