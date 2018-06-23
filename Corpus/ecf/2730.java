/*******************************************************************************
* Copyright (c) 2009 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice.client;

import java.util.Arrays;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.remoteservice.IRemoteCall;

/**
 * Implementation of {@link IRemoteCallable}.
 * 
 * @since 4.0
 */
public class RemoteCallable implements IRemoteCallable {

    protected String method;

    protected String resourcePath;

    protected IRemoteCallParameter[] defaultParameters;

    protected long defaultTimeout;

    protected IRemoteCallableRequestType requestType;

    public  RemoteCallable(String method, String resourcePath, IRemoteCallParameter[] defaultParameters, IRemoteCallableRequestType requestType, long defaultTimeout) {
        this.method = method;
        Assert.isNotNull(method);
        this.resourcePath = resourcePath;
        Assert.isNotNull(resourcePath);
        this.defaultParameters = defaultParameters;
        this.requestType = requestType;
        this.defaultTimeout = defaultTimeout;
    }

    public  RemoteCallable(String method, String resourcePath, IRemoteCallParameter[] defaultParameters, IRemoteCallableRequestType requestType) {
        this(method, resourcePath, defaultParameters, requestType, IRemoteCall.DEFAULT_TIMEOUT);
    }

    public String getMethod() {
        return method;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public IRemoteCallParameter[] getDefaultParameters() {
        return defaultParameters;
    }

    public IRemoteCallableRequestType getRequestType() {
        return requestType;
    }

    public long getDefaultTimeout() {
        return defaultTimeout;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        //$NON-NLS-1$
        buffer.append("RemoteCallable[defaultParameters=");
        buffer.append(defaultParameters != null ? Arrays.asList(defaultParameters) : null);
        //$NON-NLS-1$
        buffer.append(", defaultTimeout=");
        buffer.append(defaultTimeout);
        //$NON-NLS-1$
        buffer.append(", method=");
        buffer.append(method);
        //$NON-NLS-1$
        buffer.append(", requestType=");
        buffer.append(requestType);
        //$NON-NLS-1$
        buffer.append(", resourcePath=");
        buffer.append(resourcePath);
        //$NON-NLS-1$
        buffer.append("]");
        return buffer.toString();
    }

    /**
	 * @since 8.5
	 */
    public static class Builder {

        private final String methodName;

        private final String resourcePath;

        private IRemoteCallParameter[] defaultParameters;

        private long timeout = IRemoteCall.DEFAULT_TIMEOUT;

        private IRemoteCallableRequestType requestType;

        public  Builder(String methodName, String resourcePath) {
            Assert.isNotNull(methodName);
            this.methodName = methodName;
            this.resourcePath = resourcePath;
            Assert.isNotNull(resourcePath);
        }

        public Builder setDefaultParameters(IRemoteCallParameter[] defaultParameters) {
            this.defaultParameters = defaultParameters;
            return this;
        }

        public Builder setRequestTimeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder setRequestType(IRemoteCallableRequestType requestType) {
            this.requestType = requestType;
            return this;
        }

        public IRemoteCallable build() {
            return new RemoteCallable(this.methodName, this.resourcePath, this.defaultParameters, this.requestType, this.timeout);
        }
    }
}
