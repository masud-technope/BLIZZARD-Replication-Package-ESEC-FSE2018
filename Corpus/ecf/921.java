/*******************************************************************************
 * Copyright (c) 2010-2011 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.osgi.services.remoteserviceadmin;

import java.io.IOException;
import java.io.InputStream;

/**
 * Service for reading endpoint descriptions from xml-files in the Endpoint
 * Description Extender Format (EDEF) specified in section 122.8 of the <a
 * href="http://www.osgi.org/download/r4v42/r4.enterprise.pdf">OSGi Enterprise
 * Specification (chapter 122)</a>. The InputStream provided must be of the EDEF
 * format, otherwise an IOException or EndpointDescriptionParseException will be
 * thrown.
 * 
 */
public interface IEndpointDescriptionReader {

    /**
	 * Read endpoint descriptions from the given input stream. The ins parameter
	 * must not be <code>null</code>, and must provide data in the Endpoint
	 * Description Extender Format (EDEF) specified in section 122.8 of the <a
	 * href="http://www.osgi.org/download/r4v42/r4.enterprise.pdf">OSGi
	 * Enterprise Specification</a>.
	 * 
	 * @param ins
	 *            the input stream to read from. Must be non-<code>null</code>,
	 *            and must provide data in the format specified the EDEF
	 *            specification (see link above).
	 * @return array of
	 *         {@link org.osgi.service.remoteserviceadmin.EndpointDescription}
	 *         instance read from the given input stream.
	 * 
	 * @throws IOException
	 *             if the inputstream does not have valid data in the EDE
	 *             format. Note that the implementation of this method may call
	 *             {@link InputStream#close()}.
	 * 
	 * @throws EndpointDescriptionParseException
	 *             if the EDE format cannot be parsed from the input stream.
	 */
    public org.osgi.service.remoteserviceadmin.EndpointDescription[] readEndpointDescriptions(InputStream ins) throws IOException, EndpointDescriptionParseException;
}
