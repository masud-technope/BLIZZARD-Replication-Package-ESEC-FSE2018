/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.core.sharedobject;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import org.eclipse.ecf.core.util.reflection.ClassUtil;

/**
 * Shared Object Message. Instances may be used for sending/receiving messages
 * among shared object replicas. This class can be used to create
 * SharedObjectMsg instances via one of {@link #createMsg(String)} methods.
 */
public class SharedObjectMsg implements Serializable {

    private static final long serialVersionUID = -8761203700888592267L;

    public static final Object[] nullArgs = new Object[0];

    public static final Class[] nullTypes = new Class[0];

    protected  SharedObjectMsg() {
    // null constructor
    }

    // Static factory methods for creating SharedObjectMsg instances
    public static SharedObjectMsg createMsg(String className, String methodName, Object[] param) {
        if (methodName == null || param == null) {
            //$NON-NLS-1$
            throw new NullPointerException("SharedObjectMsg method name and parameters must not be null");
        }
        return new SharedObjectMsg(className, methodName, param);
    }

    public static SharedObjectMsg createMsg(String methodName, Object[] param) {
        return createMsg((String) null, methodName, param);
    }

    public static SharedObjectMsg createMsg(String methodName) {
        return createMsg((String) null, methodName, nullArgs);
    }

    public static SharedObjectMsg createMsg(String className, String methodName) {
        return createMsg(className, methodName, nullArgs);
    }

    public static SharedObjectMsg createMsg(String className, String methodName, Object arg) {
        Object args[] = { arg };
        return createMsg(className, methodName, args);
    }

    public static SharedObjectMsg createMsg(String methodName, Object arg) {
        return createMsg((String) null, methodName, arg);
    }

    public static SharedObjectMsg createMsg(String className, String methodName, Object arg1, Object arg2) {
        Object args[] = { arg1, arg2 };
        return createMsg(className, methodName, args);
    }

    public static SharedObjectMsg createMsg(String className, String methodName, Object arg1, Object arg2, Object arg3) {
        Object args[] = { arg1, arg2, arg3 };
        return createMsg(className, methodName, args);
    }

    public static SharedObjectMsg createMsg(String className, String methodName, Object arg1, Object arg2, Object arg3, Object arg4) {
        Object args[] = { arg1, arg2, arg3, arg4 };
        return createMsg(className, methodName, args);
    }

    public static SharedObjectMsg createMsg(String className, String methodName, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        Object args[] = { arg1, arg2, arg3, arg4, arg5 };
        return createMsg(className, methodName, args);
    }

    /**
	 * Utility for getting a Class instance from a String class name. Calls
	 * Class.forName().
	 * 
	 * @param loader
	 *            the ClassLoader to use to load the given class
	 * @param name
	 *            of Class to load
	 * @return Class instance found. If not found, a ClassNotFoundException is
	 *         thrown
	 * @exception ClassNotFoundException
	 *                thrown if specified class is not found
	 */
    public static Class getClass(ClassLoader loader, String name) throws ClassNotFoundException {
        if (name == null)
            return null;
        return Class.forName(name, true, loader);
    }

    /**
	 * Get name for given class.
	 * 
	 * @param clazz
	 *            the Class to retrieve the name from
	 * @return String name of given class
	 */
    public static String getNameForClass(Class clazz) {
        return clazz.getName();
    }

