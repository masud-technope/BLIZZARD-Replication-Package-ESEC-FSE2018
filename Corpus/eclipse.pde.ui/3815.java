/*******************************************************************************
 * Copyright (c) 2005, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Simon Muschel <smuschel@gmx.de> - bug 260549
 *     Simon Scholz <simon.scholz@vogella.com> - Bug 444808
 *******************************************************************************/
package org.eclipse.pde.internal.core.builders;

import java.util.Arrays;
import java.util.HashSet;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.internal.core.*;
import org.eclipse.pde.internal.core.ibundle.IBundlePluginModel;
import org.eclipse.pde.internal.core.ibundle.IManifestHeader;
import org.eclipse.pde.internal.core.ifeature.IFeatureModel;
import org.eclipse.pde.internal.core.util.CoreUtility;
import org.eclipse.pde.internal.core.util.IdUtil;
import org.w3c.dom.*;

public class FeatureErrorReporter extends ManifestErrorReporter {

    static HashSet<String> attrs = new HashSet();

    static String[] attrNames = { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    "id", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    "version", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    "label", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    "provider-name", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    "image", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    "os", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    "ws", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    "arch", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    "nl", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    "colocation-affinity", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    "primary", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    "exclusive", "plugin", "application", "license-feature", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    "license-feature-version" };

    private IProgressMonitor fMonitor;

    public  FeatureErrorReporter(IFile file) {
        super(file);
        if (attrs.isEmpty())
            attrs.addAll(Arrays.asList(attrNames));
    }

    @Override
    public void validateContent(IProgressMonitor monitor) {
        fMonitor = monitor;
        Element element = getDocumentRoot();
        if (element == null)
            return;
        String elementName = element.getNodeName();
        if (//$NON-NLS-1$
        !"feature".equals(elementName)) {
            reportIllegalElement(element, CompilerFlags.ERROR);
        } else {
            validateFeatureAttributes(element);
            validateInstallHandler(element);
            validateDescription(element);
            validateLicense(element);
            validateCopyright(element);
            validateURLElement(element);
            validateIncludes(element);
            validateRequires(element);
            validatePlugins(element);
            validateData(element);
        }
    }

    private void validateData(Element parent) {
        //$NON-NLS-1$
        NodeList list = getChildrenByName(parent, "data");
        for (int i = 0; i < list.getLength(); i++) {
            if (fMonitor.isCanceled())
                return;
            Element data = (Element) list.item(i);
            //$NON-NLS-1$
            assertAttributeDefined(data, "id", CompilerFlags.ERROR);
            NamedNodeMap attributes = data.getAttributes();
            for (int j = 0; j < attributes.getLength(); j++) {
                Attr attr = (Attr) attributes.item(j);
                String name = attr.getName();
                if (//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                !name.equals("id") && !name.equals("os") && !name.equals("ws") && !name.equals("nl") && //$NON-NLS-1$ //$NON-NLS-2$
                !name.equals("arch") && !name.equals("download-size") && //$NON-NLS-1$ //$NON-NLS-2$
                !name.equals("install-size")) {
                    reportUnknownAttribute(data, name, CompilerFlags.ERROR);
                }
            }
        }
    }

    private void validatePlugins(Element parent) {
        //$NON-NLS-1$
        NodeList list = getChildrenByName(parent, "plugin");
        for (int i = 0; i < list.getLength(); i++) {
            if (fMonitor.isCanceled())
                return;
            Element plugin = (Element) list.item(i);
            //$NON-NLS-1$
            assertAttributeDefined(plugin, "id", CompilerFlags.ERROR);
            //$NON-NLS-1$
            assertAttributeDefined(plugin, "version", CompilerFlags.ERROR);
            NamedNodeMap attributes = plugin.getAttributes();
            //$NON-NLS-1$ //$NON-NLS-2$
            boolean isFragment = plugin.getAttribute("fragment").equals("true");
            for (int j = 0; j < attributes.getLength(); j++) {
                Attr attr = (Attr) attributes.item(j);
                String name = attr.getName();
                if (//$NON-NLS-1$
                name.equals("id")) {
                    validatePluginExists(plugin, attr, isFragment);
                } else if (//$NON-NLS-1$
                name.equals("version")) {
                    validateVersionAttribute(plugin, attr);
                    validateVersion(plugin, attr);
                } else if (//$NON-NLS-1$ //$NON-NLS-2$
                name.equals("fragment") || name.equals("unpack")) {
                    validateBoolean(plugin, attr);
                } else if (//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                !name.equals("os") && !name.equals("ws") && !name.equals("nl") && !name.equals("arch") && //$NON-NLS-1$ //$NON-NLS-2$
                !name.equals("download-size") && !name.equals("install-size") && //$NON-NLS-1$ //$NON-NLS-2$
                !name.equals("filter")) {
                    reportUnknownAttribute(plugin, name, CompilerFlags.ERROR);
                }
            }
            validateUnpack(plugin);
        }
    }

