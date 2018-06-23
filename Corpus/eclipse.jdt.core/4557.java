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

import org.eclipse.jdt.internal.compiler.impl.*;
import org.eclipse.jdt.internal.compiler.codegen.*;
import org.eclipse.jdt.internal.compiler.flow.*;
import org.eclipse.jdt.internal.compiler.lookup.*;

public abstract class Statement extends ASTNode {

    public abstract FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo);

    /**
	 * INTERNAL USE ONLY.
	 * This is used to redirect inter-statements jumps.
	 */
    public void branchChainTo(Label label) {
    // do nothing by default
    }

    // Report an error if necessary
    public boolean complainIfUnreachable(FlowInfo flowInfo, BlockScope scope, boolean didAlreadyComplain) {
        if ((flowInfo.reachMode() & FlowInfo.UNREACHABLE) != 0) {
            this.bits &= ~ASTNode.IsReachableMASK;
            boolean reported = flowInfo == FlowInfo.DEAD_END;
            if (!didAlreadyComplain && reported) {
                scope.problemReporter().unreachableCode(this);
            }
            // keep going for fake reachable
            return reported;
        }
        return false;
    }

    public abstract void generateCode(BlockScope currentScope, CodeStream codeStream);

    public boolean isEmptyBlock() {
        return false;
    }

    public boolean isValidJavaStatement() {
        return true;
    }

    public StringBuffer print(int indent, StringBuffer output) {
        return printStatement(indent, output);
    }

    public abstract StringBuffer printStatement(int indent, StringBuffer output);

    public abstract void resolve(BlockScope scope);

    public Constant resolveCase(BlockScope scope, TypeBinding testType, SwitchStatement switchStatement) {
        // statement within a switch that are not case are treated as normal statement.... 
        resolve(scope);
        return null;
    }
}
