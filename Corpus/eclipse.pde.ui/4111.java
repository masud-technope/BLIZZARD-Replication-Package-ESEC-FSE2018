/*******************************************************************************
 *  Copyright (c) 2005, 2016 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.core.plugin;

import java.io.StringReader;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.pde.internal.core.TargetPlatformHelper;
import org.eclipse.pde.internal.core.util.IdUtil;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

public class PluginHandler extends DefaultHandler {

    private Document fDocument;

    private Element fRootElement;

    private Stack<Element> fOpenElements = new Stack();

    private String fSchemaVersion;

    private boolean fAbbreviated;

    private Locator fLocator;

    private boolean fPop;

    //$NON-NLS-1$
    private static final Pattern VERSION_RE = Pattern.compile("version\\s*=\\s*\"([^\"]+)\"");

    public  PluginHandler(boolean abbreviated) {
        fAbbreviated = abbreviated;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        fPop = true;
        if (fAbbreviated && fOpenElements.size() == 2) {
            Element parent = fOpenElements.peek();
            if (//$NON-NLS-1$
            parent.getNodeName().equals("extension") && !isInterestingExtension(fOpenElements.peek())) {
                fPop = false;
                return;
            }
        }
        Element element = fDocument.createElement(qName);
        for (int i = 0; i < attributes.getLength(); i++) {
            element.setAttribute(attributes.getQName(i), attributes.getValue(i));
            if (//$NON-NLS-1$ //$NON-NLS-2$
            "extension".equals(qName) || "extension-point".equals(qName)) {
                //$NON-NLS-1$
                element.setAttribute(//$NON-NLS-1$
                "line", //$NON-NLS-1$
                Integer.toString(fLocator.getLineNumber()));
            }
        }
        if (fRootElement == null)
            fRootElement = element;
        else
            fOpenElements.peek().appendChild(element);
        fOpenElements.push(element);
    }

    protected boolean isInterestingExtension(Element element) {
        //$NON-NLS-1$
        String point = element.getAttribute("point");
        return IdUtil.isInterestingExtensionPoint(point);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (//$NON-NLS-1$
        fPop || (qName.equals("extension") && fOpenElements.size() == 2)) {
            fOpenElements.pop();
        }
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        fLocator = locator;
    }

    @Override
    public void startDocument() throws SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            fDocument = factory.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
        }
    }

    @Override
    public void endDocument() throws SAXException {
        fDocument.appendChild(fRootElement);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        if (//$NON-NLS-1$
        "eclipse".equals(target)) {
            // Data should be of the form: version="<version>"
            data = data.trim();
            Matcher matcher = VERSION_RE.matcher(data);
            if (matcher.matches()) {
                fSchemaVersion = TargetPlatformHelper.getSchemaVersionForTargetVersion(matcher.group(1));
            } else {
                fSchemaVersion = TargetPlatformHelper.getSchemaVersion();
            }
        }
    }

    @Override
    public void characters(char[] characters, int start, int length) throws SAXException {
        if (fAbbreviated)
            return;
        processCharacters(characters, start, length);
    }

    /**
	 * @param characters
	 * @param start
	 * @param length
	 * @throws DOMException
	 */
    protected void processCharacters(char[] characters, int start, int length) throws DOMException {
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < length; i++) {
            buff.append(characters[start + i]);
        }
        Text text = fDocument.createTextNode(buff.toString());
        if (fRootElement == null)
            fDocument.appendChild(text);
        else
            fOpenElements.peek().appendChild(text);
    }

    public Node getDocumentElement() {
        if (fRootElement != null) {
            fRootElement.normalize();
        }
        return fRootElement;
    }

    public String getSchemaVersion() {
        return fSchemaVersion;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
        //$NON-NLS-1$
        return new InputSource(new StringReader(""));
    }
}