    private void validateRequires(Element parent) {
        //$NON-NLS-1$
        NodeList list = getChildrenByName(parent, "requires");
        if (list.getLength() > 0) {
            validateImports((Element) list.item(0));
            reportExtraneousElements(list, 1);
        }
    }

    private void validateImports(Element parent) {
        //$NON-NLS-1$
        NodeList list = getChildrenByName(parent, "import");
        for (int i = 0; i < list.getLength(); i++) {
            if (fMonitor.isCanceled())
                return;
            Element element = (Element) list.item(i);
            //$NON-NLS-1$
            Attr plugin = element.getAttributeNode("plugin");
            //$NON-NLS-1$
            Attr feature = element.getAttributeNode("feature");
            if (plugin == null && feature == null) {
                assertAttributeDefined(//$NON-NLS-1$
                element, //$NON-NLS-1$
                "plugin", //$NON-NLS-1$
                CompilerFlags.ERROR);
            } else if (plugin != null && feature != null) {
                //$NON-NLS-1$//$NON-NLS-2$
                reportExclusiveAttributes(element, "plugin", "feature", CompilerFlags.ERROR);
            } else if (plugin != null) {
                validatePluginExists(element, plugin, false);
            } else if (feature != null) {
                validateFeatureExists(element, feature);
            }
            NamedNodeMap attributes = element.getAttributes();
            for (int j = 0; j < attributes.getLength(); j++) {
                Attr attr = (Attr) attributes.item(j);
                String name = attr.getName();
                if (//$NON-NLS-1$
                name.equals("version")) {
                    validateVersionAttribute(element, attr);
                } else if (//$NON-NLS-1$
                name.equals("match")) {
                    if (//$NON-NLS-1$
                    element.getAttributeNode("patch") != //$NON-NLS-1$
                    null) {
                        report(NLS.bind(PDECoreMessages.Builders_Feature_patchedMatch, attr.getValue()), getLine(element, attr.getValue()), CompilerFlags.ERROR, PDEMarkerFactory.CAT_FATAL);
                    } else {
                        validateMatch(element, attr);
                    }
                } else if (//$NON-NLS-1$
                name.equals("patch")) {
                    if (//$NON-NLS-1$
                    "true".equalsIgnoreCase(attr.getValue()) && //$NON-NLS-1$
                    feature == null) {
                        report(NLS.bind(PDECoreMessages.Builders_Feature_patchPlugin, attr.getValue()), getLine(element, attr.getValue()), CompilerFlags.ERROR, PDEMarkerFactory.CAT_FATAL);
                    } else if (//$NON-NLS-1$ //$NON-NLS-2$
                    "true".equalsIgnoreCase(attr.getValue()) && element.getAttributeNode("version") == null) {
                        report(NLS.bind(PDECoreMessages.Builders_Feature_patchedVersion, attr.getValue()), getLine(element, attr.getValue()), CompilerFlags.ERROR, PDEMarkerFactory.CAT_FATAL);
                    } else {
                        validateBoolean(element, attr);
                    }
                } else if (//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                !name.equals("plugin") && !name.equals("feature") && !name.equals("filter")) {
                    reportUnknownAttribute(element, name, CompilerFlags.ERROR);
                }
            }
        }
    }

