/*******************************************************************************
* Copyright (c) 2014 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice.rest.client;

import java.text.MessageFormat;
import java.util.Dictionary;
import java.util.Hashtable;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.remoteservice.*;
import org.eclipse.ecf.remoteservice.client.*;
import org.eclipse.ecf.remoteservice.rest.identity.RestID;
import org.eclipse.ecf.remoteservice.rest.identity.RestNamespace;
import org.eclipse.ecf.remoteservice.util.RemoteFilterImpl;
import org.osgi.framework.InvalidSyntaxException;

public abstract class AbstractRestClientContainer extends AbstractClientContainer {

    //$NON-NLS-1$
    public static final String SLASH = "/";

    public  AbstractRestClientContainer(RestID containerID) {
        super(containerID);
        // Set serializers
        setParameterSerializer(new StringParameterSerializer());
        setResponseDeserializer(new XMLRemoteResponseDeserializer());
    }

    public boolean setRemoteServiceCallPolicy(IRemoteServiceCallPolicy policy) {
        return false;
    }

    public Namespace getConnectNamespace() {
        return IDFactory.getDefault().getNamespaceByName(RestNamespace.NAME);
    }

    protected abstract IRemoteService createRemoteService(RemoteServiceClientRegistration registration);

    public IRemoteServiceReference[] getRemoteServiceReferences(ID target, ID[] idFilter, String clazz, String filter) throws InvalidSyntaxException, ContainerConnectException {
        return super.getRemoteServiceReferences(transformTarget(target, filter), idFilter, clazz, filter);
    }

    public IRemoteServiceReference[] getRemoteServiceReferences(ID target, String clazz, String filter) throws InvalidSyntaxException, ContainerConnectException {
        return super.getRemoteServiceReferences(transformTarget(target, filter), clazz, filter);
    }

    protected ID transformTarget(ID originalTarget, String filter) throws InvalidSyntaxException {
        if (originalTarget != null && filter != null && originalTarget instanceof RestID)
            ((RestID) originalTarget).setRsId(new RemoteFilterImpl(filter).getRsId());
        return originalTarget;
    }

    protected String prepareBaseUri(IRemoteCall call, IRemoteCallable callable) {
        String baseUri = ((RestID) getRemoteCallTargetID()).toURI().toString();
        // strip off any trailing slashes
        while (baseUri.endsWith(SLASH)) baseUri = baseUri.substring(0, baseUri.length() - 1);
        // then do substitution
        baseUri = substituteParameters(baseUri, call.getParameters());
        return baseUri;
    }

    protected String prepareResourcePath(IRemoteCall call, IRemoteCallable callable) {
        String path = callable.getResourcePath();
        // check for valid value
        if (path == null)
            return null;
        // strip off any extra leading slashes
        while (path.startsWith(SLASH)) path = path.substring(1);
        // do substitution
        path = substituteParameters(path, call.getParameters());
        return path;
    }

    protected String substituteParameters(String path, Object[] parameters) {
        return MessageFormat.format(path, parameters);
    }

    public String prepareEndpointAddress(IRemoteCall call, IRemoteCallable callable) {
        String baseUri = prepareBaseUri(call, callable);
        if (baseUri == null)
            return baseUri;
        String resourcePath = prepareResourcePath(call, callable);
        if (resourcePath == null)
            return null;
        return new StringBuffer(baseUri).append(SLASH).append(resourcePath).toString();
    }

    protected class RestRemoteServiceClientRegistration extends RemoteServiceClientRegistration {

        public  RestRemoteServiceClientRegistration(Namespace namespace, IRemoteCallable[] restCalls, Dictionary properties, RemoteServiceClientRegistry registry) {
            super(namespace, restCalls, properties, registry);
            ID cID = getConnectedID();
            if (cID != null)
                this.containerId = cID;
            long rsId = ((RestID) containerId).getRsId();
            this.serviceID = new RemoteServiceID(namespace, containerId, rsId);
            if (rsId > 0) {
                if (this.properties == null)
                    this.properties = new Hashtable();
                this.properties.put(org.eclipse.ecf.remoteservice.Constants.SERVICE_ID, new Long(rsId));
            }
        }

        public  RestRemoteServiceClientRegistration(Namespace namespace, String[] classNames, IRemoteCallable[][] restCalls, Dictionary properties, RemoteServiceClientRegistry registry) {
            super(namespace, classNames, restCalls, properties, registry);
            ID cID = getConnectedID();
            if (cID != null)
                this.containerId = cID;
            long rsId = ((RestID) containerId).getRsId();
            this.serviceID = new RemoteServiceID(namespace, containerId, rsId);
            if (rsId > 0) {
                if (this.properties == null)
                    this.properties = new Hashtable();
                this.properties.put(org.eclipse.ecf.remoteservice.Constants.SERVICE_ID, new Long(rsId));
            }
        }
    }

    protected RemoteServiceClientRegistration createRestServiceRegistration(String[] clazzes, IRemoteCallable[][] callables, Dictionary properties) {
        return new RestRemoteServiceClientRegistration(getRemoteServiceNamespace(), clazzes, callables, properties, registry);
    }

    protected RemoteServiceClientRegistration createRestServiceRegistration(IRemoteCallable[] callables, Dictionary properties) {
        return new RestRemoteServiceClientRegistration(getRemoteServiceNamespace(), callables, properties, registry);
    }
}
