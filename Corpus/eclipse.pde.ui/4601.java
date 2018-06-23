/*******************************************************************************
 *  Copyright (c) 2000, 2014 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Carver - STAR - bug 213255
 *     Brian de Alwis (MTI) - bug 429420
 *******************************************************************************/
package org.eclipse.pde.internal.core.schema;

import java.io.PrintWriter;
import org.eclipse.pde.internal.core.ischema.*;
import org.eclipse.pde.internal.core.util.SchemaUtil;
import org.eclipse.pde.internal.core.util.XMLComponentRegistry;

public class SchemaElement extends RepeatableSchemaObject implements ISchemaElement {

    private static final long serialVersionUID = 1L;

    //$NON-NLS-1$
    public static final String P_ICON_NAME = "iconName";

    //$NON-NLS-1$
    public static final String P_LABEL_PROPERTY = "labelProperty";

    //$NON-NLS-1$
    public static final String P_TYPE = "type";

    private String labelProperty;

    private ISchemaType type;

    private String iconName;

    private boolean fTranslatable;

    private boolean fDeprecated;

    public  SchemaElement(ISchemaObject parent, String name) {
        super(parent, name);
    }

    private String calculateChildRepresentation(ISchemaObject object, boolean addLinks) {
        //$NON-NLS-1$
        String child = "";
        if (object instanceof ISchemaCompositor) {
            child = calculateCompositorRepresentation((ISchemaCompositor) object, addLinks);
            if (//$NON-NLS-1$
            !child.equals("EMPTY") && child.length() > 0) {
                //$NON-NLS-1$ //$NON-NLS-2$
                child = "(" + child + ")";
            }
        } else {
            child = object.getName();
            if (addLinks) {
                //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                child = "<a href=\"#e." + child + "\">" + child + "</a>";
            }
        }
        int minOccurs = 1;
        int maxOccurs = 1;
        if (object instanceof ISchemaRepeatable) {
            minOccurs = ((ISchemaRepeatable) object).getMinOccurs();
            maxOccurs = ((ISchemaRepeatable) object).getMaxOccurs();
        }
        if (minOccurs == 0) {
            if (maxOccurs == 1)
                //$NON-NLS-1$
                child += "?";
            else
                //$NON-NLS-1$
                child += "*";
        } else if (minOccurs == 1) {
            if (maxOccurs > 1)
                //$NON-NLS-1$
                child += "+";
        }
        return child;
    }

    private String calculateCompositorRepresentation(ISchemaCompositor compositor, boolean addLinks) {
        int kind = compositor.getKind();
        ISchemaObject[] children = compositor.getChildren();
        if (children.length == 0)
            //$NON-NLS-1$
            return "EMPTY";
        //$NON-NLS-1$ //$NON-NLS-2$
        String text = kind == ISchemaCompositor.GROUP ? "(" : "";
        for (int i = 0; i < children.length; i++) {
            ISchemaObject object = children[i];
            String child = calculateChildRepresentation(object, addLinks);
            text += child;
            if (i < children.length - 1) {
                if (kind == ISchemaCompositor.SEQUENCE)
                    //$NON-NLS-1$
                    text += " , ";
                else if (kind == ISchemaCompositor.CHOICE)
                    //$NON-NLS-1$
                    text += " | ";
            }
        }
        if (kind == ISchemaCompositor.GROUP)
            //$NON-NLS-1$
            text += ")";
        return text;
    }

    @Override
    public ISchemaAttribute getAttribute(String name) {
        if (type != null && type instanceof ISchemaComplexType) {
            return ((ISchemaComplexType) type).getAttribute(name);
        }
        return null;
    }

    @Override
    public int getAttributeCount() {
        if (type != null && type instanceof ISchemaComplexType) {
            return ((ISchemaComplexType) type).getAttributeCount();
        }
        return 0;
    }

    @Override
    public ISchemaAttribute[] getAttributes() {
        if (type != null && type instanceof ISchemaComplexType) {
            return ((ISchemaComplexType) type).getAttributes();
        }
        return new ISchemaAttribute[0];
    }

    @Override
    public String[] getAttributeNames() {
        ISchemaAttribute[] attributes = getAttributes();
        String[] names = new String[attributes.length];
        for (int i = 0; i < attributes.length; i++) names[i] = attributes[i].getName();
        return names;
    }

