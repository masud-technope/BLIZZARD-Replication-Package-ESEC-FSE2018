/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.riena.container;

import java.util.Dictionary;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ecf.core.*;
import org.eclipse.ecf.core.events.*;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.provider.riena.identity.*;
import org.eclipse.ecf.remoteservice.*;
import org.eclipse.equinox.concurrent.future.*;
import org.osgi.framework.InvalidSyntaxException;

public class RienaContainer extends BaseContainer implements IContainer, IRemoteServiceContainerAdapter {

    private RienaID connectedID = null;

    public  RienaContainer(ID id) {
        super(id);
    }

    // Impl of IContainer
    public void connect(final ID targetId, IConnectContext connectContext) throws ContainerConnectException {
        if (targetId == null)
            //$NON-NLS-1$
            throw new ContainerConnectException("targetId must not be null");
        if (!(targetId instanceof RienaID))
            //$NON-NLS-1$
            throw new ContainerConnectException("targetId must be RienaID type");
        fireContainerEvent(new IContainerConnectingEvent() {

            public ID getLocalContainerID() {
                return getID();
            }

            public ID getTargetID() {
                return targetId;
            }

            public Object getData() {
                return null;
            }
        });
        // TODO add Riena-specific connect code here...or if no explicit connect
        // is done...do nothing!
        connectedID = (RienaID) targetId;
        fireContainerEvent(new IContainerConnectedEvent() {

            public ID getLocalContainerID() {
                return getID();
            }

            public ID getTargetID() {
                return targetId;
            }
        });
    }

    public void disconnect() {
        fireContainerEvent(new IContainerDisconnectingEvent() {

            public ID getLocalContainerID() {
                return getID();
            }

            public ID getTargetID() {
                return connectedID;
            }
        });
        final ID previouslyConnectedID = connectedID;
        // TODO put riena-specific disconnect code here...or none if none
        connectedID = null;
        fireContainerEvent(new IContainerDisconnectedEvent() {

            public ID getLocalContainerID() {
                return getID();
            }

            public ID getTargetID() {
                return previouslyConnectedID;
            }
        });
    }

    public void dispose() {
        super.dispose();
        fireContainerEvent(new IContainerDisposeEvent() {

            public ID getLocalContainerID() {
                return getID();
            }
        });
    // TODO Auto-generated method stub
    }

    public Object getAdapter(Class serviceType) {
        if (this.getClass().isAssignableFrom(serviceType)) {
            return this;
        }
        return null;
    }

    public Namespace getConnectNamespace() {
        return RienaNamespace.getInstance();
    }

    public ID getConnectedID() {
        return connectedID;
    }

    // Impl of IRemoteServiceContainerAdapter
    public void addRemoteServiceListener(IRemoteServiceListener listener) {
    // TODO Auto-generated method stub
    }

    public IRemoteFilter createRemoteFilter(String filter) throws InvalidSyntaxException {
        // TODO Auto-generated method stub
        return null;
    }

    public IRemoteService getRemoteService(IRemoteServiceReference reference) {
        // TODO Auto-generated method stub
        return null;
    }

    public IRemoteServiceID getRemoteServiceID(ID containerId, long containerRelativeId) {
        // TODO
        return null;
    }

    public Namespace getRemoteServiceNamespace() {
        return RienaRemoteServiceNamespace.getInstance();
    }

    public IRemoteServiceReference getRemoteServiceReference(IRemoteServiceID serviceId) {
        // TODO Auto-generated method stub
        return null;
    }

    public IRemoteServiceReference[] getRemoteServiceReferences(ID[] idFilter, String clazz, String filter) throws InvalidSyntaxException {
        // TODO Auto-generated method stub
        return null;
    }

    public IRemoteServiceReference[] getRemoteServiceReferences(ID target, String clazz, String filter) throws InvalidSyntaxException, ContainerConnectException {
        // TODO Auto-generated method stub
        return null;
    }

    public IRemoteServiceRegistration registerRemoteService(String[] clazzes, Object service, Dictionary properties) {
        // TODO Auto-generated method stub
        return null;
    }

    public void removeRemoteServiceListener(IRemoteServiceListener listener) {
    // TODO Auto-generated method stub
    }

    public void setConnectContextForAuthentication(IConnectContext connectContext) {
    // TODO Auto-generated method stub
    }

    public boolean ungetRemoteService(IRemoteServiceReference reference) {
        // TODO Auto-generated method stub
        return false;
    }

    public IFuture asyncGetRemoteServiceReferences(final ID[] idFilter, final String clazz, final String filter) {
        IExecutor executor = new ThreadsExecutor();
        return executor.execute(new IProgressRunnable() {

            public Object run(IProgressMonitor monitor) throws Exception {
                return getRemoteServiceReferences(idFilter, clazz, filter);
            }
        }, null);
    }

    public IFuture asyncGetRemoteServiceReferences(final ID target, final String clazz, final String filter) {
        IExecutor executor = new ThreadsExecutor();
        return executor.execute(new IProgressRunnable() {

            public Object run(IProgressMonitor monitor) throws Exception {
                return getRemoteServiceReferences(target, clazz, filter);
            }
        }, null);
    }

    public IRemoteServiceReference[] getAllRemoteServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
        // TODO Auto-generated method stub
        return null;
    }

    public IRemoteServiceReference[] getRemoteServiceReferences(ID target, ID[] idFilter, String clazz, String filter) throws InvalidSyntaxException, ContainerConnectException {
        // TODO Auto-generated method stub
        return null;
    }

    public IFuture asyncGetRemoteServiceReferences(ID target, ID[] idFilter, String clazz, String filter) {
        // TODO Auto-generated method stub
        return null;
    }
}
