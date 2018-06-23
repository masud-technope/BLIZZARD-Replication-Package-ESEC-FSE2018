package org.eclipse.ecf.sync;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.util.ECFException;

public class ModelUpdateException extends ECFException {

    private static final long serialVersionUID = 6697002759105612786L;

    private IModelChange modelChange;

    private Object model;

    public  ModelUpdateException(String message, IModelChange mc, Object model) {
        super(message);
        this.modelChange = mc;
        this.model = model;
    }

    public  ModelUpdateException(Throwable cause, IModelChange mc, Object model) {
        super(cause);
        this.modelChange = mc;
        this.model = model;
    }

    public  ModelUpdateException(String message, Throwable cause, IModelChange mc, Object model) {
        super(message, cause);
        this.modelChange = mc;
        this.model = model;
    }

    public  ModelUpdateException(IStatus status, IModelChange mc, Object model) {
        super(status);
        this.modelChange = mc;
        this.model = model;
    }

    public IModelChange getModelChange() {
        return this.modelChange;
    }

    public Object getModel() {
        return this.model;
    }
}
