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

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>IService ID</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.IServiceID#getEcfServiceID <em>Ecf Service ID</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.IServiceID#getEcfServiceName <em>Ecf Service Name</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.IServiceID#getServiceTypeID <em>Service Type ID</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage#getIServiceID()
 * @model
 * @generated
 */
public interface IServiceID extends EObject {

    /**
	 * Returns the value of the '<em><b>Ecf Service ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ecf Service ID</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ecf Service ID</em>' attribute.
	 * @see #setEcfServiceID(org.eclipse.ecf.discovery.identity.IServiceID)
	 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage#getIServiceID_EcfServiceID()
	 * @model dataType="org.eclipse.ecf.discovery.model.ECFIServiceID"
	 * @generated
	 */
    org.eclipse.ecf.discovery.identity.IServiceID getEcfServiceID();

    /**
	 * Sets the value of the '{@link org.eclipse.ecf.discovery.ui.model.IServiceID#getEcfServiceID <em>Ecf Service ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ecf Service ID</em>' attribute.
	 * @see #getEcfServiceID()
	 * @generated
	 */
    void setEcfServiceID(org.eclipse.ecf.discovery.identity.IServiceID value);

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
	 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage#getIServiceID_EcfServiceName()
	 * @model
	 * @generated
	 */
    String getEcfServiceName();

    /**
	 * Sets the value of the '{@link org.eclipse.ecf.discovery.ui.model.IServiceID#getEcfServiceName <em>Ecf Service Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ecf Service Name</em>' attribute.
	 * @see #getEcfServiceName()
	 * @generated
	 */
    void setEcfServiceName(String value);

    /**
	 * Returns the value of the '<em><b>Service Type ID</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Service Type ID</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Service Type ID</em>' reference.
	 * @see #setServiceTypeID(IServiceTypeID)
	 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage#getIServiceID_ServiceTypeID()
	 * @model
	 * @generated
	 */
    IServiceTypeID getServiceTypeID();

    /**
	 * Sets the value of the '{@link org.eclipse.ecf.discovery.ui.model.IServiceID#getServiceTypeID <em>Service Type ID</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Service Type ID</em>' reference.
	 * @see #getServiceTypeID()
	 * @generated
	 */
    void setServiceTypeID(IServiceTypeID value);
}
// IServiceID
