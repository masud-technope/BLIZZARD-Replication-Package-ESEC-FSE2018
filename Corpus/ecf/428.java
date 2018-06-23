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

import java.io.File;
import java.io.IOException;
import org.eclipse.ecf.ipc.IPCException;
import org.eclipse.ecf.ipc.Utils;
import org.eclipse.ecf.ipc.IPCException.Errors;
import org.eclipse.ecf.ipc.semaphore.SemaphoreResult.Results;

/**
 * An inter-process, n-ary semaphore. <H2>Description</H2> An instance of this class
 * presents an implementation of a general semaphore that is capable of spanning more than
 * one process. Briefly, a semaphore has an integer value associated with it that is
 * supposed to always be zero or more. Calling the increment method tries to decrease the
 * value by 1, but if the value is already less than 1, the calling process waits until
 * some other process changes the value to 1 or greater before continuing. The increment
 * method increases the semaphore value by 1 and never blocks.
 * <P>
 * This class was created because standard java does not provide any mechanism for
 * communication between java VMs other than sockets, etc. Those methods are fine, but
 * semaphores, etc. can be hundreds of times faster.
 * </P>
 * <P>
 * The class uses file naming <A href="../package-summary.html#file_naming"> as explained
 * in the package description.</A> In the case of this class, the file name will refer to
 * an operating system file whose contents are a string that is the numeric "handle" that
 * the system uses to identify the semaphore in system calls.
 * </P>
 * <P>
 * The class assumes that the primary reason for using this class is speed, and therefore
 * most other issues are ignored in favor of speed. For this reason, some methods that
 * could throw a more descriptive exception, such as increment when the semaphore is not
 * connected, throw things like NullPointerException.
 * </P>
 * <H2>Native Methods</H2>
 * <P>
 * This class uses native methods, specifically the methods defined in the
 * {@link SemaphoreNative} class. This is an inherent security issue that really cannot be
 * avoided if clients want to use this class. The name of the library used is defined by
 * {@link org.eclipse.ecf.ipc.semaphore.SemaphoreNative#LIBRARY_NAME}. The library is not loaded
 * until an instance of this class actually tries to connect or create an underlying
 * semaphore; so clients have a bit of control over error handling.
 * </P>
 * <H2>Implementation</H2>
 * <P>
 * The implementation of this class depends on the notion that a semaphore can be
 * identified by a long integer "handle." This is the convention used by POSIX and
 * Windows, but if it is not in fact the case for the platform, the class will not
 * function. Of course if this is the case, the native methods should fail to load before
 * a Semaphore can be used, but that's another issue.
 * </P>
 * 
 * <H2>Virtual and Actual Names</H2>
 * <P>
 * In keeping with the virtual file approach to naming, each semaphore is associated
 * with a file.  The file may or may not be required by the underlying operating 
 * system, see the {@link SemaphoreNative} class for details.
 * </P>
 * 
 * <P>
 * The contents of the file is a string that is used to identify the semaphore to 
 * the operating system in some way.  It is possible that the string is the same as 
 * the file name itself.
 * </P>
 * 
 * @author Clark N. Hobbie
 */
public class Semaphore {

    /**
	 * The underlying operating system handle for the semaphore.
	 */
    protected long myHandle = -1;

    public long getHandle() {
        return myHandle;
    }

    public void setHandle(long handle) {
        myHandle = handle;
    }

    private String myActualName;

    public String getActualName() {
        return myActualName;
    }

    public void setActualName(String s) {
        myActualName = s;
    }

    private static int ourNextActualName;

    static {
        long temp = System.currentTimeMillis();
        ourNextActualName = (int) temp & 0x0fffffff;
    }

    protected static synchronized int generateNextActualName() {
        int temp = ourNextActualName;
        ourNextActualName++;
        return temp;
    }

    /**
	 * Create a semaphore without actually doing anything at the operating system level.
	 * <P>
	 * A call should be made to {@link #connect(String, int)} before attempting to use
	 * other methods, otherwise NullPointerExceptions will be thrown.
	 */
    public  Semaphore() {
    }

    public  Semaphore(String name) throws IPCException {
        connect(name, 1, 1);
    }

    /**
	 * Create a semaphore and attach to the underlying operating system construct.
	 * <P>
	 * This method is equivalent to calling the no-arg constructor, followed by calling
	 * the {@link #connect(String, int)} method.
	 * 
	 * @param name
	 *        The name of the semaphore.
	 * @param initialValue
	 *        The initial value of the semaphore.
	 */
    public  Semaphore(String name, int initialValue) throws IPCException {
        connect(name, initialValue, initialValue);
    }

