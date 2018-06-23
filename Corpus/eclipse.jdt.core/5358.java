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
import org.eclipse.jdt.internal.compiler.flow.*;
import org.eclipse.jdt.internal.compiler.lookup.*;

public class BreakStatement extends BranchStatement {

    public  BreakStatement(char[] label, int sourceStart, int e) {
        super(label, sourceStart, e);
    }

    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        // here requires to generate a sequence of finally blocks invocations depending corresponding
        // to each of the traversed try statements, so that execution will terminate properly.
        // lookup the label, this should answer the returnContext
        FlowContext targetContext = (label == null) ? flowContext.getTargetContextForDefaultBreak() : flowContext.getTargetContextForBreakLabel(label);
        if (targetContext == null) {
            if (label == null) {
                currentScope.problemReporter().invalidBreak(this);
            } else {
                currentScope.problemReporter().undefinedLabel(this);
            }
            // pretend it did not break since no actual target
            return flowInfo;
        }
        targetLabel = targetContext.breakLabel();
        FlowContext traversedContext = flowContext;
        int subIndex = 0, maxSub = 5;
        subroutines = new SubRoutineStatement[maxSub];
        do {
            SubRoutineStatement sub;
            if ((sub = traversedContext.subRoutine()) != null) {
                if (subIndex == maxSub) {
                    // grow
                    System.arraycopy(// grow
                    subroutines, // grow
                    0, // grow
                    (subroutines = new SubRoutineStatement[maxSub *= 2]), // grow
                    0, // grow
                    subIndex);
                }
                subroutines[subIndex++] = sub;
                if (sub.isSubRoutineEscaping()) {
                    break;
                }
            }
            traversedContext.recordReturnFrom(flowInfo.unconditionalInits());
            ASTNode node;
            if ((node = traversedContext.associatedNode) instanceof TryStatement) {
                TryStatement tryStatement = (TryStatement) node;
                // collect inits			
                flowInfo.addInitializationsFrom(tryStatement.subRoutineInits);
            } else if (traversedContext == targetContext) {
                // only record break info once accumulated through subroutines, and only against target context
                targetContext.recordBreakFrom(flowInfo);
                break;
            }
        } while ((traversedContext = traversedContext.parent) != null);
        // resize subroutines
        if (subIndex != maxSub) {
            System.arraycopy(subroutines, 0, (subroutines = new SubRoutineStatement[subIndex]), 0, subIndex);
        }
        return FlowInfo.DEAD_END;
    }

    public StringBuffer printStatement(int tab, StringBuffer output) {
        //$NON-NLS-1$
        printIndent(tab, output).append("break ");
        if (label != null)
            output.append(label);
        return output.append(';');
    }

    public void traverse(ASTVisitor visitor, BlockScope blockscope) {
        visitor.visit(this, blockscope);
        visitor.endVisit(this, blockscope);
    }
}
