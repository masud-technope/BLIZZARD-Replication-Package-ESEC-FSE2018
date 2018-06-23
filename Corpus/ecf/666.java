/*******************************************************************************
* Copyright (c) 2016 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice.client;

import java.util.*;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.remoteservice.*;
import org.eclipse.ecf.remoteservice.events.IRemoteServiceRegisteredEvent;
import org.eclipse.ecf.remoteservice.util.EndpointDescriptionPropertiesUtil;
import org.osgi.framework.InvalidSyntaxException;

/**
 * Abstract client container for use by RSA distribution providers.   Implements IRSAConsumerContainerAdapter.
 * @since 8.9
 */
public abstract class AbstractRSAClientContainer extends AbstractClientContainer implements IRSAConsumerContainerAdapter {

    public  AbstractRSAClientContainer(ID containerID) {
        super(containerID);
    }

    public boolean setRemoteServiceCallPolicy(IRemoteServiceCallPolicy policy) {
        return false;
    }

    public Namespace getConnectNamespace() {
        return getID().getNamespace();
    }

    private Long getServiceId(Map<String, Object> endpointDescriptionProperties) {
        //$NON-NLS-1$
        return EndpointDescriptionPropertiesUtil.verifyLongProperty(endpointDescriptionProperties, "endpoint.service.id");
    }

    private String getRemoteServiceFilter(Map<String, Object> endpointDescriptionProperties, Long rsId) {
        //$NON-NLS-1$
        String edRsFilter = EndpointDescriptionPropertiesUtil.verifyStringProperty(endpointDescriptionProperties, "ecf.endpoint.rsfilter");
        // If it's *still* zero, then just use the raw filter
        if (rsId == 0)
            // filter
            return edRsFilter;
        // It's a real remote service id...so we return
        StringBuffer result = //$NON-NLS-1$
        new StringBuffer("(&(").append(org.eclipse.ecf.remoteservice.Constants.SERVICE_ID).append("=").append(rsId).append(//$NON-NLS-1$ //$NON-NLS-2$
        ")");
        if (edRsFilter != null)
            result.append(edRsFilter);
        //$NON-NLS-1$
        result.append(")");
        return result.toString();
    }

    protected void connectToEndpoint(ID connectTargetID) throws ContainerConnectException {
        connect(connectTargetID, connectContext);
    }

    protected IRemoteCallable[][] createRegistrationCallables(ID targetID, String[] interfaces, Dictionary endpointDescriptionProperties) {
        return new IRemoteCallable[][] { { RemoteCallableFactory.createCallable(getID().getName()) } };
    }

    public class RSAClientRegistration extends RemoteServiceClientRegistration {

        public  RSAClientRegistration(ID targetID, String[] classNames, IRemoteCallable[][] restCalls, Dictionary properties) {
            super(getConnectNamespace(), classNames, restCalls, properties, AbstractRSAClientContainer.this.registry);
            this.containerId = targetID;
            this.serviceID = new RemoteServiceID(getConnectNamespace(), this.containerId, (Long) properties.get(org.eclipse.ecf.remoteservice.Constants.SERVICE_ID));
        }
    }

    protected Dictionary createRegistrationProperties(Map<String, Object> endpointDescriptionProperties) {
        return EndpointDescriptionPropertiesUtil.createDictionaryFromMap(endpointDescriptionProperties);
    }

    protected RemoteServiceClientRegistration createRSAClientRegistration(ID targetID, String[] interfaces, Map<String, Object> endpointDescriptionProperties) {
        Dictionary d = createRegistrationProperties(endpointDescriptionProperties);
        return new RSAClientRegistration(targetID, interfaces, createRegistrationCallables(targetID, interfaces, d), d);
    }

