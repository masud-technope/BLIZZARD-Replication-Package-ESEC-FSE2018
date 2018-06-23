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
package org.eclipse.jdt.internal.compiler.lookup;

public class NestedTypeBinding extends SourceTypeBinding {

    public SourceTypeBinding enclosingType;

    public SyntheticArgumentBinding[] enclosingInstances;

    public SyntheticArgumentBinding[] outerLocalVariables;

    // amount of slots used by synthetic enclosing instances
    public int enclosingInstancesSlotSize;

    // amount of slots used by synthetic outer local variables
    public int outerLocalVariablesSlotSize;

    public  NestedTypeBinding(char[][] typeName, ClassScope scope, SourceTypeBinding enclosingType) {
        super(typeName, enclosingType.fPackage, scope);
        this.tagBits |= IsNestedType;
        this.enclosingType = enclosingType;
    }

    /* Add a new synthetic argument for <actualOuterLocalVariable>.
	* Answer the new argument or the existing argument if one already existed.
	*/
    public SyntheticArgumentBinding addSyntheticArgument(LocalVariableBinding actualOuterLocalVariable) {
        SyntheticArgumentBinding synthLocal = null;
        if (outerLocalVariables == null) {
            synthLocal = new SyntheticArgumentBinding(actualOuterLocalVariable);
            outerLocalVariables = new SyntheticArgumentBinding[] { synthLocal };
        } else {
            int size = outerLocalVariables.length;
            int newArgIndex = size;
            for (// must search backwards
            int i = size; // must search backwards
            --i >= 0; ) {
                if (outerLocalVariables[i].actualOuterLocalVariable == actualOuterLocalVariable)
                    // already exists
                    return outerLocalVariables[i];
                if (outerLocalVariables[i].id > actualOuterLocalVariable.id)
                    newArgIndex = i;
            }
            SyntheticArgumentBinding[] synthLocals = new SyntheticArgumentBinding[size + 1];
            System.arraycopy(outerLocalVariables, 0, synthLocals, 0, newArgIndex);
            synthLocals[newArgIndex] = synthLocal = new SyntheticArgumentBinding(actualOuterLocalVariable);
            System.arraycopy(outerLocalVariables, newArgIndex, synthLocals, newArgIndex + 1, size - newArgIndex);
            outerLocalVariables = synthLocals;
        }
        //System.out.println("Adding synth arg for local var: " + new String(actualOuterLocalVariable.name) + " to: " + new String(this.readableName()));
        if (scope.referenceCompilationUnit().isPropagatingInnerClassEmulation)
            this.updateInnerEmulationDependents();
        return synthLocal;
    }

    /* Add a new synthetic argument for <enclosingType>.
	* Answer the new argument or the existing argument if one already existed.
	*/
    public SyntheticArgumentBinding addSyntheticArgument(ReferenceBinding targetEnclosingType) {
        SyntheticArgumentBinding synthLocal = null;
        if (enclosingInstances == null) {
            synthLocal = new SyntheticArgumentBinding(targetEnclosingType);
            enclosingInstances = new SyntheticArgumentBinding[] { synthLocal };
        } else {
            int size = enclosingInstances.length;
            int newArgIndex = size;
            for (int i = size; --i >= 0; ) {
                if (enclosingInstances[i].type == targetEnclosingType)
                    return // already exists
                    enclosingInstances[// already exists
                    i];
                if (this.enclosingType() == targetEnclosingType)
                    newArgIndex = 0;
            }
            SyntheticArgumentBinding[] newInstances = new SyntheticArgumentBinding[size + 1];
            System.arraycopy(enclosingInstances, 0, newInstances, newArgIndex == 0 ? 1 : 0, size);
            newInstances[newArgIndex] = synthLocal = new SyntheticArgumentBinding(targetEnclosingType);
            enclosingInstances = newInstances;
        }
        //System.out.println("Adding synth arg for enclosing type: " + new String(enclosingType.readableName()) + " to: " + new String(this.readableName()));
        if (scope.referenceCompilationUnit().isPropagatingInnerClassEmulation)
            this.updateInnerEmulationDependents();
        return synthLocal;
    }

    /* Add a new synthetic argument and field for <actualOuterLocalVariable>.
	* Answer the new argument or the existing argument if one already existed.
	*/
    public SyntheticArgumentBinding addSyntheticArgumentAndField(LocalVariableBinding actualOuterLocalVariable) {
        SyntheticArgumentBinding synthLocal = addSyntheticArgument(actualOuterLocalVariable);
        if (synthLocal == null)
            return null;
        if (synthLocal.matchingField == null)
            synthLocal.matchingField = addSyntheticField(actualOuterLocalVariable);
        return synthLocal;
    }

