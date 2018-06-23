/*******************************************************************************
 *  Copyright (c) 2005, 2013 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.core.builders;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.w3c.dom.*;

public class UpdateSiteErrorReporter extends ManifestErrorReporter {

    private IProgressMonitor fMonitor;

    public  UpdateSiteErrorReporter(IFile file) {
        super(file);
    }

    @Override
    public void validateContent(IProgressMonitor monitor) {
        fMonitor = monitor;
        Element root = getDocumentRoot();
        if (root == null)
            return;
        String elementName = root.getNodeName();
        if (//$NON-NLS-1$
        !"site".equals(elementName)) {
            reportIllegalElement(root, CompilerFlags.ERROR);
        } else {
            NamedNodeMap attributes = root.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Attr attr = (Attr) attributes.item(i);
                String name = attr.getName();
                if (//$NON-NLS-1$
                !name.equals("type") && //$NON-NLS-1$
                !name.equals("url") && !//$NON-NLS-1$
                name.equals(//$NON-NLS-1$
                "mirrorsURL") && !//$NON-NLS-1$
                name.equals(//$NON-NLS-1$
                "digestURL") && !//$NON-NLS-1$
                name.equals(//$NON-NLS-1$
                "pack200") && !//$NON-NLS-1$
                name.equals(//$NON-NLS-1$
                "availableLocales") && !name.equals("associateSitesURL")) {
                    reportUnknownAttribute(root, name, CompilerFlags.ERROR);
                }
            }
            validateDescription(root);
            validateFeatures(root);
            validateCategoryDefinitions(root);
            validateArchives(root);
        }
    }

    /**
	 * @param root
	 */
    private void validateArchives(Element root) {
        //$NON-NLS-1$
        NodeList list = getChildrenByName(root, "archive");
        for (int i = 0; i < list.getLength(); i++) {
            if (fMonitor.isCanceled())
                return;
            Element element = (Element) list.item(i);
            //$NON-NLS-1$
            assertAttributeDefined(element, "path", CompilerFlags.ERROR);
            //$NON-NLS-1$
            assertAttributeDefined(element, "url", CompilerFlags.ERROR);
            NamedNodeMap attributes = element.getAttributes();
            for (int j = 0; j < attributes.getLength(); j++) {
                Attr attr = (Attr) attributes.item(j);
                String name = attr.getName();
                if (//$NON-NLS-1$
                name.equals("url")) {
                    validateURL(//$NON-NLS-1$
                    element, //$NON-NLS-1$
                    "url");
                } else if (//$NON-NLS-1$
                !name.equals("path")) {
                    reportUnknownAttribute(element, name, CompilerFlags.ERROR);
                }
            }
        }
    }

    /**
	 * @param root
	 */
    private void validateCategoryDefinitions(Element root) {
        //$NON-NLS-1$
        NodeList list = getChildrenByName(root, "category-def");
        for (int i = 0; i < list.getLength(); i++) {
            if (fMonitor.isCanceled())
                return;
            Element element = (Element) list.item(i);
            //$NON-NLS-1$
            assertAttributeDefined(element, "name", CompilerFlags.ERROR);
            //$NON-NLS-1$
            assertAttributeDefined(element, "label", CompilerFlags.ERROR);
            NamedNodeMap attributes = element.getAttributes();
            for (int j = 0; j < attributes.getLength(); j++) {
                Attr attr = (Attr) attributes.item(j);
                String name = attr.getName();
                if (//$NON-NLS-1$ //$NON-NLS-2$
                !name.equals("name") && !name.equals("label")) {
                    reportUnknownAttribute(element, name, CompilerFlags.ERROR);
                }
            }
            validateDescription(element);
        }
    }

    /**
	 * @param root
	 */
    private void validateCategories(Element root) {
        //$NON-NLS-1$
        NodeList list = getChildrenByName(root, "category");
        for (int i = 0; i < list.getLength(); i++) {
            if (fMonitor.isCanceled())
                return;
            Element element = (Element) list.item(i);
            //$NON-NLS-1$
            assertAttributeDefined(element, "name", CompilerFlags.ERROR);
            NamedNodeMap attributes = element.getAttributes();
            for (int j = 0; j < attributes.getLength(); j++) {
                Attr attr = (Attr) attributes.item(j);
                String name = attr.getName();
                if (//$NON-NLS-1$
                !name.equals("name")) {
                    reportUnknownAttribute(element, name, CompilerFlags.ERROR);
                }
            }
        }
    }

    private void validateFeatures(Element parent) {
        //$NON-NLS-1$
        NodeList list = getChildrenByName(parent, "feature");
        for (int i = 0; i < list.getLength(); i++) {
            Element element = (Element) list.item(i);
            //$NON-NLS-1$
            assertAttributeDefined(element, "url", CompilerFlags.ERROR);
            NamedNodeMap attributes = element.getAttributes();
            for (int j = 0; j < attributes.getLength(); j++) {
                Attr attr = (Attr) attributes.item(j);
                String name = attr.getName();
                if (//$NON-NLS-1$
                name.equals("url")) {
                    validateURL(//$NON-NLS-1$
                    element, //$NON-NLS-1$
                    "url");
                } else if (//$NON-NLS-1$
                name.equals("patch")) {
                    validateBoolean(element, attr);
                } else if (//$NON-NLS-1$
                name.equals("version")) {
                    validateVersionAttribute(element, attr);
                } else if (//$NON-NLS-1$ //$NON-NLS-2$
                !name.equals("type") && !name.equals("id") && !name.equals("os") && //$NON-NLS-1$ //$NON-NLS-2$
                !name.equals("ws") && !name.equals("nl") && //$NON-NLS-1$ //$NON-NLS-2$
                !name.equals("arch")) {
                    reportUnknownAttribute(element, name, CompilerFlags.ERROR);
                }
            }
            validateCategories(element);
        }
    }

    /**
	 * @param parent
	 */
    private void validateDescription(Element parent) {
        //$NON-NLS-1$
        NodeList list = getChildrenByName(parent, "description");
        if (list.getLength() > 0) {
            if (fMonitor.isCanceled())
                return;
            Element element = (Element) list.item(0);
            validateElementWithContent((Element) list.item(0), true);
            if (//$NON-NLS-1$
            element.getAttributeNode("url") != null)
                validateURL(//$NON-NLS-1$
                element, //$NON-NLS-1$
                "url");
            reportExtraneousElements(list, 1);
        }
    }
}
