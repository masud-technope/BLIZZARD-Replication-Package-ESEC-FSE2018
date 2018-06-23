/****************************************************************************
 * Copyright (c) 2008, 2009 Composent, Inc. and others.
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
public class SelectionMessage extends Message {

    private static final long serialVersionUID = 6451633617366707234L;

    private String path;

    private int offset;

    private int length;

    public  SelectionMessage(String path, int offset, int length) {
        this.path = path;
        this.offset = offset;
        this.length = length;
    }

    public String getPath() {
        return path;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }
}
