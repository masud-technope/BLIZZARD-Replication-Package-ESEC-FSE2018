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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @since 3.0
 */
public class DiscoveryHandlerUtil {

    public static IServiceInfo getActiveIServiceInfoChecked(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
        org.eclipse.ecf.discovery.ui.model.IServiceInfo serviceInfo = toIServiceInfo(selection);
        if (serviceInfo == null) {
            return null;
        }
        return serviceInfo.getEcfServiceInfo();
    }

    public static IServiceInfo getActiveIServiceInfo(ExecutionEvent event) {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        org.eclipse.ecf.discovery.ui.model.IServiceInfo serviceInfo = toIServiceInfo(selection);
        if (serviceInfo == null) {
            return null;
        }
        return serviceInfo.getEcfServiceInfo();
    }

    private static org.eclipse.ecf.discovery.ui.model.IServiceInfo toIServiceInfo(ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            return (org.eclipse.ecf.discovery.ui.model.IServiceInfo) ((IStructuredSelection) selection).getFirstElement();
        }
        return null;
    }
}
