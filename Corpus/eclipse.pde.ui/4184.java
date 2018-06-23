/*******************************************************************************
 *  Copyright (c) 2000, 2016 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <Lars.Vogel@vogella.com> - Bug 487943
 *******************************************************************************/
package org.eclipse.pde.internal.ui.samples;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.internal.ui.*;
import org.eclipse.pde.internal.ui.parts.TablePart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PlatformUI;

public class SelectionPage extends WizardPage {

    private TablePart part;

    private Text desc;

    private SampleWizard wizard;

    class SelectionPart extends TablePart {

        public  SelectionPart() {
            //$NON-NLS-1$
            super(new String[] { "More Info" });
        }

        @Override
        protected void buttonSelected(Button button, int index) {
            if (index == 0)
                doMoreInfo();
        }

        @Override
        protected void selectionChanged(IStructuredSelection selection) {
            updateSelection(selection);
        }

        @Override
        protected void handleDoubleClick(IStructuredSelection selection) {
        }
    }

    class SampleProvider implements IStructuredContentProvider {

        @Override
        public Object[] getElements(Object input) {
            return wizard.getSamples();
        }
    }

    class SampleLabelProvider extends LabelProvider {

        private Image image;

        public  SampleLabelProvider() {
            image = PDEPlugin.getDefault().getLabelProvider().get(PDEPluginImages.DESC_NEWEXP_TOOL);
        }

        @Override
        public String getText(Object obj) {
            IConfigurationElement sample = (IConfigurationElement) obj;
            //$NON-NLS-1$
            return sample.getAttribute("name");
        }

        @Override
        public Image getImage(Object obj) {
            return image;
        }
    }

    /**
	 * @param pageName
	 */
    public  SelectionPage(SampleWizard wizard) {
        //$NON-NLS-1$
        super("selection");
        this.wizard = wizard;
        setTitle(PDEUIMessages.SelectionPage_title);
        setDescription(PDEUIMessages.SelectionPage_desc);
        part = new SelectionPart();
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 2;
        part.setMinimumSize(300, 300);
        part.createControl(container, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, 2, null);
        part.getTableViewer().setContentProvider(new SampleProvider());
        part.getTableViewer().setLabelProvider(new SampleLabelProvider());
        desc = new Text(container, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 64;
        desc.setLayoutData(gd);
        part.getTableViewer().setInput(this);
        updateSelection(null);
        setControl(container);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(container, IHelpContextIds.SELECTION);
    }

    private void doMoreInfo() {
        if (wizard.getSelection() != null) {
            //$NON-NLS-1$
            IConfigurationElement desc[] = wizard.getSelection().getChildren("description");
            //$NON-NLS-1$
            String helpHref = desc[0].getAttribute("helpHref");
            PlatformUI.getWorkbench().getHelpSystem().displayHelpResource(helpHref);
        }
    }

    private void updateSelection(IStructuredSelection selection) {
        if (selection == null) {
            //$NON-NLS-1$
            desc.setText("");
            part.setButtonEnabled(0, false);
            setPageComplete(false);
        } else {
            IConfigurationElement sample = (IConfigurationElement) selection.getFirstElement();
            //$NON-NLS-1$
            String text = "";
            String helpHref = null;
            //$NON-NLS-1$
            IConfigurationElement[] sampleDesc = sample.getChildren("description");
            if (sampleDesc.length == 1) {
                text = sampleDesc[0].getValue();
                helpHref = //$NON-NLS-1$
                sampleDesc[0].getAttribute(//$NON-NLS-1$
                "helpHref");
            }
            desc.setText(text);
            part.setButtonEnabled(0, helpHref != null);
            wizard.setSelection(sample);
            wizard.updateEntries();
            setPageComplete(true);
        }
    }
}
