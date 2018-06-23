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
package ch.ethz.iks.r_osgi.impl;

import ch.ethz.iks.r_osgi.types.Timestamp;
import ch.ethz.iks.util.MathUtils;

/**
 * <p>
 * Encapsulates an offset that a remote peer's clock has.
 * </p>
 * <p>
 * Used to transform <code>Timestamp</code> objects received from remote peers
 * to local time.
 * </p>
 * 
 * @author Jan S. Rellermeyer, ETH Zürich.
 * @since 0.2
 */
final class TimeOffset {

    /**
	 * the offset.
	 */
    private long offset;

    /**
	 * the last update time.
	 */
    private long lastUpdate;

    /**
	 * the lifetime of this offset.
	 */
    private int lifetime;

    /**
	 * the length of the next series.
	 */
    private int seriesLength;

    /**
	 * the initial lifetime in minutes.
	 */
    private static final int INITIAL_LIFETIME = 1;

    /**
	 * the maximum lifetime.
	 */
    private static final int MAX_LIFETIME = 10;

    /**
	 * the minimum lifetime.
	 */
    private static final int MIN_LIFETIME = 1;

    /**
	 * the default series length.
	 */
    private static final int DEFAULT_SERIES = 8;

    /**
	 * the maximum series length.
	 */
    private static final int MAX_SERIES = 32;

    /**
	 * the minimum series length.
	 */
    private static final int MIN_SERIES = 4;

    /**
	 * creates a new TimeOffset object.
	 * 
	 * @param timeSerie
	 *            a series of time measurements.
	 */
     TimeOffset(final long[] timeSerie) {
        // initially redo after half a minute
        lifetime = INITIAL_LIFETIME;
        seriesLength = DEFAULT_SERIES;
        update(timeSerie);
    }

    /**
	 * update the offset with a new series.
	 * 
	 * @param timeSeries
	 *            the series.
	 */
    void update(final long[] timeSeries) {
        final int len = timeSeries.length / 2;
        final long[] offsets = new long[len];
        long l = 0;
        long h = 0;
        for (int i = 0; i < len; i++) {
            final long local = timeSeries[2 * i];
            final long remote = timeSeries[2 * i + 1];
            offsets[i] = local - remote;
            l = l + MathUtils.lower32(offsets[i]);
            h = h + MathUtils.higher32(offsets[i]);
        }
        final int shift = MathUtils.log2(len);
        l = l >>> shift;
        h = h << (32 - shift);
        final long mean = (h | l);
        if (lastUpdate != 0) {
            // heuristic adaptation of seriesLength
            final long stddev = (MathUtils.max(offsets) - MathUtils.min(offsets)) / 3;
            /*
			 * System.out.println("STDDEV " + stddev);
			 */
            if (stddev < 10) {
                seriesLength /= 2;
            }
            if (stddev > 40) {
                seriesLength *= 2;
            }
            if (stddev > 100) {
                seriesLength *= 2;
            }
            // heuristic adaptation of lifetime
            final long diff = MathUtils.abs(offset - mean);
            /*
			 * System.out.println("DIFF " + diff);
			 */
            if (diff < (50 - 0.5 * (lifetime * lifetime))) {
                lifetime++;
            }
            if (diff > 100) {
                lifetime--;
            }
            if (diff > 500) {
                lifetime -= 4;
            }
            if (seriesLength > MAX_SERIES) {
                seriesLength = MAX_SERIES;
            }
            if (seriesLength < MIN_SERIES) {
                seriesLength = MIN_SERIES;
            }
            if (lifetime > MAX_LIFETIME) {
                lifetime = MAX_LIFETIME;
            }
            if (lifetime < MIN_LIFETIME) {
                lifetime = MIN_LIFETIME;
            }
        }
        lastUpdate = System.currentTimeMillis();
        offset = mean;
    }

    /**
	 * has this time offset expired ?
	 * 
	 * @return <code>true</code> if expired, <code>false</code> otherwise.
	 */
    boolean isExpired() {
        return (System.currentTimeMillis() - lastUpdate > lifetime * 60000L);
    }

    /**
	 * the length of the series. Note that this value is <i>i</i> = <i>2 * n</i>
	 * or in other words two times the number of rounds.
	 * 
	 * @return the length of the series.
	 */
    int seriesLength() {
        return seriesLength;
    }

    /**
	 * transform a remote timestamp.
	 * 
	 * @param remoteTimestamp
	 *            the remote timestamp.
	 * @return the timestamp transformed into local time.
	 */
    Long transform(final Long remoteTimestamp) {
        return new Long(remoteTimestamp.longValue() + offset);
    }

    /**
	 * transform a timestamp.
	 * 
	 * @param timestamp
	 *            the timestamp.
	 * @return the transformed timestamp.
	 */
    Timestamp transform(final Timestamp timestamp) {
        return Timestamp.from((timestamp.getValue() + 1000 * offset));
    }
}
