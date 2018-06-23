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
package org.eclipse.ecf.bulletinboard;

/**
 * Models a poll option.
 * 
 * <p>
 * <strong>EXPERIMENTAL</strong>. This class or interface has been added as
 * part of a work in progress. There is no guarantee that this API will work or
 * that it will remain the same. Please do not use this API without consulting
 * with the ECF team.
 * </p>
 * 
 * @author Erkki
 */
public interface IPollOption {

    /**
	 * Returns the poll that this option belongs to.
	 * 
	 * @return the poll
	 */
    public IPoll getPoll();

    /**
	 * Returns the option number for this poll option.
	 * 
	 * @return the option number
	 */
    public int getOptionNumber();

    /**
	 * Returns the name of this poll option.
	 * 
	 * @return the name of this option
	 */
    public String getName();
}
