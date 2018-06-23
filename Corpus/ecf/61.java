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
package org.eclipse.ecf.example.collab.share;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.internal.example.collab.Messages;
import org.eclipse.osgi.util.NLS;

public class SharedObjectMsg implements Serializable {

    static final long serialVersionUID = 571132189626558278L;

    public static final Object[] nullArgs = new Object[0];

    public static final Class[] nullTypes = new Class[0];

    class SenderID {

        private final ID id;

        // No instances other than ones created in SharedObjectMsg.invokeFrom/2
        protected  SenderID(ID theID) {
            id = theID;
        }

        public ID getID() {
            return id;
        }
    }

    // Static factory methods for creating SharedObjectMsg instances
    public static SharedObjectMsg createMsg(String className, String methodName, Object[] param) {
        Assert.isNotNull(methodName);
        Assert.isNotNull(param);
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
        final Object args[] = { arg };
        return createMsg(className, methodName, args);
    }

    public static SharedObjectMsg createMsg(String methodName, Object arg) {
        return createMsg((String) null, methodName, arg);
    }

    public static SharedObjectMsg createMsg(String className, String methodName, Object arg1, Object arg2) {
        final Object args[] = { arg1, arg2 };
        return createMsg(className, methodName, args);
    }

    public static SharedObjectMsg createMsg(String className, String methodName, Object arg1, Object arg2, Object arg3) {
        final Object args[] = { arg1, arg2, arg3 };
        return createMsg(className, methodName, args);
    }

    public static SharedObjectMsg createMsg(String className, String methodName, Object arg1, Object arg2, Object arg3, Object arg4) {
        final Object args[] = { arg1, arg2, arg3, arg4 };
        return createMsg(className, methodName, args);
    }

    public static SharedObjectMsg createMsg(String className, String methodName, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        final Object args[] = { arg1, arg2, arg3, arg4, arg5 };
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
    public static String getName(Class clazz) {
        return clazz.getName();
    }

    /**
	 * Get array of argument types from array of objects
	 * 
	 * @param args
	 *            the arguments to get types for
	 * @return Class[] of types for objects in given Object array
	 */
    public static Class[] getArgTypes(Object args[]) {
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
	 * parameter), and of arity defined by the third parameter. Calls
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
    static Method findMethod(final Class clazz, String meth, Class args[]) {
        Method methods[] = null;
        try {
            methods = (Method[]) AccessController.doPrivileged(new PrivilegedExceptionAction() {

                public Object run() throws Exception {
                    return clazz.getDeclaredMethods();
                }
            });
        } catch (final PrivilegedActionException e) {
            return null;
        }
        return searchForMethod(methods, meth, args);
    }

    public static Method searchForMethod(Method meths[], String meth, Class args[]) {
        // Find it from among the given set of Method objects
        for (int i = 0; i < meths.length; i++) {
            final Method test = meths[i];
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
        final Method aMethod = findMethod(clazz, meth, args);
        if (aMethod == null) {
            final Class superclazz = clazz.getSuperclass();
            if (superclazz != null)
                return findMethodRecursive(superclazz, meth, args);
            else
                return null;
        } else {
            return aMethod;
        }
    }

    /**
	 * Check a given msg to verify that all Objects in args array implement the
	 * Serializable interface.
	 * 
	 * @param aMsg
	 *            the Message to check
	 * @exception NotSerializableException
	 *                thrown if any objects in args array do not implement the
	 *                Serializable interface
	 */
    public static void checkForSerializable(SharedObjectMsg aMsg) throws NotSerializableException {
        final Object args[] = aMsg.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null && !(args[i] instanceof Serializable))
                throw new NotSerializableException(NLS.bind(Messages.SharedObjectMsg_EXCEPTION_NOT_SERIALIZABLE, new Integer(i)));
        }
    }

    // Instance fields
    /**
	 * @serial myClassName the class name for the message
	 */
    String myClassName;

    /**
	 * @serial myMethodName the method name of the message
	 */
    String myMethodName;

    /**
	 * @serial myArgs arguments
	 */
    Object[] myArgs;

    // Constructor
     SharedObjectMsg(String className, String methodName, Object[] args) {
        myClassName = className;
        myMethodName = methodName;
        myArgs = args;
    }

    /**
	 * Constructor for TypedMsg's.
	 */
     SharedObjectMsg(String methodName) {
        this(null, methodName, null);
    }

    public final String getMethodName() {
        return myMethodName;
    }

    public final void setMethodName(String name) {
        checkAlterMsg();
        if (name == null)
            throw new NullPointerException(Messages.SharedObjectMsg_EXCEPTION_METHOD_NOT_NULL);
        myMethodName = name;
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
        return myClassName;
    }

    public final void setClassName(String name) {
        checkAlterMsg();
        myClassName = name;
    }

    public // TypedMsg overrides
    Object[] getArgs() {
        return myArgs;
    }

    public final void setArgs(Object[] args) {
        checkAlterMsg();
        myArgs = (args == null) ? nullArgs : args;
    }

    protected Class[] getArgTypes() {
        return getArgTypes(getArgs());
    }

    protected final Method findMethod(Class clazz) {
        return findMethod(clazz, getMethodName(), getArgTypes());
    }

    protected final Method findMethodRecursive(Class clazz) {
        return findMethodRecursive(clazz, getMethodName(), getArgTypes());
    }

    public final Object invoke(Object target) throws Exception {
        return doInvoke(target);
    }

    // package scope for security
    Object doInvoke(// package scope for security
    final Object target) throws Exception {
        if (target == null)
            throw new NoSuchMethodException(Messages.SharedObjectMsg_EXCEPTION_NULL_TARGET);
        Method meth = null;
        if (myClassName == null) {
            // If not specific class is specified by SharedObjectMsg instance,
            // then use the target's class
            meth = findMethodRecursive(target.getClass());
        } else {
            // If it is specified, then load the specified class, using the
            // target object's classloader
            meth = findMethod(getClass(target.getClass().getClassLoader(), myClassName));
        }
        // If no method was found, then throw
        if (meth == null)
            throw new NoSuchMethodException(getMethodName());
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
        return toCall.invoke(target, getArgs());
    }

    public final Object invokeFrom(ID fromID, final Object target) throws Exception {
        // Setup new array of arguments with the fromID on the front
        Object[] newParams = null;
        final SenderID sender = new SenderID(fromID);
        final Object args[] = getArgs();
        if (args == null) {
            newParams = new Object[1];
            newParams[0] = sender;
        } else {
            newParams = new Object[myArgs.length + 1];
            newParams[0] = sender;
            System.arraycopy(args, 0, newParams, 1, args.length);
        }
        // Reset arguments before method search
        myArgs = newParams;
        // Now just call invoke/1
        return invoke(target);
    }

    public String toString() {
        final StringBuffer sb = new StringBuffer();
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        sb.append("SharedObjectMsg[").append(myClassName).append(":").append(myMethodName).append("(");
        if (myArgs == null) {
            sb.append(myArgs);
        } else {
            for (int i = 0; i < myArgs.length; i++) {
                if (i > 0)
                    //$NON-NLS-1$
                    sb.append(",");
                sb.append(myArgs[i]);
            }
        }
        //$NON-NLS-1$
        sb.append(")]");
        return sb.toString();
    }
}
