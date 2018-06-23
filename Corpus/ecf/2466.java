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

import java.net.InetAddress;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>IHost</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.IHost#getServices <em>Services</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.IHost#getAddress <em>Address</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.IHost#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage#getIHost()
 * @model
 * @generated
 */
public interface IHost extends EObject {

    /**
	 * Returns the value of the '<em><b>Services</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.ecf.discovery.ui.model.IServiceInfo}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Services</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Services</em>' containment reference list.
	 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage#getIHost_Services()
	 * @model type="org.eclipse.ecf.discovery.model.IServiceInfo" containment="true" resolveProxies="true"
	 * @generated
	 */
    EList getServices();

    /**
	 * Returns the value of the '<em><b>Address</b></em>' attribute.
	 * The default value is <code>"127.0.0.1"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Address</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Address</em>' attribute.
	 * @see #setAddress(InetAddress)
	 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage#getIHost_Address()
	 * @model default="127.0.0.1" dataType="org.eclipse.ecf.discovery.model.InetAddress" transient="true"
	 * @generated
	 */
    InetAddress getAddress();

    /**
	 * Sets the value of the '{@link org.eclipse.ecf.discovery.ui.model.IHost#getAddress <em>Address</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Address</em>' attribute.
	 * @see #getAddress()
	 * @generated
	 */
    void setAddress(InetAddress value);

    /**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage#getIHost_Name()
	 * @model default="" transient="true"
	 * @generated
	 */
    String getName();

    /**
	 * Sets the value of the '{@link org.eclipse.ecf.discovery.ui.model.IHost#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
    void setName(String value);
}
// IHost