    /* Add a new synthetic argument and field for <enclosingType>.
	* Answer the new argument or the existing argument if one already existed.
	*/
    public SyntheticArgumentBinding addSyntheticArgumentAndField(ReferenceBinding targetEnclosingType) {
        SyntheticArgumentBinding synthLocal = addSyntheticArgument(targetEnclosingType);
        if (synthLocal == null)
            return null;
        if (synthLocal.matchingField == null)
            synthLocal.matchingField = addSyntheticField(targetEnclosingType);
        return synthLocal;
    }

    /**
	 * Compute the resolved positions for all the synthetic arguments
	 */
    public final void computeSyntheticArgumentSlotSizes() {
        int slotSize = 0;
        // insert enclosing instances first, followed by the outerLocals
        int enclosingInstancesCount = this.enclosingInstances == null ? 0 : this.enclosingInstances.length;
        for (int i = 0; i < enclosingInstancesCount; i++) {
            SyntheticArgumentBinding argument = this.enclosingInstances[i];
            // position the enclosing instance synthetic arg
            // shift by 1 to leave room for aload0==this
            argument.resolvedPosition = slotSize + 1;
            if (// no more than 255 words of arguments
            slotSize + 1 > 0xFF) {
                this.scope.problemReporter().noMoreAvailableSpaceForArgument(argument, this.scope.referenceType());
            }
            if ((argument.type == LongBinding) || (argument.type == DoubleBinding)) {
                slotSize += 2;
            } else {
                slotSize++;
            }
        }
        this.enclosingInstancesSlotSize = slotSize;
        // reset, outer local are not positionned yet, since will be appended to user arguments
        slotSize = 0;
        int outerLocalsCount = this.outerLocalVariables == null ? 0 : this.outerLocalVariables.length;
        for (int i = 0; i < outerLocalsCount; i++) {
            SyntheticArgumentBinding argument = this.outerLocalVariables[i];
            // do NOT position the outerlocal synthetic arg yet,  since will be appended to user arguments
            if ((argument.type == LongBinding) || (argument.type == DoubleBinding)) {
                slotSize += 2;
            } else {
                slotSize++;
            }
        }
        this.outerLocalVariablesSlotSize = slotSize;
    }

    /* Answer the receiver's enclosing type... null if the receiver is a top level type.
	*/
    public ReferenceBinding enclosingType() {
        return enclosingType;
    }

    /* Answer the synthetic argument for <actualOuterLocalVariable> or null if one does not exist.
	*/
    public SyntheticArgumentBinding getSyntheticArgument(LocalVariableBinding actualOuterLocalVariable) {
        // is null if no outer local variables are known
        if (outerLocalVariables == null)
            return null;
        for (int i = outerLocalVariables.length; --i >= 0; ) if (outerLocalVariables[i].actualOuterLocalVariable == actualOuterLocalVariable)
            return outerLocalVariables[i];
        return null;
    }

    public SyntheticArgumentBinding[] syntheticEnclosingInstances() {
        // is null if no enclosing instances are required
        return enclosingInstances;
    }

    public ReferenceBinding[] syntheticEnclosingInstanceTypes() {
        if (enclosingInstances == null)
            return null;
        int length = enclosingInstances.length;
        ReferenceBinding types[] = new ReferenceBinding[length];
        for (int i = 0; i < length; i++) types[i] = (ReferenceBinding) enclosingInstances[i].type;
        return types;
    }

    public SyntheticArgumentBinding[] syntheticOuterLocalVariables() {
        // is null if no outer locals are required
        return outerLocalVariables;
    }

    /*
	 * Trigger the dependency mechanism forcing the innerclass emulation
	 * to be propagated to all dependent source types.
	 */
    public void updateInnerEmulationDependents() {
    // nothing to do in general, only local types are doing anything
    }

    /* Answer the synthetic argument for <targetEnclosingType> or null if one does not exist.
	*/
    public SyntheticArgumentBinding getSyntheticArgument(ReferenceBinding targetEnclosingType, boolean onlyExactMatch) {
        // is null if no enclosing instances are known
        if (enclosingInstances == null)
            return null;
        // exact match
        for (int i = enclosingInstances.length; --i >= 0; ) if (enclosingInstances[i].type == targetEnclosingType)
            if (enclosingInstances[i].actualOuterLocalVariable == null)
                return enclosingInstances[i];
        // class S extends T { class N extends M {}} --> need to use S as a default enclosing instance for the super constructor call in N().
        if (!onlyExactMatch) {
            for (int i = enclosingInstances.length; --i >= 0; ) if (enclosingInstances[i].actualOuterLocalVariable == null)
                if (((ReferenceBinding) enclosingInstances[i].type).findSuperTypeErasingTo(targetEnclosingType) != null)
                    return enclosingInstances[i];
        }
        return null;
    }
}
