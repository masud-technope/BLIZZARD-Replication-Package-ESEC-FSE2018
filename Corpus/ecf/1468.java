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
package org.eclipse.ecf.ui.hyperlink;

import java.net.URI;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.internal.ui.Activator;
import org.eclipse.ecf.internal.ui.Messages;
import org.eclipse.ecf.ui.IConnectWizard;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Abstract class for representing ECF URL hyperlinks.  This class can be subclassed to 
 * create an URL hyperlink specifically for accessing ECF protocols.  For example,
 * and XMPPURLHyperlink subclass can be created to open URLs of the form:
 * <p>
 * xmpp://foo@bar.com
 * </p>
 */
public abstract class AbstractURLHyperlink implements IHyperlink {

    private URI uri;

    private IRegion region;

    private String typeLabel;

    private String hyperlinkText;

    protected URI getURI() {
        return uri;
    }

    protected void setURI(URI uri) {
        this.uri = uri;
    }

    protected IRegion getRegion() {
        return region;
    }

    protected void setRegion(IRegion region) {
        this.region = region;
    }

    protected void setTypeLabel(String typeLabel) {
        this.typeLabel = typeLabel;
    }

    /**
	 * Creates a new URL hyperlink.
	 * 
	 * @param region
	 * @param uri
	 */
    public  AbstractURLHyperlink(IRegion region, URI uri) {
        Assert.isNotNull(uri);
        Assert.isNotNull(region);
        this.region = region;
        this.uri = uri;
    }

    protected abstract IContainer createContainer() throws ContainerCreateException;

    /**
	 * Create a connect wizard for this hyperlink.  Subclasses must implement this
	 * method to return a non-null instance of IConnectWizard when this method
	 * is called (during {@link #open()}.
	 * @return non-<code>null</code> instance implementing {@link IConnectWizard}.
	 */
    protected abstract IConnectWizard createConnectWizard();

    /*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IHyperlink#getHyperlinkRegion()
	 */
    public IRegion getHyperlinkRegion() {
        return region;
    }

    /*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IHyperlink#getTypeLabel()
	 */
    public String getTypeLabel() {
        return typeLabel;
    }

    /*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IHyperlink#getHyperlinkText()
	 */
    public String getHyperlinkText() {
        return hyperlinkText;
    }

    /**
	 * Returns the URL string of this hyperlink.
	 * 
	 * @return the URL string
	 * @since 3.2
	 */
    public String getURLString() {
        return uri.toString();
    }

    /**
	 * @param hyperlinkText
	 *            the hyperlinkText to set
	 */
    protected void setHyperlinkText(String hyperlinkText) {
        this.hyperlinkText = hyperlinkText;
    }

    /*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IHyperlink#open()
	 */
    public void open() {
        try {
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            IContainer container = createContainer();
            Assert.isNotNull(container);
            IConnectWizard icw = createConnectWizard();
            Assert.isNotNull(icw);
            icw.init(workbench, container);
            WizardDialog dialog = new WizardDialog(window.getShell(), icw);
            dialog.open();
        } catch (Exception ex) {
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR, Messages.AbstractURIHyperlink_EXCEPTION_HYPERLINK, ex);
            ErrorDialog.openError(null, null, null, status);
            Activator.getDefault().getLog().log(status);
        }
    }
}
