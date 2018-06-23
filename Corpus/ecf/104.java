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

import org.eclipse.ecf.discovery.ui.model.IServiceID;
import org.eclipse.ecf.discovery.ui.model.IServiceTypeID;
import org.eclipse.ecf.discovery.ui.model.ModelPackage;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>IService ID</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.impl.IServiceIDImpl#getEcfServiceID <em>Ecf Service ID</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.impl.IServiceIDImpl#getEcfServiceName <em>Ecf Service Name</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.impl.IServiceIDImpl#getServiceTypeID <em>Service Type ID</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class IServiceIDImpl extends EObjectImpl implements IServiceID {

    /**
	 * The default value of the '{@link #getEcfServiceID() <em>Ecf Service ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEcfServiceID()
	 * @generated
	 * @ordered
	 */
    protected static final org.eclipse.ecf.discovery.identity.IServiceID ECF_SERVICE_ID_EDEFAULT = null;

    /**
	 * The cached value of the '{@link #getEcfServiceID() <em>Ecf Service ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEcfServiceID()
	 * @generated
	 * @ordered
	 */
    protected org.eclipse.ecf.discovery.identity.IServiceID ecfServiceID = ECF_SERVICE_ID_EDEFAULT;

    /**
	 * The default value of the '{@link #getEcfServiceName() <em>Ecf Service Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEcfServiceName()
	 * @generated
	 * @ordered
	 */
    protected static final String ECF_SERVICE_NAME_EDEFAULT = null;

    /**
	 * The cached value of the '{@link #getEcfServiceName() <em>Ecf Service Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEcfServiceName()
	 * @generated
	 * @ordered
	 */
    protected String ecfServiceName = ECF_SERVICE_NAME_EDEFAULT;

    /**
	 * The cached value of the '{@link #getServiceTypeID() <em>Service Type ID</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getServiceTypeID()
	 * @generated
	 * @ordered
	 */
    protected IServiceTypeID serviceTypeID;

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    protected  IServiceIDImpl() {
        super();
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    protected EClass eStaticClass() {
        return ModelPackage.Literals.ISERVICE_ID;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public org.eclipse.ecf.discovery.identity.IServiceID getEcfServiceID() {
        return ecfServiceID;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public void setEcfServiceID(org.eclipse.ecf.discovery.identity.IServiceID newEcfServiceID) {
        org.eclipse.ecf.discovery.identity.IServiceID oldEcfServiceID = ecfServiceID;
        ecfServiceID = newEcfServiceID;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.ISERVICE_ID__ECF_SERVICE_ID, oldEcfServiceID, ecfServiceID));
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public String getEcfServiceName() {
        return ecfServiceName;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public void setEcfServiceName(String newEcfServiceName) {
        String oldEcfServiceName = ecfServiceName;
        ecfServiceName = newEcfServiceName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.ISERVICE_ID__ECF_SERVICE_NAME, oldEcfServiceName, ecfServiceName));
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public IServiceTypeID getServiceTypeID() {
        if (serviceTypeID != null && serviceTypeID.eIsProxy()) {
            InternalEObject oldServiceTypeID = (InternalEObject) serviceTypeID;
            serviceTypeID = (IServiceTypeID) eResolveProxy(oldServiceTypeID);
            if (serviceTypeID != oldServiceTypeID) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, ModelPackage.ISERVICE_ID__SERVICE_TYPE_ID, oldServiceTypeID, serviceTypeID));
            }
        }
        return serviceTypeID;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public IServiceTypeID basicGetServiceTypeID() {
        return serviceTypeID;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public void setServiceTypeID(IServiceTypeID newServiceTypeID) {
        IServiceTypeID oldServiceTypeID = serviceTypeID;
        serviceTypeID = newServiceTypeID;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.ISERVICE_ID__SERVICE_TYPE_ID, oldServiceTypeID, serviceTypeID));
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch(featureID) {
            case ModelPackage.ISERVICE_ID__ECF_SERVICE_ID:
                return getEcfServiceID();
            case ModelPackage.ISERVICE_ID__ECF_SERVICE_NAME:
                return getEcfServiceName();
            case ModelPackage.ISERVICE_ID__SERVICE_TYPE_ID:
                if (resolve)
                    return getServiceTypeID();
                return basicGetServiceTypeID();
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
            case ModelPackage.ISERVICE_ID__ECF_SERVICE_ID:
                setEcfServiceID((org.eclipse.ecf.discovery.identity.IServiceID) newValue);
                return;
            case ModelPackage.ISERVICE_ID__ECF_SERVICE_NAME:
                setEcfServiceName((String) newValue);
                return;
            case ModelPackage.ISERVICE_ID__SERVICE_TYPE_ID:
                setServiceTypeID((IServiceTypeID) newValue);
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
            case ModelPackage.ISERVICE_ID__ECF_SERVICE_ID:
                setEcfServiceID(ECF_SERVICE_ID_EDEFAULT);
                return;
            case ModelPackage.ISERVICE_ID__ECF_SERVICE_NAME:
                setEcfServiceName(ECF_SERVICE_NAME_EDEFAULT);
                return;
            case ModelPackage.ISERVICE_ID__SERVICE_TYPE_ID:
                setServiceTypeID((IServiceTypeID) null);
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
            case ModelPackage.ISERVICE_ID__ECF_SERVICE_ID:
                return ECF_SERVICE_ID_EDEFAULT == null ? ecfServiceID != null : !ECF_SERVICE_ID_EDEFAULT.equals(ecfServiceID);
            case ModelPackage.ISERVICE_ID__ECF_SERVICE_NAME:
                return ECF_SERVICE_NAME_EDEFAULT == null ? ecfServiceName != null : !ECF_SERVICE_NAME_EDEFAULT.equals(ecfServiceName);
            case ModelPackage.ISERVICE_ID__SERVICE_TYPE_ID:
                return serviceTypeID != null;
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
        result.append(" (ecfServiceID: ");
        result.append(ecfServiceID);
        //$NON-NLS-1$
        result.append(", ecfServiceName: ");
        result.append(ecfServiceName);
        result.append(')');
        return result.toString();
    }
}
//IServiceIDImpl
