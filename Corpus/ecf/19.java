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
package org.eclipse.ecf.provider.zookeeper.node.internal;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.provider.zookeeper.core.AdvertisedService;
import org.eclipse.ecf.provider.zookeeper.core.internal.Localizer;
import org.eclipse.ecf.provider.zookeeper.core.internal.Notification;
import org.eclipse.ecf.provider.zookeeper.util.Logger;
import org.eclipse.ecf.provider.zookeeper.util.PrettyPrinter;
import org.osgi.service.log.LogService;

public class NodeWriter {

    private INode node;

    private String ip;

    private WriteRoot writeRoot;

    public  NodeWriter(INode node, WriteRoot writeRoot) {
        Assert.isNotNull(node);
        Assert.isNotNull(writeRoot);
        this.writeRoot = writeRoot;
        this.node = node;
    }

    public String getPath() {
        return this.node.getPath();
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return this.ip;
    }

    public synchronized void publish() {
        try {
            String parentPath = this.getNode().getAbsolutePath();
            Stat stat = this.writeRoot.getWriteKeeper().exists(parentPath, false);
            if (stat == null) {
                this.writeRoot.getWriteKeeper().create(parentPath, ((AdvertisedService) this.getNode().getWrappedService()).getPropertiesAsBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            }
            Logger.log(LogService.LOG_INFO, PrettyPrinter.prompt(PrettyPrinter.PUBLISHED, this.getNode().getWrappedService()), null);
        } catch (KeeperException e) {
            if (e.code() == KeeperException.Code.CONNECTIONLOSS) {
                Logger.log(LogService.LOG_ERROR, "Can't connect to server! " + e.getMessage(), e);
            }
        } catch (InterruptedException e) {
        }
    }

    public synchronized void remove() {
        if (this.writeRoot.isConnected()) {
            try {
                String nodePath = this.getNode().getAbsolutePath();
                Stat stat = this.writeRoot.getWriteKeeper().exists(nodePath, false);
                if (stat == null) {
                    // nothing to remove
                    return;
                }
                // delete node
                this.writeRoot.getWriteKeeper().delete(nodePath, -1);
                Localizer.getSingleton().localize(new Notification(this.getNode().getWrappedService(), Notification.UNAVAILABLE));
                Logger.log(LogService.LOG_INFO, PrettyPrinter.prompt(PrettyPrinter.UNPUBLISHED, this.getNode().getWrappedService()), null);
            } catch (KeeperException e) {
            } catch (InterruptedException e) {
            }
        } else if (!this.writeRoot.isConnected() && writeRoot.getWatchManager().getConfig().isCentralized()) {
            /*
			 * connection lost to the central ZooDiscovery where our services
			 * are published to.
			 */
            Logger.log(LogService.LOG_INFO, PrettyPrinter.prompt(PrettyPrinter.UNPUBLISHED, this.getNode().getWrappedService()), null);
        }
    }

    public INode getNode() {
        return this.node;
    }
}
