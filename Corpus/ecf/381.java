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
 * A representation of the model object '<em><b>INetwork</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.ecf.discovery.ui.model.INetwork#getHosts <em>Hosts</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage#getINetwork()
 * @model
 * @generated
 */
public interface INetwork extends EObject {

    /**
	 * Returns the value of the '<em><b>Hosts</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.ecf.discovery.ui.model.IHost}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Hosts</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Hosts</em>' containment reference list.
	 * @see org.eclipse.ecf.discovery.ui.model.ModelPackage#getINetwork_Hosts()
	 * @model type="org.eclipse.ecf.discovery.model.IHost" containment="true" resolveProxies="true" changeable="false"
	 * @generated
	 */
    EList getHosts();
}
// INetwork
