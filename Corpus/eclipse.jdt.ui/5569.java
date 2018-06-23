/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.corext.refactoring.generics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.internal.corext.refactoring.typeconstraints.types.GenericType;
import org.eclipse.jdt.internal.corext.refactoring.typeconstraints.types.TType;
import org.eclipse.jdt.internal.corext.refactoring.typeconstraints2.CollectionElementVariable2;
import org.eclipse.jdt.internal.corext.refactoring.typeconstraints2.ConstraintVariable2;
import org.eclipse.jdt.internal.corext.refactoring.typeconstraints2.ITypeConstraint2;
import org.eclipse.jdt.internal.corext.refactoring.typeconstraints2.SubTypeConstraint2;
import org.eclipse.jdt.internal.corext.refactoring.typeconstraints2.TypeEquivalenceSet;

public class ParametricStructureComputer {

    public static class ParametricStructure {

        public static final ParametricStructure NONE = new ParametricStructure();

        private final GenericType fBase;

        private final ParametricStructure[] fParameters;

        public  ParametricStructure(GenericType base) {
            if (base == null)
                throw new NullPointerException();
            fBase = base;
            fParameters = new ParametricStructure[base.getTypeParameters().length];
        }

        private  ParametricStructure() {
            fBase = null;
            fParameters = new ParametricStructure[0];
        }

        public ParametricStructure[] getParameters() {
            return fParameters;
        }

        public GenericType getBase() {
            return fBase;
        }

        @Override
        public String toString() {
            if (this == NONE)
                //$NON-NLS-1$
                return "NONE";
            else
                return //$NON-NLS-1$
                "ParamStructure " + fBase.toString() + '<' + Arrays.asList(fParameters) + //$NON-NLS-1$
                '>';
        }
    }

    private static final boolean DEBUG_INITIALIZATION = false;

    /**
	 * Maps each ConstraintVariable2 onto an IType that is either an instance
	 * of AbstractTypeParameter, if the ConstraintVariable2 cannot possibly
	 * refer to a parametric type, or an instance of ParametricStructure with
	 * the appropriate sub-structure (if any) if it can.
	 */
    private final ElementStructureEnvironment fElemStructureEnv = new ElementStructureEnvironment();

    private final ConstraintVariable2[] fAllConstraintVariables;

    private InferTypeArgumentsTCModel fTCModel;

    public  ParametricStructureComputer(ConstraintVariable2[] allConstraintVariables, InferTypeArgumentsTCModel tcModel) {
        fAllConstraintVariables = allConstraintVariables;
        fTCModel = tcModel;
    }

    public ElementStructureEnvironment getElemStructureEnv() {
        return fElemStructureEnv;
    }

    private void dumpContainerStructure() {
        //$NON-NLS-1$
        System.out.println("\n*** Container Structure: ***\n");
        for (int i = 0; i < fAllConstraintVariables.length; i++) {
            ConstraintVariable2 v = fAllConstraintVariables[i];
            if (elemStructure(v) != null && !(elemStructure(v) == ParametricStructure.NONE))
                //$NON-NLS-1$ //$NON-NLS-2$
                System.out.println("elemStructure(" + v.toString() + ") = " + elemStructure(v));
        }
        System.out.println();
    }

    private Stack<ConstraintVariable2> fWorkList2 = new Stack();

    private void setStructureAndPush(ConstraintVariable2 v, ParametricStructure structure) {
        setElemStructure(v, structure);
        fWorkList2.push(v);
    }

