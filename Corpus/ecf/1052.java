/*******************************************************************************
 * Copyright (c) 2007 Remy Suen, Composent, Inc., and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *    Scott Lewis <slewis@composent.com> - error checking
 ******************************************************************************/
package org.eclipse.ecf.internal.ui.actions;

import java.util.*;
import java.util.List;
import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.*;
import org.eclipse.ecf.internal.ui.Activator;
import org.eclipse.ecf.internal.ui.Messages;
import org.eclipse.ecf.internal.ui.wizards.IWizardRegistryConstants;
import org.eclipse.ecf.ui.*;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

// TODO, we should rework this class... it's confusing >_<
public class SelectProviderAction implements IWizardRegistryConstants, IWorkbenchWindowActionDelegate, IWorkbenchWindowPulldownDelegate, IViewActionDelegate {

    private IWorkbenchWindow window;

    private Menu menu;

    private HashMap map = new HashMap();

    private List elements = new ArrayList();

    private IExtension[] configurationWizards;

    public  SelectProviderAction() {
        try {
            IExtensionRegistry registry = Activator.getDefault().getExtensionRegistry();
            if (registry != null) {
                configurationWizards = registry.getExtensionPoint(CONFIGURE_EPOINT_ID).getExtensions();
                IExtension[] connectWizards = registry.getExtensionPoint(CONNECT_EPOINT_ID).getExtensions();
                for (int i = 0; i < connectWizards.length; i++) {
                    final IConfigurationElement[] ices = connectWizards[i].getConfigurationElements();
                    for (int j = 0; j < ices.length; j++) {
                        if (ices[j].getName().equals(ELEMENT_CATEGORY)) {
                            continue;
                        }
                        final String factoryName = ices[j].getAttribute(ATT_CONTAINER_TYPE_NAME);
                        final IConfigurationWizard wizard = getWizard(configurationWizards, factoryName);
                        final IConfigurationElement ice = ices[j];
                        ContainerTypeDescription typeDescription = ContainerFactory.getDefault().getDescriptionByName(factoryName);
                        if (typeDescription != null) {
                            if (!typeDescription.isHidden()) {
                                // add to list
                                elements.add(ice);
                                if (wizard == null) {
                                    map.put(ice.getAttribute(ATT_NAME), new SelectionAdapter() {

                                        public void widgetSelected(SelectionEvent e) {
                                            openConnectWizard(ice, factoryName);
                                        }
                                    });
                                } else {
                                    map.put(ice.getAttribute(ATT_NAME), new SelectionAdapter() {

                                        public void widgetSelected(SelectionEvent e) {
                                            openConnectWizard(wizard, ice, factoryName);
                                        }
                                    });
                                }
                            }
                        } else {
                            Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, IStatus.WARNING, NLS.bind(Messages.SelectProviderAction_WARNING_CONTAINER_TYPE_DESCRIPTION_NOT_FOUND, factoryName), null));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void openConnectWizard(IConfigurationElement element, String factoryName) {
        try {
            IContainer container = ContainerFactory.getDefault().createContainer(factoryName);
            IConnectWizard icw = (IConnectWizard) element.createExecutableExtension(ATT_CLASS);
            icw.init(window.getWorkbench(), container);
            WizardDialog dialog = new WizardDialog(window.getShell(), icw);
            dialog.open();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void openConnectWizard(IConfigurationWizard wizard, IConfigurationElement element, String factoryName) {
        try {
            IWorkbench workbench = window.getWorkbench();
            wizard.init(workbench, ContainerFactory.getDefault().getDescriptionByName(factoryName));
            WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
            if (dialog.open() == Window.OK) {
                IConnectWizard icw = (IConnectWizard) element.createExecutableExtension(ATT_CLASS);
                icw.init(workbench, wizard.getConfigurationResult().getContainer());
                dialog = new WizardDialog(window.getShell(), icw);
                dialog.open();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void dispose() {
        // dispose of the menu
        if (menu != null && !menu.isDisposed()) {
            menu.dispose();
        }
    }

    public void init(IWorkbenchWindow w) {
        this.window = w;
    }

    public void run(IAction action) {
        ElementListSelectionDialog dialog = new ElementListSelectionDialog(window.getShell(), new ProviderLabelProvider());
        dialog.setElements(elements.toArray());
        dialog.setTitle(Messages.SelectProviderAction_selectProviderDialog_title);
        dialog.setMessage(Messages.SelectProviderAction_selectProviderDialog_message);
        dialog.setImage(SharedImages.getImage(SharedImages.IMG_COMMUNICATIONS));
        dialog.setHelpAvailable(false);
        if (dialog.open() == Window.OK) {
            Object[] result = dialog.getResult();
            IConfigurationElement element = (IConfigurationElement) result[0];
            String factoryName = element.getAttribute(ATT_CONTAINER_TYPE_NAME);
            IConfigurationWizard wizard = getWizard(configurationWizards, factoryName);
            if (wizard == null) {
                openConnectWizard(element, factoryName);
            } else {
                openConnectWizard(wizard, element, factoryName);
            }
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
    // nothing to do
    }

    private static IConfigurationWizard getWizard(IExtension[] extensions, String containerFactoryName) {
        for (int i = 0; i < extensions.length; i++) {
            IConfigurationElement[] elements = extensions[i].getConfigurationElements();
            for (int j = 0; j < elements.length; j++) {
                if (containerFactoryName.equals(elements[j].getAttribute(ATT_CONTAINER_TYPE_NAME))) {
                    IConfigurationWizard wizard = null;
                    try {
                        wizard = (IConfigurationWizard) elements[j].createExecutableExtension(ATT_CLASS);
                    } catch (CoreException e) {
                        Activator.log(e.getMessage());
                    }
                    return wizard;
                }
            }
        }
        return null;
    }

    public Menu getMenu(Control parent) {
        if (menu == null) {
            menu = new Menu(parent);
            for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
                String name = (String) it.next();
                MenuItem item = new MenuItem(menu, SWT.PUSH);
                item.setText(name);
                item.addSelectionListener((SelectionListener) map.get(name));
            }
        }
        return menu;
    }

    // isn't this a funny name?
    class ProviderLabelProvider extends LabelProvider {

        public Image getImage(Object element) {
            return null;
        }

        public String getText(Object element) {
            IConfigurationElement provider = (IConfigurationElement) element;
            return provider.getAttribute(ATT_NAME);
        }
    }

    public void init(IViewPart view) {
        this.window = view.getSite().getWorkbenchWindow();
    }
}
