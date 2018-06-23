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

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.httpclient.NameValuePair;
import org.eclipse.ecf.bulletinboard.BBException;
import org.eclipse.ecf.bulletinboard.IForum;
import org.eclipse.ecf.bulletinboard.IThread;
import org.eclipse.ecf.bulletinboard.IllegalWriteException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.internal.bulletinboard.commons.AbstractBBObject;
import org.eclipse.ecf.internal.bulletinboard.commons.webapp.GetRequest;
import org.eclipse.ecf.internal.bulletinboard.commons.webapp.PostRequest;
import org.eclipse.ecf.internal.bulletinboard.commons.webapp.WebRequest;
import org.eclipse.ecf.internal.provider.phpbb.identity.ForumID;

public class Forum extends PHPBBObject implements IForum {

    private static final String E_READ_ONLY = "This forum is read only.";

    private Category parent;

    protected ArrayList<IForum> subforums;

    private String description;

    protected ForumID id;

    public  Forum(ForumID id, String name) {
        super(name, READ_WRITE);
        this.id = id;
        this.parent = null;
        this.subforums = new ArrayList<IForum>();
    }

    public ID getID() {
        return id;
    }

    protected void setParent(Category cat) {
        this.parent = cat;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getType() {
        return IForum.HOLDS_THREADS;
    }

    public IForum getParentForum() {
        return parent;
    }

    public List<IForum> getSubForums() {
        return subforums;
    }

    public Collection<IThread> getThreads() {
        PHPBBParser parser = (PHPBBParser) bb.getParser();
        Map<ID, IThread> threadMap = null;
        try {
            WebRequest request = new GetRequest(bb.getHttpClient(), new URL(id.toExternalForm()), "");
            request.addParameter(new NameValuePair("f", String.valueOf(id.getLongValue())));
            request.execute();
            String resp = request.getResponseBodyAsString();
            request.releaseConnection();
            threadMap = parser.parseThreads(resp);
            for (IThread thread : threadMap.values()) {
                ((AbstractBBObject) thread).setBulletinBoard(bb);
                ((Thread) thread).forum = this;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashSet<IThread>(threadMap.values());
    }

    public IThread createThread() throws IllegalWriteException, BBException {
        if ((mode & READ_ONLY) == READ_ONLY) {
            throw new IllegalWriteException(E_READ_ONLY);
        }
        Thread thread = new Thread();
        thread.setBulletinBoard(bb);
        thread.forum = this;
        return thread;
    }

    public boolean postThread(IThread thread) throws IllegalWriteException, BBException {
        if ((mode & READ_ONLY) == READ_ONLY) {
            throw new IllegalWriteException(E_READ_ONLY);
        }
        WebRequest request = new PostRequest(bb.getHttpClient(), bb.getURL(), "posting.php");
        NameValuePair params[];
        params = new NameValuePair[] { new NameValuePair("subject", thread.getPrePostMessage().getName()), new NameValuePair("message", thread.getPrePostMessage().getMessage()), new NameValuePair("f", String.valueOf(id.getLongValue())), new NameValuePair("mode", "newtopic"), // checkbox : disabled new NameValuePair("notify", "on"),
        new NameValuePair("post", "Submit") };
        request.addParameters(params);
        // We seem to always have to get the response body.
        try {
            request.execute();
            request.getResponseBodyAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        request.releaseConnection();
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Forum) {
            Forum grp = (Forum) obj;
            return id.equals(grp.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