    //TODO hard-wired to collections
    private void initializeContainerStructure() {
        if (DEBUG_INITIALIZATION)
            //$NON-NLS-1$
            System.out.println("  *** Seeding container structure ***");
        for (int i = 0; i < fAllConstraintVariables.length; i++) {
            ConstraintVariable2 v = fAllConstraintVariables[i];
            TType varType = declaredTypeOf(v);
            if (varType != null) {
                // List<String> to a binary method taking a raw List.
                if (isParametricType(varType) && !isUnmodifiableFieldOrMethod(v)) {
                    if (DEBUG_INITIALIZATION)
                        System.out.println(//$NON-NLS-1$
                        "Entity has           container structure: " + //$NON-NLS-1$
                        v);
                    setStructureAndPush(v, newParametricType(varType));
                } else if (!mightBeParametric(varType)) {
                    // Not a supertype of any container type - can't have container structure
                    if (DEBUG_INITIALIZATION)
                        System.out.println(//$NON-NLS-1$
                        "Entity DOES NOT have container structure: " + //$NON-NLS-1$
                        v);
                    setStructureAndPush(v, ParametricStructure.NONE);
                }
            // else we're not sure yet whether this has container structure
            } else {
            //				TType exprType= v.getType(); // TODO: always null!
            //
            //				if (isArrayAccess(v)) {
            //					if (DEBUG_INITIALIZATION) System.out.println("Entity DOES NOT have container structure: " + v);
            //					setStructureAndPush(v, NO_STRUCTURE); // definitely not container structure, Java 1.5 says no generics inside arrays
            //				} else if (isParametricType(exprType)) {
            //					if (DEBUG_INITIALIZATION) System.out.println("Entity has           container structure: " + v);
            //					setStructureAndPush(v, newParametricType(exprType));
            //				} else if (exprType != null && !mightBeParametric(exprType)) {
            //					// Not a supertype of any container type - can't have container structure
            //					if (DEBUG_INITIALIZATION) System.out.println("Entity DOES NOT have container structure: " + v);
            //					setStructureAndPush(v, NO_STRUCTURE);
            //				}
            // TODO Markus: the following just updates the set of child element variables of the parent variable of 'v'.
            // You already maintain this information automatically, so the code below is not needed...
            //				if (v instanceof CollectionElementVariable2) {
            //					CollectionElementVariable2 ev= (CollectionElementVariable2) v;
            //					int idx= ev.getDeclarationTypeVariableIndex(); //TODO : INDEX IS -1 IF THE TYPE VARIABLE COMES FROM A SUPERTYPE!!!
            //
            //					Collection/*<ConstraintVariable2>*/ vars= fTCModel.getElementVariables(ev).values();
            //
            //					if (vars == null) vars= new ConstraintVariable2[ev.getNumContainerTypeParams()];
            //					vars[idx]= ev;
            //					fVariableElementEnv.setElementVariables(ev.getParentConstraintVariable(), vars);
            //				}
            // else we're not sure yet whether this has container structure
            }
        }
    // Every variable v in fAllVariables is now in one of 3 states:
    //  - elemStructure(v) == some parametric type:       definitely container structure, but we may not know the sub-structure yet
    //  - elemStructure(v) == some AbstractTypeParameter: definitely not container structure
    //  - elemStructure(v) == null:                       we know nothing yet about its structure
    }

    protected static TType declaredTypeOf(ConstraintVariable2 cv) {
        //TODO: record original type of CollectionElementVariable2 iff source already had type parameter
        return cv.getType();
    //		if (v instanceof ContextualExpressionVariable) {
    //			ContextualExpressionVariable ev= (ContextualExpressionVariable) v;
    //
    //			return ev.getBinding();
    //		} else if (v instanceof ReturnTypeVariable) {
    //			ReturnTypeVariable rv= (ReturnTypeVariable) v;
    //
    //			return rv.getBinding();
    //		} else if (v instanceof RawBindingVariable) {
    //			RawBindingVariable rv= (RawBindingVariable) v;
    //
    //			return rv.getBinding();
    //		} else if (v instanceof ParameterTypeVariable) {
    //			ParameterTypeVariable pv= (ParameterTypeVariable) v;
    //
    //			return pv.getBinding();
    //		} else
    //			return null;
    }

    private boolean mightBeParametric(TType type) {
        //TODO check this is the only case?
        return isParametricType(type);
    }

