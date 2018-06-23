/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.presence.history;

import java.util.Map;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.core.identity.ID;

/**
 * History manager for chat history.
 */
public interface IHistoryManager extends IAdaptable {

    /**
	 * Get history for given targetID.
	 * 
	 * @param targetID
	 *            the ID of the targetID we want history for. May not be
	 *            <code>null</code>.  If being used for chat rooms, the
	 *            targetID is the <b>roomID</b> for the desired history.
	 *            if being used for IM/chat, the targetID is the ID of the
	 *            chat partner.
	 * @param options
	 *            any options associated with getting history info. May be
	 *            <code>null</code>.
	 * @return IHistory for given partnerID. Will return <code>null</code> if
	 *         no history exists (with given options) for the given targetID.
	 */
    public IHistory getHistory(ID targetID, Map options);

    /**
	 * 
	 * @return <code>true</code> if history manager is active (recording
	 *         history at all).
	 */
    public boolean isActive();

    /**
	 * Make this history manager active or inactive.
	 * 
	 * @param active
	 *            <code>true</code> to make this history manager active,
	 *            <code>false</code> to make it inactive.
	 */
    public void setActive(boolean active);
}
