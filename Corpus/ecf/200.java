/*******************************************************************************
 *  Copyright (c)2010 REMAIN B.V. The Netherlands. (http://www.remainsoftware.com).
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     Wim Jongman - initial API and implementation 
 *     Ahmed Aadel - initial API and implementation     
 *******************************************************************************/
package org.eclipse.ecf.provider.zookeeper.core.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.discovery.DiscoveryContainerConfig;
import org.eclipse.ecf.provider.zookeeper.core.IDiscoveryConfig;
import org.osgi.framework.ServiceReference;

public class Configurator extends DiscoveryContainerConfig {

    private File zooConfFile;

    private File zookeeperData;

    private List<Configuration> runningConfigs = new ArrayList<Configuration>();

    public static final Configurator INSTANCE = new Configurator();

    private static final ID ConfigID = IDFactory.getDefault().createStringID(UUID.randomUUID().toString());

    private  Configurator() {
        super(ConfigID);
    }

    public Configuration createConfig(ID targetId) {
        Assert.isNotNull(targetId);
        Configuration conf = new Configuration(targetId);
        runningConfigs.add(conf);
        return conf;
    }

    public Configuration createConfig(ServiceReference reference) {
        Assert.isNotNull(reference);
        Configuration conf = new Configuration(reference);
        runningConfigs.add(conf);
        return conf;
    }

    public Configuration createConfig(String propsAsString) {
        Assert.isNotNull(propsAsString);
        Configuration conf = new Configuration(propsAsString);
        runningConfigs.add(conf);
        return conf;
    }

    public void cleanAll() {
        for (File file : this.zookeeperData.listFiles()) {
            try {
                if (file.isDirectory()) {
                    for (File f : file.listFiles()) f.delete();
                }
                file.delete();
            } catch (Throwable t) {
                continue;
            }
        }
    }

    public String getConfFile() {
        return this.zooConfFile.toString();
    }

    public static boolean isValid(String flavorInput) {
        Assert.isNotNull(flavorInput);
        //$NON-NLS-1$
        boolean valid = flavorInput.contains("=");
        //$NON-NLS-1$
        String f = flavorInput.split("=")[0];
        valid &= f.equals(IDiscoveryConfig.ZOODISCOVERY_FLAVOR_CENTRALIZED) || f.equals(IDiscoveryConfig.ZOODISCOVERY_FLAVOR_REPLICATED) || f.equals(IDiscoveryConfig.ZOODISCOVERY_FLAVOR_STANDALONE);
        return valid;
    }

    public static void validateFlavor(String f) {
        if (!isValid(f))
            throw new IllegalArgumentException(f);
    }

    @Override
    public ID getID() {
        return ConfigID;
    }
}
