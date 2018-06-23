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

/**
 * <P>
 * An internal class that contains the results from a native system call.
 * </P>
 * <P>
 * Calls to the underlying operation system will often have sever pieces of information
 * instead of just success and failure. This class allows clients to get back all the
 * information at once.
 * </P>
 * <P>
 * The RESULT_ constants are defined to make life easier for the JNI: accessing enum tags
 * in JNI is somewhat difficult.
 * </P>
 * 
 * @author Clark N. Hobbie
 */
public class SemaphoreResult {

    public static final int RESULT_SUCCESS = 0;

    public static final int RESULT_INSUFFICIENT_PERMISSIONS = 1;

    public static final int RESULT_DOES_NOT_EXIST = 2;

    public static final int RESULT_UNKNOWN_ERROR = 3;

    public static final int RESULT_TIMEOUT = 4;

    public static final int RESULT_UNKNOWN_HANDLE = 5;

    public static final String MSG_INSUFFICIENT_PERMISSIONS = "Insufficient permissions to access the semaphore";

    public static final String MSG_SUCCESS = "success";

    public static final String MSG_DOES_NOT_EXIST = "The semaphore does not exist";

    public static final String MSG_UNKNOWN_ERROR = "An unknown error occurred while trying to access the semaphore";

    public static final String MSG_TIMEOUT = "The specified period of timeout elapsed before the semaphore became available";

    public static final String MSG_UNKNOWN_HANDLE = "The specified handle was not recognized as a semaphore handle by the OS";

    public enum Results implements  {

        Success(RESULT_SUCCESS, MSG_SUCCESS) {
        }
        , InsufficientPermissions(RESULT_INSUFFICIENT_PERMISSIONS, MSG_INSUFFICIENT_PERMISSIONS) {
        }
        , DoesNotExist(RESULT_DOES_NOT_EXIST, MSG_DOES_NOT_EXIST) {
        }
        , UnknownError(RESULT_UNKNOWN_ERROR, MSG_UNKNOWN_ERROR) {
        }
        , Timeout(RESULT_TIMEOUT, MSG_TIMEOUT) {
        }
        , UnknownHandle(RESULT_UNKNOWN_HANDLE, MSG_UNKNOWN_HANDLE) {
        }
        ;

        public String message;

        public int code;

        private  Results(int resultCode, String theMessage) {
            code = resultCode;
            message = theMessage;
        }

        public static Results toResult(int code) {
            Results result = UnknownError;
            for (Results r : values()) {
                if (code == r.code) {
                    result = r;
                    break;
                }
            }
            return result;
        }

        public static int foo() {
            return -1;
        }
    }

    public void convertResultCode() {
        result = Results.toResult(resultCode);
    }

    public int resultCode;

    public Results result;

    public int errorCode;

    public long handle;
}
