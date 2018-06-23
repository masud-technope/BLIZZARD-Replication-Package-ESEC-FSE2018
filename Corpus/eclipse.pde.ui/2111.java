/*******************************************************************************
 * Copyright (c) 2008, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package a.b.c;

/**
 * Tests invalid @nooverride tags on nested inner enums
 * @nooverride
 */
public enum test5 implements  {

    A() {
    }
    ;

    /**
	 * @nooverride
	 */
    enum inner implements  {

        ;
    }

    enum inner1 implements  {

        A() {
        }
        ;

        /**
		 * @nooverride
		 */
        enum inner2 implements  {

            ;
        }
    }

    enum inner2 implements  {

        ;
    }
}

enum outer implements  {

    A() {
    }
    ;

    enum InnerNoRef4 implements  {

        ;
    }
}
