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
package ch.ethz.iks.util;

/**
 * Math utilities.
 * 
 * @author Jan S. Rellermeyer, ETH Zürich
 * @since 0.2
 */
public final class MathUtils {

    /**
	 * hidden default constructor.
	 */
    private  MathUtils() {
    }

    /**
	 * get the logarithm with base 2.
	 * 
	 * @param num
	 *            the value.
	 * @return the logarithm.
	 * @since 0.2
	 */
    public static int log2(final int num) {
        int i = num;
        int j = -1;
        while (i > 0) {
            i = i >> 1;
            j++;
        }
        return j;
    }

    /**
	 * get the lower 32 bits of a long value.
	 * 
	 * @param val
	 *            the long value.
	 * @return the lower 32 bits.
	 * @since 0.2
	 */
    public static long lower32(final long val) {
        return val & 0xFFFFFFFFL;
    }

    /**
	 * get the higher 32 bits of a long value.
	 * 
	 * @param val
	 *            the long value.
	 * @return the higher 32 bits.
	 * @since 0.2
	 */
    public static long higher32(final long val) {
        return val >>> 32;
    }

    /**
	 * get the absolute value of a long.
	 * 
	 * @param val
	 *            the (signed) long value.
	 * @return the absolute value.
	 * @since 0.2
	 */
    public static long abs(final long val) {
        if (val < 0) {
            return -1 * val;
        }
        return val;
    }

    /**
	 * get the maximum within an array of long values.
	 * 
	 * @param values
	 *            the array of long values.
	 * @return the maximum.
	 * @since 0.2
	 */
    public static long max(final long[] values) {
        long max = values[0];
        for (int i = 1; i < values.length; i++) {
            if (max < values[i]) {
                max = values[i];
            }
        }
        return max;
    }

    /**
	 * get the minimum within an array of long values.
	 * 
	 * @param values
	 *            the array of long values.
	 * @return the minimum.
	 * @since 0.2
	 */
    public static long min(final long[] values) {
        long min = values[0];
        for (int i = 1; i < values.length; i++) {
            if (min > values[i]) {
                min = values[i];
            }
        }
        return min;
    }
}
