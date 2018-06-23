/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.datashare.mergeable;

import java.util.Date;
import org.eclipse.ecf.core.identity.IIdentifiable;

/**
 * Info about when an update was made and by whom.
 */
public interface IUpdateInfo extends IIdentifiable {

    /**
	 * Get date of when update was applied.
	 * 
	 * @return Date the date when applied. Will not be <code>null</code>.
	 */
    public Date getWhen();
}
