/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.datashare.mergeable;

import java.util.List;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IIdentifiable;

/**
 * Mergeable channel for merging and publishing items. This interface is an
 * abstraction of the RSS SSE protocol for sharing item information via RSS. See
 * <a href="http://msdn.microsoft.com/xml/rss/sse">Simple Sharing Extensions for
 * RSS and OPML</a> for a description of the RSS SSE protocol.
 * 
 */
public interface IMergeableChannel extends IIdentifiable, IAdaptable {

    /**
	 * Get list of IItems
	 * 
	 * @return List of items. Will not return <code>null</code>. List
	 *         contents will be of type IItem.
	 */
    public List getItems();

    /**
	 * Merget the currrent set of items with changes made remotely
	 * 
	 * @throws MergeException
	 *             thrown if local copy cannot be merged with remote changes
	 */
    public void merge() throws MergeException;

    /**
	 * Add item to set managed by this channel
	 * 
	 * @param item
	 *            the IItem to add. Should not be <code>null</code>.
	 * @return true if added successfully, false if item already exists in set
	 *         known to this channel
	 */
    public boolean addItem(IItem item);

    /**
	 * Change the description of the item identified by the given itemID
	 * 
	 * @param itemID
	 *            the itemID of the IItem to change. Should not be
	 *            <code>null</code>.
	 * @param description
	 *            the new description to change in the IItem. May be
	 *            <code>null</code>.
	 * @return true if item found and description changes, false if item not
	 *         found
	 */
    public boolean changeItem(ID itemID, String description);

    /**
	 * Remove item from channel
	 * 
	 * @param item
	 *            the item to remove. Should not be <code>null</code>.
	 * @return true if item removed, false if item not found.
	 */
    public boolean removeItem(IItem item);

    /**
	 * Publish local item changes (add, change, remove) previously made to this
	 * channel
	 * 
	 * @throws PublishException
	 *             if problem with publishing
	 */
    public void publish() throws PublishException;

    /**
	 * Get the item factory for this channel for creating new items
	 * 
	 * @return IItemFactory for this channel. Will not be <code>null</code>.
	 */
    public IItemFactory getItemFactory();

    /**
	 * Get the channel header info (title, link, description) for this channel
	 * 
	 * @return IChannelHeader that contains this info. Will not be
	 *         <code>null</code>.
	 */
    public IChannelHeader getHeaderInfo();
}
