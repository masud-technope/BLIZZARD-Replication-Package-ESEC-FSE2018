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
import org.eclipse.ecf.discovery.ui.model.IServiceID;
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
 * This is the item provider adapter for a {@link org.eclipse.ecf.discovery.ui.model.IServiceID} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class IServiceIDItemProvider extends ItemProviderWithStatusLineAdapter implements IEditingDomainItemProvider, IStructuredItemContentProvider, ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource {

    /**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public  IServiceIDItemProvider(AdapterFactory adapterFactory) {
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
            addEcfServiceNamePropertyDescriptor(object);
            addServiceTypeIDPropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
	 * This adds a property descriptor for the Ecf Service Name feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    protected void addEcfServiceNamePropertyDescriptor(Object object) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(), getString(//$NON-NLS-1$
        "_UI_IServiceID_ecfServiceName_feature"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        getString("_UI_PropertyDescriptor_description", "_UI_IServiceID_ecfServiceName_feature", "_UI_IServiceID_type"), ModelPackage.Literals.ISERVICE_ID__ECF_SERVICE_NAME, false, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, getString(//$NON-NLS-1$
        "_UI_DiscoveryPropertyCategory"), null));
    }

    /**
	 * This adds a property descriptor for the Service Type ID feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    protected void addServiceTypeIDPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(), getString(//$NON-NLS-1$
        "_UI_IServiceID_serviceTypeID_feature"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        getString("_UI_PropertyDescriptor_description", "_UI_IServiceID_serviceTypeID_feature", "_UI_IServiceID_type"), ModelPackage.Literals.ISERVICE_ID__SERVICE_TYPE_ID, false, false, true, null, getString(//$NON-NLS-1$
        "_UI_DiscoveryPropertyCategory"), null));
    }

    /**
	 * This returns IServiceID.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public Object getImage(Object object) {
        //$NON-NLS-1$
        return overlayImage(object, getResourceLocator().getImage("full/obj16/IServiceID"));
    }

    /**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public String getText(Object object) {
        String label = ((IServiceID) object).getEcfServiceName();
        return label == null || label.length() == 0 ? //$NON-NLS-1$
        getString("_UI_IServiceID_type") : //$NON-NLS-1$ //$NON-NLS-2$
        getString("_UI_IServiceID_type") + " " + label;
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
        switch(notification.getFeatureID(IServiceID.class)) {
            case ModelPackage.ISERVICE_ID__ECF_SERVICE_ID:
            case ModelPackage.ISERVICE_ID__ECF_SERVICE_NAME:
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
