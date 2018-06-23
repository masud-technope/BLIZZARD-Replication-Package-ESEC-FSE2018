/*******************************************************************************
 * Copyright (c) 2005, 2006 Erkki Lindpere and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Erkki Lindpere - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.provider.phpbb;

/**
 * This exception is set as the cause of a BBException when an exception message
 * was parseable from the PHPBB HTML after a failed operation.
 * 
 * @author Erkki
 */
public class PHPBBException extends Exception {

    private static final long serialVersionUID = 7050504740362137141L;

    public  PHPBBException(String message) {
        super(message);
    }
}