    private void computeContainerStructure() {
        if (DEBUG_INITIALIZATION)
            //$NON-NLS-1$
            System.out.println("\n*** Computing Container Structure ***\n");
        initializeContainerStructure();
        if (DEBUG_INITIALIZATION)
            dumpContainerStructure();
        while (!fWorkList2.isEmpty()) {
            ConstraintVariable2 v = fWorkList2.pop();
            List<ITypeConstraint2> usedIn = fTCModel.getUsedIn(v);
            for (Iterator<ITypeConstraint2> iter = usedIn.iterator(); iter.hasNext(); ) {
                SubTypeConstraint2 stc = (SubTypeConstraint2) iter.next();
                ConstraintVariable2 lhs = stc.getLeft();
                ConstraintVariable2 rhs = stc.getRight();
                unifyContainerStructure(lhs, rhs);
            }
            TypeEquivalenceSet typeEquivalenceSet = v.getTypeEquivalenceSet();
            if (typeEquivalenceSet != null) {
                ConstraintVariable2[] contributingVariables = typeEquivalenceSet.getContributingVariables();
                for (int i = 0; i + 1 < contributingVariables.length; i++) {
                    ConstraintVariable2 first = contributingVariables[i];
                    ConstraintVariable2 second = contributingVariables[i + 1];
                    unifyContainerStructure(first, second);
                }
            }
        }
        if (DEBUG_INITIALIZATION)
            dumpContainerStructure();
    }

    private void unifyContainerStructure(ConstraintVariable2 lhs, ConstraintVariable2 rhs) {
        // RMF 8/19/2004 - exclude propagation through unmodifiable fields/methods
        if (isUnmodifiableFieldOrMethod(lhs) || isUnmodifiableFieldOrMethod(rhs))
            return;
        if (DEBUG_INITIALIZATION)
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            System.out.println("Examining constraint " + lhs + " {" + elemStructure(lhs) + "} <= " + rhs + " {" + elemStructure(rhs) + "}");
        if (updateStructureOfVar(lhs, elemStructure(rhs), TypeOperator.SubType)) {
            if (lhs instanceof CollectionElementVariable2)
                updateParentContainerStructureFrom((CollectionElementVariable2) lhs, rhs);
            updateElementVarStructureFromParent(lhs);
        }
        if (updateStructureOfVar(rhs, elemStructure(lhs), TypeOperator.SuperType)) {
            if (rhs instanceof CollectionElementVariable2)
                updateParentContainerStructureFrom((CollectionElementVariable2) rhs, lhs);
            updateElementVarStructureFromParent(rhs);
        }
    }

    private ParametricStructure newParametricType(TType varType) {
        //TODO: create CollectionElementVariable2s if necessary?
        GenericType genericType = (GenericType) varType.getTypeDeclaration();
        return new ParametricStructure(genericType);
    }

    private boolean isUnmodifiableFieldOrMethod(ConstraintVariable2 v) {
        //TODO: find out whether it's declared in a binary type
        return false;
    }

    private boolean isParametricType(TType type) {
        return type.isParameterizedType() || type.isGenericType() || (type.isRawType() && type.getTypeDeclaration().isGenericType());
    }

    /**
	 * Updates the structure of the i'th type parameter of the given ParametricStructure
	 * to be consistent with that of 'otherStructure'.
	 */
    private boolean updateStructureOfIthParamFrom(ParametricStructure structure1, int i, ParametricStructure otherStructure) {
        if (// no structure info to use to update 'structure1'
        (otherStructure == null))
            return false;
        //$NON-NLS-1$
        Assert.isTrue(structure1 != otherStructure, "updateStructureOfIthParamFrom(): attempt to unify ith param of a parametric type with itself!");
        ParametricStructure param1 = structure1.getParameters()[i];
        boolean param1Unknown = (param1 == null);
        if (param1Unknown) {
            if (DEBUG_INITIALIZATION)
                //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                System.out.println("  setting param " + i + " of " + structure1 + " to " + otherStructure);
            structure1.getParameters()[i] = otherStructure;
            return true;
        }
        boolean paramStructured = !(param1 == ParametricStructure.NONE);
        boolean otherStructured = !(otherStructure == ParametricStructure.NONE);
        if (// both parametric
        paramStructured && otherStructured) {
            // conservatively make the type param of structure1 unstructured.
            if (param1.getBase().equals(otherStructure.getBase()))
                return updateStructureOfType(param1, otherStructure);
            else {
                structure1.getParameters()[i] = ParametricStructure.NONE;
                return true;
            }
        }
        return false;
    }

