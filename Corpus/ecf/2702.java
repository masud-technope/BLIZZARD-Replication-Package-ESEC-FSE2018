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
import javax.servlet.http.HttpServletResponse;

/**
 * Remote call response serializer.
 *
 */
public interface IRemoteCallResponseSerializer {

    /**
	 * Serialize responseObject to HttpServletResponse
	 * 
	 * @param resp the HttpServletResponse object.  Will not be <code>null</code>.
	 * @param responseObject to serialize to resp
	 * @throws IOException if resposeObject cannot be serialized
	 * @throws ServletException if responseObject cannot be serialized
	 */
    public void serializeResponse(HttpServletResponse resp, Object responseObject) throws IOException, ServletException;
}
