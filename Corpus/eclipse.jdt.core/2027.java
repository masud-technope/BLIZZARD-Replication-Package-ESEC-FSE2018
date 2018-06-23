/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.util.FloatUtil;

public class FloatLiteral extends NumberLiteral {

    float value;

    // work-around VAJ problem 1F6IGUU
    static final float Float_MIN_VALUE = Float.intBitsToFloat(1);

    public  FloatLiteral(char[] token, int s, int e) {
        super(token, s, e);
    }

    public void computeConstant() {
        Float computedValue;
        try {
            computedValue = Float.valueOf(String.valueOf(source));
        } catch (NumberFormatException e) {
            try {
                float v = FloatUtil.valueOfHexFloatLiteral(source);
                if (v == Float.POSITIVE_INFINITY) {
                    return;
                }
                if (Float.isNaN(v)) {
                    return;
                }
                value = v;
                constant = Constant.fromValue(v);
            } catch (NumberFormatException e1) {
            }
            return;
        }
        final float floatValue = computedValue.floatValue();
        if (floatValue > Float.MAX_VALUE) {
            // error: the number is too large to represent
            return;
        }
        if (floatValue < Float.MIN_VALUE) {
            // see 1F6IGUU
            // a true 0 only has '0' and '.' in mantissa
            // 1.0e-5000d is non-zero, but underflows to 0
            boolean isHexaDecimal = false;
            label: for (//it is welled formated so just test against '0' and potential . D d  
            int i = 0; //it is welled formated so just test against '0' and potential . D d  
            i < source.length; //it is welled formated so just test against '0' and potential . D d  
            i++) {
                switch(source[i]) {
                    case '0':
                    case '.':
                        break;
                    case 'x':
                    case 'X':
                        isHexaDecimal = true;
                        break;
                    case 'e':
                    case 'E':
                    case 'f':
                    case 'F':
                    case 'd':
                    case 'D':
                        if (isHexaDecimal) {
                            return;
                        }
                        // no exponent - mantissa is all zero
                        break label;
                    case 'p':
                    case 'P':
                        break label;
                    default:
                        // error: the number is too small to represent
                        return;
                }
            }
        }
        value = floatValue;
        constant = Constant.fromValue(value);
    }

    /**
	 * Code generation for float literal
	 *
	 * @param currentScope org.eclipse.jdt.internal.compiler.lookup.BlockScope
	 * @param codeStream org.eclipse.jdt.internal.compiler.codegen.CodeStream
	 * @param valueRequired boolean
	 */
    public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
        int pc = codeStream.position;
        if (valueRequired)
            if ((implicitConversion >> 4) == T_float)
                codeStream.generateInlinedValue(value);
            else
                codeStream.generateConstant(constant, implicitConversion);
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    public TypeBinding literalType(BlockScope scope) {
        return FloatBinding;
    }

    public void traverse(ASTVisitor visitor, BlockScope blockScope) {
        visitor.visit(this, blockScope);
        visitor.endVisit(this, blockScope);
    }
}
