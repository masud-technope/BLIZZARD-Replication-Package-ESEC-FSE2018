package org.eclipse.ecf.core.sharedobject.model;

import org.eclipse.ecf.core.sharedobject.model.SharedModel.Property;

/**
 * @since 2.4
 */
public interface ISharedModelPropertyRemoveEvent extends ISharedModelEvent {

    public Property getRemovedProperty();
}
