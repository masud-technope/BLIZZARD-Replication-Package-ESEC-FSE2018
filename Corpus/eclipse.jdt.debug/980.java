/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package a.b.c;

public class GenericMethodEntryTest {

    public static <// method breakpoint on func(T[],int,int)
    T extends Comparable<T>> // method breakpoint on func(T[],int,int)
    int func(// method breakpoint on func(T[],int,int)
    T[] arr, // method breakpoint on func(T[],int,int)
    int m, // method breakpoint on func(T[],int,int)
    int n) {
        int i = 0;
        ++i;
        return i;
    }

    public static <// method breakpoint on func(int,int)
    T extends Comparable<T>> // method breakpoint on func(int,int)
    int func(// method breakpoint on func(int,int)
    int m, // method breakpoint on func(int,int)
    int n) {
        int i = 0;
        ++i;
        return i;
    }

    public static <// method breakpoint on func(int,int)
    T extends Comparable<T>> // method breakpoint on func(int,int)
    int func(// method breakpoint on func(int,int)
    T t, // method breakpoint on func(int,int)
    int m, // method breakpoint on func(int,int)
    int n) {
        int i = 0;
        ++i;
        return i;
    }

    public static void main(String[] args) {
        String[] ss = new String[] { "a", "b" };
        // should hit in func(T[],int,int)
        func(ss, 1, 2);
        // hits in func(int,int)
        func(1, 2);
        // hits in func(T,int,int)
        func("s", 1, 2);
    }
}
