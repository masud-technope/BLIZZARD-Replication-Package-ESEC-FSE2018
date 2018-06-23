package org.eclipse.ecf.tutorial.lab1.actions;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.examples.remoteservices.common.IRemoteEnvironmentInfo;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.tutorial.internal.lab1.Activator;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class Lab1Action implements IWorkbenchWindowActionDelegate {

    private IWorkbenchWindow window;

    private static final String CONTAINER_TYPE = System.getProperty("ecf.type", "ecf.r_osgi.peer");

    private static final String TARGET = System.getProperty("ecf.target", "r-osgi://localhost:9278");

    private IContainer container;

    private IRemoteServiceContainerAdapter adapter;

    /**
	 * The constructor.
	 */
    public  Lab1Action() {
    }

    protected ID createTargetID(IContainer container, String target) {
        return IDFactory.getDefault().createID(container.getConnectNamespace(), target);
    }

    private IContainer getContainer() throws ContainerCreateException {
        if (container == null) {
            container = Activator.getDefault().getContainerFactory().createContainer(CONTAINER_TYPE);
        }
        return container;
    }

    private IRemoteServiceContainerAdapter getContainerAdapter() throws ContainerCreateException {
        if (adapter == null) {
            IContainer c = getContainer();
            adapter = (IRemoteServiceContainerAdapter) c.getAdapter(IRemoteServiceContainerAdapter.class);
        }
        return adapter;
    }

    /**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
    public void run(IAction action) {
        try {
            IRemoteServiceContainerAdapter adapter = getContainerAdapter();
            // Create target ID
            String target = TARGET;
            ID targetID = createTargetID(container, target);
            // Get and resolve remote service reference
            IRemoteServiceReference[] ref = adapter.getRemoteServiceReferences(targetID, org.eclipse.ecf.examples.remoteservices.common.IRemoteEnvironmentInfo.class.getName(), null);
            IRemoteService svc = adapter.getRemoteService(ref[0]);
            // get proxy
            IRemoteEnvironmentInfo proxy = (IRemoteEnvironmentInfo) svc.getProxy();
            // Call it!
            String osArch = proxy.getOSArch();
            // Show result
            MessageDialog.openInformation(window.getShell(), "ECF Lab 1", "Target " + target + " has OS Arch=" + osArch);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
    public void selectionChanged(IAction action, ISelection selection) {
    }

    /**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
    public void dispose() {
    }

    /**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
    public void init(IWorkbenchWindow window) {
        this.window = window;
    }
}
