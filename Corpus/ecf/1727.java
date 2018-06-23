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
package org.eclipse.ecf.discovery.ui.model.util;

import org.eclipse.ecf.discovery.ui.model.*;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage
 * @generated
 */
public class ModelAdapterFactory extends AdapterFactoryImpl {

    /**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    protected static ModelPackage modelPackage;

    /**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public  ModelAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = ModelPackage.eINSTANCE;
        }
    }

    /**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
    public boolean isFactoryForType(Object object) {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject) object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

    /**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    protected ModelSwitch modelSwitch = new ModelSwitch() {

        public Object caseIServiceInfo(IServiceInfo object) {
            return createIServiceInfoAdapter();
        }

        public Object caseINetwork(INetwork object) {
            return createINetworkAdapter();
        }

        public Object caseIHost(IHost object) {
            return createIHostAdapter();
        }

        public Object caseIServiceID(IServiceID object) {
            return createIServiceIDAdapter();
        }

        public Object caseIServiceTypeID(IServiceTypeID object) {
            return createIServiceTypeIDAdapter();
        }

        public Object defaultCase(EObject object) {
            return createEObjectAdapter();
        }
    };

    /**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
    public Adapter createAdapter(Notifier target) {
        return (Adapter) modelSwitch.doSwitch((EObject) target);
    }

    /**
	 * Creates a new adapter for an object of class '{@link org.eclipse.ecf.discovery.ui.model.IServiceInfo <em>IService Info</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.ecf.discovery.ui.model.IServiceInfo
	 * @generated
	 */
    public Adapter createIServiceInfoAdapter() {
        return null;
    }

    /**
	 * Creates a new adapter for an object of class '{@link org.eclipse.ecf.discovery.ui.model.INetwork <em>INetwork</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.ecf.discovery.ui.model.INetwork
	 * @generated
	 */
    public Adapter createINetworkAdapter() {
        return null;
    }

    /**
	 * Creates a new adapter for an object of class '{@link org.eclipse.ecf.discovery.ui.model.IHost <em>IHost</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.ecf.discovery.ui.model.IHost
	 * @generated
	 */
    public Adapter createIHostAdapter() {
        return null;
    }

    /**
	 * Creates a new adapter for an object of class '{@link org.eclipse.ecf.discovery.ui.model.IServiceID <em>IService ID</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.ecf.discovery.ui.model.IServiceID
	 * @generated
	 */
    public Adapter createIServiceIDAdapter() {
        return null;
    }

    /**
	 * Creates a new adapter for an object of class '{@link org.eclipse.ecf.discovery.ui.model.IServiceTypeID <em>IService Type ID</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.ecf.discovery.ui.model.IServiceTypeID
	 * @generated
	 */
    public Adapter createIServiceTypeIDAdapter() {
        return null;
    }

    /**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
    public Adapter createEObjectAdapter() {
        return null;
    }
}
//ModelAdapterFactory
