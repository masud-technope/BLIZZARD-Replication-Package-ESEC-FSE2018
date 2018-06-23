/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.astview.views;

import java.util.ArrayList;
import org.eclipse.jdt.astview.ASTViewPlugin;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMemberValuePairBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.StringLiteral;

/**
 *
 */
public class Binding extends ASTAttribute {

    private final IBinding fBinding;

    private final String fLabel;

    private final Object fParent;

    private final boolean fIsRelevant;

    public  Binding(Object parent, String label, IBinding binding, boolean isRelevant) {
        fParent = parent;
        fBinding = binding;
        fLabel = label;
        fIsRelevant = isRelevant;
    }

    @Override
    public Object getParent() {
        return fParent;
    }

    public IBinding getBinding() {
        return fBinding;
    }

    public boolean hasBindingProperties() {
        return fBinding != null;
    }

    public boolean isRelevant() {
        return fIsRelevant;
    }

    private static boolean isType(int typeKinds, int kind) {
        return (typeKinds & kind) != 0;
    }

    @Override
    public Object[] getChildren() {
        try {
            if (fBinding != null) {
                fBinding.getKey();
            }
        } catch (RuntimeException e) {
            ASTViewPlugin.log("Exception thrown in IBinding#getKey() for \"" + fBinding + "\"", e);
            return new Object[] { new Error(this, "BrokenBinding: " + fBinding, null) };
        }
        if (fBinding != null) {
            ArrayList<ASTAttribute> res = new ArrayList();
            //$NON-NLS-1$
            res.add(new BindingProperty(this, "NAME", fBinding.getName(), true));
            //$NON-NLS-1$
            res.add(new BindingProperty(this, "KEY", fBinding.getKey(), true));
            //$NON-NLS-1$
            res.add(new BindingProperty(this, "IS RECOVERED", fBinding.isRecovered(), true));
            switch(fBinding.getKind()) {
                case IBinding.VARIABLE:
                    IVariableBinding variableBinding = (IVariableBinding) fBinding;
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "IS FIELD", //$NON-NLS-1$
                    variableBinding.isField(), //$NON-NLS-1$
                    true));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "IS ENUM CONSTANT", //$NON-NLS-1$
                    variableBinding.isEnumConstant(), //$NON-NLS-1$
                    true));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "IS PARAMETER", //$NON-NLS-1$
                    variableBinding.isParameter(), //$NON-NLS-1$
                    true));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "VARIABLE ID", //$NON-NLS-1$
                    variableBinding.getVariableId(), //$NON-NLS-1$
                    true));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "MODIFIERS", //$NON-NLS-1$
                    getModifiersString(fBinding.getModifiers(), false), //$NON-NLS-1$
                    true));
                    res.add(new //$NON-NLS-1$
                    Binding(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "TYPE", //$NON-NLS-1$
                    variableBinding.getType(), //$NON-NLS-1$
                    true));
                    res.add(new //$NON-NLS-1$
                    Binding(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "DECLARING CLASS", //$NON-NLS-1$
                    variableBinding.getDeclaringClass(), //$NON-NLS-1$
                    true));
                    res.add(new //$NON-NLS-1$
                    Binding(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "DECLARING METHOD", //$NON-NLS-1$
                    variableBinding.getDeclaringMethod(), //$NON-NLS-1$
                    true));
                    res.add(new //$NON-NLS-1$
                    Binding(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "VARIABLE DECLARATION", //$NON-NLS-1$
                    variableBinding.getVariableDeclaration(), //$NON-NLS-1$
                    true));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "IS SYNTHETIC", //$NON-NLS-1$
                    fBinding.isSynthetic(), //$NON-NLS-1$
                    true));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "IS DEPRECATED", //$NON-NLS-1$
                    fBinding.isDeprecated(), //$NON-NLS-1$
                    true));
                    //$NON-NLS-1$ //$NON-NLS-2$
                    res.add(new BindingProperty(this, "CONSTANT VALUE", variableBinding.getConstantValue(), true));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "IS EFFECTIVELY FINAL", //$NON-NLS-1$
                    variableBinding.isEffectivelyFinal(), //$NON-NLS-1$
                    true));
                    break;
                case IBinding.PACKAGE:
                    IPackageBinding packageBinding = (IPackageBinding) fBinding;
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "IS UNNAMED", //$NON-NLS-1$
                    packageBinding.isUnnamed(), //$NON-NLS-1$
                    true));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "IS SYNTHETIC", //$NON-NLS-1$
                    fBinding.isSynthetic(), //$NON-NLS-1$
                    true));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "IS DEPRECATED", //$NON-NLS-1$
                    fBinding.isDeprecated(), //$NON-NLS-1$
                    true));
                    break;
                case IBinding.TYPE:
                    ITypeBinding typeBinding = (ITypeBinding) fBinding;
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "QUALIFIED NAME", //$NON-NLS-1$
                    typeBinding.getQualifiedName(), //$NON-NLS-1$
                    true));
                    int typeKind = getTypeKind(typeBinding);
                    boolean isRefType = isType(typeKind, REF_TYPE);
                    final boolean isNonPrimitive = !isType(typeKind, PRIMITIVE_TYPE);
                    StringBuffer kinds = new //$NON-NLS-1$
                    StringBuffer(//$NON-NLS-1$
                    "KIND:");
                    if (typeBinding.isArray())
                        //$NON-NLS-1$
                        kinds.append(//$NON-NLS-1$
                        " isArray");
                    if (typeBinding.isCapture())
                        //$NON-NLS-1$
                        kinds.append(//$NON-NLS-1$
                        " isCapture");
                    if (typeBinding.isNullType())
                        //$NON-NLS-1$
                        kinds.append(//$NON-NLS-1$
                        " isNullType");
                    if (typeBinding.isPrimitive())
                        //$NON-NLS-1$
                        kinds.append(//$NON-NLS-1$
                        " isPrimitive");
                    if (typeBinding.isTypeVariable())
                        kinds.append(" isTypeVariable");
                    if (typeBinding.isWildcardType())
                        kinds.append(" isWildcardType");
                    // ref types
                    if (typeBinding.isAnnotation())
                        //$NON-NLS-1$
                        kinds.append(//$NON-NLS-1$
                        " isAnnotation");
                    if (typeBinding.isClass())
                        //$NON-NLS-1$
                        kinds.append(//$NON-NLS-1$
                        " isClass");
                    if (typeBinding.isInterface())
                        //$NON-NLS-1$
                        kinds.append(//$NON-NLS-1$
                        " isInterface");
                    if (typeBinding.isEnum())
                        //$NON-NLS-1$
                        kinds.append(//$NON-NLS-1$
                        " isEnum");
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    kinds, //$NON-NLS-1$
                    true));
                    StringBuffer generics = new //$NON-NLS-1$
                    StringBuffer(//$NON-NLS-1$
                    "GENERICS:");
                    if (typeBinding.isRawType())
                        //$NON-NLS-1$
                        generics.append(//$NON-NLS-1$
                        " isRawType");
                    if (typeBinding.isGenericType())
                        //$NON-NLS-1$
                        generics.append(//$NON-NLS-1$
                        " isGenericType");
                    if (typeBinding.isParameterizedType())
                        generics.append(" isParameterizedType");
                    if (!isType(typeKind, GENERIC | PARAMETRIZED)) {
                        generics.append(" (non-generic, non-parameterized)");
                    }
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    generics, //$NON-NLS-1$
                    isRefType));
                    res.add(new Binding(this, "ELEMENT TYPE", typeBinding.getElementType(), isType(//$NON-NLS-1$
                    typeKind, //$NON-NLS-1$
                    ARRAY_TYPE)));
                    res.add(new Binding(this, "COMPONENT TYPE", typeBinding.getComponentType(), isType(//$NON-NLS-1$
                    typeKind, //$NON-NLS-1$
                    ARRAY_TYPE)));
                    res.add(new BindingProperty(this, "DIMENSIONS", typeBinding.getDimensions(), isType(//$NON-NLS-1$
                    typeKind, //$NON-NLS-1$
                    ARRAY_TYPE)));
                    final String createArrayTypeLabel = "CREATE ARRAY TYPE (+1)";
                    try {
                        ITypeBinding arrayType = typeBinding.createArrayType(1);
                        res.add(new Binding(this, createArrayTypeLabel, arrayType, true));
                    } catch (RuntimeException e) {
                        String msg = e.getClass().getName() + ": " + e.getLocalizedMessage();
                        boolean isRelevant = !typeBinding.getName().equals(PrimitiveType.VOID.toString()) && !typeBinding.isRecovered();
                        if (isRelevant) {
                            res.add(new Error(this, createArrayTypeLabel + ": " + msg, e));
                        } else {
                            res.add(new BindingProperty(this, createArrayTypeLabel, msg, false));
                        }
                    }
                    StringBuffer origin = new //$NON-NLS-1$
                    StringBuffer(//$NON-NLS-1$
                    "ORIGIN:");
                    if (typeBinding.isTopLevel())
                        //$NON-NLS-1$
                        origin.append(//$NON-NLS-1$
                        " isTopLevel");
                    if (typeBinding.isNested())
                        //$NON-NLS-1$
                        origin.append(//$NON-NLS-1$
                        " isNested");
                    if (typeBinding.isLocal())
                        //$NON-NLS-1$
                        origin.append(//$NON-NLS-1$
                        " isLocal");
                    if (typeBinding.isMember())
                        //$NON-NLS-1$
                        origin.append(//$NON-NLS-1$
                        " isMember");
                    if (typeBinding.isAnonymous())
                        //$NON-NLS-1$
                        origin.append(//$NON-NLS-1$
                        " isAnonymous");
                    res.add(new BindingProperty(this, origin, isRefType));
                    res.add(new BindingProperty(this, "IS FROM SOURCE", typeBinding.isFromSource(), isType(typeKind, //$NON-NLS-1$
                    REF_TYPE | VARIABLE_TYPE | //$NON-NLS-1$
                    CAPTURE_TYPE)));
                    res.add(new //$NON-NLS-1$
                    Binding(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "PACKAGE", //$NON-NLS-1$
                    typeBinding.getPackage(), //$NON-NLS-1$
                    isRefType));
                    res.add(new Binding(this, "DECLARING CLASS", typeBinding.getDeclaringClass(), isType(typeKind, //$NON-NLS-1$
                    REF_TYPE | VARIABLE_TYPE | //$NON-NLS-1$
                    CAPTURE_TYPE)));
                    res.add(new Binding(this, "DECLARING METHOD", typeBinding.getDeclaringMethod(), isType(typeKind, //$NON-NLS-1$
                    REF_TYPE | VARIABLE_TYPE | //$NON-NLS-1$
                    CAPTURE_TYPE)));
                    res.add(new Binding(this, "DECLARING MEMBER", typeBinding.getDeclaringMember(), //$NON-NLS-1$
                    typeBinding.isLocal()));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "MODIFIERS", //$NON-NLS-1$
                    getModifiersString(fBinding.getModifiers(), false), //$NON-NLS-1$
                    isRefType));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "BINARY NAME", //$NON-NLS-1$
                    typeBinding.getBinaryName(), //$NON-NLS-1$
                    true));
                    String isTypeDeclaration = typeBinding == typeBinding.getTypeDeclaration() ? " ( == this)" : " ( != this)";
                    res.add(new //$NON-NLS-1$
                    Binding(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "TYPE DECLARATION" + isTypeDeclaration, //$NON-NLS-1$
                    typeBinding.getTypeDeclaration(), //$NON-NLS-1$
                    true));
                    String isErasure = typeBinding == typeBinding.getErasure() ? " ( == this)" : " ( != this)";
                    res.add(new //$NON-NLS-1$
                    Binding(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "ERASURE" + isErasure, //$NON-NLS-1$
                    typeBinding.getErasure(), //$NON-NLS-1$
                    isNonPrimitive));
                    res.add(new BindingProperty(this, "TYPE PARAMETERS", typeBinding.getTypeParameters(), isType(//$NON-NLS-1$
                    typeKind, //$NON-NLS-1$
                    GENERIC)));
                    res.add(new BindingProperty(this, "TYPE ARGUMENTS", typeBinding.getTypeArguments(), isType(//$NON-NLS-1$
                    typeKind, //$NON-NLS-1$
                    PARAMETRIZED)));
                    res.add(new BindingProperty(this, "TYPE BOUNDS", typeBinding.getTypeBounds(), isType(typeKind, //$NON-NLS-1$
                    VARIABLE_TYPE | WILDCARD_TYPE | //$NON-NLS-1$
                    CAPTURE_TYPE)));
                    res.add(new Binding(this, "BOUND", typeBinding.getBound(), isType(//$NON-NLS-1$
                    typeKind, //$NON-NLS-1$
                    WILDCARD_TYPE)));
                    res.add(new BindingProperty(this, "IS UPPERBOUND", typeBinding.isUpperbound(), isType(//$NON-NLS-1$
                    typeKind, //$NON-NLS-1$
                    WILDCARD_TYPE)));
                    res.add(new Binding(this, "GENERIC TYPE OF WILDCARD TYPE", typeBinding.getGenericTypeOfWildcardType(), isType(//$NON-NLS-1$
                    typeKind, //$NON-NLS-1$
                    WILDCARD_TYPE)));
                    res.add(new BindingProperty(this, "RANK", typeBinding.getRank(), isType(//$NON-NLS-1$
                    typeKind, //$NON-NLS-1$
                    WILDCARD_TYPE)));
                    res.add(new Binding(this, "WILDCARD", typeBinding.getWildcard(), isType(//$NON-NLS-1$
                    typeKind, //$NON-NLS-1$
                    CAPTURE_TYPE)));
                    res.add(new //$NON-NLS-1$
                    Binding(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "SUPERCLASS", //$NON-NLS-1$
                    typeBinding.getSuperclass(), //$NON-NLS-1$
                    isRefType));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "INTERFACES", //$NON-NLS-1$
                    typeBinding.getInterfaces(), //$NON-NLS-1$
                    isRefType));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "DECLARED TYPES", //$NON-NLS-1$
                    typeBinding.getDeclaredTypes(), //$NON-NLS-1$
                    isRefType));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "DECLARED FIELDS", //$NON-NLS-1$
                    typeBinding.getDeclaredFields(), //$NON-NLS-1$
                    isRefType));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "DECLARED METHODS", //$NON-NLS-1$
                    typeBinding.getDeclaredMethods(), //$NON-NLS-1$
                    isRefType));
                    res.add(new Binding(this, "FUNCTIONAL INTERFACE METHOD", typeBinding.getFunctionalInterfaceMethod(), //$NON-NLS-1$
                    typeBinding.isInterface()));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "IS SYNTHETIC", //$NON-NLS-1$
                    fBinding.isSynthetic(), //$NON-NLS-1$
                    isNonPrimitive));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "IS DEPRECATED", //$NON-NLS-1$
                    fBinding.isDeprecated(), //$NON-NLS-1$
                    isRefType));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "TYPE ANNOTATIONS", //$NON-NLS-1$
                    typeBinding.getTypeAnnotations(), //$NON-NLS-1$
                    true));
                    break;
                case IBinding.METHOD:
                    IMethodBinding methodBinding = (IMethodBinding) fBinding;
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "IS CONSTRUCTOR", //$NON-NLS-1$
                    methodBinding.isConstructor(), //$NON-NLS-1$
                    true));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "IS DEFAULT CONSTRUCTOR", //$NON-NLS-1$
                    methodBinding.isDefaultConstructor(), //$NON-NLS-1$
                    true));
                    res.add(new //$NON-NLS-1$
                    Binding(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "DECLARING CLASS", //$NON-NLS-1$
                    methodBinding.getDeclaringClass(), //$NON-NLS-1$
                    true));
                    res.add(new Binding(this, "DECLARING MEMBER", methodBinding.getDeclaringMember(), //$NON-NLS-1$
                    methodBinding.getDeclaringMember() != //$NON-NLS-1$
                    null));
                    res.add(new //$NON-NLS-1$
                    Binding(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "RETURN TYPE", //$NON-NLS-1$
                    methodBinding.getReturnType(), //$NON-NLS-1$
                    true));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "MODIFIERS", //$NON-NLS-1$
                    getModifiersString(fBinding.getModifiers(), true), //$NON-NLS-1$
                    true));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "PARAMETER TYPES", //$NON-NLS-1$
                    methodBinding.getParameterTypes(), //$NON-NLS-1$
                    true));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "IS VARARGS", //$NON-NLS-1$
                    methodBinding.isVarargs(), //$NON-NLS-1$
                    true));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "EXCEPTION TYPES", //$NON-NLS-1$
                    methodBinding.getExceptionTypes(), //$NON-NLS-1$
                    true));
                    StringBuffer genericsM = new //$NON-NLS-1$
                    StringBuffer(//$NON-NLS-1$
                    "GENERICS:");
                    if (methodBinding.isRawMethod())
                        //$NON-NLS-1$
                        genericsM.append(//$NON-NLS-1$
                        " isRawMethod");
                    if (methodBinding.isGenericMethod())
                        genericsM.append(" isGenericMethod");
                    if (methodBinding.isParameterizedMethod())
                        genericsM.append(" isParameterizedMethod");
                    res.add(new BindingProperty(this, genericsM, true));
                    String isMethodDeclaration = methodBinding == methodBinding.getMethodDeclaration() ? " ( == this)" : " ( != this)";
                    res.add(new //$NON-NLS-1$
                    Binding(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "METHOD DECLARATION" + isMethodDeclaration, //$NON-NLS-1$
                    methodBinding.getMethodDeclaration(), //$NON-NLS-1$
                    true));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "TYPE PARAMETERS", //$NON-NLS-1$
                    methodBinding.getTypeParameters(), //$NON-NLS-1$
                    true));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "TYPE ARGUMENTS", //$NON-NLS-1$
                    methodBinding.getTypeArguments(), //$NON-NLS-1$
                    true));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "IS SYNTHETIC", //$NON-NLS-1$
                    fBinding.isSynthetic(), //$NON-NLS-1$
                    true));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "IS DEPRECATED", //$NON-NLS-1$
                    fBinding.isDeprecated(), //$NON-NLS-1$
                    true));
                    res.add(new //$NON-NLS-1$
                    BindingProperty(//$NON-NLS-1$
                    this, //$NON-NLS-1$
                    "IS ANNOTATION MEMBER", //$NON-NLS-1$
                    methodBinding.isAnnotationMember(), //$NON-NLS-1$
                    true));
                    res.add(Binding.createValueAttribute(this, "DEFAULT VALUE", methodBinding.getDefaultValue()));
                    int parameterCount = methodBinding.getParameterTypes().length;
                    BindingProperty[] parametersAnnotations = new BindingProperty[parameterCount];
                    for (int i = 0; i < parameterCount; i++) {
                        parametersAnnotations[i] = new BindingProperty(this, "Parameter " + String.valueOf(i), methodBinding.getParameterAnnotations(i), true);
                    }
                    res.add(new BindingProperty(this, "PARAMETER ANNOTATIONS", parametersAnnotations, true));
                    break;
                case IBinding.ANNOTATION:
                    IAnnotationBinding annotationBinding = (IAnnotationBinding) fBinding;
                    res.add(new Binding(this, "ANNOTATION TYPE", annotationBinding.getAnnotationType(), true));
                    res.add(new BindingProperty(this, "DECLARED MEMBER VALUE PAIRS", annotationBinding.getDeclaredMemberValuePairs(), true));
                    res.add(new BindingProperty(this, "ALL MEMBER VALUE PAIRS", annotationBinding.getAllMemberValuePairs(), true));
                    break;
                case IBinding.MEMBER_VALUE_PAIR:
                    IMemberValuePairBinding memberValuePairBinding = (IMemberValuePairBinding) fBinding;
                    res.add(new Binding(this, "METHOD BINDING", memberValuePairBinding.getMethodBinding(), true));
                    res.add(new BindingProperty(this, "IS DEFAULT", memberValuePairBinding.isDefault(), true));
                    res.add(Binding.createValueAttribute(this, "VALUE", memberValuePairBinding.getValue()));
                    break;
            }
            try {
                IAnnotationBinding[] annotations = fBinding.getAnnotations();
                //$NON-NLS-1$
                res.add(//$NON-NLS-1$
                new BindingProperty(this, "ANNOTATIONS", annotations, true));
            } catch (RuntimeException e) {
                String label = "Error in IBinding#getAnnotations() for \"" + fBinding.getKey() + "\"";
                res.add(new Error(this, label, e));
                ASTViewPlugin.log("Exception thrown in IBinding#getAnnotations() for \"" + fBinding.getKey() + "\"", e);
            }
            try {
                IJavaElement javaElement = fBinding.getJavaElement();
                res.add(new JavaElement(this, javaElement));
            } catch (RuntimeException e) {
                String label = ">java element: " + e.getClass().getName() + " for \"" + fBinding.getKey() + "\"";
                res.add(new Error(this, label, e));
                ASTViewPlugin.log("Exception thrown in IBinding#getJavaElement() for \"" + fBinding.getKey() + "\"", e);
            }
            return res.toArray();
        }
        return EMPTY;
    }

    private static final int ARRAY_TYPE = 1 << 0;

    private static final int NULL_TYPE = 1 << 1;

    private static final int VARIABLE_TYPE = 1 << 2;

    private static final int WILDCARD_TYPE = 1 << 3;

    private static final int CAPTURE_TYPE = 1 << 4;

    private static final int PRIMITIVE_TYPE = 1 << 5;

    private static final int REF_TYPE = 1 << 6;

    private static final int GENERIC = 1 << 8;

    private static final int PARAMETRIZED = 1 << 9;

    private int getTypeKind(ITypeBinding typeBinding) {
        if (typeBinding.isArray())
            return ARRAY_TYPE;
        if (typeBinding.isCapture())
            return CAPTURE_TYPE;
        if (typeBinding.isNullType())
            return NULL_TYPE;
        if (typeBinding.isPrimitive())
            return PRIMITIVE_TYPE;
        if (typeBinding.isTypeVariable())
            return VARIABLE_TYPE;
        if (typeBinding.isWildcardType())
            return WILDCARD_TYPE;
        if (typeBinding.isGenericType())
            return REF_TYPE | GENERIC;
        if (typeBinding.isParameterizedType() || typeBinding.isRawType())
            return REF_TYPE | PARAMETRIZED;
        return REF_TYPE;
    }

    @Override
    public String getLabel() {
        StringBuffer buf = new StringBuffer(fLabel);
        //$NON-NLS-1$
        buf.append(": ");
        if (fBinding != null) {
            switch(fBinding.getKind()) {
                case IBinding.VARIABLE:
                    IVariableBinding variableBinding = (IVariableBinding) fBinding;
                    if (!variableBinding.isField()) {
                        buf.append(variableBinding.getName());
                    } else {
                        if (variableBinding.getDeclaringClass() == null) {
                            //$NON-NLS-1$
                            buf.append(//$NON-NLS-1$
                            "<some array type>");
                        } else {
                            buf.append(variableBinding.getDeclaringClass().getName());
                        }
                        buf.append('.');
                        buf.append(variableBinding.getName());
                    }
                    break;
                case IBinding.PACKAGE:
                    IPackageBinding packageBinding = (IPackageBinding) fBinding;
                    buf.append(packageBinding.getName());
                    break;
                case IBinding.TYPE:
                    ITypeBinding typeBinding = (ITypeBinding) fBinding;
                    appendAnnotatedQualifiedName(buf, typeBinding);
                    break;
                case IBinding.METHOD:
                    IMethodBinding methodBinding = (IMethodBinding) fBinding;
                    buf.append(methodBinding.getDeclaringClass().getName());
                    buf.append('.');
                    buf.append(methodBinding.getName());
                    buf.append('(');
                    ITypeBinding[] parameters = methodBinding.getParameterTypes();
                    for (int i = 0; i < parameters.length; i++) {
                        if (i > 0) {
                            //$NON-NLS-1$
                            buf.append(", ");
                        }
                        ITypeBinding parameter = parameters[i];
                        buf.append(parameter.getName());
                    }
                    buf.append(')');
                    break;
                case IBinding.ANNOTATION:
                case IBinding.MEMBER_VALUE_PAIR:
                    buf.append(fBinding.toString());
                    break;
            }
        } else {
            //$NON-NLS-1$
            buf.append("null");
        }
        return buf.toString();
    }

    public static void appendAnnotatedQualifiedName(StringBuffer buf, ITypeBinding typeBinding) {
        // XXX: hack, but that's OK for a debugging tool...
        String debugString = typeBinding.toString();
        if (debugString.indexOf('\n') == -1 || typeBinding.getTypeAnnotations().length != 0) {
            // one-liner || outermost type has type annotations
            buf.append(debugString);
        } else {
            buf.append(typeBinding.getQualifiedName());
        }
    }

    @Override
    public Image getImage() {
        return null;
    }

    @Override
    public String toString() {
        return getLabel();
    }

    /*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || !obj.getClass().equals(getClass())) {
            return false;
        }
        Binding other = (Binding) obj;
        if (fParent == null) {
            if (other.fParent != null)
                return false;
        } else if (!fParent.equals(other.fParent)) {
            return false;
        }
        if (fBinding == null) {
            if (other.fBinding != null)
                return false;
        } else if (!fBinding.equals(other.fBinding)) {
            return false;
        }
        if (fLabel == null) {
            if (other.fLabel != null)
                return false;
        } else if (!fLabel.equals(other.fLabel)) {
            return false;
        }
        return true;
    }

    /*
	 * @see java.lang.Object#hashCode()
	 */
    @Override
    public int hashCode() {
        int result = fParent != null ? fParent.hashCode() : 0;
        result += (fBinding != null && fBinding.getKey() != null) ? fBinding.getKey().hashCode() : 0;
        result += fLabel != null ? fLabel.hashCode() : 0;
        return result;
    }

    public static String getBindingLabel(IBinding binding) {
        String label;
        if (binding == null) {
            //$NON-NLS-1$
            label = ">binding";
        } else {
            switch(binding.getKind()) {
                case IBinding.VARIABLE:
                    label = "> variable binding";
                    break;
                case IBinding.TYPE:
                    //$NON-NLS-1$
                    label = //$NON-NLS-1$
                    "> type binding";
                    break;
                case IBinding.METHOD:
                    label = "> method binding";
                    break;
                case IBinding.PACKAGE:
                    label = "> package binding";
                    break;
                case IBinding.ANNOTATION:
                    label = "> annotation binding";
                    break;
                case IBinding.MEMBER_VALUE_PAIR:
                    label = "> member value pair binding";
                    break;
                default:
                    label = "> unknown binding";
            }
        }
        return label;
    }

    /**
	 * Creates an {@link ASTAttribute} for a value from
	 * {@link IMemberValuePairBinding#getValue()} or from
	 * {@link IMethodBinding#getDefaultValue()}.
	 * 
	 * @param parent the parent node
	 * @param name the attribute name
	 * @param value the attribute value
	 * @return an ASTAttribute
	 */
    public static ASTAttribute createValueAttribute(ASTAttribute parent, String name, Object value) {
        ASTAttribute res;
        if (value instanceof IBinding) {
            IBinding binding = (IBinding) value;
            res = new Binding(parent, name + ": " + getBindingLabel(binding), binding, true);
        } else if (value instanceof String) {
            res = new GeneralAttribute(parent, name, getEscapedStringLiteral((String) value));
        } else if (value instanceof Object[]) {
            res = new GeneralAttribute(parent, name, (Object[]) value);
        } else if (value instanceof ASTAttribute) {
            res = (ASTAttribute) value;
        } else {
            res = new GeneralAttribute(parent, name, value);
        }
        return res;
    }

    public static String getEscapedStringLiteral(String stringValue) {
        StringLiteral stringLiteral = AST.newAST(ASTView.JLS_LATEST).newStringLiteral();
        stringLiteral.setLiteralValue(stringValue);
        return stringLiteral.getEscapedValue();
    }

    public static String getEscapedCharLiteral(char charValue) {
        CharacterLiteral charLiteral = AST.newAST(ASTView.JLS_LATEST).newCharacterLiteral();
        charLiteral.setCharValue(charValue);
        return charLiteral.getEscapedValue();
    }

    private static StringBuffer getModifiersString(int flags, boolean isMethod) {
        StringBuffer sb = new StringBuffer().append("0x").append(Integer.toHexString(flags)).append(" (");
        int prologLen = sb.length();
        int rest = flags;
        rest &= ~appendFlag(sb, flags, Modifier.PUBLIC, "public ");
        rest &= ~appendFlag(sb, flags, Modifier.PRIVATE, "private ");
        rest &= ~appendFlag(sb, flags, Modifier.PROTECTED, "protected ");
        rest &= ~appendFlag(sb, flags, Modifier.STATIC, "static ");
        rest &= ~appendFlag(sb, flags, Modifier.FINAL, "final ");
        if (isMethod) {
            rest &= ~appendFlag(sb, flags, Modifier.SYNCHRONIZED, "synchronized ");
            rest &= ~appendFlag(sb, flags, Modifier.DEFAULT, "default ");
        } else {
            rest &= ~appendFlag(sb, flags, Modifier.VOLATILE, "volatile ");
            rest &= ~appendFlag(sb, flags, Modifier.TRANSIENT, "transient ");
        }
        rest &= ~appendFlag(sb, flags, Modifier.NATIVE, "native ");
        rest &= ~appendFlag(sb, flags, Modifier.ABSTRACT, "abstract ");
        rest &= ~appendFlag(sb, flags, Modifier.STRICTFP, "strictfp ");
        if (rest != 0)
            sb.append("unknown:0x").append(Integer.toHexString(rest)).append(" ");
        int len = sb.length();
        if (len != prologLen)
            sb.setLength(len - 1);
        sb.append(")");
        return sb;
    }

    private static int appendFlag(StringBuffer sb, int flags, int flag, String name) {
        if ((flags & flag) != 0) {
            sb.append(name);
            return flag;
        } else {
            return 0;
        }
    }
}
