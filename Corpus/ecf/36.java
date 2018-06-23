/*******************************************************************************
 * Copyright (c) 2007 Chris Aniszczyk and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Chris Aniszczyk <caniszczyk@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.presence.ui.handlers;

import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.internal.presence.ui.Activator;
import org.eclipse.ecf.internal.presence.ui.Messages;
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.ui.SharedImages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * @author zx
 */
public class BrowseDialog extends FilteredItemsSelectionDialog {

    private static final String DIALOG_SETTINGS = //$NON-NLS-1$
    "org.eclipse.ecf.presence.ui.dialogs.BrowseDialog";

    //$NON-NLS-1$
    private static final String SHOW_ONLINE_ONLY = "showOnlineOnly";

    private IContainer[] containers = null;

    private RosterItemLabelProvider rosterItemLabelProvider;

    private RosterItemDetailsLabelProvider rosterItemDetailsLabelProvider;

    private ToggleOnlineOnlyAction toggleOnlineOnlyAction;

    private OnlineOnlyViewerFilter onlineOnlyFilter = new OnlineOnlyViewerFilter();

    public  BrowseDialog(Shell shell, IContainer[] containers) {
        super(shell, false);
        this.containers = containers;
        setSelectionHistory(new RosterItemSelectionHistory());
        setTitle(Messages.BrowseDialog_title);
        setMessage(Messages.BrowseDialog_message);
        setImage(SharedImages.getImage(SharedImages.IMG_COMMUNICATIONS));
        rosterItemLabelProvider = new RosterItemLabelProvider();
        setListLabelProvider(rosterItemLabelProvider);
        rosterItemDetailsLabelProvider = new RosterItemDetailsLabelProvider();
        setDetailsLabelProvider(rosterItemDetailsLabelProvider);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#createExtendedContentArea(org.eclipse.swt.widgets.Composite)
	 */
    protected Control createExtendedContentArea(Composite parent) {
        return null;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#createFilter()
	 */
    protected ItemsFilter createFilter() {
        return new RosterItemsFilter();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#fillContentProvider(org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.AbstractContentProvider, org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.ItemsFilter, org.eclipse.core.runtime.IProgressMonitor)
	 */
    protected void fillContentProvider(AbstractContentProvider contentProvider, ItemsFilter itemsFilter, IProgressMonitor progressMonitor) {
        progressMonitor.beginTask(Messages.BrowseDialog_scanning, containers.length);
        // cycle through all the containers and grab entries 
        for (int i = 0; i < containers.length; i++) {
            IContainer container = containers[i];
            IPresenceContainerAdapter presenceContainer = (IPresenceContainerAdapter) container.getAdapter(IPresenceContainerAdapter.class);
            if (presenceContainer != null) {
                IRosterManager manager = presenceContainer.getRosterManager();
                if (manager != null) {
                    Collection items = manager.getRoster().getItems();
                    for (Iterator it = items.iterator(); it.hasNext(); ) {
                        IRosterItem item = (IRosterItem) it.next();
                        addRosterItem(item, contentProvider, itemsFilter);
                    }
                }
            }
            progressMonitor.worked(1);
        }
        progressMonitor.done();
    }

    // cycle through all roster items recursively
    private void addRosterItem(IRosterItem item, AbstractContentProvider contentProvider, ItemsFilter itemsFilter) {
        if (item instanceof IRosterGroup) {
            IRosterGroup group = (IRosterGroup) item;
            for (Iterator it = group.getEntries().iterator(); it.hasNext(); ) {
                addRosterItem((IRosterItem) it.next(), contentProvider, itemsFilter);
            }
        }
        contentProvider.add(item, itemsFilter);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#getDialogSettings()
	 */
    protected IDialogSettings getDialogSettings() {
        IDialogSettings settings = Activator.getDefault().getDialogSettings().getSection(DIALOG_SETTINGS);
        if (settings == null) {
            settings = Activator.getDefault().getDialogSettings().addNewSection(DIALOG_SETTINGS);
        }
        return settings;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#getElementName(java.lang.Object)
	 */
    public String getElementName(Object item) {
        IRosterItem rosterItem = (IRosterItem) item;
        return rosterItem.getName();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#getItemsComparator()
	 */
    protected Comparator getItemsComparator() {
        return new RosterItemsComparator();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#validateItem(java.lang.Object)
	 */
    protected IStatus validateItem(Object item) {
        //$NON-NLS-1$
        return new Status(IStatus.OK, Activator.PLUGIN_ID, 0, "", null);
    }

    protected void fillViewMenu(IMenuManager menuManager) {
        super.fillViewMenu(menuManager);
        toggleOnlineOnlyAction = new ToggleOnlineOnlyAction();
        menuManager.add(toggleOnlineOnlyAction);
    }

    protected void restoreDialog(IDialogSettings settings) {
        super.restoreDialog(settings);
        if (settings.get(SHOW_ONLINE_ONLY) != null) {
            boolean showOnlineOnly = settings.getBoolean(SHOW_ONLINE_ONLY);
            toggleOnlineOnlyAction.setChecked(showOnlineOnly);
        }
        addListFilter(onlineOnlyFilter);
        applyFilter();
    }

    protected void storeDialog(IDialogSettings settings) {
        super.storeDialog(settings);
        settings.put(SHOW_ONLINE_ONLY, toggleOnlineOnlyAction.isChecked());
    }

    private class OnlineOnlyViewerFilter extends ViewerFilter {

        private boolean online = true;

        public boolean select(Viewer viewer, Object parentElement, Object element) {
            if (!(element instanceof IRosterEntry)) {
                return false;
            }
            IRosterEntry entry = (IRosterEntry) element;
            // check if we are showing online only
            if (online && entry.getPresence() != null && IPresence.Type.UNAVAILABLE == entry.getPresence().getType())
                return false;
            return true;
        }

        public void setOnlineOnly(boolean show) {
            this.online = show;
        }
    }

    public class RosterItemDetailsLabelProvider extends LabelProvider {

        private Map imageTable = new HashMap(7);

        public Image getImage(Object element) {
            IRosterItem item = (IRosterItem) element;
            IRoster roster = item.getRoster();
            IWorkbenchAdapter adapter = getAdapter(roster);
            if (adapter == null)
                return null;
            ImageDescriptor descriptor = adapter.getImageDescriptor(roster);
            if (descriptor == null)
                return null;
            Image image = (Image) imageTable.get(descriptor);
            if (image == null) {
                image = descriptor.createImage();
                imageTable.put(descriptor, image);
            }
            return image;
        }

        public String getText(Object element) {
            IRosterItem item = (IRosterItem) element;
            return item.getRoster().getName();
        }
    }

    public class RosterItemLabelProvider extends LabelProvider {

        private Map imageTable = new HashMap(7);

        /*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
		 */
        public Image getImage(Object element) {
            IWorkbenchAdapter adapter = getAdapter(element);
            if (adapter == null)
                return null;
            ImageDescriptor descriptor = adapter.getImageDescriptor(element);
            if (descriptor == null)
                return null;
            Image image = (Image) imageTable.get(descriptor);
            if (image == null) {
                image = descriptor.createImage();
                imageTable.put(descriptor, image);
            }
            return image;
        }

        /*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
		 */
        public String getText(Object element) {
            IRosterItem item = (IRosterItem) element;
            if (item == null)
                //$NON-NLS-1$
                return "";
            IRoster roster = item.getRoster();
            //$NON-NLS-1$ //$NON-NLS-2$
            return roster != null ? item.getName() + " - " + roster.getName() : "";
        }

        /*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.BaseLabelProvider#dispose()
		 */
        public void dispose() {
            if (imageTable != null) {
                for (Iterator i = imageTable.values().iterator(); i.hasNext(); ) {
                    ((Image) i.next()).dispose();
                }
                imageTable = null;
            }
        }
    }

    private class RosterItemSelectionHistory extends SelectionHistory {

        protected Object restoreItemFromMemento(IMemento memento) {
            // TODO Auto-generated method stub
            return null;
        }

        protected void storeItemToMemento(Object item, IMemento memento) {
        // TODO Auto-generated method stub
        }
    }

    private class RosterItemsFilter extends ItemsFilter {

        public boolean isConsistentItem(Object item) {
            return false;
        }

        public boolean matchItem(Object item) {
            if (!(item instanceof IRosterEntry)) {
                return false;
            }
            IRosterEntry rosterItem = (IRosterEntry) item;
            return matches(rosterItem.getName());
        }
    }

    private class RosterItemsComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            Collator collator = Collator.getInstance();
            IRosterItem item1 = (IRosterItem) o1;
            IRosterItem item2 = (IRosterItem) o2;
            String s1 = item1.getName();
            String s2 = item2.getName();
            int comparability = collator.compare(s1, s2);
            if (comparability == 0) {
            // TODO more here
            }
            return comparability;
        }
    }

    protected IWorkbenchAdapter getAdapter(Object element) {
        IWorkbenchAdapter adapter = null;
        if (element instanceof IAdaptable)
            adapter = (IWorkbenchAdapter) ((IAdaptable) element).getAdapter(IWorkbenchAdapter.class);
        if (element != null && adapter == null)
            adapter = (IWorkbenchAdapter) Platform.getAdapterManager().loadAdapter(element, IWorkbenchAdapter.class.getName());
        return adapter;
    }

    private class ToggleOnlineOnlyAction extends Action {

        public  ToggleOnlineOnlyAction() {
            super(Messages.ToggleOnlineOnlyAction_title, IAction.AS_CHECK_BOX);
            setChecked(true);
        }

        public void run() {
            onlineOnlyFilter.setOnlineOnly(isChecked());
            scheduleRefresh();
        }
    }
}
