/*******************************************************************************
 * Copyright (c) 2009 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Chi Jian Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.sync.resources.core;

import java.io.Serializable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ecf.sync.IModelChange;
import org.eclipse.ecf.sync.IModelChangeMessage;
import org.eclipse.ecf.sync.ModelUpdateException;

public final class BatchModelChange implements IModelChange, Serializable {

    private static final long serialVersionUID = 6333372597585156862L;

    private final IModelChangeMessage[] messages;

    private long time = System.currentTimeMillis();

    private boolean outgoing = true;

     BatchModelChange(IModelChangeMessage[] messages) {
        this.messages = messages;
    }

    public void applyToModel(Object model) throws ModelUpdateException {
        throw new ModelUpdateException("Batch changes are applied individually", this, model);
    }

    void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    void setOutgoing(boolean outgoing) {
        this.outgoing = outgoing;
    }

    public boolean isOutgoing() {
        return outgoing;
    }

    public IModelChangeMessage[] getMessages() {
        return messages;
    }

    public Object getAdapter(Class adapter) {
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }
}
