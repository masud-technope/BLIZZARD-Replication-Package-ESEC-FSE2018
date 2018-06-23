/******************************************************************************
 * Copyright (c) 2009 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Chi Jian Suen - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.datashare.nio;

final class ChannelData {

    private final byte[] data;

    private final boolean open;

     ChannelData(byte[] data, boolean open) {
        this.data = data;
        this.open = open;
    }

    public byte[] getData() {
        return data;
    }

    public boolean isOpen() {
        return open;
    }
}