    /**
	 * Get array of argument types from array of objects
	 * 
	 * @param args
	 *            the arguments to get types for
	 * @return Class[] of types for objects in given Object array
	 */
    public static Class[] getTypesForParameters(Object args[]) {
        Class argTypes[] = null;
        if (args == null || args.length == 0)
            argTypes = nullTypes;
        else {
            argTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                if (args[i] == null)
                    argTypes[i] = null;
                else
                    argTypes[i] = args[i].getClass();
            }
        }
        return argTypes;
    }

    /**
	 * Find a Method instance on given class. This method searches for a method
	 * on the given class (first parameter), of the given name (second
	 * parameter), with arity defined by the third parameter. Calls
	 * searchForMethod to actually do the searching.
	 * 
	 * @param clazz
	 *            the Class to look on
	 * @param meth
	 *            the method name to look for
	 * @param args
	 *            the arguments that will be passed to the method on the invoke
	 *            call
	 * @return Method instance found on given class. Null if none found.
	 */
    public static Method findMethod(final Class clazz, String meth, Class args[]) {
        try {
            return ClassUtil.getDeclaredMethod(clazz, meth, args);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static Method searchForMethod(Method meths[], String meth, Class args[]) {
        // Find it from among the given set of Method objects
        for (int i = 0; i < meths.length; i++) {
            Method test = meths[i];
            if (test.getName().equals(meth)) {
                if (test.getParameterTypes().length == args.length)
                    return test;
            }
        }
        return null;
    }

    /**
	 * Find a Method instance on given class, and recursively search the class'
	 * superclass tree for given method.
	 * 
	 * @param clazz
	 *            the Class to look upon
	 * @param meth
	 *            the String name of the method to look for
	 * @param args
	 *            the array of Object arguments that will be passed to the
	 *            method for execution
	 * @return Method instance if found, null if not found
	 */
    public static Method findMethodRecursive(Class clazz, String meth, Class args[]) {
        Method aMethod = findMethod(clazz, meth, args);
        if (aMethod == null) {
            Class superclazz = clazz.getSuperclass();
            if (superclazz != null)
                return findMethodRecursive(superclazz, meth, args);
            return null;
        }
        return aMethod;
    }

    /**
	 * Check a given msg to verify that all Objects in args array implement the
	 * Serializable interface.
	 * 
	 * @param aMsg
	 *            the Message to check
	 * @exception NotSerializableException
	 *                thrown if any objects in args array do not implement
	 *                java.io.Serializable
	 */
    public static void checkForSerializable(SharedObjectMsg aMsg) throws NotSerializableException {
        Object args[] = aMsg.getParameters();
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null && !(args[i] instanceof Serializable))
                //$NON-NLS-1$ //$NON-NLS-2$
                throw new NotSerializableException("SharedObjectMsg parameter " + i + " not serializable");
        }
    }

    // Instance fields
    /**
	 * @serial clazz the class name for the message
	 */
    protected String clazz;

    /**
	 * @serial method the method name of the message
	 */
    protected String method;

    /**
	 * @serial args arguments
	 */
    protected Object[] parameters;

    // Constructor
    protected  SharedObjectMsg(String className, String methodName, Object[] parameters) {
        this.clazz = className;
        this.method = methodName;
        this.parameters = parameters;
    }

    public final String getMethod() {
        return method;
    }

    public final void setMethod(String name) {
        checkAlterMsg();
        if (name == null)
            //$NON-NLS-1$
            throw new NullPointerException("SharedObjectMsg method cannot be null");
        method = name;
    }

    /**
	 * Check if it is permitted to alter the state of this message (args, class
	 * name, method name). Default: NOP; subclasses should override as
	 * appropriate. To disallow, throw a java.lang.RuntimeException.
	 */
    protected void checkAlterMsg() {
    // NOP; subclasses should override as appropriate
    }

    public final String getClassName() {
        return clazz;
    }

    public final void setClassName(String name) {
        checkAlterMsg();
        clazz = name;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public final void setParameters(Object[] args) {
        checkAlterMsg();
        args = (args == null) ? nullArgs : args;
    }

    protected Class[] getParameterTypes() {
        return getTypesForParameters(getParameters());
    }

    protected final Method findMethod(Class clazz1) {
        return findMethod(clazz1, getMethod(), getParameterTypes());
    }

    protected final Method findMethodRecursive(Class clazz1) {
        return findMethodRecursive(clazz1, getMethod(), getParameterTypes());
    }

    public final Object invoke(Object target) throws Exception {
        return doInvoke(target);
    }

    @SuppressWarnings("unchecked")
    // package scope for security
    Object doInvoke(// package scope for security
    final Object target) throws Exception {
        if (target == null)
            //$NON-NLS-1$
            throw new NoSuchMethodException("doInvoke target method cannot be null");
        Method meth = null;
        if (clazz == null) {
            // If not specific class is specified by SharedObjectMsg instance,
            // then use the target's class
            meth = findMethodRecursive(target.getClass());
        } else {
            // If it is specified, then load the specified class, using the
            // target object's classloader
            meth = findMethod(getClass(target.getClass().getClassLoader(), clazz));
        }
        // If no method was found, then throw
        if (meth == null) {
            Class[] someParameterTypes = getParameterTypes();
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            throw new NoSuchMethodException("No such method: " + getMethod() + "(" + ((someParameterTypes == null) ? "" : Arrays.asList(someParameterTypes).toString()) + ")");
        }
        final Method toCall = meth;
        // Make priveleged call to set the method as accessible
        AccessController.doPrivileged(new PrivilegedExceptionAction() {

            public Object run() throws Exception {
                if (!toCall.isAccessible())
                    toCall.setAccessible(true);
                return null;
            }
        });
        // Actually invoke method
        return toCall.invoke(target, getParameters());
    }

    public String toString() {
        //$NON-NLS-1$
        StringBuffer sb = new StringBuffer("SharedObjectMsg[");
        //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("class=").append(clazz).append(";");
        //$NON-NLS-1$
        sb.append("method=").append(method).append(//$NON-NLS-1$
        ";");
        //$NON-NLS-1$ //$NON-NLS-2$
        sb.append("params=").append(Arrays.asList(parameters)).append("]");
        return sb.toString();
    }
}
