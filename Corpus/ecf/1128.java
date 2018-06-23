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
package org.eclipse.ecf.examples.internal.eventadmin.app;

import java.io.NotSerializableException;
import org.eclipse.ecf.remoteservice.eventadmin.serialization.SerializationHandler;

public class ExampleSerializationHandler extends SerializationHandler {

    private static final String PREFIX = "ECF_ESH:";

    private final String topic;

    public  ExampleSerializationHandler(final String topic) {
        this.topic = topic;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.remoteservice.eventadmin.serialization.SerializationHandler#serialize(java.lang.Object)
	 */
    public Object serialize(Object val) throws NotSerializableException {
        if (val instanceof NonSerializable) {
            final NonSerializable ns = (NonSerializable) val;
            return PREFIX + ns.getPayload();
        }
        return super.serialize(val);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.remoteservice.eventadmin.serialization.SerializationHandler#deserialize(java.lang.Object)
	 */
    public Object deserialize(Object val) {
        if (val instanceof String) {
            final String str = (String) val;
            // poor mans serialization
            if (str.startsWith(PREFIX)) {
                return new NonSerializable(str.substring(PREFIX.length()));
            }
        }
        return super.deserialize(val);
    }

    public Object getTopic() {
        return topic;
    }
}
