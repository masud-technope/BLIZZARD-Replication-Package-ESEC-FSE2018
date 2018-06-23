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
package org.eclipse.ecf.discovery.ui.model.impl;

import org.eclipse.ecf.discovery.ui.model.IHost;
import org.eclipse.ecf.discovery.ui.model.INetwork;
import org.eclipse.ecf.discovery.ui.model.ModelPackage;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>INetwork</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.impl.INetworkImpl#getHosts <em>Hosts</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class INetworkImpl extends EObjectImpl implements INetwork {

    /**
	 * The cached value of the '{@link #getHosts() <em>Hosts</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHosts()
	 * @generated
	 * @ordered
	 */
    protected EList hosts;

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    protected  INetworkImpl() {
        super();
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    protected EClass eStaticClass() {
        return ModelPackage.Literals.INETWORK;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EList getHosts() {
        if (hosts == null) {
            hosts = new EObjectContainmentEList.Resolving(IHost.class, this, ModelPackage.INETWORK__HOSTS);
        }
        return hosts;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch(featureID) {
            case ModelPackage.INETWORK__HOSTS:
                return ((InternalEList) getHosts()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch(featureID) {
            case ModelPackage.INETWORK__HOSTS:
                return getHosts();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public boolean eIsSet(int featureID) {
        switch(featureID) {
            case ModelPackage.INETWORK__HOSTS:
                return hosts != null && !hosts.isEmpty();
        }
        return super.eIsSet(featureID);
    }
}
//INetworkImpl
