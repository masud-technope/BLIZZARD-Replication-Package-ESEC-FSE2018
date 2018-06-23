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
package ch.ethz.iks.r_osgi;

/**
 * RemoteOSGiException is thrown when an internal error occurs.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 * @since 0.1
 */
public final class RemoteOSGiException extends RuntimeException {

    /**
	 * the nested throwable.
	 */
    private transient Throwable nested;

    /**
	 * the serial UID.
	 */
    private static final long serialVersionUID = 8370566955212317525L;

    /**
	 * creates a new RemoteOSGiException from error message.
	 * 
	 * @param message
	 *            the error message.
	 */
    public  RemoteOSGiException(final String message) {
        super(message);
    }

    /**
	 * creates a new RemoteOSGiException that nests a <code>Throwable</code>.
	 * 
	 * @param nested
	 *            the nested <code>Exception</code>.
	 */
    public  RemoteOSGiException(final Throwable nested) {
        super(nested.getMessage());
        this.nested = nested;
    }

    /**
	 * creates a new RemoteOSGiException with error message and nested
	 * <code>Throwable</code>.
	 * 
	 * @param message
	 *            the error message.
	 * @param nested
	 *            the nested <code>Exception</code>.
	 */
    public  RemoteOSGiException(final String message, final Throwable nested) {
        super(message);
        this.nested = nested;
    }

    /**
	 * get the nested exception.
	 * 
	 * @return the nested exception.
	 */
    public Throwable getCause() {
        return nested;
    }
}
