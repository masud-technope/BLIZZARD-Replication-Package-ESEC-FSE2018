/****************************************************************************
 * Copyright (c) 2008, 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *    IBM Corporation - support for certain non-text editors
 *****************************************************************************/
package org.eclipse.ecf.docshare.menu;

import java.util.Iterator;
import java.util.List;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.docshare.DocShare;
import org.eclipse.ecf.internal.docshare.Activator;
import org.eclipse.ecf.internal.docshare.Messages;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.ui.menu.AbstractRosterMenuContributionItem;
import org.eclipse.ecf.presence.ui.menu.AbstractRosterMenuHandler;
import org.eclipse.jface.action.*;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.*;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @since 2.1
 */
public class DocShareRosterMenuContributionItem extends AbstractRosterMenuContributionItem {

    public  DocShareRosterMenuContributionItem() {
        super();
        setTopMenuName(Messages.DocShareRosterMenuContributionItem_SHARE_EDITOR_MENU_TEXT);
    }

    public  DocShareRosterMenuContributionItem(String id) {
        super(id);
        setTopMenuName(Messages.DocShareRosterMenuContributionItem_SHARE_EDITOR_MENU_TEXT);
    }

    protected IEditorPart getEditorPart() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench == null)
            return null;
        final IWorkbenchWindow ww = workbench.getActiveWorkbenchWindow();
        if (ww == null)
            return null;
        final IWorkbenchPage wp = ww.getActivePage();
        if (wp == null)
            return null;
        return wp.getActiveEditor();
    }

    protected DocShare getDocShareForPresenceContainerAdapter(IPresenceContainerAdapter presenceContainerAdapter) {
        final IContainer container = (IContainer) presenceContainerAdapter.getAdapter(IContainer.class);
        if (container == null)
            return null;
        return Activator.getDefault().getDocShare(container.getID());
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.ui.menu.AbstractRosterMenuContributionItem#getContributionItems()
	 */
    protected IContributionItem[] getContributionItems() {
        // Make sure this is a text editor
        IEditorPart editorPart = getEditorPart();
        if (editorPart == null)
            return NO_CONTRIBUTIONS;
        // A frequent pattern for multi-page editors with one source page
        if (!(editorPart instanceof ITextEditor)) {
            editorPart = (IEditorPart) editorPart.getAdapter(ITextEditor.class);
            if (editorPart == null)
                return NO_CONTRIBUTIONS;
        }
        // If we are already engaged in a doc share (either as initiator or as receiver)
        // Then present menu item to stop
        final List presenceContainerAdapters = getPresenceContainerAdapters();
        for (final Iterator i = presenceContainerAdapters.iterator(); i.hasNext(); ) {
            final IPresenceContainerAdapter pca = (IPresenceContainerAdapter) i.next();
            final DocShare docShare = getDocShareForPresenceContainerAdapter(pca);
            if (docShare != null && docShare.isSharing() && docShare.getTextEditor().equals(editorPart)) {
                return getMenuContributionForStopShare(pca.getRosterManager().getRoster(), docShare, docShare.getOtherID());
            }
        }
        return super.getContributionItems();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.presence.ui.menu.AbstractRosterMenuContributionItem#createContributionItemsForPresenceContainer(org.eclipse.ecf.presence.IPresenceContainerAdapter)
	 */
    protected IContributionItem[] createContributionItemsForPresenceContainer(IPresenceContainerAdapter presenceContainerAdapter) {
        final IContainer container = (IContainer) presenceContainerAdapter.getAdapter(IContainer.class);
        if (container == null)
            return NO_CONTRIBUTIONS;
        final DocShare docShare = Activator.getDefault().getDocShare(container.getID());
        if (docShare == null)
            return NO_CONTRIBUTIONS;
        final IRoster roster = presenceContainerAdapter.getRosterManager().getRoster();
        final IContributionItem[] contributions = createContributionItemsForRoster(roster);
        if (contributions == null || contributions.length == 0)
            return NO_CONTRIBUTIONS;
        final MenuManager menuManager = createMenuManagerForRoster(roster);
        for (int i = 0; i < contributions.length; i++) {
            menuManager.add(contributions[i]);
        }
        return new IContributionItem[] { menuManager };
    }

    protected IContributionItem[] getMenuContributionForStopShare(IRoster roster, final DocShare docShare, final ID otherID) {
        final IAction stopEditorShare = new Action() {

            public void run() {
                docShare.stopShare();
            }
        };
        stopEditorShare.setText(NLS.bind(Messages.DocShareRosterMenuContributionItem_STOP_SHARE_EDITOR_MENU_TEXT, trimIDNameForMenu(otherID)));
        stopEditorShare.setImageDescriptor(getTopMenuImageDescriptor());
        return new IContributionItem[] { new Separator(), new ActionContributionItem(stopEditorShare) };
    }

    protected AbstractRosterMenuHandler createRosterEntryHandler(IRosterEntry rosterEntry) {
        return new DocShareRosterMenuHandler(rosterEntry);
    }

    protected String trimIDNameForMenu(ID id) {
        final String idName = id.getName();
        //$NON-NLS-1$
        final int indexAt = idName.indexOf("@");
        if (indexAt == -1)
            return idName;
        return idName.substring(0, indexAt);
    }
}
