/*******************************************************************************
 * Copyright (c) 2004, 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core;

import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.provider.IContainerInstantiator;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.core.*;

/**
 * Factory for creating {@link IContainer} instances. This class provides ECF
 * clients an entry point to constructing {@link IContainer} instances. <br>
 * <br>
 * Here is an example use of the ContainerFactory to construct an instance of
 * the 'standalone' container (has no connection to other containers): <br>
 * <br>
 * <code>
 * 	    IContainer container = <br>
 * 			ContainerFactory.getDefault().createContainer("ecf.generic.client");
 *      <br><br>
 *      ...further use of container here...
 * </code> For more details on the creation
 * and lifecycle of IContainer instances created via this factory see
 * {@link IContainer}.
 * 
 * @see IContainer
 * @see IContainerFactory
 * @since 3.1
 */
public class ContainerFactory implements IContainerFactory, IContainerManager {

    //$NON-NLS-1$
    public static final String BASE_CONTAINER_NAME = "ecf.base";

    static final Map containerdescriptions = new HashMap();

    static final Map containers = new HashMap();

    static final List managerListeners = new ArrayList();

    private static IContainerFactory instance = null;

    private static volatile boolean init = false;

    static {
        instance = new ContainerFactory();
    }

    class ContainerEntry {

        private final IContainer container;

        private final ContainerTypeDescription typeDescription;

        public  ContainerEntry(IContainer container, ContainerTypeDescription typeDescription) {
            this.container = container;
            this.typeDescription = typeDescription;
        }

        public IContainer getContainer() {
            return this.container;
        }

        public ContainerTypeDescription getContainerTypeDescription() {
            return this.typeDescription;
        }
    }

    public static synchronized IContainerFactory getDefault() {
        if (init == false) {
            // first mark the extension initalized because it initializeExtensions()
            // eventually calls this method again
            init = true;
            ECFPlugin.getDefault().initializeExtensions();
        }
        return instance;
    }

