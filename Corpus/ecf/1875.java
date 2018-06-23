/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.ui.wizards;

import org.eclipse.core.runtime.*;
import org.eclipse.ecf.internal.ui.Activator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.ui.*;
import org.eclipse.ui.model.*;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardDescriptor;

/**
 * Instances represent registered wizards.
 */
public class WorkbenchWizardElement extends WorkbenchAdapter implements IAdaptable, IPluginContribution, IWizardDescriptor {

    private String id;

    private ImageDescriptor imageDescriptor;

    private SelectionEnabler selectionEnabler;

    private IConfigurationElement configurationElement;

    private ImageDescriptor descriptionImage;

    private WizardCollectionElement parentCategory;

    /**
	 * Create a new instance of this class
	 * 
	 * @param configurationElement
	 * @since 3.1
	 */
    public  WorkbenchWizardElement(IConfigurationElement configurationElement) {
        this.configurationElement = configurationElement;
        id = configurationElement.getAttribute(IWizardRegistryConstants.ATT_ID);
    }

    /**
	 * Answer a boolean indicating whether the receiver is able to handle the
	 * passed selection
	 * 
	 * @return boolean
	 * @param selection
	 *            IStructuredSelection
	 */
    public boolean canHandleSelection(IStructuredSelection selection) {
        return getSelectionEnabler().isEnabledForSelection(selection);
    }

    /**
	 * Answer the selection for the reciever based on whether the it can handle
	 * the selection. If it can return the selection. If it can handle the
	 * adapted to IResource value of the selection. If it satisfies neither of
	 * these conditions return an empty IStructuredSelection.
	 * 
	 * @return IStructuredSelection
	 * @param selection
	 *            IStructuredSelection
	 */
    public IStructuredSelection adaptedSelection(IStructuredSelection selection) {
        if (canHandleSelection(selection))
            return selection;
        IStructuredSelection adaptedSelection = convertToResources(selection);
        if (canHandleSelection(adaptedSelection))
            return adaptedSelection;
        // Couldn't find one that works so just return
        return StructuredSelection.EMPTY;
    }

    /**
	 * Create an the instance of the object described by the configuration
	 * element. That is, create the instance of the class the isv supplied in
	 * the extension point.
	 * 
	 * @return the new object
	 * @throws CoreException
	 */
    public Object createExecutableExtension() throws CoreException {
        return configurationElement.createExecutableExtension(IWizardRegistryConstants.ATT_CLASS);
    }

    /**
	 * Returns an object which is an instance of the given class associated with
	 * this object. Returns <code>null</code> if no such object can be found.
	 */
    public Object getAdapter(Class adapter) {
        if (adapter == IWorkbenchAdapter.class || adapter == IWorkbenchAdapter2.class)
            return this;
        else if (adapter == IPluginContribution.class)
            return this;
        else if (adapter == IConfigurationElement.class)
            return configurationElement;
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }

    /**
	 * @return IConfigurationElement
	 */
    public IConfigurationElement getConfigurationElement() {
        return configurationElement;
    }

    public String getContainerTypeName() {
        return configurationElement.getAttribute(IWizardRegistryConstants.ATT_CONTAINER_TYPE_NAME);
    }

    /**
	 * Answer the description parameter of this element
	 * 
	 * @return java.lang.String
	 */
    public String getDescription() {
        return configurationElement.getAttribute(IWizardRegistryConstants.ATT_DESCRIPTION);
    }

    /**
	 * Answer the icon of this element.
	 */
    public ImageDescriptor getImageDescriptor() {
        if (imageDescriptor == null) {
            String iconName = configurationElement.getAttribute(IWizardRegistryConstants.ATT_ICON);
            if (iconName == null)
                return null;
            imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(configurationElement.getNamespaceIdentifier(), iconName);
        }
        return imageDescriptor;
    }

    /**
	 * Returns the name of this wizard element.
	 */
    public ImageDescriptor getImageDescriptor(Object element) {
        return getImageDescriptor();
    }

