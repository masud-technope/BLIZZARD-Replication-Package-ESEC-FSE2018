/*******************************************************************************
 * Copyright (c) 2009  Clark N. Hobbie
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Clark N. Hobbie - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.ipc.semaphore;

import org.eclipse.ecf.ipc.IPCPackage;

/**
 * <P>
 * An internal class that provides an interface into the system for implementing
 * semaphores.
 * </P>
 * <H2>Exceptions</H2>
 * <P>
 * To keep the native methods as simple as possible, the exceptions thrown in
 * response to error conditions signal the nature of the error using integer
 * codes. The codes are defined by the class constants whose names start with
 * "ERROR_". For example, ERROR_UNKNOWN_HANDLE is used if the platform does not
 * recognize the handle passed to it.
 * </P>
 * 
 * <H2>Virtual Files</H2>
 * <P>
 * Each semaphore is associated with a file in the file system.  That file may 
 * or may not be required by the underlying operating system.  
 * </P>
 * 
 * <P>
 * In the case of Linux, the contents of this file will be the absolute path of
 * the file.
 * </P>
 * 
 * <P>
 * In the case of Windows, the contents of the file will be an integer value 
 * uniquely identifies the semaphore.
 * </P>
 * 
 * @author Clark N. Hobbie
 */
public class SemaphoreNative {

    static {
        IPCPackage.initializePackage();
    }

    /**
	 * Does this particular native implementation support getting a semaphore's
	 * value?
	 */
    protected static Boolean ourSupportsGetValue;

    /**
	 * Does this particular native implementation support setting a semaphore's
	 * value other than calling decrement methods?
	 */
    protected static Boolean ourSupportsSetValue;

    /**
	 * Native routines should return this to signal that the routine executed
	 * without problems.
	 */
    public static final int RESULT_SUCCESS = 0;

    /**
	 * The semaphore operation failed for an unanticipated reason.
	 */
    public static final int RESULT_UNKNOWN_ERROR = 1;

    /**
	 * The semaphore operation failed for a reason that is specific to the
	 * platform.
	 */
    public static final int RESULT_PLATFORM_ERROR = 2;

    /**
	 * The semaphore operation failed because the native platform does not
	 * recognize the handle used.
	 */
    public static final int RESULT_UNKNOWN_HANDLE = 3;

    /**
	 * A value returned by the decrement method to signal that the method failed
	 * because the semaphore value was less than 1 and the specified timeout
	 * elapsed.
	 */
    public static final int RESULT_TIMEOUT = 4;

    /**
	 * Access to the semaphore was denied when a particular operation was
	 * attempted.
	 */
    public static final int RESULT_ACCESS_DENIED = 5;

    /**
	 * The requested method is not implemented by the native layer.
	 */
    public static final int RESULT_NOT_IMPLEMENTED = 6;

    /**
	 * The semaphore has been incremented too many times.
	 */
    public static final int RESULT_TOO_MANY_INCREMENTS = 7;

    /**
	 * Denotes that the actual name for a semaphore should be an integer value.
	 */
    public static final int METHOD_INTEGER = 0;

    /**
	 * Denotes that the actual name for a semaphore should be absolute path to
	 * the virtual file.
	 */
    public static final int METHOD_FILE_NAME = 1;

    /**
	 * Connect to the underlying semaphore, creating it if it does not already
	 * exist.
	 * <P>
	 * The return value will be greater than 0 if the method was successful.
	 * This indicates that the value can be used in future calls to this classes
	 * methods.
	 * <P>
	 * If the return value is less than 0, then the native method failed.
	 * Multiplying the return value by -1 should produce a result that
	 * corresponds to one of the return codes defined by this class. That is,
	 * the absolute value should match one of the RESULT_ constants.
	 * <P>
	 * The return value should never be equal to 0.
	 * 
	 * @param name
	 *            The logical name of the semaphore.
	 * @param initialValue
	 *            The initial value of the semaphore.
	 * @return See description. A value greater than 0 means success, a value
	 *         less than 0 means failure. The return value should not be equal
	 *         to zero.
	 */
    public static native void connect(SemaphoreResult result, String name, int initialValue);

    /**
	 * Increase the value of the semaphore by 1.
	 * 
	 * @param handle
	 *            A logical value that the operating system uses to identify the
	 *            semaphore.
	 * @return 0 if successful, otherwise -1.
	 */
    public static native void increment(SemaphoreResult result, long handle);

    /**
	 * Decrease the value of the semaphore by 1 if it is available; if not
	 * available wait at least the specified period of time for it to become
	 * available.
	 * <P>
	 * The value used for handle should be one that is returned by the connect
	 * method.
	 * <P>
	 * The native method always checks to see if the semaphore is available. The
	 * only question is whether or not the method will wait for the semaphore if
	 * it is not currently available.
	 * <P>
	 * If the timeout is less than or equal to zero, the method will not wait
	 * for the semaphore to become available. If the semaphore is currently
	 * available, then it will decrement it, but if it is not available then the
	 * method returns immediately with a return code of {@link #RESULT_TIMEOUT}.
	 * <P>
	 * If the timeout is greater than zero, the method will wait some
	 * unspecified period of time that is greater than or equal to the specified
	 * timeout for the semaphore to become available. If the semaphore is
	 * currently available, the method decrements it and returns. If the
	 * semaphore is not available, then the method waits some unspecified period
	 * of time for it to become available. If the semaphore becomes available,
	 * the method decrements it and returns {@link #RESULT_SUCCESS}. If the
	 * semaphore does not become available in the period of time, the method
	 * returns {@link #RESULT_TIMEOUT}.
	 * <P>
	 * The underlying operating system may not provide the requested resolution
	 * for nanoseconds. Generally speaking, the resolution should be at least 1
	 * millisecond.
	 * <P>
	 * At present, the method returns some value other than DECREMENT_TIMEOUT if
	 * it fails for some other reason, such as the semaphore does not exist.
	 * 
	 * @param handle
	 *            Which semaphore to use.
	 * @param timeout
	 *            The amount of time, in nanoseconds, to wait for the semaphore
	 *            to become available if it is not currently available.
	 * @return {@link #RESULT_SUCCESS} if the method decrements the semaphore,
	 *         otherwise some other RESULT_ code is returned.
	 */
    public static native void decrement(SemaphoreResult result, long handle, long timeout);

    /**
	 * Gets the current value of the semaphore.
	 * <P>
	 * The method returns 0 or greater on success and a negative value on
	 * failure. If the value indicates success, the return value is also the
	 * value of the semaphore. If negative, the absolute value corresponds to
	 * one the the RESULT_ constants.
	 * 
	 * @param handle
	 *            The semaphore to query.
	 * @return See description.
	 */
    public static native int getValue(SemaphoreResult result, long handle);

    public static native boolean supportsGetValue();

    /**
	 * Set the value of the semaphore, without regards to other processes.
	 * <P>
	 * This method replaces whatever value the semaphore had with the value
	 * passed to the method.
	 * 
	 * @param handle
	 *            The handle corresponding the semaphore to modify.
	 * @param value
	 *            The new value for the semaphore.
	 * @return {@link #RESULT_SUCCESS} on success, otherwise a different RESULT_
	 *         code.
	 */
    public static native int setValue(SemaphoreResult result, long handle, int value);

    public static native boolean supportsSetValue();

    public static native void createResult(SemaphoreResult result, String name, int maxValue, int initialValue);

    public static native void linkTest();

    /**
	 * Return the method that this platform uses in order to create a name for a
	 * semaphore.
	 * 
	 * @return This method should return one of the METHOD_ constants.
	 */
    public static native int getNamingMethod();
}
