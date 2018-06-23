/****************************************************************************
 * Copyright (c) 2010 Naumen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Pavel Samolisov - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.remoteservice;

/**
 * Factory to support the creation of {@link IRemoteCall} objects.
 * @since 5.0
 */
public class RemoteCallFactory {

    public static IRemoteCall createRemoteCall(String fqMethod, Object[] params, long timeout) {
        return new RemoteCall(fqMethod, params, timeout);
    }

    public static IRemoteCall createRemoteCall(String fqMethod, Object[] params) {
        return createRemoteCall(fqMethod, params, IRemoteCall.DEFAULT_TIMEOUT);
    }

    public static IRemoteCall createRemoteCall(String fqMethod) {
        return createRemoteCall(fqMethod, null);
    }
}