    /**
	 * Returns the name of this wizard element.
	 */
    public String getLabel(Object element) {
        return configurationElement.getAttribute(IWizardRegistryConstants.ATT_NAME);
    }

    /**
	 * Answer self's action enabler, creating it first iff necessary
	 */
    protected SelectionEnabler getSelectionEnabler() {
        if (selectionEnabler == null) {
            selectionEnabler = new SelectionEnabler(configurationElement);
        }
        return selectionEnabler;
    }

    /**
	 * Attempt to convert the elements in the passed selection into resources by
	 * asking each for its IResource property (iff it isn't already a resource).
	 * If all elements in the initial selection can be converted to resources
	 * then answer a new selection containing these resources; otherwise answer
	 * an empty selection.
	 * 
	 * @param originalSelection
	 *            the original selection
	 * @return the converted selection or an empty selection
	 */
    private IStructuredSelection convertToResources(IStructuredSelection originalSelection) {
        /*
		 * Object selectionService = PlatformUI.getWorkbench().getService(
		 * ISelectionConversionService.class); if (selectionService == null ||
		 * originalSelection == null) { return StructuredSelection.EMPTY; }
		 * return ((ISelectionConversionService) selectionService)
		 * .convertToResources(originalSelection);
		 */
        return StructuredSelection.EMPTY;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPluginContribution#getLocalId()
	 */
    public String getLocalId() {
        return getId();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPluginContribution#getPluginId()
	 */
    public String getPluginId() {
        return (configurationElement != null) ? configurationElement.getNamespaceIdentifier() : null;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.wizards.INewWizardDescriptor#getDescriptionImage()
	 */
    public ImageDescriptor getDescriptionImage() {
        if (descriptionImage == null) {
            String descImage = configurationElement.getAttribute(IWizardRegistryConstants.ATT_DESCRIPTION_IMAGE);
            if (descImage == null)
                return null;
            descriptionImage = AbstractUIPlugin.imageDescriptorFromPlugin(configurationElement.getNamespaceIdentifier(), descImage);
        }
        return descriptionImage;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.wizards.INewWizardDescriptor#getHelpHref()
	 */
    public String getHelpHref() {
        // XXX todo change to constant
        return configurationElement.getAttribute(IWizardRegistryConstants.HELP_HREF);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.wizards.INewWizardDescriptor#createWizard()
	 */
    public IWorkbenchWizard createWizard() throws CoreException {
        //$NON-NLS-1$
        throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Cannot create workbench wizard", null));
    }

    public IWizard createWizardForNode() throws CoreException {
        return (IWizard) createExecutableExtension();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPartDescriptor#getId()
	 */
    public String getId() {
        return id;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPartDescriptor#getLabel()
	 */
    public String getLabel() {
        return getLabel(this);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.wizards.INewWizardDescriptor#getCategory()
	 */
    public IWizardCategory getCategory() {
        return (IWizardCategory) getParent(this);
    }

    /**
	 * Return the collection.
	 * 
	 * @return the collection
	 * @since 3.1
	 */
    public WizardCollectionElement getCollectionElement() {
        return (WizardCollectionElement) getParent(this);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.wizards.IWizardDescriptor#getTags()
	 */
    public String[] getTags() {
        return new String[0];
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getParent(java.lang.Object)
	 */
    public Object getParent(Object object) {
        return parentCategory;
    }

    /**
	 * Set the parent category.
	 * 
	 * @param parent
	 *            the parent category
	 * @since 3.1
	 */
    public void setParent(WizardCollectionElement parent) {
        parentCategory = parent;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.wizards.IWizardDescriptor#canFinishEarly()
	 */
    public boolean canFinishEarly() {
        return Boolean.valueOf(configurationElement.getAttribute(IWizardRegistryConstants.CAN_FINISH_EARLY)).booleanValue();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.wizards.IWizardDescriptor#hasPages()
	 */
    public boolean hasPages() {
        String hasPagesString = configurationElement.getAttribute(IWizardRegistryConstants.HAS_PAGES);
        // default value is true
        if (hasPagesString == null)
            return true;
        return Boolean.valueOf(hasPagesString).booleanValue();
    }
}
