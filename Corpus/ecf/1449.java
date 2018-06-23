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

import java.net.InetAddress;
import java.util.Collection;
import org.eclipse.ecf.discovery.ui.model.IHost;
import org.eclipse.ecf.discovery.ui.model.IServiceInfo;
import org.eclipse.ecf.discovery.ui.model.ModelFactory;
import org.eclipse.ecf.discovery.ui.model.ModelPackage;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>IHost</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.impl.IHostImpl#getServices <em>Services</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.impl.IHostImpl#getAddress <em>Address</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.impl.IHostImpl#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class IHostImpl extends EObjectImpl implements IHost {

    /**
	 * The cached value of the '{@link #getServices() <em>Services</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getServices()
	 * @generated
	 * @ordered
	 */
    protected EList services;

    /**
	 * The default value of the '{@link #getAddress() <em>Address</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAddress()
	 * @generated
	 * @ordered
	 */
    protected static final InetAddress ADDRESS_EDEFAULT = (InetAddress) ModelFactory.eINSTANCE.createFromString(ModelPackage.eINSTANCE.getInetAddress(), "127.0.0.1");

    /**
	 * The cached value of the '{@link #getAddress() <em>Address</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAddress()
	 * @generated
	 * @ordered
	 */
    protected InetAddress address = ADDRESS_EDEFAULT;

    /**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
    protected static final String NAME_EDEFAULT = "";

    /**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
    protected String name = NAME_EDEFAULT;

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    protected  IHostImpl() {
        super();
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    protected EClass eStaticClass() {
        return ModelPackage.Literals.IHOST;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EList getServices() {
        if (services == null) {
            services = new EObjectContainmentEList.Resolving(IServiceInfo.class, this, ModelPackage.IHOST__SERVICES);
        }
        return services;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public InetAddress getAddress() {
        return address;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public void setAddress(InetAddress newAddress) {
        InetAddress oldAddress = address;
        address = newAddress;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.IHOST__ADDRESS, oldAddress, address));
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public String getName() {
        return name;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public void setName(String newName) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.IHOST__NAME, oldName, name));
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch(featureID) {
            case ModelPackage.IHOST__SERVICES:
                return ((InternalEList) getServices()).basicRemove(otherEnd, msgs);
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
            case ModelPackage.IHOST__SERVICES:
                return getServices();
            case ModelPackage.IHOST__ADDRESS:
                return getAddress();
            case ModelPackage.IHOST__NAME:
                return getName();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public void eSet(int featureID, Object newValue) {
        switch(featureID) {
            case ModelPackage.IHOST__SERVICES:
                getServices().clear();
                getServices().addAll((Collection) newValue);
                return;
            case ModelPackage.IHOST__ADDRESS:
                setAddress((InetAddress) newValue);
                return;
            case ModelPackage.IHOST__NAME:
                setName((String) newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public void eUnset(int featureID) {
        switch(featureID) {
            case ModelPackage.IHOST__SERVICES:
                getServices().clear();
                return;
            case ModelPackage.IHOST__ADDRESS:
                setAddress(ADDRESS_EDEFAULT);
                return;
            case ModelPackage.IHOST__NAME:
                setName(NAME_EDEFAULT);
                return;
        }
        super.eUnset(featureID);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public boolean eIsSet(int featureID) {
        switch(featureID) {
            case ModelPackage.IHOST__SERVICES:
                return services != null && !services.isEmpty();
            case ModelPackage.IHOST__ADDRESS:
                return ADDRESS_EDEFAULT == null ? address != null : !ADDRESS_EDEFAULT.equals(address);
            case ModelPackage.IHOST__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
        }
        return super.eIsSet(featureID);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public String toString() {
        if (eIsProxy())
            return super.toString();
        StringBuffer result = new StringBuffer(super.toString());
        //$NON-NLS-1$
        result.append(" (address: ");
        result.append(address);
        //$NON-NLS-1$
        result.append(", name: ");
        result.append(name);
        result.append(')');
        return result.toString();
    }
}
//IHostImpl
