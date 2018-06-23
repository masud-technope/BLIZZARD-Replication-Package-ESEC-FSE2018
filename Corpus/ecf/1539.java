/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.util;

import java.text.SimpleDateFormat;
import java.util.*;
import org.eclipse.ecf.internal.core.identity.Activator;
import org.eclipse.osgi.service.debug.DebugOptions;

/**
 * A utility for tracing debug information. Provides a simple interface for
 * filtering and generating trace output.
 * 
 */
public class Trace {

    private static final boolean TRACEALL = new Boolean(System.getProperty("org.eclipse.ecf.core.util.traceAll", "false"));

    private static final List<String> TRACE_BUNDLES = new ArrayList<String>();

    static {
        if (!TRACEALL) {
            String tracePlugins = System.getProperty("org.eclipse.ecf.core.util.traceBundles");
            if (tracePlugins != null)
                try {
                    String[] plugins = tracePlugins.split(",");
                    for (String s : plugins) TRACE_BUNDLES.add(s);
                } catch (Exception e) {
                    System.err.println("Unexpected exception in Trace class static initializer");
                    e.printStackTrace(System.err);
                }
        }
    }

    /**
	 * private constructor for the static class.
	 */
    private  Trace() {
        super();
    }

    /**
	 * String containing an open parenthesis.
	 * 
	 */
    //$NON-NLS-1$
    protected static final String PARENTHESIS_OPEN = "(";

    /**
	 * String containing a close parenthesis.
	 * 
	 */
    //$NON-NLS-1$
    protected static final String PARENTHESIS_CLOSE = ")";

    /**
	 * String containing TRACE
	 * 
	 */
    //$NON-NLS-1$
    protected static final String TRACE_STR = "TRACE";

    /**
	 * Prefix for tracing the changing of values.
	 * 
	 */
    //$NON-NLS-1$
    protected static final String PREFIX_TRACING = "TRACING ";

    /**
	 * Prefix for tracing the changing of values.
	 * 
	 */
    //$NON-NLS-1$
    protected static final String PREFIX_CHANGING = "CHANGING ";

    /**
	 * Prefix for tracing the catching of throwables.
	 * 
	 */
    //$NON-NLS-1$
    protected static final String PREFIX_CATCHING = "CAUGHT ";

    /**
	 * Prefix for tracing the throwing of throwables.
	 * 
	 */
    //$NON-NLS-1$
    protected static final String PREFIX_THROWING = "THROWN ";

    /**
	 * Prefix for tracing the entering of methods.
	 * 
	 */
    //$NON-NLS-1$
    protected static final String PREFIX_ENTERING = "ENTERING ";

    /**
	 * Prefix for tracing the exiting of methods.
	 * 
	 */
    //$NON-NLS-1$
    protected static final String PREFIX_EXITING = "EXITING ";

    /**
	 * Separator for methods.
	 * 
	 */
    //$NON-NLS-1$
    protected static final String SEPARATOR_METHOD = "#";

    /**
	 * Separator for parameters.
	 * 
	 */
    //$NON-NLS-1$
    protected static final String SEPARATOR_PARAMETER = ", ";

    /**
	 * Separator for return values.
	 * 
	 */
    //$NON-NLS-1$
    protected static final String SEPARATOR_RETURN = ":";

    /**
	 * Separator containing a space.
	 * 
	 */
    //$NON-NLS-1$
    protected static final String SEPARATOR_SPACE = " ";

    /**
	 * Label indicating old value.
	 * 
	 */
    //$NON-NLS-1$
    protected static final String LABEL_OLD_VALUE = "old=";

    /**
	 * Label indicating new value.
	 * 
	 */
    //$NON-NLS-1$
    protected static final String LABEL_NEW_VALUE = "new=";

    /**
	 * The cached debug options (for optimization).
	 */
    private static final Map<String, Boolean> cachedOptions = new HashMap<String, Boolean>();

    /**
	 * Retrieves a Boolean value indicating whether tracing is enabled for the
	 * specified plug-in.
	 * 
	 * @return Whether tracing is enabled for the plug-in.
	 * @param pluginId
	 *            The symbolic plugin id for which to determine trace
	 *            enablement.
	 * 
	 */
    protected static boolean shouldTrace(String pluginId) {
        //$NON-NLS-1$
        return shouldTrace0(pluginId + "/debug");
    }

    protected static boolean shouldTrace0(String option) {
        if (option == null)
            return false;
        Activator activator = Activator.getDefault();
        if (activator == null)
            return false;
        DebugOptions debugOptions = activator.getDebugOptions();
        if (debugOptions == null)
            return false;
        String result = debugOptions.getOption(option);
        //$NON-NLS-1$
        return (result == null) ? false : result.equalsIgnoreCase("true");
    }