    public RemoteServiceClientRegistration registerEndpoint(ID targetID, String[] interfaces, Map<String, Object> endpointDescriptionProperties) {
        final RemoteServiceClientRegistration registration = createRSAClientRegistration(targetID, interfaces, endpointDescriptionProperties);
        this.registry.registerRegistration(registration);
        // notify
        fireRemoteServiceEvent(new IRemoteServiceRegisteredEvent() {

            public IRemoteServiceReference getReference() {
                return registration.getReference();
            }

            public ID getLocalContainerID() {
                return registration.getContainerID();
            }

            public ID getContainerID() {
                return getID();
            }

            public String[] getClazzes() {
                return registration.getClazzes();
            }
        });
        return registration;
    }

    public IRemoteServiceReference[] importEndpoint(Map<String, Object> endpointDescriptionProperties) throws ContainerConnectException, InvalidSyntaxException {
        // ecf.endpoint.id
        String ecfid = EndpointDescriptionPropertiesUtil.verifyStringProperty(endpointDescriptionProperties, Constants.ENDPOINT_ID);
        if (ecfid == null)
            //$NON-NLS-1$
            ecfid = EndpointDescriptionPropertiesUtil.verifyStringProperty(endpointDescriptionProperties, "endpoint.id");
        // ecf.endpoint.ts
        Long timestamp = EndpointDescriptionPropertiesUtil.verifyLongProperty(endpointDescriptionProperties, Constants.ENDPOINT_TIMESTAMP);
        if (timestamp == null)
            timestamp = getServiceId(endpointDescriptionProperties);
        // ecf.endpoint.ns
        String idNamespace = EndpointDescriptionPropertiesUtil.verifyStringProperty(endpointDescriptionProperties, Constants.ENDPOINT_CONTAINER_ID_NAMESPACE);
        // Create/verify endpointContainerID
        ID endpointContainerID = EndpointDescriptionPropertiesUtil.verifyIDProperty(idNamespace, ecfid);
        // Get rsId
        Long rsId = EndpointDescriptionPropertiesUtil.verifyLongProperty(endpointDescriptionProperties, Constants.SERVICE_ID);
        // if null, then set to service.id
        if (rsId == null)
            //$NON-NLS-1$
            rsId = EndpointDescriptionPropertiesUtil.verifyLongProperty(endpointDescriptionProperties, "endpoint.service.id");
        // Get connectTargetID
        ID connectTargetID = EndpointDescriptionPropertiesUtil.verifyIDProperty(idNamespace, EndpointDescriptionPropertiesUtil.verifyStringProperty(endpointDescriptionProperties, Constants.ENDPOINT_CONNECTTARGET_ID));
        // If not explicitly set, then set to endpointContainerID
        if (connectTargetID == null)
            connectTargetID = endpointContainerID;
        // Get idFilter
        ID[] idFilter = EndpointDescriptionPropertiesUtil.verifyIDArray(endpointDescriptionProperties, Constants.ENDPOINT_IDFILTER_IDS, idNamespace);
        // If not set, then set to endpointContainerID
        idFilter = (idFilter == null) ? new ID[] { endpointContainerID } : idFilter;
        // Get rsFilter
        String rsFilter = getRemoteServiceFilter(endpointDescriptionProperties, rsId);
        // Get interfaces
        List<String> interfaces = EndpointDescriptionPropertiesUtil.verifyObjectClassProperty(endpointDescriptionProperties);
        // register locally
        registerEndpoint(connectTargetID, interfaces.toArray(new String[interfaces.size()]), endpointDescriptionProperties);
        // If we have a non-null targetID we connect 
        if (connectTargetID != null)
            connectToEndpoint(connectTargetID);
        return getRemoteServiceReferences(idFilter, interfaces.iterator().next(), rsFilter);
    }

    /**
	 * Create a remote service for a given remote service registration.   This method will be 
	 * called as part of the RemoteServiceAdmin.importService.   
	 * 
	 * @param registration the remote service client registration associated with the service
	 * being imported.   Will not be <code>null</code>.
	 */
    @Override
    protected abstract IRemoteService createRemoteService(RemoteServiceClientRegistration registration);

    @Override
    protected String prepareEndpointAddress(IRemoteCall call, IRemoteCallable callable) {
        return null;
    }
}
