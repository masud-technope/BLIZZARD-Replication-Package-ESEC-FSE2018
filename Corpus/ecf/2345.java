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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.httpclient.NameValuePair;
import org.eclipse.ecf.bulletinboard.BBException;
import org.eclipse.ecf.bulletinboard.IBBCredentials;
import org.eclipse.ecf.bulletinboard.IForum;
import org.eclipse.ecf.bulletinboard.IThread;
import org.eclipse.ecf.bulletinboard.IThreadMessage;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.internal.bulletinboard.commons.AbstractBulletinBoard;
import org.eclipse.ecf.internal.bulletinboard.commons.webapp.GetRequest;
import org.eclipse.ecf.internal.bulletinboard.commons.webapp.PostRequest;
import org.eclipse.ecf.internal.bulletinboard.commons.webapp.WebRequest;
import org.eclipse.ecf.internal.provider.vbulletin.container.VBContainer;
import org.eclipse.ecf.internal.provider.vbulletin.identity.MemberID;
import org.eclipse.ecf.internal.provider.vbulletin.identity.ThreadID;
import org.eclipse.ecf.internal.provider.vbulletin.internal.MemberFactory;
import org.eclipse.ecf.internal.provider.vbulletin.internal.VBCookies;

public class VBulletin extends AbstractBulletinBoard {

    private String sessionId;

    private String title;

    private Map<ID, Forum> cachedForums;

    public  VBulletin(VBContainer mainContainer) {
        super(mainContainer);
    }

    protected void reset() {
        this.cachedForums = new HashMap<ID, Forum>();
        super.reset();
    }

    public void postConnect() {
        super.postConnect();
        parser = new VBParser(namespace, url);
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

    public List<IForum> getForums() throws BBException {
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

    public List<IForum> getTopLevelForums() throws BBException {
        List<IForum> topForums = new ArrayList<IForum>();
        for (IForum forum : getForums()) {
            if (forum.getParentForum() == null) {
                topForums.add(forum);
            }
        }
        return topForums;
    }

    public IForum getForum(ID id) throws BBException {
        if (cachedForums.isEmpty()) {
            getForums();
        }
        return cachedForums.get(id);
    }

    public IThread getThread(ID id) throws BBException {
        GetRequest request = new GetRequest(httpClient, url, "showthread.php");
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected WebRequest createMemberListRequest() {
        return new GetRequest(httpClient, url, "memberlist.php");
    }

    @Override
    protected WebRequest createMemberPageRequest(ID id) {
        WebRequest request = new GetRequest(httpClient, url, "member.php");
        NameValuePair params[] = { new NameValuePair("u", String.valueOf(((MemberID) id).getLongValue())) };
        request.setParameters(params);
        return request;
    }

    public boolean login(IBBCredentials credentials) throws BBException {
        PostRequest request = new PostRequest(httpClient, url, "login.php");
        NameValuePair params[] = { new NameValuePair("vb_login_username", credentials.getUsername()), new NameValuePair("cookieuser", "1"), new NameValuePair("vb_login_password", credentials.getPassword()), new NameValuePair("submit", "Login"), new NameValuePair("s", ""), new NameValuePair("do", "login"), new NameValuePair("forceredirect", "0"), new NameValuePair("vb_login_md5password", ""), new NameValuePair("vb_login_md5password_utf", "") };
        request.setParameters(params);
        try {
            request.execute();
            request.releaseConnection();
            Map<String, String> detectedCookies = VBCookies.detectCookies(httpClient.getState().getCookies());
            if (detectedCookies.containsKey(VBCookies.KEY_SESS_ID)) {
                // We have a session id
                sessionId = detectedCookies.get(VBCookies.KEY_SESS_ID);
            }
            if (detectedCookies.containsKey(VBCookies.KEY_USER_ID)) {
                // We have a user id
                ID id = new MemberFactory().createBBObjectId(namespace, url, (String) detectedCookies.get(VBCookies.KEY_USER_ID));
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
        PostRequest request = new PostRequest(httpClient, url, "logout.php");
        try {
            request.execute();
            request.releaseConnection();
            loggedInMemberId = null;
            return true;
        } catch (Exception e) {
            throw new BBException(e);
        }
    }

    public VBParser getParser() {
        return (VBParser) parser;
    }

    @Override
    protected WebRequest createMemberGroupListRequest() {
        WebRequest request = new GetRequest(httpClient, url, "profile.php");
        request.addParameter(new NameValuePair("do", "editusergroups"));
        return request;
    }
}
