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
package org.eclipse.ecf.discovery.ui.model;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.ecf.discovery.ui.model.ModelFactory
 * @model kind="package"
 * @generated
 */
public interface ModelPackage extends EPackage {

    /**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    String eNAME = "model";

    /**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    String eNS_URI = "http://www.eclipse.org/ecf/discovery.ecore";

    /**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    String eNS_PREFIX = "org.eclipse.ecf.discovery.model";

    /**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    ModelPackage eINSTANCE = org.eclipse.ecf.discovery.ui.model.impl.ModelPackageImpl.init();

    /**
	 * The meta object id for the '{@link org.eclipse.ecf.discovery.ui.model.impl.IServiceInfoImpl <em>IService Info</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.ecf.discovery.ui.model.impl.IServiceInfoImpl
	 * @see org.eclipse.ecf.discovery.ui.model.impl.ModelPackageImpl#getIServiceInfo()
	 * @generated
	 */
    int ISERVICE_INFO = 0;

    /**
	 * The feature id for the '<em><b>Ecf Service Info</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ISERVICE_INFO__ECF_SERVICE_INFO = 0;

    /**
	 * The feature id for the '<em><b>Ecf Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ISERVICE_INFO__ECF_NAME = 1;

    /**
	 * The feature id for the '<em><b>Ecf Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ISERVICE_INFO__ECF_LOCATION = 2;

    /**
	 * The feature id for the '<em><b>Ecf Priority</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ISERVICE_INFO__ECF_PRIORITY = 3;

    /**
	 * The feature id for the '<em><b>Ecf Weight</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ISERVICE_INFO__ECF_WEIGHT = 4;

    /**
	 * The feature id for the '<em><b>Service ID</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ISERVICE_INFO__SERVICE_ID = 5;

    /**
	 * The number of structural features of the '<em>IService Info</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ISERVICE_INFO_FEATURE_COUNT = 6;

    /**
	 * The meta object id for the '{@link org.eclipse.ecf.discovery.ui.model.impl.INetworkImpl <em>INetwork</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.ecf.discovery.ui.model.impl.INetworkImpl
	 * @see org.eclipse.ecf.discovery.ui.model.impl.ModelPackageImpl#getINetwork()
	 * @generated
	 */
    int INETWORK = 1;

    /**
	 * The feature id for the '<em><b>Hosts</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int INETWORK__HOSTS = 0;

    /**
	 * The number of structural features of the '<em>INetwork</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int INETWORK_FEATURE_COUNT = 1;

    /**
	 * The meta object id for the '{@link org.eclipse.ecf.discovery.ui.model.impl.IHostImpl <em>IHost</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.ecf.discovery.ui.model.impl.IHostImpl
	 * @see org.eclipse.ecf.discovery.ui.model.impl.ModelPackageImpl#getIHost()
	 * @generated
	 */
    int IHOST = 2;

    /**
	 * The feature id for the '<em><b>Services</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int IHOST__SERVICES = 0;

    /**
	 * The feature id for the '<em><b>Address</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int IHOST__ADDRESS = 1;

    /**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int IHOST__NAME = 2;

    /**
	 * The number of structural features of the '<em>IHost</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int IHOST_FEATURE_COUNT = 3;

    /**
	 * The meta object id for the '{@link org.eclipse.ecf.discovery.ui.model.impl.IServiceIDImpl <em>IService ID</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.ecf.discovery.ui.model.impl.IServiceIDImpl
	 * @see org.eclipse.ecf.discovery.ui.model.impl.ModelPackageImpl#getIServiceID()
	 * @generated
	 */
    int ISERVICE_ID = 3;

    /**
	 * The feature id for the '<em><b>Ecf Service ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ISERVICE_ID__ECF_SERVICE_ID = 0;

    /**
	 * The feature id for the '<em><b>Ecf Service Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ISERVICE_ID__ECF_SERVICE_NAME = 1;

    /**
	 * The feature id for the '<em><b>Service Type ID</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ISERVICE_ID__SERVICE_TYPE_ID = 2;

    /**
	 * The number of structural features of the '<em>IService ID</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ISERVICE_ID_FEATURE_COUNT = 3;

    /**
	 * The meta object id for the '{@link org.eclipse.ecf.discovery.ui.model.impl.IServiceTypeIDImpl <em>IService Type ID</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.ecf.discovery.ui.model.impl.IServiceTypeIDImpl
	 * @see org.eclipse.ecf.discovery.ui.model.impl.ModelPackageImpl#getIServiceTypeID()
	 * @generated
	 */
    int ISERVICE_TYPE_ID = 4;

