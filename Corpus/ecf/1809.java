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
import java.net.URI;
import java.net.UnknownHostException;
import org.eclipse.ecf.discovery.ui.model.IHost;
import org.eclipse.ecf.discovery.ui.model.INetwork;
import org.eclipse.ecf.discovery.ui.model.IServiceID;
import org.eclipse.ecf.discovery.ui.model.IServiceInfo;
import org.eclipse.ecf.discovery.ui.model.IServiceTypeID;
import org.eclipse.ecf.discovery.ui.model.ModelFactory;
import org.eclipse.ecf.discovery.ui.model.ModelPackage;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ModelFactoryImpl extends EFactoryImpl implements ModelFactory {

    /**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public static ModelFactory init() {
        try {
            //$NON-NLS-1$ 
            ModelFactory theModelFactory = (ModelFactory) EPackage.Registry.INSTANCE.getEFactory("http://www.eclipse.org/ecf/discovery.ecore");
            if (theModelFactory != null) {
                return theModelFactory;
            }
        } catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new ModelFactoryImpl();
    }

    /**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public  ModelFactoryImpl() {
        super();
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EObject create(EClass eClass) {
        switch(eClass.getClassifierID()) {
            case ModelPackage.ISERVICE_INFO:
                return createIServiceInfo();
            case ModelPackage.INETWORK:
                return createINetwork();
            case ModelPackage.IHOST:
                return createIHost();
            case ModelPackage.ISERVICE_ID:
                return createIServiceID();
            case ModelPackage.ISERVICE_TYPE_ID:
                return createIServiceTypeID();
            default:
                //$NON-NLS-1$ //$NON-NLS-2$
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public Object createFromString(EDataType eDataType, String initialValue) {
        switch(eDataType.getClassifierID()) {
            case ModelPackage.ECFI_SERVICE_INFO:
                return createECFIServiceInfoFromString(eDataType, initialValue);
            case ModelPackage.INET_ADDRESS:
                return createInetAddressFromString(eDataType, initialValue);
            case ModelPackage.ECFI_SERVICE_ID:
                return createECFIServiceIDFromString(eDataType, initialValue);
            case ModelPackage.ECFI_SERVICE_TYPE_ID:
                return createECFIServiceTypeIDFromString(eDataType, initialValue);
            case ModelPackage.URI:
                return createURIFromString(eDataType, initialValue);
            default:
                //$NON-NLS-1$ //$NON-NLS-2$
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public String convertToString(EDataType eDataType, Object instanceValue) {
        switch(eDataType.getClassifierID()) {
            case ModelPackage.ECFI_SERVICE_INFO:
                return convertECFIServiceInfoToString(eDataType, instanceValue);
            case ModelPackage.INET_ADDRESS:
                return convertInetAddressToString(eDataType, instanceValue);
            case ModelPackage.ECFI_SERVICE_ID:
                return convertECFIServiceIDToString(eDataType, instanceValue);
            case ModelPackage.ECFI_SERVICE_TYPE_ID:
                return convertECFIServiceTypeIDToString(eDataType, instanceValue);
            case ModelPackage.URI:
                return convertURIToString(eDataType, instanceValue);
            default:
                //$NON-NLS-1$ //$NON-NLS-2$
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public IServiceInfo createIServiceInfo() {
        IServiceInfoImpl iServiceInfo = new IServiceInfoImpl();
        return iServiceInfo;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public INetwork createINetwork() {
        INetworkImpl iNetwork = new INetworkImpl();
        return iNetwork;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public IHost createIHost() {
        IHostImpl iHost = new IHostImpl();
        return iHost;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public IServiceID createIServiceID() {
        IServiceIDImpl iServiceID = new IServiceIDImpl();
        return iServiceID;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public IServiceTypeID createIServiceTypeID() {
        IServiceTypeIDImpl iServiceTypeID = new IServiceTypeIDImpl();
        return iServiceTypeID;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public org.eclipse.ecf.discovery.IServiceInfo createECFIServiceInfoFromString(EDataType eDataType, String initialValue) {
        return (org.eclipse.ecf.discovery.IServiceInfo) super.createFromString(eDataType, initialValue);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public String convertECFIServiceInfoToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
    public InetAddress createInetAddressFromString(EDataType eDataType, String initialValue) {
        try {
            return InetAddress.getByName(initialValue);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid value: '" + initialValue + "' for datatype :" + eDataType.getName());
        }
    }

    /**
	 * <!-- begin-user-doc -->
		//TODO overwrite the gen impl of this method
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public String convertInetAddressToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public org.eclipse.ecf.discovery.identity.IServiceID createECFIServiceIDFromString(EDataType eDataType, String initialValue) {
        return (org.eclipse.ecf.discovery.identity.IServiceID) super.createFromString(eDataType, initialValue);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public String convertECFIServiceIDToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public org.eclipse.ecf.discovery.identity.IServiceTypeID createECFIServiceTypeIDFromString(EDataType eDataType, String initialValue) {
        return (org.eclipse.ecf.discovery.identity.IServiceTypeID) super.createFromString(eDataType, initialValue);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public String convertECFIServiceTypeIDToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public URI createURIFromString(EDataType eDataType, String initialValue) {
        return (URI) super.createFromString(eDataType, initialValue);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public String convertURIToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public ModelPackage getModelPackage() {
        return (ModelPackage) getEPackage();
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
    public static ModelPackage getPackage() {
        return ModelPackage.eINSTANCE;
    }
}
//ModelFactoryImpl
