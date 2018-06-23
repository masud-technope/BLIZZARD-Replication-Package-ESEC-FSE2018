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
import org.eclipse.jdt.internal.compiler.codegen.*;
import org.eclipse.jdt.internal.compiler.flow.*;
import org.eclipse.jdt.internal.compiler.lookup.*;

public class CompoundAssignment extends Assignment implements OperatorIds {

    public int operator;

    public int assignmentImplicitConversion;

    public  CompoundAssignment(Expression lhs, Expression expression, int operator, int sourceEnd) {
        super(lhs, expression, sourceEnd);
        // tag lhs as NON assigned - it is also a read access
        lhs.bits &= ~IsStrictlyAssignedMASK;
        // tag lhs as assigned by compound
        lhs.bits |= IsCompoundAssignedMASK;
        this.operator = operator;
    }

    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        return ((Reference) lhs).analyseAssignment(currentScope, flowContext, flowInfo, this, true).unconditionalInits();
    }

    public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
        // various scenarii are possible, setting an array reference, 
        // a field reference, a blank final field reference, a field of an enclosing instance or 
        // just a local variable.
        int pc = codeStream.position;
        ((Reference) lhs).generateCompoundAssignment(currentScope, codeStream, expression, operator, assignmentImplicitConversion, valueRequired);
        if (valueRequired) {
            codeStream.generateImplicitConversion(implicitConversion);
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    public String operatorToString() {
        switch(operator) {
            case PLUS:
                //$NON-NLS-1$
                return "+=";
            case MINUS:
                //$NON-NLS-1$
                return "-=";
            case MULTIPLY:
                //$NON-NLS-1$
                return "*=";
            case DIVIDE:
                //$NON-NLS-1$
                return "/=";
            case AND:
                //$NON-NLS-1$
                return "&=";
            case OR:
                //$NON-NLS-1$
                return "|=";
            case XOR:
                //$NON-NLS-1$
                return "^=";
            case REMAINDER:
                //$NON-NLS-1$
                return "%=";
            case LEFT_SHIFT:
                //$NON-NLS-1$
                return "<<=";
            case RIGHT_SHIFT:
                //$NON-NLS-1$
                return ">>=";
            case UNSIGNED_RIGHT_SHIFT:
                //$NON-NLS-1$
                return ">>>=";
        }
        //$NON-NLS-1$
        return "unknown operator";
    }

    public StringBuffer printExpressionNoParenthesis(int indent, StringBuffer output) {
        lhs.printExpression(indent, output).append(' ').append(operatorToString()).append(' ');
        return expression.printExpression(0, output);
    }

    public TypeBinding resolveType(BlockScope scope) {
        constant = NotAConstant;
        if (!(this.lhs instanceof Reference) || this.lhs.isThis()) {
            scope.problemReporter().expressionShouldBeAVariable(this.lhs);
            return null;
        }
        TypeBinding lhsType = lhs.resolveType(scope);
        TypeBinding expressionType = expression.resolveType(scope);
        if (lhsType == null || expressionType == null)
            return null;
        int lhsId = lhsType.id;
        int expressionId = expressionType.id;
        if (restrainUsageToNumericTypes() && !lhsType.isNumericType()) {
            scope.problemReporter().operatorOnlyValidOnNumericType(this, lhsType, expressionType);
            return null;
        }
        if (lhsId > 15 || expressionId > 15) {
            if (// String += Thread is valid whereas Thread += String  is not
            lhsId != T_String) {
                scope.problemReporter().invalidOperator(this, lhsType, expressionType);
                return null;
            }
            // use the Object has tag table
            expressionId = T_Object;
        }
        // the code is an int
        // (cast)  left   Op (cast)  rigth --> result 
        //  0000   0000       0000   0000      0000
        //  <<16   <<12       <<8     <<4        <<0
        // the conversion is stored INTO the reference (info needed for the code gen)
        int result = OperatorExpression.OperatorSignatures[operator][(lhsId << 4) + expressionId];
        if (result == T_undefined) {
            scope.problemReporter().invalidOperator(this, lhsType, expressionType);
            return null;
        }
        if (operator == PLUS) {
            if (lhsId == T_JavaLangObject) {
                // <Object> += <String> is illegal (39248)
                scope.problemReporter().invalidOperator(this, lhsType, expressionType);
                return null;
            } else {
                // <int | boolean> += <String> is illegal
                if ((lhsType.isNumericType() || lhsId == T_boolean) && !expressionType.isNumericType()) {
                    scope.problemReporter().invalidOperator(this, lhsType, expressionType);
                    return null;
                }
            }
        }
        // TODO (philippe) should retrofit in using #computeConversion
        lhs.implicitConversion = result >>> 12;
        expression.implicitConversion = (result >>> 4) & 0x000FF;
        assignmentImplicitConversion = (lhsId << 4) + (result & 0x0000F);
        return this.resolvedType = lhsType;
    }

    public boolean restrainUsageToNumericTypes() {
        return false;
    }

    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            lhs.traverse(visitor, scope);
            expression.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
}
