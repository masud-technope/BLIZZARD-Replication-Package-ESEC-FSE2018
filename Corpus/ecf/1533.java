package org.eclipse.ecf.twitter.client;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.start.IECFStart;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.provider.twitter.container.TwitterInstantiator;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.internal.progress.ProgressMonitorJobsDialog;

@SuppressWarnings("restriction")
public class Application implements IApplication, IECFStart, IStartApp {

    private IStatus status;

    IApplicationContext context;

    private static final String containerName = "ecf.twitter.client";

    private IContainer container;

    @Override
    public Object start(IApplicationContext context) throws Exception {
        this.context = context;
        IProgressMonitor pgm = (IProgressMonitor) new ProgressMonitorJobsDialog(Display.getDefault().getActiveShell());
        run(pgm);
        return MultiStatus.OK_STATUS;
    }

    @Override
    public void stop() {
    }

    public IContainer createClient() throws ContainerCreateException {
        return ContainerFactory.getDefault().createContainer(new ContainerTypeDescription(containerName, TwitterInstantiator.class.getName(), "Trivial client/container for TwitterHub"));
    }

    protected static String getContainername() {
        return containerName;
    }

    @Override
    public IStatus run(IProgressMonitor monitor) {
        try {
            setContainer(this.createClient());
        } catch (ContainerCreateException e) {
            Trace.trace(Activator.PLUGIN_ID, "ContainerCreateException");
        }
        return MultiStatus.OK_STATUS;
    }

    public void setContainer(IContainer container) {
        this.container = container;
    }

    public IContainer getContainer() {
        return container;
    }
}
