/*******************************************************************************
 *  Copyright (c) 2000, 2016 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Carver - STAR - bug 213255
 *******************************************************************************/
package org.eclipse.pde.internal.core.schema;

import java.io.*;
import java.net.*;
import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.pde.core.*;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.internal.core.*;
import org.eclipse.pde.internal.core.ischema.*;
import org.eclipse.pde.internal.core.util.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class Schema extends PlatformObject implements ISchema {

    private URL fURL;

    private ListenerList<IModelChangedListener> fListeners = new ListenerList();

    private Vector<ISchemaElement> fElements = new Vector();

    private Vector<DocumentSection> fDocSections = new Vector();

    private Vector<ISchemaInclude> fIncludes;

    private String fPointID;

    private String fPluginID;

    private ISchemaDescriptor fSchemaDescriptor;

    private boolean fLoaded;

    private Vector<SchemaElementReference> fReferences;

    private String fDescription;

    private double fTargetVersion;

    //$NON-NLS-1$
    private String fName = "";

    private boolean fNotificationEnabled;

    //$NON-NLS-1$
    public static final String INDENT = "   ";

    private boolean fDisposed;

    private boolean fValid;

    private boolean fAbbreviated;

    private List<IPath> fSearchPath;

    public  Schema(String pluginId, String pointId, String name, boolean abbreviated) {
        fPluginID = pluginId;
        fPointID = pointId;
        fName = name;
        fAbbreviated = abbreviated;
    }

    public  Schema(ISchemaDescriptor schemaDescriptor, URL url, boolean abbreviated) {
        fSchemaDescriptor = schemaDescriptor;
        fURL = url;
        fAbbreviated = abbreviated;
    }

    public void addDocumentSection(DocumentSection docSection) {
        fDocSections.add(docSection);
        fireModelChanged(new ModelChangedEvent(this, IModelChangedEvent.INSERT, new Object[] { docSection }, null));
    }

    public void addElement(ISchemaElement element) {
        addElement(element, null);
    }

    public void addElement(ISchemaElement element, ISchemaElement afterElement) {
        int index = -1;
        if (afterElement != null) {
            index = fElements.indexOf(afterElement);
        }
        if (index != -1)
            fElements.add(index + 1, element);
        else
            fElements.add(element);
        fireModelChanged(new ModelChangedEvent(this, IModelChangedEvent.INSERT, new Object[] { element }, null));
    }

    public void addInclude(ISchemaInclude include) {
        if (fIncludes == null)
            fIncludes = new Vector();
        fIncludes.add(include);
        fireModelChanged(new ModelChangedEvent(this, IModelChangedEvent.INSERT, new Object[] { include }, null));
    }

    public void removeInclude(ISchemaInclude include) {
        if (fIncludes == null)
            return;
        fIncludes.remove(include);
        fireModelChanged(new ModelChangedEvent(this, IModelChangedEvent.REMOVE, new Object[] { include }, null));
    }

    @Override
    public void addModelChangedListener(IModelChangedListener listener) {
        fListeners.add(listener);
    }

    private void collectElements(ISchemaCompositor compositor, Vector<Object> result) {
        Object[] children = compositor.getChildren();
        for (int i = 0; i < children.length; i++) {
            Object child = children[i];
            if (child instanceof ISchemaCompositor)
                collectElements((ISchemaCompositor) child, result);
            else if (child instanceof ISchemaObjectReference) {
                ISchemaObjectReference ref = (ISchemaObjectReference) child;
                Object referenced = ref.getReferencedObject();
                if (referenced instanceof ISchemaElement)
                    result.addElement(referenced);
            }
        }
    }

    @Override
    public void dispose() {
        if (fIncludes != null) {
            for (int i = 0; i < fIncludes.size(); i++) {
                ISchemaInclude include = fIncludes.get(i);
                include.dispose();
            }
        }
        reset();
        fDisposed = true;
    }

    @Override
    public ISchemaElement findElement(String name) {
        if (!isLoaded())
            load();
        for (int i = 0; i < fElements.size(); i++) {
            ISchemaElement element = fElements.get(i);
            if (element.getName().equals(name))
                return element;
        }
        if (fIncludes != null) {
            for (int i = 0; i < fIncludes.size(); i++) {
                ISchemaInclude include = fIncludes.get(i);
                ISchema ischema = include.getIncludedSchema();
                if (ischema == null)
                    continue;
                ISchemaElement element = ischema.findElement(name);
                if (element != null)
                    return element;
            }
        }
        return null;
    }

    @Override
    public void fireModelChanged(IModelChangedEvent event) {
        if (!fNotificationEnabled)
            return;
        for (IModelChangedListener listener : fListeners) {
            listener.modelChanged(event);
        }
    }

    @Override
    public void fireModelObjectChanged(Object object, String property, Object oldValue, Object newValue) {
        fireModelChanged(new ModelChangedEvent(this, object, property, oldValue, newValue));
    }

    private String getAttribute(Node node, String name) {
        NamedNodeMap map = node.getAttributes();
        Node attNode = map.getNamedItem(name);
        if (attNode != null) {
            String value = attNode.getNodeValue();
            if (value.length() > 0)
                return value;
        }
        return null;
    }

    @Override
    public ISchemaElement[] getCandidateChildren(ISchemaElement element) {
        Vector<Object> candidates = new Vector();
        ISchemaType type = element.getType();
        if (type instanceof ISchemaComplexType) {
            ISchemaCompositor compositor = ((ISchemaComplexType) type).getCompositor();
            if (compositor != null)
                collectElements(compositor, candidates);
        }
        ISchemaElement[] result = new ISchemaElement[candidates.size()];
        candidates.copyInto(result);
        return result;
    }

    @Override
    public String getDescription() {
        return fDescription;
    }

    @Override
    public boolean isValid() {
        return fValid;
    }

    @Override
    public IDocumentSection[] getDocumentSections() {
        return fDocSections.toArray(new IDocumentSection[fDocSections.size()]);
    }

    @Override
    public int getElementCount() {
        return fElements.size();
    }

    @Override
    public int getResolvedElementCount() {
        int localCount = getElementCount();
        if (fIncludes == null)
            return localCount;
        int totalCount = localCount;
        for (int i = 0; i < fIncludes.size(); i++) {
            ISchemaInclude include = fIncludes.get(i);
            ISchema schema = include.getIncludedSchema();
            if (schema == null)
                continue;
            totalCount += schema.getResolvedElementCount();
        }
        return totalCount;
    }

    @Override
    public ISchemaElement[] getElements() {
        if (!isLoaded())
            load();
        return fElements.toArray(new ISchemaElement[fElements.size()]);
    }

    @Override
    public String[] getElementNames() {
        ISchemaElement[] elements = getElements();
        String[] names = new String[elements.length];
        for (int i = 0; i < elements.length; i++) names[i] = elements[i].getName();
        return names;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ISchemaElement[] getResolvedElements() {
        if (fIncludes == null)
            return getElements();
        if (!isLoaded())
            load();
        @SuppressWarnings("rawtypes") Vector result = (Vector) fElements.clone();
        for (int i = 0; i < fIncludes.size(); i++) {
            ISchemaInclude include = fIncludes.get(i);
            ISchema schema = include.getIncludedSchema();
            if (schema == null)
                continue;
            ISchemaElement[] ielements = schema.getElements();
            for (int j = 0; j < ielements.length; j++) result.add(ielements[j]);
        }
        return (ISchemaElement[]) result.toArray(new ISchemaElement[result.size()]);
    }

    @Override
    public ISchemaInclude[] getIncludes() {
        if (fIncludes == null)
            return new ISchemaInclude[0];
        return fIncludes.toArray(new ISchemaInclude[fIncludes.size()]);
    }

    @Override
    public String getName() {
        return fName;
    }

    private String getNormalizedText(String source) {
        if (source == null)
            //$NON-NLS-1$
            return "";
        String result = source.replace('\t', ' ');
        result = result.trim();
        return result;
    }

    @Override
    public ISchemaObject getParent() {
        return null;
    }

    @Override
    public void setParent(ISchemaObject obj) {
    }

    public ISchemaElement getElementAt(int index) {
        return fElements.get(index);
    }

    @Override
    public String getQualifiedPointId() {
        // Check if the extension point ID is already fully qualified
        if (fPointID.indexOf('.') >= 0) {
            return fPointID;
        }
        //$NON-NLS-1$
        return fPluginID + "." + fPointID;
    }

    @Override
    public String getPluginId() {
        return fPluginID;
    }

    @Override
    public String getPointId() {
        return fPointID;
    }

    @Override
    public ISchema getSchema() {
        return this;
    }

    @Override
    public ISchemaDescriptor getSchemaDescriptor() {
        return fSchemaDescriptor;
    }

    @Override
    public URL getURL() {
        return fURL;
    }

    public int indexOf(Object obj) {
        return fElements.indexOf(obj);
    }

    @Override
    public boolean isDisposed() {
        return fDisposed;
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    public boolean isLoaded() {
        return fLoaded;
    }

    public boolean isNotificationEnabled() {
        return fNotificationEnabled;
    }

    public void load() {
        URLConnection connection = null;
        InputStream input = null;
        try {
            connection = SchemaUtil.getURLConnection(fURL);
            input = connection.getInputStream();
            load(input);
        } catch (FileNotFoundException e) {
            fLoaded = false;
        } catch (IOException e) {
            PDECore.logException(e);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (connection instanceof JarURLConnection) {
                    ((JarURLConnection) connection).getJarFile().close();
                }
            } catch (IOException e1) {
            }
        }
    }

    public void load(InputStream stream) {
        try {
            SAXParserWrapper parser = new SAXParserWrapper();
            XMLDefaultHandler handler = new XMLDefaultHandler(fAbbreviated);
            parser.parse(stream, handler);
            traverseDocumentTree(handler.getDocumentElement());
        } catch (SAXException e) {
        } catch (IOException e) {
            PDECore.logException(e, "IOException reading following URL: " + fURL);
        } catch (Exception e) {
            PDECore.logException(e);
        }
    }

    private ISchemaAttribute processAttribute(ISchemaElement element, Node elementNode) {
        //$NON-NLS-1$
        String aname = getAttribute(elementNode, "name");
        if (aname == null)
            return null;
        //$NON-NLS-1$
        String atype = getAttribute(elementNode, "type");
        //$NON-NLS-1$
        String ause = getAttribute(elementNode, "use");
        //$NON-NLS-1$
        String avalue = getAttribute(elementNode, "value");
        ISchemaSimpleType type = null;
        if (atype != null) {
            type = (ISchemaSimpleType) resolveTypeReference(atype);
        }
        SchemaAttribute attribute = new SchemaAttribute(element, aname);
        //attribute.bindSourceLocation(elementNode, lineTable);
        if (ause != null) {
            int use = ISchemaAttribute.OPTIONAL;
            if (//$NON-NLS-1$
            ause.equals("required"))
                use = ISchemaAttribute.REQUIRED;
            else if (//$NON-NLS-1$
            ause.equals("optional"))
                use = ISchemaAttribute.OPTIONAL;
            else if (//$NON-NLS-1$
            ause.equals("default"))
                use = ISchemaAttribute.DEFAULT;
            attribute.setUse(use);
        }
        if (avalue != null)
            attribute.setValue(avalue);
        NodeList children = elementNode.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String tag = child.getNodeName();
                if (//$NON-NLS-1$
                tag.equals("annotation")) {
                    processAttributeAnnotation(attribute, child);
                } else if (//$NON-NLS-1$
                tag.equals("simpleType")) {
                    processAttributeSimpleType(attribute, child);
                }
            }
        }
        if (type != null && attribute.getType() == null)
            attribute.setType(type);
        return attribute;
    }

    private void processAttributeAnnotation(SchemaAttribute element, Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (//$NON-NLS-1$
                child.getNodeName().equals("documentation")) {
                    Node doc = child.getFirstChild();
                    if (doc != null)
                        element.setDescription(getNormalizedText(doc.getNodeValue()));
                } else if (//$NON-NLS-1$ //$NON-NLS-2$
                child.getNodeName().equals("appInfo") || child.getNodeName().equals("appinfo")) {
                    NodeList infos = child.getChildNodes();
                    for (int j = 0; j < infos.getLength(); j++) {
                        Node meta = infos.item(j);
                        if (meta.getNodeType() == Node.ELEMENT_NODE) {
                            if (//$NON-NLS-1$
                            meta.getNodeName().equals(//$NON-NLS-1$
                            "meta.attribute")) {
                                element.setKind(processKind(//$NON-NLS-1$
                                getAttribute(meta, "kind")));
                                element.setBasedOn(getAttribute(//$NON-NLS-1$
                                meta, //$NON-NLS-1$
                                "basedOn"));
                                element.setTranslatableProperty(processTranslatable(getAttribute(//$NON-NLS-1$
                                meta, //$NON-NLS-1$
                                "translatable")));
                                element.setDeprecatedProperty(processDeprecated(getAttribute(//$NON-NLS-1$
                                meta, //$NON-NLS-1$
                                "deprecated")));
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean processTranslatable(String value) {
        //$NON-NLS-1$
        return (value != null && "true".equals(value));
    }

    private boolean processDeprecated(String value) {
        //$NON-NLS-1$
        return value != null && "true".equals(value);
    }

    private SchemaSimpleType processAttributeRestriction(SchemaAttribute attribute, Node node) {
        NodeList children = node.getChildNodes();
        if (children.getLength() == 0)
            return null;
        //$NON-NLS-1$
        String baseName = getAttribute(node, "base");
        if (//$NON-NLS-1$
        baseName.equals("string") == false) {
            //$NON-NLS-1$
            return new SchemaSimpleType(attribute.getSchema(), "string");
        }
        SchemaSimpleType type = new SchemaSimpleType(attribute.getSchema(), baseName);
        Vector<ISchemaEnumeration> items = new Vector();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (//$NON-NLS-1$
                child.getNodeName().equals("enumeration")) {
                    ISchemaEnumeration enumeration = processEnumeration(attribute.getSchema(), child);
                    if (enumeration != null)
                        items.add(enumeration);
                }
            }
        }
        ChoiceRestriction restriction = new ChoiceRestriction(attribute.getSchema());
        restriction.setChildren(items);
        type.setRestriction(restriction);
        return type;
    }

    private void processAttributeSimpleType(SchemaAttribute attribute, Node node) {
        NodeList children = node.getChildNodes();
        if (children.getLength() == 0)
            return;
        SchemaSimpleType type = null;
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (//$NON-NLS-1$
                child.getNodeName().equals("restriction")) {
                    type = processAttributeRestriction(attribute, child);
                }
            }
        }
        if (type != null)
            attribute.setType(type);
    }

    private SchemaComplexType processComplexType(ISchemaElement owner, Node typeNode) {
        //$NON-NLS-1$
        String aname = getAttribute(typeNode, "name");
        //$NON-NLS-1$
        String amixed = getAttribute(typeNode, "mixed");
        SchemaComplexType complexType = new SchemaComplexType(this, aname);
        if (//$NON-NLS-1$
        amixed != null && amixed.equals("true"))
            complexType.setMixed(true);
        NodeList children = typeNode.getChildNodes();
        ISchemaCompositor compositor = null;
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (//$NON-NLS-1$
                child.getNodeName().equals("attribute")) {
                    complexType.addAttribute(processAttribute(owner, child));
                } else {
                    ISchemaObject object = processCompositorChild(owner, child, ISchemaCompositor.ROOT);
                    if (object instanceof ISchemaCompositor && compositor == null) {
                        compositor = (ISchemaCompositor) object;
                    }
                }
            }
        }
        complexType.setCompositor(compositor);
        return complexType;
    }

    private ISchemaCompositor processCompositor(ISchemaObject parent, Node node, int type) {
        SchemaCompositor compositor = new SchemaCompositor(parent, type);
        NodeList children = node.getChildNodes();
        int minOccurs = 1;
        int maxOccurs = 1;
        //$NON-NLS-1$
        String aminOccurs = getAttribute(node, "minOccurs");
        //$NON-NLS-1$
        String amaxOccurs = getAttribute(node, "maxOccurs");
        if (aminOccurs != null)
            minOccurs = Integer.valueOf(aminOccurs).intValue();
        if (amaxOccurs != null) {
            if (//$NON-NLS-1$
            amaxOccurs.equals("unbounded"))
                maxOccurs = Integer.MAX_VALUE;
            else {
                maxOccurs = Integer.valueOf(amaxOccurs).intValue();
            }
        }
        compositor.setMinOccurs(minOccurs);
        compositor.setMaxOccurs(maxOccurs);
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            ISchemaObject object = processCompositorChild(compositor, child, type);
            if (object != null)
                compositor.addChild(object);
        }
        return compositor;
    }

    private ISchemaObject processCompositorChild(ISchemaObject parent, Node child, int parentKind) {
        String tag = child.getNodeName();
        if (//$NON-NLS-1$
        tag.equals("element") && parentKind != ISchemaCompositor.ROOT) {
            return processElement(parent, child);
        }
        // sequence: element | group | choice | sequence
        if (//$NON-NLS-1$
        tag.equals("sequence") && parentKind != ISchemaCompositor.ALL) {
            return processCompositor(parent, child, ISchemaCompositor.SEQUENCE);
        }
        // choice: element | group | choice | sequence
        if (//$NON-NLS-1$
        tag.equals("choice") && parentKind != ISchemaCompositor.ALL) {
            return processCompositor(parent, child, ISchemaCompositor.CHOICE);
        }
        // all: element
        if (//$NON-NLS-1$
        tag.equals("all") && (parentKind == ISchemaCompositor.ROOT || parentKind == ISchemaCompositor.GROUP)) {
            return processCompositor(parent, child, ISchemaCompositor.SEQUENCE);
        }
        // group: all | choice | sequence
        if (//$NON-NLS-1$
        tag.equals("group") && (parentKind == ISchemaCompositor.CHOICE || parentKind == ISchemaCompositor.SEQUENCE)) {
            return processCompositor(parent, child, ISchemaCompositor.SEQUENCE);
        }
        return null;
    }

    private ISchemaElement processElement(ISchemaObject parent, Node elementNode) {
        if (parent instanceof ISchemaCompositor)
            return processElementReference((ISchemaCompositor) parent, elementNode);
        return processElementDeclaration(parent, elementNode);
    }

    private ISchemaElement processElementDeclaration(ISchemaObject parent, Node elementNode) {
        //$NON-NLS-1$
        String aname = getAttribute(elementNode, "name");
        if (aname == null)
            return null;
        //$NON-NLS-1$
        String atype = getAttribute(elementNode, "type");
        int minOccurs = getMinOccurs(elementNode);
        int maxOccurs = getMaxOccurs(elementNode);
        ISchemaType type = null;
        if (atype != null) {
            type = resolveTypeReference(atype);
        }
        SchemaElement element;
        if (//$NON-NLS-1$
        aname.equals("extension"))
            element = new SchemaRootElement(parent, aname);
        else
            element = new SchemaElement(parent, aname);
        //element.bindSourceLocation(elementNode, lineTable);
        element.setMinOccurs(minOccurs);
        element.setMaxOccurs(maxOccurs);
        NodeList children = elementNode.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String tag = child.getNodeName();
                if (//$NON-NLS-1$
                type == null && tag.equals("complexType")) {
                    type = processComplexType(element, child);
                }
                if (//$NON-NLS-1$
                tag.equals("annotation")) {
                    processElementAnnotation(element, child);
                }
            }
        }
        element.setType(type);
        return element;
    }

    private ISchemaElement processElementReference(ISchemaCompositor compositor, Node elementNode) {
        //$NON-NLS-1$
        String aref = getAttribute(elementNode, "ref");
        if (aref == null) {
            return null;
        }
        int minOccurs = getMinOccurs(elementNode);
        int maxOccurs = getMaxOccurs(elementNode);
        SchemaElementReference reference = new SchemaElementReference(compositor, aref);
        reference.addComments(elementNode);
        reference.setMinOccurs(minOccurs);
        reference.setMaxOccurs(maxOccurs);
        fReferences.addElement(reference);
        //reference.bindSourceLocation(elementNode, lineTable);
        return reference;
    }

    private int getMinOccurs(Node elementNode) {
        //$NON-NLS-1$
        String aminOccurs = getAttribute(elementNode, "minOccurs");
        if (aminOccurs != null)
            return Integer.valueOf(aminOccurs).intValue();
        return 1;
    }

    private int getMaxOccurs(Node elementNode) {
        //$NON-NLS-1$
        String amaxOccurs = getAttribute(elementNode, "maxOccurs");
        if (amaxOccurs != null) {
            if (//$NON-NLS-1$
            amaxOccurs.equals("unbounded"))
                return Integer.MAX_VALUE;
            return Integer.valueOf(amaxOccurs).intValue();
        }
        return 1;
    }

    private void processElementAnnotation(SchemaElement element, Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (//$NON-NLS-1$
                child.getNodeName().equals("documentation") && !fAbbreviated) {
                    element.setDescription(getNormalizedText(child.getFirstChild().getNodeValue()));
                } else if (//$NON-NLS-1$ //$NON-NLS-2$
                child.getNodeName().equals("appInfo") || child.getNodeName().equals("appinfo")) {
                    NodeList infos = child.getChildNodes();
                    for (int j = 0; j < infos.getLength(); j++) {
                        Node meta = infos.item(j);
                        if (meta.getNodeType() == Node.ELEMENT_NODE) {
                            if (//$NON-NLS-1$
                            meta.getNodeName().equals(//$NON-NLS-1$
                            "meta.element")) {
                                element.setLabelProperty(getAttribute(//$NON-NLS-1$
                                meta, //$NON-NLS-1$
                                "labelAttribute"));
                                //$NON-NLS-1$
                                element.setIconProperty(//$NON-NLS-1$
                                getAttribute(meta, "icon"));
                                if (element.getIconProperty() == null)
                                    //$NON-NLS-1$
                                    element.setIconProperty(//$NON-NLS-1$
                                    getAttribute(meta, "iconName"));
                                element.setTranslatableProperty(processTranslatable(getAttribute(//$NON-NLS-1$
                                meta, //$NON-NLS-1$
                                "translatable")));
                                element.setDeprecatedProperty(processDeprecated(getAttribute(//$NON-NLS-1$
                                meta, //$NON-NLS-1$
                                "deprecated")));
                                if (element instanceof ISchemaRootElement) {
                                    // set deprecated suggestion
                                    String depSug = getAttribute(meta, ISchemaRootElement.P_DEP_REPLACEMENT);
                                    ((ISchemaRootElement) element).setDeprecatedSuggestion(depSug);
                                    // set internal
                                    String internal = getAttribute(meta, ISchemaRootElement.P_INTERNAL);
                                    ((ISchemaRootElement) element).setInternal(Boolean.valueOf(internal).booleanValue());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private ISchemaEnumeration processEnumeration(ISchema schema, Node node) {
        //$NON-NLS-1$
        String name = getAttribute(node, "value");
        return new SchemaEnumeration(schema, name);
    }

    private int processKind(String name) {
        if (name != null) {
            if (//$NON-NLS-1$
            name.equals("java"))
                return IMetaAttribute.JAVA;
            if (//$NON-NLS-1$
            name.equals("resource"))
                return IMetaAttribute.RESOURCE;
            if (//$NON-NLS-1$
            name.equals("identifier"))
                return IMetaAttribute.IDENTIFIER;
        }
        return IMetaAttribute.STRING;
    }

    private void processSchemaAnnotation(Node node) {
        NodeList children = node.getChildNodes();
        //$NON-NLS-1$
        String section = "overview";
        //$NON-NLS-1$
        String sectionName = "Overview";
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (//$NON-NLS-1$
                child.getNodeName().equals("documentation") && !fAbbreviated) {
                    String text = getNormalizedText(child.getFirstChild().getNodeValue());
                    if (section != null) {
                        if (//$NON-NLS-1$
                        section.equals(//$NON-NLS-1$
                        "overview")) {
                            setDescription(text);
                        } else {
                            DocumentSection sec = new DocumentSection(this, section, sectionName);
                            sec.setDescription(text);
                            fDocSections.add(sec);
                        }
                    }
                } else if (//$NON-NLS-1$ //$NON-NLS-2$
                child.getNodeName().equals("appInfo") || child.getNodeName().equals("appinfo")) {
                    NodeList infos = child.getChildNodes();
                    for (int j = 0; j < infos.getLength(); j++) {
                        Node meta = infos.item(j);
                        if (meta.getNodeType() == Node.ELEMENT_NODE) {
                            if (//$NON-NLS-1$
                            meta.getNodeName().equals(//$NON-NLS-1$
                            "meta.schema")) {
                                //$NON-NLS-1$
                                section = "overview";
                                setName(//$NON-NLS-1$
                                getAttribute(meta, "name"));
                                //$NON-NLS-1$
                                fPluginID = //$NON-NLS-1$
                                getAttribute(meta, "plugin");
                                //$NON-NLS-1$
                                fPointID = //$NON-NLS-1$
                                getAttribute(meta, "id");
                                fValid = true;
                            } else if (//$NON-NLS-1$
                            meta.getNodeName().equals(//$NON-NLS-1$
                            "meta.section")) {
                                //$NON-NLS-1$
                                section = //$NON-NLS-1$
                                getAttribute(meta, "type");
                                //$NON-NLS-1$
                                sectionName = //$NON-NLS-1$
                                getAttribute(meta, "name");
                                if (sectionName == null)
                                    sectionName = section;
                            }
                        }
                    }
                }
            }
        }
    }

    private void processInclude(Node node) {
        //$NON-NLS-1$
        String location = getAttribute(node, "schemaLocation");
        SchemaInclude include = new SchemaInclude(this, location, fAbbreviated, fSearchPath);
        if (fIncludes == null)
            fIncludes = new Vector();
        fIncludes.add(include);
    }

    public void reload() {
        reload(null);
    }

    public void reload(InputStream is) {
        setNotificationEnabled(false);
        reset();
        if (is != null)
            load(is);
        else
            load();
        setNotificationEnabled(true);
        if (isLoaded())
            fireModelChanged(new ModelChangedEvent(this, IModelChangedEvent.WORLD_CHANGED, new Object[0], null));
    }

    public void removeDocumentSection(IDocumentSection docSection) {
        fDocSections.remove(docSection);
        fireModelChanged(new ModelChangedEvent(this, IModelChangedEvent.REMOVE, new Object[] { docSection }, null));
    }

    public void moveElementToSibling(ISchemaElement element, ISchemaObject sibling) {
        if (!isLoaded())
            load();
        int index = fElements.indexOf(element);
        int newIndex;
        if (sibling != null && fElements.contains(sibling))
            newIndex = fElements.indexOf(sibling);
        else
            newIndex = fElements.size() - 1;
        if (index > newIndex) {
            for (int i = index; i > newIndex; i--) {
                fElements.set(i, fElements.get(i - 1));
            }
        } else if (index < newIndex) {
            for (int i = index; i < newIndex; i++) {
                fElements.set(i, fElements.get(i + 1));
            }
        } else
            // don't need to move
            return;
        fElements.set(newIndex, element);
        fireModelChanged(new ModelChangedEvent(this, IModelChangedEvent.CHANGE, new Object[] { this }, null));
    }

    public void removeElement(ISchemaElement element) {
        fElements.remove(element);
        fireModelChanged(new ModelChangedEvent(this, IModelChangedEvent.REMOVE, new Object[] { element }, null));
    }

    @Override
    public void removeModelChangedListener(IModelChangedListener listener) {
        fListeners.remove(listener);
    }

    private void reset() {
        fElements = new Vector();
        fDocSections = new Vector();
        fIncludes = null;
        fPointID = null;
        fPluginID = null;
        fReferences = null;
        fDescription = null;
        fName = null;
        fValid = false;
        fLoaded = false;
    }

    private void resolveElementReference(ISchemaObjectReference reference) {
        ISchemaElement[] elementList = getResolvedElements();
        for (int i = 0; i < elementList.length; i++) {
            ISchemaElement element = elementList[i];
            if (!(element instanceof ISchemaObjectReference) && element.getName().equals(reference.getName())) {
                // Link
                reference.setReferencedObject(element);
                break;
            }
        }
    }

    private void resolveReference(ISchemaObjectReference reference) {
        Class<?> clazz = reference.getReferencedObjectClass();
        if (clazz.equals(ISchemaElement.class)) {
            resolveElementReference(reference);
        }
    }

    private void resolveReferences(Vector<SchemaElementReference> references) {
        for (int i = 0; i < references.size(); i++) {
            ISchemaObjectReference reference = references.elementAt(i);
            resolveReference(reference);
        }
    }

    private SchemaType resolveTypeReference(String typeName) {
        // for now, create a simple type
        return new SchemaSimpleType(this, typeName);
    }

    public void setDescription(String newDescription) {
        String oldValue = fDescription;
        fDescription = newDescription;
        fireModelObjectChanged(this, P_DESCRIPTION, oldValue, fDescription);
    }

    public void setName(String newName) {
        if (newName == null)
            //$NON-NLS-1$
            newName = "";
        String oldValue = fName;
        fName = newName;
        fireModelObjectChanged(this, P_NAME, oldValue, fName);
    }

    @Override
    public void setPluginId(String newId) {
        String oldValue = fPluginID;
        fPluginID = newId;
        fireModelObjectChanged(this, P_PLUGIN, oldValue, newId);
    }

    @Override
    public void setPointId(String newId) {
        String oldValue = fPointID;
        fPointID = newId;
        fireModelObjectChanged(this, P_POINT, oldValue, newId);
    }

    public void setNotificationEnabled(boolean newNotificationEnabled) {
        fNotificationEnabled = newNotificationEnabled;
    }

    /**
	 * Sets a list of additional schema relative or absolute paths to search when
	 * trying to find an included schema.  Must be set before {@link #load()} is
	 * called.
	 *
	 * @param searchPath the list of paths to search for included schema or <code>null</code> for no additional paths
	 */
    public void setSearchPath(List<IPath> searchPath) {
        fSearchPath = searchPath;
    }

    @Override
    public String toString() {
        return fName;
    }

    public void traverseDocumentTree(Node root) {
        if (root == null)
            return;
        NodeList children = root.getChildNodes();
        fReferences = new Vector();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNodeName().toLowerCase(Locale.ENGLISH);
                if (//$NON-NLS-1$
                nodeName.equals("element")) {
                    ISchemaElement element = processElement(this, child);
                    if (element == null) {
                        fValid = false;
                        return;
                    }
                    ISchemaAttribute[] attributes = element.getAttributes();
                    for (int j = 0; j < attributes.length; j++) if (attributes[j] == null) {
                        fValid = false;
                        return;
                    }
                    fElements.add(element);
                } else if (//$NON-NLS-1$
                nodeName.equals("annotation")) {
                    processSchemaAnnotation(child);
                } else if (//$NON-NLS-1$
                nodeName.equals("include")) {
                    processInclude(child);
                }
            }
        }
        addOmittedDocumentSections();
        fLoaded = true;
        if (fReferences.size() > 0)
            resolveReferences(fReferences);
        fReferences = null;
    }

    private void addOmittedDocumentSections() {
        for (int i = 0; i < DocumentSection.DOC_SECTIONS.length; i++) {
            DocumentSection section = new DocumentSection(this, DocumentSection.DOC_SECTIONS[i], null);
            if (!fDocSections.contains(section)) {
                addDocumentSection(section);
            }
        }
        Collections.sort(fDocSections);
    }

    public void updateReferencesFor(ISchemaElement element) {
        updateReferencesFor(element, ISchema.REFRESH_RENAME);
    }

    public void updateReferencesFor(ISchemaElement element, int kind) {
        for (int i = 0; i < fElements.size(); i++) {
            ISchemaElement el = fElements.get(i);
            if (el.equals(element))
                continue;
            ISchemaType type = el.getType();
            if (type instanceof ISchemaComplexType) {
                SchemaCompositor compositor = (SchemaCompositor) ((ISchemaComplexType) type).getCompositor();
                if (compositor != null)
                    compositor.updateReferencesFor(element, kind);
            }
        }
    }

    @Override
    public void write(String indent, PrintWriter writer) {
        //$NON-NLS-1$
        writer.println("<?xml version='1.0' encoding='UTF-8'?>");
        //$NON-NLS-1$
        writer.println("<!-- Schema file written by PDE -->");
        //$NON-NLS-1$ //$NON-NLS-2$
        writer.println("<schema targetNamespace=\"" + fPluginID + "\" xmlns=\"http://www.w3.org/2001/XMLSchema\">");
        String indent2 = INDENT + INDENT;
        String indent3 = indent2 + INDENT;
        //$NON-NLS-1$
        writer.println(indent + "<annotation>");
        //$NON-NLS-1$ //$NON-NLS-2$
        writer.println(indent2 + (getSchemaVersion() >= 3.4 ? "<appinfo>" : "<appInfo>"));
        //$NON-NLS-1$ //$NON-NLS-2$
        writer.print(indent3 + "<meta.schema plugin=\"" + fPluginID + "\"");
        //$NON-NLS-1$ //$NON-NLS-2$
        writer.print(" id=\"" + fPointID + "\"");
        //$NON-NLS-1$ //$NON-NLS-2$
        writer.println(" name=\"" + getName() + "\"/>");
        //$NON-NLS-1$ //$NON-NLS-2$
        writer.println(indent2 + (getSchemaVersion() >= 3.4 ? "</appinfo>" : "</appInfo>"));
        //$NON-NLS-1$
        writer.println(indent2 + "<documentation>");
        writer.println(indent3 + getWritableDescription());
        //$NON-NLS-1$
        writer.println(indent2 + "</documentation>");
        //$NON-NLS-1$
        writer.println(INDENT + "</annotation>");
        writer.println();
        // add includes, if defined
        if (fIncludes != null) {
            for (int i = 0; i < fIncludes.size(); i++) {
                ISchemaInclude include = fIncludes.get(i);
                include.write(INDENT, writer);
                writer.println();
            }
        }
        // add elements
        for (int i = 0; i < fElements.size(); i++) {
            ISchemaElement element = fElements.get(i);
            element.write(INDENT, writer);
            writer.println();
        }
        // add document sections
        for (int i = 0; i < fDocSections.size(); i++) {
            IDocumentSection section = fDocSections.get(i);
            section.write(INDENT, writer);
            writer.println();
        }
        //$NON-NLS-1$
        writer.println("</schema>");
    }

    private String getWritableDescription() {
        //$NON-NLS-1$
        String lineDelimiter = System.getProperty("line.separator");
        String description = PDEXMLHelper.getWritableString(getDescription());
        //$NON-NLS-1$
        String platformDescription = description.replaceAll("\\r\\n|\\r|\\n", lineDelimiter);
        return platformDescription;
    }

    @Override
    public boolean isDeperecated() {
        Iterator<ISchemaElement> it = fElements.iterator();
        while (it.hasNext()) {
            Object next = it.next();
            if (next instanceof SchemaRootElement)
                return ((SchemaRootElement) next).isDeprecated();
        }
        return false;
    }

    @Override
    public String getDeprecatedSuggestion() {
        Iterator<ISchemaElement> it = fElements.iterator();
        while (it.hasNext()) {
            Object next = it.next();
            if (next instanceof SchemaRootElement)
                return ((SchemaRootElement) next).getDeprecatedSuggestion();
        }
        return null;
    }

    @Override
    public boolean isInternal() {
        Iterator<ISchemaElement> it = fElements.iterator();
        while (it.hasNext()) {
            Object next = it.next();
            if (next instanceof SchemaRootElement)
                return ((SchemaRootElement) next).isInternal();
        }
        return false;
    }

    @Override
    public double getSchemaVersion() {
        if (fTargetVersion == 0) {
            IPluginModelBase model = PDECore.getDefault().getModelManager().findModel(fPluginID);
            if (model != null) {
                IPluginBase base = model.getPluginBase();
                if (base != null) {
                    if (base.getSchemaVersion() != null) {
                        fTargetVersion = Double.parseDouble(base.getSchemaVersion());
                    }
                }
            }
            if (fTargetVersion == 0) {
                // Use default for target platform
                fTargetVersion = Double.parseDouble(TargetPlatformHelper.getSchemaVersion());
            }
        }
        return fTargetVersion;
    }
}
