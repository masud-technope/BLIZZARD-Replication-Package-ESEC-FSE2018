/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.codeassist.complete;

import org.eclipse.jdt.internal.compiler.ast.*;
import org.eclipse.jdt.internal.compiler.lookup.*;

public class CompletionOnMemberAccess extends FieldReference {

    public boolean isInsideAnnotation;

    public  CompletionOnMemberAccess(char[] source, long pos, boolean isInsideAnnotation) {
        super(source, pos);
        this.isInsideAnnotation = isInsideAnnotation;
    }

    public StringBuffer printExpression(int indent, StringBuffer output) {
        //$NON-NLS-1$
        output.append("<CompleteOnMemberAccess:");
        return super.printExpression(0, output).append('>');
    }

    public TypeBinding resolveType(BlockScope scope) {
        this.actualReceiverType = this.receiver.resolveType(scope);
        if ((this.actualReceiverType == null || !this.actualReceiverType.isValidBinding()) && this.receiver instanceof MessageSend) {
            MessageSend messageSend = (MessageSend) this.receiver;
            if (messageSend.receiver instanceof ThisReference) {
                Expression[] arguments = messageSend.arguments;
                int length = arguments == null ? 0 : arguments.length;
                TypeBinding[] argBindings = new TypeBinding[length];
                for (int i = 0; i < length; i++) {
                    argBindings[i] = arguments[i].resolvedType;
                    if (argBindings[i] == null || !argBindings[i].isValidBinding()) {
                        throw new CompletionNodeFound();
                    }
                }
                ProblemMethodBinding problemMethodBinding = new ProblemMethodBinding(messageSend.selector, argBindings, ProblemReasons.NotFound);
                throw new CompletionNodeFound(this, problemMethodBinding, scope);
            }
        }
        if (this.actualReceiverType == null || !this.actualReceiverType.isValidBinding())
            throw new CompletionNodeFound();
        else
            throw new CompletionNodeFound(this, this.actualReceiverType, scope);
    // array types are passed along to find the length field
    }
}