    /**
	 * Retrieves a Boolean value indicating whether tracing is enabled for the
	 * specified debug option of the specified plug-in.
	 * 
	 * @return Whether tracing is enabled for the debug option of the plug-in.
	 * @param pluginId
	 *            The plug-in for which to determine trace enablement.
	 * @param option
	 *            The debug option for which to determine trace enablement.
	 * 
	 */
    public static boolean shouldTrace(String pluginId, String option) {
        if (pluginId == null)
            return false;
        if (TRACEALL || TRACE_BUNDLES.contains(pluginId))
            return true;
        if (shouldTrace(pluginId)) {
            Boolean value = null;
            synchronized (cachedOptions) {
                value = cachedOptions.get(option);
                if (null == value) {
                    value = shouldTrace0(option);
                    cachedOptions.put(option, value);
                }
            }
            return value.booleanValue();
        }
        return false;
    }

    /**
	 * Retrieves a textual representation of the specified argument.
	 * 
	 * @return A textual representation of the specified argument.
	 * @param argument
	 *            The argument for which to retrieve a textual representation.
	 * 
	 */
    public static String getArgumentString(Object argument) {
        if (argument == null)
            //$NON-NLS-1$
            return "null";
        if (argument instanceof byte[])
            return getStringFromByteArray((byte[]) argument);
        if (argument.getClass().isArray())
            return getArgumentsString((Object[]) argument);
        return String.valueOf(argument);
    }

    private static String getStringFromByteArray(byte[] bytes) {
        //$NON-NLS-1$
        StringBuffer buf = new StringBuffer("[");
        for (int i = 0; i < bytes.length; i++) {
            buf.append(bytes[i]);
            if (i == (bytes.length - 1))
                //$NON-NLS-1$
                buf.append(//$NON-NLS-1$
                "]");
            else
                //$NON-NLS-1$
                buf.append(//$NON-NLS-1$
                ",");
        }
        return buf.toString();
    }

    /**
	 * Retrieves a textual representation of the specified arguments.
	 * 
	 * @return A textual representation of the specified arguments.
	 * @param arguments
	 *            The arguments for which to retrieve a textual representation.
	 * 
	 */
    public static String getArgumentsString(Object[] arguments) {
        if (arguments == null)
            //$NON-NLS-1$
            return "[]";
        //$NON-NLS-1$
        StringBuffer buffer = new StringBuffer("[");
        for (int i = 0; i < arguments.length; i++) {
            buffer.append(getArgumentString(arguments[i]));
            if (i < arguments.length - 1)
                buffer.append(SEPARATOR_PARAMETER);
        }
        //$NON-NLS-1$
        buffer.append("]");
        return buffer.toString();
    }

    /**
	 * Traces the specified message.
	 * 
	 * @param message
	 *            The message to be traced.
	 * 
	 */
    protected static void trace(String message) {
        StringBuffer buf = new StringBuffer(PARENTHESIS_OPEN);
        buf.append(TRACE_STR).append(PARENTHESIS_CLOSE).append(getTimeString()).append(message).append(SEPARATOR_SPACE);
        System.out.println(buf.toString());
    }

    /**
	 * Get date and time string
	 * 
	 * @return String with current date and time
	 */
    protected static String getTimeString() {
        Date d = new Date();
        //$NON-NLS-1$
        SimpleDateFormat df = new SimpleDateFormat("[MM/dd/yy;HH:mm:ss:SSS]");
        return df.format(d);
    }

    /**
	 * Traces the specified message from the specified plug-in.
	 * 
	 * @param pluginId
	 *            The plug-in from which to trace.
	 * @param message
	 *            The message to be traced.
	 * 
	 */
    public static void trace(String pluginId, String message) {
        if (shouldTrace(pluginId))
            trace(message);
    }

    /**
	 * Traces the specified message from the specified plug-in for the specified
	 * debug option.
	 * 
	 * @param pluginId
	 *            The plug-in from which to trace.
	 * @param option
	 *            The debug option for which to trace.
	 * @param message
	 *            The message to be traced.
	 * 
	 */
    public static void trace(String pluginId, String option, String message) {
        if (shouldTrace(pluginId, option))
            trace(message);
    }

