/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.docshare.messages;

/**
 * @since 2.1
 *
 */
public class SelectionMessage extends Message {

    private static final long serialVersionUID = 6451633617366707234L;

    int offset;

    int length;

    public  SelectionMessage(int offset, int length) {
        super();
        this.offset = offset;
        this.length = length;
    }

    /**
	 * @return the offset
	 */
    public int getOffset() {
        return offset;
    }

    /**
	 * @return the length
	 */
    public int getLength() {
        return length;
    }
}
