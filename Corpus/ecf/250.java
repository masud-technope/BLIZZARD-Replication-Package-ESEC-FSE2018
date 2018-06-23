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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.httpclient.NameValuePair;
import org.eclipse.ecf.bulletinboard.BBException;
import org.eclipse.ecf.bulletinboard.IBBCredentials;
import org.eclipse.ecf.bulletinboard.IForum;
import org.eclipse.ecf.bulletinboard.IMember;
import org.eclipse.ecf.bulletinboard.IThread;
import org.eclipse.ecf.bulletinboard.IThreadMessage;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.internal.bulletinboard.commons.AbstractBulletinBoard;
import org.eclipse.ecf.internal.bulletinboard.commons.webapp.GetRequest;
import org.eclipse.ecf.internal.bulletinboard.commons.webapp.PostRequest;
import org.eclipse.ecf.internal.bulletinboard.commons.webapp.WebRequest;
import org.eclipse.ecf.internal.provider.phpbb.container.PHPBBContainer;
import org.eclipse.ecf.internal.provider.phpbb.identity.MemberID;
import org.eclipse.ecf.internal.provider.phpbb.identity.ThreadID;
import org.eclipse.ecf.internal.provider.phpbb.identity.ThreadMessageID;

public class PHPBB extends AbstractBulletinBoard {

    public static final String P_SESSION_ID_COOKIE = "phpBB.sessionIdCookie";

    public static final String P_DATA_COOKIE = "phpBB.dataCookie";

    private Map<ID, Forum> cachedForums;

    private String sessionId;

    private String title;

    protected  PHPBB(PHPBBContainer mainContainer) {
        super(mainContainer);
    }

    protected void reset() {
        this.cachedForums = new HashMap<ID, Forum>();
        super.reset();
    }

    public void postConnect() {
        super.postConnect();
        parser = new PHPBBParser(namespace, url);
    }

    public void postDisconnect() {
        parser = null;
        super.postDisconnect();
    }

    /**
	 * @return Returns the sessionId.
	 */
    protected String getSessionId() {
        return sessionId;
    }

    public List<IForum> getForums() {
        if (cachedForums.isEmpty()) {
            GetRequest request = new GetRequest(httpClient, url, "");
            try {
                request.execute();
                String resp = request.getResponseBodyAsString();
                request.releaseConnection();
                cachedForums = getParser().parseForums(resp);
                for (Forum forum : cachedForums.values()) {
                    forum.setBulletinBoard(this);
                }
            } catch (IOException e) {
            }
        }
        return new ArrayList<IForum>(cachedForums.values());
    }

    public List<IForum> getTopLevelForums() {
        List<IForum> topForums = new ArrayList<IForum>();
        for (IForum forum : getForums()) {
            if (forum.getParentForum() == null) {
                topForums.add(forum);
            }
        }
        return topForums;
    }

    public IForum getForum(ID id) {
        if (cachedForums.isEmpty()) {
            getForums();
        }
        return cachedForums.get(id);
    }

    public PHPBBParser getParser() {
        return (PHPBBParser) this.parser;
    }

    @Override
    protected WebRequest createMemberListRequest() {
        return new GetRequest(httpClient, url, "memberlist.php");
    }

    @Override
    protected WebRequest createMemberPageRequest(ID id) {
        WebRequest request = new GetRequest(httpClient, url, "profile.php");
        NameValuePair params[] = { new NameValuePair("mode", "viewprofile"), new NameValuePair("u", String.valueOf(((MemberID) id).getLongValue())) };
        request.setParameters(params);
        return request;
    }

    @Override
    protected WebRequest createMemberGroupListRequest() {
        return new GetRequest(httpClient, url, "groupcp.php");
    }

    public boolean login(IBBCredentials credentials) throws BBException {
        PostRequest request = new PostRequest(httpClient, url, "login.php");
        NameValuePair params[] = { new NameValuePair("username", credentials.getUsername()), new NameValuePair("password", credentials.getPassword()), // disabled checkbox: new NameValuePair("autologin", "on"),
        new NameValuePair("redirect", ""), new NameValuePair("login", "Log in") };
        request.setParameters(params);
        try {
            request.execute();
            request.releaseConnection();
            Map<String, String> detectedCookies = PHPBBCookies.detectCookies(httpClient.getState().getCookies());
            if (detectedCookies.containsKey(PHPBBCookies.KEY_SESS_ID)) {
                // We have a session id
                sessionId = detectedCookies.get(PHPBBCookies.KEY_SESS_ID);
            }
            if (detectedCookies.containsKey(PHPBBCookies.KEY_USER_ID)) {
                // We have a user id
                ID id = new MemberFactory().createBBObjectId(getNamespace(), url, (String) detectedCookies.get(PHPBBCookies.KEY_USER_ID));
                if (id == null) {
                    return false;
                } else {
                    loggedInMemberId = id;
                    return true;
                }
            }
        } catch (Exception e) {
            throw new BBException(e);
        }
        return false;
    }

    public boolean logout() throws BBException {
        PostRequest request = new PostRequest(httpClient, url, "login.php");
        request.addParameter(new NameValuePair("logout", "true"));
        try {
            request.execute();
            request.releaseConnection();
            loggedInMemberId = null;
            return true;
        } catch (Exception e) {
            throw new BBException(e);
        }
    }

    public IThread getThread(ID id) throws BBException {
        GetRequest request = new GetRequest(httpClient, url, "viewtopic.php");
        request.addParameter(new NameValuePair("t", String.valueOf(((ThreadID) id).getLongValue())));
        String resp = null;
        try {
            request.execute();
            resp = request.getResponseBodyAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        request.releaseConnection();
        if (resp != null) {
            Thread t = getParser().parseThreadPageForThreadAttributes(resp);
            t.setBulletinBoard(this);
            return t;
        }
        return null;
    }

    public IThreadMessage getMessage(ID id) throws BBException {
        GetRequest request = new GetRequest(httpClient, url, "viewtopic.php");
        request.addParameter(new NameValuePair("p", String.valueOf(((ThreadMessageID) id).getLongValue())));
        String resp = null;
        try {
            request.execute();
            resp = request.getResponseBodyAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        request.releaseConnection();
        if (resp != null) {
            ThreadMessage msg = getParser().parseRequestedMessage((ThreadMessageID) id, resp);
            msg.setBulletinBoard(this);
            IMember author = msg.author;
            ((Member) author).setBulletinBoard(this);
            return msg;
        }
        return null;
    }

    public String getTitle() throws BBException {
        if (this.title == null) {
            GetRequest request = new GetRequest(httpClient, url, "");
            String resp = null;
            try {
                request.execute();
                resp = request.getResponseBodyAsString();
            } catch (IOException e) {
                throw new BBException(e);
            }
            request.releaseConnection();
            if (resp != null) {
                this.title = getParser().parseTitle(resp);
            }
        }
        return this.title;
    }
}
