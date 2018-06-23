/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Carver - STAR - bug 213255
 *******************************************************************************/
package org.eclipse.pde.internal.core.schema;

import java.io.PrintWriter;
import org.eclipse.pde.internal.core.ischema.*;
import org.eclipse.pde.internal.core.util.SchemaUtil;
import org.eclipse.pde.internal.core.util.XMLComponentRegistry;

public class SchemaAttribute extends SchemaObject implements ISchemaAttribute {

    private static final long serialVersionUID = 1L;

    private int kind = STRING;

    private int use = OPTIONAL;

    private String valueFilter;

    private ISchemaSimpleType type;

    private String basedOn;

    private Object value;

    //$NON-NLS-1$
    public static final String P_USE = "useProperty";

    //$NON-NLS-1$
    public static final String P_VALUE_FILTER = "valueFilterProperty";

    //$NON-NLS-1$
    public static final String P_VALUE = "value";

    //$NON-NLS-1$
    public static final String P_KIND = "kindProperty";

    //$NON-NLS-1$
    public static final String P_TYPE = "typeProperty";

    //$NON-NLS-1$
    public static final String P_BASED_ON = "basedOnProperty";

    private boolean fTranslatable;

    private boolean fDeprecated;

    public  SchemaAttribute(ISchemaAttribute att, String newName) {
        super(att.getParent(), newName);
        kind = att.getKind();
        use = att.getUse();
        value = att.getValue();
        type = new SchemaSimpleType(att.getType());
        basedOn = att.getBasedOn();
    }

    public  SchemaAttribute(ISchemaObject parent, String name) {
        super(parent, name);
    }

    @Override
    public String getBasedOn() {
        if (getKind() == JAVA || getKind() == IDENTIFIER)
            return basedOn;
        return null;
    }

    @Override
    public int getKind() {
        return kind;
    }

    @Override
    public ISchemaSimpleType getType() {
        return type;
    }

    @Override
    public int getUse() {
        return use;
    }

    @Override
    public Object getValue() {
        return value;
    }

    public String getValueFilter() {
        return valueFilter;
    }

    public void setBasedOn(String newBasedOn) {
        String oldValue = basedOn;
        basedOn = newBasedOn;
        getSchema().fireModelObjectChanged(this, P_BASED_ON, oldValue, basedOn);
    }

