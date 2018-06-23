/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteserviceadmin.ui.rsa.model;

/**
 * @since 3.4
 */
public class StackTraceElementNode extends NameValuePropertyNode {

    public  StackTraceElementNode(String stackTraceElement) {
        super("at", stackTraceElement);
        setNameValueSeparator(" ");
    }
}
