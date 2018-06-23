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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.ecf.internal.example.collab.ClientPlugin;
import org.eclipse.ecf.internal.example.collab.Messages;
import org.eclipse.ecf.internal.example.collab.ui.EditorHelper;
import org.eclipse.ecf.internal.example.collab.ui.hyperlink.EclipseCollabHyperlinkDetector.Selection;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 *
 */
public class EclipseCollabHyperlink implements IHyperlink {

    private final IRegion region;

    private final String fileName;

    private final Selection selection;

    /**
	 * @param detectedRegion
	 * @param fileName
	 * @param selection
	 */
    public  EclipseCollabHyperlink(IRegion detectedRegion, String fileName, Selection selection) {
        this.region = detectedRegion;
        this.fileName = fileName;
        this.selection = selection;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.jface.text.hyperlink.IHyperlink#getHyperlinkRegion()
	 */
    public IRegion getHyperlinkRegion() {
        return region;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.jface.text.hyperlink.IHyperlink#getHyperlinkText()
	 */
    public String getHyperlinkText() {
        return null;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.jface.text.hyperlink.IHyperlink#getTypeLabel()
	 */
    public String getTypeLabel() {
        return null;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.jface.text.hyperlink.IHyperlink#open()
	 */
    public void open() {
        final IWorkbench wb = PlatformUI.getWorkbench();
        final IWorkbenchWindow ww = wb.getActiveWorkbenchWindow();
        final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fileName));
        if (file != null) {
            final EditorHelper eh = new EditorHelper(ww);
            try {
                eh.openAndSelectForFile(file, (selection == null) ? 0 : selection.getStart(), (selection == null) ? 0 : (selection.getEnd() - selection.getStart()));
            } catch (final Exception e) {
                ClientPlugin.log(Messages.EclipseCollabHyperlink_EXCEPTION_OPEN_EDITOR, e);
            }
        } else {
            MessageDialog.openInformation(ww.getShell(), Messages.EclipseCollabHyperlink_EXCEPTION_OPEN_EDITOR_TITLE, NLS.bind(Messages.EclipseCollabHyperlink_MESSAGE_EXCEPTION_OPEN_EDITOR, fileName));
        }
    }
}