    protected  ContainerFactory() {
        ECFPlugin.getDefault().addDisposable(new IDisposable() {

            public void dispose() {
                synchronized (containers) {
                    for (Iterator i = containers.keySet().iterator(); i.hasNext(); ) {
                        ContainerEntry entry = (ContainerEntry) containers.get(i.next());
                        if (entry != null) {
                            IContainer c = entry.getContainer();
                            try {
                                c.dispose();
                            } catch (Throwable e) {
                                ECFPlugin.getDefault().log(new Status(IStatus.ERROR, ECFPlugin.getDefault().getBundle().getSymbolicName(), IStatus.ERROR, "container dispose error", e));
                                Trace.catching(ECFPlugin.PLUGIN_ID, ECFDebugOptions.EXCEPTIONS_CATCHING, ContainerFactory.class, "doDispose", e);
                            }
                        }
                    }
                    containers.clear();
                }
                containerdescriptions.clear();
                managerListeners.clear();
            }
        });
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IContainerFactory#addDescription(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
    public ContainerTypeDescription addDescription(ContainerTypeDescription containerTypeDescription) {
        return addDescription0(containerTypeDescription);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IContainerFactory#getDescriptions()
	 */
    public List getDescriptions() {
        return getDescriptions0();
    }

    protected List getDescriptions0() {
        synchronized (containerdescriptions) {
            return new ArrayList(containerdescriptions.values());
        }
    }

    protected ContainerTypeDescription addDescription0(ContainerTypeDescription containerTypeDescription) {
        if (containerTypeDescription == null)
            return null;
        synchronized (containerdescriptions) {
            return (ContainerTypeDescription) containerdescriptions.put(containerTypeDescription.getName(), containerTypeDescription);
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IContainerFactory#containsDescription(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
    public boolean containsDescription(ContainerTypeDescription containerTypeDescription) {
        return containsDescription0(containerTypeDescription);
    }

    protected boolean containsDescription0(ContainerTypeDescription containerTypeDescription) {
        if (containerTypeDescription == null)
            return false;
        synchronized (containerdescriptions) {
            return containerdescriptions.containsKey(containerTypeDescription.getName());
        }
    }

    protected ContainerTypeDescription getDescription0(ContainerTypeDescription containerTypeDescription) {
        if (containerTypeDescription == null)
            return null;
        synchronized (containerdescriptions) {
            return (ContainerTypeDescription) containerdescriptions.get(containerTypeDescription.getName());
        }
    }

    protected ContainerTypeDescription getDescription0(String containerTypeDescriptionName) {
        if (containerTypeDescriptionName == null)
            return null;
        synchronized (containerdescriptions) {
            return (ContainerTypeDescription) containerdescriptions.get(containerTypeDescriptionName);
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IContainerFactory#getDescriptionByName(java.lang.String)
	 */
    public ContainerTypeDescription getDescriptionByName(String containerTypeDescriptionName) {
        return getDescription0(containerTypeDescriptionName);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IContainerFactory#removeDescription(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
    public ContainerTypeDescription removeDescription(ContainerTypeDescription containerTypeDescription) {
        return removeDescription0(containerTypeDescription);
    }

    protected ContainerTypeDescription removeDescription0(ContainerTypeDescription containerTypeDescription) {
        if (containerTypeDescription == null)
            return null;
        synchronized (containerdescriptions) {
            return (ContainerTypeDescription) containerdescriptions.remove(containerTypeDescription.getName());
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IContainerFactory#getDescriptionsForContainerAdapter(java.lang.Class)
	 */
    public ContainerTypeDescription[] getDescriptionsForContainerAdapter(Class containerAdapter) {
        if (containerAdapter == null)
            //$NON-NLS-1$
            throw new NullPointerException("containerAdapter cannot be null");
        List result = new ArrayList();
        List descriptions = getDescriptions();
        for (Iterator i = descriptions.iterator(); i.hasNext(); ) {
            ContainerTypeDescription description = (ContainerTypeDescription) i.next();
            String[] supportedAdapters = description.getSupportedAdapterTypes();
            if (supportedAdapters != null) {
                for (int j = 0; j < supportedAdapters.length; j++) {
                    if (supportedAdapters[j].equals(containerAdapter.getName()))
                        result.add(description);
                }
            }
        }
        return (ContainerTypeDescription[]) result.toArray(new ContainerTypeDescription[] {});
    }

    protected void throwContainerCreateException(String message, Throwable cause, String method) throws ContainerCreateException {
        ContainerCreateException except = (cause == null) ? new ContainerCreateException(message) : new ContainerCreateException(message, cause);
        Trace.throwing(ECFPlugin.PLUGIN_ID, ECFDebugOptions.EXCEPTIONS_THROWING, ContainerFactory.class, method, except);
        throw except;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IContainerFactory#createContainer()
	 */
    public IContainer createContainer() throws ContainerCreateException {
        return createContainer(BASE_CONTAINER_NAME);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainerFactory#createContainer(org.eclipse.ecf.core.identity.ID)
	 */
    public IContainer createContainer(ID containerID) throws ContainerCreateException {
        if (containerID == null)
            return createContainer();
        return createContainer(BASE_CONTAINER_NAME, new Object[] { containerID });
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainerFactory#createContainer(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
    public IContainer createContainer(ContainerTypeDescription containerTypeDescription) throws ContainerCreateException {
        return createContainer(containerTypeDescription, (Object[]) null);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IContainerFactory#createContainer(java.lang.String)
	 */
    public IContainer createContainer(String containerTypeDescriptionName) throws ContainerCreateException {
        return createContainer(getDescriptionByNameWithException(containerTypeDescriptionName), (Object[]) null);
    }

    private ContainerTypeDescription getDescriptionByNameWithException(String containerTypeDescriptionName) throws ContainerCreateException {
        ContainerTypeDescription typeDescription = getDescriptionByName(containerTypeDescriptionName);
        if (typeDescription == null)
            //$NON-NLS-1$ //$NON-NLS-2$
            throw new ContainerCreateException("Container type description with name=" + containerTypeDescriptionName + " not found.  This may indicate that the desired provider is not available or not startable within runtime.");
        return typeDescription;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IContainerFactory#createContainer(org.eclipse.ecf.core.ContainerTypeDescription,
	 *      java.lang.Object[])
	 */
    public IContainer createContainer(ContainerTypeDescription containerTypeDescription, Object[] parameters) throws ContainerCreateException {
        //$NON-NLS-1$
        String method = "createContainer";
        Trace.entering(ECFPlugin.PLUGIN_ID, ECFDebugOptions.METHODS_ENTERING, ContainerFactory.class, method, new Object[] { containerTypeDescription, Trace.getArgumentsString(parameters) });
        if (containerTypeDescription == null)
            //$NON-NLS-1$
            throwContainerCreateException("ContainerTypeDescription cannot be null", null, method);
        ContainerTypeDescription cd = getDescription0(containerTypeDescription);
        if (cd == null)
            throwContainerCreateException(//$NON-NLS-1$
            "ContainerTypeDescription '" + containerTypeDescription.getName() + //$NON-NLS-1$
            "' not found", //$NON-NLS-1$
            null, //$NON-NLS-1$
            method);
        IContainerInstantiator instantiator = null;
        try {
            instantiator = cd.getInstantiator();
        } catch (Exception e) {
            throwContainerCreateException("createContainer cannot get IContainerInstantiator for description : " + containerTypeDescription, e, method);
        }
        // Ask instantiator to actually create instance
        IContainer container = instantiator.createInstance(containerTypeDescription, parameters);
        if (container == null)
            throwContainerCreateException(//$NON-NLS-1$
            "Instantiator returned null for '" + cd.getName() + //$NON-NLS-1$
            "'", //$NON-NLS-1$
            null, //$NON-NLS-1$
            method);
        // Add to containers map if container.getID() provides a valid value.
        ID containerID = container.getID();
        if (containerID != null)
            addContainer(container, cd);
        Trace.exiting(ECFPlugin.PLUGIN_ID, ECFDebugOptions.METHODS_EXITING, ContainerFactory.class, method, container);
        return container;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IContainerFactory#createContainer(java.lang.String,
	 *      java.lang.Object[])
	 */
    public IContainer createContainer(String containerTypeDescriptionName, Object[] parameters) throws ContainerCreateException {
        return createContainer(getDescriptionByNameWithException(containerTypeDescriptionName), parameters);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainerFactory#createContainer(org.eclipse.ecf.core.ContainerTypeDescription, org.eclipse.ecf.core.identity.ID, java.lang.Object[])
	 */
    public IContainer createContainer(ContainerTypeDescription containerTypeDescription, ID containerID, Object[] parameters) throws ContainerCreateException {
        if (containerID == null)
            return createContainer(containerTypeDescription, parameters);
        Object[] params = (parameters == null || parameters.length == 0) ? new Object[1] : new Object[parameters.length + 1];
        params[0] = containerID;
        if (parameters != null && parameters.length != 0)
            System.arraycopy(parameters, 0, params, 1, parameters.length);
        return createContainer(containerTypeDescription, params);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainerFactory#createContainer(java.lang.String, org.eclipse.ecf.core.identity.ID, java.lang.Object[])
	 */
    public IContainer createContainer(String containerTypeDescriptionName, ID containerID, Object[] parameters) throws ContainerCreateException {
        if (containerID == null)
            return createContainer(containerTypeDescriptionName, parameters);
        return createContainer(getDescriptionByNameWithException(containerTypeDescriptionName), containerID, parameters);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainerFactory#createContainer(org.eclipse.ecf.core.ContainerTypeDescription, org.eclipse.ecf.core.identity.ID)
	 */
    public IContainer createContainer(ContainerTypeDescription containerTypeDescription, ID containerID) throws ContainerCreateException {
        if (containerID == null)
            return createContainer(containerTypeDescription);
        return createContainer(containerTypeDescription, new Object[] { containerID });
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainerFactory#createContainer(java.lang.String, org.eclipse.ecf.core.identity.ID)
	 */
    public IContainer createContainer(String containerTypeDescriptionName, ID containerID) throws ContainerCreateException {
        return createContainer(getDescriptionByNameWithException(containerTypeDescriptionName), new Object[] { containerID });
    }

    /**
	 * @since 3.1
	 */
    public IContainer createContainer(String containerTypeDescriptionName, String containerId) throws ContainerCreateException {
        return createContainer(getDescriptionByNameWithException(containerTypeDescriptionName), containerId);
    }

    /**
	 * @since 3.1
	 */
    public IContainer createContainer(String containerTypeDescriptionName, String containerId, Object[] parameters) throws ContainerCreateException {
        return createContainer(getDescriptionByNameWithException(containerTypeDescriptionName), containerId, parameters);
    }

    /**
	 * @since 3.1
	 */
    public IContainer createContainer(ContainerTypeDescription containerTypeDescription, String containerId) throws ContainerCreateException {
        return createContainer(containerTypeDescription, containerId, (Object[]) null);
    }

    /**
	 * @since 3.1
	 */
    public IContainer createContainer(ContainerTypeDescription containerTypeDescription, String containerId, Object[] parameters) throws ContainerCreateException {
        if (containerId == null)
            return createContainer(containerTypeDescription, parameters);
        Object[] params = (parameters == null || parameters.length == 0) ? new Object[1] : new Object[parameters.length + 1];
        params[0] = containerId;
        if (parameters != null && parameters.length != 0)
            System.arraycopy(parameters, 0, params, 1, parameters.length);
        return createContainer(containerTypeDescription, params);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IContainerManager#getAllContainers()
	 */
    public IContainer[] getAllContainers() {
        List containerValues = new ArrayList();
        synchronized (containers) {
            Collection containerEntrys = containers.values();
            for (Iterator i = containerEntrys.iterator(); i.hasNext(); ) {
                ContainerEntry entry = (ContainerEntry) i.next();
                containerValues.add(entry.getContainer());
            }
        }
        return (IContainer[]) containerValues.toArray(new IContainer[] {});
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IContainerManager#getContainer(org.eclipse.ecf.core.identity.ID)
	 */
    public IContainer getContainer(ID containerID) {
        if (containerID == null)
            return null;
        synchronized (containers) {
            ContainerEntry entry = (ContainerEntry) containers.get(containerID);
            if (entry == null)
                return null;
            return entry.getContainer();
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IContainerManager#hasContainer(org.eclipse.ecf.core.identity.ID)
	 */
    public boolean hasContainer(ID containerID) {
        Assert.isNotNull(containerID);
        synchronized (containers) {
            return containers.containsKey(containerID);
        }
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainerManager#addListener(org.eclipse.ecf.core.IContainerManagerListener)
	 */
    public boolean addListener(IContainerManagerListener listener) {
        Assert.isNotNull(listener);
        synchronized (managerListeners) {
            return managerListeners.add(listener);
        }
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.IContainerManager#removeListener(org.eclipse.ecf.core.IContainerManagerListener)
	 */
    public boolean removeListener(IContainerManagerListener listener) {
        Assert.isNotNull(listener);
        synchronized (managerListeners) {
            return managerListeners.remove(listener);
        }
    }

    public IContainer addContainer(IContainer container, ContainerTypeDescription typeDescription) {
        Assert.isNotNull(container);
        Assert.isNotNull(typeDescription);
        ID containerID = container.getID();
        //$NON-NLS-1$
        Assert.isNotNull(containerID, "Container ID cannot be null");
        ContainerEntry result = null;
        synchronized (containers) {
            result = (ContainerEntry) containers.put(containerID, new ContainerEntry(container, typeDescription));
        }
        if (result == null)
            fireContainerAdded(container);
        return container;
    }

    /**
	 * @param result
	 */
    private void fireContainerAdded(IContainer result) {
        List toNotify = null;
        synchronized (managerListeners) {
            toNotify = new ArrayList(managerListeners);
        }
        for (Iterator i = toNotify.iterator(); i.hasNext(); ) {
            IContainerManagerListener cml = (IContainerManagerListener) i.next();
            cml.containerAdded(result);
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IContainerManager#removeContainer(org.eclipse.ecf.core.IContainer)
	 */
    public IContainer removeContainer(IContainer container) {
        Assert.isNotNull(container);
        ID containerID = container.getID();
        if (containerID == null)
            return null;
        return removeContainer(containerID);
    }

    public IContainer removeContainer(ID containerID) {
        Assert.isNotNull(containerID);
        ContainerEntry result = null;
        synchronized (containers) {
            result = (ContainerEntry) containers.remove(containerID);
        }
        IContainer resultContainer = null;
        if (result != null) {
            resultContainer = result.getContainer();
            fireContainerRemoved(resultContainer);
        }
        return resultContainer;
    }

    /**
	 * @param result
	 */
    private void fireContainerRemoved(IContainer result) {
        List toNotify = null;
        synchronized (managerListeners) {
            toNotify = new ArrayList(managerListeners);
        }
        for (Iterator i = toNotify.iterator(); i.hasNext(); ) {
            IContainerManagerListener cml = (IContainerManagerListener) i.next();
            cml.containerRemoved(result);
        }
    }

    public ContainerTypeDescription getContainerTypeDescription(ID containerID) {
        if (containerID == null)
            return null;
        synchronized (containers) {
            ContainerEntry entry = (ContainerEntry) containers.get(containerID);
            if (entry == null)
                return null;
            return entry.getContainerTypeDescription();
        }
    }

    public IContainerFactory getContainerFactory() {
        return this;
    }

    public void removeAllContainers() {
        synchronized (containers) {
            for (Iterator i = containers.keySet().iterator(); i.hasNext(); ) {
                ID key = (ID) i.next();
                ContainerEntry entry = (ContainerEntry) containers.get(key);
                i.remove();
                fireContainerRemoved(entry.getContainer());
            }
        }
    }

    /**
	 * @since 3.1
	 */
    public IContainer createContainer(ContainerTypeDescription containerTypeDescription, ID containerID, Map parameters) throws ContainerCreateException {
        if (containerID == null)
            return createContainer(containerTypeDescription, parameters);
        if (parameters == null)
            return createContainer(containerTypeDescription, containerID);
        return createContainer(containerTypeDescription, new Object[] { containerID, parameters });
    }

    /**
	 * @since 3.1
	 */
    public IContainer createContainer(ContainerTypeDescription containerTypeDescription, String containerId, Map parameters) throws ContainerCreateException {
        if (containerId == null)
            return createContainer(containerTypeDescription, parameters);
        if (parameters == null)
            return createContainer(containerTypeDescription, containerId);
        return createContainer(containerTypeDescription, new Object[] { containerId, parameters });
    }

    /**
	 * @since 3.1
	 */
    public IContainer createContainer(String containerTypeDescriptionName, ID containerID, Map parameters) throws ContainerCreateException {
        return createContainer(getDescriptionByNameWithException(containerTypeDescriptionName), containerID, parameters);
    }

    /**
	 * @since 3.1
	 */
    public IContainer createContainer(String containerTypeDescriptionName, String containerId, Map parameters) throws ContainerCreateException {
        return createContainer(getDescriptionByNameWithException(containerTypeDescriptionName), containerId, parameters);
    }

    /**
	 * @since 3.1
	 */
    public IContainer createContainer(ContainerTypeDescription containerTypeDescription, Map parameters) throws ContainerCreateException {
        if (parameters == null)
            return createContainer(containerTypeDescription);
        return createContainer(containerTypeDescription, new Object[] { parameters });
    }

    /**
	 * @since 3.1
	 */
    public IContainer createContainer(String containerTypeDescriptionName, Map parameters) throws ContainerCreateException {
        if (parameters == null)
            return createContainer(containerTypeDescriptionName);
        return createContainer(containerTypeDescriptionName, new Object[] { parameters });
    }
}
