/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.sharedobject.util;

import org.eclipse.ecf.core.util.Event;

/**
 * Queue enqueing semantics
 * 
 */
public interface IQueueEnqueue {

    /**
	 * Enqueue a given Event onto the underlying queue
	 * 
	 * @param event
	 *            the Event to enqueue
	 * @throws QueueException
	 *             thrown if event cannot be enqueued
	 */
    void enqueue(Event event) throws QueueException;

    /**
	 * Enqueue a set of events Event onto the underlying queue
	 * 
	 * @param events
	 *            the Events to enqueue
	 * @throws QueueException
	 *             thrown if events cannot be enqueued
	 */
    void enqueue(Event[] events) throws QueueException;

    /**
	 * Prepare an enqueue of a set of Event instances. The Object returned
	 * should subsequently be used to either commit the prepared enqueue
	 * transaction ({@link #enqueue_commit(Object)}, or to abort the prepared
	 * enqueue transaction {@link #enqueue_abort(Object)}
	 * 
	 * @param events
	 *            the events to enqueue
	 * @return Object representing the transaction
	 * @throws QueueException
	 *             if preparation for enqueue cannot occur
	 */
    Object enqueue_prepare(Event[] events) throws QueueException;

    /**
	 * Commit a set of Event instances previously prepared via
	 * {@link #enqueue_prepare(Event[])}
	 * 
	 * @param enqueue_key
	 *            the transaction key previously returned from the call to
	 *            {@link #enqueue_prepare(Event[])}
	 */
    void enqueue_commit(Object enqueue_key);

    /**
	 * Abort the commit of a set of Event instances previously prepared via
	 * {@link #enqueue_prepare(Event[])}
	 * 
	 * @param enqueue_key
	 *            the transaction key previously returned from the call to
	 *            {@link #enqueue_prepare(Event[])}
	 */
    void enqueue_abort(Object enqueue_key);

    /**
	 * Enqueue the given event with lossy enqueuing.
	 * 
	 * @param event
	 *            the event to
	 * @return true if enqueued successfully, false if not
	 */
    boolean enqueue_lossy(Event event);

    /**
	 * Set enqueue processor for this queue. The given processor, if non-null,
	 * will be consulted when and enqueue operation is requested to determine
	 * whether the enqueue should occur or not
	 * 
	 * @param processor
	 *            the IEnqueueProcessor for this queue. Should not be null.
	 */
    void setEnqueueProcessor(IEnqueueProcessor processor);

    /**
	 * Get enqueue processor for this queue. Returns the enqueue processor
	 * previously assigned via the
	 * {@link #setEnqueueProcessor(IEnqueueProcessor)}. Returns null if no
	 * enqueue processor previously assigned
	 * 
	 * @return IEnqueueProcessor previously assigned via
	 *         {@link #setEnqueueProcessor(IEnqueueProcessor)}
	 */
    IEnqueueProcessor getEnqueueProcessor();

    /**
	 * Return size of contents in queue
	 * 
	 * @return int size of queue contents. Zero if empty
	 */
    int size();
}
