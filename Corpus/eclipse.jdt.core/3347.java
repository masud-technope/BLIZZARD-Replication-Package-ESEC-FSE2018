/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.impl;

public class CharConstant extends Constant {

    private char value;

    public static Constant fromValue(char value) {
        return new CharConstant(value);
    }

    private  CharConstant(char value) {
        this.value = value;
    }

    public byte byteValue() {
        return (byte) this.value;
    }

    public char charValue() {
        return this.value;
    }

    public double doubleValue() {
        // implicit cast to return type
        return this.value;
    }

    public float floatValue() {
        // implicit cast to return type
        return this.value;
    }

    public int intValue() {
        // implicit cast to return type
        return this.value;
    }

    public long longValue() {
        // implicit cast to return type
        return this.value;
    }

    public short shortValue() {
        return (short) this.value;
    }

    public String stringValue() {
        // spec 15.17.11
        return String.valueOf(this.value);
    }

    public String toString() {
        //$NON-NLS-1$
        return "(char)" + this.value;
    }

    public int typeID() {
        return T_char;
    }

    public int hashCode() {
        return this.value;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CharConstant other = (CharConstant) obj;
        return this.value == other.value;
    }
}
