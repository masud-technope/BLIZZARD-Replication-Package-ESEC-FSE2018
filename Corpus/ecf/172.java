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
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.httpclient.NameValuePair;
import org.eclipse.ecf.bulletinboard.BBException;
import org.eclipse.ecf.bulletinboard.IThreadMessage;
import org.eclipse.ecf.internal.bulletinboard.commons.AbstractBulletinBoard;
import org.eclipse.ecf.internal.bulletinboard.commons.webapp.PostRequest;
import org.eclipse.ecf.internal.bulletinboard.commons.webapp.WebRequest;
import org.eclipse.ecf.internal.provider.phpbb.identity.ThreadID;

public class ThreadBrowser2 {

    Thread thread;

    private AbstractBulletinBoard bb;

    public  ThreadBrowser2(AbstractBulletinBoard bb, Thread thread) {
        this.bb = bb;
        this.thread = thread;
    }

    private static final int STARTPAGE = 1;

    private static final int NONE = -1;

    public List<IThreadMessage> fetchNewMessages() throws BBException {
        List<IThreadMessage> messages = new ArrayList<IThreadMessage>();
        try {
            int nextPage = STARTPAGE;
            while (nextPage > NONE) {
                WebRequest req = createRequest(nextPage);
                req.execute();
                String resp = req.getResponseBodyAsString();
                req.releaseConnection();
                // Add messages from page
                messages.addAll(0, ((PHPBBParser) bb.getParser()).parseMessages2(resp, thread.lastReadMessageId, true));
                nextPage = ((PHPBBParser) bb.getParser()).parseNextPage(resp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public WebRequest createRequest(int page) {
        /*
		 * String ppStr =
		 * bb.getActiveConfiguration().getProperties().getProperty(
		 * IBBConfiguration.P_POSTS_PER_PAGE);
		 */
        int pp = 15;
        int start = (page - 1) * pp;
        WebRequest req = new PostRequest(bb.getHttpClient(), bb.getURL(), "viewtopic.php?t=" + ((ThreadID) thread.getID()).getLongValue() + "&start=" + start);
        req.addParameter(new NameValuePair("postorder", "desc"));
        req.addParameter(new NameValuePair("postdays", "0"));
        req.addParameter(new NameValuePair("submit", "Go"));
        return req;
    }
}
