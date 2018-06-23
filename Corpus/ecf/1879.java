/*******************************************************************************
 *  Copyright (c)2011 REMAIN B.V. The Netherlands. (http://www.remainsoftware.com).
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

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.provider.zookeeper.core.DiscoverdService;
import org.eclipse.ecf.provider.zookeeper.core.ZooDiscoveryContainer;
import org.eclipse.ecf.provider.zookeeper.util.Geo;
import org.eclipse.ecf.provider.zookeeper.util.Logger;
import org.osgi.service.log.LogService;

public class ReadRoot implements Watcher, ChildrenCallback {

    ZooKeeper readKeeper;

    String ip;

    private final WatchManager watchManager;

    private boolean isConnected;

    private final Map<String, NodeReader> nodeReaders = Collections.synchronizedMap(new HashMap<String, NodeReader>());

    private final Map<String, DiscoverdService> discoverdServices;

    private final Map<String, List<DiscoverdService>> perTypeDiscoverdServices;

    private final Object connectionLock = new Object();

     ReadRoot(String ip, WatchManager watchManager) {
        Assert.isNotNull(ip);
        Assert.isNotNull(watchManager);
        this.ip = ip;
        this.watchManager = watchManager;
        discoverdServices = Collections.synchronizedMap(new HashMap<String, DiscoverdService>());
        perTypeDiscoverdServices = Collections.synchronizedMap(new HashMap<String, List<DiscoverdService>>());
        connect();
    }

    public void process(final WatchedEvent event) {
        ZooDiscoveryContainer.CACHED_THREAD_POOL.execute(new Runnable() {

            public void run() {
                synchronized (connectionLock) {
                    if (watchManager.isDisposed())
                        return;
                    switch(event.getState()) {
                        case Disconnected:
                            if (!ReadRoot.this.readKeeper.getState().isAlive()) {
                                ReadRoot.this.isConnected = false;
                                connect();
                            }
                            break;
                        case Expired:
                            ReadRoot.this.isConnected = false;
                            connect();
                            break;
                        case SyncConnected:
                            if (!ReadRoot.this.isConnected) {
                                ReadRoot.this.isConnected = true;
                                ReadRoot.this.watchManager.addZooKeeper(ReadRoot.this.readKeeper);
                                ReadRoot.this.readKeeper.exists(INode.ROOT, ReadRoot.this, null, null);
                                ReadRoot.this.readKeeper.getChildren(INode.ROOT, ReadRoot.this, ReadRoot.this, null);
                            }
                            break;
                    }
                    switch(event.getType()) {
                        case NodeDeleted:
                            if (event.getPath() == null || event.getPath().equals(INode.ROOT))
                                break;
                            ReadRoot.this.nodeReaders.remove(event.getPath());
                            break;
                        case NodeChildrenChanged:
                            if (ReadRoot.this.isConnected) {
                                ReadRoot.this.readKeeper.exists(INode.ROOT, ReadRoot.this, null, null);
                                ReadRoot.this.readKeeper.getChildren(INode.ROOT, ReadRoot.this, ReadRoot.this, null);
                            }
                            break;
                    }
                }
            }
        });
    }

    private void connect() {
        synchronized (connectionLock) {
            if (this.isConnected || watchManager.isDisposed())
                return;
            this.nodeReaders.clear();
            if (this.readKeeper != null) {
                // discard the current stale reader
                try {
                    this.readKeeper.close();
                } catch (InterruptedException e) {
                    Logger.log(LogService.LOG_ERROR, "Error while closing the current ZooKeeper: " + e.getMessage(), e);
                }
                this.watchManager.removeZooKeeper(this.readKeeper);
                this.readKeeper = null;
            }
            try {
                // try reconnecting
                this.readKeeper = new ZooKeeper(this.ip, 3000, this);
            } catch (IOException ioe) {
                Logger.log(LogService.LOG_ERROR, "Cannot initiate a new ZooKeeper: " + ioe.getMessage(), ioe);
            }
        }
    }

    public void processResult(int rc, final String path, Object ctx, final List<String> children) {
        ZooDiscoveryContainer.CACHED_THREAD_POOL.execute(new Runnable() {

            public void run() {
                synchronized (connectionLock) {
                    if (watchManager.isDisposed())
                        return;
                    if (path == null || children == null || children.size() == 0) {
                        return;
                    }
                    for (String p : children) {
                        if (Geo.isOwnPublication(p)) {
                            /* own publications need not to be discovered */
                            continue;
                        }
                        if (!ReadRoot.this.nodeReaders.containsKey(p)) {
                            /* launch a new reader to handle this node's data */
                            NodeReader nr = new NodeReader(p, ReadRoot.this);
                            /* watch this very path for deletion */
                            ReadRoot.this.readKeeper.exists(nr.getAbsolutePath(), ReadRoot.this, null, null);
                            ReadRoot.this.nodeReaders.put(nr.getPath(), nr);
                        }
                    }
                }
            }
        });
    }

    public ZooKeeper getReadKeeper() {
        return this.readKeeper;
    }

    public String getIp() {
        return this.ip;
    }

    public Map<String, DiscoverdService> getDiscoverdServices() {
        return discoverdServices;
    }

    public Map<String, List<DiscoverdService>> getPerTypeDiscoverdServices() {
        return perTypeDiscoverdServices;
    }
}
