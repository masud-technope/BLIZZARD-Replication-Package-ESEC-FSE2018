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
package org.eclipse.ecf.remoteservice.eventadmin.serialization;

import java.io.NotSerializableException;

/**
 * @since 1.2
 */
public abstract class SerializationHandler {

    public Object serialize(Object val) throws NotSerializableException {
        return val;
    }

    public Object deserialize(Object val) {
        return val;
    }

    public abstract Object getTopic();
}
