/*******************************************************************************
 * Copyright (c) 2008 Abner Ballardo and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Abner Ballardo <modlost@modlost.net> - initial API and implementation via bug 197745
 ******************************************************************************/
package org.eclipse.ecf.internal.irc.ui.hyperlink;

import java.net.URI;
import org.eclipse.ecf.ui.hyperlink.AbstractURLHyperlinkDetector;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public class IRCHyperlinkDetector extends AbstractURLHyperlinkDetector {

    //$NON-NLS-1$
    public static final String IRC_PROTOCOL = "irc";

    public  IRCHyperlinkDetector() {
        setProtocols(new String[] { IRC_PROTOCOL });
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.ui.hyperlink.AbstractURLHyperlinkDetector#createHyperLinksForURI(org.eclipse.jface.text.IRegion, java.net.URI)
	 */
    protected IHyperlink[] createHyperLinksForURI(IRegion region, URI uri) {
        return new IHyperlink[] { new IRCHyperlink(region, uri) };
    }
}
