/*******************************************************************************
* Copyright (c) 2009 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice.client;

import java.io.NotSerializableException;
import java.util.Map;
import org.eclipse.ecf.remoteservice.IRemoteCall;

/**
 * Deserializer for processing call response objects.
 * 
 * @since 4.0
 */
public interface IRemoteResponseDeserializer {

    /**
	 * Deserialize remote response.
	 * 
	 * @param endpoint the endpoint.  Should not be <code>null</code>.
	 * @param call the call associated with the parameter to serialize.  Will not be <code>null</code>.
	 * @param callable the callable associated with the parameter to serialize.  Will not be <code>null</code>.
	 * @param responseHeaders response headers associated with the successful remote call.  May be <code>null</code>.
	 * @param responseBody the actual response body to deserialize.  May be <code>null</code>.
	 * @return Object the deserialized response.  May be <code>null</code>.
	 * @throws NotSerializableException thrown if the responseBody cannot be deserialized.
	 * @since 8.0
	 */
    public Object deserializeResponse(String endpoint, IRemoteCall call, IRemoteCallable callable, Map responseHeaders, byte[] responseBody) throws NotSerializableException;
}
