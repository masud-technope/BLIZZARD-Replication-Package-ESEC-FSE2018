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

import java.net.URI;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>IService Info</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.IServiceInfo#getEcfServiceInfo <em>Ecf Service Info</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.IServiceInfo#getEcfName <em>Ecf Name</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.IServiceInfo#getEcfLocation <em>Ecf Location</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.IServiceInfo#getEcfPriority <em>Ecf Priority</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.IServiceInfo#getEcfWeight <em>Ecf Weight</em>}</li>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.IServiceInfo#getServiceID <em>Service ID</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage#getIServiceInfo()
 * @model
 * @generated
 */
public interface IServiceInfo extends EObject {

    /**
	 * Returns the value of the '<em><b>Ecf Service Info</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ecf Service Info</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ecf Service Info</em>' attribute.
	 * @see #setEcfServiceInfo(org.eclipse.ecf.discovery.IServiceInfo)
	 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage#getIServiceInfo_EcfServiceInfo()
	 * @model dataType="org.eclipse.ecf.discovery.model.ECFIServiceInfo"
	 * @generated
	 */
    org.eclipse.ecf.discovery.IServiceInfo getEcfServiceInfo();

    /**
	 * Sets the value of the '{@link org.eclipse.ecf.discovery.ui.model.IServiceInfo#getEcfServiceInfo <em>Ecf Service Info</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ecf Service Info</em>' attribute.
	 * @see #getEcfServiceInfo()
	 * @generated
	 */
    void setEcfServiceInfo(org.eclipse.ecf.discovery.IServiceInfo value);

    /**
	 * Returns the value of the '<em><b>Ecf Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ecf Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ecf Name</em>' attribute.
	 * @see #setEcfName(String)
	 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage#getIServiceInfo_EcfName()
	 * @model transient="true"
	 * @generated
	 */
    String getEcfName();

    /**
	 * Sets the value of the '{@link org.eclipse.ecf.discovery.ui.model.IServiceInfo#getEcfName <em>Ecf Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ecf Name</em>' attribute.
	 * @see #getEcfName()
	 * @generated
	 */
    void setEcfName(String value);

    /**
	 * Returns the value of the '<em><b>Ecf Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ecf Location</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ecf Location</em>' attribute.
	 * @see #setEcfLocation(URI)
	 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage#getIServiceInfo_EcfLocation()
	 * @model dataType="org.eclipse.ecf.discovery.model.URI"
	 * @generated
	 */
    URI getEcfLocation();

    /**
	 * Sets the value of the '{@link org.eclipse.ecf.discovery.ui.model.IServiceInfo#getEcfLocation <em>Ecf Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ecf Location</em>' attribute.
	 * @see #getEcfLocation()
	 * @generated
	 */
    void setEcfLocation(URI value);

    /**
	 * Returns the value of the '<em><b>Ecf Priority</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ecf Priority</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ecf Priority</em>' attribute.
	 * @see #setEcfPriority(int)
	 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage#getIServiceInfo_EcfPriority()
	 * @model
	 * @generated
	 */
    int getEcfPriority();

    /**
	 * Sets the value of the '{@link org.eclipse.ecf.discovery.ui.model.IServiceInfo#getEcfPriority <em>Ecf Priority</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ecf Priority</em>' attribute.
	 * @see #getEcfPriority()
	 * @generated
	 */
    void setEcfPriority(int value);

    /**
	 * Returns the value of the '<em><b>Ecf Weight</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ecf Weight</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ecf Weight</em>' attribute.
	 * @see #setEcfWeight(int)
	 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage#getIServiceInfo_EcfWeight()
	 * @model
	 * @generated
	 */
    int getEcfWeight();

    /**
	 * Sets the value of the '{@link org.eclipse.ecf.discovery.ui.model.IServiceInfo#getEcfWeight <em>Ecf Weight</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ecf Weight</em>' attribute.
	 * @see #getEcfWeight()
	 * @generated
	 */
    void setEcfWeight(int value);

    /**
	 * Returns the value of the '<em><b>Service ID</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Service ID</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Service ID</em>' reference.
	 * @see #setServiceID(IServiceID)
	 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage#getIServiceInfo_ServiceID()
	 * @model
	 * @generated
	 */
    IServiceID getServiceID();

    /**
	 * Sets the value of the '{@link org.eclipse.ecf.discovery.ui.model.IServiceInfo#getServiceID <em>Service ID</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Service ID</em>' reference.
	 * @see #getServiceID()
	 * @generated
	 */
    void setServiceID(IServiceID value);
}
// IServiceInfo
