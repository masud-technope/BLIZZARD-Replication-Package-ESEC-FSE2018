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
 * Test supported @noreference tag on static final fields in inner / outer enums
 */
public enum test3 implements  {

    A() {
    }
    ;

    /**
	 * @noreference
	 */
    public static final Object f1 = null;

    /**
	 * @noreference
	 */
    protected static final int f2 = 0;

    /**
	 * @noreference
	 */
    private static final char[] f3 = {};

    static enum inner implements  {

        A() {
        }
        ;

        /**
		 * @noreference
		 */
        public static final Object f1 = null;

        /**
		 * @noreference
		 */
        protected static final int f2 = 0;

        /**
		 * @noreference
		 */
        private static final char[] f3 = {};

        static enum inner2 implements  {

            A() {
            }
            ;

            /**
			 * @noreference
			 */
            public static final Object f1 = null;

            /**
			 * @noreference
			 */
            protected static final int f2 = 0;

            /**
			 * @noreference
			 */
            private static final char[] f3 = {};
        }
    }
}

enum outer implements  {

    A() {
    }
    ;

    public static final Object f1 = null;

    protected static final int f2 = 0;

    private static final char[] f3 = {};
}