    private void validateIncludes(Element parent) {
        //$NON-NLS-1$
        NodeList list = getChildrenByName(parent, "includes");
        for (int i = 0; i < list.getLength(); i++) {
            if (fMonitor.isCanceled())
                return;
            Element include = (Element) list.item(i);
            if (//$NON-NLS-1$
            assertAttributeDefined(include, "id", CompilerFlags.ERROR) && assertAttributeDefined(//$NON-NLS-1$
            include, //$NON-NLS-1$
            "version", CompilerFlags.ERROR)) {
                validateFeatureExists(//$NON-NLS-1$
                include, //$NON-NLS-1$
                include.getAttributeNode("id"));
            }
            NamedNodeMap attributes = include.getAttributes();
            for (int j = 0; j < attributes.getLength(); j++) {
                Attr attr = (Attr) attributes.item(j);
                String name = attr.getName();
                if (//$NON-NLS-1$
                name.equals("version")) {
                    validateVersionAttribute(include, attr);
                } else if (//$NON-NLS-1$
                name.equals("optional")) {
                    validateBoolean(include, attr);
                } else if (//$NON-NLS-1$
                name.equals("search-location")) {
                    String value = include.getAttribute("search-location");
                    if (//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    !value.equals("root") && !value.equals("self") && !value.equals("both")) {
                        reportIllegalAttributeValue(include, attr);
                    }
                } else if (//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                !name.equals("id") && !name.equals("name") && !name.equals("os") && !name.equals("ws") && !name.equals("nl") && !name.equals("arch") && //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                !name.equals("filter")) {
                    reportUnknownAttribute(include, name, CompilerFlags.ERROR);
                }
            }
        }
    }

    private void validateURLElement(Element parent) {
        //$NON-NLS-1$
        NodeList list = getChildrenByName(parent, "url");
        if (list.getLength() > 0) {
            Element url = (Element) list.item(0);
            validateUpdateURL(url);
            validateDiscoveryURL(url);
            reportExtraneousElements(list, 1);
        }
    }