    public void setKind(int newKind) {
        Integer oldValue = Integer.valueOf(kind);
        kind = newKind;
        getSchema().fireModelObjectChanged(this, P_KIND, oldValue, Integer.valueOf(kind));
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

    public void setType(ISchemaSimpleType newType) {
        Object oldValue = type;
        type = newType;
        getSchema().fireModelObjectChanged(this, P_TYPE, oldValue, type);
    }

    @Override
    public void setParent(ISchemaObject obj) {
        super.setParent(obj);
        if (type != null)
            type.setSchema(getSchema());
    }

    public void setUse(int newUse) {
        Integer oldValue = Integer.valueOf(use);
        use = newUse;
        getSchema().fireModelObjectChanged(this, P_USE, oldValue, Integer.valueOf(use));
    }

    public void setValue(String value) {
        String oldValue = (String) this.value;
        this.value = value;
        getSchema().fireModelObjectChanged(this, P_VALUE, oldValue, value);
    }

    public void setValueFilter(String valueFilter) {
        String oldValue = this.valueFilter;
        this.valueFilter = valueFilter;
        getSchema().fireModelObjectChanged(this, P_VALUE_FILTER, oldValue, valueFilter);
    }

    @Override
    public void write(String indent, PrintWriter writer) {
        boolean annotation = false;
        ISchemaSimpleType type = getType();
        String typeName = type.getName();
        writer.print(indent);
        //$NON-NLS-1$ //$NON-NLS-2$
        writer.print("<attribute name=\"" + getName() + "\"");
        if (type.getRestriction() == null)
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(" type=\"" + typeName + "\"");
        String useString = null;
        switch(getUse()) {
            case OPTIONAL:
                // useString="optional";
                break;
            case DEFAULT:
                //$NON-NLS-1$
                useString = //$NON-NLS-1$
                "default";
                break;
            case REQUIRED:
                //$NON-NLS-1$
                useString = //$NON-NLS-1$
                "required";
                break;
        }
        if (useString != null) {
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(" use=\"" + useString + "\"");
        }
        if (value != null && getUse() == DEFAULT) {
            //$NON-NLS-1$ //$NON-NLS-2$
            writer.print(" value=\"" + value + "\"");
        }
        String documentation = getWritableDescription();
        if (documentation != null || this.getBasedOn() != null || getKind() != STRING) {
            // Add annotation
            annotation = true;
            //$NON-NLS-1$
            writer.println(">");
            String annIndent = indent + Schema.INDENT;
            String indent2 = annIndent + Schema.INDENT;
            String indent3 = indent2 + Schema.INDENT;
            writer.print(annIndent);
            //$NON-NLS-1$
            writer.println("<annotation>");
            if (documentation != null) {
                //$NON-NLS-1$
                writer.println(//$NON-NLS-1$
                indent2 + "<documentation>");
                writer.println(indent3 + documentation);
                //$NON-NLS-1$
                writer.println(//$NON-NLS-1$
                indent2 + "</documentation>");
            }
            if (getBasedOn() != null || getKind() != STRING || isDeprecated() || isTranslatable()) {
                //$NON-NLS-1$ //$NON-NLS-2$
                writer.println(indent2 + (getSchema().getSchemaVersion() >= 3.4 ? "<appinfo>" : "<appInfo>"));
                //$NON-NLS-1$
                writer.print(//$NON-NLS-1$
                indent3 + "<meta.attribute");
                String kindValue = null;
                switch(// TODO let's use some constants ;D
                getKind()) {
                    case JAVA:
                        //$NON-NLS-1$
                        kindValue = "java";
                        break;
                    case RESOURCE:
                        //$NON-NLS-1$
                        kindValue = //$NON-NLS-1$
                        "resource";
                        break;
                    case IDENTIFIER:
                        //$NON-NLS-1$
                        kindValue = //$NON-NLS-1$
                        "identifier";
                }
                if (kindValue != null)
                    //$NON-NLS-1$ //$NON-NLS-2$
                    writer.print(" kind=\"" + kindValue + "\"");
                if (getBasedOn() != null)
                    //$NON-NLS-1$ //$NON-NLS-2$
                    writer.print(" basedOn=\"" + getBasedOn() + "\"");
                if (isTranslatable())
                    writer.print(" translatable=\"true\"");
                if (isDeprecated())
                    writer.print(" deprecated=\"true\"");
                //$NON-NLS-1$
                writer.println(//$NON-NLS-1$
                "/>");
                //$NON-NLS-1$ //$NON-NLS-2$
                writer.println(indent2 + (getSchema().getSchemaVersion() >= 3.4 ? "</appinfo>" : "</appInfo>"));
            }
            //$NON-NLS-1$
            writer.println(annIndent + "</annotation>");
        }
        if (type.getRestriction() != null) {
            type.write(indent + Schema.INDENT, writer);
        }
        if (annotation || type.getRestriction() != null) {
            //$NON-NLS-1$
            writer.println(indent + "</attribute>");
        } else {
            //$NON-NLS-1$
            writer.println("/>");
        }
    }

    @Override
    public boolean isTranslatable() {
        if (getKind() == STRING && fTranslatable)
            //$NON-NLS-1$
            return type == null || "string".equals(type.getName());
        return false;
    }

    @Override
    public boolean isDeprecated() {
        return fDeprecated;
    }

    @Override
    public String getDescription() {
        if (super.getDescription() != null) {
            return super.getDescription();
        }
        ISchema schema = getSchema();
        if ((schema == null) || (schema.getURL() == null)) {
            return null;
        }
        String elementName = null;
        if (getParent() instanceof ISchemaElement) {
            elementName = ((ISchemaElement) getParent()).getName();
            if (elementName == null) {
                return null;
            }
        }
        //$NON-NLS-1$ //$NON-NLS-2$
        String hashkey = schema.getURL().hashCode() + "_" + elementName + "_" + getName();
        String description = XMLComponentRegistry.Instance().getDescription(hashkey, XMLComponentRegistry.F_ATTRIBUTE_COMPONENT);
        if (description == null) {
            SchemaAttributeHandler handler = new SchemaAttributeHandler(elementName, getName());
            SchemaUtil.parseURL(schema.getURL(), handler);
            description = handler.getDescription();
            XMLComponentRegistry.Instance().putDescription(hashkey, description, XMLComponentRegistry.F_ATTRIBUTE_COMPONENT);
        }
        return description;
    }
}
