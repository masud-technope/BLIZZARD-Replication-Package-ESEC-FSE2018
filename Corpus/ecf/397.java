/*******************************************************************************
 * Copyright (c) 2008 Jan S. Rellermeyer, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jan S. Rellermeyer - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.r_osgi;

import ch.ethz.iks.r_osgi.RemoteOSGiService;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import org.eclipse.ecf.core.*;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.provider.IContainerInstantiator;
import org.eclipse.ecf.core.provider.IRemoteServiceContainerInstantiator;
import org.eclipse.ecf.provider.r_osgi.identity.*;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;

/**
 * The container instantiator creates new container instances of type
 * <i>ecf.r_osgi.peer</i> through the
 * <code>org.eclipse.ecf.ContainerFactory</code> extension point.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 */
public final class R_OSGiContainerInstantiator implements IContainerInstantiator, IRemoteServiceContainerInstantiator {

    //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    public static final String[] r_OSGiIntents = { "passByValue", "exactlyOnce", "ordered" };

    public static R_OSGiID createR_OSGiID(Namespace namespace, String uriStr) {
        return (R_OSGiID) IDFactory.getDefault().createID(namespace, uriStr);
    }

    //$NON-NLS-1$
    public static final String ID_PROP = "id";

    //$NON-NLS-1$
    private static final String WS_PROTOCOL = "http";

    //$NON-NLS-1$
    private static final String WSS_PROTOCOL = "https";

    private static final int WS_DEFAULT_PORT = 80;

    private static final int WSS_DEFAULT_PORT = 443;

    //$NON-NLS-1$ //$NON-NLS-2$
    final boolean useHostname = Boolean.valueOf(System.getProperty("org.eclipse.ecf.provider.r_osgi.useHostName", "true")).booleanValue();

    /**
	 * creates a new container instance.
	 * 
	 * @param description
	 *            the container type description.
	 * @param parameters
	 *            the parameter passed to the container constructor.
	 * @return the factored container instance.
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#createInstance(org.eclipse.ecf.core.ContainerTypeDescription,
	 *      java.lang.Object[])
	 */
    public IContainer createInstance(final ContainerTypeDescription description, final Object[] parameters) throws ContainerCreateException {
        try {
            final RemoteOSGiService remoteOSGiService = Activator.getDefault().getRemoteOSGiService();
            String descriptionName = description.getName();
            boolean wss = descriptionName.equals(ROSGI_WEBSOCKETSS_CONFIG);
            boolean ws = (descriptionName.equals(ROSGI_WEBSOCKETS_CONFIG) || wss);
            Namespace ns = (wss ? R_OSGiWSSNamespace.getDefault() : ((ws) ? R_OSGiWSNamespace.getDefault() : R_OSGiNamespace.getDefault()));
            ID containerID = null;
            if (parameters == null) {
                String //$NON-NLS-1$
                localHost = //$NON-NLS-1$
                "localhost";
                if (useHostname) {
                    try {
                        localHost = InetAddress.getLocalHost().getCanonicalHostName();
                    } catch (UnknownHostException e) {
                    }
                }
                final String nsScheme = ns.getScheme();
                final String wsProtocol = (wss ? WSS_PROTOCOL : (ws ? WS_PROTOCOL : null));
                int listeningPort = remoteOSGiService.getListeningPort((wsProtocol != null) ? wsProtocol : nsScheme);
                int idPort = -1;
                if (WSS_PROTOCOL.equals(wsProtocol) && listeningPort != WSS_DEFAULT_PORT)
                    idPort = listeningPort;
                else if (WS_PROTOCOL.equals(wsProtocol) && listeningPort != WS_DEFAULT_PORT)
                    idPort = listeningPort;
                //$NON-NLS-1$ //$NON-NLS-2$
                String portStr = (idPort > 0 ? (":" + idPort) : "");
                //$NON-NLS-1$ 
                containerID = createR_OSGiID(ns, new String(nsScheme + "://" + localHost + portStr));
            } else if (parameters.length > 0) {
                if (parameters[0] instanceof ID)
                    containerID = (ID) parameters[0];
                else if (parameters[0] instanceof String)
                    containerID = createR_OSGiID(ns, (String) parameters[0]);
                else if (parameters[0] instanceof Map) {
                    Map params = (Map) parameters[0];
                    String idStr = (String) params.get(ID_PROP);
                    if (idStr == null)
                        throw new NullPointerException("No ID prop found in parameters map");
                    containerID = createR_OSGiID(ns, idStr);
                }
            }
            if (containerID == null)
                throw new ContainerCreateException("Unsupported arguments " + Arrays.asList(parameters));
            if (wss)
                return new R_OSGiWSSRemoteServiceContainer(remoteOSGiService, containerID);
            else if (ws)
                return new R_OSGiWSRemoteServiceContainer(remoteOSGiService, containerID);
            else
                return new R_OSGiRemoteServiceContainer(remoteOSGiService, containerID);
        } catch (Exception e) {
            throw new ContainerCreateException("Could not create R_OSGI ID", e);
        }
    }

