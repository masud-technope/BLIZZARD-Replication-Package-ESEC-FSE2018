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
package org.eclipse.ecf.provider.zookeeper.node.internal;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.provider.zookeeper.core.ZooDiscoveryContainer;
import org.eclipse.ecf.provider.zookeeper.util.Logger;
import org.eclipse.ecf.provider.zookeeper.util.PrettyPrinter;
import org.osgi.service.log.LogService;

class WriteRoot implements Watcher {

    private ZooKeeper writeKeeper;

    private String ip;

    private WatchManager watchManager;

    private boolean isConnected;

    private Object connectionLock = new Object();

     WriteRoot(String ip, WatchManager watchManager) {
        Assert.isNotNull(ip);
        Assert.isNotNull(watchManager);
        this.ip = ip;
        this.watchManager = watchManager;
        initWriteKeeper();
    }

    @SuppressWarnings({ "incomplete-switch" })
    public void process(final WatchedEvent event) {
        ZooDiscoveryContainer.CACHED_THREAD_POOL.execute(new Runnable() {

            public void run() {
                synchronized (connectionLock) {
                    switch(event.getState()) {
                        case Disconnected:
                            isConnected = false;
                            watchManager.unpublishAll();
                            connect();
                            break;
                        case Expired:
                            isConnected = false;
                            watchManager.unpublishAll();
                            connect();
                            break;
                        case SyncConnected:
                            if (!isConnected) {
                                isConnected = true;
                                watchManager.addZooKeeper(writeKeeper);
                                watchManager.republishAll();
                            }
                            break;
                    }
                }
            }
        });
    }

    private void connect() {
        synchronized (connectionLock) {
            if (this.isConnected || watchManager.isDisposed()) {
                return;
            }
            try {
                if (writeKeeper != null) {
                    writeKeeper.close();
                    watchManager.removeZooKeeper(writeKeeper);
                    writeKeeper = null;
                }
                writeKeeper = new ZooKeeper(this.ip, 3000, this);
            } catch (Exception e) {
                Logger.log(LogService.LOG_DEBUG, e.getMessage(), e);
            }
        }
    }

    private void initWriteKeeper() {
        try {
            if (watchManager.getConfig().isQuorum() || watchManager.getConfig().isStandAlone()) {
                // we write nodes locally but we should check for client port.
                int port = watchManager.getConfig().getClientPort();
                if (port != 0)
                    //$NON-NLS-1$
                    ip += ":" + port;
            } else if (watchManager.getConfig().isCentralized()) {
                // we write nodes to the machine with this specified IP address.
                ip = watchManager.getConfig().getServerIps();
            }
            try {
                writeKeeper = new ZooKeeper(this.ip, 3000, this);
            } catch (Exception e) {
                Logger.log(LogService.LOG_ERROR, "Fatal error while initializing a zookeeper client to write to: " + ip, e);
                throw new IllegalStateException(e);
            }
            while (!this.isConnected) {
                synchronized (connectionLock) {
                    if (watchManager.isDisposed()) {
                        // no need for connecting, we're disposed.
                        try {
                            writeKeeper.close();
                        } catch (Throwable t) {
                        }
                        break;
                    }
                    try {
                        Stat s = this.writeKeeper.exists(INode.ROOT, this);
                        this.isConnected = true;
                        if (s == null) {
                            writeKeeper.create(INode.ROOT, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                        }
                    } catch (KeeperException e) {
                        if (e.code().equals(KeeperException.Code.CONNECTIONLOSS)) {
                            isConnected = false;
                            PrettyPrinter.attemptingConnectionTo(this.ip);
                        } else
                            Logger.log(LogService.LOG_ERROR, "Error while trying to connect to " + this.ip, e);
                    }
                }
            }
            synchronized (this) {
                this.notifyAll();
            }
        } catch (Exception e) {
            Logger.log(LogService.LOG_DEBUG, e.getMessage(), e);
        }
    }

    public ZooKeeper getWriteKeeper() {
        return writeKeeper;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public WatchManager getWatchManager() {
        return watchManager;
    }
}
