/****************************************************************************
 * Copyright (c) 2013 Markus Alexander Kuppe and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Markus Alexander Kuppe - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.remoteservice.eventadmin;

import java.io.Externalizable;
import java.io.NotSerializableException;
import java.io.Serializable;
import org.eclipse.ecf.remoteservice.eventadmin.serialization.SerializationHandler;

/**
 * The default is to serialize what is already serialized or externalizable
 * and fail fast for the rest
 */
public class DefaultSerializationHandler extends SerializationHandler {

    public static final SerializationHandler INST = new DefaultSerializationHandler();

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.remoteservice.eventadmin.serialization.SmartSerializationHandler#serialize(java.lang.Object)
	 */
    public Object serialize(Object val) throws NotSerializableException {
        if (!(val instanceof Serializable || val instanceof Externalizable)) {
            throw new NotSerializableException("Cannot serialize property value=" + val);
        }
        return val;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.remoteservice.eventadmin.serialization.SmartSerializationHandler#deserialize(java.lang.Object)
	 */
    public Object deserialize(Object val) {
        return super.deserialize(val);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.remoteservice.eventadmin.serialization.SerializationHandler#getTopic()
	 */
    public Object getTopic() {
        // invalid characters will make sure that topic is unique
        return "/ECF__NoSuchTopicNoSuchName__ECF/";
    }
}
