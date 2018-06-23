/*******************************************************************************
* Copyright (c) 2013 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * Remote call parameter deserializer.
 *
 */
public interface IRemoteCallParameterDeserializer {

    /**
	 * Deserialize parameters from HttpServletRequest.
	 * 
	 * @param req the HttpServletRequest.  Will not be <code>null</code>.
	 * @return Object[] the deserialized parameters.
	 * @throws IOException if parameters cannot be deserialized
	 * @throws ServletException if parameters cannot be deserialized
	 */
    public Object[] deserializeParameters(HttpServletRequest req) throws IOException, ServletException;
}
