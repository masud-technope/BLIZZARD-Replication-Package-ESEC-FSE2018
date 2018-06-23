/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.provider.bittorrent.ui;

import java.net.URI;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.ui.IConnectWizard;
import org.eclipse.ecf.ui.hyperlink.AbstractURLHyperlink;
import org.eclipse.jface.text.IRegion;

/**
 * 
 */
public class BitTorrentHyperlink extends AbstractURLHyperlink {

    //$NON-NLS-1$
    private static final String ECF_BITTORRENT_CONTAINER_NAME = "ecf.filetransfer.bittorrent";

    /**
	 * Creates a new URL hyperlink.
	 * 
	 * @param region
	 * @param uri
	 */
    public  BitTorrentHyperlink(IRegion region, URI uri) {
        super(region, uri);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.ui.hyperlink.AbstractURLHyperlink#createConnectWizard()
	 */
    protected IConnectWizard createConnectWizard() {
        String schemeSpecificPart = getURI().getSchemeSpecificPart();
        while (schemeSpecificPart.startsWith("/")) schemeSpecificPart = schemeSpecificPart.substring(1);
        return new BitTorrentConnectWizard(schemeSpecificPart);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.ui.hyperlink.AbstractURLHyperlink#createContainer()
	 */
    protected IContainer createContainer() throws ContainerCreateException {
        return ContainerFactory.getDefault().createContainer(ECF_BITTORRENT_CONTAINER_NAME);
    }
}
