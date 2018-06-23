/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package a.b.c;

import org.eclipse.pde.api.tools.annotations.NoImplement;

/**
 * Tests invalid @NoImplement annotations on nested inner enums
 */
@NoImplement
public enum test9 implements  {

    A() {
    }
    ;

    /**
	 */
    @NoImplement
    enum inner implements  {

        ;
    }

    enum inner1 implements  {

        A() {
        }
        ;

        /**
		 */
        @NoImplement
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

    @NoImplement
    enum inner implements  {

        ;
    }
}
