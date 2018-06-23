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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.text.*;
import org.eclipse.osgi.util.ManifestElement;
import org.eclipse.osgi.util.NLS;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.PDECoreMessages;

public class JarManifestErrorReporter extends ErrorReporter {

    /**
	 * Map of IHeader by name
	 */
    protected Map<String, JarManifestHeader> fHeaders;

    protected IDocument fTextDocument;

    public  JarManifestErrorReporter(IFile file) {
        super(file);
        fTextDocument = createDocument(file);
    }

    private String getHeaderName(String line) {
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == ':') {
                return line.substring(0, i);
            }
            if ((c < 'A' || 'Z' < c) && (c < 'a' || 'z' < c) && (c < '0' || '9' < c)) {
                if (i == 0) {
                    return null;
                }
                if (c != '-' && c != '_') {
                    return null;
                }
            }
        }
        return null;
    }

    protected int getPackageLine(IHeader header, ManifestElement element) {
        String packageName = element.getValue();
        if (element.getDirectiveKeys() != null || element.getKeys() != null)
            //$NON-NLS-1$
            return getLine(header, packageName + ";");
        // check for this exact package on the last line
        try {
            IRegion lineRegion = fTextDocument.getLineInformation(header.getLineNumber() + header.getLinesSpan() - 1);
            String lineStr = fTextDocument.get(lineRegion.getOffset(), lineRegion.getLength());
            if (lineStr.endsWith(packageName)) {
                return header.getLineNumber() + header.getLinesSpan();
            }
        } catch (BadLocationException ble) {
            PDECore.logException(ble);
        }
        //$NON-NLS-1$
        return getLine(header, packageName + ",");
    }

    protected int getLine(IHeader header, String valueSubstring) {
        for (int l = header.getLineNumber(); l < header.getLineNumber() + header.getLinesSpan(); l++) {
            try {
                IRegion lineRegion = fTextDocument.getLineInformation(l);
                String lineStr = fTextDocument.get(lineRegion.getOffset(), lineRegion.getLength());
                if (lineStr.indexOf(valueSubstring) >= 0) {
                    return l + 1;
                }
            } catch (BadLocationException ble) {
                PDECore.logException(ble);
            }
        }
        // it might span mutliple lines, try a longer algorithm
        try {
            IRegion lineRegion = fTextDocument.getLineInformation(header.getLineNumber());
            String lineStr = fTextDocument.get(lineRegion.getOffset(), lineRegion.getLength());
            for (int l = header.getLineNumber() + 1; l < header.getLineNumber() + header.getLinesSpan(); l++) {
                lineRegion = fTextDocument.getLineInformation(l);
                lineStr += fTextDocument.get(lineRegion.getOffset() + /* the space */
                1, lineRegion.getLength());
                if (lineStr.indexOf(valueSubstring) >= 0) {
                    return l;
                }
            }
        } catch (BadLocationException ble) {
            PDECore.logException(ble);
        }
        return header.getLineNumber() + 1;
    }

    /**
	 * @param document
	 */
    protected void parseManifest(IDocument document, IProgressMonitor monitor) {
        try {
            fHeaders = new HashMap();
            JarManifestHeader header = null;
            int l = 0;
            for (; l < document.getNumberOfLines(); l++) {
                if (l % 100 == 0)
                    checkCanceled(monitor);
                IRegion lineInfo = document.getLineInformation(l);
                String line = document.get(lineInfo.getOffset(), lineInfo.getLength());
                // test lines' length
                Charset charset = //$NON-NLS-1$
                Charset.forName(//$NON-NLS-1$
                "UTF-8");
                String lineDelimiter = document.getLineDelimiter(l);
                if (lineDelimiter == null) {
                    //$NON-NLS-1$
                    lineDelimiter = //$NON-NLS-1$
                    "";
                }
                ByteBuffer byteBuf = charset.encode(line);
                if (byteBuf.limit() + lineDelimiter.length() > 512) {
                    report(PDECoreMessages.BundleErrorReporter_lineTooLong, l + 1, CompilerFlags.ERROR, PDEMarkerFactory.CAT_FATAL);
                    return;
                }
                // parse
                if (line.length() == 0) {
                    // Empty Line
                    if (l == 0) {
                        report(PDECoreMessages.BundleErrorReporter_noMainSection, 1, CompilerFlags.ERROR, PDEMarkerFactory.CAT_FATAL);
                        return;
                    }
                    /* flush last line */
                    if (header != null) {
                        fHeaders.put(header.getName().toLowerCase(), header);
                        header = null;
                    }
                    /* done processing main attributes */
                    break;
                }
                if (line.charAt(0) == ' ') {
                    /* if no previous line */
                    if (l == 0) {
                        report(PDECoreMessages.BundleErrorReporter_noMainSection, 1, CompilerFlags.ERROR, PDEMarkerFactory.CAT_FATAL);
                        return;
                    }
                    if (header != null) {
                        header.append(line.substring(1));
                    }
                    continue;
                }
                // Expecting New Header
                if (header != null) {
                    fHeaders.put(header.getName().toLowerCase(), header);
                    header = null;
                }
                int colon = line.indexOf(':');
                if (/* no colon */
                colon == -1) {
                    report(PDECoreMessages.BundleErrorReporter_noColon, l + 1, CompilerFlags.ERROR, PDEMarkerFactory.CAT_FATAL);
                    return;
                }
                String headerName = getHeaderName(line);
                if (headerName == null) {
                    report(PDECoreMessages.BundleErrorReporter_invalidHeaderName, l + 1, CompilerFlags.ERROR, PDEMarkerFactory.CAT_FATAL);
                    return;
                }
                if (line.length() < colon + 2 || line.charAt(colon + 1) != ' ') {
                    report(PDECoreMessages.BundleErrorReporter_noSpaceValue, l + 1, CompilerFlags.ERROR, PDEMarkerFactory.CAT_FATAL);
                    return;
                }
                if (//$NON-NLS-1$
                "Name".equals(headerName)) {
                    report(PDECoreMessages.BundleErrorReporter_nameHeaderInMain, l + 1, CompilerFlags.ERROR, PDEMarkerFactory.CAT_FATAL);
                    return;
                }
                header = new JarManifestHeader(headerName, line.substring(colon + 2), l, this);
                if (fHeaders.containsKey(header.getName().toLowerCase())) {
                    report(PDECoreMessages.BundleErrorReporter_duplicateHeader, l + 1, CompilerFlags.WARNING, PDEMarkerFactory.CAT_OTHER);
                }
            }
            if (header != null) {
                // lingering header, line not terminated
                IMarker marker = report(PDECoreMessages.BundleErrorReporter_noLineTermination, l, CompilerFlags.ERROR, PDEMarkerFactory.M_NO_LINE_TERMINATION, PDEMarkerFactory.CAT_FATAL);
                try {
                    if (marker != null) {
                        // Check whether last line is purely whitespace, and add this information to the marker.
                        IRegion lineInfo = document.getLineInformation(document.getNumberOfLines() - 1);
                        String line = document.get(lineInfo.getOffset(), lineInfo.getLength());
                        marker.setAttribute(PDEMarkerFactory.ATTR_HAS_CONTENT, !//$NON-NLS-1$
                        line.matches("\\s+"));
                    }
                } catch (CoreException e) {
                }
                return;
            }
            // the empty lines are a mistake, report it.
            for (; l < document.getNumberOfLines(); l++) {
                IRegion lineInfo = document.getLineInformation(l);
                String line = document.get(lineInfo.getOffset(), lineInfo.getLength());
                if (line.length() == 0) {
                    continue;
                }
                if (//$NON-NLS-1$
                !line.startsWith("Name:")) {
                    report(PDECoreMessages.BundleErrorReporter_noNameHeader, l, CompilerFlags.ERROR, PDEMarkerFactory.CAT_FATAL);
                }
                break;
            }
            return;
        } catch (BadLocationException ble) {
            PDECore.logException(ble);
        }
    }

    protected void reportIllegalAttributeValue(IHeader header, String key, String value) {
        String msg = NLS.bind(PDECoreMessages.BundleErrorReporter_att_value, (new String[] { value, key }));
        report(//$NON-NLS-1$
        msg, //$NON-NLS-1$
        getLine(header, key + "="), //$NON-NLS-1$
        CompilerFlags.ERROR, PDEMarkerFactory.CAT_FATAL);
    }

    protected void reportIllegalValue(IHeader header, String value) {
        String msg = NLS.bind(PDECoreMessages.BundleErrorReporter_illegal_value, value);
        report(msg, getLine(header, value), CompilerFlags.ERROR, PDEMarkerFactory.CAT_FATAL);
    }

    protected void reportIllegalDirectiveValue(IHeader header, String key, String value) {
        String msg = NLS.bind(PDECoreMessages.BundleErrorReporter_dir_value, (new String[] { value, key }));
        //$NON-NLS-1$
        report(msg, getLine(header, key + ":="), CompilerFlags.ERROR, PDEMarkerFactory.CAT_FATAL);
    }

    protected void validateAttributeValue(IHeader header, ManifestElement element, String key, String[] allowedValues) {
        String value = element.getAttribute(key);
        if (value == null) {
            return;
        }
        for (int i = 0; i < allowedValues.length; i++) {
            if (allowedValues[i].equals(value)) {
                return;
            }
        }
        reportIllegalAttributeValue(header, key, value);
    }

    protected void validateBooleanAttributeValue(IHeader header, ManifestElement element, String key) {
        validateAttributeValue(header, element, key, BOOLEAN_VALUES);
    }

    protected void validateBooleanDirectiveValue(IHeader header, ManifestElement element, String key) {
        validateDirectiveValue(header, element, key, BOOLEAN_VALUES);
    }

    protected void validateBooleanValue(IHeader header) {
        validateHeaderValue(header, BOOLEAN_VALUES);
    }

    @Override
    protected void validate(IProgressMonitor monitor) {
        if (fTextDocument != null)
            parseManifest(fTextDocument, monitor);
    }

    protected void validateDirectiveValue(IHeader header, ManifestElement element, String key, String[] allowedValues) {
        String value = element.getDirective(key);
        if (value == null) {
            return;
        }
        for (int i = 0; i < allowedValues.length; i++) {
            if (allowedValues[i].equals(value)) {
                return;
            }
        }
        reportIllegalDirectiveValue(header, key, value);
    }

    protected void validateHeaderValue(IHeader header, String[] allowedValues) {
        ManifestElement[] elements = header.getElements();
        if (elements.length > 0) {
            for (int i = 0; i < allowedValues.length; i++) {
                if (allowedValues[i].equals(elements[0].getValue())) {
                    return;
                }
            }
            reportIllegalValue(header, elements[0].getValue());
        }
    }

    protected IHeader validateRequiredHeader(String name) {
        IHeader header = fHeaders.get(name.toLowerCase());
        if (header == null) {
            report(NLS.bind(PDECoreMessages.BundleErrorReporter_headerMissing, name), 1, CompilerFlags.ERROR, PDEMarkerFactory.CAT_FATAL);
        }
        return header;
    }

    protected IHeader getHeader(String key) {
        return fHeaders.get(key.toLowerCase());
    }

    protected void checkCanceled(IProgressMonitor monitor) throws OperationCanceledException {
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
    }
}
