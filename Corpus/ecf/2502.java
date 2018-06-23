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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import org.eclipse.ecf.core.identity.ID;

final class Util {

    //$NON-NLS-1$
    static final String PLUGIN_ID = "org.eclipse.ecf.provider.datashare.nio";

    static void closeChannel(Channel channel) {
        try {
            channel.close();
        } catch (IOException e) {
        }
    }

    static byte[] serialize(ID id) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(id);
        oos.flush();
        return baos.toByteArray();
    }

    /**
	 * Reads messages from the channel and returns the data, if any.
	 * 
	 * @param channel
	 *            the channel to read the messages from
	 * @param buffer
	 *            the buffer to use for reading the data into
	 * @return the data from the channel and information about the channel, will
	 *         not be <code>null</code>
	 * @throws IOException
	 */
    static ChannelData read(ReadableByteChannel channel, ByteBuffer buffer) throws IOException {
        // check how much data has been read
        int read = channel.read(buffer);
        if (read == -1) {
            return new ChannelData(null, false);
        } else if (read == 0) {
            return new ChannelData(null, true);
        }
        // the read data
        byte[] message = new byte[read];
        // retrieve the contents from the buffer
        buffer.flip();
        buffer.get(message, 0, read);
        buffer.clear();
        // try reading more data
        read = channel.read(buffer);
        while (read > 0) {
            // create a temporary array, copy what's been read so far
            byte[] temp = new byte[read + message.length];
            System.arraycopy(message, 0, temp, 0, message.length);
            // retrieve the contents from the buffer
            buffer.flip();
            buffer.get(temp, message.length, read);
            buffer.clear();
            // assign the temporary array back to the primary
            message = temp;
            // try reading more data
            read = channel.read(buffer);
        }
        // if we didn't read zero bytes that'd imply we've reached end-of-stream
        return new ChannelData(message, read == 0);
    }

    static void write(WritableByteChannel channel, ByteBuffer buffer, byte[] data) throws IOException {
        buffer.clear();
        int remaining = data.length;
        int limit = buffer.limit();
        if (remaining < limit) {
            buffer.put(data);
            buffer.flip();
            channel.write(buffer);
            buffer.clear();
            return;
        }
        int offset = 0;
        while (remaining > limit) {
            buffer.put(data, offset, limit);
            buffer.flip();
            channel.write(buffer);
            buffer.clear();
            offset += limit;
            remaining -= limit;
        }
        buffer.put(data, offset, remaining);
        buffer.flip();
        channel.write(buffer);
        buffer.clear();
    }
}