    @Override
    public String getDTDRepresentation(boolean addLinks) {
        //$NON-NLS-1$
        String text = "";
        if (type == null)
            //$NON-NLS-1$
            text += "EMPTY";
        else {
            if (type instanceof ISchemaComplexType) {
                ISchemaComplexType complexType = (ISchemaComplexType) type;
                ISchemaCompositor compositor = complexType.getCompositor();
                if (compositor != null)
                    text += calculateChildRepresentation(compositor, addLinks);
                else if (getAttributeCount() != 0)
                    //$NON-NLS-1$
                    text += "EMPTY";
            }
            if (text.length() == 0)
                //$NON-NLS-1$
                text += //$NON-NLS-1$
                "(#PCDATA)";
        }
        if (text.length() > 0) {
            if (//$NON-NLS-1$
            !text.equals("EMPTY") && text.charAt(0) != '(')
                //$NON-NLS-1$ //$NON-NLS-2$
                text = "(" + text + ")";
        }
        return text;
    }

    @Override
    public String getIconProperty() {
        if (iconName != null)
            return iconName;
        ISchemaAttribute[] attributes = getAttributes();
        for (int i = 0; i < attributes.length; i++) {
            if (isValidIconProperty(attributes[i]))
                return attributes[i].getName();
        }
        return null;
    }

    @Override
    public String getLabelProperty() {
        if (labelProperty != null)
            return labelProperty;
        ISchemaAttribute[] attributes = getAttributes();
        for (int i = 0; i < attributes.length; i++) {
            if (isValidLabelProperty(attributes[i]))
                return attributes[i].getName();
        }
        return null;
    }

    private boolean isValidLabelProperty(ISchemaAttribute a) {
        return a.getKind() == IMetaAttribute.STRING && a.getType().getName().equals(ISchemaAttribute.TYPES[ISchemaAttribute.STR_IND]) && a.isTranslatable();
    }

    private boolean isValidIconProperty(ISchemaAttribute a) {
        return a.getKind() == IMetaAttribute.RESOURCE;
    }

    @Override
    public ISchemaType getType() {
        return type;
    }

    @Override
    public void setParent(ISchemaObject parent) {
        super.setParent(parent);
        if (type != null) {
            type.setSchema(getSchema());
            if (type instanceof ISchemaComplexType) {
                ISchemaComplexType ctype = (ISchemaComplexType) type;
                ISchemaCompositor comp = ctype.getCompositor();
                if (comp != null)
                    comp.setParent(this);
            }
        }
        if (getAttributeCount() > 0) {
            ISchemaAttribute[] atts = getAttributes();
            for (int i = 0; i < atts.length; i++) {
                ISchemaAttribute att = atts[i];
                att.setParent(this);
            }
        }
    }

    public void setIconProperty(String newIconName) {
        String oldValue = iconName;
        iconName = newIconName;
        getSchema().fireModelObjectChanged(this, P_ICON_NAME, oldValue, iconName);
    }

    public void setTranslatableProperty(boolean translatable) {
        boolean oldValue = fTranslatable;
        fTranslatable = translatable;
        getSchema().fireModelObjectChanged(this, P_TRANSLATABLE, Boolean.valueOf(oldValue), Boolean.valueOf(translatable));
    }

    public void setDeprecatedProperty(boolean deprecated) {
        boolean oldValue = fDeprecated;
        fDeprecated = deprecated;
        getSchema().fireModelObjectChanged(this, P_DEPRECATED, Boolean.valueOf(oldValue), Boolean.valueOf(deprecated));
    }

    public void setLabelProperty(String labelProperty) {
        String oldValue = this.labelProperty;
        this.labelProperty = labelProperty;
        getSchema().fireModelObjectChanged(this, P_LABEL_PROPERTY, oldValue, labelProperty);
    }

    public void setType(ISchemaType newType) {
        Object oldValue = type;
        type = newType;
        getSchema().fireModelObjectChanged(this, P_TYPE, oldValue, type);
    }

