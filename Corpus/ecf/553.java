/*******************************************************************************
* Copyright (c) 2009 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.examples.internal.remoteservices.hello.ds.consumer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ecf.examples.remoteservices.hello.IHello;
import org.eclipse.ecf.examples.remoteservices.hello.IHelloAsync;
import org.eclipse.ecf.remoteservice.IAsyncCallback;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceProxy;
import org.eclipse.ecf.remoteservice.RemoteServiceHelper;
import org.eclipse.equinox.concurrent.future.IFuture;

public class HelloClientComponent {

    private static final String CONSUMER_NAME = "helloclientcomponent";

    public void bindHello(IHello proxy) {
        // First print out on console that we got a proxy instance
        System.out.println("Got proxy IHello=" + proxy);
        // Call proxy synchronously.  Note that this call may block or fail due to 
        // synchronous communication with remote service
        System.out.println("STARTING remote call via proxy...");
        proxy.hello(CONSUMER_NAME + " via proxy");
        System.out.println("COMPLETED remote call via proxy");
        System.out.println();
        // this asynchronous interface to invoke methods asynchronously
        if (proxy instanceof IHelloAsync) {
            IHelloAsync helloA = (IHelloAsync) proxy;
            // Create callback for use in IHelloAsync
            IAsyncCallback callback = new IAsyncCallback<String>() {

                public void onSuccess(String result) {
                    System.out.println("COMPLETED remote call with callback SUCCESS with result=" + result);
                    System.out.println();
                }

                public void onFailure(Throwable t) {
                    System.out.println("COMPLETED remote call with callback FAILED with exception=" + t);
                    System.out.println();
                }
            };
            // Call asynchronously with callback
            System.out.println("STARTING async remote call via callback...");
            helloA.helloAsync(CONSUMER_NAME + " via async proxy with listener", callback);
            System.out.println("LOCAL async invocation complete");
            System.out.println();
            // Call asynchronously with future
            System.out.println("STARTING async remote call via future...");
            Future<String> future = helloA.helloAsync(CONSUMER_NAME + " via async proxy with future");
            System.out.println("LOCAL async future invocation complete");
            System.out.println();
            try {
                while (!future.isDone()) {
                    // do some other stuff
                    System.out.println("LOCAL future not yet done...so we're doing other stuff while waiting for future to be done");
                    Thread.sleep(200);
                }
                // Now it's done, so this will not block
                String result = future.get();
                System.out.println("COMPLETED remote call with future SUCCEEDED with result=" + result);
                System.out.println();
            } catch (OperationCanceledException e) {
                System.out.println("COMPLETED remote call with callback CANCELLED with exception=" + e);
                System.out.println();
                e.printStackTrace();
            } catch (InterruptedException e) {
                System.out.println("COMPLETED remote call with callback INTERRUPTED with exception=" + e);
                System.out.println();
                e.printStackTrace();
            } catch (ExecutionException e) {
                System.out.println("COMPLETED remote call with callback INTERRUPTED with exception=" + e);
                System.out.println();
                e.printStackTrace();
            }
        }
        // It's also possible to get the remote service directly from the proxy
        IRemoteService remoteService = ((IRemoteServiceProxy) proxy).getRemoteService();
        Assert.isNotNull(remoteService);
        // In this case, we will make an non-blocking call and immediately get a 'future'...which is
        // a placeholder for a result of the remote computation.  This will not block.
        System.out.println("STARTING async remote call via future...");
        IFuture future = RemoteServiceHelper.futureExec(remoteService, "hello", new Object[] { CONSUMER_NAME + " future" });
        System.out.println("LOCAL async future invocation complete");
        System.out.println();
        // Client can execute arbitrary code here...
        try {
            // This blocks until communication and computation have completed successfully
            while (!future.isDone()) {
                // do some other stuff
                System.out.println("LOCAL future not yet done...so we're doing other stuff while waiting for future to be done");
                Thread.sleep(200);
            }
            // Now it's done, so this will not block
            Object result = future.get();
            System.out.println("COMPLETED remote call with future SUCCEEDED with result=" + result);
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