    /**
	 * The feature id for the '<em><b>Ecf Service Type ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ISERVICE_TYPE_ID__ECF_SERVICE_TYPE_ID = 0;

    /**
	 * The feature id for the '<em><b>Ecf Naming Authority</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ISERVICE_TYPE_ID__ECF_NAMING_AUTHORITY = 1;

    /**
	 * The feature id for the '<em><b>Ecf Services</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ISERVICE_TYPE_ID__ECF_SERVICES = 2;

    /**
	 * The feature id for the '<em><b>Ecf Protocols</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ISERVICE_TYPE_ID__ECF_PROTOCOLS = 3;

    /**
	 * The feature id for the '<em><b>Ecf Scopes</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ISERVICE_TYPE_ID__ECF_SCOPES = 4;

    /**
	 * The feature id for the '<em><b>Ecf Service Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ISERVICE_TYPE_ID__ECF_SERVICE_NAME = 5;

    /**
	 * The number of structural features of the '<em>IService Type ID</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
    int ISERVICE_TYPE_ID_FEATURE_COUNT = 6;

    /**
	 * The meta object id for the '<em>ECFI Service Info</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.ecf.discovery.IServiceInfo
	 * @see org.eclipse.ecf.discovery.ui.model.impl.ModelPackageImpl#getECFIServiceInfo()
	 * @generated
	 */
    int ECFI_SERVICE_INFO = 5;

    /**
	 * The meta object id for the '<em>Inet Address</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.net.InetAddress
	 * @see org.eclipse.ecf.discovery.ui.model.impl.ModelPackageImpl#getInetAddress()
	 * @generated
	 */
    int INET_ADDRESS = 6;

    /**
	 * The meta object id for the '<em>ECFI Service ID</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.ecf.discovery.identity.IServiceID
	 * @see org.eclipse.ecf.discovery.ui.model.impl.ModelPackageImpl#getECFIServiceID()
	 * @generated
	 */
    int ECFI_SERVICE_ID = 7;

    /**
	 * The meta object id for the '<em>ECFI Service Type ID</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.ecf.discovery.identity.IServiceTypeID
	 * @see org.eclipse.ecf.discovery.ui.model.impl.ModelPackageImpl#getECFIServiceTypeID()
	 * @generated
	 */
    int ECFI_SERVICE_TYPE_ID = 8;

    /**
	 * The meta object id for the '<em>URI</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.net.URI
	 * @see org.eclipse.ecf.discovery.ui.model.impl.ModelPackageImpl#getURI()
	 * @generated
	 */
    int URI = 9;

    /**
	 * Returns the meta object for class '{@link org.eclipse.ecf.discovery.ui.model.IServiceInfo <em>IService Info</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>IService Info</em>'.
	 * @see org.eclipse.ecf.discovery.ui.model.IServiceInfo
	 * @generated
	 */
    EClass getIServiceInfo();

    /**
	 * Returns the meta object for the attribute '{@link org.eclipse.ecf.discovery.ui.model.IServiceInfo#getEcfServiceInfo <em>Ecf Service Info</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ecf Service Info</em>'.
	 * @see org.eclipse.ecf.discovery.ui.model.IServiceInfo#getEcfServiceInfo()
	 * @see #getIServiceInfo()
	 * @generated
	 */
    EAttribute getIServiceInfo_EcfServiceInfo();

    /**
	 * Returns the meta object for the attribute '{@link org.eclipse.ecf.discovery.ui.model.IServiceInfo#getEcfName <em>Ecf Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ecf Name</em>'.
	 * @see org.eclipse.ecf.discovery.ui.model.IServiceInfo#getEcfName()
	 * @see #getIServiceInfo()
	 * @generated
	 */
    EAttribute getIServiceInfo_EcfName();

    /**
	 * Returns the meta object for the attribute '{@link org.eclipse.ecf.discovery.ui.model.IServiceInfo#getEcfLocation <em>Ecf Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ecf Location</em>'.
	 * @see org.eclipse.ecf.discovery.ui.model.IServiceInfo#getEcfLocation()
	 * @see #getIServiceInfo()
	 * @generated
	 */
    EAttribute getIServiceInfo_EcfLocation();

