/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.debug.eval.ast.instructions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.jdt.debug.core.IJavaArrayType;
import org.eclipse.jdt.debug.core.IJavaClassObject;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaFieldVariable;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaReferenceType;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.debug.core.JDIDebugPlugin;
import org.eclipse.jdt.internal.debug.eval.ast.engine.IRuntimeContext;
import org.eclipse.jdt.internal.debug.eval.ast.engine.Interpreter;
import org.eclipse.osgi.util.NLS;

/**
 * Common behavior for instructions.
 */
public abstract class Instruction {

    private Interpreter fInterpreter;

    public abstract int getSize();

    public void setInterpreter(Interpreter interpreter) {
        fInterpreter = interpreter;
    }

    public void setLastValue(IJavaValue value) {
        fInterpreter.setLastValue(value);
    }

    public void stop() {
        fInterpreter.stop();
    }

    public static int getBinaryPromotionType(int left, int right) {
        return fTypeTable[left][right];
    }

    public abstract void execute() throws CoreException;

    protected IRuntimeContext getContext() {
        return fInterpreter.getContext();
    }

    protected IJavaDebugTarget getVM() {
        return getContext().getVM();
    }

    /**
	 * Return the internal variable with the given name.
	 * 
	 * @see Interpreter#getInternalVariable(String)
	 */
    protected IVariable getInternalVariable(String name) {
        return fInterpreter.getInternalVariable(name);
    }

    /**
	 * Create and return a new internal variable with the given name and the
	 * given type.
	 * 
	 * @see Interpreter#createInternalVariable(String, String)
	 */
    protected IVariable createInternalVariable(String name, IJavaType referencType) {
        return fInterpreter.createInternalVariable(name, referencType);
    }

    /**
	 * Answers the instance of Class that the given type represents.
	 */
    protected IJavaObject getClassObject(IJavaType type) throws CoreException {
        if (type instanceof IJavaReferenceType) {
            return ((IJavaReferenceType) type).getClassObject();
        }
        return null;
    }

    protected void jump(int offset) {
        fInterpreter.jump(offset);
    }

    protected void push(Object object) {
        fInterpreter.push(object);
    }

    protected Object pop() {
        return fInterpreter.pop();
    }

    protected IJavaValue popValue() throws CoreException {
        Object element = fInterpreter.pop();
        if (element instanceof IJavaVariable) {
            return (IJavaValue) ((IJavaVariable) element).getValue();
        }
        return (IJavaValue) element;
    }

    protected void pushNewValue(boolean value) {
        fInterpreter.push(newValue(value));
    }

    protected IJavaValue newValue(boolean value) {
        return getVM().newValue(value);
    }

    protected void pushNewValue(byte value) {
        fInterpreter.push(newValue(value));
    }

    protected IJavaValue newValue(byte value) {
        return getVM().newValue(value);
    }

    protected void pushNewValue(short value) {
        fInterpreter.push(newValue(value));
    }

    protected IJavaValue newValue(short value) {
        return getVM().newValue(value);
    }

    protected void pushNewValue(int value) {
        fInterpreter.push(newValue(value));
    }

    protected IJavaValue newValue(int value) {
        return getVM().newValue(value);
    }

    protected void pushNewValue(long value) {
        fInterpreter.push(newValue(value));
    }

    protected IJavaValue newValue(long value) {
        return getVM().newValue(value);
    }

    protected void pushNewValue(char value) {
        fInterpreter.push(newValue(value));
    }

    protected IJavaValue newValue(char value) {
        return getVM().newValue(value);
    }

    protected void pushNewValue(float value) {
        fInterpreter.push(newValue(value));
    }

    protected IJavaValue newValue(float value) {
        return getVM().newValue(value);
    }

    protected void pushNewValue(double value) {
        fInterpreter.push(newValue(value));
    }

    protected IJavaValue newValue(double value) {
        return getVM().newValue(value);
    }

    protected void pushNewValue(String value) {
        fInterpreter.push(newValue(value));
    }

    protected IJavaValue newValue(String value) {
        return getVM().newValue(value);
    }

    protected void pushNullValue() {
        fInterpreter.push(nullValue());
    }

    protected IJavaValue nullValue() {
        return getVM().nullValue();
    }

    public static int getUnaryPromotionType(int typeId) {
        return fTypeTable[typeId][T_int];
    }

    protected IJavaType getType(String qualifiedName) throws CoreException {
        // Force the class to be loaded, and record the class reference
        // for later use if there are multiple classes with the same name.
        IJavaClassObject classReference = getContext().classForName(qualifiedName);
        // Found many classes, look for the right one for this scope.
        if (classReference == null) {
            throw new CoreException(new Status(IStatus.ERROR, JDIDebugPlugin.getUniqueIdentifier(), IStatus.OK, NLS.bind(InstructionsEvaluationMessages.Instruction_No_type, new String[] { qualifiedName }), null));
        }
        return classReference.getInstanceType();
    }

