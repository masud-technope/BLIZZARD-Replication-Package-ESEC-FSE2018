/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.internal.remoteservice.java8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.provider.generic.GenericContainerInstantiator;
import org.eclipse.ecf.provider.generic.SOContainerConfig;

public class J8GenericContainerInstantiator extends GenericContainerInstantiator {

    public static final String JAVA8_CLIENT_NAME = "ecf.generic.client.java8";

    public static final String JAVA8_SERVER_NAME = "ecf.generic.server.java8";

    @Override
    protected boolean isClient(ContainerTypeDescription description) {
        if (description.getName().equals(JAVA8_SERVER_NAME))
            return false;
        return true;
    }

    @Override
    protected IContainer createClientContainer(GenericContainerArgs gcargs) throws Exception {
        return new J8TCPClientSOContainer(new SOContainerConfig(gcargs.getID()), gcargs.getKeepAlive().intValue());
    }

    @Override
    protected IContainer createServerContainer(GenericContainerArgs gcargs) throws Exception {
        return new J8TCPServerSOContainer(new SOContainerConfig(gcargs.getID()), gcargs.getBindAddress(), gcargs.getKeepAlive().intValue());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public String[] getImportedConfigs(ContainerTypeDescription description, String[] exporterSupportedConfigs) {
        if (exporterSupportedConfigs == null)
            return null;
        List results = new ArrayList();
        List supportedConfigs = Arrays.asList(exporterSupportedConfigs);
        // For a server, if exporter is a client then we can be an importer
        if (JAVA8_SERVER_NAME.equals(description.getName())) {
            if (supportedConfigs.contains(JAVA8_CLIENT_NAME))
                results.add(JAVA8_SERVER_NAME);
        // For a client, if exporter is server we can import
        // or if remote is either generic server or generic client
        } else if (JAVA8_CLIENT_NAME.equals(description.getName())) {
            if (supportedConfigs.contains(JAVA8_SERVER_NAME) || supportedConfigs.contains(JAVA8_CLIENT_NAME))
                results.add(JAVA8_CLIENT_NAME);
        }
        if (results.size() == 0)
            return null;
        return (String[]) results.toArray(new String[] {});
    }
}
