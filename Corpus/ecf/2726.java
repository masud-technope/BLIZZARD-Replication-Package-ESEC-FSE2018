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
import org.eclipse.ecf.remoteservice.IRemoteCall;

/**
 * Call parameter serializer.  Represents the serializer for remote call parameters.
 * 
 * @since 4.0
 */
public interface IRemoteCallParameterSerializer {

    /**
	 * Serialize a remote call parameter.
	 * 
	 * @param endpoint the endpoint.  Should not be <code>null</code>.
	 * @param call the call associated with the parameter to serialize.  Will not be <code>null</code>.
	 * @param callable the callable associated with the parameter to serialize.  Will not be <code>null</code>.
	 * @param paramDefault the default value (from the callable), for the parameter to serialize.
	 * @param paramToSerialize the actual parameter value to serialize.
	 * @return IRemoteCallParameter the serialized parameter...with appropriate name and serialized value.
	 * @throws NotSerializableException if parameter cannot be serialized.
	 */
    public IRemoteCallParameter serializeParameter(String endpoint, IRemoteCall call, IRemoteCallable callable, IRemoteCallParameter paramDefault, Object paramToSerialize) throws NotSerializableException;

    /**
	 * Serializes all remote call parameters of this call. This method is invoked after 
	 * {@link IRemoteCallParameter}{@link #serializeParameter(String, IRemoteCall, IRemoteCallable, IRemoteCallParameter, Object)} 
	 * and allows to override its results (read override IRemoteCallParameters).
	 * 
	 * @param endpoint the endpoint.  Should not be <code>null</code>.
	 * @param call the call associated with the parameter to serialize.  Will not be <code>null</code>.
	 * @param callable the callable associated with the parameter to serialize.  Will not be <code>null</code>.
	 * @param currentParameters the list of current parameters (from the callable) that would be send
	 * @param paramToSerialize all parameters to serialize.
	 * @return List the serialized parameters...with appropriate name and serialized value.
	 * @throws NotSerializableException if a parameter cannot be serialized.
	 * @since 8.0
	 * 
	 * @see "https://bugs.eclipse.org/408034"
	 * @noreference This method is not intended to be referenced by clients.
	 */
    public IRemoteCallParameter[] serializeParameter(String endpoint, IRemoteCall call, IRemoteCallable callable, IRemoteCallParameter[] currentParameters, Object[] paramToSerialize) throws NotSerializableException;
}
