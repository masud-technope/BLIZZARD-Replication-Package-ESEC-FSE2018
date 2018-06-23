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
package org.eclipse.ecf.discovery.ui.model.provider;

import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.util.ResourceLocator;
import org.osgi.framework.BundleContext;

/**
 * This is the central singleton for the Discovery edit plugin.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public final class DiscoveryEditPlugin extends EMFPlugin {

    /**
	 * Keep track of the singleton.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public static final DiscoveryEditPlugin INSTANCE = new DiscoveryEditPlugin();

    /**
	 * Keep track of the singleton.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    private static Implementation plugin;

    /**
	 * Create the instance.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public  DiscoveryEditPlugin() {
        super(new ResourceLocator[] {});
    }

    /**
	 * Returns the singleton instance of the Eclipse plugin.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the singleton instance.
	 * @generated
	 */
    public ResourceLocator getPluginResourceLocator() {
        return plugin;
    }

    /**
	 * Returns the singleton instance of the Eclipse plugin.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the singleton instance.
	 * @generated
	 */
    public static Implementation getPlugin() {
        return plugin;
    }

    /**
	 * The actual implementation of the Eclipse <b>Plugin</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
    public static class Implementation extends EclipsePlugin {

        /**
		 * Creates an instance.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
        public  Implementation() {
            super();
            // Remember the static instance.
            //
            plugin = this;
        }

        /* (non-Javadoc)
		 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
		 */
        public void start(BundleContext context) throws Exception {
            // start the model
            DiscoveryEditingDomainProvider.eINSTANCE.load();
            super.start(context);
        }

        /* (non-Javadoc)
		 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
		 */
        public void stop(BundleContext context) throws Exception {
            // store the model
            DiscoveryEditingDomainProvider.eINSTANCE.save();
            super.stop(context);
        }
    }
}
