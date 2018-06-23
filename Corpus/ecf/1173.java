package org.eclipse.ecf.core.sharedobject.model;

import org.eclipse.ecf.core.sharedobject.model.SharedModel.Property;

/**
 * @since 2.4
 */
public interface ISharedModelPropertyAddEvent extends ISharedModelEvent {

    public Property getPreAddedProperty();

    public Property getAddedProperty();
}
