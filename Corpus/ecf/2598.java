/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservice.provider;

import java.util.*;

/**
 * @since 8.7
 */
public abstract class PeerRemoteServiceContainerInstantiator extends RemoteServiceContainerInstantiator {

    public  PeerRemoteServiceContainerInstantiator(String peerA, String peerB) {
        this.exporterConfigs = new ArrayList<String>();
        this.exporterConfigs.add(peerA);
        this.exporterConfigs.add(peerB);
        this.exporterConfigToImporterConfigs = new HashMap<String, List<String>>();
        this.exporterConfigToImporterConfigs.put(peerA, Arrays.asList(new String[] { peerA, peerB }));
        this.exporterConfigToImporterConfigs.put(peerB, Arrays.asList(new String[] { peerA, peerB }));
    }
}
