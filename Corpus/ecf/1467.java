/*******************************************************************************
* Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.examples.remoteservices.hello;

import java.util.concurrent.Future;
import org.eclipse.ecf.remoteservice.IAsyncCallback;

/**
 * @since 2.0
 */
public interface IHelloAsync {

    public void helloAsync(String from, IAsyncCallback<String> callback);

    /**
	 * @since 4.0
	 */
    public Future<String> helloAsync(String from);

    /**
	 * @since 3.0
	 */
    public void helloMessageAsync(HelloMessage message, IAsyncCallback<String> callback);

    /**
	 * @since 4.0
	 */
    public Future<String> helloMessageAsync(HelloMessage message);
}
