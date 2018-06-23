/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann - Contribution for
 *								bug 331649 - [compiler][null] consider null annotations for fields
 *								Bug 400874 - [1.8][compiler] Inference infrastructure should evolve to meet JLS8 18.x (Part G of JSR335 spec)
 *								Bug 426996 - [1.8][inference] try to avoid method Expression.unresolve()? 
 *     Jesper S Moller - Contributions for
 *							bug 382721 - [1.8][compiler] Effectively final variables needs special treatment
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.*;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;

public abstract class NameReference extends Reference implements InvocationSite {

    //may be aTypeBinding-aFieldBinding-aLocalVariableBinding
    public Binding binding;

    // modified receiver type - actual one according to namelookup
    public TypeBinding actualReceiverType;

    //no changeClass in java.
    public  NameReference() {
        // restrictiveFlag
        this.bits |= Binding.TYPE | Binding.VARIABLE;
    }

    /** 
 * Use this method only when sure that the current reference is <strong>not</strong>
 * a chain of several fields (QualifiedNameReference with more than one field).
 * Otherwise use {@link #lastFieldBinding()}.
 */
    public FieldBinding fieldBinding() {
        //check its use doing senders.........
        return (FieldBinding) this.binding;
    }

    public FieldBinding lastFieldBinding() {
        if ((this.bits & ASTNode.RestrictiveFlagMASK) == Binding.FIELD)
            // most subclasses only refer to one field anyway
            return fieldBinding();
        return null;
    }

    public InferenceContext18 freshInferenceContext(Scope scope) {
        return null;
    }

    public boolean isSuperAccess() {
        return false;
    }

    public boolean isTypeAccess() {
        // null is acceptable when we are resolving the first part of a reference
        return this.binding == null || this.binding instanceof ReferenceBinding;
    }

    public boolean isTypeReference() {
        return this.binding instanceof ReferenceBinding;
    }

    public void setActualReceiverType(ReferenceBinding receiverType) {
        // error scenario only
        if (receiverType == null)
            return;
        this.actualReceiverType = receiverType;
    }

    public void setDepth(int depth) {
        // flush previous depth if any
        this.bits &= ~DepthMASK;
        if (depth > 0) {
            // encoded on 8 bits
            this.bits |= (depth & 0xFF) << DepthSHIFT;
        }
    }

    public void setFieldIndex(int index) {
    // ignored
    }

    public abstract String unboundReferenceErrorName();

    public abstract char[][] getName();

    /* Called during code generation to ensure that outer locals's effectively finality is guaranteed. 
   Aborts if constraints are violated. Due to various complexities, this check is not conveniently
   implementable in resolve/analyze phases.
*/
    protected void checkEffectiveFinality(LocalVariableBinding localBinding, Scope scope) {
        if ((this.bits & ASTNode.IsCapturedOuterLocal) != 0) {
            if (!localBinding.isFinal() && !localBinding.isEffectivelyFinal()) {
                scope.problemReporter().cannotReferToNonEffectivelyFinalOuterLocal(localBinding, this);
                throw new AbortMethod(scope.referenceCompilationUnit().compilationResult, null);
            }
        }
    }
}