    /**
	 * Updates the structure of any subsidiary element variables (if any) for
	 * the given ConstraintVariable2 (if it is in fact a container).
	 */
    private void updateElementVarStructureFromParent(ConstraintVariable2 v) {
        // Propagate structure from container variable to any subsidiary element variables
        if (elemStructure(v) != ParametricStructure.NONE && fTCModel.getElementVariables(v).size() > 0) {
            ParametricStructure t = elemStructure(v);
            for (Iterator<CollectionElementVariable2> iterator = fTCModel.getElementVariables(v).values().iterator(); iterator.hasNext(); ) {
                CollectionElementVariable2 typeVar = iterator.next();
                int declarationTypeVariableIndex = typeVar.getDeclarationTypeVariableIndex();
                if (declarationTypeVariableIndex != CollectionElementVariable2.NOT_DECLARED_TYPE_VARIABLE_INDEX)
                    updateStructureOfVar(typeVar, t.getParameters()[declarationTypeVariableIndex], TypeOperator.Equals);
            }
        }
    }

    /**
	 * Updates the structure of the parent container variable of the given
	 * CollectionElementVariable2 from the structure of 'v1'.
	 * @param elemVar
	 * @param v1
	 */
    private void updateParentContainerStructureFrom(CollectionElementVariable2 elemVar, ConstraintVariable2 v1) {
        ConstraintVariable2 elemContainer = elemVar.getParentConstraintVariable();
        // This could be something that appears like it should have container
        // structure, but doesn't, e.g., an array access for an array of containers
        // (JDK 1.5 disallows arrays of parametric types). So if it doesn't have
        // container structure, ignore it.
        ParametricStructure elemContainerStructure = elemStructure(elemContainer);
        if (elemContainerStructure == ParametricStructure.NONE)
            return;
        if (// handle clone()
        elemContainerStructure == null) {
            elemContainerStructure = newParametricType(elemContainer.getType());
            setStructureAndPush(elemContainer, elemContainerStructure);
        }
        ParametricStructure v1Structure = elemStructure(v1);
        //TODO: index is NOT_DECLARED_TYPE_VARIABLE_INDEX if the type variable comes from a supertype!!!
        int parmIdx = elemVar.getDeclarationTypeVariableIndex();
        if (parmIdx == CollectionElementVariable2.NOT_DECLARED_TYPE_VARIABLE_INDEX)
            //TODO: ParametricStructure should use type variable keys instead of index
            return;
        if (// avoid creating cyclic structure
        elemContainerStructure == v1Structure || containsSubStructure(v1Structure, elemContainerStructure)) {
            if (!(elemStructure(elemVar) == ParametricStructure.NONE))
                setStructureAndPush(elemVar, ParametricStructure.NONE);
            if (elemContainerStructure.getParameters()[parmIdx] == null) {
                elemContainerStructure.getParameters()[parmIdx] = ParametricStructure.NONE;
                fWorkList2.push(elemContainer);
            }
        } else if (updateStructureOfIthParamFrom(elemContainerStructure, parmIdx, v1Structure)) {
            setStructureAndPush(elemVar, elemContainerStructure.getParameters()[parmIdx]);
            fWorkList2.push(elemContainer);
            if (DEBUG_INITIALIZATION)
                //$NON-NLS-1$ //$NON-NLS-2$
                System.out.println("  updated structure of " + elemContainer + " to " + elemContainerStructure);
        }
    }

