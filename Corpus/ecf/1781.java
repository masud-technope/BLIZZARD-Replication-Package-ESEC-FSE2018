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
package org.eclipse.ecf.internal.provider.vbulletin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.ecf.bulletinboard.BBException;
import org.eclipse.ecf.bulletinboard.IThreadMessage;
import org.eclipse.ecf.internal.bulletinboard.commons.webapp.PostRequest;
import org.eclipse.ecf.internal.bulletinboard.commons.webapp.WebRequest;
import org.eclipse.ecf.internal.provider.vbulletin.identity.ThreadID;

/**
 * TODO This threadbrowser assumes the user account settings to have been set to
 * the reverse post sorting order. This option should be detected instead.
 * 
 * @author Erkki
 * 
 */
public class ThreadBrowser2 {

    Thread thread;

    private VBulletin bb;

    public  ThreadBrowser2(VBulletin bb, Thread thread) {
        this.bb = bb;
        this.thread = thread;
    }

    private static final int STARTPAGE = 1;

    private static final int NONE = -1;

    class SkippedStatus {

        public boolean messagesSkipped = false;
    }

    ;

    public List<IThreadMessage> fetchNewMessages() throws BBException {
        List<IThreadMessage> messages = new ArrayList<IThreadMessage>();
        try {
            int nextPage = STARTPAGE;
            SkippedStatus skipped = new SkippedStatus();
            while (nextPage > NONE) {
                WebRequest req = createRequest(nextPage);
                req.execute();
                String resp = req.getResponseBodyAsString();
                req.releaseConnection();
                messages.addAll(0, bb.getParser().parseMessages2(resp, thread.lastReadMessageId, true, skipped));
                if (skipped.messagesSkipped) {
                    nextPage = NONE;
                } else {
                    nextPage = bb.getParser().parseNextPage(resp);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public WebRequest createRequest(int page) {
        WebRequest req = new PostRequest(bb.getHttpClient(), bb.getURL(), "showthread.php?t=" + ((ThreadID) thread.getID()).getLongValue() + "&page=" + page);
        return req;
    }
}
