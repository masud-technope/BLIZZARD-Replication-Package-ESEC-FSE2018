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
import org.eclipse.ecf.discovery.ui.model.IHost;
import org.eclipse.ecf.discovery.ui.model.INetwork;
import org.eclipse.ecf.discovery.ui.model.IServiceID;
import org.eclipse.ecf.discovery.ui.model.IServiceInfo;
import org.eclipse.ecf.discovery.ui.model.IServiceTypeID;
import org.eclipse.ecf.discovery.ui.model.ModelFactory;
import org.eclipse.ecf.discovery.ui.model.ModelPackage;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ModelPackageImpl extends EPackageImpl implements ModelPackage {

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    private EClass iServiceInfoEClass = null;

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    private EClass iNetworkEClass = null;

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    private EClass iHostEClass = null;

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    private EClass iServiceIDEClass = null;

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    private EClass iServiceTypeIDEClass = null;

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    private EDataType ecfiServiceInfoEDataType = null;

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    private EDataType inetAddressEDataType = null;

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    private EDataType ecfiServiceIDEDataType = null;

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    private EDataType ecfiServiceTypeIDEDataType = null;

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    private EDataType uriEDataType = null;

    /**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
    private  ModelPackageImpl() {
        super(eNS_URI, ModelFactory.eINSTANCE);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    private static boolean isInited = false;

    /**
	 * Creates, registers, and initializes the <b>Package</b> for this
	 * model, and for any others upon which it depends.  Simple
	 * dependencies are satisfied by calling this method on all
	 * dependent packages before doing anything else.  This method drives
	 * initialization for interdependent packages directly, in parallel
	 * with this package, itself.
	 * <p>Of this package and its interdependencies, all packages which
	 * have not yet been registered by their URI values are first created
	 * and registered.  The packages are then initialized in two steps:
	 * meta-model objects for all of the packages are created before any
	 * are initialized, since one package's meta-model objects may refer to
	 * those of another.
	 * <p>Invocation of this method will not affect any packages that have
	 * already been initialized.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
    public static ModelPackage init() {
        if (isInited)
            return (ModelPackage) EPackage.Registry.INSTANCE.getEPackage(ModelPackage.eNS_URI);
        // Obtain or create and register package
        ModelPackageImpl theModelPackage = (ModelPackageImpl) (EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof ModelPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new ModelPackageImpl());
        isInited = true;
        // Create package meta-data objects
        theModelPackage.createPackageContents();
        // Initialize created meta-data
        theModelPackage.initializePackageContents();
        // Mark meta-data to indicate it can't be changed
        theModelPackage.freeze();
        return theModelPackage;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EClass getIServiceInfo() {
        return iServiceInfoEClass;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getIServiceInfo_EcfServiceInfo() {
        return (EAttribute) iServiceInfoEClass.getEStructuralFeatures().get(0);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getIServiceInfo_EcfName() {
        return (EAttribute) iServiceInfoEClass.getEStructuralFeatures().get(1);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getIServiceInfo_EcfLocation() {
        return (EAttribute) iServiceInfoEClass.getEStructuralFeatures().get(2);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getIServiceInfo_EcfPriority() {
        return (EAttribute) iServiceInfoEClass.getEStructuralFeatures().get(3);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getIServiceInfo_EcfWeight() {
        return (EAttribute) iServiceInfoEClass.getEStructuralFeatures().get(4);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EReference getIServiceInfo_ServiceID() {
        return (EReference) iServiceInfoEClass.getEStructuralFeatures().get(5);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EClass getINetwork() {
        return iNetworkEClass;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EReference getINetwork_Hosts() {
        return (EReference) iNetworkEClass.getEStructuralFeatures().get(0);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EClass getIHost() {
        return iHostEClass;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EReference getIHost_Services() {
        return (EReference) iHostEClass.getEStructuralFeatures().get(0);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getIHost_Address() {
        return (EAttribute) iHostEClass.getEStructuralFeatures().get(1);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getIHost_Name() {
        return (EAttribute) iHostEClass.getEStructuralFeatures().get(2);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EClass getIServiceID() {
        return iServiceIDEClass;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getIServiceID_EcfServiceID() {
        return (EAttribute) iServiceIDEClass.getEStructuralFeatures().get(0);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getIServiceID_EcfServiceName() {
        return (EAttribute) iServiceIDEClass.getEStructuralFeatures().get(1);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EReference getIServiceID_ServiceTypeID() {
        return (EReference) iServiceIDEClass.getEStructuralFeatures().get(2);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EClass getIServiceTypeID() {
        return iServiceTypeIDEClass;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getIServiceTypeID_EcfServiceTypeID() {
        return (EAttribute) iServiceTypeIDEClass.getEStructuralFeatures().get(0);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getIServiceTypeID_EcfNamingAuthority() {
        return (EAttribute) iServiceTypeIDEClass.getEStructuralFeatures().get(1);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getIServiceTypeID_EcfServices() {
        return (EAttribute) iServiceTypeIDEClass.getEStructuralFeatures().get(2);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getIServiceTypeID_EcfProtocols() {
        return (EAttribute) iServiceTypeIDEClass.getEStructuralFeatures().get(3);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getIServiceTypeID_EcfScopes() {
        return (EAttribute) iServiceTypeIDEClass.getEStructuralFeatures().get(4);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getIServiceTypeID_EcfServiceName() {
        return (EAttribute) iServiceTypeIDEClass.getEStructuralFeatures().get(5);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EDataType getECFIServiceInfo() {
        return ecfiServiceInfoEDataType;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EDataType getInetAddress() {
        return inetAddressEDataType;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EDataType getECFIServiceID() {
        return ecfiServiceIDEDataType;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EDataType getECFIServiceTypeID() {
        return ecfiServiceTypeIDEDataType;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public EDataType getURI() {
        return uriEDataType;
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public ModelFactory getModelFactory() {
        return (ModelFactory) getEFactoryInstance();
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    private boolean isCreated = false;

    /**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public void createPackageContents() {
        if (isCreated)
            return;
        isCreated = true;
        // Create classes and their features
        iServiceInfoEClass = createEClass(ISERVICE_INFO);
        createEAttribute(iServiceInfoEClass, ISERVICE_INFO__ECF_SERVICE_INFO);
        createEAttribute(iServiceInfoEClass, ISERVICE_INFO__ECF_NAME);
        createEAttribute(iServiceInfoEClass, ISERVICE_INFO__ECF_LOCATION);
        createEAttribute(iServiceInfoEClass, ISERVICE_INFO__ECF_PRIORITY);
        createEAttribute(iServiceInfoEClass, ISERVICE_INFO__ECF_WEIGHT);
        createEReference(iServiceInfoEClass, ISERVICE_INFO__SERVICE_ID);
        iNetworkEClass = createEClass(INETWORK);
        createEReference(iNetworkEClass, INETWORK__HOSTS);
        iHostEClass = createEClass(IHOST);
        createEReference(iHostEClass, IHOST__SERVICES);
        createEAttribute(iHostEClass, IHOST__ADDRESS);
        createEAttribute(iHostEClass, IHOST__NAME);
        iServiceIDEClass = createEClass(ISERVICE_ID);
        createEAttribute(iServiceIDEClass, ISERVICE_ID__ECF_SERVICE_ID);
        createEAttribute(iServiceIDEClass, ISERVICE_ID__ECF_SERVICE_NAME);
        createEReference(iServiceIDEClass, ISERVICE_ID__SERVICE_TYPE_ID);
        iServiceTypeIDEClass = createEClass(ISERVICE_TYPE_ID);
        createEAttribute(iServiceTypeIDEClass, ISERVICE_TYPE_ID__ECF_SERVICE_TYPE_ID);
        createEAttribute(iServiceTypeIDEClass, ISERVICE_TYPE_ID__ECF_NAMING_AUTHORITY);
        createEAttribute(iServiceTypeIDEClass, ISERVICE_TYPE_ID__ECF_SERVICES);
        createEAttribute(iServiceTypeIDEClass, ISERVICE_TYPE_ID__ECF_PROTOCOLS);
        createEAttribute(iServiceTypeIDEClass, ISERVICE_TYPE_ID__ECF_SCOPES);
        createEAttribute(iServiceTypeIDEClass, ISERVICE_TYPE_ID__ECF_SERVICE_NAME);
        // Create data types
        ecfiServiceInfoEDataType = createEDataType(ECFI_SERVICE_INFO);
        inetAddressEDataType = createEDataType(INET_ADDRESS);
        ecfiServiceIDEDataType = createEDataType(ECFI_SERVICE_ID);
        ecfiServiceTypeIDEDataType = createEDataType(ECFI_SERVICE_TYPE_ID);
        uriEDataType = createEDataType(URI);
    }

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    private boolean isInitialized = false;

    /**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public void initializePackageContents() {
        if (isInitialized)
            return;
        isInitialized = true;
        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);
        // Add supertypes to classes
        // Initialize classes and features; add operations and parameters
        //$NON-NLS-1$
        initEClass(iServiceInfoEClass, IServiceInfo.class, "IServiceInfo", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        //$NON-NLS-1$
        initEAttribute(getIServiceInfo_EcfServiceInfo(), this.getECFIServiceInfo(), "ecfServiceInfo", null, 0, 1, IServiceInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        //$NON-NLS-1$
        initEAttribute(getIServiceInfo_EcfName(), ecorePackage.getEString(), "ecfName", null, 0, 1, IServiceInfo.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        //$NON-NLS-1$
        initEAttribute(getIServiceInfo_EcfLocation(), this.getURI(), "ecfLocation", null, 0, 1, IServiceInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        //$NON-NLS-1$
        initEAttribute(getIServiceInfo_EcfPriority(), ecorePackage.getEInt(), "ecfPriority", null, 0, 1, IServiceInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        //$NON-NLS-1$
        initEAttribute(getIServiceInfo_EcfWeight(), ecorePackage.getEInt(), "ecfWeight", null, 0, 1, IServiceInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        //$NON-NLS-1$
        initEReference(getIServiceInfo_ServiceID(), this.getIServiceID(), null, "serviceID", null, 0, 1, IServiceInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        //$NON-NLS-1$
        initEClass(iNetworkEClass, INetwork.class, "INetwork", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        //$NON-NLS-1$
        initEReference(getINetwork_Hosts(), this.getIHost(), null, "hosts", null, 0, -1, INetwork.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        //$NON-NLS-1$
        initEClass(iHostEClass, IHost.class, "IHost", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        //$NON-NLS-1$
        initEReference(getIHost_Services(), this.getIServiceInfo(), null, "services", null, 0, -1, IHost.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getIHost_Address(), this.getInetAddress(), "address", "127.0.0.1", 0, 1, IHost.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getIHost_Name(), ecorePackage.getEString(), "name", "", 0, 1, IHost.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        //$NON-NLS-1$
        initEClass(iServiceIDEClass, IServiceID.class, "IServiceID", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        //$NON-NLS-1$
        initEAttribute(getIServiceID_EcfServiceID(), this.getECFIServiceID(), "ecfServiceID", null, 0, 1, IServiceID.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        //$NON-NLS-1$
        initEAttribute(getIServiceID_EcfServiceName(), ecorePackage.getEString(), "ecfServiceName", null, 0, 1, IServiceID.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        //$NON-NLS-1$
        initEReference(getIServiceID_ServiceTypeID(), this.getIServiceTypeID(), null, "serviceTypeID", null, 0, 1, IServiceID.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        //$NON-NLS-1$
        initEClass(iServiceTypeIDEClass, IServiceTypeID.class, "IServiceTypeID", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        //$NON-NLS-1$
        initEAttribute(getIServiceTypeID_EcfServiceTypeID(), this.getECFIServiceTypeID(), "ecfServiceTypeID", null, 0, 1, IServiceTypeID.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        //$NON-NLS-1$
        initEAttribute(getIServiceTypeID_EcfNamingAuthority(), ecorePackage.getEString(), "ecfNamingAuthority", null, 0, 1, IServiceTypeID.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        //$NON-NLS-1$
        initEAttribute(getIServiceTypeID_EcfServices(), ecorePackage.getEString(), "ecfServices", null, 0, -1, IServiceTypeID.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        //$NON-NLS-1$
        initEAttribute(getIServiceTypeID_EcfProtocols(), ecorePackage.getEString(), "ecfProtocols", null, 0, -1, IServiceTypeID.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        //$NON-NLS-1$
        initEAttribute(getIServiceTypeID_EcfScopes(), ecorePackage.getEString(), "ecfScopes", null, 0, -1, IServiceTypeID.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        //$NON-NLS-1$
        initEAttribute(getIServiceTypeID_EcfServiceName(), ecorePackage.getEString(), "ecfServiceName", null, 0, 1, IServiceTypeID.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        // Initialize data types
        //$NON-NLS-1$
        initEDataType(ecfiServiceInfoEDataType, org.eclipse.ecf.discovery.IServiceInfo.class, "ECFIServiceInfo", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
        //$NON-NLS-1$
        initEDataType(inetAddressEDataType, InetAddress.class, "InetAddress", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
        //$NON-NLS-1$
        initEDataType(ecfiServiceIDEDataType, org.eclipse.ecf.discovery.identity.IServiceID.class, "ECFIServiceID", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
        //$NON-NLS-1$
        initEDataType(ecfiServiceTypeIDEDataType, org.eclipse.ecf.discovery.identity.IServiceTypeID.class, "ECFIServiceTypeID", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
        //$NON-NLS-1$
        initEDataType(uriEDataType, java.net.URI.class, "URI", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
        // Create resource
        createResource(eNS_URI);
    }
}
//ModelPackageImpl
