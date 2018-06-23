/*******************************************************************************
 *  Copyright (c) 2000, 2008 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.ui.wizards.extension;

import java.util.Locale;
import java.util.StringTokenizer;
import org.eclipse.core.runtime.*;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.PDEUIMessages;
import org.eclipse.pde.internal.ui.elements.ElementList;
import org.eclipse.pde.internal.ui.wizards.*;
import org.eclipse.swt.graphics.Image;

public class NewExtensionRegistryReader {

    //$NON-NLS-1$
    public static final String TAG_WIZARD = "wizard";

    //$NON-NLS-1$
    public static final String TAG_EDITOR_WIZARD = "editorWizard";

    //$NON-NLS-1$
    public static final String ATT_CATEGORY = "category";

    //$NON-NLS-1$
    public static final String ATT_SHORTCUTTABLE = "availableAsShortcut";

    //$NON-NLS-1$
    public static final String CATEGORY_SEPARATOR = "/";

    //$NON-NLS-1$
    public static final String TAG_CATEGORY = "category";

    //$NON-NLS-1$
    public static final String TAG_DESCRIPTION = "description";

    //$NON-NLS-1$
    private static final String UNCATEGORIZED_WIZARD_CATEGORY = "org.eclipse.pde.ui.Other";

    //$NON-NLS-1$
    private static final String UNCATEGORIZED_WIZARD_CATEGORY_LABEL = "Other";

    private boolean editorWizardMode;

    public  NewExtensionRegistryReader() {
        this(false);
    }

    public  NewExtensionRegistryReader(boolean editorWizardMode) {
        this.editorWizardMode = editorWizardMode;
    }

    protected WizardCollectionElement createCollectionElement(WizardCollectionElement parent, String id, String label) {
        WizardCollectionElement newElement = new WizardCollectionElement(id, label, parent);
        if (parent != null)
            parent.add(newElement);
        return newElement;
    }

    protected WizardElement createWizardElement(IConfigurationElement config) {
        String name = config.getAttribute(WizardElement.ATT_NAME);
        String id = config.getAttribute(WizardElement.ATT_ID);
        String className = config.getAttribute(WizardElement.ATT_CLASS);
        String template = config.getAttribute(WizardElement.ATT_TEMPLATE);
        if (name == null || id == null)
            return null;
        if (className == null && template == null)
            return null;
        WizardElement element = new WizardElement(config);
        String imageName = config.getAttribute(WizardElement.ATT_ICON);
        if (imageName != null) {
            String pluginID = config.getNamespaceIdentifier();
            Image image = PDEPlugin.getDefault().getLabelProvider().getImageFromPlugin(pluginID, imageName);
            element.setImage(image);
        }
        return element;
    }

    protected WizardElement createEditorWizardElement(IConfigurationElement config) {
        String name = config.getAttribute(WizardElement.ATT_NAME);
        String id = config.getAttribute(WizardElement.ATT_ID);
        String className = config.getAttribute(WizardElement.ATT_CLASS);
        String point = config.getAttribute(WizardElement.ATT_POINT);
        if (name == null || id == null || className == null)
            return null;
        if (point == null)
            return null;
        WizardElement element = new WizardElement(config);
        String imageName = config.getAttribute(WizardElement.ATT_ICON);
        if (imageName != null) {
            String pluginID = config.getNamespaceIdentifier();
            Image image = PDEPlugin.getDefault().getLabelProvider().getImageFromPlugin(pluginID, imageName);
            element.setImage(image);
        }
        return element;
    }

    protected String getCategoryStringFor(IConfigurationElement config) {
        String result = config.getAttribute(ATT_CATEGORY);
        if (result == null)
            result = UNCATEGORIZED_WIZARD_CATEGORY;
        return result;
    }

    protected WizardCollectionElement getChildWithID(WizardCollectionElement parent, String id) {
        Object[] children = parent.getChildren();
        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                WizardCollectionElement currentChild = (WizardCollectionElement) children[i];
                if (currentChild.getId().equals(id))
                    return currentChild;
            }
        }
        return null;
    }

    protected void insertUsingCategory(WizardElement element, ElementList result) {
        WizardCollectionElement currentResult = (WizardCollectionElement) result;
        StringTokenizer familyTokenizer = new StringTokenizer(getCategoryStringFor(element.getConfigurationElement()), CATEGORY_SEPARATOR);
        // use the period-separated sections of the current Wizard's category
        // to traverse through the NamedSolution "tree" that was previously
        // created
        // ie.-
        WizardCollectionElement currentCollectionElement = currentResult;
        // root
        boolean moveToOther = false;
        while (familyTokenizer.hasMoreElements()) {
            WizardCollectionElement tempCollectionElement = getChildWithID(currentCollectionElement, familyTokenizer.nextToken());
            if (// can't find the path; bump it
            tempCollectionElement == null) {
                // to uncategorized
                moveToOther = true;
                break;
            }
            currentCollectionElement = tempCollectionElement;
        }
        if (moveToOther)
            moveElementToUncategorizedCategory(currentResult, element);
        else
            currentCollectionElement.getWizards().add(element);
    }

    protected void moveElementToUncategorizedCategory(WizardCollectionElement root, WizardElement element) {
        WizardCollectionElement otherCategory = getChildWithID(root, UNCATEGORIZED_WIZARD_CATEGORY);
        if (otherCategory == null)
            otherCategory = createCollectionElement(root, UNCATEGORIZED_WIZARD_CATEGORY, UNCATEGORIZED_WIZARD_CATEGORY_LABEL);
        otherCategory.getWizards().add(element);
    }

    private void processCategory(IConfigurationElement config, ElementList list) {
        WizardCollectionElement result = (WizardCollectionElement) list;
        Category category = null;
        category = new Category(config);
        if (category.getID() == null || category.getLabel() == null) {
            System.out.println(PDEUIMessages.NewExtensionRegistryReader_missingProperty);
            return;
        }
        String[] categoryPath = category.getParentCategoryPath();
        // ie.- root
        WizardCollectionElement parent = result;
        if (categoryPath != null) {
            for (int i = 0; i < categoryPath.length; i++) {
                WizardCollectionElement tempElement = getChildWithID(parent, categoryPath[i]);
                if (tempElement == null) {
                    parent = null;
                    break;
                }
                parent = tempElement;
            }
        }
        if (parent != null)
            createCollectionElement(parent, category.getID(), category.getLabel());
    }

    protected void processElement(IConfigurationElement element, ElementList result, boolean shortcutsOnly) {
        String tag = element.getName();
        if (tag.equals(TAG_WIZARD) && !editorWizardMode) {
            WizardElement wizard = createWizardElement(element);
            if (shortcutsOnly) {
                String shortcut = element.getAttribute(ATT_SHORTCUTTABLE);
                if (//$NON-NLS-1$
                shortcut != null && shortcut.toLowerCase(Locale.ENGLISH).equals("true")) {
                    result.add(wizard);
                }
            } else
                insertUsingCategory(wizard, result);
        } else if (tag.equals(TAG_EDITOR_WIZARD) && editorWizardMode) {
            WizardElement wizard = createEditorWizardElement(element);
            if (shortcutsOnly) {
                result.add(wizard);
            } else
                insertUsingCategory(wizard, result);
        } else if (tag.equals(TAG_CATEGORY)) {
            if (shortcutsOnly == false) {
                processCategory(element, result);
            }
        }
    }

    public ElementList readRegistry(String pluginId, String pluginPointId, boolean shortcutsOnly) {
        ElementList result = //$NON-NLS-1$
        (shortcutsOnly) ? //$NON-NLS-1$
        (new ElementList("shortcuts")) : //$NON-NLS-1$ //$NON-NLS-2$
        (new WizardCollectionElement("root", "root", null));
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint point = registry.getExtensionPoint(pluginId, pluginPointId);
        if (point == null)
            return null;
        IExtension[] extensions = point.getExtensions();
        for (int i = 0; i < extensions.length; i++) {
            IConfigurationElement[] elements = extensions[i].getConfigurationElements();
            for (int j = 0; j < elements.length; j++) {
                IConfigurationElement config = elements[j];
                processElement(config, result, shortcutsOnly);
            }
        }
        return result;
    }
}
