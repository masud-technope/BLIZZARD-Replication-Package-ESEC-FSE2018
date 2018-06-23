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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.provider.zookeeper.core.DiscoverdService;
import org.eclipse.ecf.provider.zookeeper.core.internal.Localizer;
import org.eclipse.ecf.provider.zookeeper.core.internal.Notification;
import org.eclipse.ecf.provider.zookeeper.util.Logger;
import org.eclipse.ecf.provider.zookeeper.util.PrettyPrinter;
import org.osgi.service.log.LogService;

public class NodeReader implements Watcher, org.apache.zookeeper.AsyncCallback.DataCallback {

    private String path;

    private DiscoverdService discovered;

    private ZooKeeper zookeeper;

    private String ip;

    boolean isNodePublished;

    private boolean isDisposed;

    private ReadRoot readRoot;

    public  NodeReader(String path, ReadRoot readRoot) {
        Assert.isNotNull(path);
        Assert.isNotNull(readRoot);
        this.readRoot = readRoot;
        this.path = path;
        this.zookeeper = readRoot.getReadKeeper();
        this.ip = readRoot.getIp();
        this.zookeeper.getData(getAbsolutePath(), this, this, null);
        this.zookeeper.exists(getAbsolutePath(), this, null, null);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public String getAbsolutePath() {
        return INode.ROOT_SLASH + getPath();
    }

    public synchronized void processResult(int rc, String p, Object ctx, byte[] data, Stat stat) {
        if (p == null || !p.equals(getAbsolutePath()) || data == null) {
            return;
        }
        ObjectInputStream objin = null;
        ByteArrayInputStream bain = null;
        Map<String, Object> serviceData = null;
        try {
            bain = new ByteArrayInputStream(data);
            objin = new ObjectInputStream(bain);
            try {
                serviceData = (Map<String, Object>) objin.readObject();
            } catch (ClassNotFoundException e) {
                Logger.log(LogService.LOG_ERROR, "NodeReader.processResult: " + e.getMessage(), e);
            }
            if (serviceData == null || serviceData.isEmpty()) {
                return;
            }
            this.discovered = new DiscoverdService(getPath(), serviceData);
            readRoot.getDiscoverdServices().put(this.discovered.getServiceID().getServiceTypeID().getName(), this.discovered);
            Logger.log(LogService.LOG_DEBUG, PrettyPrinter.prompt(PrettyPrinter.REMOTE_AVAILABLE, this.discovered), null);
            Localizer.getSingleton().localize(new Notification(this.discovered, Notification.AVAILABLE));
        } catch (IOException e) {
            Logger.log(LogService.LOG_DEBUG, e.getMessage(), e);
        } finally {
            if (objin != null) {
                try {
                    objin.close();
                } catch (IOException e) {
                }
            }
            if (bain != null) {
                try {
                    bain.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return this.ip;
    }

    public void process(WatchedEvent event) {
        if (this.isDisposed) {
            // Already disposed
            return;
        }
        if (event.getState() == KeeperState.Disconnected || event.getState() == KeeperState.Expired || event.getType() == EventType.NodeDeleted) {
            /*
			 * This node is deleted or the connection with the server we're
			 * reading from is down. This discovered service wrapped by this
			 * node is no more available.
			 */
            dispose();
        }
    }

    public synchronized void dispose() {
        if (isDisposed || discovered == null)
            return;
        if (null != readRoot.getDiscoverdServices().remove(this.discovered.getServiceID().getServiceTypeID().getName())) {
            this.discovered.dispose();
            isDisposed = true;
        }
    }
}
