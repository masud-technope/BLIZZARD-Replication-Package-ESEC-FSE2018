/****************************************************************************
 * Copyright (c) 2006 Remy Suen, Composent, Inc., and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.provider.ui.wizards;

import java.net.URI;
import org.eclipse.ecf.ui.SharedImages;
import org.eclipse.ecf.ui.wizards.AbstractConnectWizardPage;

/**
 * @since 1.4
 */
public class GenericClientContainerConnectWizardPage extends AbstractConnectWizardPage {

    public  GenericClientContainerConnectWizardPage() {
        super("GenericClientContainerConnectWizardPage");
        setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMG_COLLABORATION_WIZARD));
    }

    public  GenericClientContainerConnectWizardPage(URI uri) {
        super("GenericClientContainerConnectWizardPage", uri);
        setImageDescriptor(SharedImages.getImageDescriptor(SharedImages.IMG_COLLABORATION_WIZARD));
    }

    public boolean shouldRequestUsername() {
        return true;
    }

    public boolean shouldRequestPassword() {
        return true;
    }

    public String getExampleID() {
        return "ecftcp://<server>:<port>/<groupname>";
    }

    protected String getProviderTitle() {
        return "ECF Generic Client Connection";
    }

    protected String getProviderDescription() {
        return "Creates a connection to the specified ECF Generic Server.";
    }

    protected String getDefaultConnectText() {
        return "ecftcp://ecf.eclipse.org:3282/server";
    }
}
