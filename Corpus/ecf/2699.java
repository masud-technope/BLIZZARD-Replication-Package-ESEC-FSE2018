/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.sharedobject;

import java.util.*;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.sharedobject.provider.ISharedObjectInstantiator;
import org.eclipse.ecf.core.status.SerializableStatus;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.core.sharedobject.Activator;
import org.eclipse.ecf.internal.core.sharedobject.SharedObjectDebugOptions;

/**
 * Factory for creating {@link ISharedObject} instances. This class provides ECF
 * clients an entry point to constructing {@link ISharedObject} instances. <br>
 */
public class SharedObjectFactory implements ISharedObjectFactory {

    private static Hashtable sharedobjectdescriptions = new Hashtable();

    protected static ISharedObjectFactory instance = null;

    static {
        instance = new SharedObjectFactory();
    }

    protected  SharedObjectFactory() {
    // null constructor
    }

    public static ISharedObjectFactory getDefault() {
        return instance;
    }

    private static void trace(String msg) {
        Trace.trace(Activator.PLUGIN_ID, msg);
    }

    private static void dumpStack(String msg, Throwable e) {
        //$NON-NLS-1$
        Trace.catching(Activator.PLUGIN_ID, SharedObjectDebugOptions.EXCEPTIONS_CATCHING, SharedObjectFactory.class, "dumpStack", e);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectFactory#addDescription(org.eclipse.ecf.core.SharedObjectTypeDescription)
	 */
    public SharedObjectTypeDescription addDescription(SharedObjectTypeDescription description) {
        //$NON-NLS-1$ //$NON-NLS-2$
        trace("addDescription(" + description + ")");
        return addDescription0(description);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectFactory#getDescriptions()
	 */
    public List getDescriptions() {
        return getDescriptions0();
    }

    @SuppressWarnings("unchecked")
    protected List getDescriptions0() {
        return new ArrayList(sharedobjectdescriptions.values());
    }

    @SuppressWarnings("unchecked")
    protected SharedObjectTypeDescription addDescription0(SharedObjectTypeDescription n) {
        if (n == null)
            return null;
        return (SharedObjectTypeDescription) sharedobjectdescriptions.put(n.getName(), n);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectFactory#containsDescription(org.eclipse.ecf.core.SharedObjectTypeDescription)
	 */
    public boolean containsDescription(SharedObjectTypeDescription scd) {
        return containsDescription0(scd);
    }

    protected boolean containsDescription0(SharedObjectTypeDescription scd) {
        if (scd == null)
            return false;
        return sharedobjectdescriptions.containsKey(scd.getName());
    }

    protected SharedObjectTypeDescription getDescription0(SharedObjectTypeDescription scd) {
        if (scd == null)
            return null;
        return (SharedObjectTypeDescription) sharedobjectdescriptions.get(scd.getName());
    }

    protected SharedObjectTypeDescription getDescription0(String name) {
        if (name == null)
            return null;
        return (SharedObjectTypeDescription) sharedobjectdescriptions.get(name);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContainerFactory#getDescriptionByName(java.lang.String)
	 */
    public SharedObjectTypeDescription getDescriptionByName(String name) throws SharedObjectCreateException {
        //$NON-NLS-1$ //$NON-NLS-2$
        trace("getDescriptionByName(" + name + ")");
        SharedObjectTypeDescription res = getDescription0(name);
        if (res == null)
            //$NON-NLS-1$ //$NON-NLS-2$
            throwSharedObjectCreateException("SharedObjectDescription named " + name + " not found");
        return res;
    }

    private void throwSharedObjectCreateException(String message, Throwable exception) throws SharedObjectCreateException {
        //$NON-NLS-1$
        throw new SharedObjectCreateException(new SerializableStatus(IStatus.ERROR, "org.eclipse.ecf.sharedobject", message, exception));
    }

    private void throwSharedObjectCreateException(String message) throws SharedObjectCreateException {
        throwSharedObjectCreateException(message, null);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContainerFactory#createSharedObject(org.eclipse.ecf.core.SharedObjectTypeDescription,
	 *      java.lang.Object[])
	 */
    public ISharedObject createSharedObject(SharedObjectTypeDescription desc, Object[] args) throws SharedObjectCreateException {
        trace(//$NON-NLS-1$ //$NON-NLS-2$
        "createSharedObject(" + desc + "," + Trace.getArgumentsString(args) + //$NON-NLS-1$
        ")");
        if (desc == null)
            //$NON-NLS-1$
            throwSharedObjectCreateException("SharedObjectTypeDescription cannot be null");
        SharedObjectTypeDescription cd = getDescription0(desc);
        if (cd == null)
            //throw new SharedObjectCreateException(Messages.SharedObjectFactory_Exception_Create_Shared_Objec + desc.getName() + Messages.SharedObjectFactory_Exception_Create_Shared_Object_Not_Found);
            //$NON-NLS-1$ //$NON-NLS-2$
            throwSharedObjectCreateException("SharedObjectDescription named " + desc.getName() + " not found");
        ISharedObjectInstantiator instantiator = null;
        try {
            instantiator = cd.getInstantiator();
        } catch (Exception e) {
            dumpStack("Exception in createSharedObject", e);
            throwSharedObjectCreateException("createSharedObject exception with description" + desc, e);
        }
        if (instantiator == null)
            //throw new SharedObjectCreateException(Messages.SharedObjectFactory_Exception_Create_Instantiator + cd.getName() + Messages.SharedObjectFactory_Exception_Create_Instantiator_Null);
            //$NON-NLS-1$ //$NON-NLS-2$
            throwSharedObjectCreateException("Instantiator for SharedObjectDescription " + cd.getName() + " is null");
        // Ask instantiator to actually create instance
        return instantiator.createInstance(desc, args);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContainerFactory#createSharedObject(java.lang.String)
	 */
    public ISharedObject createSharedObject(String descriptionName) throws SharedObjectCreateException {
        return createSharedObject(getDescriptionByName(descriptionName), null);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContainerFactory#createSharedObject(java.lang.String,
	 *      java.lang.Object[])
	 */
    public ISharedObject createSharedObject(String descriptionName, Object[] args) throws SharedObjectCreateException {
        return createSharedObject(getDescriptionByName(descriptionName), args);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContainerFactory#removeDescription(org.eclipse.ecf.core.SharedObjectTypeDescription)
	 */
    public SharedObjectTypeDescription removeDescription(SharedObjectTypeDescription scd) {
        //$NON-NLS-1$ //$NON-NLS-2$
        trace("removeDescription(" + scd + ")");
        return removeDescription0(scd);
    }

    protected SharedObjectTypeDescription removeDescription0(SharedObjectTypeDescription n) {
        if (n == null)
            return null;
        return (SharedObjectTypeDescription) sharedobjectdescriptions.remove(n.getName());
    }
}
