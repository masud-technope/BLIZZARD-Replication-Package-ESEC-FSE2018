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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.core.identity.IIdentifiable;

/**
 * This interface contains getters for common base properties of all uniquely
 * identifiable bulletin board objects, which are: Forums, Threads, Messages,
 * Members, Member Groups and Polls.
 * 
 * Each of these objects has an identifier, a name and a URL, which usually
 * includes the identifier.
 * 
 * This interface should not be directly implemented.
 * 
 * @author Erkki
 */
public interface IBBObject extends IIdentifiable, IAdaptable {

    /**
	 * This object is read-only. The state and contents of it cannot be
	 * modified.
	 */
    public static final int READ_ONLY = 1;

    /**
	 * The state and contents of this object can be modified. It is possible
	 * that an object is in READ_WRITE mode, but some their attributes still
	 * cannot be modified.
	 * 
	 * For example, if you can post a reply in a thread, the thread may be in
	 * READ_WRITE mode, but it's possible that you can't change the thread's
	 * name.
	 */
    public static final int READ_WRITE = 2;

    /**
	 * Returns the name of the object. This might be the forum title or thread
	 * or message subject.
	 * 
	 * TODO perhaps this should be called "Title" instead, IBulletinBoard also
	 * has getTitle()
	 * 
	 * @return the object's name
	 */
    public String getName();

    /**
	 * Returns the open mode for this object. READ_ONLY or READ_WRITE, or -1 if
	 * the mode cannot be determined.
	 * 
	 * @return the open mode for this object
	 */
    public int getMode();
}
