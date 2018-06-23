/*******************************************************************************
* Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.server.generic;

import java.net.InetAddress;
import java.util.*;
import org.eclipse.core.runtime.Assert;

/**
 * @since 6.0
 */
public class GenericServerContainerGroupFactory implements IGenericServerContainerGroupFactory {

    protected class SCGData {

        private String hostname;

        private int port;

        private InetAddress bindAddress;

        /**
		 * @since 7.0
		 */
        public  SCGData(String hostname, int port, InetAddress bindAddress) {
            Assert.isNotNull(hostname);
            Assert.isTrue(port > 0);
            this.hostname = hostname;
            this.port = port;
            this.bindAddress = bindAddress;
        }

        public String getHostname() {
            return hostname;
        }

        public int getPort() {
            return port;
        }

        public boolean equals(Object other) {
            if (!(other instanceof SCGData))
                return false;
            SCGData o = (SCGData) other;
            if (this.hostname.equals(o.hostname) && this.port == o.port)
                return true;
            return false;
        }

        public int hashCode() {
            return this.hostname.hashCode() ^ this.port;
        }

        /**
		 * @since 7.0
		 */
        public InetAddress getBindAddress() {
            return bindAddress;
        }
    }

    private Hashtable serverContainerGroups = new Hashtable();

    /**
	 * @since 7.0
	 */
    public IGenericServerContainerGroup createContainerGroup(String hostname, int port, InetAddress bindAddress, Map defaultContainerProperties) throws GenericServerContainerGroupCreateException {
        synchronized (serverContainerGroups) {
            SCGData scgdata = new SCGData(hostname, port, bindAddress);
            if (serverContainerGroups.contains(scgdata))
                //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                throw new GenericServerContainerGroupCreateException("Cannot container group hostname=" + hostname + " port=" + port + " already exists");
            IGenericServerContainerGroup scg = createGenericServerContainerGroup(scgdata, defaultContainerProperties);
            serverContainerGroups.put(scgdata, scg);
            return scg;
        }
    }

    public IGenericServerContainerGroup createContainerGroup(String hostname, int port, Map defaultContainerProperties) throws GenericServerContainerGroupCreateException {
        return createContainerGroup(hostname, port, null, defaultContainerProperties);
    }

    protected boolean isSSLTransportSpecified(Map defaultContainerProperties) {
        boolean sslTransport = false;
        if (defaultContainerProperties != null) {
            Object sslTransportPropValue = defaultContainerProperties.get(IGenericServerContainerGroupFactory.SSLTRANSPORT_CONTAINER_PROP);
            if (sslTransportPropValue instanceof Boolean)
                sslTransport = ((Boolean) sslTransportPropValue).booleanValue();
            else if (sslTransportPropValue instanceof String)
                sslTransport = Boolean.valueOf((String) sslTransportPropValue).booleanValue();
        }
        return sslTransport;
    }

    /**
	 * @throws GenericServerContainerGroupCreateException  
	 */
    protected IGenericServerContainerGroup createGenericServerContainerGroup(SCGData scgdata, Map defaultContainerProperties) throws GenericServerContainerGroupCreateException {
        if (isSSLTransportSpecified(defaultContainerProperties))
            return new SSLGenericServerContainerGroup(scgdata.getHostname(), scgdata.getPort(), scgdata.getBindAddress(), defaultContainerProperties);
        return new GenericServerContainerGroup(scgdata.getHostname(), scgdata.getPort(), scgdata.getBindAddress(), defaultContainerProperties);
    }

    public IGenericServerContainerGroup createContainerGroup(String hostname, int port) throws GenericServerContainerGroupCreateException {
        return createContainerGroup(hostname, port, null);
    }

    public IGenericServerContainerGroup createContainerGroup(String hostname) throws GenericServerContainerGroupCreateException {
        return createContainerGroup(hostname, DEFAULT_PORT);
    }

    public void close() {
        synchronized (serverContainerGroups) {
            for (Iterator i = serverContainerGroups.keySet().iterator(); i.hasNext(); ) {
                SCGData scgdata = (SCGData) i.next();
                IGenericServerContainerGroup scg = (IGenericServerContainerGroup) serverContainerGroups.get(scgdata);
                // call close
                scg.close();
            }
        }
        serverContainerGroups.clear();
    }

    public IGenericServerContainerGroup getContainerGroup(String hostname, int port) {
        if (hostname == null)
            return null;
        SCGData scgdata = new SCGData(hostname, port, null);
        synchronized (serverContainerGroups) {
            return (IGenericServerContainerGroup) serverContainerGroups.get(scgdata);
        }
    }

    public IGenericServerContainerGroup[] getContainerGroups() {
        List results = new ArrayList();
        synchronized (serverContainerGroups) {
            for (Iterator i = serverContainerGroups.keySet().iterator(); i.hasNext(); ) {
                SCGData scgdata = (SCGData) i.next();
                IGenericServerContainerGroup gscg = (IGenericServerContainerGroup) serverContainerGroups.get(scgdata);
                if (gscg != null)
                    results.add(gscg);
            }
        }
        return (IGenericServerContainerGroup[]) results.toArray(new IGenericServerContainerGroup[] {});
    }

    public IGenericServerContainerGroup removeContainerGroup(String hostname, int port) {
        if (hostname == null)
            return null;
        SCGData scgdata = new SCGData(hostname, port, null);
        synchronized (serverContainerGroups) {
            return (IGenericServerContainerGroup) serverContainerGroups.remove(scgdata);
        }
    }
}
