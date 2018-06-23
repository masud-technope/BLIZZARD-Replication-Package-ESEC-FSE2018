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

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.discovery.ui.model.ModelPlugin;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;

public class DiscoveryEditingDomainProvider implements IEditingDomainProvider {

    public static DiscoveryEditingDomainProvider eINSTANCE = new DiscoveryEditingDomainProvider();

    /**
	 * This keeps track of the editing domain that is used to track all changes to the model.
	 */
    private AdapterFactoryEditingDomain editingDomain;

    /**
	 * This is the one adapter factory used for providing views of the model.
	 */
    private ComposedAdapterFactory adapterFactory;

    private BasicCommandStack commandStack;

    public void load() {
        // Create an adapter factory that yields item providers.
        adapterFactory = new ComposedAdapterFactory();
        adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new ModelItemProviderAdapterFactory());
        adapterFactory.addAdapterFactory(new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE));
        // Create the command stack that will notify this editor as commands are executed.
        commandStack = new BasicCommandStack();
        // Create the editing domain with a special command stack.
        editingDomain = new AdapterFactoryEditingDomain(adapterFactory, commandStack, new HashMap());
        // Load the resource through the editing domain.
        URI resourceURI = URI.createURI("service://");
        try {
            getEditingDomain().getResourceSet().getResource(resourceURI, true);
        } catch (Exception e) {
            getEditingDomain().getResourceSet().getResource(resourceURI, false);
        }
    }

    /* (non-Javadoc)
	 * @see org.eclipse.emf.edit.domain.IEditingDomainProvider#getEditingDomain()
	 */
    public EditingDomain getEditingDomain() {
        return editingDomain;
    }

    public void addCommandStackListener(CommandStackListener csl) {
        // Add a listener to set the most recent command's affected objects to be the selection of the viewer with focus.
        commandStack.addCommandStackListener(csl);
    }

    public void removeCommandStackListener(CommandStackListener csl) {
        commandStack.removeCommandStackListener(csl);
    }

    /**
	 * @return the adapterFactory
	 */
    public ComposedAdapterFactory getAdapterFactory() {
        return adapterFactory;
    }

    /**
	 * 
	 */
    public void save() {
        for (Iterator itr = editingDomain.getResourceSet().getResources().iterator(); itr.hasNext(); ) {
            Resource resource = (Resource) itr.next();
            if (!resource.getContents().isEmpty() && !editingDomain.isReadOnly(resource)) {
                try {
                    resource.save(Collections.EMPTY_MAP);
                //					resource.save(System.out, Collections.EMPTY_MAP);
                } catch (UnsupportedOperationException e) {
                    ModelPlugin.getDefault().getLog().log(new Status(IStatus.WARNING, ModelPlugin.PLUGIN_ID, "Saving " + resource.toString() + " isn't supported yet"));
                } catch (IOException e) {
                    ModelPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, ModelPlugin.PLUGIN_ID, e.getMessage(), e));
                }
            }
        }
    }
}
