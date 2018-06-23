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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * String utilities.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 * @since 0.2
 */
public final class StringUtils {

    /**
	 * hide the default constructor.
	 */
    private  StringUtils() {
    }

    /**
	 * transforms a string list into an array of Strings.
	 * 
	 * @param data
	 *            the string list.
	 * @param delim
	 *            the list delimiter.
	 * @return the array of strings.
	 * @since 0.2
	 */
    public static String[] stringToArray(final String data, final String delim) {
        if (data == null) {
            return new String[0];
        }
        final List tokens = new ArrayList(data.length() / 10);
        int pointer = 0;
        int quotePointer = 0;
        int tokenStart = 0;
        int nextDelimiter;
        while ((nextDelimiter = data.indexOf(delim, pointer)) > -1) {
            final int openingQuote = data.indexOf("\"", quotePointer);
            int closingQuote = data.indexOf("\"", openingQuote + 1);
            if (openingQuote > closingQuote) {
                throw new IllegalArgumentException("Missing closing quotation mark.");
            }
            if (openingQuote > -1 && openingQuote < nextDelimiter && closingQuote < nextDelimiter) {
                quotePointer = ++closingQuote;
                continue;
            }
            if (openingQuote < nextDelimiter && nextDelimiter < closingQuote) {
                pointer = ++closingQuote;
                continue;
            }
            // TODO: for performance, fold the trim into the splitting
            tokens.add(data.substring(tokenStart, nextDelimiter).trim());
            pointer = ++nextDelimiter;
            quotePointer = pointer;
            tokenStart = pointer;
        }
        tokens.add(data.substring(tokenStart).trim());
        return (String[]) tokens.toArray(new String[tokens.size()]);
    }

    /**
	 * R \ L (comparison operation allows wildcards)
	 * 
	 * @param left
	 *            A set of matchers (supports wildcard at end)
	 * @param right
	 *            A set of inputs
	 * @return The subset of right with all elements removed matching left
	 * @since 1.0
	 */
    public static Collection rightDifference(Collection left, Collection right) {
        // This is O(n²) due to substring (wildcard) matching
        // It's also quick and dirty (better use pattern matcher instead)
        // TODO use pattern matcher
        // (pattern matcher would increase the BREE dependency, but we could
        // hide
        // the FilterUtils implementation behind an interface and provide
        // different
        // service implementations)
        // A trie would also allow for faster lookup.
        // Have to convert c1 into List to support remove operation
        final List result = new ArrayList(right);
        for (Iterator iterator = right.iterator(); iterator.hasNext(); ) {
            final String f1 = (String) iterator.next();
            for (Iterator itr2 = left.iterator(); itr2.hasNext(); ) {
                String f2 = (String) itr2.next();
                if (f2.endsWith("*")) {
                    f2 = f2.substring(0, f2.length() - 1);
                    if (f1.startsWith(f2)) {
                        result.remove(f1);
                    }
                } else {
                    if (f1.equals(f2)) {
                        result.remove(f1);
                    }
                }
            }
        }
        return result;
    }
}