    /**
	 * Returns the meta object for the attribute '{@link org.eclipse.ecf.discovery.ui.model.IServiceInfo#getEcfPriority <em>Ecf Priority</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ecf Priority</em>'.
	 * @see org.eclipse.ecf.discovery.ui.model.IServiceInfo#getEcfPriority()
	 * @see #getIServiceInfo()
	 * @generated
	 */
    EAttribute getIServiceInfo_EcfPriority();

    /**
	 * Returns the meta object for the attribute '{@link org.eclipse.ecf.discovery.ui.model.IServiceInfo#getEcfWeight <em>Ecf Weight</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ecf Weight</em>'.
	 * @see org.eclipse.ecf.discovery.ui.model.IServiceInfo#getEcfWeight()
	 * @see #getIServiceInfo()
	 * @generated
	 */
    EAttribute getIServiceInfo_EcfWeight();

    /**
	 * Returns the meta object for the reference '{@link org.eclipse.ecf.discovery.ui.model.IServiceInfo#getServiceID <em>Service ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Service ID</em>'.
	 * @see org.eclipse.ecf.discovery.ui.model.IServiceInfo#getServiceID()
	 * @see #getIServiceInfo()
	 * @generated
	 */
    EReference getIServiceInfo_ServiceID();

    /**
	 * Returns the meta object for class '{@link org.eclipse.ecf.discovery.ui.model.INetwork <em>INetwork</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>INetwork</em>'.
	 * @see org.eclipse.ecf.discovery.ui.model.INetwork
	 * @generated
	 */
    EClass getINetwork();

    /**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.ecf.discovery.ui.model.INetwork#getHosts <em>Hosts</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Hosts</em>'.
	 * @see org.eclipse.ecf.discovery.ui.model.INetwork#getHosts()
	 * @see #getINetwork()
	 * @generated
	 */
    EReference getINetwork_Hosts();

    /**
	 * Returns the meta object for class '{@link org.eclipse.ecf.discovery.ui.model.IHost <em>IHost</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>IHost</em>'.
	 * @see org.eclipse.ecf.discovery.ui.model.IHost
	 * @generated
	 */
    EClass getIHost();

    /**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.ecf.discovery.ui.model.IHost#getServices <em>Services</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Services</em>'.
	 * @see org.eclipse.ecf.discovery.ui.model.IHost#getServices()
	 * @see #getIHost()
	 * @generated
	 */
    EReference getIHost_Services();

    /**
	 * Returns the meta object for the attribute '{@link org.eclipse.ecf.discovery.ui.model.IHost#getAddress <em>Address</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Address</em>'.
	 * @see org.eclipse.ecf.discovery.ui.model.IHost#getAddress()
	 * @see #getIHost()
	 * @generated
	 */
    EAttribute getIHost_Address();

    /**
	 * Returns the meta object for the attribute '{@link org.eclipse.ecf.discovery.ui.model.IHost#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.ecf.discovery.ui.model.IHost#getName()
	 * @see #getIHost()
	 * @generated
	 */
    EAttribute getIHost_Name();

    /**
	 * Returns the meta object for class '{@link org.eclipse.ecf.discovery.ui.model.IServiceID <em>IService ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>IService ID</em>'.
	 * @see org.eclipse.ecf.discovery.ui.model.IServiceID
	 * @generated
	 */
    EClass getIServiceID();

    /**
	 * Returns the meta object for the attribute '{@link org.eclipse.ecf.discovery.ui.model.IServiceID#getEcfServiceID <em>Ecf Service ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ecf Service ID</em>'.
	 * @see org.eclipse.ecf.discovery.ui.model.IServiceID#getEcfServiceID()
	 * @see #getIServiceID()
	 * @generated
	 */
    EAttribute getIServiceID_EcfServiceID();

    /**
	 * Returns the meta object for the attribute '{@link org.eclipse.ecf.discovery.ui.model.IServiceID#getEcfServiceName <em>Ecf Service Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ecf Service Name</em>'.
	 * @see org.eclipse.ecf.discovery.ui.model.IServiceID#getEcfServiceName()
	 * @see #getIServiceID()
	 * @generated
	 */
    EAttribute getIServiceID_EcfServiceName();

