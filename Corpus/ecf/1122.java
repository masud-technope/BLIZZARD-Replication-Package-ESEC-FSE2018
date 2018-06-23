/*******************************************************************************
 * Copyright (c) 2010 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.util.reflection;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @since 3.3
 */
@SuppressWarnings("unchecked")
public class ClassUtil {

    private static Map convertor = new HashMap();

    static {
        convertor.put(boolean.class, Boolean.class);
        convertor.put(byte.class, Byte.class);
        convertor.put(char.class, Character.class);
        convertor.put(double.class, Double.class);
        convertor.put(float.class, Float.class);
        convertor.put(int.class, Integer.class);
        convertor.put(long.class, Long.class);
        convertor.put(short.class, Short.class);
    }

    /**
	 * @param aClass The Class providing method under question (Must not be null)
	 * @param aMethodName The method name to search for (Must not be null)
	 * @param someParameterTypes Method arguments (May be null or parameters)
	 * @return A match. If more than one method matched (due to overloading) an arbitrary match is taken
	 * @throws NoSuchMethodException If a match cannot be found
	 */
    public static Method getMethod(final Class aClass, String aMethodName, final Class[] someParameterTypes) throws NoSuchMethodException {
        // no args makes matching simple
        if (someParameterTypes == null || someParameterTypes.length == 0) {
            return aClass.getMethod(aMethodName, (Class[]) null);
        }
        return getMethod(aClass.getMethods(), aMethodName, someParameterTypes);
    }

    /**
	 * @param aClass The Class providing method under question (Must not be null)
	 * @param aMethodName The method name to search for (Must not be null)
	 * @param someParameterTypes Method arguments (May be null or parameters)
	 * @return A match. If more than one method matched (due to overloading) an arbitrary match is taken
	 * @throws NoSuchMethodException If a match cannot be found
	 */
    public static Method getDeclaredMethod(final Class aClass, String aMethodName, final Class[] someParameterTypes) throws NoSuchMethodException {
        // no args makes matching simple
        if (someParameterTypes == null || someParameterTypes.length == 0) {
            return aClass.getDeclaredMethod(aMethodName, (Class[]) null);
        }
        return getMethod(aClass.getDeclaredMethods(), aMethodName, someParameterTypes);
    }

    private static Method getMethod(final Method[] candidates, String aMethodName, final Class[] someParameterTypes) throws NoSuchMethodException {
        // match parameters to determine callee
        final int parameterCount = someParameterTypes.length;
        aMethodName = aMethodName.intern();
        final TreeSet matches = new TreeSet(new MethodComparator(someParameterTypes));
        OUTER: for (int i = 0; i < candidates.length; i++) {
            final Method candidate = candidates[i];
            final String candidateMethodName = candidate.getName().intern();
            final Class[] candidateParameterTypes = candidate.getParameterTypes();
            final int candidateParameterCount = candidateParameterTypes.length;
            if (candidateParameterCount == parameterCount && aMethodName == candidateMethodName) {
                for (int j = 0; j < candidateParameterCount; j++) {
                    final Class clazzA = candidateParameterTypes[j];
                    final Class clazzB = someParameterTypes[j];
                    if (clazzB != null && !isAssignableFrom(clazzA, clazzB)) {
                        continue OUTER;
                    }
                }
                matches.add(candidate);
            }
        }
        // if no match has been found, fail with NSME
        if (matches.size() == 0) {
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            throw new NoSuchMethodException("No such method: " + aMethodName + "(" + Arrays.asList(someParameterTypes) + ")");
        }
        return (Method) matches.first();
    }

    // extends Class.isAssingable(Class) with autoboxing
    private static boolean isAssignableFrom(Class clazzA, Class clazzB) {
        if (!(clazzA.isPrimitive() ^ clazzB.isPrimitive())) {
            return clazzA.isAssignableFrom(clazzB);
        } else if (clazzA.isPrimitive()) {
            final Class oClazzA = (Class) convertor.get(clazzA);
            return oClazzA.isAssignableFrom(clazzB);
        } else {
            final Class oClazzB = (Class) convertor.get(clazzB);
            return clazzA.isAssignableFrom(oClazzB);
        }
    }

    private static class MethodComparator implements Comparator {

        private final Class[] parameterTypes;

        public  MethodComparator(Class[] someParameterTypes) {
            parameterTypes = someParameterTypes;
        }

        public int compare(Object object1, Object object2) {
            final Class[] pt1 = ((Method) object1).getParameterTypes();
            final Class[] pt2 = ((Method) object2).getParameterTypes();
            if (Arrays.equals(pt1, pt2)) {
                return 0;
            } else if (Arrays.equals(parameterTypes, pt1)) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
