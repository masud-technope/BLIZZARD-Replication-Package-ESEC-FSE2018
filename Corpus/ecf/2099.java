/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.example.collab.ui.hyperlink;

import org.eclipse.ecf.example.collab.share.EclipseCollabSharedObject.SharedMarker;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public class EclipseCollabHyperlinkDetector extends AbstractHyperlinkDetector {

    //$NON-NLS-1$
    public static final String SHARE_FILE_HYPERLINK_END = "/>";

    //$NON-NLS-1$
    public static final String SHARE_FILE_HYPERLINK_START = "<open file=\"";

    //$NON-NLS-1$
    public static final String SHARE_FILE_HYPERLINK_SELECTION = " selection=";

    /* (non-Javadoc)
	 * @see org.eclipse.jface.text.hyperlink.IHyperlinkDetector#detectHyperlinks(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion, boolean)
	 */
    public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
        if (region == null || textViewer == null)
            return null;
        final IDocument document = textViewer.getDocument();
        if (document == null)
            return null;
        final int offset = region.getOffset();
        IRegion lineInfo;
        String line;
        try {
            lineInfo = document.getLineInformationOfOffset(offset);
            line = document.get(lineInfo.getOffset(), lineInfo.getLength());
        } catch (final BadLocationException ex) {
            return null;
        }
        final IRegion detectedRegion = detectSubRegion(lineInfo, line);
        if (detectedRegion == null)
            return null;
        final int detectedStart = detectedRegion.getOffset() - lineInfo.getOffset();
        final String substring = line.substring(detectedStart, detectedStart + detectedRegion.getLength());
        final String fileName = detectFileName(substring);
        if (fileName == null)
            return null;
        final Selection selection = detectSelection(substring);
        return new IHyperlink[] { new EclipseCollabHyperlink(detectedRegion, fileName, selection) };
    }

    private Selection detectSelection(String linkString) {
        final int beginIndex = linkString.indexOf(SHARE_FILE_HYPERLINK_SELECTION);
        if (beginIndex == -1)
            return null;
        final int endIndex = linkString.indexOf(SHARE_FILE_HYPERLINK_END);
        if (endIndex == -1)
            return null;
        // should have syntax start-end
        final String selection = linkString.substring(beginIndex + SHARE_FILE_HYPERLINK_SELECTION.length(), endIndex);
        //$NON-NLS-1$
        final int dashIndex = selection.indexOf("-");
        if (dashIndex == -1)
            return null;
        try {
            final int start = Integer.parseInt(selection.substring(0, dashIndex));
            final int end = Integer.parseInt(selection.substring(dashIndex + 1));
            return new Selection(start, end);
        } catch (final NumberFormatException e) {
            return null;
        }
    }

    class Selection {

        int start;

        int end;

        public  Selection(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }
    }

    private String detectFileName(String substring) {
        //$NON-NLS-1$
        final int startIndex = substring.indexOf("\"");
        if (startIndex == -1)
            return null;
        //$NON-NLS-1$
        final int endIndex = substring.indexOf("\"", startIndex + 1);
        if (endIndex == -1)
            return null;
        return substring.substring(startIndex + 1, endIndex);
    }

    protected IRegion detectSubRegion(IRegion lineInfo, String fromLine) {
        final int startIndex = fromLine.indexOf(SHARE_FILE_HYPERLINK_START);
        if (startIndex == -1)
            return null;
        // got one...look for terminator after
        final int endIndex = fromLine.indexOf(SHARE_FILE_HYPERLINK_END, startIndex);
        if (endIndex == -1)
            return null;
        return new Region(lineInfo.getOffset() + startIndex, (endIndex - startIndex) + SHARE_FILE_HYPERLINK_END.length());
    }

    public static String createDisplayStringForEditorOpen(String resourceName, SharedMarker marker) {
        final StringBuffer se = new StringBuffer(EclipseCollabHyperlinkDetector.SHARE_FILE_HYPERLINK_START);
        //$NON-NLS-1$
        se.append(resourceName).append("\"");
        if (marker != null) {
            final int start = marker.getOffset().intValue();
            final int length = marker.getLength().intValue();
            if (length > 0) {
                se.append(EclipseCollabHyperlinkDetector.SHARE_FILE_HYPERLINK_SELECTION);
                //$NON-NLS-1$
                se.append(start).append("-").append(//$NON-NLS-1$
                start + length);
            }
        }
        se.append(EclipseCollabHyperlinkDetector.SHARE_FILE_HYPERLINK_END);
        return se.toString();
    }
}
