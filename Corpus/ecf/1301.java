/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.osgi.services.remoteserviceadmin;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IIDFactory;
import org.eclipse.ecf.core.identity.Namespace;

public class IDUtil {

    public static IIDFactory getIDFactory() {
        return org.eclipse.ecf.remoteservice.util.IDUtil.getIDFactory();
    }

    public static Namespace getNamespaceByName(String namespaceName) {
        return org.eclipse.ecf.remoteservice.util.IDUtil.getNamespaceByName(namespaceName);
    }

    public static Namespace findNamespaceByIdName(String idName) {
        return org.eclipse.ecf.remoteservice.util.IDUtil.findNamespaceByIdName(idName);
    }

    public static Namespace findNamespaceByScheme(String scheme) {
        return org.eclipse.ecf.remoteservice.util.IDUtil.findNamespaceByScheme(scheme);
    }

    public static ID createID(String namespaceName, String idName) throws IDCreateException {
        return org.eclipse.ecf.remoteservice.util.IDUtil.createID(namespaceName, idName);
    }

    public static ID createID(Namespace namespace, String idName) throws IDCreateException {
        return org.eclipse.ecf.remoteservice.util.IDUtil.createID(namespace, idName);
    }

    public static ID createID(Namespace namespace, Object[] args) throws IDCreateException {
        return org.eclipse.ecf.remoteservice.util.IDUtil.createID(namespace, args);
    }
}
