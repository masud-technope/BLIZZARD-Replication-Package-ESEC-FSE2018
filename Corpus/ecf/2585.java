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
package org.eclipse.ecf.discovery.ui.model.provider;

import java.util.List;
import org.eclipse.ecf.discovery.ui.model.IServiceTypeID;
import org.eclipse.ecf.discovery.ui.model.ItemProviderWithStatusLineAdapter;
import org.eclipse.ecf.discovery.ui.model.ModelPackage;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;

/**
 * This is the item provider adapter for a {@link org.eclipse.ecf.discovery.ui.model.IServiceTypeID} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class IServiceTypeIDItemProvider extends ItemProviderWithStatusLineAdapter implements IEditingDomainItemProvider, IStructuredItemContentProvider, ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource {

    /**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public  IServiceTypeIDItemProvider(AdapterFactory adapterFactory) {
        super(adapterFactory);
    }

    /**
	 * This returns the property descriptors for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public List getPropertyDescriptors(Object object) {
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);
            addEcfNamingAuthorityPropertyDescriptor(object);
            addEcfServicesPropertyDescriptor(object);
            addEcfProtocolsPropertyDescriptor(object);
            addEcfScopesPropertyDescriptor(object);
            addEcfServiceNamePropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
	 * This adds a property descriptor for the Ecf Naming Authority feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    protected void addEcfNamingAuthorityPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(), getString(//$NON-NLS-1$
        "_UI_IServiceTypeID_ecfNamingAuthority_feature"), getString(//$NON-NLS-1$
        "_UI_IServiceTypeID_ecfNamingAuthority_description"), ModelPackage.Literals.ISERVICE_TYPE_ID__ECF_NAMING_AUTHORITY, false, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, getString(//$NON-NLS-1$
        "_UI_DiscoveryPropertyCategory"), null));
    }

    /**
	 * This adds a property descriptor for the Ecf Protocols feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    protected void addEcfProtocolsPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(), getString(//$NON-NLS-1$
        "_UI_IServiceTypeID_ecfProtocols_feature"), getString(//$NON-NLS-1$
        "_UI_IServiceTypeID_ecfProtocols_description"), ModelPackage.Literals.ISERVICE_TYPE_ID__ECF_PROTOCOLS, false, true, true, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, getString(//$NON-NLS-1$
        "_UI_DiscoveryPropertyCategory"), null));
    }

    /**
	 * This adds a property descriptor for the Ecf Scopes feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    protected void addEcfScopesPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(), getString(//$NON-NLS-1$
        "_UI_IServiceTypeID_ecfScopes_feature"), getString(//$NON-NLS-1$
        "_UI_IServiceTypeID_ecfScopes_description"), ModelPackage.Literals.ISERVICE_TYPE_ID__ECF_SCOPES, false, true, true, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, getString(//$NON-NLS-1$
        "_UI_DiscoveryPropertyCategory"), null));
    }

    /**
	 * This adds a property descriptor for the Ecf Service Name feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    protected void addEcfServiceNamePropertyDescriptor(Object object) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(), getString(//$NON-NLS-1$
        "_UI_IServiceTypeID_ecfServiceName_feature"), getString(//$NON-NLS-1$
        "_UI_IServiceTypeID_ecfServiceName_description"), ModelPackage.Literals.ISERVICE_TYPE_ID__ECF_SERVICE_NAME, false, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, getString(//$NON-NLS-1$
        "_UI_DiscoveryPropertyCategory"), null));
    }

    /**
	 * This adds a property descriptor for the Ecf Services feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    protected void addEcfServicesPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(), getString(//$NON-NLS-1$
        "_UI_IServiceTypeID_ecfServices_feature"), getString(//$NON-NLS-1$
        "_UI_IServiceTypeID_ecfServices_description"), ModelPackage.Literals.ISERVICE_TYPE_ID__ECF_SERVICES, false, true, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, getString(//$NON-NLS-1$
        "_UI_DiscoveryPropertyCategory"), null));
    }

    /**
	 * This returns IServiceTypeID.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public Object getImage(Object object) {
        //$NON-NLS-1$
        return overlayImage(object, getResourceLocator().getImage("full/obj16/IServiceTypeID"));
    }

    /**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public String getText(Object object) {
        String label = ((IServiceTypeID) object).getEcfServiceName();
        return label == null || label.length() == 0 ? //$NON-NLS-1$
        getString("_UI_IServiceTypeID_type") : //$NON-NLS-1$ //$NON-NLS-2$
        getString("_UI_IServiceTypeID_type") + " " + label;
    }

    /**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public void notifyChanged(Notification notification) {
        updateChildren(notification);
        switch(notification.getFeatureID(IServiceTypeID.class)) {
            case ModelPackage.ISERVICE_TYPE_ID__ECF_SERVICE_TYPE_ID:
            case ModelPackage.ISERVICE_TYPE_ID__ECF_NAMING_AUTHORITY:
            case ModelPackage.ISERVICE_TYPE_ID__ECF_SERVICES:
            case ModelPackage.ISERVICE_TYPE_ID__ECF_PROTOCOLS:
            case ModelPackage.ISERVICE_TYPE_ID__ECF_SCOPES:
            case ModelPackage.ISERVICE_TYPE_ID__ECF_SERVICE_NAME:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
                return;
        }
        super.notifyChanged(notification);
    }

    /**
	 * Return the resource locator for this item provider's resources.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public ResourceLocator getResourceLocator() {
        return DiscoveryEditPlugin.INSTANCE;
    }
}
