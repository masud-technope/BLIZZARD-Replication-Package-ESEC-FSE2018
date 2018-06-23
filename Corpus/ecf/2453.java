/*******************************************************************************
* Copyright (c) 2016 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice.util;

import java.util.Iterator;
import java.util.List;
import org.eclipse.ecf.core.identity.*;

/**
 * @since 8.9
 */
public class IDUtil {

    public static IIDFactory getIDFactory() {
        return IDFactory.getDefault();
    }

    public static Namespace getNamespaceByName(String namespaceName) {
        if (namespaceName == null)
            return null;
        return getIDFactory().getNamespaceByName(namespaceName);
    }

    public static Namespace findNamespaceByIdName(String idName) {
        if (idName == null)
            return null;
        int colonIndex = idName.indexOf(Namespace.SCHEME_SEPARATOR);
        if (colonIndex <= 0)
            return null;
        String scheme = idName.substring(0, colonIndex);
        // First try to find the Namespace using the protocol directly
        Namespace ns = getNamespaceByName(scheme);
        return (ns == null) ? findNamespaceByScheme(scheme) : ns;
    }

    public static Namespace findNamespaceByScheme(String scheme) {
        if (scheme == null)
            return null;
        if (//$NON-NLS-1$
        scheme.equals("ecftcp"))
            return getIDFactory().getNamespaceByName(StringID.class.getName());
        List namespaces = getIDFactory().getNamespaces();
        for (Iterator i = namespaces.iterator(); i.hasNext(); ) {
            Namespace ns = (Namespace) i.next();
            if (scheme.equals(ns.getScheme()))
                return ns;
        }
        return null;
    }

    public static ID createID(String namespaceName, String idName) throws IDCreateException {
        Namespace ns = (namespaceName != null) ? getNamespaceByName(namespaceName) : findNamespaceByIdName(idName);
        if (ns == null)
            throw new IDCreateException(//$NON-NLS-1$
            "Cannot find Namespace for namespaceName=" + namespaceName + //$NON-NLS-1$
            " and idName=" + //$NON-NLS-1$
            idName);
        return createID(ns, idName);
    }

    public static ID createID(Namespace namespace, String idName) throws IDCreateException {
        return getIDFactory().createID(namespace, idName);
    }

    public static ID createID(Namespace namespace, Object[] args) throws IDCreateException {
        return getIDFactory().createID(namespace, args);
    }
}