    /**
	 * Returns the meta object for the reference '{@link org.eclipse.ecf.discovery.ui.model.IServiceID#getServiceTypeID <em>Service Type ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Service Type ID</em>'.
	 * @see org.eclipse.ecf.discovery.ui.model.IServiceID#getServiceTypeID()
	 * @see #getIServiceID()
	 * @generated
	 */
    EReference getIServiceID_ServiceTypeID();

    /**
	 * Returns the meta object for class '{@link org.eclipse.ecf.discovery.ui.model.IServiceTypeID <em>IService Type ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>IService Type ID</em>'.
	 * @see org.eclipse.ecf.discovery.ui.model.IServiceTypeID
	 * @generated
	 */
    EClass getIServiceTypeID();

    /**
	 * Returns the meta object for the attribute '{@link org.eclipse.ecf.discovery.ui.model.IServiceTypeID#getEcfServiceTypeID <em>Ecf Service Type ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ecf Service Type ID</em>'.
	 * @see org.eclipse.ecf.discovery.ui.model.IServiceTypeID#getEcfServiceTypeID()
	 * @see #getIServiceTypeID()
	 * @generated
	 */
    EAttribute getIServiceTypeID_EcfServiceTypeID();

    /**
	 * Returns the meta object for the attribute '{@link org.eclipse.ecf.discovery.ui.model.IServiceTypeID#getEcfNamingAuthority <em>Ecf Naming Authority</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ecf Naming Authority</em>'.
	 * @see org.eclipse.ecf.discovery.ui.model.IServiceTypeID#getEcfNamingAuthority()
	 * @see #getIServiceTypeID()
	 * @generated
	 */
    EAttribute getIServiceTypeID_EcfNamingAuthority();

    /**
	 * Returns the meta object for the attribute list '{@link org.eclipse.ecf.discovery.ui.model.IServiceTypeID#getEcfServices <em>Ecf Services</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Ecf Services</em>'.
	 * @see org.eclipse.ecf.discovery.ui.model.IServiceTypeID#getEcfServices()
	 * @see #getIServiceTypeID()
	 * @generated
	 */
    EAttribute getIServiceTypeID_EcfServices();

    /**
	 * Returns the meta object for the attribute list '{@link org.eclipse.ecf.discovery.ui.model.IServiceTypeID#getEcfProtocols <em>Ecf Protocols</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Ecf Protocols</em>'.
	 * @see org.eclipse.ecf.discovery.ui.model.IServiceTypeID#getEcfProtocols()
	 * @see #getIServiceTypeID()
	 * @generated
	 */
    EAttribute getIServiceTypeID_EcfProtocols();

    /**
	 * Returns the meta object for the attribute list '{@link org.eclipse.ecf.discovery.ui.model.IServiceTypeID#getEcfScopes <em>Ecf Scopes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Ecf Scopes</em>'.
	 * @see org.eclipse.ecf.discovery.ui.model.IServiceTypeID#getEcfScopes()
	 * @see #getIServiceTypeID()
	 * @generated
	 */
    EAttribute getIServiceTypeID_EcfScopes();

    /**
	 * Returns the meta object for the attribute '{@link org.eclipse.ecf.discovery.ui.model.IServiceTypeID#getEcfServiceName <em>Ecf Service Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ecf Service Name</em>'.
	 * @see org.eclipse.ecf.discovery.ui.model.IServiceTypeID#getEcfServiceName()
	 * @see #getIServiceTypeID()
	 * @generated
	 */
    EAttribute getIServiceTypeID_EcfServiceName();

    /**
	 * Returns the meta object for data type '{@link org.eclipse.ecf.discovery.IServiceInfo <em>ECFI Service Info</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>ECFI Service Info</em>'.
	 * @see org.eclipse.ecf.discovery.IServiceInfo
	 * @model instanceClass="org.eclipse.ecf.discovery.IServiceInfo"
	 * @generated
	 */
    EDataType getECFIServiceInfo();

    /**
	 * Returns the meta object for data type '{@link java.net.InetAddress <em>Inet Address</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Inet Address</em>'.
	 * @see java.net.InetAddress
	 * @model instanceClass="java.net.InetAddress"
	 * @generated
	 */
    EDataType getInetAddress();

