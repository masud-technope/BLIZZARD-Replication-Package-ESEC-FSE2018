package org.eclipse.ecf.internal.bulletinboard.commons;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ecf.bulletinboard.BBException;
import org.eclipse.ecf.bulletinboard.IBulletinBoardContainerAdapter;
import org.eclipse.ecf.bulletinboard.IMember;
import org.eclipse.ecf.bulletinboard.IMemberGroup;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.internal.bulletinboard.commons.webapp.WebRequest;

public abstract class AbstractBulletinBoard implements IBulletinBoardContainerAdapter {

    private final AbstractBBContainer mainContainer;

    protected AbstractParser parser;

    protected URL url;

    protected Namespace namespace;

    protected HttpClient httpClient;

    protected ID loggedInMemberId;

    protected Map<ID, IMember> cachedMembers;

    protected Map<ID, IMemberGroup> cachedMemberGroups;

    public  AbstractBulletinBoard(AbstractBBContainer mainContainer) {
        super();
        this.mainContainer = mainContainer;
        this.namespace = mainContainer.getConnectNamespace();
        reset();
    }

    protected void reset() {
        this.url = null;
        this.cachedMembers = new HashMap<ID, IMember>();
        this.cachedMemberGroups = new HashMap<ID, IMemberGroup>();
    }

    public void postConnect() {
        try {
            this.url = new URL(getID().toExternalForm());
            final MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
            httpClient = new HttpClient(connectionManager);
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void postDisconnect() {
        httpClient = null;
        reset();
    }

    /**
	 * @return <code>true</code> if connected, <code>false</code> otherwise.
	 * @deprecated Connection status should be the business of the main
	 *             container.
	 */
    public boolean isConnected() {
        return getID() != null;
    }

    /**
	 * @throws BBException 
	 * @deprecated Connection status should be the business of the main
	 *             container.
	 */
    public void close() throws BBException {
        if (isConnected()) {
            mainContainer.disconnect();
        } else {
            throw new BBException("Connection already closed.");
        }
    }

    public ID getID() {
        return mainContainer.getConnectedID();
    }

    public Object getAdapter(Class adapter) {
        final IAdapterManager adapterManager = Activator.getDefault().getAdapterManager();
        if (adapterManager == null)
            return null;
        return adapterManager.getAdapter(this, adapter);
    }

    public URL getURL() {
        return url;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public AbstractParser getParser() {
        return parser;
    }

    protected abstract WebRequest createMemberPageRequest(ID id);

    public IMember getMember(ID id) throws BBException {
        if (cachedMembers.containsKey(id)) {
            return cachedMembers.get(id);
        } else {
            final WebRequest request = createMemberPageRequest(id);
            try {
                request.execute();
                final String str = request.getResponseBodyAsString();
                request.releaseConnection();
                final IMember member = parser.parseMemberPageForName(str, id);
                if (member != null) {
                    ((AbstractBBObject) member).setBulletinBoard(this);
                    cachedMembers.put(member.getID(), member);
                    return member;
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public IMember getLoggedInMember() throws BBException {
        if (loggedInMemberId == null) {
            return null;
        }
        return getMember(loggedInMemberId);
    }

    protected abstract WebRequest createMemberListRequest();

    public List<IMember> getMembers() throws BBException {
        // TODO: this only returns first page
        if (cachedMembers.isEmpty()) {
            final WebRequest request = createMemberListRequest();
            try {
                request.execute();
                final String str = request.getResponseBodyAsString();
                request.releaseConnection();
                cachedMembers = parser.parseMembers(str);
                for (final IMember member : cachedMembers.values()) {
                    ((AbstractBBObject) member).setBulletinBoard(this);
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<IMember>(cachedMembers.values());
    }

    protected abstract WebRequest createMemberGroupListRequest();

    public Collection<IMemberGroup> getMemberGroups() throws BBException {
        if (cachedMemberGroups.isEmpty()) {
            final WebRequest request = createMemberGroupListRequest();
            try {
                request.execute();
                final String str = request.getResponseBodyAsString();
                request.releaseConnection();
                cachedMemberGroups = parser.parseMemberGroups(str);
                for (final IMemberGroup grp : cachedMemberGroups.values()) {
                    ((AbstractBBObject) grp).setBulletinBoard(this);
                }
            } catch (final IOException e) {
            }
        }
        return new HashSet<IMemberGroup>(cachedMemberGroups.values());
    }

    public IMemberGroup getMemberGroup(ID id) throws BBException {
        if (cachedMemberGroups.isEmpty()) {
            getMemberGroups();
        }
        return cachedMemberGroups.get(id);
    }
}