    private void validateUpdateURL(Element parent) {
        //$NON-NLS-1$
        NodeList list = getChildrenByName(parent, "update");
        if (list.getLength() > 0) {
            if (fMonitor.isCanceled())
                return;
            Element update = (Element) list.item(0);
            //$NON-NLS-1$
            assertAttributeDefined(update, "url", CompilerFlags.ERROR);
            NamedNodeMap attributes = update.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                String name = attributes.item(i).getNodeName();
                if (//$NON-NLS-1$
                name.equals("url")) {
                    validateURL(//$NON-NLS-1$
                    update, //$NON-NLS-1$
                    "url");
                } else if (//$NON-NLS-1$
                !name.equals("label")) {
                    reportUnknownAttribute(update, name, CompilerFlags.ERROR);
                }
            }
            reportExtraneousElements(list, 1);
        }
    }

    private void validateDiscoveryURL(Element parent) {
        //$NON-NLS-1$
        NodeList list = getChildrenByName(parent, "discovery");
        if (list.getLength() > 0) {
            if (fMonitor.isCanceled())
                return;
            Element discovery = (Element) list.item(0);
            //$NON-NLS-1$
            assertAttributeDefined(discovery, "url", CompilerFlags.ERROR);
            NamedNodeMap attributes = discovery.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                String name = attributes.item(i).getNodeName();
                if (//$NON-NLS-1$
                name.equals("url")) {
                    validateURL(//$NON-NLS-1$
                    discovery, //$NON-NLS-1$
                    "url");
                } else if (//$NON-NLS-1$
                name.equals("type")) {
                    String value = //$NON-NLS-1$
                    discovery.getAttribute(//$NON-NLS-1$
                    "type");
                    if (//$NON-NLS-1$ //$NON-NLS-2$
                    !value.equals("web") && !value.equals("update")) {
                        reportIllegalAttributeValue(discovery, (Attr) attributes.item(i));
                    }
                    reportDeprecatedAttribute(discovery, //$NON-NLS-1$
                    discovery.getAttributeNode(//$NON-NLS-1$
                    "type"));
                } else if (//$NON-NLS-1$
                !name.equals("label")) {
                    reportUnknownAttribute(discovery, name, CompilerFlags.ERROR);
                }
            }
        }
    }

    private void validateCopyright(Element parent) {
        //$NON-NLS-1$
        NodeList list = getChildrenByName(parent, "copyright");
        if (list.getLength() > 0) {
            if (fMonitor.isCanceled())
                return;
            Element element = (Element) list.item(0);
            validateElementWithContent((Element) list.item(0), true);
            NamedNodeMap attributes = element.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Attr attr = (Attr) attributes.item(i);
                String name = attr.getName();
                if (//$NON-NLS-1$
                name.equals("url")) {
                    validateURL(element, name);
                } else {
                    reportUnknownAttribute(element, name, CompilerFlags.ERROR);
                }
            }
            reportExtraneousElements(list, 1);
        }
    }

    private void validateLicense(Element parent) {
        //$NON-NLS-1$
        NodeList list = getChildrenByName(parent, "license");
        if (list.getLength() > 0) {
            if (fMonitor.isCanceled())
                return;
            Element element = (Element) list.item(0);
            validateElementWithContent((Element) list.item(0), true);
            NamedNodeMap attributes = element.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Attr attr = (Attr) attributes.item(i);
                String name = attr.getName();
                if (//$NON-NLS-1$
                name.equals("url")) {
                    validateURL(element, name);
                } else {
                    reportUnknownAttribute(element, name, CompilerFlags.ERROR);
                }
            }
            reportExtraneousElements(list, 1);
        }
    }

    private void validateDescription(Element parent) {
        //$NON-NLS-1$
        NodeList list = getChildrenByName(parent, "description");
        if (list.getLength() > 0) {
            if (fMonitor.isCanceled())
                return;
            Element element = (Element) list.item(0);
            validateElementWithContent((Element) list.item(0), true);
            NamedNodeMap attributes = element.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Attr attr = (Attr) attributes.item(i);
                String name = attr.getName();
                if (//$NON-NLS-1$
                name.equals("url")) {
                    validateURL(element, name);
                } else {
                    reportUnknownAttribute(element, name, CompilerFlags.ERROR);
                }
            }
            reportExtraneousElements(list, 1);
        }
    }

    private void validateInstallHandler(Element element) {
        //$NON-NLS-1$
        NodeList elements = getChildrenByName(element, "install-handler");
        if (elements.getLength() > 0) {
            if (fMonitor.isCanceled())
                return;
            Element handler = (Element) elements.item(0);
            NamedNodeMap attributes = handler.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                String name = attributes.item(i).getNodeName();
                if (//$NON-NLS-1$ //$NON-NLS-2$
                !name.equals("library") && !name.equals("handler"))
                    reportUnknownAttribute(handler, name, CompilerFlags.ERROR);
            }
            reportExtraneousElements(elements, 1);
        }
    }

    private void validateFeatureAttributes(Element element) {
        if (fMonitor.isCanceled())
            return;
        //$NON-NLS-1$
        assertAttributeDefined(element, "id", CompilerFlags.ERROR);
        //$NON-NLS-1$
        assertAttributeDefined(element, "version", CompilerFlags.ERROR);
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            String name = attributes.item(i).getNodeName();
            if (!attrs.contains(name)) {
                reportUnknownAttribute(element, name, CompilerFlags.ERROR);
            } else if (//$NON-NLS-1$
            name.equals("id")) {
                validateFeatureID(element, (Attr) attributes.item(i));
            } else if (//$NON-NLS-1$ //$NON-NLS-2$
            name.equals("primary") || name.equals("exclusive")) {
                validateBoolean(element, (Attr) attributes.item(i));
            } else if (//$NON-NLS-1$
            name.equals("version")) {
                validateVersionAttribute(element, (Attr) attributes.item(i));
            }
            if (//$NON-NLS-1$
            name.equals("primary")) {
                reportDeprecatedAttribute(element, (Attr) attributes.item(i));
            } else if (//$NON-NLS-1$
            name.equals("plugin")) {
                validatePluginExists(element, (Attr) attributes.item(i), false);
            }
        }
    }

    /**
	 * Checks whether the given attribute value is a valid feature ID.  If it is not valid, a marker
	 * is created on the element and <code>false</code> is returned. If valid, <code>true</code> is
	 * returned.  Also see {@link #validatePluginID(Element, Attr)}
	 *
	 * @param element element to add the marker to if invalid
	 * @param attr the attribute to check the value of
	 * @return whether the given attribute value is a valid feature ID.
	 */
    protected boolean validateFeatureID(Element element, Attr attr) {
        if (!IdUtil.isValidCompositeID(attr.getValue())) {
            String message = NLS.bind(PDECoreMessages.Builders_Manifest_compositeID, attr.getValue(), attr.getName());
            report(message, getLine(element, attr.getName()), CompilerFlags.WARNING, PDEMarkerFactory.CAT_OTHER);
            return false;
        }
        return true;
    }

    private void validatePluginExists(Element element, Attr attr, boolean isFragment) {
        String id = attr.getValue();
        int severity = CompilerFlags.getFlag(fProject, CompilerFlags.F_UNRESOLVED_PLUGINS);
        if (severity != CompilerFlags.IGNORE) {
            IPluginModelBase model = PluginRegistry.findModel(id);
            if (model == null || !model.isEnabled() || (isFragment && !model.isFragmentModel()) || (!isFragment && model.isFragmentModel())) {
                report(NLS.bind(PDECoreMessages.Builders_Feature_reference, id), getLine(element, attr.getName()), severity, PDEMarkerFactory.CAT_OTHER);
            }
        }
    }

    private void validateFeatureExists(Element element, Attr attr) {
        int severity = CompilerFlags.getFlag(fProject, CompilerFlags.F_UNRESOLVED_FEATURES);
        if (severity != CompilerFlags.IGNORE) {
            IFeatureModel[] models = PDECore.getDefault().getFeatureModelManager().findFeatureModels(attr.getValue());
            if (models.length == 0) {
                report(NLS.bind(PDECoreMessages.Builders_Feature_freference, attr.getValue()), getLine(element, attr.getName()), severity, PDEMarkerFactory.CAT_OTHER);
            }
        }
    }

    protected void reportExclusiveAttributes(Element element, String attName1, String attName2, int severity) {
        String message = NLS.bind(PDECoreMessages.Builders_Feature_exclusiveAttributes, (new String[] { attName1, attName2 }));
        report(message, getLine(element, attName2), severity, PDEMarkerFactory.CAT_OTHER);
    }

    private void validateUnpack(Element parent) {
        int severity = CompilerFlags.getFlag(fProject, CompilerFlags.F_UNRESOLVED_PLUGINS);
        if (severity == CompilerFlags.IGNORE) {
            return;
        }
        if (severity == CompilerFlags.ERROR) {
            // this might not be an error, so max the flag at WARNING level.
            severity = CompilerFlags.WARNING;
        }
        //$NON-NLS-1$
        String unpack = parent.getAttribute("unpack");
        //$NON-NLS-1$
        IPluginModelBase pModel = PluginRegistry.findModel(parent.getAttribute("id"));
        if (pModel == null) {
            return;
        }
        if (pModel instanceof IBundlePluginModel) {
            IBundlePluginModel bModel = (IBundlePluginModel) pModel;
            IManifestHeader header = bModel.getBundleModel().getBundle().getManifestHeader(ICoreConstants.ECLIPSE_BUNDLE_SHAPE);
            if (header != null) {
                String value = header.getValue();
                //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                String unpackValue = "true".equals(unpack) ? "jar" : "dir";
                if (value != null && !value.equalsIgnoreCase(unpackValue)) {
                    //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    String message = NLS.bind(PDECoreMessages.Builders_Feature_mismatchUnpackBundleShape, (new String[] { "unpack=" + unpack, parent.getAttribute("id"), "Eclipse-BundleShape: " + value }));
                    report(message, getLine(parent), severity, PDEMarkerFactory.CAT_OTHER);
                }
            }
        }
        if (//$NON-NLS-1$
        "true".equals(unpack) && !CoreUtility.guessUnpack(pModel.getBundleDescription())) {
            //$NON-NLS-1$ //$NON-NLS-2$
            String message = NLS.bind(PDECoreMessages.Builders_Feature_missingUnpackFalse, (new String[] { parent.getAttribute("id"), "unpack=\"false\"" }));
            report(message, getLine(parent), severity, PDEMarkerFactory.CAT_OTHER);
        }
    }

    /**
	 * Validates that the version of the given plug-in is available in the registry.  Adds a
	 * warning if the plug-in could not be found.
	 *
	 * @param plugin xml element describing the plug-in to look for in the registry
	 * @param attr set of element attributes
	 */
    private void validateVersion(Element plugin, Attr attr) {
        //$NON-NLS-1$
        String id = plugin.getAttribute("id");
        //$NON-NLS-1$
        String version = plugin.getAttribute("version");
        if (id.trim().length() == 0 || version.trim().length() == 0 || version.equals(ICoreConstants.DEFAULT_VERSION))
            return;
        ModelEntry entry = PluginRegistry.findEntry(id);
        if (entry != null) {
            IPluginModelBase[] allModels = entry.getActiveModels();
            for (int i = 0; i < allModels.length; i++) {
                IPluginModelBase availablePlugin = allModels[i];
                if (id.equals(availablePlugin.getPluginBase().getId())) {
                    if (version.equals(availablePlugin.getPluginBase().getVersion())) {
                        return;
                    }
                }
            }
        }
        report(NLS.bind(PDECoreMessages.Builders_Feature_mismatchPluginVersion, new String[] { version, id }), getLine(plugin, attr.getName()), CompilerFlags.WARNING, PDEMarkerFactory.CAT_OTHER);
    }
}
