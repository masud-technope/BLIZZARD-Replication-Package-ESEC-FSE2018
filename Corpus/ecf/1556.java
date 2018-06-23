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
import org.eclipse.ecf.discovery.ui.model.IServiceInfo;
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
 * This is the item provider adapter for a {@link org.eclipse.ecf.discovery.ui.model.IServiceInfo} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class IServiceInfoItemProvider extends ItemProviderWithStatusLineAdapter implements IEditingDomainItemProvider, IStructuredItemContentProvider, ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource {

    /**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public  IServiceInfoItemProvider(AdapterFactory adapterFactory) {
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
            addEcfNamePropertyDescriptor(object);
            addEcfLocationPropertyDescriptor(object);
            addEcfPriorityPropertyDescriptor(object);
            addEcfWeightPropertyDescriptor(object);
            addServiceIDPropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
	 * This adds a property descriptor for the Ecf Name feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    protected void addEcfNamePropertyDescriptor(Object object) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(), getString(//$NON-NLS-1$
        "_UI_IServiceInfo_ecfName_feature"), getString(//$NON-NLS-1$
        "_UI_IServiceInfo_ecfName_description"), ModelPackage.Literals.ISERVICE_INFO__ECF_NAME, false, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, getString(//$NON-NLS-1$
        "_UI_DiscoveryPropertyCategory"), null));
    }

    /**
	 * This adds a property descriptor for the Ecf Location feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    protected void addEcfLocationPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(), getString(//$NON-NLS-1$
        "_UI_IServiceInfo_ecfLocation_feature"), getString(//$NON-NLS-1$
        "_UI_IServiceInfo_ecfLocation_description"), ModelPackage.Literals.ISERVICE_INFO__ECF_LOCATION, false, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, getString(//$NON-NLS-1$
        "_UI_DiscoveryPropertyCategory"), null));
    }

    /**
	 * This adds a property descriptor for the Ecf Priority feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    protected void addEcfPriorityPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(), getString(//$NON-NLS-1$
        "_UI_IServiceInfo_ecfPriority_feature"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        getString("_UI_PropertyDescriptor_description", "_UI_IServiceInfo_ecfPriority_feature", "_UI_IServiceInfo_type"), ModelPackage.Literals.ISERVICE_INFO__ECF_PRIORITY, false, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, getString(//$NON-NLS-1$
        "_UI_DiscoveryPropertyCategory"), null));
    }

    /**
	 * This adds a property descriptor for the Ecf Weight feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    protected void addEcfWeightPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(), getString(//$NON-NLS-1$
        "_UI_IServiceInfo_ecfWeight_feature"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        getString("_UI_PropertyDescriptor_description", "_UI_IServiceInfo_ecfWeight_feature", "_UI_IServiceInfo_type"), ModelPackage.Literals.ISERVICE_INFO__ECF_WEIGHT, false, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, getString(//$NON-NLS-1$
        "_UI_DiscoveryPropertyCategory"), null));
    }

    /**
	 * This adds a property descriptor for the Service ID feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    protected void addServiceIDPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(), getString(//$NON-NLS-1$
        "_UI_IServiceInfo_serviceID_feature"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        getString("_UI_PropertyDescriptor_description", "_UI_IServiceInfo_serviceID_feature", "_UI_IServiceInfo_type"), ModelPackage.Literals.ISERVICE_INFO__SERVICE_ID, false, false, true, null, getString(//$NON-NLS-1$
        "_UI_DiscoveryPropertyCategory"), null));
    }

    /**
	 * This returns IServiceInfo.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
    public Object getImage(Object object) {
        //$NON-NLS-1$
        return overlayImage(object, getResourceLocator().getImage("full/obj16/IServiceInfo"));
    }

    /**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
    public String getText(Object object) {
        IServiceInfo serviceInfo = ((IServiceInfo) object);
        String namingAuthority = serviceInfo.getServiceID().getServiceTypeID().getEcfNamingAuthority();
        List services = serviceInfo.getServiceID().getServiceTypeID().getEcfServices();
        List protocols = serviceInfo.getServiceID().getServiceTypeID().getEcfProtocols();
        return ((services.size() == 1) ? ((String) services.get(0)) : services.toString()) + " " + protocols + ":" + namingAuthority;
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
        switch(notification.getFeatureID(IServiceInfo.class)) {
            case ModelPackage.ISERVICE_INFO__ECF_SERVICE_INFO:
            case ModelPackage.ISERVICE_INFO__ECF_NAME:
            case ModelPackage.ISERVICE_INFO__ECF_LOCATION:
            case ModelPackage.ISERVICE_INFO__ECF_PRIORITY:
            case ModelPackage.ISERVICE_INFO__ECF_WEIGHT:
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
