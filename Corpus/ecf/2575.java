/*******************************************************************************
 * Copyright (c) 2008 Versant Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Kuppe (mkuppe <at> versant <dot> com) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.discovery.ui;

import org.eclipse.ecf.discovery.IServiceInfo;

/**
 * @since 3.0
 */
public class DiscoveryPropertyTesterUtil {

    public static IServiceInfo getIServiceInfoReceiver(Object receiver) {
        if (receiver instanceof org.eclipse.ecf.discovery.ui.model.IServiceInfo) {
            org.eclipse.ecf.discovery.ui.model.IServiceInfo isi = (org.eclipse.ecf.discovery.ui.model.IServiceInfo) receiver;
            return isi.getEcfServiceInfo();
        }
        return null;
    }
}
