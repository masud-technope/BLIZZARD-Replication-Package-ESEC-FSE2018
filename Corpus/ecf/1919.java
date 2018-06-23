/*******************************************************************************
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.core.sharedobject.util;

import java.io.IOException;
import org.eclipse.ecf.core.identity.ID;

/**
 * Serialize/deserialize shared object messages.  Instances of this class can be
 * provided to an ISharedObjectContainer (via ISOC.setSharedObjectMessageSerializer) to
 * customize the shared object message serialization.  Note that the serializer has
 * to be symmetric, and able to deserialize objects of all relevant classes.
 * 
 * @since 2.0
 */
public interface ISharedObjectMessageSerializer {

    /**
     * <p>
	 * Serialize an object to byte array.  This method will be called every time a 
	 * shared object message is sent from one shared object instance to its replica.
	 * </p>
     * <p>
	 * Note that this method may be called many times, and should perform as rapidly
	 * as possible to support good marshalling performance.
	 * </p>
	 * @param sharedObjectID the ID for the sender shared object.  Will not be <code>null</code>.
	 * @param message the object to be serialized as the message.
	 * @return byte[] that can be deserialized by {@link #deserializeMessage(byte[])} into
	 * a corresponding object on a compatible container instance.  Must not be <code>null</code>.
	 * 
	 * @throws IOException thrown if data cannot be serialized.  Note that exceptions thrown will result in container-level disconnection.
	 */
    public byte[] serializeMessage(ID sharedObjectID, Object message) throws IOException;

    /**
    * <p>
	 * Deserialize a byte array into an object message.  This method will be called every
	 * time a shared object message is received, but before the resulting Object is
	 * delivered to the shared object.
	 * </p>
     * <p>
	 * Note that this method may be called many times, and should perform as rapidly
	 * as possible to support good marshalling performance.
	 * </p>
	 * @param data the data to use to deserialize.  Will not be <code>null</code>.
	 * @return Object that is the deserialized instance of the Object.
	 * 
	 * @throws IOException if some problem deserializing from given bytes.
	 * @throws ClassNotFoundException thrown if class encoded in byte[] cannot be found in 
	 * local runtime.  Note that exceptions thrown will result in container-level disconnection.
	 */
    public Object deserializeMessage(byte[] data) throws IOException, ClassNotFoundException;
}
