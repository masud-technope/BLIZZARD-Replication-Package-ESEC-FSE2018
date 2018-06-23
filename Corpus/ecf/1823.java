/****************************************************************************
 * Copyright (c) 2007, 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.docshare2.messages;

/**
 * @since 2.1
 *
 */
public class StopMessage extends Message {

    private static final long serialVersionUID = -1687663593624638268L;

    private String path;

    public  StopMessage(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
