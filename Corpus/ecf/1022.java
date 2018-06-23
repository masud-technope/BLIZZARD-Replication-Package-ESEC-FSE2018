/*******************************************************************************
 *  Copyright (c)2010 REMAIN B.V. The Netherlands. (http://www.remainsoftware.com).
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *    Wim Jongman - initial API and implementation 
 *    Ahmed Aadel - initial API and implementation     
 *******************************************************************************/
package org.eclipse.ecf.provider.zookeeper.core.internal;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.provider.zookeeper.core.DefaultDiscoveryConfig;
import org.eclipse.ecf.provider.zookeeper.core.IDiscoveryConfig;
import org.eclipse.ecf.provider.zookeeper.core.ZooDiscoveryContainer.FLAVOR;
import org.eclipse.ecf.provider.zookeeper.util.Geo;
import org.eclipse.ecf.provider.zookeeper.util.Logger;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class Configuration extends DefaultDiscoveryConfig {

    private File zooConfFile;

    private File zookeeperDataFile;

    private ServiceReference reference;

    private List<String> serverIps = new ArrayList<String>();

    private FLAVOR flavor;

    //$NON-NLS-1$
    private static final String LOCALHOST = "localhost";

    public  Configuration(ServiceReference reference) {
        Assert.isNotNull(reference);
        Set<String> legalKeys = getConfigProperties().keySet();
        for (String key : reference.getPropertyKeys()) {
            if (legalKeys.contains(key) || key.startsWith(DefaultDiscoveryConfig.ZOODISCOVERY_PREFIX))
                getConfigProperties().put(key, reference.getProperty(key));
        }
    }

    public  Configuration(ID targetId) {
        this(targetId.getName());
    }

    public  Configuration(String propsAsString) {
        Assert.isNotNull(propsAsString);
        //$NON-NLS-1$
        String ss[] = propsAsString.split(";");
        for (String s : ss) {
            //$NON-NLS-1$
            String key_value[] = s.split("=");
            if (key_value.length == 2)
                defaultConfigProperties.put(key_value[0], key_value[1]);
        }
    }

    public Configuration configure() {
        PrintWriter writer = null;
        boolean isNewZookeeperData = false;
        try {
            String dataDirName = (String) getConfigProperties().get(ZOOKEEPER_DATADIR);
            // if no data directory name is specified, we randomly pick one.
            if (DATADIR_DEFAULT.equals(dataDirName)) {
                dataDirName = randomDirName();
            }
            this.zookeeperDataFile = new File(new File(getConfigProperties().get(ZOOKEEPER_TEMPDIR).toString()), dataDirName);
            isNewZookeeperData = this.zookeeperDataFile.mkdir();
            this.zookeeperDataFile.deleteOnExit();
            if (!isNewZookeeperData) {
                /*
				 * the same data directory is being reused, we try emptying it
				 * to avoid data corruption
				 */
                clean();
            }
            //$NON-NLS-1$
            this.zooConfFile = new File(this.zookeeperDataFile, "zoo.cfg");
            this.zooConfFile.createNewFile();
            this.zooConfFile.deleteOnExit();
            if (getConfigProperties().containsKey(ZOODISCOVERY_FLAVOR_CENTRALIZED)) {
                this.setFlavor(FLAVOR.CENTRALIZED);
                this.serverIps = parseIps();
                if (this.serverIps.size() != 1) {
                    String msg = "ZooDiscovery property " + ZOODISCOVERY_FLAVOR_CENTRALIZED + " must contain exactly one IP address designating the location of the ZooDiscovery instance playing this central role.";
                    Logger.log(LogService.LOG_ERROR, msg, null);
                    throw new ServiceException(msg);
                }
            } else if (getConfigProperties().containsKey(ZOODISCOVERY_FLAVOR_REPLICATED)) {
                this.setFlavor(FLAVOR.REPLICATED);
                this.serverIps = parseIps();
                if (!this.serverIps.contains(Geo.getHost())) {
                    this.serverIps.add(Geo.getHost());
                }
                if (this.serverIps.size() < 2) {
                    String msg = "Industrial Discovery property " + IDiscoveryConfig.ZOODISCOVERY_FLAVOR_REPLICATED + " must contain at least one IP address which is not localhost.";
                    Logger.log(LogService.LOG_ERROR, msg, null);
                    throw new ServiceException(msg);
                }
            } else if (getConfigProperties().containsKey(ZOODISCOVERY_FLAVOR_STANDALONE)) {
                this.setFlavor(FLAVOR.STANDALONE);
                this.serverIps = parseIps();
            }
            Collections.sort(this.serverIps);
            if (this.isQuorum()) {
                String myip = Geo.getHost();
                int myId = this.serverIps.indexOf(myip);
                File myIdFile = new //$NON-NLS-1$
                File(//$NON-NLS-1$
                getZookeeperDataFile(), //$NON-NLS-1$
                "myid");
                myIdFile.createNewFile();
                myIdFile.deleteOnExit();
                writer = new PrintWriter(myIdFile);
                writer.print(myId);
                writer.flush();
                writer.close();
            }
            writer = new PrintWriter(this.zooConfFile);
            if (this.isQuorum()) {
                for (int i = 0; i < this.serverIps.size(); i++) {
                    //$NON-NLS-1$
                    writer.println("server." + //$NON-NLS-1$
                    i + "=" + //$NON-NLS-1$
                    this.serverIps.get(i) + ":" + //$NON-NLS-1$
                    getServerPort() + ":" + getElectionPort());
                }
            }
            for (String k : getConfigProperties().keySet()) {
                if (k.startsWith(ZOODISCOVERY_PREFIX)) {
                    /*
					 * Ignore properties that are not intended for ZooKeeper
					 * internal configuration
					 */
                    continue;
                }
                writer.println(k + "=" + //$NON-NLS-1$
                getConfigProperties().get(//$NON-NLS-1$
                k));
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Logger.log(LogService.LOG_ERROR, e.getMessage(), e);
        } finally {
            if (writer != null)
                writer.close();
        }
        return this;
    }

    private String randomDirName() {
        String name = UUID.randomUUID() + "";
        name = name.replaceAll("-", "");
        return "zdd" + name;
    }

    public int getElectionPort() {
        return Integer.parseInt((String) getConfigProperties().get(ZOOKEEPER_ELECTION_PORT));
    }

    public String getConfFile() {
        return this.zooConfFile.toString();
    }

    public String getServerIps() {
        //$NON-NLS-1$
        String ipsString = "";
        for (String i : this.serverIps) {
            //$NON-NLS-1$ //$NON-NLS-2$
            ipsString += i + ",";
        }
        //$NON-NLS-1$
        return ipsString.substring(0, ipsString.lastIndexOf(","));
    }

    public int getClientPort() {
        return Integer.parseInt((String) getConfigProperties().get(ZOOKEEPER_CLIENTPORT));
    }

    public List<String> getServerIpsAsList() {
        return this.serverIps;
    }

    public File getZookeeperDataFile() {
        return this.zookeeperDataFile;
    }

    public void setFlavor(FLAVOR flavor) {
        this.flavor = flavor;
    }

    public FLAVOR getFlavor() {
        return this.flavor;
    }

    public boolean isQuorum() {
        return this.flavor == FLAVOR.REPLICATED;
    }

    public boolean isCentralized() {
        return this.flavor == FLAVOR.CENTRALIZED;
    }

    public boolean isStandAlone() {
        return this.flavor == FLAVOR.STANDALONE;
    }

    public ServiceReference getReference() {
        return this.reference;
    }

    private void clean() {
        for (File file : this.zookeeperDataFile.listFiles()) {
            try {
                if (file.isDirectory()) {
                    for (File f : file.listFiles()) f.delete();
                }
                file.delete();
            } catch (Throwable t) {
                Logger.log(LogService.LOG_ERROR, t.getMessage(), null);
            }
        }
    }

    private List<String> parseIps() {
        List<String> ips = Arrays.asList(((String) getConfigProperties().get(//$NON-NLS-1$
        flavor.toString())).split(//$NON-NLS-1$
        ","));
        List<String> unfixedSize = new ArrayList<String>();
        for (String ip : ips) {
            if (ip.contains(LOCALHOST))
                ip = ip.replace(LOCALHOST, Geo.getHost());
            unfixedSize.add(ip);
        }
        Collections.sort(unfixedSize);
        return unfixedSize;
    }

    public String toString() {
        String s = flavor.name();
        for (Object o : parseIps()) s += o;
        return s;
    }

    public int getTickTime() {
        return Integer.parseInt((String) getConfigProperties().get(ZOOKEEPER_TICKTIME));
    }

    public int getServerPort() {
        return Integer.parseInt((String) getConfigProperties().get(ZOOKEEPER_SERVER_PORT));
    }
}
