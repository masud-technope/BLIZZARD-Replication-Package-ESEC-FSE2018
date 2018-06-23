/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.remoteservice.generic;

import java.io.Serializable;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.remoteservice.IRemoteCallListener;

public class Request implements Serializable {

    private static final long serialVersionUID = -6428866228973362178L;

    private static long nextRequestId = 0;

    long requestId;

    ID requestContainerID;

    long serviceId;

    RemoteCallImpl call;

    Response response;

    boolean done = false;

    transient IRemoteCallListener listener = null;

    private static synchronized long getNextRequestId() {
        long result = nextRequestId;
        nextRequestId = (nextRequestId == Long.MAX_VALUE) ? 0L : nextRequestId + 1;
        return result;
    }

    public  Request(ID requestContainerID, long serviceId, RemoteCallImpl call) {
        this(requestContainerID, serviceId, call, null);
    }

    public  Request(ID requestContainerID, long serviceId, RemoteCallImpl call, IRemoteCallListener listener) {
        this.requestContainerID = requestContainerID;
        this.serviceId = serviceId;
        this.call = call;
        this.requestId = getNextRequestId();
        this.listener = listener;
    }

    public long getRequestId() {
        return requestId;
    }

    public ID getRequestContainerID() {
        return requestContainerID;
    }

    public long getServiceId() {
        return serviceId;
    }

    public RemoteCallImpl getCall() {
        return call;
    }

    protected void setResponse(Response response) {
        this.response = response;
    }

    protected Response getResponse() {
        return response;
    }

    protected boolean isDone() {
        return done;
    }

    protected void setDone(boolean val) {
        this.done = val;
    }

    protected IRemoteCallListener getListener() {
        return listener;
    }

    public String toString() {
        //$NON-NLS-1$
        final StringBuffer buf = new StringBuffer("Request[");
        //$NON-NLS-1$ //$NON-NLS-2$
        buf.append("requestId=").append(requestId).append(";cont=").append(requestContainerID).append(//$NON-NLS-1$
        ";serviceId=").append(//$NON-NLS-1$
        serviceId).append(";call=").append(call).append(";done=").append(//$NON-NLS-1$ //$NON-NLS-2$
        done).append(";response=").append(response).append(//$NON-NLS-1$ //$NON-NLS-2$
        ";listener=").append(//$NON-NLS-1$
        listener).append(//$NON-NLS-1$
        "]");
        return buf.toString();
    }
}