    private boolean containsSubStructure(ParametricStructure containingStructure, ParametricStructure subStructure) {
        if (containingStructure == null)
            return false;
        ParametricStructure[] parameters = containingStructure.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            ParametricStructure parameter = parameters[i];
            if (parameter == subStructure)
                return true;
            else if (containsSubStructure(parameter, subStructure))
                return true;
        }
        return false;
    }

    /**
	 * Make type1's sub-structure at least as detailed as that of type2 by
	 * copying the structure of type2's parameters (or its parameters' parameters)
	 * into type1's parameters.
	 */
    private boolean updateStructureOfType(ParametricStructure type1, ParametricStructure type2) {
        if (type1 == null || type2 == null)
            return false;
        ParametricStructure[] parms1 = type1.getParameters();
        ParametricStructure[] parms2 = type2.getParameters();
        boolean someChange = false;
        Assert.isTrue(parms1.length == parms2.length);
        for (int i = 0; i < parms1.length; i++) {
            if (// avoid creating cyclic structures!
            type1 == parms2[i]) {
                if (parms1[i] != ParametricStructure.NONE) {
                    parms1[i] = ParametricStructure.NONE;
                    someChange = true;
                }
            } else if (updateStructureOfIthParamFrom(type1, i, parms2[i]))
                someChange = true;
        }
        return someChange;
    }

    static class TypeOperator {

        // This could be a ConstraintOperator, if that had supertype
        private final String fOp;

        private  TypeOperator(String op) {
            fOp = op;
        }

        //$NON-NLS-1$
        public static TypeOperator Equals = new TypeOperator("=^=");

        //$NON-NLS-1$
        public static TypeOperator SubType = new TypeOperator("<=");

        //$NON-NLS-1$
        public static TypeOperator SuperType = new TypeOperator("=>");

        @Override
        public String toString() {
            return fOp;
        }
    }

    /**
	 * Updates the structure of the given ConstraintVariable to be consistent
	 * with the structure of 'type2', in accordance with the given TypeOperator.
	 * If any changes are made, pushes the variable onto fWorkList2.
	 */
    private boolean updateStructureOfVar(ConstraintVariable2 v, ParametricStructure type2, TypeOperator op) {
        if (// no structure info to use to update 'v'
        (type2 == null))
            return false;
        ParametricStructure vStructure = elemStructure(v);
        boolean vStructureUnknown = (vStructure == null);
        boolean type2Structured = type2 != ParametricStructure.NONE;
        if (vStructureUnknown) {
            if (DEBUG_INITIALIZATION)
                //$NON-NLS-1$ //$NON-NLS-2$
                System.out.println("  setting structure of " + v + " to " + type2);
            setStructureAndPush(v, type2);
            return true;
        }
        boolean vStructured = vStructure != ParametricStructure.NONE;
        if (vStructured && !type2Structured) {
            // supertype (or equal to) something unstructured.
            if (op == TypeOperator.Equals || op == TypeOperator.SuperType) {
                setStructureAndPush(v, type2);
                return true;
            }
        } else if (// both are structured (parametric types)
        vStructured && type2Structured) {
            // conservatively make v unstructured.
            if (// different parametric types?
            !vStructure.getBase().equals(type2.getBase())) {
                if (// if (v >= other), v can't have parametric structure
                op == TypeOperator.SuperType) {
                    setStructureAndPush(v, ParametricStructure.NONE);
                    return true;
                }
            } else if (updateStructureOfType(vStructure, type2)) {
                fWorkList2.push(v);
                return true;
            }
        }
        return false;
    }

    private void setElemStructure(ConstraintVariable2 v, ParametricStructure t) {
        fElemStructureEnv.setElemStructure(v, t);
    }

    private ParametricStructure elemStructure(ConstraintVariable2 v) {
        return fElemStructureEnv.elemStructure(v);
    }

    public Collection<CollectionElementVariable2> createElemConstraintVariables() {
        Collection<CollectionElementVariable2> newVars = new HashSet();
        computeContainerStructure();
        if (DEBUG_INITIALIZATION)
            //$NON-NLS-1$
            System.out.println("\n*** Creating Element Variables: ***\n");
        for (int i = 0; i < fAllConstraintVariables.length; i++) {
            newVars.addAll(createVariablesFor(fAllConstraintVariables[i]));
        }
        //		fAllConstraintVariables.addAll(newVars);
        return newVars;
    }

    private Collection<CollectionElementVariable2> createVariablesFor(ConstraintVariable2 v) {
        ParametricStructure t = elemStructure(v);
        if (t == null || t == ParametricStructure.NONE)
            return Collections.emptyList();
        ParametricStructure parmType = t;
        TType base = parmType.getBase();
        if (isParametricType(base)) {
            return createAndInitVars(v, parmType);
        }
        //$NON-NLS-1$
        throw new IllegalStateException("Attempt to create element variables for parametric variable of unknown type: " + parmType);
    }

    private Collection<CollectionElementVariable2> getElementVariables(GenericType base, ConstraintVariable2 parent) {
        fTCModel.makeElementVariables(parent, base);
        return fTCModel.getElementVariables(parent).values();
    }

    private Collection<CollectionElementVariable2> createAndInitVars(ConstraintVariable2 v, ParametricStructure parmType) {
        //TODO (->): in InferTypeArgumentsConstraintsSolver#createInitialEstimate(..)
        //->		fTypeEstimates.setEstimateOf(v, SubTypesOfSingleton.create(ParametricStructure.getBaseContainerType(parmType.getBase(), sHierarchy)));
        Collection<CollectionElementVariable2> elementVars = getElementVariables(parmType.getBase(), v);
        //->		setNewTypeParamEstimateForEach(elementVars);
        Collection<CollectionElementVariable2> result = createVars(elementVars, parmType.getParameters());
        return result;
    }

    private Collection<CollectionElementVariable2> createVars(Collection<CollectionElementVariable2> cvs, ParametricStructure[] parms) {
        if (// happens, e.g., for Properties (non-parametric)
        parms.length > 0) {
            //			Assert.isTrue(cvs.size() == parms.length, "cvs.length==" + cvs.size() + " parms.length=" + parms.length); //assumption is wrong in presence of NOT_DECLARED_TYPE_VARIABLE_INDEX
            for (Iterator<CollectionElementVariable2> iter = cvs.iterator(); iter.hasNext(); ) {
                CollectionElementVariable2 childVar = iter.next();
                int declarationTypeVariableIndex = childVar.getDeclarationTypeVariableIndex();
                if (declarationTypeVariableIndex != CollectionElementVariable2.NOT_DECLARED_TYPE_VARIABLE_INDEX)
                    setElemStructure(childVar, parms[declarationTypeVariableIndex]);
            }
        } else {
            for (Iterator<CollectionElementVariable2> iter = cvs.iterator(); iter.hasNext(); ) {
                CollectionElementVariable2 childVar = iter.next();
                int declarationTypeVariableIndex = childVar.getDeclarationTypeVariableIndex();
                if (declarationTypeVariableIndex != CollectionElementVariable2.NOT_DECLARED_TYPE_VARIABLE_INDEX)
                    setElemStructure(childVar, ParametricStructure.NONE);
            }
        }
        //roughly
        List<CollectionElementVariable2> result = new ArrayList(cvs.size() * 2);
        for (Iterator<CollectionElementVariable2> iter = cvs.iterator(); iter.hasNext(); ) {
            CollectionElementVariable2 childVar = iter.next();
            int declarationTypeVariableIndex = childVar.getDeclarationTypeVariableIndex();
            if (declarationTypeVariableIndex != CollectionElementVariable2.NOT_DECLARED_TYPE_VARIABLE_INDEX) {
                result.add(childVar);
                result.addAll(createVariablesFor(childVar));
            }
        }
        return result;
    }
}
