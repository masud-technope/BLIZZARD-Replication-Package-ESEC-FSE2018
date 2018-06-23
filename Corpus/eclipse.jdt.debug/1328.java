/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
public class Literals17 {

    // OK (decimal literal)
    int x1 = 1_0;

    // OK (hexadecimal literal)
    int x2 = 0x1_0;

    // OK (octal literal)
    int x3 = 0_10;

    // OK (binary literal)
    int x4 = 0b10_00;

    // OK (double literal)
    double x5 = 10.5_56D;

    // OK (float literal)
    float x6 = 3.14_15F;

    // OK (char literal)
    char x7 = 1_2_3;

    // OK (long literal)
    long x8 = 1_0L;

    // OK (short literal)
    short x9 = 1_0;

    // OK (byte literal)
    byte x10 = 0b1_0_0_0;

    public static void main(String[] args) {
        Literals17 literals = new Literals17();
        System.out.println(literals.x1);
    }
}
