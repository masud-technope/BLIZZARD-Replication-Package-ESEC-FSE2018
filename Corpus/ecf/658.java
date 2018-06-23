/*******************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.example.collab.ui.hyperlink;

import java.net.URI;
import org.eclipse.ecf.ui.hyperlink.AbstractURLHyperlinkDetector;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public class ECFGenericHyperlinkDetector extends AbstractURLHyperlinkDetector {

    //$NON-NLS-1$
    public static final String ECFGENERIC_PROTOCOL = "ecftcp";

    public  ECFGenericHyperlinkDetector() {
        setProtocols(new String[] { ECFGENERIC_PROTOCOL });
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.ui.hyperlink.AbstractURLHyperlinkDetector#createHyperLinksForURI(org.eclipse.jface.text.IRegion,
	 *      java.net.URI)
	 */
    protected IHyperlink[] createHyperLinksForURI(IRegion region, URI uri) {
        return new IHyperlink[] { new ECFGenericHyperlink(region, uri) };
    }
}