    /**
	 * Returns the meta object for data type '{@link org.eclipse.ecf.discovery.identity.IServiceID <em>ECFI Service ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>ECFI Service ID</em>'.
	 * @see org.eclipse.ecf.discovery.identity.IServiceID
	 * @model instanceClass="org.eclipse.ecf.discovery.identity.IServiceID"
	 * @generated
	 */
    EDataType getECFIServiceID();

    /**
	 * Returns the meta object for data type '{@link org.eclipse.ecf.discovery.identity.IServiceTypeID <em>ECFI Service Type ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>ECFI Service Type ID</em>'.
	 * @see org.eclipse.ecf.discovery.identity.IServiceTypeID
	 * @model instanceClass="org.eclipse.ecf.discovery.identity.IServiceTypeID"
	 * @generated
	 */
    EDataType getECFIServiceTypeID();

    /**
	 * Returns the meta object for data type '{@link java.net.URI <em>URI</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>URI</em>'.
	 * @see java.net.URI
	 * @model instanceClass="java.net.URI"
	 * @generated
	 */
    EDataType getURI();

    /**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
    ModelFactory getModelFactory();

    /**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
    interface Literals {

        /**
		 * The meta object literal for the '{@link org.eclipse.ecf.discovery.ui.model.impl.IServiceInfoImpl <em>IService Info</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.ecf.discovery.ui.model.impl.IServiceInfoImpl
		 * @see org.eclipse.ecf.discovery.ui.model.impl.ModelPackageImpl#getIServiceInfo()
		 * @generated
		 */
        EClass ISERVICE_INFO = eINSTANCE.getIServiceInfo();

        /**
		 * The meta object literal for the '<em><b>Ecf Service Info</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute ISERVICE_INFO__ECF_SERVICE_INFO = eINSTANCE.getIServiceInfo_EcfServiceInfo();

        /**
		 * The meta object literal for the '<em><b>Ecf Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute ISERVICE_INFO__ECF_NAME = eINSTANCE.getIServiceInfo_EcfName();

        /**
		 * The meta object literal for the '<em><b>Ecf Location</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute ISERVICE_INFO__ECF_LOCATION = eINSTANCE.getIServiceInfo_EcfLocation();

        /**
		 * The meta object literal for the '<em><b>Ecf Priority</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute ISERVICE_INFO__ECF_PRIORITY = eINSTANCE.getIServiceInfo_EcfPriority();

        /**
		 * The meta object literal for the '<em><b>Ecf Weight</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute ISERVICE_INFO__ECF_WEIGHT = eINSTANCE.getIServiceInfo_EcfWeight();

        /**
		 * The meta object literal for the '<em><b>Service ID</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
        EReference ISERVICE_INFO__SERVICE_ID = eINSTANCE.getIServiceInfo_ServiceID();

        /**
		 * The meta object literal for the '{@link org.eclipse.ecf.discovery.ui.model.impl.INetworkImpl <em>INetwork</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.ecf.discovery.ui.model.impl.INetworkImpl
		 * @see org.eclipse.ecf.discovery.ui.model.impl.ModelPackageImpl#getINetwork()
		 * @generated
		 */
        EClass INETWORK = eINSTANCE.getINetwork();

        /**
		 * The meta object literal for the '<em><b>Hosts</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
        EReference INETWORK__HOSTS = eINSTANCE.getINetwork_Hosts();

        /**
		 * The meta object literal for the '{@link org.eclipse.ecf.discovery.ui.model.impl.IHostImpl <em>IHost</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.ecf.discovery.ui.model.impl.IHostImpl
		 * @see org.eclipse.ecf.discovery.ui.model.impl.ModelPackageImpl#getIHost()
		 * @generated
		 */
        EClass IHOST = eINSTANCE.getIHost();

        /**
		 * The meta object literal for the '<em><b>Services</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
        EReference IHOST__SERVICES = eINSTANCE.getIHost_Services();

        /**
		 * The meta object literal for the '<em><b>Address</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute IHOST__ADDRESS = eINSTANCE.getIHost_Address();

        /**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute IHOST__NAME = eINSTANCE.getIHost_Name();

        /**
		 * The meta object literal for the '{@link org.eclipse.ecf.discovery.ui.model.impl.IServiceIDImpl <em>IService ID</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.ecf.discovery.ui.model.impl.IServiceIDImpl
		 * @see org.eclipse.ecf.discovery.ui.model.impl.ModelPackageImpl#getIServiceID()
		 * @generated
		 */
        EClass ISERVICE_ID = eINSTANCE.getIServiceID();

