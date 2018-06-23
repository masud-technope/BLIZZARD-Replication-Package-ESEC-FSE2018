/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.discovery;

import org.eclipse.ecf.core.IContainer;

/**
 * Adapter for IServiceInfo instances.
 * 
 * @since 3.0
 */
public interface IContainerServiceInfoAdapter {

    /**
	 * ECF Service Property Names
	 * 
	 * @since 3.0
	 **/
    //$NON-NLS-1$
    public static final String CONTAINER_FACTORY_NAME_PROPERTY = "org.eclipse.ecf.containerFactoryName";

    /**
	 * @since 3.0
	 */
    //$NON-NLS-1$
    public static final String CONTAINER_CONNECT_TARGET = "org.eclipse.ecf.connectTarget";

    /**
	 * @since 3.0
	 */
    //$NON-NLS-1$
    public static final String CONTAINER_CONNECT_TARGET_PROTOCOL = "org.eclipse.ecf.connectTargetProtocol";

    /**
	 * @since 3.0
	 */
    //$NON-NLS-1$
    public static final String CONTAINER_CONNECT_TARGET_PATH = "org.eclipse.ecf.connectTargetPath";

    /**
	 * @since 3.0
	 */
    //$NON-NLS-1$
    public static final String CONTAINER_CONNECT_REQUIRES_PASSWORD = "org.eclipse.ecf.connectContextRequiresPassword";

    /**
	 * Get container name associated with this service info.
	 * 
	 * @return the container factory name. Will return <code>null</code> if no
	 *         container factory name associated with this service info.
	 */
    public String getContainerFactoryName();

    /**
	 * Get the targetID for accessing the remote container. The String returned
	 * may be used to constuct a targetID for use in the
	 * IContainer.connect(targetID) call. For example:
	 * 
	 * <pre>
	 * IContainer container = ContainerFactory.getDefault().createContainer(
	 * 		this.getContainerFactoryName());
	 * ID targetID = IDFactory.getDefault().createID(container.getConnectNamespace(),
	 * 		this.getConnectTarget());
	 * container.connect(targetID, null);
	 * </pre>
	 * 
	 * @return String for use in connecting to the remote container. The
	 *         returned value should be of proper syntax to be used to create a
	 *         targetID for passing to
	 *         {@link IContainer#connect(org.eclipse.ecf.core.identity.ID, org.eclipse.ecf.core.security.IConnectContext)}
	 *         . May return <code>null</code> if there is incomplete/absent
	 *         information for the info.
	 */
    public String getConnectTarget();

    /**
	 * Get whether the target requires password.
	 * 
	 * @return Boolean.TRUE if does require password, Boolean.FALSE if not,
	 *         <code>null</code> if undefined.
	 */
    public Boolean connectRequiresPassword();

    /**
	 * Set the container properties. This method sets an implicit connect
	 * target. On the receiver, calls to {@link #getConnectTarget()} will
	 * complete the URI in the following way
	 * 
	 * <pre>
	 *  &lt;connectProtocol&gt;://&lt;IServiceInfo.getLocation().getAuthority()&gt;/&lt;connectPath&gt;
	 * </pre>
	 * 
	 * See {@link #getConnectTarget()}.
	 * 
	 * @param containerFactoryName
	 *            set the containerFactoryName for this info. May not be
	 *            <code>null</code>.
	 * @param connectProtocol
	 *            set the connectProtocol for the target. May not be
	 *            <code>null</code>.
	 * @param connectPath
	 *            set the connect path for the target. May be <code>null</code>.
	 * @param connectRequiresPassword
	 *            set whether the target requires a password. May be
	 *            <code>null</code>.
	 */
    public void setContainerProperties(String containerFactoryName, String connectProtocol, String connectPath, Boolean connectRequiresPassword);

    /**
	 * Set the container properties. This method sets an explicit connectTarget.
	 * The given connectTarget is assumed to be complete (e.g.
	 * ecftcp://user@host:port/path). See {@link #getConnectTarget()}.
	 * 
	 * @param containerFactoryName
	 *            set the containerFactoryName for this info. May not be
	 *            <code>null</code>.
	 * @param connectTarget
	 *            set the connectProtocol for the target. May not be
	 *            <code>null</code>.
	 * @param connectRequiresPassword
	 *            set whether the target requires a password. May be
	 *            <code>null</code>.
	 */
    public void setContainerProperties(String containerFactoryName, String connectTarget, Boolean connectRequiresPassword);
}
