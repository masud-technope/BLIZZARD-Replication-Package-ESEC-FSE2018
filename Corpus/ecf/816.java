package org.eclipse.ecf.ui.actions;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.util.IExceptionHandler;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Action class to synchronously invoke {@link IContainer#connect(ID, IConnectContext)}.
 */
public class SynchContainerConnectAction implements IWorkbenchWindowActionDelegate {

    protected IWorkbenchWindow window;

    protected ID targetID;

    protected IConnectContext connectContext;

    protected IContainer container;

    protected IExceptionHandler exceptionHandler;

    protected Runnable successBlock;

    public  SynchContainerConnectAction(IContainer container, ID targetID, IConnectContext connectContext, IExceptionHandler exceptionHandler, Runnable successBlock) {
        this.container = container;
        this.targetID = targetID;
        this.connectContext = connectContext;
        this.exceptionHandler = exceptionHandler;
        this.successBlock = successBlock;
    }

    public  SynchContainerConnectAction(IContainer container, ID targetID, IConnectContext connectContext, IExceptionHandler exceptionHandler) {
        this(container, targetID, connectContext, exceptionHandler, null);
    }

    public  SynchContainerConnectAction(IContainer container, ID targetID, IConnectContext connectContext) {
        this(container, targetID, connectContext, null);
    }

    public void dispose() {
        this.container = null;
        this.targetID = null;
        this.connectContext = null;
        this.window = null;
    }

    protected void handleConnectException(IAction action, ContainerConnectException e) {
        if (exceptionHandler != null)
            exceptionHandler.handleException(e);
    }

    public void init(IWorkbenchWindow w) {
        this.window = w;
    }

    public void run(IAction action) {
        try {
            container.connect(this.targetID, this.connectContext);
            if (successBlock != null) {
                successBlock.run();
            }
        } catch (ContainerConnectException e) {
            handleConnectException(action, e);
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
    //
    }

    protected IWorkbenchWindow getWindow() {
        return window;
    }

    protected ID getTargetID() {
        return targetID;
    }

    protected IConnectContext getConnectContext() {
        return connectContext;
    }

    protected IContainer getContainer() {
        return container;
    }

    protected IExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    protected Runnable getSuccessBlock() {
        return successBlock;
    }
}
