/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package com.mycorp.examples.timeservice;

import java.util.concurrent.CompletableFuture;

public interface ITimeServiceAsync {

    /**
	 * Get current time using Java 8 {@link CompletableFuture}.
	 * 
	 * @return CompletableFuture<Long> The future value time in milliseconds since Jan 1, 1970. Will not
	 *         return <code>null</code>.
	 */
    public CompletableFuture<Long> getCurrentTimeAsync();
}