    /**
	 * Traces the specified message from the specified plug-in for the specified
	 * debug option.
	 * 
	 * @param pluginId
	 *            The plug-in from which to trace.
	 * @param option
	 *            The debug option for which to trace.
	 * @param clazz
	 *            The class whose method is being entered.
	 * @param methodName
	 *            The name of method that is being entered.
	 * @param message
	 *            The message to be traced.
	 * 
	 */
    public static void trace(String pluginId, String option, @SuppressWarnings("rawtypes") Class clazz, String methodName, String message) {
        if (shouldTrace(pluginId, option)) {
            StringBuffer buf = new StringBuffer(PREFIX_TRACING).append(clazz.getName());
            buf.append(SEPARATOR_METHOD).append(methodName);
            buf.append(PARENTHESIS_OPEN).append(message).append(PARENTHESIS_CLOSE);
            trace(buf.toString());
        }
    }

    /**
	 * Traces the changing of a value.
	 * 
	 * @param pluginId
	 *            The plug-in from which to trace.
	 * @param option
	 *            The debug option for which to trace.
	 * @param valueDescription
	 *            The description of the value which is changing.
	 * @param oldValue
	 *            The old value.
	 * @param newValue
	 *            The new value.
	 */
    public static void changing(String pluginId, String option, String valueDescription, Object oldValue, Object newValue) {
        if (shouldTrace(pluginId, option)) {
            StringBuffer buf = new StringBuffer(PREFIX_CHANGING);
            buf.append(valueDescription).append(SEPARATOR_SPACE).append(LABEL_OLD_VALUE).append(getArgumentString(oldValue));
            buf.append(SEPARATOR_PARAMETER).append(LABEL_NEW_VALUE).append(getArgumentString(newValue));
            trace(buf.toString());
        }
    }

    /**
	 * 
	 * @param pluginId
	 *            The plug-in from which to trace.
	 * @param option
	 *            The debug option for which to trace.
	 * @param clazz
	 *            The class in which the value is changing.
	 * @param methodName
	 *            The name of the method in which the value is changing.
	 * @param valueDescription
	 *            The description of the value which is changing.
	 * @param oldValue
	 *            The old value.
	 * @param newValue
	 *            The new value.
	 */
    public static void changing(String pluginId, String option, @SuppressWarnings("rawtypes") Class clazz, String methodName, String valueDescription, Object oldValue, Object newValue) {
        if (shouldTrace(pluginId, option)) {
            StringBuffer buf = new StringBuffer(PREFIX_CHANGING);
            buf.append(valueDescription).append(SEPARATOR_SPACE).append(LABEL_OLD_VALUE).append(getArgumentString(oldValue));
            buf.append(SEPARATOR_PARAMETER).append(LABEL_NEW_VALUE).append(getArgumentString(newValue));
            buf.append(SEPARATOR_SPACE).append(PARENTHESIS_OPEN).append(clazz.getName()).append(SEPARATOR_METHOD);
            buf.append(methodName).append(PARENTHESIS_CLOSE);
            trace(buf.toString());
        }
    }

    /**
	 * Traces the catching of the specified throwable in the specified method of
	 * the specified class.
	 * 
	 * @param pluginId
	 *            The plug-in from which to trace.
	 * @param option
	 *            The debug option for which to trace.
	 * @param clazz
	 *            The class in which the throwable is being caught.
	 * @param methodName
	 *            The name of the method in which the throwable is being caught.
	 * @param throwable
	 *            The throwable that is being caught.
	 * 
	 */
    public static void catching(String pluginId, String option, @SuppressWarnings("rawtypes") Class clazz, String methodName, Throwable throwable) {
        if (shouldTrace(pluginId, option)) {
            StringBuffer buf = new StringBuffer(PREFIX_CATCHING);
            if (throwable != null) {
                String message = throwable.getMessage();
                if (message != null)
                    buf.append(message).append(SEPARATOR_SPACE);
            }
            buf.append(PARENTHESIS_OPEN).append(clazz.getName()).append(SEPARATOR_METHOD);
            buf.append(methodName).append(PARENTHESIS_CLOSE);
            trace(buf.toString());
            if (throwable != null) {
                throwable.printStackTrace(System.err);
            }
        }
    }

    /**
	 * Traces the throwing of the specified throwable from the specified method
	 * of the specified class.
	 * 
	 * @param pluginId
	 *            The plug-in from which to trace.
	 * @param option
	 *            The debug option for which to trace.
	 * @param clazz
	 *            The class from which the throwable is being thrown.
	 * @param methodName
	 *            The name of the method from which the throwable is being
	 *            thrown.
	 * @param throwable
	 *            The throwable that is being thrown.
	 * 
	 */
    public static void throwing(String pluginId, String option, @SuppressWarnings("rawtypes") Class clazz, String methodName, Throwable throwable) {
        if (shouldTrace(pluginId, option)) {
            StringBuffer buf = new StringBuffer(PREFIX_THROWING);
            if (throwable != null) {
                String message = throwable.getMessage();
                if (message != null)
                    buf.append(message).append(SEPARATOR_SPACE);
            }
            buf.append(PARENTHESIS_OPEN).append(clazz.getName()).append(SEPARATOR_METHOD);
            buf.append(methodName).append(PARENTHESIS_CLOSE);
            trace(buf.toString());
            throwable.printStackTrace(System.err);
        }
    }

