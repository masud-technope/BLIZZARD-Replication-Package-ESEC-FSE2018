/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.datashare.mergeable;

import java.util.List;

public interface IItemHistory extends IUpdateInfo {

    /**
	 * Get updates previously applied as part of this item history
	 * 
	 * @return List of IUpdateInfo instances of previous updates. Will not
	 *         return <code>null</code>, but may be empty List.
	 */
    public List getUpdates();
}
