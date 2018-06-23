/*******************************************************************************
* Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice;

/**
 * Async method annotation.
 * 
 * @since 4.1
 */
public @interface AsyncMethod {

    /**
	 * value values are:  "none", "callback", "future", "both".  All other values
	 * will be ignored
	 * @return String type value
	 */
    String type();
}
