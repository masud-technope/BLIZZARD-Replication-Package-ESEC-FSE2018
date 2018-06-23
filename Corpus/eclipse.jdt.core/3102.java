/*******************************************************************************
 * Copyright (c) 2005, 2016 BEA Systems, Inc. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tyeung@bea.com - initial API and implementation
 *    IBM Corporation - Fixed https://bugs.eclipse.org/bugs/show_bug.cgi?id=352949
 *******************************************************************************/
package org.eclipse.jdt.apt.core.internal.util;

import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationValue;
import com.sun.mirror.declaration.ParameterDeclaration;
import com.sun.mirror.type.AnnotationType;
import com.sun.mirror.type.ArrayType;
import com.sun.mirror.type.ClassType;
import com.sun.mirror.type.InterfaceType;
import com.sun.mirror.type.PrimitiveType;
import com.sun.mirror.type.TypeMirror;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.apt.core.internal.declaration.*;
import org.eclipse.jdt.apt.core.internal.env.BaseProcessorEnv;
import org.eclipse.jdt.apt.core.internal.type.ArrayTypeImpl;
import org.eclipse.jdt.apt.core.internal.type.ErrorType;
import org.eclipse.jdt.apt.core.internal.type.WildcardTypeImpl;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class Factory {

    //$NON-NLS-1$
    private static final String NULL_BINDING_NAME = "[NullBinding]";

    // using auto-boxing to take advantage of caching, if any.
    // the dummy value picked here falls within the caching range.
    public static final Byte DUMMY_BYTE = 0;

    public static final Character DUMMY_CHAR = '0';

    public static final Double DUMMY_DOUBLE = 0d;

    public static final Float DUMMY_FLOAT = 0f;

    public static final Integer DUMMY_INTEGER = 0;

    public static final Long DUMMY_LONG = 0l;

    public static final Short DUMMY_SHORT = 0;

    public static TypeDeclarationImpl createReferenceType(ITypeBinding binding, BaseProcessorEnv env) {
        if (binding == null || binding.isNullType())
            return null;
        // to exist (as an ErrorType) but there is no declaration.
        if (binding.isRecovered())
            return null;
        TypeDeclarationImpl mirror = null;
        // is an interface
        if (binding.isAnnotation())
            mirror = new AnnotationDeclarationImpl(binding, env);
        else if (binding.isInterface())
            mirror = new InterfaceDeclarationImpl(binding, env);
        else // must test for enum first since enum is also a class. 
        if (binding.isEnum())
            mirror = new EnumDeclarationImpl(binding, env);
        else if (binding.isClass())
            mirror = new ClassDeclarationImpl(binding, env);
        else
            //$NON-NLS-1$
            throw new IllegalStateException("cannot create type declaration from " + binding);
        return mirror;
    }

    public static EclipseDeclarationImpl createDeclaration(IBinding binding, BaseProcessorEnv env) {
        if (binding == null)
            return null;
        switch(binding.getKind()) {
            case IBinding.TYPE:
                final ITypeBinding typeBinding = (ITypeBinding) binding;
                if (typeBinding.isAnonymous() || typeBinding.isArray() || typeBinding.isWildcardType() || typeBinding.isPrimitive())
                    //$NON-NLS-1$
                    throw new IllegalStateException("failed to create declaration from " + binding);
                if (typeBinding.isTypeVariable())
                    return new TypeParameterDeclarationImpl(typeBinding, env);
                else
                    return createReferenceType(typeBinding, env);
            case IBinding.VARIABLE:
                final IVariableBinding varBinding = (IVariableBinding) binding;
                if (varBinding.isEnumConstant())
                    return new EnumConstantDeclarationImpl(varBinding, env);
                else
                    return new FieldDeclarationImpl(varBinding, env);
            case IBinding.METHOD:
                final IMethodBinding method = (IMethodBinding) binding;
                if (method.isConstructor())
                    return new ConstructorDeclarationImpl(method, env);
                final ITypeBinding declaringType = method.getDeclaringClass();
                if (declaringType != null && declaringType.isAnnotation())
                    return new AnnotationElementDeclarationImpl(method, env);
                else
                    return new MethodDeclarationImpl(method, env);
            case IBinding.PACKAGE:
                // apt also doesn't return a value
                return null;
            default:
                //$NON-NLS-1$
                throw new IllegalStateException("failed to create declaration from " + binding);
        }
    }

    public static EclipseDeclarationImpl createDeclaration(ASTNode node, IFile file, BaseProcessorEnv env) {
        if (node == null)
            return null;
        switch(node.getNodeType()) {
            case ASTNode.SINGLE_VARIABLE_DECLARATION:
                return new SourceParameterDeclarationImpl((SingleVariableDeclaration) node, file, env);
            case ASTNode.VARIABLE_DECLARATION_FRAGMENT:
                return new ASTBasedFieldDeclarationImpl((VariableDeclarationFragment) node, file, env);
            case ASTNode.METHOD_DECLARATION:
                final org.eclipse.jdt.core.dom.MethodDeclaration methodDecl = (org.eclipse.jdt.core.dom.MethodDeclaration) node;
                if (methodDecl.isConstructor())
                    return new ASTBasedConstructorDeclarationImpl(methodDecl, file, env);
                else
                    return new ASTBasedMethodDeclarationImpl(methodDecl, file, env);
            case ASTNode.ANNOTATION_TYPE_MEMBER_DECLARATION:
                return new ASTBasedMethodDeclarationImpl((AnnotationTypeMemberDeclaration) node, file, env);
            default:
                throw new UnsupportedOperationException(//$NON-NLS-1$
                "cannot create mirror type from " + node.getClass().getName());
        }
    }

    public static EclipseMirrorType createTypeMirror(ITypeBinding binding, BaseProcessorEnv env) {
        if (binding == null)
            return null;
        if (binding.isPrimitive()) {
            if (//$NON-NLS-1$
            "int".equals(binding.getName()))
                return env.getIntType();
            else if (//$NON-NLS-1$
            "byte".equals(binding.getName()))
                return env.getByteType();
            else if (//$NON-NLS-1$
            "short".equals(binding.getName()))
                return env.getShortType();
            else if (//$NON-NLS-1$
            "char".equals(binding.getName()))
                return env.getCharType();
            else if (//$NON-NLS-1$
            "long".equals(binding.getName()))
                return env.getLongType();
            else if (//$NON-NLS-1$
            "float".equals(binding.getName()))
                return env.getFloatType();
            else if (//$NON-NLS-1$
            "double".equals(binding.getName()))
                return env.getDoubleType();
            else if (//$NON-NLS-1$
            "boolean".equals(binding.getName()))
                return env.getBooleanType();
            else if (//$NON-NLS-1$
            "void".equals(binding.getName()))
                return env.getVoidType();
            else
                throw new //$NON-NLS-1$
                IllegalStateException(//$NON-NLS-1$
                "unrecognized primitive type: " + binding);
        } else if (binding.isArray())
            return new ArrayTypeImpl(binding, env);
        else if (binding.isWildcardType()) {
            return new WildcardTypeImpl(binding, env);
        } else if (binding.isTypeVariable())
            return new TypeParameterDeclarationImpl(binding, env);
        else
            return createReferenceType(binding, env);
    }

    public static ParameterDeclaration createParameterDeclaration(final SingleVariableDeclaration param, final IFile file, final BaseProcessorEnv env) {
        return new SourceParameterDeclarationImpl(param, file, env);
    }

    public static ParameterDeclaration createParameterDeclaration(final ExecutableDeclarationImpl exec, final int paramIndex, final ITypeBinding type, final BaseProcessorEnv env) {
        return new BinaryParameterDeclarationImpl(exec, type, paramIndex, env);
    }

    /**
     * @param annotation the ast node.
     * @param annotated the declaration that <code>annotation</code> annotated
     * @param env
     * @return a newly created {@link AnnotationMirror} object
     */
    public static AnnotationMirror createAnnotationMirror(final IAnnotationBinding annotation, final EclipseDeclarationImpl annotated, final BaseProcessorEnv env) {
        return new AnnotationMirrorImpl(annotation, annotated, env);
    }

    public static AnnotationValue createDefaultValue(Object domValue, AnnotationElementDeclarationImpl decl, BaseProcessorEnv env) {
        if (domValue == null)
            return null;
        final Object converted = convertDOMValueToMirrorValue(domValue, null, decl, decl, env, decl.getReturnType());
        return createAnnotationValueFromDOMValue(converted, null, -1, decl, env);
    }

    /**
	 * Build an {@link AnnotationValue} object based on the given dom value.
	 * @param domValue default value according to the DOM API.
	 * @param decl the element declaration whose default value is <code>domValue</code>
	 * 			   if domValue is an annotation, then this is the declaration it annotated. 
	 * 			   In all other case, this parameter is ignored.
	 * @param env 
	 * @return an annotation value
	 */
    public static AnnotationValue createDefaultValue(Object domValue, ASTBasedAnnotationElementDeclarationImpl decl, BaseProcessorEnv env) {
        if (domValue == null)
            return null;
        final Object converted = convertDOMValueToMirrorValue(domValue, null, decl, decl, env, decl.getReturnType());
        return createAnnotationValueFromDOMValue(converted, null, -1, decl, env);
    }

    /**
	 * Build an {@link AnnotationValue} object based on the given dom value.
	 * @param domValue annotation member value according to the DOM API.
	 * @param elementName the name of the member value
	 * @param anno the annotation that directly contains <code>domValue</code>	
	 * @param env 
	 * @return an annotation value
	 */
    public static AnnotationValue createAnnotationMemberValue(Object domValue, String elementName, AnnotationMirrorImpl anno, BaseProcessorEnv env, TypeMirror expectedType) {
        if (domValue == null)
            return null;
        final Object converted = convertDOMValueToMirrorValue(domValue, elementName, anno, anno.getAnnotatedDeclaration(), env, expectedType);
        return createAnnotationValueFromDOMValue(converted, elementName, -1, anno, env);
    }

    /**
	 * @param convertedValue value in mirror form.
	 * @param name the name of the annotation member or null for default value 
	 * @param index the number indicate the source order of the annotation member value 
	 *              in the annotation instance.
	 * @param mirror either {@link AnnotationMirrorImpl } or {@link AnnotationElementDeclarationImpl}
	 * @param env
	 */
    public static AnnotationValue createAnnotationValueFromDOMValue(Object convertedValue, String name, int index, EclipseMirrorObject mirror, BaseProcessorEnv env) {
        if (convertedValue == null)
            return null;
        if (mirror instanceof AnnotationMirrorImpl)
            return new AnnotationValueImpl(convertedValue, name, index, (AnnotationMirrorImpl) mirror, env);
        else
            return new AnnotationValueImpl(convertedValue, index, (AnnotationElementDeclarationImpl) mirror, env);
    }

    /**
     * Building an annotation value object based on the dom value.
     * 
     * @param dom the dom value to convert to the mirror specification.      
     * @see com.sun.mirror.declaration.AnnotationValue#getObject()
     * @param name the name of the element if <code>domValue</code> is an 
     * element member value of an annotation
     * @param parent the parent of this annotation value.
     * @param decl if <code>domValue</code> is a default value, then this is the 
     * annotation element declaration where the default value originates
     * if <code>domValue</code> is an annotation, then <code>decl</code>
     * is the declaration that it annotates.
     * @param expectedType the declared type of the member value.
     * @param needBoxing <code>true</code> indicate an array should be returned. 
     * @return the converted annotation value or null if the conversion failed
     */
    private static Object convertDOMValueToMirrorValue(Object domValue, String name, EclipseMirrorObject parent, EclipseDeclarationImpl decl, BaseProcessorEnv env, TypeMirror expectedType) {
        if (domValue == null)
            return null;
        final Object returnValue;
        if (domValue instanceof Boolean || domValue instanceof Byte || domValue instanceof Character || domValue instanceof Double || domValue instanceof Float || domValue instanceof Integer || domValue instanceof Long || domValue instanceof Short || domValue instanceof String)
            returnValue = domValue;
        else if (domValue instanceof IVariableBinding) {
            returnValue = Factory.createDeclaration((IVariableBinding) domValue, env);
        } else if (domValue instanceof Object[]) {
            final Object[] elements = (Object[]) domValue;
            final int len = elements.length;
            final List<AnnotationValue> annoValues = new ArrayList(len);
            final TypeMirror leaf;
            if (expectedType instanceof ArrayType)
                leaf = ((ArrayType) expectedType).getComponentType();
            else
                // doing our best here.
                leaf = expectedType;
            for (int i = 0; i < len; i++) {
                if (elements[i] == null)
                    continue;
                else // there should be already a java compile time error
                if (elements[i] instanceof Object[])
                    return null;
                Object o = convertDOMValueToMirrorValue(elements[i], name, parent, decl, env, leaf);
                if (o == null)
                    return null;
                assert (!(o instanceof IAnnotationBinding)) : "Unexpected return value from convertDomValueToMirrorValue! o.getClass().getName() = " + o.getClass().getName();
                final AnnotationValue annoValue = createAnnotationValueFromDOMValue(o, name, i, parent, env);
                if (annoValue != null)
                    annoValues.add(annoValue);
            }
            return annoValues;
        } else // caller should have caught this case.
        if (domValue instanceof ITypeBinding)
            returnValue = Factory.createTypeMirror((ITypeBinding) domValue, env);
        else if (domValue instanceof IAnnotationBinding) {
            returnValue = Factory.createAnnotationMirror((IAnnotationBinding) domValue, decl, env);
        } else
            // should never reach this point
            throw //$NON-NLS-1$       
            new IllegalStateException("cannot build annotation value object from " + domValue);
        return performNecessaryTypeConversion(expectedType, returnValue, name, parent, env);
    }

    public static Object getMatchingDummyValue(final Class<?> expectedType) {
        if (expectedType.isPrimitive()) {
            if (expectedType == boolean.class)
                return Boolean.FALSE;
            else if (expectedType == byte.class)
                return DUMMY_BYTE;
            else if (expectedType == char.class)
                return DUMMY_CHAR;
            else if (expectedType == double.class)
                return DUMMY_DOUBLE;
            else if (expectedType == float.class)
                return DUMMY_FLOAT;
            else if (expectedType == int.class)
                return DUMMY_INTEGER;
            else if (expectedType == long.class)
                return DUMMY_LONG;
            else if (expectedType == short.class)
                return DUMMY_SHORT;
            else
                // expectedType == void.class. can this happen?
                return // anything would work
                DUMMY_INTEGER;
        } else
            return null;
    }

    /**
     * This method is designed to be invoke by the invocation handler and anywhere that requires
     * a AnnotationValue (AnnotationMirror member values and default values from anonotation member).
     * 
     * Regardless of the path, there are common primitive type conversion that needs to take place. 
     * The type conversions are respects the type widening and narrowing rules from JLS 5.1.2 and 5.1.2.
     * 
     * The only question remains is what is the type of the return value when the type conversion fails?     * 
     * When <code>avoidReflectException</code> is set to <code>true</code> 
     * Return <code>false</code> if the expected type is <code>boolean</code>
     * Return numeric 0 for all numeric primitive types and '0' for <code>char</code>
     * 
     * Otherwise:
     * Return the value unchanged. 
     *  
     * In the invocation handler case: 
     * The value returned by {@link java.lang.reflect.InvocationHandler#invoke} 
     * will be converted into the expected type by the {@link java.lang.reflect.Proxy}. 
     * If the value and the expected type does not agree, and the value is not null, 
     * a ClassCastException will be thrown. A NullPointerException will be resulted if the 
     * expected type is a primitive type and the value is null.
     * This behavior is currently causing annotation processor a lot of pain and the decision is
     * to not throw such unchecked exception. In the case where a ClassCastException or 
     * NullPointerException will be thrown return some dummy value. Otherwise, return 
     * the original value.
     * Chosen dummy values:  
     * Return <code>false</code> if the expected type is <code>boolean</code>
     * Return numeric 0 for all numeric primitive types and '0' for <code>char</code>
     * 
     * This behavior is triggered by setting <code>avoidReflectException</code> to <code>true</code>
     * 
     * Note: the new behavior deviates from what's documented in
     * {@link java.lang.reflect.InvocationHandler#invoke} and also deviates from 
     * Sun's implementation.
     *
     * see CR260743 and 260563.
     * @param value the current value from the annotation instance.
     * @param expectedType the expected type of the value.
     * 
     */
    public static Object performNecessaryPrimitiveTypeConversion(final Class<?> expectedType, final Object value, final boolean avoidReflectException) {
        //$NON-NLS-1$
        assert expectedType.isPrimitive() : "expectedType is not a primitive type: " + expectedType.getName();
        if (value == null)
            return avoidReflectException ? getMatchingDummyValue(expectedType) : null;
        // apply widening conversion based on JLS 5.1.2 and 5.1.3
        final String typeName = expectedType.getName();
        final char expectedTypeChar = typeName.charAt(0);
        final int nameLen = typeName.length();
        // narrowing byte -> char
        if (value instanceof Byte) {
            final byte b = ((Byte) value).byteValue();
            switch(expectedTypeChar) {
                case 'b':
                    if (// byte
                    nameLen == 4)
                        // exact match.
                        return value;
                    else
                        return avoidReflectException ? Boolean.FALSE : value;
                case 'c':
                    return // narrowing.
                    Character.valueOf(// narrowing.
                    (char) b);
                case 'd':
                    return // widening.
                    new Double(b);
                case 'f':
                    return // widening.
                    new Float(b);
                case 'i':
                    return // widening.
                    Integer.valueOf(// widening.
                    b);
                case 'l':
                    return // widening.
                    Long.valueOf(// widening.
                    b);
                case 's':
                    return // widening.
                    Short.valueOf(// widening.
                    b);
                default:
                    throw new //$NON-NLS-1$
                    IllegalStateException(//$NON-NLS-1$
                    "unknown type " + expectedTypeChar);
            }
        } else // narrowing short -> byte or char
        if (value instanceof Short) {
            final short s = ((Short) value).shortValue();
            switch(expectedTypeChar) {
                case 'b':
                    if (// byte
                    nameLen == 4)
                        return // narrowing.
                        Byte.valueOf(// narrowing.
                        (byte) s);
                    else
                        // completely wrong.
                        return avoidReflectException ? Boolean.FALSE : value;
                case 'c':
                    return // narrowing.
                    Character.valueOf(// narrowing.
                    (char) s);
                case 'd':
                    return // widening.
                    new Double(s);
                case 'f':
                    return // widening.
                    new Float(s);
                case 'i':
                    return // widening.
                    Integer.valueOf(// widening.
                    s);
                case 'l':
                    return // widening.
                    Long.valueOf(// widening.
                    s);
                case 's':
                    // exact match
                    return value;
                default:
                    throw new //$NON-NLS-1$
                    IllegalStateException(//$NON-NLS-1$
                    "unknown type " + expectedTypeChar);
            }
        } else // narrowing char -> byte or short
        if (value instanceof Character) {
            final char c = ((Character) value).charValue();
            switch(expectedTypeChar) {
                case 'b':
                    if (// byte
                    nameLen == 4)
                        return // narrowing.
                        Byte.valueOf(// narrowing.
                        (byte) c);
                    else
                        // completely wrong.
                        return avoidReflectException ? Boolean.FALSE : value;
                case 'c':
                    // exact match
                    return value;
                case 'd':
                    return // widening.
                    new Double(c);
                case 'f':
                    return // widening.
                    new Float(c);
                case 'i':
                    return // widening.
                    Integer.valueOf(// widening.
                    c);
                case 'l':
                    return // widening.
                    Long.valueOf(// widening.
                    c);
                case 's':
                    return // narrowing.
                    Short.valueOf(// narrowing.
                    (short) c);
                default:
                    throw new //$NON-NLS-1$
                    IllegalStateException(//$NON-NLS-1$
                    "unknown type " + expectedTypeChar);
            }
        } else // narrowing int -> byte, short, or char 
        if (value instanceof Integer) {
            final int i = ((Integer) value).intValue();
            switch(expectedTypeChar) {
                case 'b':
                    if (// byte
                    nameLen == 4)
                        return // narrowing.
                        Byte.valueOf(// narrowing.
                        (byte) i);
                    else
                        // completely wrong.
                        return avoidReflectException ? Boolean.FALSE : value;
                case 'c':
                    return // narrowing
                    Character.valueOf(// narrowing
                    (char) i);
                case 'd':
                    return // widening.
                    new Double(i);
                case 'f':
                    return // widening.
                    new Float(i);
                case 'i':
                    // exact match
                    return value;
                case 'l':
                    return // widening.
                    Long.valueOf(// widening.
                    i);
                case 's':
                    return // narrowing.
                    Short.valueOf(// narrowing.
                    (short) i);
                default:
                    throw new //$NON-NLS-1$
                    IllegalStateException(//$NON-NLS-1$
                    "unknown type " + expectedTypeChar);
            }
        } else // widening long -> float or double
        if (value instanceof Long) {
            final long l = ((Long) value).longValue();
            switch(expectedTypeChar) {
                // both byte and boolean
                case 'b':
                case 'c':
                case 'i':
                case 's':
                    // completely wrong.
                    return avoidReflectException ? getMatchingDummyValue(expectedType) : value;
                case 'd':
                    return // widening.
                    new Double(l);
                case 'f':
                    // widening.			
                    return new Float(l);
                case 'l':
                    // exact match.
                    return value;
                default:
                    throw new //$NON-NLS-1$
                    IllegalStateException(//$NON-NLS-1$
                    "unknown type " + expectedTypeChar);
            }
        } else // widening float -> double    		 
        if (value instanceof Float) {
            final float f = ((Float) value).floatValue();
            switch(expectedTypeChar) {
                // both byte and boolean
                case 'b':
                case 'c':
                case 'i':
                case 's':
                case 'l':
                    // completely wrong.
                    return avoidReflectException ? getMatchingDummyValue(expectedType) : value;
                case 'd':
                    return // widening.
                    new Double(f);
                case 'f':
                    // exact match.
                    return value;
                default:
                    throw new //$NON-NLS-1$
                    IllegalStateException(//$NON-NLS-1$
                    "unknown type " + expectedTypeChar);
            }
        } else if (value instanceof Double) {
            if (expectedTypeChar == 'd')
                // exact match
                return value;
            else {
                // completely wrong.
                return avoidReflectException ? getMatchingDummyValue(expectedType) : value;
            }
        } else if (value instanceof Boolean) {
            if (// "boolean".length() == 7
            expectedTypeChar == 'b' && nameLen == 7)
                return value;
            else
                // completely wrong.
                return avoidReflectException ? getMatchingDummyValue(expectedType) : value;
        } else
            // can't convert
            return avoidReflectException ? getMatchingDummyValue(expectedType) : value;
    }

    private static Class<?> getJavaLangClass_Primitive(final PrimitiveType primitiveType) {
        switch(primitiveType.getKind()) {
            case BOOLEAN:
                return boolean.class;
            case BYTE:
                return byte.class;
            case CHAR:
                return char.class;
            case DOUBLE:
                return double.class;
            case FLOAT:
                return float.class;
            case INT:
                return int.class;
            case LONG:
                return long.class;
            case SHORT:
                return short.class;
            default:
                //$NON-NLS-1$
                throw new IllegalStateException("unknow primitive type " + primitiveType);
        }
    }

    /**
     * Apply type conversion according to JLS 5.1.2 and 5.1.3 and / or auto-boxing.
     * @param expectedType the expected type
     * @param value the value where conversion may be applied to
     * @param name name of the member value
     * @param parent the of the annotation of the member value
     * @param env 
     * @return the value matching the expected type or itself if no conversion can be applied.
     */
    private static Object performNecessaryTypeConversion(final TypeMirror expectedType, final Object value, final String name, final EclipseMirrorObject parent, final BaseProcessorEnv env) {
        if (expectedType == null)
            return value;
        if (expectedType instanceof PrimitiveType) {
            final Class<?> primitiveClass = getJavaLangClass_Primitive((PrimitiveType) expectedType);
            return performNecessaryPrimitiveTypeConversion(primitiveClass, value, false);
        } else // handle auto-boxing
        if (expectedType instanceof ArrayType) {
            final TypeMirror componentType = ((ArrayType) expectedType).getComponentType();
            Object converted = value;
            // if it is an error case, will just leave it as is.
            if (!(componentType instanceof ArrayType))
                converted = performNecessaryTypeConversion(componentType, value, name, parent, env);
            final AnnotationValue annoValue = createAnnotationValueFromDOMValue(converted, name, 0, parent, env);
            return Collections.singletonList(annoValue);
        } else
            // no change
            return value;
    }

    public static InterfaceType createErrorInterfaceType(final ITypeBinding binding) {
        String name = null == binding ? NULL_BINDING_NAME : binding.getName();
        return new ErrorType.ErrorInterface(name);
    }

    public static ClassType createErrorClassType(final ITypeBinding binding) {
        String name = null == binding ? NULL_BINDING_NAME : binding.getName();
        return createErrorClassType(name);
    }

    public static ClassType createErrorClassType(final String name) {
        return new ErrorType.ErrorClass(name);
    }

    public static AnnotationType createErrorAnnotationType(final ITypeBinding binding) {
        String name = null == binding ? NULL_BINDING_NAME : binding.getName();
        return createErrorAnnotationType(name);
    }

    public static AnnotationType createErrorAnnotationType(String name) {
        return new ErrorType.ErrorAnnotation(name);
    }

    public static ArrayType createErrorArrayType(final String name, final int dimension) {
        return new ErrorType.ErrorArrayType(name, dimension);
    }
}
