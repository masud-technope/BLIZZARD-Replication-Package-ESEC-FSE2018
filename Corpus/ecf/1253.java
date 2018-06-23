/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.core.sharedobject.util;

import org.eclipse.ecf.core.util.Event;

public class QueueEnqueueImpl implements IQueueEnqueue {

    SimpleFIFOQueue queue = null;

    public  QueueEnqueueImpl(SimpleFIFOQueue impl) {
        super();
        this.queue = impl;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.util.IQueueEnqueue#enqueue(org.eclipse.ecf.core.util.Event)
	 */
    /**
	 * @param element
	 * @throws QueueException not thrown by this implementation.
	 */
    public void enqueue(Event element) throws QueueException {
        queue.enqueue(element);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.util.IQueueEnqueue#enqueue(org.eclipse.ecf.core.util.Event[])
	 */
    public void enqueue(Event[] elements) throws QueueException {
        if (elements != null) {
            for (int i = 0; i < elements.length; i++) {
                enqueue(elements[i]);
            }
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.util.IQueueEnqueue#enqueue_prepare(org.eclipse.ecf.core.util.Event[])
	 */
    /**
	 * @param elements
	 * @return Object
	 * @throws QueueException not thrown by this implementation.
	 */
    public Object enqueue_prepare(Event[] elements) throws QueueException {
        return elements;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.util.IQueueEnqueue#enqueue_commit(java.lang.Object)
	 */
    public void enqueue_commit(Object enqueue_key) {
        if (enqueue_key instanceof Event[]) {
            Event[] events = (Event[]) enqueue_key;
            try {
                enqueue(events);
            } catch (QueueException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.util.IQueueEnqueue#enqueue_abort(java.lang.Object)
	 */
    public void enqueue_abort(Object enqueue_key) {
    // Do nothing
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.util.IQueueEnqueue#enqueue_lossy(org.eclipse.ecf.core.util.Event)
	 */
    public boolean enqueue_lossy(Event element) {
        queue.enqueue(element);
        return true;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.util.IQueueEnqueue#setEnqueuePredicate(org.eclipse.ecf.core.util.IEnqueuePredicate)
	 */
    public void setEnqueueProcessor(IEnqueueProcessor pred) {
    // This queue does not support enqueue predicate
    // So we do nothing
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.util.IQueueEnqueue#getEnqueuePredicate()
	 */
    public IEnqueueProcessor getEnqueueProcessor() {
        // We don't support enqueue predicate, so return null;
        return null;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.util.IQueueEnqueue#size()
	 */
    public int size() {
        return queue.size();
    }
}
