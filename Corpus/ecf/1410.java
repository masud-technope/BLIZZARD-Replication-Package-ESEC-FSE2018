/**
 * Copyright (c) 2006 Ecliptical Software Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ecliptical Software Inc. - initial API and implementation
 */
package org.eclipse.ecf.example.pubsub;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.SafeRunner;

public class AppendableList implements Serializable {

    private static final long serialVersionUID = -5447897626712251185L;

    private transient ListenerList listeners;

    protected final List values = new ArrayList();

    public void addListener(IAppendableListListener listener) {
        getListenerList().add(listener);
    }

    public void removeListener(IAppendableListListener listener) {
        getListenerList().remove(listener);
    }

    protected void fireAppended(final Object value) {
        Object[] l = getListenerList().getListeners();
        for (int i = 0; i < l.length; ++i) {
            final IAppendableListListener listener = (IAppendableListListener) l[i];
            SafeRunner.run(new ISafeRunnable() {

                public void run() throws Exception {
                    listener.appended(AppendableList.this, value);
                }

                public void handleException(Throwable exception) {
                    // TODO Auto-generated method stub
                    exception.printStackTrace();
                }
            });
        }
    }

    protected synchronized ListenerList getListenerList() {
        if (listeners == null)
            listeners = new ListenerList();
        return listeners;
    }

    public synchronized Object[] getValues() {
        return values.toArray();
    }

    public synchronized boolean add(Object value) {
        boolean result = values.add(value);
        fireAppended(value);
        return result;
    }

    public synchronized String toString() {
        return values.toString();
    }
}
