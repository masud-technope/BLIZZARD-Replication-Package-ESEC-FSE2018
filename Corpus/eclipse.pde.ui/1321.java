/*******************************************************************************
 *  Copyright (c) 2005, 2008 IBM Corporation and others.
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
import org.eclipse.osgi.util.NLS;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.internal.core.PDECoreMessages;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class PluginBaseErrorReporter extends ExtensionsErrorReporter {

    public  PluginBaseErrorReporter(IFile file) {
        super(file);
    }

    @Override
    public void validateContent(IProgressMonitor monitor) {
        Element element = getDocumentRoot();
        if (element == null)
            return;
        String elementName = element.getNodeName();
        if (!getRootElementName().equals(elementName)) {
            reportIllegalElement(element, CompilerFlags.ERROR);
        } else {
            validateTopLevelAttributes(element);
            NodeList children = element.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                if (monitor.isCanceled())
                    break;
                Element child = (Element) children.item(i);
                String name = child.getNodeName();
                if (//$NON-NLS-1$
                name.equals("extension")) {
                    validateExtension(child);
                } else if (//$NON-NLS-1$
                name.equals("extension-point")) {
                    validateExtensionPoint(child);
                } else if (//$NON-NLS-1$
                name.equals("runtime")) {
                    validateRuntime(child);
                } else if (//$NON-NLS-1$
                name.equals("requires")) {
                    validateRequires(child);
                } else {
                    int severity = CompilerFlags.getFlag(fProject, CompilerFlags.P_UNKNOWN_ELEMENT);
                    if (severity != CompilerFlags.IGNORE)
                        reportIllegalElement(element, severity);
                }
            }
        }
    }

    protected void validateTopLevelAttributes(Element element) {
        if (//$NON-NLS-1$
        assertAttributeDefined(element, "id", CompilerFlags.ERROR)) {
            //$NON-NLS-1$
            validatePluginID(element, element.getAttributeNode("id"));
        }
        if (//$NON-NLS-1$
        assertAttributeDefined(element, "version", CompilerFlags.ERROR)) {
            //$NON-NLS-1$
            validateVersionAttribute(element, element.getAttributeNode("version"));
        }
        if (//$NON-NLS-1$
        assertAttributeDefined(element, "name", CompilerFlags.ERROR)) {
            //$NON-NLS-1$
            validateTranslatableString(element, element.getAttributeNode("name"), true);
        }
        //$NON-NLS-1$
        Attr attr = element.getAttributeNode("provider-name");
        if (attr != null)
            validateTranslatableString(element, attr, true);
    }

    protected abstract String getRootElementName();

    protected void validateRequires(Element element) {
        int severity = CompilerFlags.getFlag(fProject, CompilerFlags.P_UNKNOWN_ELEMENT);
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Element child = (Element) children.item(i);
            if (//$NON-NLS-1$
            child.getNodeName().equals("import")) {
                validateImport(child);
            } else if (severity != CompilerFlags.IGNORE) {
                reportIllegalElement(child, severity);
            }
        }
    }

    protected void validateImport(Element element) {
        if (//$NON-NLS-1$
        assertAttributeDefined(element, "plugin", CompilerFlags.ERROR)) {
            //$NON-NLS-1$
            validatePluginIDRef(element, element.getAttributeNode("plugin"));
        }
        //$NON-NLS-1$
        Attr attr = element.getAttributeNode("version");
        if (attr != null)
            validateVersionAttribute(element, attr);
        //$NON-NLS-1$
        attr = element.getAttributeNode("match");
        if (attr != null)
            validateMatch(element, attr);
        //$NON-NLS-1$
        attr = element.getAttributeNode("export");
        if (attr != null)
            validateBoolean(element, attr);
        //$NON-NLS-1$
        attr = element.getAttributeNode("optional");
        if (attr != null)
            validateBoolean(element, attr);
    }

    protected void validateRuntime(Element element) {
        int severity = CompilerFlags.getFlag(fProject, CompilerFlags.P_UNKNOWN_ELEMENT);
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Element child = (Element) children.item(i);
            if (//$NON-NLS-1$
            child.getNodeName().equals("library")) {
                validateLibrary(child);
            } else if (severity != CompilerFlags.IGNORE) {
                reportIllegalElement(child, severity);
            }
        }
    }

    protected void validateLibrary(Element element) {
        //$NON-NLS-1$
        assertAttributeDefined(element, "name", CompilerFlags.ERROR);
        int unknownSev = CompilerFlags.getFlag(fProject, CompilerFlags.P_UNKNOWN_ELEMENT);
        int deprecatedSev = CompilerFlags.getFlag(fProject, CompilerFlags.P_DEPRECATED);
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Element child = (Element) children.item(i);
            String elementName = child.getNodeName();
            if (//$NON-NLS-1$
            "export".equals(elementName)) {
                assertAttributeDefined(//$NON-NLS-1$
                child, //$NON-NLS-1$
                "name", //$NON-NLS-1$
                CompilerFlags.ERROR);
            } else if (//$NON-NLS-1$
            "packages".equals(elementName)) {
                if (deprecatedSev != CompilerFlags.IGNORE) {
                    reportDeprecatedElement(child, deprecatedSev);
                }
            } else if (unknownSev != CompilerFlags.IGNORE) {
                reportIllegalElement(child, unknownSev);
            }
        }
    }

    protected void validatePluginIDRef(Element element, Attr attr) {
        if (!validatePluginID(element, attr)) {
            return;
        }
        int severity = CompilerFlags.getFlag(fProject, CompilerFlags.P_UNRESOLVED_IMPORTS);
        if (//$NON-NLS-1$ //$NON-NLS-2$
        "true".equals(element.getAttribute("optional")) && severity == CompilerFlags.ERROR)
            severity = CompilerFlags.WARNING;
        if (severity != CompilerFlags.IGNORE) {
            IPluginModelBase model = PluginRegistry.findModel(attr.getValue());
            if (model == null || !model.isEnabled()) {
                report(NLS.bind(PDECoreMessages.Builders_Manifest_dependency, attr.getValue()), getLine(element, attr.getName()), severity, PDEMarkerFactory.CAT_FATAL);
            }
        }
    }

    private void reportDeprecatedElement(Element element, int severity) {
        report(NLS.bind(PDECoreMessages.Builders_Manifest_deprecated_3_0, element.getNodeName()), getLine(element), severity, PDEMarkerFactory.CAT_DEPRECATION);
    }
}
