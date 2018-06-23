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

/**
 * Models a thread message in a Bulletin Board forum.
 * 
 * Extends the IMessageBase interface with additional properties that are
 * specific to thread messages.
 * 
 * @author Erkki
 */
public interface IThreadMessage extends IMessageBase {

    /**
	 * Returns the thread that this message belongs to or null if it's unknown.
	 * 
	 * @return the thread
	 */
    public IThread getThread();

    /**
	 * Returns the number of this message in the thread or -1 if undetermined.
	 * 
	 * @return the number of the message in the thread
	 */
    public int getMessageNumber();
}
