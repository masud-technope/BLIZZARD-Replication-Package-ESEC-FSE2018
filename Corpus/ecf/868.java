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
package org.eclipse.ecf.internal.provider.vbulletin;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import org.eclipse.ecf.bulletinboard.IBBObject;
import org.eclipse.ecf.bulletinboard.IMember;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.internal.bulletinboard.commons.IBBObjectFactory;
import org.eclipse.ecf.internal.provider.vbulletin.identity.ThreadID;
import org.eclipse.ecf.internal.provider.vbulletin.identity.VBNamespace;

public class ThreadFactory implements IBBObjectFactory {

    public ID createBBObjectId(Namespace namespace, String stringValue) throws IDCreateException {
        try {
            return new ThreadID((VBNamespace) namespace, new URI(stringValue));
        } catch (URISyntaxException e) {
            throw new IDCreateException(e);
        }
    }

    public ID createBBObjectId(Namespace namespace, URL baseURL, String longValue) throws IDCreateException {
        try {
            return new ThreadID((VBNamespace) namespace, baseURL, Long.parseLong(longValue));
        } catch (URISyntaxException e) {
            throw new IDCreateException(e);
        }
    }

    public IBBObject createBBObject(ID id, String name, Map<String, Object> parameters) {
        Thread t = new Thread((ThreadID) id, name);
        if (parameters != null) {
            if (parameters.containsKey("author")) {
                t.setAuthor((IMember) parameters.get("author"));
            }
        }
        return t;
    }
}
