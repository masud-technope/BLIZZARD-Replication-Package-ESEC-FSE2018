package org.eclipse.ecf.internal.bulletinboard.commons;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;

public abstract class AbstractBBObject implements IAdaptable {

    protected String name;

    protected int mode;

    protected AbstractBulletinBoard bb;

    public  AbstractBBObject(String name, int mode) {
        super();
        this.name = name;
        this.mode = mode;
    }

    public void setBulletinBoard(AbstractBulletinBoard bb) {
        this.bb = bb;
    }

    public String getName() {
        return name;
    }

    public void setNameInternal(String name) {
        this.name = name;
    }

    public int getMode() {
        return mode;
    }

    public Object getAdapter(Class adapter) {
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }
}
