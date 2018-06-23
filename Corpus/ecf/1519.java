/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.sync;

import org.eclipse.core.runtime.IAdaptable;

/**
 * Change message.  Instances of this interface
 * may be serialized to a byte [] so that they can be
 * communicated to remote processes.
 * 
 * @since 2.1
 */
public interface IModelChangeMessage extends IAdaptable {

    /**
	 * Serialize this message to byte [].
	 * @return byte [] that is serialized representation of this model change message.
	 * @throws SerializationException if this model change message
	 * cannot be serialized.
	 */
    public byte[] serialize() throws SerializationException;
}
