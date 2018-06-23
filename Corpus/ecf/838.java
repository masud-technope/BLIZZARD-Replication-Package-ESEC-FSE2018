/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis (slewis@composent.com) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.examples.raspberrypi.management;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface IRaspberryPiAsync {

    /**
	 * Get remote system properties via CompletableFuture for non-blocking.
	 * Note:  signature of this method is connected to {@link IRaspberryPi#getSystemProperties()}.
	 * 
	 * @return CompletableFuture
	 */
    public CompletableFuture<Map<String, String>> getSystemPropertiesAsync();
}
