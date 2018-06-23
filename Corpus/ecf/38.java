/*******************************************************************************
 * Copyright (c) 2005, 2006 Erkki Lindpere and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Erkki Lindpere - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.bulletinboard;

import java.util.Collection;
import java.util.List;

/**
 * This interface models a forum or a forum category.
 * 
 * Some bulletin boards support flexible forum structures, including forums
 * inside other forums; some support forum categories that can contain forums,
 * but no forums inside other forums. Implementations should model categories as
 * forums that do not have the HOLDS_FORUMS type.
 * 
 * Two different forum instances representing the same forum must be equal.
 * 
 * @author Erkki
 */
public interface IForum extends IBBObject {

    /**
	 * This forum can contain other forums.
	 */
    public static final int HOLDS_FORUMS = 1;

    /**
	 * This forum can contain messages.
	 */
    public static final int HOLDS_THREADS = 2;

    /**
	 * Returns the description of this forum.
	 * 
	 * @return the description of this forum
	 */
    public String getDescription();

    /**
	 * Returns the type of this forum, that is, whether this forum can hold
	 * threads or subforums or both.
	 * 
	 * @return the type of this forum
	 */
    public int getType();

    /**
	 * Returns the parent of this forum or null if this is a top-level forum.
	 * 
	 * @return the parent of this forum or null
	 */
    public IForum getParentForum();

    /**
	 * Returns the subforums of this forum or an empty list if there are none.
	 * 
	 * Must return all the subforums that exist in this forum.
	 * 
	 * @return subforums
	 */
    public List getSubForums();

    /**
	 * Returns the threads in this forum or an empty list if there are none.
	 * 
	 * Note: this method may not return all threads that exist in the forum.
	 * 
	 * @return threads
	 */
    public Collection getThreads();

    /**
	 * Returns a newly created thread that can be filled and posted in this
	 * forum.
	 * 
	 * @return a newly created thread
	 * @throws IllegalWriteException
	 *             if the forum is read-only.
	 * @throws BBException
	 *             if the forum cannot contain threads
	 */
    public IThread createThread() throws IllegalWriteException, BBException;

    /**
	 * Posts a new thread in this forum.
	 * 
	 * @param thread
	 *            the thread to post.
	 * @return true if the post was successful, false otherwise
	 * @throws IllegalWriteException
	 *             if the forum is read-only.
	 * @throws BBException
	 *             if the post failed.
	 */
    public boolean postThread(IThread thread) throws IllegalWriteException, BBException;
}
