/****************************************************************************
 * Copyright (c) 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.examples.remoteservices.hello;

public interface IHello {

    /**
	 * @since 2.0
	 */
    public String hello(String from);

    /**
	 * @since 3.0
	 */
    public String helloMessage(HelloMessage message);
}
