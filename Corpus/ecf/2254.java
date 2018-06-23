/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteserviceadmin.ui.rsa.model;

/**
 * @since 3.4
 */
public class ExceptionNode extends NameValuePropertyNode {

    public  ExceptionNode(String exceptionLabel, Throwable exception, boolean showStack) {
        super(exceptionLabel, exception.getLocalizedMessage());
        if (showStack) {
            for (StackTraceElement ste : exception.getStackTrace()) addChild(new StackTraceElementNode(ste.toString()));
            Throwable cause = exception.getCause();
            if (cause != null)
                addChild(new ExceptionNode(cause, true));
        }
    }

    public  ExceptionNode(Throwable exception, boolean showStack) {
        this("", exception, showStack);
        setNameValueSeparator("");
    }
}
