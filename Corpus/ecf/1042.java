/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.ui;

import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Required interface for implementing
 * <b>org.eclipse.ecf.ui.configurationWizards</b> extension point. Extensions
 * for extension point <b>org.eclipse.ecf.ui.configurationWizards</b> must
 * provide a class implementing this interface.
 */
public interface IConfigurationWizard extends IWizard {

    /**
	 * Initialize the wizard with the workbench and the desired container type
	 * description
	 * 
	 * @param workbench
	 *            the workbench for the wizard. Will not be null.
	 * @param description
	 *            the {@link ContainerTypeDescription} to use to
	 *            create/configure the new IContainer instance
	 */
    public void init(IWorkbench workbench, ContainerTypeDescription description);

    /**
	 * Get result of configuration.
	 * 
	 * @return ContainerConfigurationResult the result of the configuration. If
	 *         null, the container could not be created.
	 */
    public ContainerConfigurationResult getConfigurationResult();
}