    @Override
    public void write(String indent, PrintWriter writer) {
        //$NON-NLS-1$ //$NON-NLS-2$
        writer.print(indent + "<element name=\"" + getName() + "\"");
        ISchemaType type = getType();
        if (type instanceof SchemaSimpleType) {
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(" type=\"" + type.getName() + "\"");
        }
        //$NON-NLS-1$
        writer.println(">");
        String indent2 = indent + Schema.INDENT;
        String realDescription = getWritableDescription();
        if (realDescription.length() == 0)
            realDescription = null;
        String extendedProperties = getExtendedAttributes();
        if (realDescription != null || iconName != null || labelProperty != null || extendedProperties != null || isDeprecated() || hasTranslatableContent()) {
            String indent3 = indent2 + Schema.INDENT;
            String indent4 = indent3 + Schema.INDENT;
            //$NON-NLS-1$
            writer.println(indent2 + "<annotation>");
            if (iconName != null || labelProperty != null || extendedProperties != null || isDeprecated() || hasTranslatableContent()) {
                //$NON-NLS-1$ //$NON-NLS-2$
                writer.println(indent3 + (getSchema().getSchemaVersion() >= 3.4 ? "<appinfo>" : "<appInfo>"));
                //$NON-NLS-1$
                writer.print(//$NON-NLS-1$
                indent4 + "<meta.element");
                if (labelProperty != null)
                    //$NON-NLS-1$ //$NON-NLS-2$
                    writer.print(" labelAttribute=\"" + labelProperty + "\"");
                if (iconName != null)
                    //$NON-NLS-1$ //$NON-NLS-2$
                    writer.print(" icon=\"" + iconName + "\"");
                if (hasTranslatableContent())
                    writer.print(" translatable=\"true\"");
                if (isDeprecated())
                    writer.print(" deprecated=\"true\"");
                if (extendedProperties != null)
                    writer.print(extendedProperties);
                //$NON-NLS-1$
                writer.println(//$NON-NLS-1$
                "/>");
                //$NON-NLS-1$ //$NON-NLS-2$
                writer.println(indent3 + (getSchema().getSchemaVersion() >= 3.4 ? "</appinfo>" : "</appInfo>"));
            }
            if (realDescription != null) {
                //$NON-NLS-1$
                writer.println(//$NON-NLS-1$
                indent3 + "<documentation>");
                if (getDescription() != null)
                    writer.println(indent4 + realDescription);
                //$NON-NLS-1$
                writer.println(//$NON-NLS-1$
                indent3 + "</documentation>");
            }
            //$NON-NLS-1$
            writer.println(indent2 + "</annotation>");
        }
        if (type instanceof SchemaComplexType) {
            SchemaComplexType complexType = (SchemaComplexType) type;
            complexType.write(indent2, writer);
        }
        //$NON-NLS-1$
        writer.println(indent + "</element>");
    }

    @Override
    public boolean hasTranslatableContent() {
        return fTranslatable;
    }

    @Override
    public boolean isDeprecated() {
        return fDeprecated;
    }

    @Override
    public boolean hasDeprecatedAttributes() {
        for (ISchemaAttribute att : getAttributes()) {
            if (att.isDeprecated()) {
                return true;
            }
        }
        return false;
    }

    public String getExtendedAttributes() {
        return null;
    }

    @Override
    public String getDescription() {
        if (super.getDescription() != null) {
            return super.getDescription();
        }
        ISchema schema = getSchema();
        if ((schema == null) || (schema.getURL() == null)) {
            // This can happen when creating a new extension point schema
            return null;
        }
        //$NON-NLS-1$
        String hashkey = schema.getURL().hashCode() + "_" + getName();
        String description = XMLComponentRegistry.Instance().getDescription(hashkey, XMLComponentRegistry.F_ELEMENT_COMPONENT);
        if (description == null) {
            SchemaElementHandler handler = new SchemaElementHandler(getName());
            SchemaUtil.parseURL(schema.getURL(), handler);
            description = handler.getDescription();
            XMLComponentRegistry.Instance().putDescription(hashkey, description, XMLComponentRegistry.F_ELEMENT_COMPONENT);
        }
        return description;
    }

    @Override
    public int compareTo(Object arg0) {
        if (arg0 instanceof ISchemaElement)
            return getName().compareToIgnoreCase(((ISchemaElement) arg0).getName());
        return -1;
    }
}
