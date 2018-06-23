/*******************************************************************************
 * Copyright (c) 2005, 2006 Erkki Lindpere and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Erkki Lindpere - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.provider.phpbb.identity;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.eclipse.ecf.internal.bulletinboard.commons.util.IDUtil;

public class GuestID extends PHPBBID {

    private static final long serialVersionUID = 6005970691318023633L;

    public  GuestID(PHPBBNamespace namespace, URI uri) throws URISyntaxException {
        super(namespace, uri);
    }

    public  GuestID(PHPBBNamespace namespace, URL baseURL, String name) throws URISyntaxException {
        super(namespace, IDUtil.composeURI(baseURL, "profile.php?mode=viewprofile&u=0#" + name));
    }

    public long getLongValue() {
        return 0;
    }
}
