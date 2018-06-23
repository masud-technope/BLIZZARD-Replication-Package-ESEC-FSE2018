/*******************************************************************************
 * Copyright (c) 2005, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann - Contributions for
 *								bug 349326 - [1.7] new warning for missing try-with-resources
 *								bug 392099 - [1.8][compiler][null] Apply null annotation on types for null analysis
 *								bug 395002 - Self bound generic class doesn't resolve bounds properly for wildcards for certain parametrisation.
 *								bug 392384 - [1.8][compiler][null] Restore nullness info from type annotations in class files
 *								Bug 392099 - [1.8][compiler][null] Apply null annotation on types for null analysis
 *								Bug 415291 - [1.8][null] differentiate type incompatibilities due to null annotations
 *								Bug 415043 - [1.8][null] Follow-up re null type annotations after bug 392099
 *								Bug 412076 - [compiler] @NonNullByDefault doesn't work for varargs parameter when in generic interface
 *								Bug 403216 - [1.8][null] TypeReference#captureTypeAnnotations treats type annotations as type argument annotations
 *								Bug 415850 - [1.8] Ensure RunJDTCoreTests can cope with null annotations enabled
 *								Bug 415043 - [1.8][null] Follow-up re null type annotations after bug 392099
 *								Bug 416175 - [1.8][compiler][null] NPE with a code snippet that used null annotations on wildcards
 *								Bug 416174 - [1.8][compiler][null] Bogus name clash error with null annotations
 *								Bug 416176 - [1.8][compiler][null] null type annotations cause grief on type variables
 *								Bug 400874 - [1.8][compiler] Inference infrastructure should evolve to meet JLS8 18.x (Part G of JSR335 spec)
 *								Bug 423504 - [1.8] Implement "18.5.3 Functional Interface Parameterization Inference"
 *								Bug 425278 - [1.8][compiler] Suspect error: The target type of this expression is not a well formed parameterized type due to bound(s) mismatch
 *								Bug 425798 - [1.8][compiler] Another NPE in ConstraintTypeFormula.reduceSubType
 *								Bug 425156 - [1.8] Lambda as an argument is flagged with incompatible error
 *								Bug 426563 - [1.8] AIOOBE when method with error invoked with lambda expression as argument
 *								Bug 426792 - [1.8][inference][impl] generify new type inference engine
 *								Bug 428294 - [1.8][compiler] Type mismatch: cannot convert from List<Object> to Collection<Object[]>
 *								Bug 427199 - [1.8][resource] avoid resource leak warnings on Streams that have no resource
 *								Bug 416182 - [1.8][compiler][null] Contradictory null annotations not rejected
 *								Bug 438458 - [1.8][null] clean up handling of null type annotations wrt type variables
 *								Bug 438179 - [1.8][null] 'Contradictory null annotations' error on type variable with explicit null-annotation.
 *								Bug 441693 - [1.8][null] Bogus warning for type argument annotated with @NonNull
 *								Bug 446434 - [1.8][null] Enable interned captures also when analysing null type annotations
 *								Bug 435805 - [1.8][compiler][null] Java 8 compiler does not recognize declaration style null annotations
 *								Bug 456508 - Unexpected RHS PolyTypeBinding for: <code-snippet>
 *								Bug 390064 - [compiler][resource] Resource leak warning missing when extending parameterized class
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants.BoundCheckStatus;

/**
 * A parameterized type encapsulates a type with type arguments,
 */
public class ParameterizedTypeBinding extends ReferenceBinding implements Substitution {

    // must ensure the type is resolved
    protected ReferenceBinding type;

    public TypeBinding[] arguments;

    public LookupEnvironment environment;

    public char[] genericTypeSignature;

    public ReferenceBinding superclass;

    public ReferenceBinding[] superInterfaces;

    public FieldBinding[] fields;

    public ReferenceBinding[] memberTypes;

    public MethodBinding[] methods;

    protected ReferenceBinding enclosingType;

    public  ParameterizedTypeBinding(ReferenceBinding type, TypeBinding[] arguments, ReferenceBinding enclosingType, LookupEnvironment environment) {
        this.environment = environment;
        // never unresolved, never lazy per construction
        this.enclosingType = enclosingType;
        if (type.isStatic() && arguments == null && !(this instanceof RawTypeBinding))
            throw new IllegalStateException();
        initialize(type, arguments);
        if (type instanceof UnresolvedReferenceBinding)
            ((UnresolvedReferenceBinding) type).addWrapper(this, environment);
        if (arguments != null) {
            for (int i = 0, l = arguments.length; i < l; i++) {
                if (arguments[i] instanceof UnresolvedReferenceBinding)
                    ((UnresolvedReferenceBinding) arguments[i]).addWrapper(this, environment);
                if (arguments[i].hasNullTypeAnnotations())
                    this.tagBits |= TagBits.HasNullTypeAnnotation;
            }
        }
        if (enclosingType != null && enclosingType.hasNullTypeAnnotations())
            this.tagBits |= TagBits.HasNullTypeAnnotation;
        // cleared in resolve()
        this.tagBits |= TagBits.HasUnresolvedTypeVariables;
        this.typeBits = type.typeBits;
    }

    /**
	 * May return an UnresolvedReferenceBinding.
	 * @see ParameterizedTypeBinding#genericType()
	 */
    public ReferenceBinding actualType() {
        return this.type;
    }

    public boolean isParameterizedType() {
        return true;
    }

