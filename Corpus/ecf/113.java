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
package org.eclipse.ecf.internal.provider.phpbb;

import java.util.Collections;
import java.util.List;
import org.eclipse.ecf.bulletinboard.BBException;
import org.eclipse.ecf.bulletinboard.IForum;
import org.eclipse.ecf.bulletinboard.IThread;
import org.eclipse.ecf.internal.provider.phpbb.identity.CategoryID;

public class Category extends Forum {

    public  Category(CategoryID id, String name) {
        super(id, name);
        this.mode = READ_ONLY;
    }

    protected void addSubForum(Forum forum) {
        subforums.add(forum);
    }

    public String getDescription() {
        // Descriptions not supported for categories.
        return null;
    }

    public int getType() {
        return IForum.HOLDS_FORUMS;
    }

    public List<IThread> getThreads() {
        return Collections.emptyList();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.tigris.bbapi.phpBB.Forum#createThread()
	 */
    @Override
    public IThread createThread() throws BBException {
        throw new BBException("This forum cannot hold threads.");
    }

    public boolean postThread(IThread thread) throws BBException {
        throw new BBException("This forum cannot hold threads.");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Category) {
            Category grp = (Category) obj;
            return id.equals(grp.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
