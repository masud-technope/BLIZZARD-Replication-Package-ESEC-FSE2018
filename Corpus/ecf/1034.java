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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>IService Type ID</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.IServiceTypeID#getEcfServiceTypeID <em>Ecf Service Type ID</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.IServiceTypeID#getEcfNamingAuthority <em>Ecf Naming Authority</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.IServiceTypeID#getEcfServices <em>Ecf Services</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.IServiceTypeID#getEcfProtocols <em>Ecf Protocols</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.IServiceTypeID#getEcfScopes <em>Ecf Scopes</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.IServiceTypeID#getEcfServiceName <em>Ecf Service Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage#getIServiceTypeID()
 * @model
 * @generated
 */
public interface IServiceTypeID extends EObject {

    /**
	 * Returns the value of the '<em><b>Ecf Service Type ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ecf Service Type ID</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ecf Service Type ID</em>' attribute.
	 * @see #setEcfServiceTypeID(org.eclipse.ecf.discovery.identity.IServiceTypeID)
	 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage#getIServiceTypeID_EcfServiceTypeID()
	 * @model dataType="org.eclipse.ecf.discovery.model.ECFIServiceTypeID"
	 * @generated
	 */
    org.eclipse.ecf.discovery.identity.IServiceTypeID getEcfServiceTypeID();

    /**
	 * Sets the value of the '{@link org.eclipse.ecf.discovery.ui.model.IServiceTypeID#getEcfServiceTypeID <em>Ecf Service Type ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ecf Service Type ID</em>' attribute.
	 * @see #getEcfServiceTypeID()
	 * @generated
	 */
    void setEcfServiceTypeID(org.eclipse.ecf.discovery.identity.IServiceTypeID value);

    /**
	 * Returns the value of the '<em><b>Ecf Naming Authority</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ecf Naming Authority</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ecf Naming Authority</em>' attribute.
	 * @see #setEcfNamingAuthority(String)
	 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage#getIServiceTypeID_EcfNamingAuthority()
	 * @model
	 * @generated
	 */
    String getEcfNamingAuthority();

    /**
	 * Sets the value of the '{@link org.eclipse.ecf.discovery.ui.model.IServiceTypeID#getEcfNamingAuthority <em>Ecf Naming Authority</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ecf Naming Authority</em>' attribute.
	 * @see #getEcfNamingAuthority()
	 * @generated
	 */
    void setEcfNamingAuthority(String value);

    /**
	 * Returns the value of the '<em><b>Ecf Services</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ecf Services</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ecf Services</em>' attribute list.
	 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage#getIServiceTypeID_EcfServices()
	 * @model
	 * @generated
	 */
    EList getEcfServices();

    /**
	 * Returns the value of the '<em><b>Ecf Protocols</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ecf Protocols</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ecf Protocols</em>' attribute list.
	 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage#getIServiceTypeID_EcfProtocols()
	 * @model
	 * @generated
	 */
    EList getEcfProtocols();

    /**
	 * Returns the value of the '<em><b>Ecf Scopes</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ecf Scopes</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ecf Scopes</em>' attribute list.
	 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage#getIServiceTypeID_EcfScopes()
	 * @model
	 * @generated
	 */
    EList getEcfScopes();

    /**
	 * Returns the value of the '<em><b>Ecf Service Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ecf Service Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ecf Service Name</em>' attribute.
	 * @see #setEcfServiceName(String)
	 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage#getIServiceTypeID_EcfServiceName()
	 * @model
	 * @generated
	 */
    String getEcfServiceName();

    /**
	 * Sets the value of the '{@link org.eclipse.ecf.discovery.ui.model.IServiceTypeID#getEcfServiceName <em>Ecf Service Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ecf Service Name</em>' attribute.
	 * @see #getEcfServiceName()
	 * @generated
	 */
    void setEcfServiceName(String value);
}
// IServiceTypeID
