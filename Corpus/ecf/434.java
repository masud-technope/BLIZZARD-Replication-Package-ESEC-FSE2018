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

import org.eclipse.ecf.provider.zookeeper.node.internal.WatchManager;

public class Advertiser {

    private static Advertiser singleton;

    private WatchManager watcher;

    private  Advertiser(WatchManager watcher) {
        this.watcher = watcher;
        singleton = this;
    }

    public static Advertiser getSingleton(WatchManager watcher) {
        if (singleton == null)
            new Advertiser(watcher);
        return singleton;
    }

    public WatchManager getWather() {
        return this.watcher;
    }
}