    /**
	 * Connect to the underlying operating system resource, creating it if it does not
	 * exist.
	 * <P>
	 * Calling this method when the instance is already connected has no effect.
	 * </P>
	 * <P>
	 * A side effect of this method is that it creates a file whose name is the value 
	 * of the name parameter.  The contents of the file is the actual name of the 
	 * semaphore that is used when creating the semaphore.
	 * </P>
	 * 
	 * @param name
	 *        The name of the semaphore.
	 * @param initialValue
	 *        The initial value for the semaphore.
	 *        
	 */
    public void connect(String name, int maxValue, int initialValue) throws IPCException {
        determineActualName(name);
        SemaphoreResult result = new SemaphoreResult();
        SemaphoreNative.connect(result, getActualName(), initialValue);
        result.convertResultCode();
        switch(result.result) {
            case Success:
                break;
            case InsufficientPermissions:
                throw new IPCException(Errors.Permissions);
            default:
                throw new IPCException(Errors.Unknown);
        }
        setHandle(result.handle);
    }

    /**
	 * 
	 * @param name
	 * @throws IPCException
	 */
    private void determineActualName(String name) throws IPCException {
        String actualName = null;
        int method;
        method = SemaphoreNative.getNamingMethod();
        switch(method) {
            case SemaphoreNative.METHOD_FILE_NAME:
                {
                    File file = new File(name);
                    actualName = file.toString();
                    break;
                }
            case SemaphoreNative.METHOD_INTEGER:
                {
                    actualName = Integer.toString(generateNextActualName());
                    break;
                }
            default:
                throw new IPCException("Impossible case");
        }
        try {
            actualName = Utils.readOrCreate(name, actualName);
            setActualName(actualName);
        } catch (IOException e) {
            throw new IPCException("Error trying to create or read virtual file for semaphore", e);
        }
    }

    /**
	 * Connect to the underlying semaphore.
	 * <P>
	 * This method will attempt to connect to the underlying semaphore, creating it if it
	 * does not exist.
	 * </P>
	 * <P>
	 * The initialValue parameter is ignored if the semaphore already exists. In the
	 * situation where the semaphore is created, the initialValue argument is used to set
	 * the starting value
	 * </P>
	 * 
	 * @param name The name of the semaphore.  See class documentation for details.
	 * @param initialValue The initial value for the semaphore if it needs to be created.
	 * @throws IPCException If there is a problem creating the semaphore.
	 */
    public void connect(String name, int initialValue) throws IPCException {
        connect(name, initialValue, initialValue);
    }

    protected void convertReturnCode(SemaphoreResult result) {
        result.result = Results.toResult(result.resultCode);
    }

    /**
	 * Increment the value of the semaphore.
	 * <P>
	 * Calling this method when the semaphore is not connected will result in a
	 * NullPointerException being thrown.
	 * </P>
	 */
    public void increment() throws IPCException {
        SemaphoreResult result = new SemaphoreResult();
        SemaphoreNative.increment(result, getHandle());
        result.convertResultCode();
        if (Results.Success != result.result) {
            throw new IPCException(result.result.message);
        }
    }

    /**
	 * <P>
	 * Try to decrement the semaphore, waiting at least until the specified time to do so
	 * if the semaphore is already at 0 or below.
	 * </P>
	 * <P>
	 * Note that time is relative to {@link System#nanoTime()}. If that method returns a
	 * value that is greater than the value passed to us, then the method will not wait
	 * for the semaphore to become available.
	 * <P>
	 * The method always tries at least once to decrement the semaphore. The only question
	 * is whether or not the method will wait if it cannot immediately perform its
	 * operation.
	 * 
	 * @param stopTime
	 *        The time, relative to {@link System#nanoTime()}, after which the method will
	 *        not wait for the semaphore to become available. Passing a negative value
	 *        will cause the method to wait until the semaphore is available instead of
	 *        timing out.
	 * @return true if the semaphore was reserved, false if it timed out.
	 * @throws IPCException
	 *         If the call times out.
	 */
    public void decrement(long timeout) throws IPCException {
        SemaphoreResult result = new SemaphoreResult();
        SemaphoreNative.decrement(result, getHandle(), timeout);
        result.convertResultCode();
        switch(result.result) {
            case Success:
                break;
            case Timeout:
                throw new IPCException(Errors.Timeout);
            default:
                throw new IPCException(result.result.message);
        }
    }

    /**
	 * Decrement the semaphore by 1, blocking if required.
	 * <P>
	 * This method is equivalent to calling {@linkplain #decrement(long) decrement(-1)}.
	 * See that method for details.
	 * </P>
	 * 
	 * @return Whether the decrement succeeded. This version of the method should always
	 *         return true.
	 * @throws IPCException
	 *         If a problem is encountered while trying to perform this operation.
	 */
    public void decrement() throws IPCException {
        decrement(-1);
    }
}
