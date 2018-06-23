/*******************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.ecf.presence.search;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.util.ECFException;

/**
 * 
 * @since 2.0
 */
public class UserSearchException extends ECFException {

    /**
	 * 
	 */
    private static final long serialVersionUID = -896845055593390010L;

    private ICriteria criteria = null;

    /**
	 * 
	 */
    public  UserSearchException(ICriteria criteria) {
        super();
        this.criteria = criteria;
    }

    /**
	 * @param status
	 */
    public  UserSearchException(IStatus status, ICriteria criteria) {
        super(status);
        this.criteria = criteria;
    }

    /**
	 * @param message
	 * @param cause
	 */
    public  UserSearchException(String message, Throwable cause, ICriteria criteria) {
        super(message, cause);
        this.criteria = criteria;
    }

    /**
	 * @param message
	 */
    public  UserSearchException(String message, ICriteria criteria) {
        super(message);
        this.criteria = criteria;
    }

    /**
	 * @param cause
	 */
    public  UserSearchException(Throwable cause, ICriteria criteria) {
        super(cause);
        this.criteria = criteria;
    }

    public ICriteria getSearchCriteria() {
        return this.criteria;
    }
}
