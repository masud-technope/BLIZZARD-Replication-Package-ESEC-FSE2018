/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.presence.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.model.IWorkbenchAdapter2;

/**
 * Adapter factory for adapter to IWorkbenchAdapter2 (foreground and background
 * color and font for labels).  Subclasses may override as appropriate.
 */
public class RosterWorkbenchAdapter2Factory implements IAdapterFactory {

    private IWorkbenchAdapter2 rosterAdapter = new IWorkbenchAdapter2() {

        public RGB getBackground(Object element) {
            return getBackgroundForRoster((IRoster) element);
        }

        public FontData getFont(Object element) {
            return getFontForRoster((IRoster) element);
        }

        public RGB getForeground(Object element) {
            return getForegroundForRoster((IRoster) element);
        }
    };

    private IWorkbenchAdapter2 rosterGroupAdapter = new IWorkbenchAdapter2() {

        public RGB getBackground(Object element) {
            return getBackgroundForRosterGroup((IRosterGroup) element);
        }

        public FontData getFont(Object element) {
            return getFontForRosterGroup((IRosterGroup) element);
        }

        public RGB getForeground(Object element) {
            return getForegroundForRosterGroup((IRosterGroup) element);
        }
    };

    private IWorkbenchAdapter2 rosterItemAdapter = new IWorkbenchAdapter2() {

        public RGB getBackground(Object element) {
            return getBackgroundForRosterItem((IRosterItem) element);
        }

        public FontData getFont(Object element) {
            return getFontForRosterItem((IRosterItem) element);
        }

        public RGB getForeground(Object element) {
            return getForegroundForRosterItem((IRosterItem) element);
        }
    };

    private IWorkbenchAdapter2 rosterEntryAdapter = new IWorkbenchAdapter2() {

        public RGB getBackground(Object element) {
            return getBackgroundForRosterEntry((IRosterEntry) element);
        }

        public FontData getFont(Object element) {
            return getFontForRosterEntry((IRosterEntry) element);
        }

        public RGB getForeground(Object element) {
            return getForegroundForRosterEntry((IRosterEntry) element);
        }
    };

    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType.equals(IWorkbenchAdapter2.class)) {
            if (adaptableObject instanceof IRoster)
                return rosterAdapter;
            if (adaptableObject instanceof IRosterGroup)
                return rosterGroupAdapter;
            if (adaptableObject instanceof IRosterEntry)
                return rosterEntryAdapter;
            if (adaptableObject instanceof IRosterItem)
                return rosterItemAdapter;
        }
        return null;
    }

    /**
	 * @param element
	 *            to get foreground color for. This implementation returns null,
	 *            meaning that the default color will be used. Subclasses should
	 *            override as appropriate.
	 * @return RGB to use as foreground color. If <code>null</code> use
	 *         default color
	 */
    protected RGB getForegroundForRosterEntry(IRosterEntry element) {
        return null;
    }

    /**
	 * @param element
	 *            to get fontdata for. This implementation returns null, meaning
	 *            that the default font will be used. Subclasses should override
	 *            as appropriate.
	 * @return FontData to use for rendering given element. If <code>null</code>
	 *         use default FontData
	 */
    protected FontData getFontForRosterEntry(IRosterEntry element) {
        return null;
    }

    /**
	 * @param element
	 *            to get background color for. This implementation returns null,
	 *            meaning that the default color will be used. Subclasses should
	 *            override as appropriate.
	 * @return RGB to use as background color. If <code>null</code> use
	 *         default color
	 */
    protected RGB getBackgroundForRosterEntry(IRosterEntry element) {
        return null;
    }

    /**
	 * @param element
	 *            to get foreground color for. This implementation returns null,
	 *            meaning that the default color will be used. Subclasses should
	 *            override as appropriate.
	 * @return RGB to use as foreground color. If <code>null</code> use
	 *         default color
	 */
    protected RGB getForegroundForRosterItem(IRosterItem element) {
        return null;
    }

    /**
	 * @param element
	 *            to get fontdata for. This implementation returns null, meaning
	 *            that the default font will be used. Subclasses should override
	 *            as appropriate.
	 * @return FontData to use for rendering given element. If <code>null</code>
	 *         use default FontData
	 */
    protected FontData getFontForRosterItem(IRosterItem element) {
        return null;
    }

    /**
	 * @param element
	 *            to get background color for. This implementation returns null,
	 *            meaning that the default color will be used. Subclasses should
	 *            override as appropriate.
	 * @return RGB to use as background color. If <code>null</code> use
	 *         default color
	 */
    protected RGB getBackgroundForRosterItem(IRosterItem element) {
        return null;
    }

    /**
	 * @param element
	 *            to get foreground color for. This implementation returns null,
	 *            meaning that the default color will be used. Subclasses should
	 *            override as appropriate.
	 * @return RGB to use as foreground color. If <code>null</code> use
	 *         default color
	 */
    protected RGB getForegroundForRosterGroup(IRosterGroup element) {
        return null;
    }

    /**
	 * @param element
	 *            to get fontdata for. This implementation returns null, meaning
	 *            that the default font will be used. Subclasses should override
	 *            as appropriate.
	 * @return FontData to use for rendering given element. If <code>null</code>
	 *         use default FontData
	 */
    protected FontData getFontForRosterGroup(IRosterGroup element) {
        return null;
    }

    /**
	 * @param element
	 *            to get background color for.
	 * @return RGB to use as background color. If <code>null</code> use
	 *         default color
	 */
    protected RGB getBackgroundForRosterGroup(IRosterGroup element) {
        return null;
    }

    /**
	 * @param element
	 *            to get foreground color for.
	 * @return RGB to use as foreground color. If <code>null</code> use
	 *         default color
	 */
    protected RGB getForegroundForRoster(IRoster element) {
        return null;
    }

    /**
	 * @param element
	 *            to get fontdata for. This implementation returns null, meaning
	 *            that the default font will be used. Subclasses should override
	 *            as appropriate.
	 * @return FontData to use for rendering given element. If <code>null</code>
	 *         use default FontData
	 */
    protected FontData getFontForRoster(IRoster element) {
        return null;
    }

    /**
	 * @param element
	 *            to get background color for. This implementation returns null,
	 *            meaning that the default color will be used. Subclasses should
	 *            override as appropriate.
	 * @return RGB to use as background color. If <code>null</code> use
	 *         default color
	 */
    protected RGB getBackgroundForRoster(IRoster element) {
        return null;
    }

    public Class[] getAdapterList() {
        return new Class[] { IWorkbenchAdapter2.class };
    }
}