    /**
	 * Traces the entering into the specified method of the specified class.
	 * 
	 * @param pluginId
	 *            The plug-in from which to trace.
	 * @param option
	 *            The debug option for which to trace.
	 * @param clazz
	 *            The class whose method is being entered.
	 * @param methodName
	 *            The name of method that is being entered.
	 * 
	 */
    public static void entering(String pluginId, String option, @SuppressWarnings("rawtypes") Class clazz, String methodName) {
        if (shouldTrace(pluginId, option)) {
            StringBuffer buf = new StringBuffer(PREFIX_ENTERING).append(clazz.getName());
            buf.append(SEPARATOR_METHOD).append(methodName).append(PARENTHESIS_OPEN).append(PARENTHESIS_CLOSE);
            trace(buf.toString());
        }
    }

    /**
	 * Traces the entering into the specified method of the specified class,
	 * with the specified parameter.
	 * 
	 * @param pluginId
	 *            The plug-in from which to trace.
	 * @param option
	 *            The debug option for which to trace.
	 * @param clazz
	 *            The class whose method is being entered.
	 * @param methodName
	 *            The name of method that is being entered.
	 * @param parameter
	 *            The parameter to the method being entered.
	 * 
	 */
    public static void entering(String pluginId, String option, @SuppressWarnings("rawtypes") Class clazz, String methodName, Object parameter) {
        if (shouldTrace(pluginId, option)) {
            StringBuffer buf = new StringBuffer(PREFIX_ENTERING).append(clazz.getName());
            buf.append(SEPARATOR_METHOD).append(methodName);
            buf.append(PARENTHESIS_OPEN).append(getArgumentString(parameter)).append(PARENTHESIS_CLOSE);
            trace(buf.toString());
        }
    }

    /**
	 * Traces the entering into the specified method of the specified class,
	 * with the specified parameters.
	 * 
	 * @param pluginId
	 *            The plug-in from which to trace.
	 * @param option
	 *            The debug option for which to trace.
	 * @param clazz
	 *            The class whose method is being entered.
	 * @param methodName
	 *            The name of method that is being entered.
	 * @param parameters
	 *            The parameters to the method being entered.
	 * 
	 */
    public static void entering(String pluginId, String option, @SuppressWarnings("rawtypes") Class clazz, String methodName, Object[] parameters) {
        if (shouldTrace(pluginId, option)) {
            StringBuffer buf = new StringBuffer(PREFIX_ENTERING).append(clazz.getName());
            buf.append(SEPARATOR_METHOD).append(methodName);
            buf.append(PARENTHESIS_OPEN).append(getArgumentString(parameters)).append(PARENTHESIS_CLOSE);
            trace(buf.toString());
        }
    }

    /**
	 * Traces the exiting from the specified method of the specified class.
	 * 
	 * @param pluginId
	 *            The plug-in from which to trace.
	 * @param option
	 *            The debug option for which to trace.
	 * @param clazz
	 *            The class whose method is being exited.
	 * @param methodName
	 *            The name of method that is being exited.
	 * 
	 */
    public static void exiting(String pluginId, String option, @SuppressWarnings("rawtypes") Class clazz, String methodName) {
        if (shouldTrace(pluginId, option)) {
            StringBuffer buf = new StringBuffer(PREFIX_EXITING).append(clazz.getName());
            buf.append(SEPARATOR_METHOD).append(methodName);
            trace(buf.toString());
        }
    }

    /**
	 * Traces the exiting from the specified method of the specified class, with
	 * the specified return value.
	 * 
	 * @param pluginId
	 *            The plug-in from which to trace.
	 * @param option
	 *            The debug option for which to trace.
	 * @param clazz
	 *            The class whose method is being exited.
	 * @param methodName
	 *            The name of method that is being exited.
	 * @param returnValue
	 *            The return value of the method being exited.
	 * 
	 */
    public static void exiting(String pluginId, String option, @SuppressWarnings("rawtypes") Class clazz, String methodName, Object returnValue) {
        if (shouldTrace(pluginId, option)) {
            StringBuffer buf = new StringBuffer(PREFIX_EXITING).append(clazz.getName());
            buf.append(SEPARATOR_METHOD).append(methodName);
            buf.append(PARENTHESIS_OPEN).append(getArgumentString(returnValue)).append(PARENTHESIS_CLOSE);
            trace(buf.toString());
        }
    }
}
