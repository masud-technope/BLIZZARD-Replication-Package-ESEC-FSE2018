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

import java.io.Serializable;

/**
 * Boxes a primitive value for remote method calls that have primitive type
 * arguments.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich.
 * @since 0.4
 */
public final class BoxedPrimitive implements Serializable {

    /**
	 * the serial UID.
	 */
    private static final long serialVersionUID = 5293209550505179288L;

    /**
	 * the boxed value.
	 */
    private Object boxed;

    /**
	 * hidden default constructor.
	 */
    private  BoxedPrimitive() {
    }

    /**
	 * create from object.
	 * 
	 * @param o
	 *            the object.
	 */
    public  BoxedPrimitive(final Object o) {
        this();
        boxed = o;
    }

    /**
	 * box an <code>int</code> value.
	 * 
	 * @param i
	 *            the int value.
	 */
    public  BoxedPrimitive(final int i) {
        boxed = new Integer(i);
    }

    /**
	 * box a <code>boolean</code> value.
	 * 
	 * @param b
	 *            the <code>boolean</code> value.
	 */
    public  BoxedPrimitive(final boolean b) {
        boxed = new Boolean(b);
    }

    /**
	 * box a <code>long</code> value.
	 * 
	 * @param l
	 *            the <code>long</code> value.
	 */
    public  BoxedPrimitive(final long l) {
        boxed = new Long(l);
    }

    /**
	 * box a <code>char</code> value.
	 * 
	 * @param c
	 *            the <code>char</code> value.
	 */
    public  BoxedPrimitive(final char c) {
        boxed = new Character(c);
    }

    /**
	 * box a <code>double</code> value.
	 * 
	 * @param d
	 *            the <code>double</code> value.
	 */
    public  BoxedPrimitive(final double d) {
        boxed = new Double(d);
    }

    /**
	 * box a <code>float</code> value.
	 * 
	 * @param f
	 *            the <code>float</code> value.
	 */
    public  BoxedPrimitive(final float f) {
        boxed = new Float(f);
    }

    /**
	 * box a <code>short</code> value.
	 * 
	 * @param s
	 *            the <code>short</code> value.
	 */
    public  BoxedPrimitive(final short s) {
        boxed = new Short(s);
    }

    /**
	 * box a <code>byte</code> value.
	 * 
	 * @param b
	 *            the <code>short</code> value.
	 */
    public  BoxedPrimitive(final byte b) {
        boxed = new Byte(b);
    }

    /**
	 * get the boxed value.
	 * 
	 * @return the boxing object.
	 */
    public Object getBoxed() {
        return boxed;
    }

    public String toString() {
        //$NON-NLS-1$ //$NON-NLS-2$
        return "BoxedPrimitive{" + boxed.toString() + "}";
    }

    /**
	 * check for equality.
	 * 
	 * @param o
	 *            the other object.
	 * @return true, iff equal.
	 */
    public boolean equals(final Object o) {
        return boxed.equals(o);
    }

    /**
	 * get the hash code.
	 * 
	 * @return the hash code.
	 */
    public int hashCode() {
        return boxed.hashCode();
    }
}
