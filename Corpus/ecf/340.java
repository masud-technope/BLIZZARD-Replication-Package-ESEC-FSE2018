/*******************************************************************************
 * Copyright (c) 2008 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Chi Jian Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservices.ui;

import java.lang.reflect.Method;

class RemoteMethod {

    private final Method method;

    private final Parameter[] parameters;

    private final Class[] parameterTypes;

     RemoteMethod(Method method) {
        this.method = method;
        parameterTypes = method.getParameterTypes();
        parameters = new Parameter[parameterTypes.length];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = new Parameter(parameterTypes[i]);
        }
    }

    public String[] getParameterTypes() {
        final String[] types = new String[parameterTypes.length];
        for (int i = 0; i < types.length; i++) {
            final String name = parameterTypes[i].getName();
            if (name.charAt(0) == 'j') {
                //$NON-NLS-1$
                types[i] = //$NON-NLS-1$
                "String";
            } else {
                types[i] = name;
            }
        }
        return types;
    }

    Method getMethod() {
        return method;
    }

    Parameter[] getParameters() {
        return parameters;
    }

    public String getReturnType() {
        String name = method.getReturnType().getName();
        // Fix array types
        if (//$NON-NLS-1$
        name.startsWith("[L")) {
            //$NON-NLS-1$
            name = name.substring(2, name.length() - 1).concat("[]");
        }
        final int index = name.lastIndexOf('.');
        if (index != -1) {
            name = name.substring(index + 1);
        }
        name = name.replace('$', '.');
        return name;
    }

    public String getSignature() {
        final StringBuffer buffer = new StringBuffer(method.getName());
        synchronized (buffer) {
            buffer.append('(');
            final String[] types = getParameterTypes();
            if (types.length != 0) {
                for (int i = 0; i < types.length; i++) {
                    //$NON-NLS-1$
                    buffer.append(types[i]).append(//$NON-NLS-1$
                    ", ");
                }
                buffer.delete(buffer.length() - 2, buffer.length());
            }
            buffer.append(')');
            return buffer.toString();
        }
    }

    class Parameter {

        private final Class parameter;

        //$NON-NLS-1$
        private String argument = "";

         Parameter(Class parameter) {
            this.parameter = parameter;
        }

        void setArgument(String argument) {
            this.argument = argument;
        }

        String getArgument() {
            return argument;
        }

        Class getParameter() {
            return parameter;
        }
    }
}
