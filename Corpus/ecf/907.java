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

import java.util.Collection;
import org.eclipse.ecf.discovery.ui.model.IServiceTypeID;
import org.eclipse.ecf.discovery.ui.model.ModelPackage;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>IService Type ID</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.impl.IServiceTypeIDImpl#getEcfServiceTypeID <em>Ecf Service Type ID</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.impl.IServiceTypeIDImpl#getEcfNamingAuthority <em>Ecf Naming Authority</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.impl.IServiceTypeIDImpl#getEcfServices <em>Ecf Services</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.impl.IServiceTypeIDImpl#getEcfProtocols <em>Ecf Protocols</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.impl.IServiceTypeIDImpl#getEcfScopes <em>Ecf Scopes</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.impl.IServiceTypeIDImpl#getEcfServiceName <em>Ecf Service Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class IServiceTypeIDImpl extends EObjectImpl implements IServiceTypeID {

    /**
	 * The default value of the '{@link #getEcfServiceTypeID() <em>Ecf Service Type ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEcfServiceTypeID()
	 * @generated
	 * @ordered
	 */
    protected static final org.eclipse.ecf.discovery.identity.IServiceTypeID ECF_SERVICE_TYPE_ID_EDEFAULT = null;

    /**
	 * The cached value of the '{@link #getEcfServiceTypeID() <em>Ecf Service Type ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEcfServiceTypeID()
	 * @generated
	 * @ordered
	 */
    protected org.eclipse.ecf.discovery.identity.IServiceTypeID ecfServiceTypeID = ECF_SERVICE_TYPE_ID_EDEFAULT;

    /**
	 * The default value of the '{@link #getEcfNamingAuthority() <em>Ecf Naming Authority</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEcfNamingAuthority()
	 * @generated
	 * @ordered
	 */
    protected static final String ECF_NAMING_AUTHORITY_EDEFAULT = null;

    /**
	 * The cached value of the '{@link #getEcfNamingAuthority() <em>Ecf Naming Authority</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEcfNamingAuthority()
	 * @generated
	 * @ordered
	 */
    protected String ecfNamingAuthority = ECF_NAMING_AUTHORITY_EDEFAULT;

    /**
	 * The cached value of the '{@link #getEcfServices() <em>Ecf Services</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEcfServices()
	 * @generated
	 * @ordered
	 */
    protected EList ecfServices;

    /**
	 * The cached value of the '{@link #getEcfProtocols() <em>Ecf Protocols</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEcfProtocols()
	 * @generated
	 * @ordered
	 */
    protected EList ecfProtocols;

    /**
	 * The cached value of the '{@link #getEcfScopes() <em>Ecf Scopes</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEcfScopes()
	 * @generated
	 * @ordered
	 */
    protected EList ecfScopes;

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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    protected  IServiceTypeIDImpl() {
        super();
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    protected EClass eStaticClass() {
        return ModelPackage.Literals.ISERVICE_TYPE_ID;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public org.eclipse.ecf.discovery.identity.IServiceTypeID getEcfServiceTypeID() {
        return ecfServiceTypeID;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public void setEcfServiceTypeID(org.eclipse.ecf.discovery.identity.IServiceTypeID newEcfServiceTypeID) {
        org.eclipse.ecf.discovery.identity.IServiceTypeID oldEcfServiceTypeID = ecfServiceTypeID;
        ecfServiceTypeID = newEcfServiceTypeID;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.ISERVICE_TYPE_ID__ECF_SERVICE_TYPE_ID, oldEcfServiceTypeID, ecfServiceTypeID));
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public String getEcfNamingAuthority() {
        return ecfNamingAuthority;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public void setEcfNamingAuthority(String newEcfNamingAuthority) {
        String oldEcfNamingAuthority = ecfNamingAuthority;
        ecfNamingAuthority = newEcfNamingAuthority;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.ISERVICE_TYPE_ID__ECF_NAMING_AUTHORITY, oldEcfNamingAuthority, ecfNamingAuthority));
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EList getEcfServices() {
        if (ecfServices == null) {
            ecfServices = new EDataTypeUniqueEList(String.class, this, ModelPackage.ISERVICE_TYPE_ID__ECF_SERVICES);
        }
        return ecfServices;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EList getEcfProtocols() {
        if (ecfProtocols == null) {
            ecfProtocols = new EDataTypeUniqueEList(String.class, this, ModelPackage.ISERVICE_TYPE_ID__ECF_PROTOCOLS);
        }
        return ecfProtocols;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EList getEcfScopes() {
        if (ecfScopes == null) {
            ecfScopes = new EDataTypeUniqueEList(String.class, this, ModelPackage.ISERVICE_TYPE_ID__ECF_SCOPES);
        }
        return ecfScopes;
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
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.ISERVICE_TYPE_ID__ECF_SERVICE_NAME, oldEcfServiceName, ecfServiceName));
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch(featureID) {
            case ModelPackage.ISERVICE_TYPE_ID__ECF_SERVICE_TYPE_ID:
                return getEcfServiceTypeID();
            case ModelPackage.ISERVICE_TYPE_ID__ECF_NAMING_AUTHORITY:
                return getEcfNamingAuthority();
            case ModelPackage.ISERVICE_TYPE_ID__ECF_SERVICES:
                return getEcfServices();
            case ModelPackage.ISERVICE_TYPE_ID__ECF_PROTOCOLS:
                return getEcfProtocols();
            case ModelPackage.ISERVICE_TYPE_ID__ECF_SCOPES:
                return getEcfScopes();
            case ModelPackage.ISERVICE_TYPE_ID__ECF_SERVICE_NAME:
                return getEcfServiceName();
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
            case ModelPackage.ISERVICE_TYPE_ID__ECF_SERVICE_TYPE_ID:
                setEcfServiceTypeID((org.eclipse.ecf.discovery.identity.IServiceTypeID) newValue);
                return;
            case ModelPackage.ISERVICE_TYPE_ID__ECF_NAMING_AUTHORITY:
                setEcfNamingAuthority((String) newValue);
                return;
            case ModelPackage.ISERVICE_TYPE_ID__ECF_SERVICES:
                getEcfServices().clear();
                getEcfServices().addAll((Collection) newValue);
                return;
            case ModelPackage.ISERVICE_TYPE_ID__ECF_PROTOCOLS:
                getEcfProtocols().clear();
                getEcfProtocols().addAll((Collection) newValue);
                return;
            case ModelPackage.ISERVICE_TYPE_ID__ECF_SCOPES:
                getEcfScopes().clear();
                getEcfScopes().addAll((Collection) newValue);
                return;
            case ModelPackage.ISERVICE_TYPE_ID__ECF_SERVICE_NAME:
                setEcfServiceName((String) newValue);
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
            case ModelPackage.ISERVICE_TYPE_ID__ECF_SERVICE_TYPE_ID:
                setEcfServiceTypeID(ECF_SERVICE_TYPE_ID_EDEFAULT);
                return;
            case ModelPackage.ISERVICE_TYPE_ID__ECF_NAMING_AUTHORITY:
                setEcfNamingAuthority(ECF_NAMING_AUTHORITY_EDEFAULT);
                return;
            case ModelPackage.ISERVICE_TYPE_ID__ECF_SERVICES:
                getEcfServices().clear();
                return;
            case ModelPackage.ISERVICE_TYPE_ID__ECF_PROTOCOLS:
                getEcfProtocols().clear();
                return;
            case ModelPackage.ISERVICE_TYPE_ID__ECF_SCOPES:
                getEcfScopes().clear();
                return;
            case ModelPackage.ISERVICE_TYPE_ID__ECF_SERVICE_NAME:
                setEcfServiceName(ECF_SERVICE_NAME_EDEFAULT);
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
            case ModelPackage.ISERVICE_TYPE_ID__ECF_SERVICE_TYPE_ID:
                return ECF_SERVICE_TYPE_ID_EDEFAULT == null ? ecfServiceTypeID != null : !ECF_SERVICE_TYPE_ID_EDEFAULT.equals(ecfServiceTypeID);
            case ModelPackage.ISERVICE_TYPE_ID__ECF_NAMING_AUTHORITY:
                return ECF_NAMING_AUTHORITY_EDEFAULT == null ? ecfNamingAuthority != null : !ECF_NAMING_AUTHORITY_EDEFAULT.equals(ecfNamingAuthority);
            case ModelPackage.ISERVICE_TYPE_ID__ECF_SERVICES:
                return ecfServices != null && !ecfServices.isEmpty();
            case ModelPackage.ISERVICE_TYPE_ID__ECF_PROTOCOLS:
                return ecfProtocols != null && !ecfProtocols.isEmpty();
            case ModelPackage.ISERVICE_TYPE_ID__ECF_SCOPES:
                return ecfScopes != null && !ecfScopes.isEmpty();
            case ModelPackage.ISERVICE_TYPE_ID__ECF_SERVICE_NAME:
                return ECF_SERVICE_NAME_EDEFAULT == null ? ecfServiceName != null : !ECF_SERVICE_NAME_EDEFAULT.equals(ecfServiceName);
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
        result.append(" (ecfServiceTypeID: ");
        result.append(ecfServiceTypeID);
        //$NON-NLS-1$
        result.append(", ecfNamingAuthority: ");
        result.append(ecfNamingAuthority);
        //$NON-NLS-1$
        result.append(", ecfServices: ");
        result.append(ecfServices);
        //$NON-NLS-1$
        result.append(", ecfProtocols: ");
        result.append(ecfProtocols);
        //$NON-NLS-1$
        result.append(", ecfScopes: ");
        result.append(ecfScopes);
        //$NON-NLS-1$
        result.append(", ecfServiceName: ");
        result.append(ecfServiceName);
        result.append(')');
        return result.toString();
    }
}
//IServiceTypeIDImpl
