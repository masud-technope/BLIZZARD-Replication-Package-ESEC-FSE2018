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
package ch.ethz.iks.r_osgi.types;

import java.util.Random;

/**
 * <p>
 * Encapsulates a timestamp that can be used as attribute for a service or as
 * part of a RemoteEvent. Timestamps consist of a physical clock timestamp and a
 * logical time to avoid ambiguity. (Especially, if more than one event is
 * generated within a physical tick, which is typically 50 ms on desktop
 * computers but may be larger on small devices)
 * </p>
 * <p>
 * Timestamps are automatically transformed into local time whenever a
 * RemoteEvent is received by a peer or a proxy bundle with a Timestamp
 * attribute is generated.
 * </p>
 * 
 * @author Jan S. Rellermeyer, ETH Zürich.
 * @since 0.3
 */
public final class Timestamp implements Comparable {

    /**
	 * the offset of the logical clock. Initialized by a pseudo- random number
	 * to simplify causal ordering among different peers.
	 */
    private static int counter = new Random().nextInt(1000);

    /**
	 * the actual timestamp, stored as <code>long</code>.
	 */
    private final long timestamp;

    /**
	 * creates an new Timestamp object with the current time.
	 */
    public  Timestamp() {
        counter++;
        String ts = String.valueOf(System.currentTimeMillis());
        String counterString = String.valueOf(counter);
        for (int i = 0; i < 3 - counterString.length(); i++) {
            //$NON-NLS-1$
            counterString = "0" + counterString;
        }
        ts = ts + counterString;
        timestamp = new Long(ts).longValue();
    }

    /**
	 * hidden constructor.
	 * 
	 * @param ts
	 *            a <code>String</code> representation of an existing timestamp.
	 */
    private  Timestamp(final String ts) {
        timestamp = new Long(ts).longValue();
    }

    /**
	 * hidden constructor.
	 * 
	 * @param ts
	 *            a <code>long</code> representation of an existing timestamp.
	 */
    private  Timestamp(final long ts) {
        timestamp = ts;
    }

    /**
	 * compares this instance with an <code>Object</code>.
	 * 
	 * @param o
	 *            the <code>Object</code> to be compared.
	 * @return an int describing the relation.
	 * @see java.lang.Comparable#compareTo(Object)
	 */
    public int compareTo(final Object o) {
        if (o instanceof Timestamp) {
            final long val = ((Timestamp) o).getValue();
            if (timestamp < val) {
                return -1;
            }
            if (timestamp == val) {
                return 0;
            }
            if (timestamp > val) {
                return 1;
            }
        }
        return 1;
    }

    /**
	 * get a <code>String</code> representation of the instance.
	 * 
	 * @return the <code>String</code> representation.
	 */
    public String toString() {
        return String.valueOf(timestamp);
    }

    /**
	 * Get a Timestamp object from <code>String</code>.
	 * 
	 * @param timestamp
	 *            the <code>String</code> representation of a timestamp.
	 * @return a Timestamp instance.
	 */
    public static Timestamp fromString(final String timestamp) {
        return new Timestamp(timestamp);
    }

    /**
	 * Get a Timestamp object from <code>long</code>.
	 * 
	 * @param timestamp
	 *            the <code>long</code> representing a timestamp.
	 * @return a Timestamp instance.
	 */
    public static Timestamp from(final long timestamp) {
        return new Timestamp(timestamp);
    }

    /**
	 * Get the <code>long</code> value of the Timestamp instance.
	 * 
	 * @return the <code>long</code> value.
	 */
    public long getValue() {
        return timestamp;
    }

    /**
	 * Checks, if this instance equals another Timestamp.
	 * 
	 * @param o
	 *            an <code>Object</code>. If not instance of
	 *            <code>Timestamp</code>, false is returned.>
	 * @return true in case of equality.
	 */
    public boolean equals(final Object o) {
        if (o instanceof Timestamp) {
            return ((Timestamp) o).getValue() == timestamp;
        }
        return false;
    }

    /**
	 * Get the hash code of the timestamp.
	 * 
	 * @return the int value that represents the hash code.
	 */
    public int hashCode() {
        return new Long(timestamp).hashCode();
    }
}