    /**
	 * get the adapter types that are supported by this container instantiator.
	 * 
	 * @param description
	 *            the container type description.
	 * @return a string array of the supported classes to which the factored
	 *         containers can provide adapters.
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#getSupportedAdapterTypes(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
    public String[] getSupportedAdapterTypes(final ContainerTypeDescription description) {
        return new String[] { IRemoteServiceContainerAdapter.class.getName(), IContainer.class.getName() };
    }

    /**
	 * get the supported parameter types which the constructor of the container
	 * takes.
	 * 
	 * @param description
	 *            the container type description.
	 * @return an array of class arrays. Each array entry describes one sequence
	 *         of supported parameter.
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#getSupportedParameterTypes(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
    public Class[][] getSupportedParameterTypes(final ContainerTypeDescription description) {
        return new Class[][] { new Class[] {}, new Class[] { ID.class } };
    }

    public String[] getSupportedIntents(ContainerTypeDescription description) {
        return r_OSGiIntents;
    }

    //$NON-NLS-1$
    private static final String ROSGI_CONFIG = "ecf.r_osgi.peer";

    //$NON-NLS-1$
    private static final String ROSGI_WEBSOCKETS_CONFIG = "ecf.r_osgi.peer.ws";

    //$NON-NLS-1$
    private static final String ROSGI_WEBSOCKETSS_CONFIG = "ecf.r_osgi.peer.wss";

    public static final String NAME = ROSGI_CONFIG;

    public static final String NAME_HTTP = ROSGI_WEBSOCKETS_CONFIG;

    public static final String NAME_HTTPS = ROSGI_WEBSOCKETSS_CONFIG;

    public String[] getSupportedConfigs(ContainerTypeDescription description) {
        return new String[] { description.getName() };
    }

    public String[] getImportedConfigs(ContainerTypeDescription description, String[] exporterSupportedConfigs) {
        if (exporterSupportedConfigs == null)
            return null;
        List results = new ArrayList();
        for (int i = 0; i < exporterSupportedConfigs.length; i++) {
            if (exporterSupportedConfigs[i].equals(ROSGI_CONFIG))
                results.add(ROSGI_CONFIG);
            if (exporterSupportedConfigs[i].equals(ROSGI_WEBSOCKETS_CONFIG))
                results.add(ROSGI_WEBSOCKETS_CONFIG);
            if (exporterSupportedConfigs[i].equals(ROSGI_WEBSOCKETSS_CONFIG))
                results.add(ROSGI_WEBSOCKETSS_CONFIG);
        }
        return (results.size() == 0) ? null : (String[]) results.toArray(new String[results.size()]);
    }

    public Dictionary getPropertiesForImportedConfigs(ContainerTypeDescription description, String[] importedConfigs, Dictionary exportedProperties) {
        return null;
    }
}