    /**
	 * Iterate type arguments, and validate them according to corresponding variable bounds.
	 */
    public void boundCheck(Scope scope, TypeReference[] argumentReferences) {
        if ((this.tagBits & TagBits.PassedBoundCheck) == 0) {
            boolean hasErrors = false;
            TypeVariableBinding[] typeVariables = this.type.typeVariables();
            if (// arguments may be null in error cases
            this.arguments != null && typeVariables != null) {
                for (int i = 0, length = typeVariables.length; i < length; i++) {
                    BoundCheckStatus checkStatus = typeVariables[i].boundCheck(this, this.arguments[i], scope, argumentReferences[i]);
                    hasErrors |= checkStatus != BoundCheckStatus.OK;
                    if (!checkStatus.isOKbyJLS() && (this.arguments[i].tagBits & TagBits.HasMissingType) == 0) {
                        // do not report secondary error, if type reference already got complained against
                        scope.problemReporter().typeMismatchError(this.arguments[i], typeVariables[i], this.type, argumentReferences[i]);
                    }
                }
            }
            // no need to recheck it in the future
            if (!hasErrors)
                this.tagBits |= TagBits.PassedBoundCheck;
        }
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#canBeInstantiated()
	 */
    public boolean canBeInstantiated() {
        // cannot instantiate param type with wildcard arguments
        return ((this.tagBits & TagBits.HasDirectWildcard) == 0) && super.canBeInstantiated();
    }

    /**
	 * Perform capture conversion for a parameterized type with wildcard arguments
	 * @see org.eclipse.jdt.internal.compiler.lookup.TypeBinding#capture(Scope,int, int)
	 */
    public ParameterizedTypeBinding capture(Scope scope, int start, int end) {
        if ((this.tagBits & TagBits.HasDirectWildcard) == 0)
            return this;
        TypeBinding[] originalArguments = this.arguments;
        int length = originalArguments.length;
        TypeBinding[] capturedArguments = new TypeBinding[length];
        // Retrieve the type context for capture bindingKey
        ReferenceBinding contextType = scope.enclosingSourceType();
        // maybe null when used programmatically by DOM
        if (contextType != null)
            contextType = contextType.outermostEnclosingType();
        CompilationUnitScope compilationUnitScope = scope.compilationUnitScope();
        ASTNode cud = compilationUnitScope.referenceContext;
        long sourceLevel = this.environment.globalOptions.sourceLevel;
        final boolean needUniqueCapture = sourceLevel >= ClassFileConstants.JDK1_8;
        for (int i = 0; i < length; i++) {
            TypeBinding argument = originalArguments[i];
            if (// no capture for intersection types
            argument.kind() == Binding.WILDCARD_TYPE) {
                final WildcardBinding wildcard = (WildcardBinding) argument;
                if (wildcard.boundKind == Wildcard.SUPER && wildcard.bound.id == TypeIds.T_JavaLangObject)
                    capturedArguments[i] = wildcard.bound;
                else if (needUniqueCapture)
                    capturedArguments[i] = this.environment.createCapturedWildcard(wildcard, contextType, start, end, cud, compilationUnitScope.nextCaptureID());
                else
                    capturedArguments[i] = new CaptureBinding(wildcard, contextType, start, end, cud, compilationUnitScope.nextCaptureID());
            } else {
                capturedArguments[i] = argument;
            }
        }
        ParameterizedTypeBinding capturedParameterizedType = this.environment.createParameterizedType(this.type, capturedArguments, enclosingType(), this.typeAnnotations);
        for (int i = 0; i < length; i++) {
            TypeBinding argument = capturedArguments[i];
            if (argument.isCapture()) {
                ((CaptureBinding) argument).initializeBounds(scope, capturedParameterizedType);
            }
        }
        return capturedParameterizedType;
    }

    /**
	 * Perform capture deconversion for a parameterized type with captured wildcard arguments
	 * @see org.eclipse.jdt.internal.compiler.lookup.TypeBinding#uncapture(Scope)
	 */
    public TypeBinding uncapture(Scope scope) {
        if ((this.tagBits & TagBits.HasCapturedWildcard) == 0)
            return this;
        int length = this.arguments == null ? 0 : this.arguments.length;
        TypeBinding[] freeTypes = new TypeBinding[length];
        for (int i = 0; i < length; i++) {
            freeTypes[i] = this.arguments[i].uncapture(scope);
        }
        return scope.environment().createParameterizedType(this.type, freeTypes, (ReferenceBinding) (this.enclosingType != null ? this.enclosingType.uncapture(scope) : null), this.typeAnnotations);
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.TypeBinding#collectMissingTypes(java.util.List)
	 */
    public List<TypeBinding> collectMissingTypes(List<TypeBinding> missingTypes) {
        if ((this.tagBits & TagBits.HasMissingType) != 0) {
            if (this.enclosingType != null) {
                missingTypes = this.enclosingType.collectMissingTypes(missingTypes);
            }
            missingTypes = genericType().collectMissingTypes(missingTypes);
            if (this.arguments != null) {
                for (int i = 0, max = this.arguments.length; i < max; i++) {
                    missingTypes = this.arguments[i].collectMissingTypes(missingTypes);
                }
            }
        }
        return missingTypes;
    }

    /**
	 * Collect the substitutes into a map for certain type variables inside the receiver type
	 * e.g.   Collection<T>.collectSubstitutes(Collection<List<X>>, Map), will populate Map with: T --> List<X>
	 * Constraints:
	 *   A << F   corresponds to:   F.collectSubstitutes(..., A, ..., CONSTRAINT_EXTENDS (1))
	 *   A = F   corresponds to:      F.collectSubstitutes(..., A, ..., CONSTRAINT_EQUAL (0))
	 *   A >> F   corresponds to:   F.collectSubstitutes(..., A, ..., CONSTRAINT_SUPER (2))
	 */
    public void collectSubstitutes(Scope scope, TypeBinding actualType, InferenceContext inferenceContext, int constraint) {
        if ((this.tagBits & TagBits.HasTypeVariable) == 0) {
            TypeBinding actualEquivalent = actualType.findSuperTypeOriginatingFrom(this.type);
            if (actualEquivalent != null && actualEquivalent.isRawType()) {
                inferenceContext.isUnchecked = true;
            }
            return;
        }
        if (actualType == TypeBinding.NULL || actualType.kind() == POLY_TYPE)
            return;
        if (!(actualType instanceof ReferenceBinding))
            return;
        TypeBinding formalEquivalent, actualEquivalent;
        switch(constraint) {
            case TypeConstants.CONSTRAINT_EQUAL:
            case TypeConstants.CONSTRAINT_EXTENDS:
                formalEquivalent = this;
                actualEquivalent = actualType.findSuperTypeOriginatingFrom(this.type);
                if (actualEquivalent == null)
                    return;
                break;
            case TypeConstants.CONSTRAINT_SUPER:
            default:
                formalEquivalent = this.findSuperTypeOriginatingFrom(actualType);
                if (formalEquivalent == null)
                    return;
                actualEquivalent = actualType;
                break;
        }
        // collect through enclosing type
        ReferenceBinding formalEnclosingType = formalEquivalent.enclosingType();
        if (formalEnclosingType != null) {
            formalEnclosingType.collectSubstitutes(scope, actualEquivalent.enclosingType(), inferenceContext, constraint);
        }
        // collect through type arguments
        if (this.arguments == null)
            return;
        TypeBinding[] formalArguments;
        switch(formalEquivalent.kind()) {
            case Binding.GENERIC_TYPE:
                formalArguments = formalEquivalent.typeVariables();
                break;
            case Binding.PARAMETERIZED_TYPE:
                formalArguments = ((ParameterizedTypeBinding) formalEquivalent).arguments;
                break;
            case Binding.RAW_TYPE:
                if (inferenceContext.depth > 0) {
                    // marker for impossible inference
                    inferenceContext.status = InferenceContext.FAILED;
                }
                return;
            default:
                return;
        }
        TypeBinding[] actualArguments;
        switch(actualEquivalent.kind()) {
            case Binding.GENERIC_TYPE:
                actualArguments = actualEquivalent.typeVariables();
                break;
            case Binding.PARAMETERIZED_TYPE:
                actualArguments = ((ParameterizedTypeBinding) actualEquivalent).arguments;
                break;
            case Binding.RAW_TYPE:
                if (inferenceContext.depth > 0) {
                    // marker for impossible inference
                    inferenceContext.status = InferenceContext.FAILED;
                } else {
                    inferenceContext.isUnchecked = true;
                }
                return;
            default:
                return;
        }
        inferenceContext.depth++;
        for (int i = 0, length = formalArguments.length; i < length; i++) {
            TypeBinding formalArgument = formalArguments[i];
            TypeBinding actualArgument = actualArguments[i];
            if (formalArgument.isWildcard()) {
                formalArgument.collectSubstitutes(scope, actualArgument, inferenceContext, constraint);
                continue;
            } else if (actualArgument.isWildcard()) {
                WildcardBinding actualWildcardArgument = (WildcardBinding) actualArgument;
                if (actualWildcardArgument.otherBounds == null) {
                    if (// JLS 15.12.7, p.459
                    constraint == TypeConstants.CONSTRAINT_SUPER) {
                        switch(actualWildcardArgument.boundKind) {
                            case Wildcard.EXTENDS:
                                formalArgument.collectSubstitutes(scope, actualWildcardArgument.bound, inferenceContext, TypeConstants.CONSTRAINT_SUPER);
                                continue;
                            case Wildcard.SUPER:
                                formalArgument.collectSubstitutes(scope, actualWildcardArgument.bound, inferenceContext, TypeConstants.CONSTRAINT_EXTENDS);
                                continue;
                            default:
                                // cannot infer anything further from unbound wildcard
                                continue;
                        }
                    } else {
                        // cannot infer anything further from wildcard
                        continue;
                    }
                }
            }
            // by default, use EQUAL constraint
            formalArgument.collectSubstitutes(scope, actualArgument, inferenceContext, TypeConstants.CONSTRAINT_EQUAL);
        }
        inferenceContext.depth--;
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#computeId()
	 */
    public void computeId() {
        this.id = TypeIds.NoId;
    }

    public char[] computeUniqueKey(boolean isLeaf) {
        StringBuffer sig = new StringBuffer(10);
        ReferenceBinding enclosing;
        if (isMemberType() && ((enclosing = enclosingType()).isParameterizedType() || enclosing.isRawType())) {
            char[] typeSig = enclosing.computeUniqueKey(/*not a leaf*/
            false);
            // copy all but trailing semicolon
            sig.append(typeSig, 0, typeSig.length - 1);
            sig.append('.').append(sourceName());
        } else if (this.type.isLocalType()) {
            LocalTypeBinding localTypeBinding = (LocalTypeBinding) this.type;
            enclosing = localTypeBinding.enclosingType();
            ReferenceBinding temp;
            while ((temp = enclosing.enclosingType()) != null) enclosing = temp;
            char[] typeSig = enclosing.computeUniqueKey(/*not a leaf*/
            false);
            // copy all but trailing semicolon
            sig.append(typeSig, 0, typeSig.length - 1);
            sig.append('$');
            sig.append(localTypeBinding.sourceStart);
        } else {
            char[] typeSig = this.type.computeUniqueKey(/*not a leaf*/
            false);
            // copy all but trailing semicolon
            sig.append(typeSig, 0, typeSig.length - 1);
        }
        ReferenceBinding captureSourceType = null;
        if (this.arguments != null) {
            sig.append('<');
            for (int i = 0, length = this.arguments.length; i < length; i++) {
                TypeBinding typeBinding = this.arguments[i];
                sig.append(typeBinding.computeUniqueKey(/*not a leaf*/
                false));
                if (typeBinding instanceof CaptureBinding)
                    captureSourceType = ((CaptureBinding) typeBinding).sourceType;
            }
            sig.append('>');
        }
        sig.append(';');
        if (captureSourceType != null && TypeBinding.notEquals(captureSourceType, this.type)) {
            // contains a capture binding
            //$NON-NLS-1$
            sig.insert(0, "&");
            sig.insert(0, captureSourceType.computeUniqueKey(/*not a leaf*/
            false));
        }
        int sigLength = sig.length();
        char[] uniqueKey = new char[sigLength];
        sig.getChars(0, sigLength, uniqueKey, 0);
        return uniqueKey;
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.TypeBinding#constantPoolName()
	 */
    public char[] constantPoolName() {
        // erasure
        return this.type.constantPoolName();
    }

    public TypeBinding clone(TypeBinding outerType) {
        return new ParameterizedTypeBinding(this.type, this.arguments, (ReferenceBinding) outerType, this.environment);
    }

    public ParameterizedMethodBinding createParameterizedMethod(MethodBinding originalMethod) {
        return new ParameterizedMethodBinding(this, originalMethod);
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.TypeBinding#debugName()
	 */
    public String debugName() {
        if (this.hasTypeAnnotations())
            return annotatedDebugName();
        StringBuffer nameBuffer = new StringBuffer(10);
        if (this.type instanceof UnresolvedReferenceBinding) {
            nameBuffer.append(this.type);
        } else {
            nameBuffer.append(this.type.sourceName());
        }
        if (this.arguments != null && this.arguments.length > 0) {
            nameBuffer.append('<');
            for (int i = 0, length = this.arguments.length; i < length; i++) {
                if (i > 0)
                    nameBuffer.append(',');
                nameBuffer.append(this.arguments[i].debugName());
            }
            nameBuffer.append('>');
        }
        return nameBuffer.toString();
    }

    public String annotatedDebugName() {
        StringBuffer nameBuffer = new StringBuffer(super.annotatedDebugName());
        if (this.arguments != null && this.arguments.length > 0) {
            nameBuffer.append('<');
            for (int i = 0, length = this.arguments.length; i < length; i++) {
                if (i > 0)
                    nameBuffer.append(',');
                nameBuffer.append(this.arguments[i].annotatedDebugName());
            }
            nameBuffer.append('>');
        }
        return nameBuffer.toString();
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#enclosingType()
	 */
    public ReferenceBinding enclosingType() {
        return this.enclosingType;
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.Substitution#environment()
	 */
    public LookupEnvironment environment() {
        return this.environment;
    }

    /**
     * @see org.eclipse.jdt.internal.compiler.lookup.TypeBinding#erasure()
     */
    public TypeBinding erasure() {
        // erasure
        return this.type.erasure();
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#fieldCount()
	 */
    public int fieldCount() {
        // same as erasure (lazy)
        return this.type.fieldCount();
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#fields()
	 */
    public FieldBinding[] fields() {
        if ((this.tagBits & TagBits.AreFieldsComplete) != 0)
            return this.fields;
        try {
            FieldBinding[] originalFields = this.type.fields();
            int length = originalFields.length;
            FieldBinding[] parameterizedFields = new FieldBinding[length];
            for (int i = 0; i < length; i++) // substitute all fields, so as to get updated declaring class at least
            parameterizedFields[i] = new ParameterizedFieldBinding(this, originalFields[i]);
            this.fields = parameterizedFields;
        } finally {
            // if the original fields cannot be retrieved (ex. AbortCompilation), then assume we do not have any fields
            if (this.fields == null)
                this.fields = Binding.NO_FIELDS;
            this.tagBits |= TagBits.AreFieldsComplete;
        }
        return this.fields;
    }

    /**
	 * Return the original generic type from which the parameterized type got instantiated from.
	 * This will perform lazy resolution automatically if needed.
	 * @see ParameterizedTypeBinding#actualType() if no resolution is required (unlikely)
	 */
    public ReferenceBinding genericType() {
        if (this.type instanceof UnresolvedReferenceBinding)
            ((UnresolvedReferenceBinding) this.type).resolve(this.environment, false);
        return this.type;
    }

    /**
	 * Ltype<param1 ... paramN>;
	 * LY<TT;>;
	 */
    public char[] genericTypeSignature() {
        if (this.genericTypeSignature == null) {
            if ((this.modifiers & ExtraCompilerModifiers.AccGenericSignature) == 0) {
                this.genericTypeSignature = this.type.signature();
            } else {
                StringBuffer sig = new StringBuffer(10);
                if (isMemberType() && !isStatic()) {
                    ReferenceBinding enclosing = enclosingType();
                    char[] typeSig = enclosing.genericTypeSignature();
                    // copy all but trailing semicolon
                    sig.append(typeSig, 0, typeSig.length - 1);
                    if ((enclosing.modifiers & ExtraCompilerModifiers.AccGenericSignature) != 0) {
                        sig.append('.');
                    } else {
                        sig.append('$');
                    }
                    sig.append(sourceName());
                } else {
                    char[] typeSig = this.type.signature();
                    // copy all but trailing semicolon
                    sig.append(typeSig, 0, typeSig.length - 1);
                }
                if (this.arguments != null) {
                    sig.append('<');
                    for (int i = 0, length = this.arguments.length; i < length; i++) {
                        sig.append(this.arguments[i].genericTypeSignature());
                    }
                    sig.append('>');
                }
                sig.append(';');
                int sigLength = sig.length();
                this.genericTypeSignature = new char[sigLength];
                sig.getChars(0, sigLength, this.genericTypeSignature, 0);
            }
        }
        return this.genericTypeSignature;
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#getAnnotationTagBits()
	 */
    public long getAnnotationTagBits() {
        return this.type.getAnnotationTagBits();
    }

    public int getEnclosingInstancesSlotSize() {
        return genericType().getEnclosingInstancesSlotSize();
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#getExactConstructor(TypeBinding[])
	 */
    public MethodBinding getExactConstructor(TypeBinding[] argumentTypes) {
        int argCount = argumentTypes.length;
        MethodBinding match = null;
        if (// have resolved all arg types & return type of the methods
        (this.tagBits & TagBits.AreMethodsComplete) != 0) {
            long range;
            if ((range = ReferenceBinding.binarySearch(TypeConstants.INIT, this.methods)) >= 0) {
                nextMethod: for (int imethod = (int) range, end = (int) (range >> 32); imethod <= end; imethod++) {
                    MethodBinding method = this.methods[imethod];
                    if (method.parameters.length == argCount) {
                        TypeBinding[] toMatch = method.parameters;
                        for (int iarg = 0; iarg < argCount; iarg++) if (TypeBinding.notEquals(toMatch[iarg], argumentTypes[iarg]))
                            continue nextMethod;
                        if (// collision case
                        match != null)
                            // collision case
                            return null;
                        match = method;
                    }
                }
            }
        } else {
            // takes care of duplicates & default abstract methods
            MethodBinding[] matchingMethods = getMethods(TypeConstants.INIT);
            nextMethod: for (int m = matchingMethods.length; --m >= 0; ) {
                MethodBinding method = matchingMethods[m];
                TypeBinding[] toMatch = method.parameters;
                if (toMatch.length == argCount) {
                    for (int p = 0; p < argCount; p++) if (TypeBinding.notEquals(toMatch[p], argumentTypes[p]))
                        continue nextMethod;
                    if (// collision case
                    match != null)
                        // collision case
                        return null;
                    match = method;
                }
            }
        }
        return match;
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#getExactMethod(char[], TypeBinding[],CompilationUnitScope)
	 */
    public MethodBinding getExactMethod(char[] selector, TypeBinding[] argumentTypes, CompilationUnitScope refScope) {
        // sender from refScope calls recordTypeReference(this)
        int argCount = argumentTypes.length;
        boolean foundNothing = true;
        MethodBinding match = null;
        if (// have resolved all arg types & return type of the methods
        (this.tagBits & TagBits.AreMethodsComplete) != 0) {
            long range;
            if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0) {
                nextMethod: for (int imethod = (int) range, end = (int) (range >> 32); imethod <= end; imethod++) {
                    MethodBinding method = this.methods[imethod];
                    // inner type lookups must know that a method with this name exists
                    foundNothing = false;
                    if (method.parameters.length == argCount) {
                        TypeBinding[] toMatch = method.parameters;
                        for (int iarg = 0; iarg < argCount; iarg++) if (TypeBinding.notEquals(toMatch[iarg], argumentTypes[iarg]))
                            continue nextMethod;
                        if (// collision case
                        match != null)
                            // collision case
                            return null;
                        match = method;
                    }
                }
            }
        } else {
            // takes care of duplicates & default abstract methods
            MethodBinding[] matchingMethods = getMethods(selector);
            foundNothing = matchingMethods == Binding.NO_METHODS;
            nextMethod: for (int m = matchingMethods.length; --m >= 0; ) {
                MethodBinding method = matchingMethods[m];
                TypeBinding[] toMatch = method.parameters;
                if (toMatch.length == argCount) {
                    for (int p = 0; p < argCount; p++) if (TypeBinding.notEquals(toMatch[p], argumentTypes[p]))
                        continue nextMethod;
                    if (// collision case
                    match != null)
                        // collision case
                        return null;
                    match = method;
                }
            }
        }
        if (match != null) {
            // class B<TT> extends A<Integer> { public <ZZ> void id(Integer i) {} }
            if (match.hasSubstitutedParameters())
                return null;
            return match;
        }
        if (foundNothing && (this.arguments == null || this.arguments.length <= 1)) {
            if (isInterface()) {
                if (superInterfaces().length == 1) {
                    if (refScope != null)
                        refScope.recordTypeReference(this.superInterfaces[0]);
                    return this.superInterfaces[0].getExactMethod(selector, argumentTypes, refScope);
                }
            } else if (superclass() != null) {
                if (refScope != null)
                    refScope.recordTypeReference(this.superclass);
                return this.superclass.getExactMethod(selector, argumentTypes, refScope);
            }
        }
        return null;
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#getField(char[], boolean)
	 */
    public FieldBinding getField(char[] fieldName, boolean needResolve) {
        // ensure fields have been initialized... must create all at once unlike methods
        fields();
        return ReferenceBinding.binarySearch(fieldName, this.fields);
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#getMemberType(char[])
	 */
    public ReferenceBinding getMemberType(char[] typeName) {
        // ensure memberTypes have been initialized... must create all at once unlike methods
        memberTypes();
        int typeLength = typeName.length;
        for (int i = this.memberTypes.length; --i >= 0; ) {
            ReferenceBinding memberType = this.memberTypes[i];
            if (memberType.sourceName.length == typeLength && CharOperation.equals(memberType.sourceName, typeName))
                return memberType;
        }
        return null;
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#getMethods(char[])
	 */
    public MethodBinding[] getMethods(char[] selector) {
        if (this.methods != null) {
            long range;
            if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0) {
                int start = (int) range;
                int length = (int) (range >> 32) - start + 1;
                // cannot optimize since some clients rely on clone array
                // if (start == 0 && length == this.methods.length)
                //	return this.methods; // current set is already interesting subset
                MethodBinding[] result;
                System.arraycopy(this.methods, start, result = new MethodBinding[length], 0, length);
                return result;
            }
        }
        if ((this.tagBits & TagBits.AreMethodsComplete) != 0)
            // have created all the methods and there are no matches
            return Binding.NO_METHODS;
        MethodBinding[] parameterizedMethods = null;
        try {
            MethodBinding[] originalMethods = this.type.getMethods(selector);
            int length = originalMethods.length;
            if (length == 0)
                return Binding.NO_METHODS;
            parameterizedMethods = new MethodBinding[length];
            boolean useNullTypeAnnotations = this.environment.usesNullTypeAnnotations();
            for (int i = 0; i < length; i++) {
                // substitute methods, so as to get updated declaring class at least
                parameterizedMethods[i] = createParameterizedMethod(originalMethods[i]);
                if (useNullTypeAnnotations)
                    parameterizedMethods[i] = NullAnnotationMatching.checkForContradictions(parameterizedMethods[i], null, null);
            }
            if (this.methods == null) {
                MethodBinding[] temp = new MethodBinding[length];
                System.arraycopy(parameterizedMethods, 0, temp, 0, length);
                // must be a copy of parameterizedMethods since it will be returned below
                this.methods = temp;
            } else {
                int total = length + this.methods.length;
                MethodBinding[] temp = new MethodBinding[total];
                System.arraycopy(parameterizedMethods, 0, temp, 0, length);
                System.arraycopy(this.methods, 0, temp, length, this.methods.length);
                if (total > 1)
                    // resort to ensure order is good
                    ReferenceBinding.sortMethods(temp, 0, total);
                this.methods = temp;
            }
            return parameterizedMethods;
        } finally {
            // if the original methods cannot be retrieved (ex. AbortCompilation), then assume we do not have any methods
            if (parameterizedMethods == null)
                this.methods = parameterizedMethods = Binding.NO_METHODS;
        }
    }

    public int getOuterLocalVariablesSlotSize() {
        return genericType().getOuterLocalVariablesSlotSize();
    }

    public boolean hasMemberTypes() {
        return this.type.hasMemberTypes();
    }

    public boolean hasTypeBit(int bit) {
        TypeBinding erasure = erasure();
        if (erasure instanceof ReferenceBinding)
            return ((ReferenceBinding) erasure).hasTypeBit(bit);
        return false;
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#implementsMethod(MethodBinding)
	 */
    public boolean implementsMethod(MethodBinding method) {
        // erasure
        return this.type.implementsMethod(method);
    }

    void initialize(ReferenceBinding someType, TypeBinding[] someArguments) {
        this.type = someType;
        this.sourceName = someType.sourceName;
        this.compoundName = someType.compoundName;
        this.fPackage = someType.fPackage;
        this.fileName = someType.fileName;
        // should not be set yet
        // this.superclass = null;
        // this.superInterfaces = null;
        // this.fields = null;
        // this.methods = null;
        // discard generic signature, will compute later
        this.modifiers = someType.modifiers & ~ExtraCompilerModifiers.AccGenericSignature;
        // only set AccGenericSignature if parameterized or have enclosing type required signature
        if (someArguments != null) {
            this.modifiers |= ExtraCompilerModifiers.AccGenericSignature;
        } else if (this.enclosingType != null) {
            this.modifiers |= (this.enclosingType.modifiers & ExtraCompilerModifiers.AccGenericSignature);
            this.tagBits |= this.enclosingType.tagBits & (TagBits.HasTypeVariable | TagBits.HasMissingType | TagBits.HasCapturedWildcard);
        }
        if (someArguments != null) {
            this.arguments = someArguments;
            for (int i = 0, length = someArguments.length; i < length; i++) {
                TypeBinding someArgument = someArguments[i];
                switch(someArgument.kind()) {
                    case Binding.WILDCARD_TYPE:
                        this.tagBits |= TagBits.HasDirectWildcard;
                        if (((WildcardBinding) someArgument).boundKind != Wildcard.UNBOUND) {
                            this.tagBits |= TagBits.IsBoundParameterizedType;
                        }
                        break;
                    case Binding.INTERSECTION_TYPE:
                        // Surely NOT X<?,?>, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=366131
                        this.tagBits |= TagBits.HasDirectWildcard | TagBits.IsBoundParameterizedType;
                        break;
                    default:
                        this.tagBits |= TagBits.IsBoundParameterizedType;
                        break;
                }
                this.tagBits |= someArgument.tagBits & (TagBits.HasTypeVariable | TagBits.HasMissingType | TagBits.ContainsNestedTypeReferences | TagBits.HasCapturedWildcard);
            }
        }
        this.tagBits |= someType.tagBits & (TagBits.IsLocalType | TagBits.IsMemberType | TagBits.IsNestedType | TagBits.ContainsNestedTypeReferences | TagBits.HasMissingType | TagBits.AnnotationNullMASK | TagBits.AnnotationNonNullByDefault | TagBits.AnnotationNullUnspecifiedByDefault | TagBits.HasCapturedWildcard);
        this.tagBits &= ~(TagBits.AreFieldsComplete | TagBits.AreMethodsComplete);
    }

    protected void initializeArguments() {
    // do nothing for true parameterized types (only for raw types)
    }

    void initializeForStaticImports() {
        this.type.initializeForStaticImports();
    }

    /**
	 *  Returns true if parameterized type AND not of the form List<?>
	 */
    public boolean isBoundParameterizedType() {
        return (this.tagBits & TagBits.IsBoundParameterizedType) != 0;
    }

    public boolean isEquivalentTo(TypeBinding otherType) {
        if (equalsEquals(this, otherType))
            return true;
        if (otherType == null)
            return false;
        switch(otherType.kind()) {
            case Binding.WILDCARD_TYPE:
            case Binding.INTERSECTION_TYPE:
                return ((WildcardBinding) otherType).boundCheck(this);
            case Binding.PARAMETERIZED_TYPE:
                ParameterizedTypeBinding otherParamType = (ParameterizedTypeBinding) otherType;
                if (TypeBinding.notEquals(this.type, otherParamType.type))
                    return false;
                if (// static member types do not compare their enclosing
                !isStatic()) {
                    ReferenceBinding enclosing = enclosingType();
                    if (enclosing != null) {
                        ReferenceBinding otherEnclosing = otherParamType.enclosingType();
                        if (otherEnclosing == null)
                            return false;
                        if ((otherEnclosing.tagBits & TagBits.HasDirectWildcard) == 0) {
                            if (TypeBinding.notEquals(enclosing, otherEnclosing))
                                return false;
                        } else {
                            if (!enclosing.isEquivalentTo(otherParamType.enclosingType()))
                                return false;
                        }
                    }
                }
                if (this.arguments != ParameterizedSingleTypeReference.DIAMOND_TYPE_ARGUMENTS) {
                    if (this.arguments == null) {
                        return otherParamType.arguments == null;
                    }
                    int length = this.arguments.length;
                    TypeBinding[] otherArguments = otherParamType.arguments;
                    if (otherArguments == null || otherArguments.length != length)
                        return false;
                    for (int i = 0; i < length; i++) {
                        if (!this.arguments[i].isTypeArgumentContainedBy(otherArguments[i]))
                            return false;
                    }
                }
                return true;
            case Binding.RAW_TYPE:
                return TypeBinding.equalsEquals(erasure(), otherType.erasure());
        }
        /* With the hybrid 1.4/1.5+ projects modes, while establishing type equivalence, we need to
	       be prepared for a type such as Map appearing in one of three forms: As (a) a ParameterizedTypeBinding 
	       e.g Map<String, String>, (b) as RawTypeBinding Map#RAW and finally (c) as a BinaryTypeBinding 
	       When the usage of a type lacks type parameters, whether we land up with the raw form or not depends
	       on whether the underlying type was "seen to be" a generic type in the particular build environment or
	       not. See https://bugs.eclipse.org/bugs/show_bug.cgi?id=328827 
	     */
        if (TypeBinding.equalsEquals(erasure(), otherType)) {
            return true;
        }
        return false;
    }

    public boolean isHierarchyConnected() {
        return this.superclass != null && this.superInterfaces != null;
    }

    public boolean isProperType(boolean admitCapture18) {
        if (this.arguments != null) {
            for (int i = 0; i < this.arguments.length; i++) if (!this.arguments[i].isProperType(admitCapture18))
                return false;
        }
        return super.isProperType(admitCapture18);
    }

    TypeBinding substituteInferenceVariable(InferenceVariable var, TypeBinding substituteType) {
        if (this.arguments != null) {
            TypeBinding[] newArgs = null;
            int length = this.arguments.length;
            for (int i = 0; i < length; i++) {
                TypeBinding oldArg = this.arguments[i];
                TypeBinding newArg = oldArg.substituteInferenceVariable(var, substituteType);
                if (TypeBinding.notEquals(newArg, oldArg)) {
                    if (newArgs == null)
                        System.arraycopy(this.arguments, 0, newArgs = new TypeBinding[length], 0, length);
                    newArgs[i] = newArg;
                }
            }
            if (newArgs != null)
                return this.environment.createParameterizedType(this.type, newArgs, this.enclosingType);
        }
        return this;
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.Substitution#isRawSubstitution()
	 */
    public boolean isRawSubstitution() {
        return isRawType();
    }

    public TypeBinding unannotated() {
        return this.hasTypeAnnotations() ? this.environment.getUnannotatedType(this) : this;
    }

    @Override
    public TypeBinding withoutToplevelNullAnnotation() {
        if (!hasNullTypeAnnotations())
            return this;
        ReferenceBinding unannotatedGenericType = (ReferenceBinding) this.environment.getUnannotatedType(this.type);
        AnnotationBinding[] newAnnotations = this.environment.filterNullTypeAnnotations(this.typeAnnotations);
        return this.environment.createParameterizedType(unannotatedGenericType, this.arguments, this.enclosingType, newAnnotations);
    }

    public int kind() {
        return PARAMETERIZED_TYPE;
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#memberTypes()
	 */
    public ReferenceBinding[] memberTypes() {
        if (this.memberTypes == null) {
            try {
                ReferenceBinding[] originalMemberTypes = this.type.memberTypes();
                int length = originalMemberTypes.length;
                ReferenceBinding[] parameterizedMemberTypes = new ReferenceBinding[length];
                // boolean isRaw = this.isRawType();
                for (int i = 0; i < length; i++) {
                    // substitute all member types, so as to get updated enclosing types
                    parameterizedMemberTypes[i] = originalMemberTypes[i].isStatic() ? originalMemberTypes[i] : this.environment.createParameterizedType(originalMemberTypes[i], null, this);
                }
                this.memberTypes = parameterizedMemberTypes;
            } finally {
                // if the original fields cannot be retrieved (ex. AbortCompilation), then assume we do not have any fields
                if (this.memberTypes == null)
                    this.memberTypes = Binding.NO_MEMBER_TYPES;
            }
        }
        return this.memberTypes;
    }

    public boolean mentionsAny(TypeBinding[] parameters, int idx) {
        if (super.mentionsAny(parameters, idx))
            return true;
        if (this.arguments != null) {
            int len = this.arguments.length;
            for (int i = 0; i < len; i++) {
                if (TypeBinding.notEquals(this.arguments[i], this) && this.arguments[i].mentionsAny(parameters, idx))
                    return true;
            }
        }
        return false;
    }

    void collectInferenceVariables(Set<InferenceVariable> variables) {
        if (this.arguments != null) {
            int len = this.arguments.length;
            for (int i = 0; i < len; i++) {
                if (TypeBinding.notEquals(this.arguments[i], this))
                    this.arguments[i].collectInferenceVariables(variables);
            }
        }
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#methods()
	 */
    public MethodBinding[] methods() {
        if ((this.tagBits & TagBits.AreMethodsComplete) != 0)
            return this.methods;
        try {
            MethodBinding[] originalMethods = this.type.methods();
            int length = originalMethods.length;
            MethodBinding[] parameterizedMethods = new MethodBinding[length];
            boolean useNullTypeAnnotations = this.environment.usesNullTypeAnnotations();
            for (int i = 0; i < length; i++) {
                // substitute all methods, so as to get updated declaring class at least
                parameterizedMethods[i] = createParameterizedMethod(originalMethods[i]);
                if (useNullTypeAnnotations)
                    parameterizedMethods[i] = NullAnnotationMatching.checkForContradictions(parameterizedMethods[i], null, null);
            }
            this.methods = parameterizedMethods;
        } finally {
            // if the original methods cannot be retrieved (ex. AbortCompilation), then assume we do not have any methods
            if (this.methods == null)
                this.methods = Binding.NO_METHODS;
            this.tagBits |= TagBits.AreMethodsComplete;
        }
        return this.methods;
    }

    /**
	 * Define to be able to get the computeId() for the inner type binding.
	 *
	 * @see org.eclipse.jdt.internal.compiler.lookup.Binding#problemId()
	 */
    public int problemId() {
        return this.type.problemId();
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.TypeBinding#qualifiedPackageName()
	 */
    public char[] qualifiedPackageName() {
        return this.type.qualifiedPackageName();
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.TypeBinding#qualifiedSourceName()
	 */
    public char[] qualifiedSourceName() {
        return this.type.qualifiedSourceName();
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.Binding#readableName()
	 */
    public char[] readableName() {
        return readableName(true);
    }

    public char[] readableName(boolean showGenerics) {
        StringBuffer nameBuffer = new StringBuffer(10);
        if (isMemberType()) {
            nameBuffer.append(CharOperation.concat(enclosingType().readableName(showGenerics && !isStatic()), this.sourceName, '.'));
        } else {
            nameBuffer.append(CharOperation.concatWith(this.type.compoundName, '.'));
        }
        if (showGenerics) {
            if (// empty arguments array happens when PTB has been created just to capture type annotations
            this.arguments != null && this.arguments.length > 0) {
                nameBuffer.append('<');
                for (int i = 0, length = this.arguments.length; i < length; i++) {
                    if (i > 0)
                        nameBuffer.append(',');
                    nameBuffer.append(this.arguments[i].readableName());
                }
                nameBuffer.append('>');
            }
        }
        int nameLength = nameBuffer.length();
        char[] readableName = new char[nameLength];
        nameBuffer.getChars(0, nameLength, readableName, 0);
        return readableName;
    }

    ReferenceBinding resolve() {
        if ((this.tagBits & TagBits.HasUnresolvedTypeVariables) == 0)
            return this;
        // can be recursive so only want to call once
        this.tagBits &= ~TagBits.HasUnresolvedTypeVariables;
        // still part of parameterized type ref
        ReferenceBinding resolvedType = (ReferenceBinding) BinaryTypeBinding.resolveType(this.type, this.environment, /* no raw conversion */
        false);
        this.tagBits |= resolvedType.tagBits & TagBits.ContainsNestedTypeReferences;
        if (this.arguments != null) {
            int argLength = this.arguments.length;
            for (int i = 0; i < argLength; i++) {
                TypeBinding resolveType = BinaryTypeBinding.resolveType(this.arguments[i], this.environment, /* raw conversion */
                true);
                this.arguments[i] = resolveType;
                this.tagBits |= resolvedType.tagBits & TagBits.ContainsNestedTypeReferences;
            }
        /* https://bugs.eclipse.org/bugs/show_bug.cgi?id=186565, Removed generic check
			   and arity check since we are dealing with binary types here and the fact that
			   the compiler produced class files for these types at all is proof positive that
			   the generic check and the arity check passed in the build environment that produced
			   these class files. Otherwise we don't handle mixed 1.5 and 1.4 projects correctly.
			   Just as with bounds check below, incremental build will propagate the change and
			   detect problems in source.
			 */
        //			// arity check
        //			TypeVariableBinding[] refTypeVariables = resolvedType.typeVariables();
        //			if (refTypeVariables == Binding.NO_TYPE_VARIABLES) { // check generic
        //				// Below 1.5, we should have already complained about the use of type parameters.
        //				boolean isCompliant15 = this.environment.globalOptions.originalSourceLevel >= ClassFileConstants.JDK1_5;
        //				if (isCompliant15 && (resolvedType.tagBits & TagBits.HasMissingType) == 0) {
        //					this.environment.problemReporter.nonGenericTypeCannotBeParameterized(0, null, resolvedType, this.arguments);
        //				}
        //				return this;
        //			} else if (argLength != refTypeVariables.length) { // check arity
        //				this.environment.problemReporter.incorrectArityForParameterizedType(null, resolvedType, this.arguments);
        //				return this; // cannot reach here as AbortCompilation is thrown
        //			}
        // check argument type compatibility... REMOVED for now since incremental build will propagate change & detect in source
        //			for (int i = 0; i < argLength; i++) {
        //			    TypeBinding resolvedArgument = this.arguments[i];
        //				if (refTypeVariables[i].boundCheck(this, resolvedArgument) != TypeConstants.OK) {
        //					this.environment.problemReporter.typeMismatchError(resolvedArgument, refTypeVariables[i], resolvedType, null);
        //			    }
        //			}
        }
        return this;
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.Binding#shortReadableName()
	 */
    public char[] shortReadableName() {
        return shortReadableName(true);
    }

    public char[] shortReadableName(boolean showGenerics) {
        StringBuffer nameBuffer = new StringBuffer(10);
        if (isMemberType()) {
            nameBuffer.append(CharOperation.concat(enclosingType().shortReadableName(showGenerics && !isStatic()), this.sourceName, '.'));
        } else {
            nameBuffer.append(this.type.sourceName);
        }
        if (showGenerics) {
            if (// empty arguments array happens when PTB has been created just to capture type annotations
            this.arguments != null && this.arguments.length > 0) {
                nameBuffer.append('<');
                for (int i = 0, length = this.arguments.length; i < length; i++) {
                    if (i > 0)
                        nameBuffer.append(',');
                    nameBuffer.append(this.arguments[i].shortReadableName());
                }
                nameBuffer.append('>');
            }
        }
        int nameLength = nameBuffer.length();
        char[] shortReadableName = new char[nameLength];
        nameBuffer.getChars(0, nameLength, shortReadableName, 0);
        return shortReadableName;
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.TypeBinding#nullAnnotatedReadableName(CompilerOptions,boolean)
	 */
    public char[] nullAnnotatedReadableName(CompilerOptions options, boolean shortNames) {
        if (shortNames)
            return nullAnnotatedShortReadableName(options);
        return nullAnnotatedReadableName(options);
    }

    char[] nullAnnotatedReadableName(CompilerOptions options) {
        StringBuffer nameBuffer = new StringBuffer(10);
        if (isMemberType()) {
            nameBuffer.append(enclosingType().nullAnnotatedReadableName(options, false));
            nameBuffer.append('.');
            appendNullAnnotation(nameBuffer, options);
            nameBuffer.append(this.sourceName);
        } else if (this.type.compoundName != null) {
            int i;
            int l = this.type.compoundName.length;
            for (i = 0; i < l - 1; i++) {
                nameBuffer.append(this.type.compoundName[i]);
                nameBuffer.append('.');
            }
            appendNullAnnotation(nameBuffer, options);
            nameBuffer.append(this.type.compoundName[i]);
        } else {
            // case of TypeVariableBinding with nullAnnotationTagBits:
            appendNullAnnotation(nameBuffer, options);
            if (this.type.sourceName != null)
                nameBuffer.append(this.type.sourceName);
            else
                // WildcardBinding, CaptureBinding have no sourceName
                nameBuffer.append(this.type.readableName());
        }
        if (// empty arguments array happens when PTB has been created just to capture type annotations
        this.arguments != null && this.arguments.length > 0) {
            nameBuffer.append('<');
            for (int i = 0, length = this.arguments.length; i < length; i++) {
                if (i > 0)
                    nameBuffer.append(',');
                nameBuffer.append(this.arguments[i].nullAnnotatedReadableName(options, false));
            }
            nameBuffer.append('>');
        }
        int nameLength = nameBuffer.length();
        char[] readableName = new char[nameLength];
        nameBuffer.getChars(0, nameLength, readableName, 0);
        return readableName;
    }

    char[] nullAnnotatedShortReadableName(CompilerOptions options) {
        StringBuffer nameBuffer = new StringBuffer(10);
        if (isMemberType()) {
            nameBuffer.append(enclosingType().nullAnnotatedReadableName(options, true));
            nameBuffer.append('.');
            appendNullAnnotation(nameBuffer, options);
            nameBuffer.append(this.sourceName);
        } else {
            appendNullAnnotation(nameBuffer, options);
            if (this.type.sourceName != null)
                nameBuffer.append(this.type.sourceName);
            else
                // WildcardBinding, CaptureBinding have no sourceName
                nameBuffer.append(this.type.shortReadableName());
        }
        if (// empty arguments array happens when PTB has been created just to capture type annotations
        this.arguments != null && this.arguments.length > 0) {
            nameBuffer.append('<');
            for (int i = 0, length = this.arguments.length; i < length; i++) {
                if (i > 0)
                    nameBuffer.append(',');
                nameBuffer.append(this.arguments[i].nullAnnotatedReadableName(options, true));
            }
            nameBuffer.append('>');
        }
        int nameLength = nameBuffer.length();
        char[] shortReadableName = new char[nameLength];
        nameBuffer.getChars(0, nameLength, shortReadableName, 0);
        return shortReadableName;
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.TypeBinding#signature()
	 */
    public char[] signature() {
        if (this.signature == null) {
            // erasure
            this.signature = this.type.signature();
        }
        return this.signature;
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.TypeBinding#sourceName()
	 */
    public char[] sourceName() {
        return this.type.sourceName();
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.Substitution#substitute(org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding)
	 */
    public TypeBinding substitute(TypeVariableBinding originalVariable) {
        ParameterizedTypeBinding currentType = this;
        while (true) {
            TypeVariableBinding[] typeVariables = currentType.type.typeVariables();
            int length = typeVariables.length;
            // check this variable can be substituted given parameterized type
            if (originalVariable.rank < length && TypeBinding.equalsEquals(typeVariables[originalVariable.rank], originalVariable)) {
                // lazy init, since cannot do so during binding creation if during supertype connection
                if (currentType.arguments == null)
                    // only for raw types
                    currentType.initializeArguments();
                if (currentType.arguments != null) {
                    if (// diamond type
                    currentType.arguments.length == 0) {
                        return originalVariable;
                    }
                    TypeBinding substitute = currentType.arguments[originalVariable.rank];
                    return originalVariable.combineTypeAnnotations(substitute);
                }
            }
            // recurse on enclosing type, as it may hold more substitutions to perform
            if (currentType.isStatic())
                break;
            ReferenceBinding enclosing = currentType.enclosingType();
            if (!(enclosing instanceof ParameterizedTypeBinding))
                break;
            currentType = (ParameterizedTypeBinding) enclosing;
        }
        return originalVariable;
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#superclass()
	 */
    public ReferenceBinding superclass() {
        if (this.superclass == null) {
            // note: Object cannot be generic
            ReferenceBinding genericSuperclass = this.type.superclass();
            // e.g. interfaces
            if (genericSuperclass == null)
                return null;
            this.superclass = (ReferenceBinding) Scope.substitute(this, genericSuperclass);
            this.typeBits |= (this.superclass.typeBits & TypeIds.InheritableBits);
            if (// avoid the side-effects of hasTypeBit()! 
            (this.typeBits & (TypeIds.BitAutoCloseable | TypeIds.BitCloseable)) != 0)
                this.typeBits |= applyCloseableClassWhitelists();
        }
        return this.superclass;
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#superInterfaces()
	 */
    public ReferenceBinding[] superInterfaces() {
        if (this.superInterfaces == null) {
            if (this.type.isHierarchyBeingConnected())
                // prevent superinterfaces from being assigned before they are connected
                return Binding.NO_SUPERINTERFACES;
            this.superInterfaces = Scope.substitute(this, this.type.superInterfaces());
            if (this.superInterfaces != null) {
                for (int i = this.superInterfaces.length; --i >= 0; ) {
                    this.typeBits |= (this.superInterfaces[i].typeBits & TypeIds.InheritableBits);
                    if (// avoid the side-effects of hasTypeBit()! 
                    (this.typeBits & (TypeIds.BitAutoCloseable | TypeIds.BitCloseable)) != 0)
                        this.typeBits |= applyCloseableInterfaceWhitelists();
                }
            }
        }
        return this.superInterfaces;
    }

    public void swapUnresolved(UnresolvedReferenceBinding unresolvedType, ReferenceBinding resolvedType, LookupEnvironment env) {
        boolean update = false;
        if (//$IDENTITY-COMPARISON$
        this.type == unresolvedType) {
            // cannot be raw since being parameterized below
            this.type = resolvedType;
            update = true;
            ReferenceBinding enclosing = resolvedType.enclosingType();
            if (enclosing != null) {
                // needed when binding unresolved member type
                this.enclosingType = (ReferenceBinding) env.convertUnresolvedBinaryToRawType(enclosing);
            }
        }
        if (this.arguments != null) {
            for (int i = 0, l = this.arguments.length; i < l; i++) {
                if (//$IDENTITY-COMPARISON$
                this.arguments[i] == unresolvedType) {
                    this.arguments[i] = env.convertUnresolvedBinaryToRawType(resolvedType);
                    update = true;
                }
            }
        }
        if (update)
            initialize(this.type, this.arguments);
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#syntheticEnclosingInstanceTypes()
	 */
    public ReferenceBinding[] syntheticEnclosingInstanceTypes() {
        return genericType().syntheticEnclosingInstanceTypes();
    }

    /**
	 * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#syntheticOuterLocalVariables()
	 */
    public SyntheticArgumentBinding[] syntheticOuterLocalVariables() {
        return genericType().syntheticOuterLocalVariables();
    }

    /**
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        if (this.hasTypeAnnotations()) {
            return annotatedDebugName();
        }
        StringBuffer buffer = new StringBuffer(30);
        if (this.type instanceof UnresolvedReferenceBinding) {
            buffer.append(debugName());
        } else {
            //$NON-NLS-1$
            if (isDeprecated())
                buffer.append("deprecated ");
            //$NON-NLS-1$
            if (isPublic())
                buffer.append("public ");
            //$NON-NLS-1$
            if (isProtected())
                buffer.append("protected ");
            //$NON-NLS-1$
            if (isPrivate())
                buffer.append("private ");
            //$NON-NLS-1$
            if (isAbstract() && isClass())
                buffer.append("abstract ");
            //$NON-NLS-1$
            if (isStatic() && isNestedType())
                buffer.append("static ");
            //$NON-NLS-1$
            if (isFinal())
                buffer.append("final ");
            if (//$NON-NLS-1$
            isEnum())
                //$NON-NLS-1$
                buffer.append("enum ");
            else if (//$NON-NLS-1$
            isAnnotationType())
                //$NON-NLS-1$
                buffer.append("@interface ");
            else if (//$NON-NLS-1$
            isClass())
                //$NON-NLS-1$
                buffer.append("class ");
            else
                //$NON-NLS-1$
                buffer.append("interface ");
            buffer.append(debugName());
            //$NON-NLS-1$
            buffer.append("\n\textends ");
            //$NON-NLS-1$
            buffer.append((this.superclass != null) ? this.superclass.debugName() : "NULL TYPE");
            if (this.superInterfaces != null) {
                if (this.superInterfaces != Binding.NO_SUPERINTERFACES) {
                    buffer.append("\n\timplements : ");
                    for (int i = 0, length = this.superInterfaces.length; i < length; i++) {
                        if (i > 0)
                            //$NON-NLS-1$
                            buffer.append(", ");
                        buffer.append(//$NON-NLS-1$
                        (this.superInterfaces[i] != null) ? //$NON-NLS-1$
                        this.superInterfaces[i].debugName() : //$NON-NLS-1$
                        "NULL TYPE");
                    }
                }
            } else {
                //$NON-NLS-1$
                buffer.append(//$NON-NLS-1$
                "NULL SUPERINTERFACES");
            }
            if (enclosingType() != null) {
                //$NON-NLS-1$
                buffer.append(//$NON-NLS-1$
                "\n\tenclosing type : ");
                buffer.append(enclosingType().debugName());
            }
            if (this.fields != null) {
                if (this.fields != Binding.NO_FIELDS) {
                    buffer.append("\n/*   fields   */");
                    for (int i = 0, length = this.fields.length; i < length; i++) buffer.append('\n').append(//$NON-NLS-1$
                    (this.fields[i] != null) ? //$NON-NLS-1$
                    this.fields[i].toString() : //$NON-NLS-1$
                    "NULL FIELD");
                }
            } else {
                //$NON-NLS-1$
                buffer.append(//$NON-NLS-1$
                "NULL FIELDS");
            }
            if (this.methods != null) {
                if (this.methods != Binding.NO_METHODS) {
                    buffer.append("\n/*   methods   */");
                    for (int i = 0, length = this.methods.length; i < length; i++) buffer.append('\n').append(//$NON-NLS-1$
                    (this.methods[i] != null) ? //$NON-NLS-1$
                    this.methods[i].toString() : //$NON-NLS-1$
                    "NULL METHOD");
                }
            } else {
                //$NON-NLS-1$
                buffer.append(//$NON-NLS-1$
                "NULL METHODS");
            }
            //		if (memberTypes != null) {
            //			if (memberTypes != NoMemberTypes) {
            //				buffer.append("\n/*   members   */");
            //				for (int i = 0, length = memberTypes.length; i < length; i++)
            //					buffer.append('\n').append((memberTypes[i] != null) ? memberTypes[i].toString() : "NULL TYPE");
            //			}
            //		} else {
            //			buffer.append("NULL MEMBER TYPES");
            //		}
            //$NON-NLS-1$
            buffer.append("\n\n");
        }
        return buffer.toString();
    }

    public TypeVariableBinding[] typeVariables() {
        if (this.arguments == null) {
            // retain original type variables if not substituted (member type of parameterized type)
            return this.type.typeVariables();
        }
        return Binding.NO_TYPE_VARIABLES;
    }

    public TypeBinding[] typeArguments() {
        return this.arguments;
    }

    public FieldBinding[] unResolvedFields() {
        return this.fields;
    }

    @Override
    protected MethodBinding[] getInterfaceAbstractContracts(Scope scope, boolean replaceWildcards) throws InvalidInputException {
        if (replaceWildcards) {
            TypeBinding[] types = getNonWildcardParameterization(scope);
            if (types == null)
                return new MethodBinding[] { new ProblemMethodBinding(TypeConstants.ANONYMOUS_METHOD, null, ProblemReasons.NotAWellFormedParameterizedType) };
            for (int i = 0; i < types.length; i++) {
                if (TypeBinding.notEquals(types[i], this.arguments[i])) {
                    // non-wildcard parameterization differs from this, so use it:
                    ParameterizedTypeBinding declaringType = scope.environment().createParameterizedType(this.type, types, this.type.enclosingType());
                    TypeVariableBinding[] typeParameters = this.type.typeVariables();
                    for (int j = 0, length = typeParameters.length; j < length; j++) {
                        if (!typeParameters[j].boundCheck(declaringType, types[j], scope, null).isOKbyJLS())
                            return new MethodBinding[] { new ProblemMethodBinding(TypeConstants.ANONYMOUS_METHOD, null, ProblemReasons.NotAWellFormedParameterizedType) };
                    }
                    return declaringType.getInterfaceAbstractContracts(scope, replaceWildcards);
                }
            }
        }
        return super.getInterfaceAbstractContracts(scope, replaceWildcards);
    }

    public MethodBinding getSingleAbstractMethod(final Scope scope, boolean replaceWildcards) {
        return getSingleAbstractMethod(scope, replaceWildcards, /* do not capture */
        -1, -1);
    }

    public MethodBinding getSingleAbstractMethod(final Scope scope, boolean replaceWildcards, int start, int end) {
        // capturePosition >= 0 IFF replaceWildcard == true
        int index = replaceWildcards ? end < 0 ? 0 : 1 : 2;
        if (this.singleAbstractMethod != null) {
            if (this.singleAbstractMethod[index] != null)
                return this.singleAbstractMethod[index];
        } else {
            this.singleAbstractMethod = new MethodBinding[3];
        }
        if (!isValidBinding())
            return null;
        final ReferenceBinding genericType = genericType();
        MethodBinding theAbstractMethod = genericType.getSingleAbstractMethod(scope, replaceWildcards);
        if (theAbstractMethod == null || !theAbstractMethod.isValidBinding())
            return this.singleAbstractMethod[index] = theAbstractMethod;
        ParameterizedTypeBinding declaringType = null;
        TypeBinding[] types = this.arguments;
        if (replaceWildcards) {
            types = getNonWildcardParameterization(scope);
            if (types == null)
                return this.singleAbstractMethod[index] = new ProblemMethodBinding(TypeConstants.ANONYMOUS_METHOD, null, ProblemReasons.NotAWellFormedParameterizedType);
        } else if (types == null) {
            types = NO_TYPES;
        }
        if (end >= 0) {
            // capture first and then substitute.
            for (int i = 0, length = types.length; i < length; i++) {
                types[i] = types[i].capture(scope, start, end);
            }
        }
        declaringType = scope.environment().createParameterizedType(genericType, types, genericType.enclosingType());
        TypeVariableBinding[] typeParameters = genericType.typeVariables();
        for (int i = 0, length = typeParameters.length; i < length; i++) {
            if (!typeParameters[i].boundCheck(declaringType, types[i], scope, null).isOKbyJLS())
                return this.singleAbstractMethod[index] = new ProblemMethodBinding(TypeConstants.ANONYMOUS_METHOD, null, ProblemReasons.NotAWellFormedParameterizedType);
        }
        ReferenceBinding substitutedDeclaringType = (ReferenceBinding) declaringType.findSuperTypeOriginatingFrom(theAbstractMethod.declaringClass);
        MethodBinding[] choices = substitutedDeclaringType.getMethods(theAbstractMethod.selector);
        for (int i = 0, length = choices.length; i < length; i++) {
            MethodBinding method = choices[i];
            // (re)skip statics, defaults, public object methods ...
            if (!method.isAbstract() || method.redeclaresPublicObjectMethod(scope))
                continue;
            this.singleAbstractMethod[index] = method;
            break;
        }
        return this.singleAbstractMethod[index];
    }

    // from JLS 9.8
    public TypeBinding[] getNonWildcardParameterization(Scope scope) {
        // precondition: isValidBinding()
        // A1 ... An
        TypeBinding[] typeArguments = this.arguments;
        if (typeArguments == null)
            return NO_TYPES;
        // P1 ... Pn
        TypeVariableBinding[] typeParameters = genericType().typeVariables();
        // T1 ... Tn
        TypeBinding[] types = new TypeBinding[typeArguments.length];
        for (int i = 0, length = typeArguments.length; i < length; i++) {
            TypeBinding typeArgument = typeArguments[i];
            if (typeArgument.kind() == Binding.WILDCARD_TYPE) {
                if (typeParameters[i].mentionsAny(typeParameters, i))
                    return null;
                WildcardBinding wildcard = (WildcardBinding) typeArgument;
                switch(wildcard.boundKind) {
                    case Wildcard.EXTENDS:
                        // If Ai is a upper-bounded wildcard ? extends Ui, then Ti = glb(Ui, Bi).
                        // Note: neither Ui nor Bi is necessarily scalar -> need to collect all bounds
                        TypeBinding[] otherUBounds = wildcard.otherBounds;
                        TypeBinding[] otherBBounds = typeParameters[i].otherUpperBounds();
                        int len = 1 + (otherUBounds != null ? otherUBounds.length : 0) + otherBBounds.length;
                        if (typeParameters[i].firstBound != null)
                            len++;
                        // TypeBinding so that in this round we accept ArrayBinding, too.
                        TypeBinding[] allBounds = new TypeBinding[len];
                        int idx = 0;
                        // Ui
                        allBounds[idx++] = wildcard.bound;
                        if (otherUBounds != null)
                            for (int j = 0; j < otherUBounds.length; j++) allBounds[idx++] = otherUBounds[j];
                        // Bi
                        if (typeParameters[i].firstBound != null)
                            allBounds[idx++] = typeParameters[i].firstBound;
                        for (int j = 0; j < otherBBounds.length; j++) allBounds[idx++] = otherBBounds[j];
                        TypeBinding[] glb = Scope.greaterLowerBound(allBounds, null, this.environment);
                        if (glb == null || glb.length == 0) {
                            return null;
                        } else if (glb.length == 1) {
                            types[i] = glb[0];
                        } else {
                            try {
                                ReferenceBinding[] refs = new ReferenceBinding[glb.length];
                                // TODO: if an array type plus more types get here, we get ArrayStoreException!
                                System.arraycopy(glb, 0, refs, 0, glb.length);
                                types[i] = this.environment.createIntersectionType18(refs);
                            } catch (ArrayStoreException ase) {
                                scope.problemReporter().genericInferenceError("Cannot compute glb of " + Arrays.toString(glb), null);
                                return null;
                            }
                        }
                        break;
                    case Wildcard.SUPER:
                        // If Ai is a lower-bounded wildcard ? super Li, then Ti = Li.
                        types[i] = wildcard.bound;
                        break;
                    case Wildcard.UNBOUND:
                        // If Ai is an unbound wildcard ?, then Ti = Bi.
                        types[i] = typeParameters[i].firstBound;
                        if (types[i] == null)
                            types[i] = // assumably j.l.Object?
                            typeParameters[i].superclass;
                        break;
                }
            } else {
                // If Ai is a type, then Ti = Ai.
                types[i] = typeArgument;
            }
        }
        return types;
    }

    @Override
    public long updateTagBits() {
        if (this.arguments != null)
            for (TypeBinding argument : this.arguments) this.tagBits |= argument.updateTagBits();
        return super.updateTagBits();
    }
}
