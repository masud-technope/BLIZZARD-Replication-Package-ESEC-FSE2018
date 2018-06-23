/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.core.builders;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Locale;
import org.eclipse.core.runtime.*;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.ischema.*;
import org.eclipse.pde.internal.core.schema.*;
import org.osgi.framework.Bundle;

public class SchemaTransformer {

    //$NON-NLS-1$
    private static final String PLATFORM_PLUGIN = "org.eclipse.platform";

    //$NON-NLS-1$
    private static final String PLATFORM_PLUGIN_DOC = "org.eclipse.platform.doc.isv";

    //$NON-NLS-1$
    private static final String SCHEMA_CSS = "schema.css";

    //$NON-NLS-1$
    private static final String PLATFORM_CSS = "book.css";

    public static final byte TEMP = 0x00;

    public static final byte BUILD = 0x01;

    private byte fCssPurpose;

    private PrintWriter fWriter;

    private ISchema fSchema;

    private URL fCssURL;

    public void transform(ISchema schema, PrintWriter out) {
        transform(schema, out, null, TEMP);
    }

    public void transform(ISchema schema, PrintWriter out, URL cssURL, byte cssPurpose) {
        fSchema = schema;
        fWriter = out;
        fCssPurpose = cssPurpose;
        setCssURL(cssURL);
        printHTMLContent();
    }

    private void setCssURL(URL cssURL) {
        try {
            if (cssURL != null)
                fCssURL = FileLocator.resolve(cssURL);
        } catch (IOException e) {
        }
        if (fCssURL == null && fCssPurpose != BUILD)
            fCssURL = getResourceURL(getProductPlugin(), PLATFORM_CSS);
        if (fCssURL == null && fCssPurpose != BUILD)
            fCssURL = getResourceURL(PDECore.PLUGIN_ID, PLATFORM_CSS);
    }

    private String getCssURL() {
        //$NON-NLS-1$
        return (fCssURL != null) ? fCssURL.toString() : "../../" + PLATFORM_CSS;
    }

    private String getSchemaCssURL() {
        if (fCssPurpose == BUILD)
            //$NON-NLS-1$
            return "../../" + SCHEMA_CSS;
        URL url = getResourceURL(PLATFORM_PLUGIN_DOC, SCHEMA_CSS);
        if (url == null) {
            // this CSS file is last resort and is always there.
            url = getResourceURL(PDECore.PLUGIN_ID, SCHEMA_CSS);
        }
        return url.toString();
    }

