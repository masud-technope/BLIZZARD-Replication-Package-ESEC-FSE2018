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
package org.eclipse.ecf.provider.zookeeper.core;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.zookeeper.server.NIOServerCnxn;
import org.apache.zookeeper.server.NIOServerCnxn.Factory;
import org.apache.zookeeper.server.PurgeTxnLog;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.server.quorum.QuorumPeer;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.discovery.AbstractDiscoveryContainerAdapter;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.IServiceListener;
import org.eclipse.ecf.discovery.IServiceTypeListener;
import org.eclipse.ecf.discovery.identity.IServiceID;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.provider.zookeeper.core.internal.Advertiser;
import org.eclipse.ecf.provider.zookeeper.core.internal.Configuration;
import org.eclipse.ecf.provider.zookeeper.core.internal.Configurator;
import org.eclipse.ecf.provider.zookeeper.core.internal.Localizer;
import org.eclipse.ecf.provider.zookeeper.core.internal.Notification;
import org.eclipse.ecf.provider.zookeeper.node.internal.WatchManager;
import org.eclipse.ecf.provider.zookeeper.util.Geo;
import org.eclipse.ecf.provider.zookeeper.util.Logger;
import org.eclipse.ecf.provider.zookeeper.util.PrettyPrinter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class ZooDiscoveryContainer extends AbstractDiscoveryContainerAdapter {

    private static ZooDiscoveryContainer discovery;

    public static ExecutorService CACHED_THREAD_POOL = Executors.newCachedThreadPool();

    private QuorumPeer quorumPeer;

    private Properties DiscoveryProperties;

    protected Advertiser advertiser;

    protected Localizer localizer;

    private static ZooKeeperServer zooKeeperServer;

    private ID targetId;

    protected boolean isQuorumPeerReady;

    private boolean isConnected;

    private boolean isDisposed;

    private WatchManager watchManager;

    public enum FLAVOR implements  {

        STANDALONE() {
        }
        , CENTRALIZED() {
        }
        , REPLICATED() {
        }
        ;

        public String toString() {
            switch(this) {
                case STANDALONE:
                    return DefaultDiscoveryConfig.ZOODISCOVERY_FLAVOR_STANDALONE;
                case CENTRALIZED:
                    return DefaultDiscoveryConfig.ZOODISCOVERY_FLAVOR_CENTRALIZED;
                case REPLICATED:
                    return DefaultDiscoveryConfig.ZOODISCOVERY_FLAVOR_REPLICATED;
            }
            throw new AssertionError("Unsupported configuration");
        }
    }

    private  ZooDiscoveryContainer() {
        super(ZooDiscoveryNamespace.NAME, Configurator.INSTANCE);
        DiscoveryProperties = new Properties();
        if (autoStart()) {
            try {
                this.targetId = this.getConnectNamespace().createInstance(new String[] { DefaultDiscoveryConfig.getDefaultTarget() });
                init(targetId);
            } catch (Exception e) {
                Logger.log(LogService.LOG_ERROR, e.getMessage(), e);
            }
        }
        Logger.log(LogService.LOG_INFO, PrettyPrinter.prompt(PrettyPrinter.ACTIVATED, null), null);
    }

    /**
	 * @return true if the service is in autoStart mode.
	 */
    public static boolean autoStart() {
        return System.getProperty(DefaultDiscoveryConfig.ZOODISCOVERY_PREFIX + DefaultDiscoveryConfig.ZOOKEEPER_AUTOSTART) != null;
    }

    public static synchronized ZooDiscoveryContainer getSingleton() {
        if (discovery == null) {
            discovery = new ZooDiscoveryContainer();
        }
        discovery.setDisposed(false);
        return discovery;
    }

    public void init(ServiceReference reference) {
        Configuration conf = Configurator.INSTANCE.createConfig(reference).configure();
        doStart(conf);
    }

    private void init(ID targetID) {
        Configuration conf = Configurator.INSTANCE.createConfig(targetID).configure();
        doStart(conf);
    }

    private void doStart(final Configuration conf) {
        if (watchManager != null && !watchManager.isDisposed())
            return;
        watchManager = new WatchManager(conf);
        this.advertiser = Advertiser.getSingleton(watchManager);
        this.localizer = Localizer.getSingleton();
        if (conf.isCentralized()) {
            if (//$NON-NLS-1$
            Geo.getHost().equals(conf.getServerIps().split(":")[0])) {
                CACHED_THREAD_POOL.execute(new Runnable() {

                    public void run() {
                        startStandAlone(conf);
                        watchManager.watch();
                        ZooDiscoveryContainer.this.localizer.init();
                    }
                });
            } else {
                watchManager.watch();
                this.localizer.init();
            }
        } else if (conf.isQuorum()) {
            CACHED_THREAD_POOL.execute(new Runnable() {

                public void run() {
                    startQuorumPeer(conf);
                    watchManager.watch();
                    ZooDiscoveryContainer.this.localizer.init();
                }
            });
        } else if (conf.isStandAlone()) {
            CACHED_THREAD_POOL.execute(new Runnable() {

                public void run() {
                    startStandAlone(conf);
                    watchManager.watch();
                    ZooDiscoveryContainer.this.localizer.init();
                }
            });
        }
    }

    /**
	 * Start a ZooKeeer server locally to write nodes to. Implied by
	 * {@link IDiscoveryConfig#ZOODISCOVERY_FLAVOR_STANDALONE} configuration.
	 * 
	 * @param conf
	 */
    void startStandAlone(final Configuration conf) {
        if (ZooDiscoveryContainer.zooKeeperServer != null && ZooDiscoveryContainer.zooKeeperServer.isRunning())
            return;
        else if (ZooDiscoveryContainer.zooKeeperServer != null && !ZooDiscoveryContainer.zooKeeperServer.isRunning())
            try {
                ZooDiscoveryContainer.zooKeeperServer.startup();
                return;
            } catch (Exception e) {
                Logger.log(LogService.LOG_DEBUG, "Zookeeper server cannot be started! ", e);
            }
        try {
            ZooDiscoveryContainer.zooKeeperServer = new ZooKeeperServer();
            FileTxnSnapLog fileTxnSnapLog = new FileTxnSnapLog(conf.getZookeeperDataFile(), conf.getZookeeperDataFile());
            ZooDiscoveryContainer.zooKeeperServer.setTxnLogFactory(fileTxnSnapLog);
            ZooDiscoveryContainer.zooKeeperServer.setTickTime(conf.getTickTime());
            Factory cnxnFactory = new NIOServerCnxn.Factory(new InetSocketAddress(conf.getClientPort()));
            cnxnFactory.startup(ZooDiscoveryContainer.zooKeeperServer);
        } catch (Exception e) {
            Logger.log(LogService.LOG_ERROR, "Zookeeper server cannot be started! Possibly another instance is already running on the same port. ", e);
        }
    }

    /**
	 * Start a local ZooKeeer server to write nodes to. It plays as a peer
	 * within a replicated servers configuration. Implied by
	 * {@link IDiscoveryConfig#ZOODISCOVERY_FLAVOR_REPLICATED} configuration.
	 * 
	 * @param conf
	 */
    void startQuorumPeer(final Configuration conf) {
        if (this.quorumPeer != null && this.quorumPeer.isAlive()) {
            return;
        } else if (this.quorumPeer != null && !this.quorumPeer.isAlive()) {
            this.quorumPeer.start();
            return;
        }
        try {
            final QuorumPeerConfig quorumPeerConfig = new QuorumPeerConfig();
            quorumPeerConfig.parse(conf.getConfFile());
            QuorumPeer.Factory qpFactory = new QuorumPeer.Factory() {

                public QuorumPeer create(NIOServerCnxn.Factory cnxnFactory) throws IOException {
                    ServerConfig serverConfig = new ServerConfig();
                    serverConfig.readFrom(quorumPeerConfig);
                    QuorumPeer peer = new QuorumPeer(quorumPeerConfig.getServers(), new File(serverConfig.getDataDir()), new File(serverConfig.getDataLogDir()), quorumPeerConfig.getElectionAlg(), quorumPeerConfig.getServerId(), quorumPeerConfig.getTickTime(), quorumPeerConfig.getInitLimit(), quorumPeerConfig.getSyncLimit(), cnxnFactory, quorumPeerConfig.getQuorumVerifier());
                    ZooDiscoveryContainer.this.quorumPeer = peer;
                    return peer;
                }

                public NIOServerCnxn.Factory createConnectionFactory() throws IOException {
                    return new NIOServerCnxn.Factory(quorumPeerConfig.getClientPortAddress());
                }
            };
            quorumPeer = qpFactory.create(qpFactory.createConnectionFactory());
            quorumPeer.start();
            quorumPeer.setDaemon(true);
            isQuorumPeerReady = true;
        } catch (Exception e) {
            Logger.log(LogService.LOG_ERROR, "Zookeeper quorum cannot be started! ", e);
            isQuorumPeerReady = false;
        }
    }

    public void setDiscoveryProperties(Properties discoveryProperties) {
        this.DiscoveryProperties = discoveryProperties;
    }

    public Properties getDiscoveryProperties() {
        return this.DiscoveryProperties;
    }

    public synchronized void shutdown() {
        if (isDisposed)
            return;
        try {
            if (watchManager != null) {
                watchManager.dispose();
            }
            if (this.localizer != null) {
                this.localizer.close();
            }
            if (ZooDiscoveryContainer.zooKeeperServer != null) {
                // purge snaps and logs. Keep only last three of each
                PurgeTxnLog.purge(ZooDiscoveryContainer.zooKeeperServer.getTxnLogFactory().getDataDir(), ZooDiscoveryContainer.zooKeeperServer.getTxnLogFactory().getSnapDir(), 3);
                ZooDiscoveryContainer.zooKeeperServer.shutdown();
            }
            if (this.quorumPeer != null) {
                // purge snaps and logs. Keep only last three of each
                PurgeTxnLog.purge(this.quorumPeer.getTxnFactory().getDataDir(), this.quorumPeer.getTxnFactory().getSnapDir(), 3);
                // shut down server
                if (this.quorumPeer.isAlive()) {
                    this.quorumPeer.shutdown();
                }
                // shutdown sockets
                this.quorumPeer.getCnxnFactory().shutdown();
            }
        } catch (Throwable t) {
            Logger.log(LogService.LOG_ERROR, t.getMessage(), t);
        }
        targetId = null;
        isConnected = false;
        isDisposed = true;
        discovery = null;
    }

    public ZooKeeperServer getLocalServer() {
        return ZooDiscoveryContainer.zooKeeperServer;
    }

    public void connect(ID id, IConnectContext connectContext) throws ContainerConnectException {
        if (isDisposed)
            throw new ContainerConnectException("Container already disposed!");
        if (this.isConnected)
            throw new ContainerConnectException("Container already connected!");
        this.targetId = id;
        if (this.targetId == null) {
            this.targetId = this.getConnectNamespace().createInstance(new String[] { DefaultDiscoveryConfig.getDefaultTarget() });
        }
        init(this.targetId);
        isConnected = true;
    }

    public void disconnect() {
        if (watchManager != null) {
            watchManager.dispose();
        }
        isConnected = false;
        targetId = null;
    }

    public Namespace getConnectNamespace() {
        return super.getConnectNamespace();
    }

    public ID getConnectedID() {
        if (!isConnected || isDisposed)
            return null;
        return this.targetId;
    }

    public IServiceInfo getServiceInfo(IServiceID serviceID) {
        Assert.isNotNull(serviceID);
        return watchManager.getAllKnownServices().get(serviceID.getName());
    }

    public IServiceTypeID[] getServiceTypes() {
        IServiceTypeID ids[] = new IServiceTypeID[getServices().length];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = getServices()[i].getServiceID().getServiceTypeID();
        }
        return ids;
    }

    public IServiceInfo[] getServices() {
        if (watchManager == null) {
            return new IServiceInfo[0];
        }
        return watchManager.getAllKnownServices().values().toArray(new IServiceInfo[watchManager.getAllKnownServices().size()]);
    }

    public IServiceInfo[] getServices(IServiceTypeID type) {
        Assert.isNotNull(type);
        if (watchManager == null) {
            return new IServiceInfo[0];
        }
        List<IServiceInfo> services = new ArrayList<IServiceInfo>();
        for (IServiceInfo sinfo : watchManager.getAllKnownServices().values()) {
            if (sinfo.getServiceID().getServiceTypeID().getInternal() == type.getInternal())
                services.add(sinfo);
        }
        return services.toArray(new IServiceInfo[services.size()]);
    }

    public Namespace getServicesNamespace() {
        return super.getServicesNamespace();
    }

    public synchronized void registerService(IServiceInfo serviceInfo) {
        Assert.isNotNull(serviceInfo);
        if (targetId == null) {
            this.targetId = this.getConnectNamespace().createInstance(new String[] { DefaultDiscoveryConfig.getDefaultTarget() });
            init(this.targetId);
        }
        if (serviceInfo instanceof AdvertisedService) {
            watchManager.publish((AdvertisedService) serviceInfo);
        } else {
            watchManager.publish(new AdvertisedService(serviceInfo));
        }
        Localizer.getSingleton().localize(new Notification(serviceInfo, Notification.AVAILABLE));
    }

    public void unregisterAllServices() {
        watchManager.unpublishAll();
    }

    public void unregisterService(IServiceInfo serviceInfo) {
        Assert.isNotNull(serviceInfo);
        watchManager.unpublish(serviceInfo.getServiceID().getName());
        Localizer.getSingleton().localize(new Notification(serviceInfo, Notification.UNAVAILABLE));
    }

    public Collection<IServiceListener> getAllServiceListeners() {
        return super.allServiceListeners;
    }

    public Collection<IServiceListener> getServiceListenersForType(IServiceTypeID type) {
        return super.getListeners(type);
    }

    public Collection<IServiceTypeListener> getServiceTypeListeners() {
        return super.serviceTypeListeners;
    }

    public void dispose() {
        shutdown();
        super.dispose();
    }

    public ID getID() {
        return Configurator.INSTANCE.getID();
    }

    public boolean isDisposed() {
        return this.isDisposed;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.discovery.AbstractDiscoveryContainerAdapter#getContainerName
	 * ()
	 */
    public String getContainerName() {
        return ZooDiscoveryContainerInstantiator.NAME;
    }

    private void setDisposed(boolean d) {
        this.isDisposed = d;
    }
}
