/****************************************************************************
 * Copyright (c) 2008 Versant Corp. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Versant Corp. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.discovery.ui.model.resource;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ecf.discovery.ui.model.ModelPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;

public class ServiceResourceFactory extends ResourceFactoryImpl {

    /**
	 * 
	 */
    private static final String EMF_FILE_NAME = "known.service";

    /* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl#createResource(org.eclipse.emf.common.util.URI)
	 */
    public Resource createResource(URI protocol) {
        IPath path = ModelPlugin.getDefault().getStateLocation();
        URI uri = URI.createFileURI(path.append(EMF_FILE_NAME).toOSString());
        return new ServiceResource(uri);
    }
}
