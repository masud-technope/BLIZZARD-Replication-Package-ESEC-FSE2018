package org.eclipse.ecf.core.sharedobject.model;

import org.eclipse.ecf.core.sharedobject.model.SharedModel.Property;

/**
 * @since 2.4
 */
public interface ISharedModelPropertyValueChangeEvent extends ISharedModelEvent {

    public Property getProperty();

    public Object getPreviousValue();

    public Object getValue();
}
