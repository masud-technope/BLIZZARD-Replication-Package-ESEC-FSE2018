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
 * Model synchronization strategy contract. Model synchronization strategy
 * instances are used to synchronize replicated instances of an arbitrary model.
 * This is done by creating instances implementing this interface on
 * participating processes, and then using them in the following manner.
 * <p>
 * First, assume for simplicity that there are two processes (A and B), each
 * with a replica of a given model (a and b). Also assume that prior to using a
 * synchronization strategy that the model is accurately and reliably replicated
 * to A and B (i.e. a == b). Further, assume that for both A and B an instance
 * of IModelSynchronizationStrategy is created (call them Sa and Sb) prior to
 * any changes being made to either a or b.
 * </p>
 * <p>
 * On process A assume that the user makes a change to a via a (local) editor.
 * Then the expected sequence of activities is as follows:
 * </p>
 * <ul>
 * <li>Process A should synchronously call
 * {@link #registerLocalChange(IModelChange)}. The IModelChange instance
 * provided must be 1) not <code>null</code>; and 2) Of a type that is
 * appropriate for this synchronization strategy.</li>
 * <li>Process A should take the resulting {@link IModelChangeMessage},
 * serialize it (by calling {@link IModelChangeMessage#serialize()} and send the
 * message to remote processes (i.e. B).</li>
 * <li>
 * Process B should take the received byte array and call
 * {@link #deserializeRemoteChange(byte[])} to create an IModelChange instance.</li>
 * <li>
 * Process B should then pass the IModelChange instance to
 * {@link #transformRemoteChange(IModelChange)}. The synchronization
 * implementation will then return an array of IModelChange instances
 * (IModelChange []). These IModelChange instance should then be cast to the
 * appropriate type, and applied to the local instance of the model (i.e. b).
 * The {@link #transformRemoteChange(IModelChange)} metho will take the local
 * changes (previously registered with the synchronization strategy via
 * {@link #registerLocalChange(IModelChange)}), and the remote changes provided
 * via {@link #transformRemoteChange(IModelChange)}, and transform the changes
 * into a set that will result in a synchronized local copy.</li>
 * </ul>
 * <p>
 * Note that clients should generally call the
 * {@link #registerLocalChange(IModelChange)} and apply the IModelChanges
 * returned from {@link #transformRemoteChange(IModelChange)} on the same thread
 * that is responsible for modifying the underlying model. For example, if a
 * document model is modified by an editor (via UI thread) then the UI thread
 * should also synchronously call {@link #registerLocalChange(IModelChange)},
 * and the changes from {@link #transformRemoteChange(IModelChange)} should also
 * be applied to the local document from within this same thread.
 * </p>
 * 
 * @since 2.1
 */
public interface IModelSynchronizationStrategy extends IAdaptable {

    /**
	 * Register local model change with synchronization strategy. This method
	 * should be synchronously called when a local model change has been made to
	 * the underlying model.
	 * 
	 * @param localChange
	 *            the IModelChange made to the local model. Must be non-
	 *            <code>null</code>, and must be of type appropriate for the
	 *            synchronization strategy.
	 * @return IModelChangeMessage[] an array of change message to be delivered
	 *         to remote participants. If no change message can be created for
	 *         this local change (e.g. because the localChange is not of the
	 *         expected type, or because the change is not to be propogated to
	 *         remotes, then an empty array will be returned).
	 */
    public IModelChangeMessage[] registerLocalChange(IModelChange localChange);

    /**
	 * Transform remote change into a set of local changes to be synchronously
	 * applied to the local model.
	 * 
	 * @param remoteChange
	 *            the remote model change instance to be transformed by this
	 *            synchronization strategy.
	 * @return IModelChange[] to apply to local model.
	 */
    public IModelChange[] transformRemoteChange(IModelChange remoteChange);

    /**
	 * Deserialization of given byte array to concrete instance of IModelChange
	 * object to represent local change to be applied
	 * 
	 * @param bytes
	 *            the bytes to be deserialized.
	 * @return IModelChange instance from bytes. Will not be <code>null</code>.
	 * @throws SerializationException
	 *             thrown if some problem deserializing given bytes.
	 */
    public IModelChange deserializeRemoteChange(byte[] bytes) throws SerializationException;
}
