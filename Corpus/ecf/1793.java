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

import java.net.URI;
import org.eclipse.ecf.discovery.ui.model.IServiceID;
import org.eclipse.ecf.discovery.ui.model.IServiceInfo;
import org.eclipse.ecf.discovery.ui.model.ModelPackage;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>IService Info</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.impl.IServiceInfoImpl#getEcfServiceInfo <em>Ecf Service Info</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.impl.IServiceInfoImpl#getEcfName <em>Ecf Name</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.impl.IServiceInfoImpl#getEcfLocation <em>Ecf Location</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.impl.IServiceInfoImpl#getEcfPriority <em>Ecf Priority</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.impl.IServiceInfoImpl#getEcfWeight <em>Ecf Weight</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.impl.IServiceInfoImpl#getServiceID <em>Service ID</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class IServiceInfoImpl extends EObjectImpl implements IServiceInfo {

    /**
	 * The default value of the '{@link #getEcfServiceInfo() <em>Ecf Service Info</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEcfServiceInfo()
	 * @generated
	 * @ordered
	 */
    protected static final org.eclipse.ecf.discovery.IServiceInfo ECF_SERVICE_INFO_EDEFAULT = null;

    /**
	 * The cached value of the '{@link #getEcfServiceInfo() <em>Ecf Service Info</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEcfServiceInfo()
	 * @generated
	 * @ordered
	 */
    protected org.eclipse.ecf.discovery.IServiceInfo ecfServiceInfo = ECF_SERVICE_INFO_EDEFAULT;

    /**
	 * The default value of the '{@link #getEcfName() <em>Ecf Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEcfName()
	 * @generated
	 * @ordered
	 */
    protected static final String ECF_NAME_EDEFAULT = null;

    /**
	 * The cached value of the '{@link #getEcfName() <em>Ecf Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEcfName()
	 * @generated
	 * @ordered
	 */
    protected String ecfName = ECF_NAME_EDEFAULT;

    /**
	 * The default value of the '{@link #getEcfLocation() <em>Ecf Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEcfLocation()
	 * @generated
	 * @ordered
	 */
    protected static final URI ECF_LOCATION_EDEFAULT = null;

    /**
	 * The cached value of the '{@link #getEcfLocation() <em>Ecf Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEcfLocation()
	 * @generated
	 * @ordered
	 */
    protected URI ecfLocation = ECF_LOCATION_EDEFAULT;

    /**
	 * The default value of the '{@link #getEcfPriority() <em>Ecf Priority</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEcfPriority()
	 * @generated
	 * @ordered
	 */
    protected static final int ECF_PRIORITY_EDEFAULT = 0;

    /**
	 * The cached value of the '{@link #getEcfPriority() <em>Ecf Priority</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEcfPriority()
	 * @generated
	 * @ordered
	 */
    protected int ecfPriority = ECF_PRIORITY_EDEFAULT;

    /**
	 * The default value of the '{@link #getEcfWeight() <em>Ecf Weight</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEcfWeight()
	 * @generated
	 * @ordered
	 */
    protected static final int ECF_WEIGHT_EDEFAULT = 0;

    /**
	 * The cached value of the '{@link #getEcfWeight() <em>Ecf Weight</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEcfWeight()
	 * @generated
	 * @ordered
	 */
    protected int ecfWeight = ECF_WEIGHT_EDEFAULT;

    /**
	 * The cached value of the '{@link #getServiceID() <em>Service ID</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getServiceID()
	 * @generated
	 * @ordered
	 */
    protected IServiceID serviceID;

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    protected  IServiceInfoImpl() {
        super();
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    protected EClass eStaticClass() {
        return ModelPackage.Literals.ISERVICE_INFO;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public org.eclipse.ecf.discovery.IServiceInfo getEcfServiceInfo() {
        return ecfServiceInfo;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public void setEcfServiceInfo(org.eclipse.ecf.discovery.IServiceInfo newEcfServiceInfo) {
        org.eclipse.ecf.discovery.IServiceInfo oldEcfServiceInfo = ecfServiceInfo;
        ecfServiceInfo = newEcfServiceInfo;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.ISERVICE_INFO__ECF_SERVICE_INFO, oldEcfServiceInfo, ecfServiceInfo));
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public String getEcfName() {
        return ecfName;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public void setEcfName(String newEcfName) {
        String oldEcfName = ecfName;
        ecfName = newEcfName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.ISERVICE_INFO__ECF_NAME, oldEcfName, ecfName));
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public URI getEcfLocation() {
        return ecfLocation;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public void setEcfLocation(URI newEcfLocation) {
        URI oldEcfLocation = ecfLocation;
        ecfLocation = newEcfLocation;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.ISERVICE_INFO__ECF_LOCATION, oldEcfLocation, ecfLocation));
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public int getEcfPriority() {
        return ecfPriority;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public void setEcfPriority(int newEcfPriority) {
        int oldEcfPriority = ecfPriority;
        ecfPriority = newEcfPriority;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.ISERVICE_INFO__ECF_PRIORITY, oldEcfPriority, ecfPriority));
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public int getEcfWeight() {
        return ecfWeight;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public void setEcfWeight(int newEcfWeight) {
        int oldEcfWeight = ecfWeight;
        ecfWeight = newEcfWeight;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.ISERVICE_INFO__ECF_WEIGHT, oldEcfWeight, ecfWeight));
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public IServiceID getServiceID() {
        if (serviceID != null && serviceID.eIsProxy()) {
            InternalEObject oldServiceID = (InternalEObject) serviceID;
            serviceID = (IServiceID) eResolveProxy(oldServiceID);
            if (serviceID != oldServiceID) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, ModelPackage.ISERVICE_INFO__SERVICE_ID, oldServiceID, serviceID));
            }
        }
        return serviceID;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public IServiceID basicGetServiceID() {
        return serviceID;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public void setServiceID(IServiceID newServiceID) {
        IServiceID oldServiceID = serviceID;
        serviceID = newServiceID;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.ISERVICE_INFO__SERVICE_ID, oldServiceID, serviceID));
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch(featureID) {
            case ModelPackage.ISERVICE_INFO__ECF_SERVICE_INFO:
                return getEcfServiceInfo();
            case ModelPackage.ISERVICE_INFO__ECF_NAME:
                return getEcfName();
            case ModelPackage.ISERVICE_INFO__ECF_LOCATION:
                return getEcfLocation();
            case ModelPackage.ISERVICE_INFO__ECF_PRIORITY:
                return new Integer(getEcfPriority());
            case ModelPackage.ISERVICE_INFO__ECF_WEIGHT:
                return new Integer(getEcfWeight());
            case ModelPackage.ISERVICE_INFO__SERVICE_ID:
                if (resolve)
                    return getServiceID();
                return basicGetServiceID();
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
            case ModelPackage.ISERVICE_INFO__ECF_SERVICE_INFO:
                setEcfServiceInfo((org.eclipse.ecf.discovery.IServiceInfo) newValue);
                return;
            case ModelPackage.ISERVICE_INFO__ECF_NAME:
                setEcfName((String) newValue);
                return;
            case ModelPackage.ISERVICE_INFO__ECF_LOCATION:
                setEcfLocation((URI) newValue);
                return;
            case ModelPackage.ISERVICE_INFO__ECF_PRIORITY:
                setEcfPriority(((Integer) newValue).intValue());
                return;
            case ModelPackage.ISERVICE_INFO__ECF_WEIGHT:
                setEcfWeight(((Integer) newValue).intValue());
                return;
            case ModelPackage.ISERVICE_INFO__SERVICE_ID:
                setServiceID((IServiceID) newValue);
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
            case ModelPackage.ISERVICE_INFO__ECF_SERVICE_INFO:
                setEcfServiceInfo(ECF_SERVICE_INFO_EDEFAULT);
                return;
            case ModelPackage.ISERVICE_INFO__ECF_NAME:
                setEcfName(ECF_NAME_EDEFAULT);
                return;
            case ModelPackage.ISERVICE_INFO__ECF_LOCATION:
                setEcfLocation(ECF_LOCATION_EDEFAULT);
                return;
            case ModelPackage.ISERVICE_INFO__ECF_PRIORITY:
                setEcfPriority(ECF_PRIORITY_EDEFAULT);
                return;
            case ModelPackage.ISERVICE_INFO__ECF_WEIGHT:
                setEcfWeight(ECF_WEIGHT_EDEFAULT);
                return;
            case ModelPackage.ISERVICE_INFO__SERVICE_ID:
                setServiceID((IServiceID) null);
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
            case ModelPackage.ISERVICE_INFO__ECF_SERVICE_INFO:
                return ECF_SERVICE_INFO_EDEFAULT == null ? ecfServiceInfo != null : !ECF_SERVICE_INFO_EDEFAULT.equals(ecfServiceInfo);
            case ModelPackage.ISERVICE_INFO__ECF_NAME:
                return ECF_NAME_EDEFAULT == null ? ecfName != null : !ECF_NAME_EDEFAULT.equals(ecfName);
            case ModelPackage.ISERVICE_INFO__ECF_LOCATION:
                return ECF_LOCATION_EDEFAULT == null ? ecfLocation != null : !ECF_LOCATION_EDEFAULT.equals(ecfLocation);
            case ModelPackage.ISERVICE_INFO__ECF_PRIORITY:
                return ecfPriority != ECF_PRIORITY_EDEFAULT;
            case ModelPackage.ISERVICE_INFO__ECF_WEIGHT:
                return ecfWeight != ECF_WEIGHT_EDEFAULT;
            case ModelPackage.ISERVICE_INFO__SERVICE_ID:
                return serviceID != null;
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
        result.append(" (ecfServiceInfo: ");
        result.append(ecfServiceInfo);
        //$NON-NLS-1$
        result.append(", ecfName: ");
        result.append(ecfName);
        //$NON-NLS-1$
        result.append(", ecfLocation: ");
        result.append(ecfLocation);
        //$NON-NLS-1$
        result.append(", ecfPriority: ");
        result.append(ecfPriority);
        //$NON-NLS-1$
        result.append(", ecfWeight: ");
        result.append(ecfWeight);
        result.append(')');
        return result.toString();
    }
}
//IServiceInfoImpl
