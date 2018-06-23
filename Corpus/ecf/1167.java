/****************************************************************************
 * Copyright (c) 2014 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.remoteservice.asyncproxy;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.eclipse.equinox.concurrent.future.IFuture;

public abstract class AbstractAsyncProxyRemoteService {

    @SuppressWarnings("rawtypes")
    protected abstract IFuture callAsync(AbstractAsyncProxyRemoteCall call);

    @SuppressWarnings("rawtypes")
    protected abstract Future callFutureAsync(AbstractAsyncProxyRemoteCall call);

    protected abstract void callCompletableAsync(AbstractAsyncProxyRemoteCall call, IAsyncProxyCompletable completable);

    @SuppressWarnings("unchecked")
    protected Object callFuture(AbstractAsyncProxyRemoteCall call, @SuppressWarnings("rawtypes") Class returnType) {
        // we callCompletableAsync
        if (CompletableFuture.class.isAssignableFrom(returnType)) {
            @SuppressWarnings("rawtypes") CompletableFuture result = new CompletableFuture();
            callCompletableAsync(call, ( r,  hadException,  exception) -> {
                if (hadException)
                    result.completeExceptionally(exception);
                else
                    result.complete(r);
            });
            // And return the CompletableFuture
            return result;
        }
        // IFuture result of callAsync
        if (IFuture.class.isAssignableFrom(returnType))
            return callAsync(call);
        // Else it must be a Future return value
        return callFutureAsync(call);
    }
}
