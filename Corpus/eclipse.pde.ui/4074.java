/*******************************************************************************
 * Copyright (c) 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.internal.model;

import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiElement;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiTypeContainer;

/**
 * A collection of class file containers.
 *
 * @since 1.0
 */
public class CompositeApiTypeContainer extends AbstractApiTypeContainer {

    private List<IApiTypeContainer> fContainers;

    /**
	 * Constructs a composite container on the given list of containers.
	 *
	 * @param containers list of containers
	 */
    public  CompositeApiTypeContainer(IApiElement parent, List<IApiTypeContainer> containers) {
        //$NON-NLS-1$
        super(parent, IApiElement.API_TYPE_CONTAINER, "Composite Class File Container");
        this.fContainers = containers;
    }

    @Override
    protected List<IApiTypeContainer> createApiTypeContainers() throws CoreException {
        return fContainers;
    }

    /**
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
        StringBuffer buff = new StringBuffer();
        //$NON-NLS-1$
        buff.append("Composite Class File Container:\n");
        if (fContainers == null) {
            //$NON-NLS-1$
            buff.append("\t<EMPTY>");
        } else {
            IApiTypeContainer container = null;
            for (Iterator<IApiTypeContainer> iter = fContainers.iterator(); iter.hasNext(); ) {
                container = iter.next();
                //$NON-NLS-1$
                buff.append(//$NON-NLS-1$
                "\t" + container.toString());
            }
        }
        return buff.toString();
    }
}
