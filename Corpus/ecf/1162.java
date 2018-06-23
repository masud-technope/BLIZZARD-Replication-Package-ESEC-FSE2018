package org.eclipse.ecf.internal.bulletinboard.commons;

import org.eclipse.ecf.bulletinboard.BBException;
import org.eclipse.ecf.bulletinboard.IBBCredentials;
import org.eclipse.ecf.bulletinboard.IBulletinBoardContainerAdapter;
import org.eclipse.ecf.core.AbstractContainer;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.security.Callback;
import org.eclipse.ecf.core.security.CallbackHandler;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.security.NameCallback;
import org.eclipse.ecf.core.security.ObjectCallback;

public abstract class AbstractBBContainer extends AbstractContainer {

    private ID localID;

    private ID targetID;

    protected AbstractBulletinBoard bb;

    public  AbstractBBContainer(ID id) {
        super();
        this.localID = id;
        this.targetID = null;
    }

    public void connect(ID targetID, IConnectContext connectContext) throws ContainerConnectException {
        this.targetID = targetID;
        bb.postConnect();
        IBBCredentials creds = getCredentialsFromConnectContext(connectContext);
        if (creds != null) {
            try {
                bb.login(creds);
            } catch (BBException e) {
                throw new ContainerConnectException(e);
            }
        }
    }

    public void disconnect() {
        try {
            if (bb.getLoggedInMember() != null) {
                bb.logout();
            }
            bb.postDisconnect();
            targetID = null;
        } catch (BBException e) {
            e.printStackTrace();
        }
    }

    public ID getID() {
        return localID;
    }

    public ID getConnectedID() {
        return targetID;
    }

    @Override
    public void dispose() {
        disconnect();
        bb = null;
    }

    public Object getAdapter(Class serviceType) {
        if (serviceType.equals(IBulletinBoardContainerAdapter.class)) {
            return bb;
        }
        return super.getAdapter(serviceType);
    }

    public static class Credentials implements IBBCredentials {

        private String username;

        private String password;

        private  Credentials(String username, String password) {
            super();
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return this.username;
        }

        public String getPassword() {
            return this.password;
        }
    }

    protected IBBCredentials getCredentialsFromConnectContext(IConnectContext connectContext) throws ContainerConnectException {
        try {
            if (connectContext == null) {
                return null;
            }
            Callback[] callbacks = new Callback[2];
            callbacks[0] = new NameCallback("Username");
            callbacks[1] = new ObjectCallback();
            CallbackHandler handler = connectContext.getCallbackHandler();
            if (handler != null) {
                handler.handle(callbacks);
            }
            NameCallback nc = (NameCallback) callbacks[0];
            ObjectCallback cb = (ObjectCallback) callbacks[1];
            return new Credentials(nc.getName(), (String) cb.getObject());
        } catch (Exception e) {
            throw new ContainerConnectException("Exception in CallbackHandler.handle(<callbacks>)", e);
        }
    }
}