    private void printHTMLContent() {
        //$NON-NLS-1$
        fWriter.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">");
        //$NON-NLS-1$
        fWriter.println("<HTML>");
        printHeader();
        printBody();
        //$NON-NLS-1$
        fWriter.println("</HTML>");
    }

    private void printHeader() {
        //$NON-NLS-1$
        fWriter.print("<HEAD>");
        //$NON-NLS-1$
        fWriter.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
        //$NON-NLS-1$ //$NON-NLS-2$
        fWriter.println("<title>" + fSchema.getName() + "</title>");
        printStyles();
        //$NON-NLS-1$
        fWriter.println("</HEAD>");
    }

    private void printStyles() {
        //$NON-NLS-1$ //$NON-NLS-2$
        fWriter.println("<style type=\"text/css\">@import url(\"" + getCssURL() + "\");</style>");
        //$NON-NLS-1$ //$NON-NLS-2$
        fWriter.println("<style type=\"text/css\">@import url(\"" + getSchemaCssURL() + "\");</style>");
    }

    private URL getResourceURL(String bundleID, String resourcePath) {
        try {
            Bundle bundle = Platform.getBundle(bundleID);
            if (bundle != null) {
                URL entry = bundle.getEntry(resourcePath);
                if (entry != null)
                    return FileLocator.toFileURL(entry);
            }
        } catch (IOException e) {
        }
        return null;
    }

    private void printBody() {
        //$NON-NLS-1$
        fWriter.println("<BODY>");
        //$NON-NLS-1$ //$NON-NLS-2$
        fWriter.println("<H1 style=\"text-align:center\">" + fSchema.getName() + "</H1>");
        if (fSchema.isDeperecated()) {
            //$NON-NLS-1$
            fWriter.print("<div style=\"border: 1px solid #990000; padding: 5px; text-align: center; color: red;\">");
            //$NON-NLS-1$
            fWriter.print("This extension point is deprecated");
            String suggestion = fSchema.getDeprecatedSuggestion();
            if (suggestion != null)
                //$NON-NLS-1$ //$NON-NLS-2$
                fWriter.print(", use <i>" + suggestion + "</i> as a replacement.");
            //$NON-NLS-1$
            fWriter.println("</div>");
        }
        if (fSchema.isInternal()) {
            //$NON-NLS-1$
            fWriter.print("<div style=\"border: 1px solid #990000; padding: 5px; text-align: center; color: red;\">");
            //$NON-NLS-1$
            fWriter.print("This extension point is internal");
            //$NON-NLS-1$
            fWriter.println("</div>");
        }
        //$NON-NLS-1$
        fWriter.println("<p></p>");
        //$NON-NLS-1$
        fWriter.print("<h6 class=\"CaptionFigColumn SchemaHeader\">Identifier: </h6>");
        fWriter.print(fSchema.getQualifiedPointId());
        //$NON-NLS-1$
        fWriter.println("<p></p>");
        //$NON-NLS-1$
        transformSection("Since:", IDocumentSection.SINCE);
        transformDescription();
        //$NON-NLS-1$
        fWriter.println("<h6 class=\"CaptionFigColumn SchemaHeader\">Configuration Markup:</h6>");
        transformMarkup();
        //$NON-NLS-1$
        transformSection("Examples:", IDocumentSection.EXAMPLES);
        //$NON-NLS-1$
        transformSection("API Information:", IDocumentSection.API_INFO);
        //$NON-NLS-1$
        transformSection("Supplied Implementation:", IDocumentSection.IMPLEMENTATION);
        //$NON-NLS-1$
        fWriter.println("<br>");
        //$NON-NLS-1$
        fWriter.println("<p class=\"note SchemaCopyright\">");
        transformSection(null, IDocumentSection.COPYRIGHT);
        //$NON-NLS-1$
        fWriter.println("</p>");
        //$NON-NLS-1$
        fWriter.println("</BODY>");
    }

    private void transformSection(String title, String sectionId) {
        IDocumentSection section = findSection(fSchema.getDocumentSections(), sectionId);
        if (section == null)
            return;
        String description = section.getDescription();
        if (description == null || description.trim().length() == 0)
            return;
        if (title != null)
            //$NON-NLS-1$ //$NON-NLS-2$
            fWriter.print("<h6 class=\"CaptionFigColumn SchemaHeader\">" + title + " </h6>");
        transformText(description);
        fWriter.println();
        if (!sectionId.equals(IDocumentSection.COPYRIGHT))
            //$NON-NLS-1$
            fWriter.println("<p></p>");
        fWriter.println();
    }

    private DocumentSection findSection(IDocumentSection[] sections, String sectionId) {
        for (int i = 0; i < sections.length; i++) {
            if (sections[i].getSectionId().equalsIgnoreCase(sectionId)) {
                return (DocumentSection) sections[i];
            }
        }
        return null;
    }

    private void transformText(String text) {
        if (text == null)
            return;
        boolean preformatted = false;
        boolean inTag = false;
        boolean inCstring = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '<') {
                if (isPreStart(text, i)) {
                    fWriter.print("<pre class=\"Example\"><span class=\"code SchemaTag\">");
                    i += 4;
                    preformatted = true;
                    continue;
                }
                if (isPreEnd(text, i)) {
                    //$NON-NLS-1$
                    fWriter.print(//$NON-NLS-1$
                    "</span></pre>");
                    i += 5;
                    preformatted = false;
                    inTag = false;
                    inCstring = false;
                    continue;
                }
            }
            if (preformatted) {
                switch(c) {
                    case '<':
                        inTag = true;
                        //$NON-NLS-1$
                        fWriter.print("&lt;");
                        break;
                    case '>':
                        //$NON-NLS-1$
                        fWriter.print("&gt;");
                        inTag = false;
                        inCstring = false;
                        break;
                    case '&':
                        //$NON-NLS-1$
                        fWriter.print(//$NON-NLS-1$
                        "&amp;");
                        break;
                    case '\'':
                        //$NON-NLS-1$
                        fWriter.print(//$NON-NLS-1$
                        "&apos;");
                        break;
                    case '\"':
                        if (inTag) {
                            if (inCstring) {
                                //$NON-NLS-1$
                                fWriter.print("&quot;");
                                fWriter.print("</span><span class=\"code SchemaTag\">");
                                inCstring = false;
                            } else {
                                inCstring = true;
                                fWriter.print("</span><span class=\"code SchemaCstring\">");
                                //$NON-NLS-1$
                                fWriter.print("&quot;");
                            }
                        } else {
                            //$NON-NLS-1$
                            fWriter.print("\"");
                        }
                        break;
                    default:
                        fWriter.print(c);
                }
            } else
                fWriter.print(c);
        }
    }

    private void transformDescription() {
        //$NON-NLS-1$
        fWriter.print("<h6 class=\"CaptionFigColumn SchemaHeader\">Description: </h6>");
        transformText(fSchema.getDescription());
        ISchemaInclude[] includes = fSchema.getIncludes();
        for (int i = 0; i < includes.length; i++) {
            ISchema ischema = includes[i].getIncludedSchema();
            if (ischema != null) {
                //$NON-NLS-1$
                fWriter.println(//$NON-NLS-1$
                "<p>");
                transformText(ischema.getDescription());
                //$NON-NLS-1$
                fWriter.println(//$NON-NLS-1$
                "</p>");
            }
        }
        //$NON-NLS-1$
        fWriter.println("<p></p>");
    }

    private void transformMarkup() {
        //$NON-NLS-1$
        fWriter.println("<p></p>");
        ISchemaElement[] elements = fSchema.getResolvedElements();
        for (int i = 0; i < elements.length; i++) {
            transformElement(elements[i]);
        }
    }

    private void transformElement(ISchemaElement element) {
        String name = element.getName();
        String dtd = element.getDTDRepresentation(true);
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String nameLink = "<a name=\"e." + name + "\">" + name + "</a>";
        if (element.isDeprecated() && !(element instanceof SchemaRootElement))
            //$NON-NLS-1$ //$NON-NLS-2$
            fWriter.print("<div style=\"color: red; font-style: italic;\">The <b>" + name + "</b> element is deprecated</div> ");
        fWriter.print(//$NON-NLS-1$
        "<p class=\"code SchemaDtd\">&lt;!ELEMENT " + //$NON-NLS-1$
        nameLink + //$NON-NLS-1$
        " " + dtd);
        //$NON-NLS-1$
        fWriter.println("&gt;</p>");
        ISchemaAttribute[] attributes = element.getAttributes();
        if (attributes.length > 0) {
            fWriter.println(//$NON-NLS-1$
            "<p class=\"code SchemaDtd\">&lt;!ATTLIST " + //$NON-NLS-1$
            name + //$NON-NLS-1$
            "</p>");
            int maxWidth = calculateMaxAttributeWidth(element.getAttributes());
            for (int i = 0; i < attributes.length; i++) {
                //$NON-NLS-1$
                fWriter.print(//$NON-NLS-1$
                "<p class=\"code SchemaDtdAttlist\">");
                appendAttlist(attributes[i], maxWidth);
                if (i + 1 == attributes.length) {
                    //$NON-NLS-1$
                    fWriter.print(//$NON-NLS-1$
                    "&gt;");
                }
                //$NON-NLS-1$
                fWriter.println(//$NON-NLS-1$
                "</p>");
            }
        }
        //$NON-NLS-1$
        fWriter.println("<p></p>");
        // inserted desc here for element
        String description = element.getDescription();
        if (description != null && description.trim().length() > 0) {
            //$NON-NLS-1$ //$NON-NLS-2$
            String elementType = containsParagraph(description) ? "div" : "p";
            //$NON-NLS-1$ //$NON-NLS-2$
            fWriter.println("<" + elementType + " class=\"ConfigMarkupElementDesc\">");
            transformText(description);
            //$NON-NLS-1$ //$NON-NLS-2$
            fWriter.println("</" + elementType + ">");
        }
        // end of inserted desc for element
        if (attributes.length == 0) {
            //$NON-NLS-1$
            fWriter.println("<br><br>");
            return;
        } else if (description != null && description.trim().length() > 0) {
            //$NON-NLS-1$
            fWriter.println("<br>");
        }
        //$NON-NLS-1$
        fWriter.println("<ul class=\"ConfigMarkupAttlistDesc\">");
        for (int i = 0; i < attributes.length; i++) {
            ISchemaAttribute att = attributes[i];
            if (//$NON-NLS-1$
            name.equals("extension")) {
                if (att.getDescription() == null || att.getDescription().trim().length() == 0) {
                    continue;
                }
            }
            //$NON-NLS-1$
            fWriter.print("<li>");
            if (att.isDeprecated())
                //$NON-NLS-1$
                fWriter.print(//$NON-NLS-1$
                "<i style=\"color: red;\">Deprecated</i> ");
            //$NON-NLS-1$ //$NON-NLS-2$
            fWriter.print("<b>" + att.getName() + "</b> - ");
            transformText(att.getDescription());
            //$NON-NLS-1$
            fWriter.println("</li>");
        }
        //$NON-NLS-1$
        fWriter.println("</ul>");
        // adding spaces for new shifted view
        //$NON-NLS-1$
        fWriter.print("<br>");
    }

    private boolean containsParagraph(String input) {
        if (//$NON-NLS-1$
        input.indexOf("<p>") != -1)
            return true;
        if (//$NON-NLS-1$
        input.indexOf("</p>") != -1)
            return true;
        return false;
    }

    /**
	 * Writes out an attribute for an element's attribute list.  Note that this method does not add newlines.
	 * @param att the schema attribute to print
	 * @param maxWidth the length of the largest name in the list so items can be padded out.
	 */
    private void appendAttlist(ISchemaAttribute att, int maxWidth) {
        // add name
        fWriter.print(att.getName());
        // fill spaces to align data type
        int delta = maxWidth - att.getName().length();
        for (int i = 0; i < delta + 1; i++) {
            //$NON-NLS-1$
            fWriter.print("&nbsp;");
        }
        // add data type
        ISchemaSimpleType type = att.getType();
        ISchemaRestriction restriction = null;
        boolean choices = false;
        if (type != null)
            restriction = type.getRestriction();
        //$NON-NLS-1$
        String typeName = type != null ? type.getName().toLowerCase(Locale.ENGLISH) : "string";
        if (//$NON-NLS-1$
        typeName.equals("boolean")) {
            //$NON-NLS-1$
            fWriter.print("(true | false) ");
            choices = true;
        } else if (att.getKind() == IMetaAttribute.IDENTIFIER) {
            //$NON-NLS-1$
            fWriter.print("IDREF ");
        } else if (restriction != null) {
            appendRestriction(restriction);
            choices = true;
        } else {
            //$NON-NLS-1$
            fWriter.print("CDATA ");
        }
        // add use
        if (att.getUse() == ISchemaAttribute.REQUIRED) {
            if (!choices)
                //$NON-NLS-1$
                fWriter.print(//$NON-NLS-1$
                "#REQUIRED");
        } else if (att.getUse() == ISchemaAttribute.DEFAULT) {
            //$NON-NLS-1$ //$NON-NLS-2$
            fWriter.print("\"" + att.getValue() + "\"");
        } else if (!choices)
            //$NON-NLS-1$
            fWriter.print("#IMPLIED");
    }

    private void appendRestriction(ISchemaRestriction restriction) {
        if (restriction instanceof ChoiceRestriction) {
            String[] choices = ((ChoiceRestriction) restriction).getChoicesAsStrings();
            //$NON-NLS-1$
            fWriter.print("(");
            for (int i = 0; i < choices.length; i++) {
                if (i > 0)
                    //$NON-NLS-1$
                    fWriter.print(//$NON-NLS-1$
                    "|");
                fWriter.print(choices[i]);
            }
            //$NON-NLS-1$
            fWriter.print(") ");
        }
    }

    private boolean isPreEnd(String text, int loc) {
        if (loc + 5 >= text.length())
            return false;
        //$NON-NLS-1$
        return (text.substring(loc, loc + 6).toLowerCase(Locale.ENGLISH).equals("</pre>"));
    }

    private boolean isPreStart(String text, int loc) {
        if (loc + 4 >= text.length())
            return false;
        //$NON-NLS-1$
        return (text.substring(loc, loc + 5).toLowerCase(Locale.ENGLISH).equals("<pre>"));
    }

    private int calculateMaxAttributeWidth(ISchemaAttribute[] attributes) {
        int width = 0;
        for (int i = 0; i < attributes.length; i++) {
            width = Math.max(width, attributes[i].getName().length());
        }
        return width;
    }

    private String getProductPlugin() {
        IProduct product = Platform.getProduct();
        if (product != null) {
            Bundle plugin = product.getDefiningBundle();
            if (plugin != null) {
                return plugin.getSymbolicName();
            }
        }
        return PLATFORM_PLUGIN;
    }
}
