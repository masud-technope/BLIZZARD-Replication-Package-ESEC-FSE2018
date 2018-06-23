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

import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ecf.internal.ui.Activator;

public abstract class RegistryReader {

    /**
	 * The constructor.
	 */
    protected  RegistryReader() {
    //
    }

    /**
	 * Logs the error in the workbench log using the provided text and the
	 * information in the configuration element.
	 */
    protected static void logError(IConfigurationElement element, String text) {
        IExtension extension = element.getDeclaringExtension();
        StringBuffer buf = new StringBuffer();
        //$NON-NLS-2$//$NON-NLS-1$
        buf.append("Plugin " + Activator.PLUGIN_ID + ", extension " + extension.getExtensionPointUniqueIdentifier());
        //$NON-NLS-1$
        buf.append("\n" + text);
        Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 5555, buf.toString(), null));
    }

    /**
	 * Logs a very common registry error when a required attribute is missing.
	 */
    protected static void logMissingAttribute(IConfigurationElement element, String attributeName) {
        //$NON-NLS-2$//$NON-NLS-1$
        logError(element, "Required attribute '" + attributeName + "' not defined");
    }

    /**
	 * Logs a very common registry error when a required child is missing.
	 */
    protected static void logMissingElement(IConfigurationElement element, String elementName) {
        //$NON-NLS-2$//$NON-NLS-1$
        logError(element, "Required sub element '" + elementName + "' not defined");
    }

    /**
	 * Logs a registry error when the configuration element is unknown.
	 */
    protected static void logUnknownElement(IConfigurationElement element) {
        //$NON-NLS-1$
        logError(element, "Unknown extension tag found: " + element.getName());
    }

    /**
	 * Apply a reproducable order to the list of extensions provided, such that
	 * the order will not change as extensions are added or removed.
	 * 
	 * @param extensions
	 *            the extensions to order
	 * @return ordered extensions
	 */
    public static IExtension[] orderExtensions(IExtension[] extensions) {
        // By default, the order is based on plugin id sorted
        // in ascending order. The order for a plugin providing
        // more than one extension for an extension point is
        // dependent in the order listed in the XML file.
        IExtension[] sortedExtension = new IExtension[extensions.length];
        System.arraycopy(extensions, 0, sortedExtension, 0, extensions.length);
        Comparator comparer = new Comparator() {

            public int compare(Object arg0, Object arg1) {
                String s1 = ((IExtension) arg0).getContributor().getName();
                String s2 = ((IExtension) arg1).getContributor().getName();
                return s1.compareToIgnoreCase(s2);
            }
        };
        Collections.sort(Arrays.asList(sortedExtension), comparer);
        return sortedExtension;
    }

    /**
	 * Implement this method to read element's attributes. If children should
	 * also be read, then implementor is responsible for calling
	 * <code>readElementChildren</code>. Implementor is also responsible for
	 * logging missing attributes.
	 * 
	 * @return true if element was recognized, false if not.
	 */
    protected abstract boolean readElement(IConfigurationElement element);

    /**
	 * Read the element's children. This is called by the subclass' readElement
	 * method when it wants to read the children of the element.
	 */
    protected void readElementChildren(IConfigurationElement element) {
        readElements(element.getChildren());
    }

    /**
	 * Read each element one at a time by calling the subclass implementation of
	 * <code>readElement</code>.
	 * 
	 * Logs an error if the element was not recognized.
	 */
    protected void readElements(IConfigurationElement[] elements) {
        for (int i = 0; i < elements.length; i++) {
            if (!readElement(elements[i])) {
                logUnknownElement(elements[i]);
            }
        }
    }

    /**
	 * Read one extension by looping through its configuration elements.
	 */
    protected void readExtension(IExtension extension) {
        readElements(extension.getConfigurationElements());
    }

    /**
	 * Start the registry reading process using the supplied plugin ID and
	 * extension point.
	 * 
	 * @param registry
	 *            the registry to read from
	 * @param pluginId
	 *            the plugin id of the extenion point
	 * @param extensionPoint
	 *            the extension point id
	 */
    public void readRegistry(IExtensionRegistry registry, String pluginId, String extensionPoint) {
        IExtensionPoint point = registry.getExtensionPoint(pluginId, extensionPoint);
        if (point == null)
            return;
        IExtension[] extensions = point.getExtensions();
        extensions = orderExtensions(extensions);
        for (int i = 0; i < extensions.length; i++) {
            readExtension(extensions[i]);
        }
    }

    /**
	 * Utility for extracting the description child of an element.
	 * 
	 * @param configElement
	 *            the element
	 * @return the description
	 * @since 3.1
	 */
    public static String getDescription(IConfigurationElement configElement) {
        IConfigurationElement[] children = configElement.getChildren(IWizardRegistryConstants.ATT_DESCRIPTION);
        if (children.length >= 1)
            return children[0].getValue();
        //$NON-NLS-1$
        return "";
    }

    /**
	 * Utility for extracting the value of a class attribute or a nested class
	 * element that follows the pattern set forth by
	 * {@link org.eclipse.core.runtime.IExecutableExtension}.
	 * 
	 * @param configElement
	 *            the element
	 * @param classAttributeName
	 *            the name of the class attribute to check
	 * @return the value of the attribute or nested class element
	 * @since 3.1
	 */
    public static String getClassValue(IConfigurationElement configElement, String classAttributeName) {
        String className = configElement.getAttribute(classAttributeName);
        if (className != null)
            return className;
        IConfigurationElement[] candidateChildren = configElement.getChildren(classAttributeName);
        if (candidateChildren.length == 0)
            return null;
        return candidateChildren[0].getAttribute(IWizardRegistryConstants.ATT_CLASS);
    }
}
