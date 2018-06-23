/*******************************************************************************
 * Copyright (c) 2009 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.discovery.ui.browser;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.ui.DiscoveryHandlerUtil;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

public class OpenBrowserCommand extends AbstractHandler {

    /* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
    public Object execute(ExecutionEvent event) throws ExecutionException {
        // get service
        final IServiceInfo serviceInfo = DiscoveryHandlerUtil.getActiveIServiceInfoChecked(event);
        final URI location = serviceInfo.getLocation();
        // open browser view or reuse existing if already open
        final IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
        try {
            final URL url = location.toURL();
            support.createBrowser(url.toExternalForm()).openURL(url);
        } catch (PartInitException e) {
            throw new ExecutionException(e.getMessage(), e);
        } catch (MalformedURLException e) {
            throw new ExecutionException(e.getMessage(), e);
        }
        return null;
    }
}