    /**
	 * Returns the primitive type with the given name.
	 * 
	 * @param name
	 *            type name, for example - "int"
	 * @return primitive type
	 * @throws CoreException
	 */
    protected IJavaType getPrimitiveType(String name) throws CoreException {
        IJavaReferenceType type = null;
        if (//$NON-NLS-1$
        "boolean".equals(name)) {
            //$NON-NLS-1$
            type = (IJavaReferenceType) getType("java.lang.Boolean");
        } else if (//$NON-NLS-1$
        "byte".equals(name)) {
            //$NON-NLS-1$
            type = (IJavaReferenceType) getType("java.lang.Byte");
        } else if (//$NON-NLS-1$
        "char".equals(name)) {
            //$NON-NLS-1$
            type = (IJavaReferenceType) getType("java.lang.Character");
        } else if (//$NON-NLS-1$
        "double".equals(name)) {
            //$NON-NLS-1$
            type = (IJavaReferenceType) getType("java.lang.Double");
        } else if (//$NON-NLS-1$
        "float".equals(name)) {
            //$NON-NLS-1$
            type = (IJavaReferenceType) getType("java.lang.Float");
        } else if (//$NON-NLS-1$
        "int".equals(name)) {
            //$NON-NLS-1$
            type = (IJavaReferenceType) getType("java.lang.Integer");
        } else if (//$NON-NLS-1$
        "long".equals(name)) {
            //$NON-NLS-1$
            type = (IJavaReferenceType) getType("java.lang.Long");
        } else if (//$NON-NLS-1$
        "short".equals(name)) {
            //$NON-NLS-1$
            type = (IJavaReferenceType) getType("java.lang.Short");
        } else if (//$NON-NLS-1$
        "void".equals(name)) {
            //$NON-NLS-1$
            type = (IJavaReferenceType) getType("java.lang.Void");
        }
        if (type != null) {
            //$NON-NLS-1$
            IJavaFieldVariable field = type.getField("TYPE");
            IJavaClassObject clazz = (IJavaClassObject) field.getValue();
            return clazz.getInstanceType();
        }
        throw new CoreException(new Status(IStatus.ERROR, JDIDebugPlugin.getUniqueIdentifier(), IStatus.OK, NLS.bind(InstructionsEvaluationMessages.Instruction_No_type, new String[] { name }), null));
    }

    protected IJavaArrayType getArrayType(String typeSignature, int dimension) throws CoreException {
        String qualifiedName = RuntimeSignature.toString(typeSignature);
        //$NON-NLS-1$
        String braces = "";
        for (int i = 0; i < dimension; i++) {
            //$NON-NLS-1$
            qualifiedName += "[]";
            //$NON-NLS-1$
            braces += "[";
        }
        String signature = braces + typeSignature;
        // Force the class to be loaded, and record the class reference
        // for later use if there are multiple classes with the same name.
        IJavaObject classReference = getContext().classForName(signature);
        if (classReference == null) {
            throw new CoreException(new Status(IStatus.ERROR, JDIDebugPlugin.getUniqueIdentifier(), IStatus.OK, NLS.bind(InstructionsEvaluationMessages.Instruction_No_type, new String[] { qualifiedName }), null));
        }
        IJavaType[] types = getVM().getJavaTypes(qualifiedName);
        checkTypes(types, qualifiedName);
        if (types.length == 1) {
            // Found only one class.
            return (IJavaArrayType) types[0];
        }
        // Found many classes, look for the right one for this scope.
        for (IJavaType type : types) {
            if (classReference.equals(getClassObject(type))) {
                return (IJavaArrayType) type;
            }
        }
        throw new CoreException(new Status(IStatus.ERROR, JDIDebugPlugin.getUniqueIdentifier(), IStatus.OK, NLS.bind(InstructionsEvaluationMessages.Instruction_No_type, new String[] { qualifiedName }), null));
    }

    protected void checkTypes(IJavaType[] types, String qualifiedName) throws CoreException {
        if (types == null || types.length == 0) {
            throw new CoreException(new Status(IStatus.ERROR, JDIDebugPlugin.getUniqueIdentifier(), IStatus.OK, NLS.bind(InstructionsEvaluationMessages.Instruction_No_type, new String[] { qualifiedName }), null));
        }
    }

    public static final int T_undefined = 0;

    public static final int T_Object = 1;

    public static final int T_char = 2;

    public static final int T_byte = 3;

    public static final int T_short = 4;

    public static final int T_boolean = 5;

    public static final int T_void = 6;

    public static final int T_long = 7;

    public static final int T_double = 8;

    public static final int T_float = 9;

    public static final int T_int = 10;

    public static final int T_String = 11;

    public static final int T_null = 12;

    private static final int[][] fTypeTable = { /* undefined */
    { T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined }, /* object */
    { T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_String, T_undefined }, /* char */
    { T_undefined, T_undefined, T_int, T_int, T_int, T_undefined, T_undefined, T_long, T_double, T_float, T_int, T_String, T_undefined }, /* byte */
    { T_undefined, T_undefined, T_int, T_int, T_int, T_undefined, T_undefined, T_long, T_double, T_float, T_int, T_String, T_undefined }, /* short */
    { T_undefined, T_undefined, T_int, T_int, T_int, T_undefined, T_undefined, T_long, T_double, T_float, T_int, T_String, T_undefined }, /* boolean */
    { T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_boolean, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_String, T_undefined }, /* void */
    { T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined }, /* long */
    { T_undefined, T_undefined, T_long, T_long, T_long, T_undefined, T_undefined, T_long, T_double, T_float, T_long, T_String, T_undefined }, /* double */
    { T_undefined, T_undefined, T_double, T_double, T_double, T_undefined, T_undefined, T_double, T_double, T_double, T_double, T_String, T_undefined }, /* float */
    { T_undefined, T_undefined, T_float, T_float, T_float, T_undefined, T_undefined, T_float, T_double, T_float, T_float, T_String, T_undefined }, /* int */
    { T_undefined, T_undefined, T_int, T_int, T_int, T_undefined, T_undefined, T_long, T_double, T_float, T_int, T_String, T_undefined }, /* String */
    { T_undefined, T_String, T_String, T_String, T_String, T_String, T_undefined, T_String, T_String, T_String, T_String, T_String, T_String }, /* null */
    { T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_undefined, T_String, T_undefined } };
}
