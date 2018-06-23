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
package org.eclipse.ecf.internal.irc.ui.hyperlink;

import java.net.URI;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.internal.irc.ui.wizards.IRCConnectWizard;
import org.eclipse.ecf.ui.IConnectWizard;
import org.eclipse.ecf.ui.hyperlink.AbstractURLHyperlink;
import org.eclipse.jface.text.IRegion;

/**
 * 
 */
public class IRCHyperlink extends AbstractURLHyperlink {

    //$NON-NLS-1$
    private static final String ECF_IRC_CONTAINER_NAME = "ecf.irc.irclib";

    /**
	 * Creates a new URL hyperlink.
	 * 
	 * @param region
	 * @param uri
	 */
    public  IRCHyperlink(IRegion region, URI uri) {
        super(region, uri);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.ui.hyperlink.AbstractURLHyperlink#createConnectWizard()
	 */
    protected IConnectWizard createConnectWizard() {
        final URI uri = getURI();
        String authAndPath = uri.getSchemeSpecificPart();
        //$NON-NLS-1$
        while (authAndPath.startsWith("/")) authAndPath = authAndPath.substring(1);
        final String fragment = uri.getFragment();
        if (fragment != null) {
            final StringBuffer buf = new StringBuffer(authAndPath);
            //$NON-NLS-1$
            buf.append("#").append(fragment);
            authAndPath = buf.toString();
        }
        return new IRCConnectWizard(authAndPath);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.ui.hyperlink.AbstractURLHyperlink#createContainer()
	 */
    protected IContainer createContainer() throws ContainerCreateException {
        return ContainerFactory.getDefault().createContainer(ECF_IRC_CONTAINER_NAME);
    }
}
