/*******************************************************************************
 * Copyright (c) 2009 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Chi Jian Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.sync.ui.resources;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.ide.IDE;

public class NotificationDialog extends PopupDialog {

    private ID sender;

    private String projectName;

    private IHyperlinkListener listener;

    private FormToolkit toolkit;

    public  NotificationDialog(Shell parent, ID sender, String projectName, IHyperlinkListener listener) {
        super(parent, SWT.TOOL | SWT.ON_TOP, false, false, false, false, false, null, null);
        this.sender = sender;
        this.projectName = projectName;
        this.listener = listener;
    }

    protected Control createContents(Composite parent) {
        toolkit = new FormToolkit(parent.getDisplay());
        Section section = toolkit.createSection(parent, Section.TITLE_BAR);
        section.setLayout(new GridLayout(1, false));
        section.setText("Incoming Collaboration Request");
        Composite composite = toolkit.createComposite(section);
        composite.setLayout(new GridLayout(1, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        section.setClient(composite);
        ImageHyperlink hyperlink = toolkit.createImageHyperlink(composite, SWT.LEAD);
        hyperlink.setHoverImage(PlatformUI.getWorkbench().getSharedImages().getImage(IDE.SharedImages.IMG_OBJ_PROJECT));
        hyperlink.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED));
        hyperlink.setText("Share Project Request");
        if (listener != null) {
            hyperlink.addHyperlinkListener(listener);
        }
        hyperlink.addHyperlinkListener(new CloseDialogListener());
        Control text = toolkit.createText(composite, sender.getName() + " has sent a request to share project '" + projectName + "' with you.");
        text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        return section;
    }

    protected Point getInitialLocation(Point initialSize) {
        //		return new Point(x, y);
        return super.getInitialLocation(initialSize);
    }

    public int open() {
        int open = super.open();
        //		job.schedule(5000);
        return open;
    }

    public boolean close() {
        if (toolkit != null) {
            toolkit.dispose();
            toolkit = null;
        }
        return super.close();
    }

    private class CloseDialogListener implements IHyperlinkListener {

        public void linkActivated(HyperlinkEvent e) {
            close();
        }

        public void linkEntered(HyperlinkEvent e) {
        }

        public void linkExited(HyperlinkEvent e) {
        }
    }
}
