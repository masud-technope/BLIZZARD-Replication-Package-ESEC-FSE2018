/*******************************************************************************
 * Copyright (c) 2013 GK Software AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stephan Herrmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.lookup;

/**
 * Generalization over TypeBounds and ConstraintFormulas which both
 * can be created during reduction.
 */
public abstract class ReductionResult {

    protected static final ConstraintTypeFormula TRUE = new ConstraintTypeFormula() {

        /* empty body just to make abstract class instantiable */
        public Object reduce(InferenceContext18 context) {
            return this;
        }

        //$NON-NLS-1$
        public String toString() {
            return "TRUE";
        }
    };

    protected static final ConstraintTypeFormula FALSE = new ConstraintTypeFormula() {

        /* empty body just to make abstract class instantiable */
        public Object reduce(InferenceContext18 context) {
            return this;
        }

        //$NON-NLS-1$
        public String toString() {
            return "FALSE";
        }
    };

    // Relation kinds, mimic an enum:
    protected static final int COMPATIBLE = 1;

    protected static final int SUBTYPE = 2;

    protected static final int SUPERTYPE = 3;

    protected static final int SAME = 4;

    protected static final int TYPE_ARGUMENT_CONTAINED = 5;

    protected static final int CAPTURE = 6;

    static final int EXCEPTIONS_CONTAINED = 7;

    protected static final int POTENTIALLY_COMPATIBLE = 8;

    // note that the LHS differs between sub-classes.
    protected TypeBinding right;

    protected int relation;

    protected static String relationToString(int relation) {
        switch(relation) {
            case //$NON-NLS-1$
            SAME:
                //$NON-NLS-1$
                return " = ";
            case COMPATIBLE:
                return //$NON-NLS-1$
                " ? ";
            //$NON-NLS-1$
            case POTENTIALLY_COMPATIBLE:
                return " ?? ";
            //$NON-NLS-1$
            case SUBTYPE:
                return " <: ";
            case //$NON-NLS-1$
            SUPERTYPE:
                //$NON-NLS-1$
                return " :> ";
            case TYPE_ARGUMENT_CONTAINED:
                //$NON-NLS-1$
                return " <= ";
            case CAPTURE:
                //$NON-NLS-1$
                return " captureOf ";
            default:
                throw new //$NON-NLS-1$
                IllegalArgumentException(//$NON-NLS-1$
                "Unknown type relation " + relation);
        }
    }
}