        /**
		 * The meta object literal for the '<em><b>Ecf Service ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute ISERVICE_ID__ECF_SERVICE_ID = eINSTANCE.getIServiceID_EcfServiceID();

        /**
		 * The meta object literal for the '<em><b>Ecf Service Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute ISERVICE_ID__ECF_SERVICE_NAME = eINSTANCE.getIServiceID_EcfServiceName();

        /**
		 * The meta object literal for the '<em><b>Service Type ID</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
        EReference ISERVICE_ID__SERVICE_TYPE_ID = eINSTANCE.getIServiceID_ServiceTypeID();

        /**
		 * The meta object literal for the '{@link org.eclipse.ecf.discovery.ui.model.impl.IServiceTypeIDImpl <em>IService Type ID</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.ecf.discovery.ui.model.impl.IServiceTypeIDImpl
		 * @see org.eclipse.ecf.discovery.ui.model.impl.ModelPackageImpl#getIServiceTypeID()
		 * @generated
		 */
        EClass ISERVICE_TYPE_ID = eINSTANCE.getIServiceTypeID();

        /**
		 * The meta object literal for the '<em><b>Ecf Service Type ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute ISERVICE_TYPE_ID__ECF_SERVICE_TYPE_ID = eINSTANCE.getIServiceTypeID_EcfServiceTypeID();

        /**
		 * The meta object literal for the '<em><b>Ecf Naming Authority</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute ISERVICE_TYPE_ID__ECF_NAMING_AUTHORITY = eINSTANCE.getIServiceTypeID_EcfNamingAuthority();

        /**
		 * The meta object literal for the '<em><b>Ecf Services</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute ISERVICE_TYPE_ID__ECF_SERVICES = eINSTANCE.getIServiceTypeID_EcfServices();

        /**
		 * The meta object literal for the '<em><b>Ecf Protocols</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute ISERVICE_TYPE_ID__ECF_PROTOCOLS = eINSTANCE.getIServiceTypeID_EcfProtocols();

        /**
		 * The meta object literal for the '<em><b>Ecf Scopes</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute ISERVICE_TYPE_ID__ECF_SCOPES = eINSTANCE.getIServiceTypeID_EcfScopes();

        /**
		 * The meta object literal for the '<em><b>Ecf Service Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
        EAttribute ISERVICE_TYPE_ID__ECF_SERVICE_NAME = eINSTANCE.getIServiceTypeID_EcfServiceName();

        /**
		 * The meta object literal for the '<em>ECFI Service Info</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.ecf.discovery.IServiceInfo
		 * @see org.eclipse.ecf.discovery.ui.model.impl.ModelPackageImpl#getECFIServiceInfo()
		 * @generated
		 */
        EDataType ECFI_SERVICE_INFO = eINSTANCE.getECFIServiceInfo();

        /**
		 * The meta object literal for the '<em>Inet Address</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.net.InetAddress
		 * @see org.eclipse.ecf.discovery.ui.model.impl.ModelPackageImpl#getInetAddress()
		 * @generated
		 */
        EDataType INET_ADDRESS = eINSTANCE.getInetAddress();

        /**
		 * The meta object literal for the '<em>ECFI Service ID</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.ecf.discovery.identity.IServiceID
		 * @see org.eclipse.ecf.discovery.ui.model.impl.ModelPackageImpl#getECFIServiceID()
		 * @generated
		 */
        EDataType ECFI_SERVICE_ID = eINSTANCE.getECFIServiceID();

        /**
		 * The meta object literal for the '<em>ECFI Service Type ID</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.ecf.discovery.identity.IServiceTypeID
		 * @see org.eclipse.ecf.discovery.ui.model.impl.ModelPackageImpl#getECFIServiceTypeID()
		 * @generated
		 */
        EDataType ECFI_SERVICE_TYPE_ID = eINSTANCE.getECFIServiceTypeID();

        /**
		 * The meta object literal for the '<em>URI</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.net.URI
		 * @see org.eclipse.ecf.discovery.ui.model.impl.ModelPackageImpl#getURI()
		 * @generated
		 */
        EDataType URI = eINSTANCE.getURI();
    }
}
//ModelPackage
