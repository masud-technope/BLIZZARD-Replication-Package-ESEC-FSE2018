/* Copyright (c) 2006-2009 Jan S. Rellermeyer
 * Systems Group,
 * Department of Computer Science, ETH Zurich.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of ETH Zurich nor the names of its contributors may be
 *      used to endorse or promote products derived from this software without
 *      specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package ch.ethz.iks.r_osgi.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * <p>
 * TimeOffsetMessages measures the time offset between two peers.
 * </p>
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 * @since 0.2
 */
public final class TimeOffsetMessage extends RemoteOSGiMessage {

    /**
	 * the time series. Both peers append their timestamps and the series is
	 * then evaluated to determine the offset
	 */
    private long[] timeSeries;

    /**
	 * creates a new empty TimeSyncMessage.
	 */
    public  TimeOffsetMessage() {
        super(TIME_OFFSET);
        timeSeries = new long[0];
    }

    /**
	 * creates a new TimeSyncMessage from network packet:
	 * 
	 * <pre>
	 *        0                   1                   2                   3
	 *        0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 *       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *       |       R-OSGi header (function = TimeOffset = 7)               |
	 *       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *       |                   Marshalled Long[]                           \
	 *       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * </pre>
	 * 
	 * .
	 * 
	 * @param input
	 *            an <code>ObjectInputStream</code> that provides the body of a
	 *            R-OSGi network packet.
	 * @throws IOException
	 *             in case of IO failures.
	 */
    public  TimeOffsetMessage(final ObjectInputStream input) throws IOException {
        super(TIME_OFFSET);
        final int size = input.readInt();
        timeSeries = new long[size];
        for (int i = 0; i < size; i++) {
            timeSeries[i] = input.readLong();
        }
    }

    /**
	 * write the body of the message to a stream.
	 * 
	 * @param out
	 *            the ObjectOutputStream.
	 * @throws IOException
	 *             in case of IO failures.
	 */
    public void writeBody(final ObjectOutputStream out) throws IOException {
        out.writeInt(timeSeries.length);
        for (int i = 0; i < timeSeries.length; i++) {
            out.writeLong(timeSeries[i]);
        }
    }

    /**
	 * add the current time to the time series.
	 */
    public void timestamp() {
        final int len = timeSeries.length;
        final long[] newSeries = new long[len + 1];
        System.arraycopy(timeSeries, 0, newSeries, 0, len);
        newSeries[len] = System.currentTimeMillis();
        timeSeries = newSeries;
    }

    /**
	 * for retransmissions: replace the last timestamp with the current one. The
	 * sending method must increase the XID to signal that this is a "new"
	 * message rather than a strict retransmission.
	 * @param newXID 
	 */
    public void restamp(final int newXID) {
        xid = newXID;
        timeSeries[timeSeries.length - 1] = System.currentTimeMillis();
    }

    /**
	 * returns the time series.
	 * 
	 * @return the time series as <code>long</code> array.
	 */
    public final long[] getTimeSeries() {
        return timeSeries;
    }

    /**
	 * set the time series.
	 * 
	 * @param series
	 *            the time series.
	 */
    public final void setTimeSeries(final long[] series) {
        timeSeries = series;
    }

    /**
	 * String representation for debug outputs.
	 * 
	 * @return a string representation.
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        //$NON-NLS-1$
        buffer.append("[TIME_OFFSET, ");
        //$NON-NLS-1$
        buffer.append("] - XID: ");
        buffer.append(xid);
        //$NON-NLS-1$
        buffer.append("timeSeries: [");
        for (int i = 0; i < timeSeries.length; i++) {
            buffer.append(timeSeries[i]);
            if (i < timeSeries.length - 1) {
                //$NON-NLS-1$
                buffer.append(//$NON-NLS-1$
                ", ");
            }
        }
        //$NON-NLS-1$
        buffer.append("]");
        for (int i = 0; i < timeSeries.length; i++) {
            buffer.append(timeSeries[i]);
            //$NON-NLS-1$
            buffer.append(", ");
        }
        return buffer.toString();
    }
}
